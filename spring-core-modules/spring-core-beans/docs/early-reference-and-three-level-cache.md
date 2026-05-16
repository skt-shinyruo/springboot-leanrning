# Early reference 与三级缓存：循环依赖为什么有时能过、有时会炸

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 singleton 循环依赖为什么有时可以成功，有时会失败，以及 early reference 在对象流转里到底插在什么位置。
    - 核心结论：不是背三个缓存名，而是看对象如何从创建中途被提前暴露、如何被包装成早期引用、以及最终如何落回 singleton cache。
    - 还要看清楚 raw reference、early proxy 和最终 exposed object 之间的差异，尤其是“已包装但仍注入 raw”的边界。

    观察对象：singleton 创建中的提前暴露、早期代理和缓存回流。
    主线位置：singleton 正在创建、另一个 bean 需要它的时候。
    对照入口：`SpringCoreBeansCircularDependencyBoundaryLabTest`、`SpringCoreBeansEarlyReferenceLabTest`、`SpringCoreBeansRawInjectionDespiteWrappingLabTest`。
<!-- CHAPTER-CARD:END -->

三级缓存不是背名词的题目，而是一个对象流转模型。它解决的是：singleton 还没完全初始化时，另一个 bean 先依赖上来了，容器怎么办。

## 先看对象流

可以把它理解成三步流转：

1. bean 开始创建，raw instance 先产生
2. 在还没完成初始化时，容器可能把它提前暴露出去，形成 early reference
3. 初始化和后处理完成后，最终对象回到对外暴露的 singleton 结果里

这条流把“正在创建中”的对象和“最终可见”的对象分开了。

## 为什么有时循环依赖能成功

如果是典型的 setter / field 注入循环，容器有机会在创建 A 的中途先让 A 的早期引用可见，再去满足 B 对 A 的依赖。等 B 创建完返回后，A 继续走完后续初始化。

`SpringCoreBeansEarlyReferenceLabTest` 的早期代理实验正是这个思路：

- `getEarlyBeanReference()` 可以在循环依赖期间返回一个 early proxy
- B 注入到的是早期暴露出来的 A 引用
- 最终对外拿到的 A 仍然是同一个代理对象

这说明循环依赖成功的关键不是“有没有循环”，而是“能不能在中途把一个可用引用提前交出来”。

## 为什么有时会失败

当依赖关系需要一个当前还不存在、又无法提前暴露的对象时，循环就会失败。典型情况包括：

- 依赖需要构造阶段就完成，无法等到 setter 注入
- 循环发生在 prototype 对象之间，容器没有 singleton 三级缓存可以复用
- 注入点要求 concrete type，但最终暴露对象是 JDK proxy
- 后处理器在过晚阶段才包装对象，早期暴露和最终对象不一致

`SpringCoreBeansEarlyReferenceLabTest` 里 concrete type mismatch 的实验就是这个边界：JDK proxy 只实现接口，不可赋给具体类，循环依赖最终会走向 `UnsatisfiedDependencyException`。

这也解释了 prototype 循环为什么不能靠三级缓存“自动解决”。三级缓存服务的是 singleton 创建过程：容器可以把正在创建的 singleton 记录下来，并在另一个 singleton 需要它时提供 early reference。prototype 每次请求都要创建新实例，容器不会把一个正在创建的 prototype 放进 singleton 缓存体系里复用；prototype A 要 prototype B，B 又要 A，本质上仍然是无法完成的递归创建。

## raw injection despite wrapping

最容易踩坑的情况是：bean 最终会被包装，但循环注入时先拿到的却还是 raw reference。

`SpringCoreBeansRawInjectionDespiteWrappingLabTest` 说明了这条边界：如果显式允许 `allowRawInjectionDespiteWrapping=true`，即使 bean 后面会被代理，其他 bean 在早期注入点里仍可能先拿到原始对象。结果就是“最终暴露的是代理，但某条依赖边里存的是 raw instance”。

这不是默认推荐路径。默认更安全的行为是 `allowRawInjectionDespiteWrapping=false`：Spring 会倾向于 fail-fast 或通过 early proxy 保持“早期引用 == 最终暴露对象”的一致性，避免系统里同时流动 raw target 和 proxy。只有打开允许 raw 注入的开关时，容器才会为了让循环依赖继续成功而接受这种不一致。

这会让调用路径出现不一致：

- 外部通过 `getBean()` 拿到的是 proxy
- 某个依赖字段里却已经缓存了 raw target
- self-invocation、事务、切面等能力可能因此表现不一致

## 三个缓存不是记忆题，而是职责分层

与其背名字，不如记职责：

- 一个缓存负责保存已经完全创建好的 singleton
- 一个缓存负责保存早期引用，给循环依赖一个临时出口
- 一个缓存负责记录对象工厂，延后真正生成引用的时机

这三层一起工作，才让“创建中对象被提前引用”这件事可控。重点不是某个名字，而是对象从 raw 到 early reference 再到最终 singleton 的流动方向。

## 看到循环依赖时，先分三种结果

- 成功了，但拿到的是 early proxy
- 显式允许 raw injection 后成功了，但某个依赖里注入的是 raw reference
- 失败了，因为对象不能提前暴露或类型不匹配

把结果分类清楚，再去看缓存和后处理器，排障会快很多。
