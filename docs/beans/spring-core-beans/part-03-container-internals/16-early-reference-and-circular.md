# 16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？

## 导读

- 本章主题：**16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRawInjectionDespiteWrappingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

## 机制主线

循环依赖是学习容器机制时绕不开的一块。

这一章聚焦一个非常关键但经常被忽略的入口：

- `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`

## 1. 现象：setter 循环依赖有时能成功

- 构造器循环依赖通常会失败（无法创建任何一方）
- setter 循环依赖有时能成功

补充一个容易被忽略的工程视角（与本章的 early reference 主题互补，不冲突）：

- **构造器环并不等于“彻底无解”**：你可以通过“延迟获取依赖”的方式打断环（例如 `@Lazy` 注入点代理、`ObjectProvider<T>`）。
- 但它解决的是“启动/实例化时机”的问题，不解决“架构设计上为什么会形成环”的问题。

对应对照实验入口：

- `SpringCoreBeansCircularDependencyBoundaryLabTest`（@Lazy/ObjectProvider 打断 constructor 环）
- 以及上一章的“基础现象”：`SpringCoreBeansContainerLabTest`（constructor fail-fast vs setter 可能成功）

成功的关键在于：容器允许在“对象还没完全初始化完成”时，先暴露一个 **early singleton reference**。

## 2. getEarlyBeanReference：为什么需要它？

如果你只关注“循环依赖能不能启动”，你会以为 early reference 只是“救活 setter 循环”的技巧。

但对读者 C 来说，真正关键的是这句话：

> **getEarlyBeanReference 解决的不是“引用能不能拿到”，而是“拿到的引用是否已经是最终形态（proxy/wrapper）”。**

为什么这很重要？

- 很多框架能力（AOP/事务/异步/缓存/安全）都会在 BPP 阶段把 bean 包装成 proxy（见 [31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）。
- 循环依赖场景下，A 在创建中需要注入 B，B 也在创建中需要注入 A。
- 为了“救活”，容器会把 A 的一个引用提前暴露给 B（early exposure）。

问题来了：如果你提前暴露的是 **原始对象**，但最终对外暴露的是 **proxy**，那么：

- B 持有的是原始对象引用（绕过 proxy）
- 其他地方拿到的是 proxy
- 同一个 bean 在系统里出现两种形态 → 行为不一致、排障困难

`getEarlyBeanReference` 就是为了解决这个“early 与 final 不一致”的根源问题：让框架能在“提前暴露”时就给出代理版引用，从而让依赖方拿到的就是最终形态。

### 2.1 你应该把它与哪几个点一起记？

- `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`：给出 early proxy 的扩展点
- `BeanPostProcessor#postProcessAfterInitialization`：给出 final proxy 的常见扩展点
- 三层缓存：`singletonFactories`/`earlySingletonObjects`/`singletonObjects`（见 [09. 循环依赖：现象、原因与规避（constructor vs setter）](../part-01-ioc-container/09-circular-dependencies.md)）

### 面试常问：单例三级缓存 / early reference / 代理形态一致性

如果没有 getEarlyBeanReference，setter 循环依赖通常会用“原始对象引用”去填充依赖。

但真实系统里经常存在“包装/代理”需求（典型就是 AOP）：

- 你希望注入到别人那里的是“代理对象”
- 而不是“半成品的原始对象”

`getEarlyBeanReference` 的设计目的就是：

- 在循环依赖需要 early reference 的时候
- 也能返回“该 bean 最终应该暴露的形态”（例如 proxy）

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
  - `getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution()`（核心闭环：early 与 final 都是同一个 proxy）
  - `injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency()`（边界：按实现类注入可能因 JDK proxy 直接失败）

- `Alpha` 与 `Beta` 用 setter 互相依赖
- 我们实现一个 `SmartInstantiationAwareBeanPostProcessor`
  - 在 `getEarlyBeanReference` 阶段为 `alpha` 创建 JDK proxy
  - 并在 `postProcessAfterInitialization` 阶段返回同一个 proxy，保证“最终形态一致”

你应该观察到：

- 循环依赖能够完成
- `alpha` 在容器里拿到的是 proxy
- `beta` 注入到的 `alpha` 也是同一个 proxy

如果你：

- early 阶段返回了原始对象
- final 阶段又返回了代理对象

容器可能会报类似错误（含义是）：

- 有别的 bean 已经拿到“原始对象”
- 但最终这个 bean 又被包装成了“代理对象”
- 系统出现“同一个 bean 两种形态并存”，容器默认会阻止这种不一致

学习阶段建议：

入口：

最小复现入口（方法级）：

- `SpringCoreBeansEarlyReferenceLabTest.getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution()`
- `SpringCoreBeansEarlyReferenceLabTest.injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency()`

推荐断点（闭环版）：

1) `DefaultSingletonBeanRegistry#getSingleton`：三层缓存/early 分支的主入口（看 `earlySingletonReference` 从哪来）
2) `AbstractAutowireCapableBeanFactory#doCreateBean`：创建主线（对照 early reference 与最终 `exposedObject`）
3) `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`：early reference 的统一入口（会调用 `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`）
4) 你在 Lab 里实现的 `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`：观察 early 阶段返回的到底是 raw 还是 proxy
5) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：final 阶段代理通常在这里产生（对照 early 与 final 是否一致）

你应该看到：

- `beta` 注入到的 `alpha` 与容器最终暴露的 `alpha` 是同一个 proxy（same reference）

## 排障分流：这是定义层问题还是实例层问题？

- “构造器循环依赖失败” → **实例层（创建时机）**：构造器依赖发生在实例化之前，容器没机会提前暴露引用（回看 [09](../part-01-ioc-container/09-circular-dependencies.md)）
- “setter 循环依赖也失败/报 raw vs wrapped 不一致” → **实例层（early vs final 形态不一致）**：检查 early 与 afterInit 是否返回同一个 proxy（本章第 4 节）
- “循环依赖解决了但拿到的类型变了（proxy）” → **实例层（代理语义）**：这是为了保证“最终暴露形态一致”，与 AOP/事务心智模型一致（见 [31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）
- “以为这只和循环依赖有关” → **实例层通用机制**：early reference 是为了解决“创建中暴露引用”，但核心仍是 BPP 能改变 bean 形态（见 [00](../part-00-guide/011-00-deep-dive-guide.md)）

## 源码最短路径（call chain）

> 目标：在循环依赖场景下，把“三级缓存（singleton/early/factory）”与“raw vs wrapped 检查点”放回同一条最短调用链。

一条最常见、最有价值的最短栈（以 setter 循环依赖为例）：

你只要把这条链路走通一次，就能把“三级缓存到底解决什么”与“为什么会有 raw vs wrapped 不一致”串成一条线。

## 固定观察点（watch list）

> 目标：在 `getSingleton` 与 `doCreateBean` 里，只看这些结构/变量，就能判断当前处于哪一层缓存、以及是否发生 raw vs wrapped 不一致。

建议在 `DefaultSingletonBeanRegistry#getSingleton(...)` 里 watch/evaluate：

- `singletonObjects`：一级缓存（完全初始化后的单例）
- `earlySingletonObjects`：二级缓存（提前暴露的 early singleton reference）
- `singletonFactories`：三级缓存（`ObjectFactory`，用于按需创建 early reference）
- `isSingletonCurrentlyInCreation(beanName)`：是否处于创建中（决定 early 分支是否可能发生）

- `earlySingletonReference`：early 引用（如果发生了循环依赖/提前暴露，这里通常不为 null）
- `exposedObject`：最终暴露对象（可能已经被 after-init BPP 替换成 proxy）
- `hasDependentBean(beanName)` + `getDependentBeans(beanName)`：是否已有其他 bean 拿到过 early/raw 引用（决定是否触发 fail-fast）

> 小技巧：当你看到异常提示里出现 “raw version / wrapped” 关键词时，几乎可以直接定位到这个尾部检查点。

## 反例（counterexample）

**反例：我按“实现类”注入，但最终容器暴露的是 JDK proxy（wrapped），类型直接对不上。**

- 你的注入点是 `ConcreteAlphaImpl`（实现类）
- 但容器最终暴露的 bean 可能是 JDK proxy（例如 `$Proxy...`，只实现接口）
- 因此注入阶段会直接失败：`BeanNotOfRequiredTypeException`（通常被 `UnsatisfiedDependencyException` 包一层）

- 优先按接口注入（让 proxy 仍然满足类型约束），并把“调用链必须走代理”的心智模型打牢（见 [31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）
- 如果你确实必须按实现类注入：你需要 class-based proxy（或避免用会改变暴露类型的代理策略）
- 如果你在循环依赖场景还希望 early 与 final 形态一致：实现 `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference(...)` 并保证 after-init 返回同一个 proxy  
  ⇒ 参见 `getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution()`

## 5. 补充：raw injection despite wrapping 与 allowRawInjectionDespiteWrapping

如果你在真实项目里看到过类似错误信息（关键词往往很明显）：

- `BeanCurrentlyInCreationException`
- “raw version / wrapped” / “has been injected into other beans” 之类的提示

它想保护的核心就一句话：

> **同一个 bean 在系统里不应该同时存在 raw 与 wrapped/proxy 两份引用。**

循环依赖场景下，容器为了“救活 setter 循环”，会提前暴露 early reference。  
但如果你的增强/代理发生在 `postProcessAfterInitialization`（final 阶段），那么：

- 依赖方（dependent bean）可能早早拿到了 raw
- 容器最终对外暴露的是 proxy
- 系统出现 “raw vs proxy” 两种形态并存，行为可能不一致（最典型：调用路径绕过代理，导致事务/安全/缓存等失效）

Spring 为了避免这种不一致，提供了一个 fail-fast 的保护开关：

- `DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping(boolean)`
  - `false`（默认，更安全）：检测到 “raw 注入 + 最终被 wrapping” 时直接失败
  - `true`：允许启动，但你必须清楚这会留下“依赖绕过代理”的隐患

你应该观察到两个稳定结论：

### 5.2 你应该把它和哪几个点一起记（避免混淆）

一个常用的心智模型：

- **能做到 early 与 final 一致**（例如 early 就返回 proxy）→ 最优
- **做不到一致**（early raw + final proxy）→ 默认 fail-fast；除非你明确接受风险才打开开关

## 6. 一句话自检

- 常问：`getEarlyBeanReference` 解决的到底是什么问题？
  - 答题要点：不是“能不能拿到引用”，而是“early 暴露的引用是否等于最终暴露形态（proxy/wrapper）”，避免 raw vs wrapped 不一致与绕过代理。
- 常见追问：三级缓存分别扮演什么角色？为什么 `singletonFactories` 不是直接缓存对象？
  - 答题要点：`singletonFactories` 缓存的是 `ObjectFactory`，用于按需生成 early reference（可能是 proxy），避免过早决定形态。
- 常见追问：为什么“按实现类注入”在 JDK proxy 场景会失败？
  - 答题要点：JDK proxy 只实现接口，不可赋值给实现类；因此注入点应优先按接口，或使用 class-based proxy 策略。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`

补充实验（raw injection despite wrapping 的风险与开关语义）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRawInjectionDespiteWrappingLabTest.java`

在 `SpringCoreBeansContainerLabTest` 里你已经见过：

- 题目：Spring 解决 setter 循环依赖的关键机制是什么？为什么构造器循环依赖基本无解？
- 追问：
  - 三级缓存分别是什么（概念上）？为什么 `singletonFactories` 是“工厂”而不是直接缓存对象？
  - 引入代理（AOP/事务）后，为什么必须把 early reference 和 `getEarlyBeanReference` 一起讲？不然会出现什么“对象形态不一致”的问题？
- 复现入口（可断言 + 可断点）：
  - setter vs constructor 循环依赖（基础现象）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
      - `circularDependencyWithConstructorsFailsFast()`
      - `circularDependencyWithSettersMaySucceedViaEarlySingletonExposure()`
  - early proxy（解决 raw vs wrapped 不一致）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
      - `getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution()`
  - proxy 类型陷阱（按实现类注入/获取失败）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
      - `injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency()`

## 3. 本模块的实验：让 early reference 直接变成 proxy

- `SpringCoreBeansEarlyReferenceLabTest.getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution()`

实验做的事：

- 要么 early 与 final 都不包装
- 要么像本实验一样：**early 与 final 返回同一个 wrapper/proxy**

## 源码锚点（建议从这里下断点）

- `DefaultSingletonBeanRegistry#getSingleton`：三级缓存取值分支（singletonObjects / earlySingletonObjects / singletonFactories）
- `DefaultSingletonBeanRegistry#addSingletonFactory`：把 early reference 工厂放进三级缓存的时机
- `AbstractAutowireCapableBeanFactory#doCreateBean`：bean 创建主流程（在哪一步触发 early exposure）
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`：容器向 BPP 请求 early reference 的入口
- `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`：早期代理的扩展点（AOP/自定义 wrapper 常用）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
  - `getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution()`

建议断点：

1) 你在 Lab 里实现的 `getEarlyBeanReference(...)`：观察 early 阶段返回的对象形态（proxy）
2) `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`：观察容器在什么时候向 BPP 请求 early reference
3) `DefaultSingletonBeanRegistry#getSingleton`：观察循环依赖时“从三级缓存取 early reference”的分支
4) `BeanPostProcessor#postProcessAfterInitialization`（你在 Lab 里的实现）：确认 final 阶段返回的对象与 early 阶段一致

最小复现入口（必现，且错误信息直给“expected type vs actual proxy type”）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
  - `injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency()`

你在断点里应该看到什么（用于纠错）：

正确做法（本章实验的成功路径）：

### 5.1 最小复现入口（可断言 + 可断点）

- 入口测试：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRawInjectionDespiteWrappingLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRawInjectionDespiteWrappingLabTest test`

1) `allowRawInjectionDespiteWrapping=false`：**创建该 bean 时 fail-fast**，拒绝让系统进入 raw/proxy 不一致状态（如果是 eager singleton 场景，表现为 refresh 失败）
2) `allowRawInjectionDespiteWrapping=true`：容器允许继续运行，但 dependent bean 持有 raw，容器对外暴露 proxy，调用链会出现“绕过代理”

- `getEarlyBeanReference`：解决“循环依赖时拿到的引用是否已是最终形态（proxy）”
- `allowRawInjectionDespiteWrapping`：解决“如果 early 阶段拿到 raw、final 阶段又变 proxy，是否允许系统带着不一致继续运行”

### 5.3 推荐断点（把异常放回 refresh 主线）

- 你能解释清楚：为什么循环依赖场景下，容器需要一个“提前暴露的引用”？
- 你能解释清楚：`getEarlyBeanReference` 为什么必须跟“代理/包装”一起讲？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
推荐断点：`DefaultSingletonBeanRegistry#getSingleton`、`AbstractAutowireCapableBeanFactory#getEarlyBeanReference`、`SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`

## 常见坑与边界

## 4. 最容易踩的坑：early 和 final 不一致

## 小结与下一章

- `DefaultSingletonBeanRegistry#getSingleton`：单例获取入口（循环依赖时会出现 early reference 分支）
- `DefaultSingletonBeanRegistry#addSingletonFactory`：提前暴露“singletonFactory”的地方（为 early reference 做准备）
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`：容器向 BPP 请求 early reference 的桥接点
- `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`：扩展点入口（让 early reference 也能是 proxy/wrapper）
- `AbstractAutowireCapableBeanFactory#doCreateBean`：创建主流程（在合适时机触发提前暴露与属性填充）

- `AbstractAutowireCapableBeanFactory#doCreateBean("alpha", ...)`
  - `DefaultSingletonBeanRegistry#addSingletonFactory("alpha", ...)`  
    - **这里把 `singletonFactory` 放进三级缓存（为 early reference 做准备）**
  - `populateBean(...)`（开始属性填充，触发对 `beta` 的依赖解析与创建）
    - `doResolveDependency(...)` / `getBean("beta")`
      - `doCreateBean("beta", ...)`
        - `populateBean(...)`（beta 需要注入 alpha）
          - `DefaultSingletonBeanRegistry#getSingleton("alpha", ...)`  
            - **这里命中 early reference 分支（从三级缓存拿 early 引用）**
            - `AbstractAutowireCapableBeanFactory#getEarlyBeanReference(...)`（如果有 `SmartInstantiationAwareBeanPostProcessor`，会走到这里）
- `initializeBean(...)`（BPP after-init 可能会把 bean 替换成 proxy/wrapper）
- `doCreateBean(...)` 尾部的“raw vs wrapped”一致性检查  
  - **如果 early 引用是 raw，但最终暴露对象是 wrapped/proxy，容器可能会失败（fail fast）**

建议在 `AbstractAutowireCapableBeanFactory#doCreateBean(...)` 尾部 watch/evaluate：

- `DefaultSingletonBeanRegistry#getSingleton`：命中 early reference 分支的入口
- `AbstractAutowireCapableBeanFactory#doCreateBean`：尾部 “raw vs wrapped” 一致性检查点（异常往往就在这里抛）
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`：如果你实现了 early proxy，会走到这里

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRawInjectionDespiteWrappingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[15. 实例化前短路：还没 new 就拿到对象了？](15-pre-instantiation-short-circuit.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[17. 生命周期回调顺序：Aware/@PostConstruct/afterPropertiesSet/initMethod](17-lifecycle-callback-order.md)

<!-- BOOKIFY:END -->
