# 第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansContainerLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary](014-03-dependency-injection-resolution.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](016-05-lifecycle-and-callbacks.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseSolutionTest.java`

## 机制主线

这一章的主题是：**scope 不是“对象的特性”，而是“容器如何管理对象的策略”。**

尤其要吃透一句话：

> prototype 的语义是“每次向容器要都会新建”，不是“每次方法调用都会新建”。

## 1. singleton vs prototype：到底“一”指什么？

- `singleton`：**同一个容器**里，这个 beanName 对应的实例只有一个
- `prototype`：容器**每次创建/获取**都会创建一个新实例；容器通常不缓存它（也不负责销毁回调）

### 1.1 机制讲透：条件 → 分支 → 结果（可断点验证）

**条件**：当前 bean 的 scope 是 singleton 还是 prototype  
**分支**：`AbstractBeanFactory#doGetBean`  
- singleton：优先走缓存（`singletonObjects` / `earlySingletonObjects`）  
- prototype：直接走 `createBean`，并设置 `isPrototypeCurrentlyInCreation`  
**结果**：  
- singleton 可以被循环依赖“部分救回”（有 early reference）  
- prototype 循环依赖直接 fail-fast（不会进单例缓存）  
**断点建议**：`AbstractBeanFactory#doGetBean`（观察 `mbd.isPrototype()` 与 `isPrototypeCurrentlyInCreation`）

## 2. 本模块里你能直接观察到的现象

代码对应：

- prototype bean：`PrototypeIdGenerator`（`@Scope("prototype")`）
- 直接注入：`DirectPrototypeConsumer`
- Provider 延迟获取：`ProviderPrototypeConsumer`

- `DirectPrototypeConsumer.currentId()` 连续两次拿到同一个 UUID
- `ProviderPrototypeConsumer.newId()` 连续两次拿到不同 UUID

## 2.1 prototype 的关键边界（创建 guard / 循环依赖 / 缓存差异）

- **创建 guard**：prototype 不走 `singletonObjects`，每次 `getBean` 都走 `createBean`  
- **循环依赖不可救**：prototype 没有 early reference 缓冲区，触发 `BeanCurrentlyInCreationException`  
- **缓存差异本质**：singleton 是“容器托管 + 缓存复用”，prototype 是“一次性交付”

## 3. 为什么“prototype 注入 singleton”会看起来像单例？

容器创建 singleton 的时候，会把它的依赖也解析出来并注入进去。

如果你把 prototype 当作一个普通依赖注入到 singleton 里，发生的是：

1) 创建 singleton A
2) 解析到它需要 prototype P
3) **创建一个 P 并注入到 A**
4) A 从此持有这个 P 的引用（A 自己是单例）

之后你再调用 A 的方法，当然一直是同一个 P 引用 —— 这不是 prototype “失效”，而是你**只向容器要过一次 P**。

## 4. 解决方案 1：`ObjectProvider`（推荐，简单有效）

`ObjectProvider<T>` 让你把“获取对象的动作”推迟到方法调用时：

- 注入的是 provider（可以理解为“容器句柄”）
- 每次 `getObject()` 才真正向容器要一个实例

本模块的 `ProviderPrototypeConsumer` 就是这样做的。

适用场景：

- prototype 注入 singleton
- 可选依赖（没注册也可以）
- 想延迟创建（避免启动期就创建）

## 5. 解决方案 2：`@Lookup`（方法注入，适合“每次调用都要新的”）

`@Lookup` 的效果可以理解为：

- Spring 生成一个子类/代理
- 在方法调用时，由容器动态返回一个 bean

适用场景：

- 你希望“每次方法调用都获取一个新的 prototype”，但不想在业务代码里显式依赖 `ObjectProvider`
- 你希望调用点保持简单（`consumer.next()`），由容器在运行时完成“按需取 bean”

常见边界（必知）：

- 依赖运行时子类化：final 类/方法无法被覆盖（因此无法被 `@Lookup` 替换）
- 调试成本更高：调用栈会进入容器与增强逻辑，建议配合本章的断点建议与对照用例

## 6. 解决方案 3：scoped proxy（谨慎使用）

你可以把某个 scope 的 bean 包装成代理，然后把代理注入到 singleton：

- singleton 持有的是“代理”
- 代理在每次方法调用时去当前 scope 找真实对象

学习阶段建议把它当作“了解存在即可”的方案。

### 6.1 三种方案对照（ObjectProvider / @Lookup / scoped proxy）

| 方案 | 优点 | 代价 | 关键类/入口 |
| --- | --- | --- | --- |
| `ObjectProvider` | 简单直观、断点容易下 | 需要显式调用 `getObject()` | `DefaultListableBeanFactory#getBeanProvider` |
| `@Lookup` | 业务代码调用点更自然 | 依赖子类增强、对 final 限制敏感 | `LookupOverride` / `CglibSubclassingInstantiationStrategy` |
| scoped proxy | 调用点透明 | 调试复杂、代理层级增加 | `ScopedProxyFactoryBean` / `ScopedObject` |

## 7. prototype 的销毁语义（容器默认不托管）

这一点在真实工程里非常关键，因为它决定了“资源释放责任在谁”：

- prototype 更像是：**容器帮你 new，一次性交付**
- 而不是：**容器全程托管（创建 + 使用 + 销毁）**

因此默认行为是：

- 你向容器要一个 prototype → 容器负责创建（注入/初始化也照常发生）
- 但当容器关闭时 → **不会自动触发 prototype 的 destroy callbacks**

这也是为什么很多人会困惑：

- “我写了 `@PreDestroy` / `DisposableBean#destroy`，为什么 prototype 看起来不执行？”
  - 因为容器没有保存这些 prototype 实例的引用，无法在 close 时逐个回收

你应该观察到：

- `context.close()` 不会触发 prototype 的 `@PreDestroy`
- 只有当你显式调用 `BeanFactory#destroyBean(...)`，才会触发 destroy callbacks（资源释放需要调用方负责）

### 7.1 自定义 scope 的回收要点

- 自定义 scope 要显式注册销毁回调：`Scope#registerDestructionCallback`  
- 若 scope 生命周期结束（如请求/会话），必须主动触发回收，否则容易发生上下文泄漏  
- 需要显式销毁时可用 `ConfigurableBeanFactory#destroyBean` 或 `destroyScopedBean`

### 7.2 排障提示：什么时候应该怀疑是 prototype 销毁语义问题？

- 症状：连接/文件句柄/线程池等资源泄漏，但你确认 `@PreDestroy` 逻辑存在
- 排查：这个 bean 是否是 prototype？它的创建者（调用方）是否负责 close/destroy？

## 可复现闭环（基于 `SpringCoreBeansContainerLabTest`）

你至少要能用 3 个断言讲清楚本章主线：

1) **prototype 注入 singleton 会“冻结为同一个实例”**  
   - 断点：`doResolveDependency` → `doGetBean("prototypeBean")`  
   - 断言：`DirectPrototypeConsumer.currentId()` 两次相同
2) **`ObjectProvider` 能做到“每次调用新实例”**  
   - 断点：`ObjectProvider#getObject`  
   - 断言：`ProviderPrototypeConsumer.newId()` 两次不同
3) **prototype destroy 不会自动触发**  
   - 断点：`DefaultSingletonBeanRegistry#destroySingletons`  
   - 断言：`@PreDestroy` 不执行，除非显式 `destroyBean`

## 8. 源码调用链（方法级）：prototype 为什么会“像单例”？

把问题压缩成一句话：

> **prototype 不是“每次方法调用都新建”，而是“每次向容器要（resolve/getBean）都新建”。**

### 8.1 直接注入 prototype 到 singleton：为什么会冻结成同一个实例？

1) 创建 singleton A：`AbstractAutowireCapableBeanFactory#doCreateBean("a")`
2) 依赖解析：`DefaultListableBeanFactory#doResolveDependency`
3) 解析到 prototype P：`AbstractBeanFactory#doGetBean("p")` → `createBean("p")`
4) **把这个 P 注入到 A 的字段/构造器参数里**（发生在 `populateBean`/构造器解析阶段）
5) A 从此持有 P 的引用（A 是单例 ⇒ 引用不会变）

### 8.2 `ObjectProvider`：为什么能做到“每次调用拿一个新的 prototype”？

关键差异：注入的不再是 P，而是 provider（容器句柄）。

1) 注入阶段只注入 provider：`ObjectProvider<T>`
2) 每次业务方法调用时：`provider.getObject()`
3) 才触发：`AbstractBeanFactory#doGetBean("p")`（于是每次都是新实例）

### 8.3 `@Lookup`：方法注入为什么也能“每次调用都新”？

- 它依赖运行时子类化（方法被覆盖），在方法调用点再去容器取 bean
- 证据链入口（方法级）：`LookupOverride` / `CglibSubclassingInstantiationStrategy`（了解存在即可；学习阶段优先掌握 provider）

## 9. 排障决策表（scope/prototype：从“像单例”到“证据链”）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| prototype 注入到 singleton 后“总是同一个” | 获取动作只发生在 singleton 创建时 | 断点 `doResolveDependency`；观察 prototype 的 `doGetBean("p")` 只发生一次 | 使用 `ObjectProvider` / `@Lookup` 延迟获取；或改 scope | `SpringCoreBeansLabTest` |
| `@Lookup` 不生效 | final 类/方法无法覆盖；或没被容器增强 | 断点 `CglibSubclassingInstantiationStrategy`（可选）；观察目标类是否被增强 | 避免 final；优先用 `ObjectProvider` | `SpringCoreBeansContainerLabTest.lookupMethodCanObtainFreshPrototypeEachCall` |
| prototype 的 `@PreDestroy` 不触发 | 容器默认不托管 prototype 的销毁 | `DefaultSingletonBeanRegistry#destroySingletons` 不会遍历 prototype；prototype 不进入 `disposableBeans` | 调用方显式销毁；或改为 singleton + 显式资源管理 | `SpringCoreBeansPrototypeDestroySemanticsLabTest` |
| scoped proxy 行为“像代理/类型不对” | 注入的是代理而不是目标对象 | 观察注入对象是否为 proxy；看 scopedTarget 命名 | 明确按接口注入；理解代理边界；优先用 provider | `SpringCoreBeansCustomScopeLabTest`（结合 [28](../part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md)） |

## 10. 练习与参考答案（Exercise ↔ Solution）

如果你想把“现象 → 原理 → 断点 → 代码改造”做成闭环，可以对照下面两份测试：

- Exercise（默认 `@Disabled`，自己动手改造）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
    - `exercise_makeDirectPrototypeConsumerUseFreshPrototypeEachCall()`
    - `exercise_changePrototypeScopeAndUpdateExpectations()`
- Solution（默认参与回归，可直接对照答案）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseSolutionTest.java`

## 11. 面试常问（prototype 注入陷阱）

### Q1：prototype 的语义是什么？为什么“prototype 注入 singleton”会像单例？

- 标准答案（可复述）：
  - prototype 的语义是“每次向容器要都会新建”；但注入发生在 singleton 创建时，只向容器要了一次 prototype，之后被 singleton 持有引用，因此表现得像单例。
- 证据链（方法级）：
  - `DefaultListableBeanFactory#doResolveDependency`
  - `AbstractBeanFactory#doGetBean`（prototype 只在依赖解析时触发一次）
- 最小复现：
  - `SpringCoreBeansLabTest.demonstratesPrototypeScopeBehavior`

### Q2：怎么让 singleton 每次调用都拿到新的 prototype？

- 标准答案（可复述）：
  - 把“获取动作”延迟到使用时：优先用 `ObjectProvider#getObject()`；也可用 `@Lookup`（方法注入）或 scoped proxy（谨慎，debug 成本更高）。

### Q3：prototype 的销毁回调为什么经常“不触发”？

- 标准答案（可复述）：
  - 容器默认不托管 prototype 的销毁；`context.close()` 只会销毁 singleton；prototype 的 `@PreDestroy` 需要显式 destroy 或由调用方管理生命周期。
- 证据链（方法级）：
  - `DefaultSingletonBeanRegistry#destroySingletons`
  - `ConfigurableBeanFactory#destroyBean`
- 最小复现：
  - `SpringCoreBeansPrototypeDestroySemanticsLabTest`

## 12. 一句话自检

读完这一章你应该能回答：

1) prototype 的第一性语义是什么？（每次 resolve/getBean 都新建）
2) 为什么“prototype 注入 singleton”会冻结？（获取动作只发生一次）
3) 你会用哪条证据链证明 provider/lookup 把获取动作推迟到了“使用时”？

## 小结与下一章

下一章我们把 scope 与生命周期合起来讲：什么时候创建、什么时候初始化、什么时候销毁（以及回调顺序）。如果你已经开始关心“销毁回调顺序/触发者”，可以直接跳到下一章 [05](016-05-lifecycle-and-callbacks.md)。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest`
- Exercise：`SpringCoreBeansExerciseTest`
- Solution：`SpringCoreBeansExerciseSolutionTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseSolutionTest.java`

上一章：[03. 依赖注入解析：类型/名称/@Qualifier/@Primary](014-03-dependency-injection-resolution.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](016-05-lifecycle-and-callbacks.md)

<!-- BOOKIFY:END -->
