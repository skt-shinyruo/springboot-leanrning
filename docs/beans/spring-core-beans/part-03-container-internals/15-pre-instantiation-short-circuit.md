# 15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行

## 导读

- 本章主题：**15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansPreInstantiationLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`

## 机制主线

这一章讲一个“非常像魔法”的容器机制：

- `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`

它允许你在 bean 实例化之前返回一个对象，从而 **短路默认的创建路径**。

## 1. 现象：构造器抛异常会让 refresh 直接失败

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`
  - `withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`（证据：构造器被调用一次，context refresh 失败）

你会看到：

- `FailingService` 构造器被调用
- 构造器抛异常导致容器 refresh 失败

这说明：**默认情况下，单例会在 refresh 阶段被创建**（非 lazy）。

## 2. 现象：短路后，构造器不再执行

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`
  - `postProcessBeforeInstantiation_canShortCircuitDefaultInstantiationPath()`（证据：构造器调用次数为 0，拿到的是 proxy）

我们注册了一个 `InstantiationAwareBeanPostProcessor`：

- 当容器准备创建 `FailingService` 时
- `postProcessBeforeInstantiation` 直接返回一个 JDK proxy（实现了 `GreetingService`）
- 容器就把这个 proxy 当作最终 bean

因此：

- 构造器不会执行
- refresh 不会失败

## 3. 这个机制有什么现实意义？

理解它的价值在于：

- 你能理解“容器为什么能把某个 bean 变成代理/替身对象”
- 你能理解“实例层增强”的入口不仅仅是 AOP（很多能力都是类似机制）

入口：

最小复现入口（方法级）：

- `SpringCoreBeansPreInstantiationLabTest.withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`
- `SpringCoreBeansPreInstantiationLabTest.postProcessBeforeInstantiation_canShortCircuitDefaultInstantiationPath()`

推荐断点（闭环版）：

1) `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`：短路入口（是否走到这里决定“构造器会不会执行”）
2) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation`：观察哪个 `InstantiationAwareBeanPostProcessor` 返回了替身
3) 你在 Lab 里实现的 `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`：观察返回对象（surrogate/proxy）
4) `AbstractAutowireCapableBeanFactory#doCreateBean`：对照两条路径（短路成功时目标 bean 不会走完整创建主线）

## 排障分流：这是定义层问题还是实例层问题？

- “我写了 before-instantiation 的 BPP，但构造器还是执行了” → **实例层（时机/注册方式）**：BPP 是否在 refresh 前注册？是否真的被当作 BPP 注册进 BeanFactory？（对照 [25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)）
- “短路后出现 `BeanNotOfRequiredTypeException`” → **实例层（暴露类型）**：返回对象的类型是否与容器期望类型兼容？（JDK proxy 只实现接口）
- “短路后生命周期回调/注入行为变得反直觉” → **实例层（绕过默认流程）**：你返回对象意味着你可能绕过 `doCreateBean` 的部分阶段（可对照 [17](17-lifecycle-callback-order.md)、[30](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)）
- “我以为这是 AOP/事务专属机制” → **实例层通用机制**：代理/替身的出现不止发生在 AOP（见 [31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）

## 5. 一句话自检

- 常问：`postProcessBeforeInstantiation` 能做什么？为什么它看起来像“魔法”？
  - 答题要点：它允许在实例化前直接返回替身/proxy，从而短路默认实例化与后续创建流程。
- 常见追问：怎么证明某个 bean 命中了“实例化前短路”？
  - 答题要点：在 `resolveBeforeInstantiation` 加条件断点（beanName），观察 `applyBeanPostProcessorsBeforeInstantiation` 是否返回非 null。
- 常见追问：为什么说它是高危扩展点？
  - 答题要点：短路意味着你可能绕过注入/初始化回调的直觉，导致生命周期行为变得反直觉，排障成本上升。

## 面试常问（实例化前短路的风险）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansPreInstantiationLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPreInstantiationLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`

- `SpringCoreBeansPreInstantiationLabTest.withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`

- `SpringCoreBeansPreInstantiationLabTest.postProcessBeforeInstantiation_canShortCircuitDefaultInstantiationPath()`

## 源码锚点（建议从这里下断点）

- `AbstractAutowireCapableBeanFactory#createBean` / `doCreateBean`（创建主线）
- `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`（短路入口）
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation`（遍历 IABPP 的关键循环）
- `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`（用户/框架扩展点）
- （对照）`AbstractAutowireCapableBeanFactory#createBeanInstance`（默认实例化策略入口：构造器/工厂方法等）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`
  - `withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`
  - `postProcessBeforeInstantiation_canShortCircuitDefaultInstantiationPath()`

建议断点：

1) `FailingService` 构造器：对照两段测试，确认“默认路径一定会调用构造器”
2) 你在 Lab 里实现的 `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation(...)`：观察它返回的对象（proxy）
3) `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`：观察短路发生在默认实例化之前
4) `AbstractAutowireCapableBeanFactory#doCreateBean`：在短路成功的测试里，验证这里不会被命中（或不会为目标 bean 执行）

- 你能解释清楚：为什么短路后构造器不执行，但 bean 仍然可以被容器拿到并调用吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`
推荐断点：`AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation`、`AbstractAutowireCapableBeanFactory#createBeanInstance`

- 常问：`postProcessBeforeInstantiation` 能做什么？为什么它是高危扩展点？
  - 答题要点：可以在实例化前直接返回替身/proxy，短路后续创建流程；风险是打破注入/初始化回调的直觉，引入“看似没执行构造但对象可用”的误判。
- 常见追问：怎么证明某个 bean 命中了短路？断点怎么下？
  - 答题要点：以 `resolveBeforeInstantiation` 为入口，沿着 `applyBeanPostProcessorsBeforeInstantiation` 找到具体哪个 `InstantiationAwareBeanPostProcessor` 返回了替身。

## 常见坑与边界

## 4. 常见坑

- **坑 1：返回的对象类型不兼容**
  - 容器后续按类型注入、按类型 getBean 可能会失败（`BeanNotOfRequiredTypeException`）。

- **坑 2：短路会绕过一些正常生命周期**
  - 短路意味着你不走默认实例化流程，很多生命周期回调/依赖注入的时机都会改变。

- **坑 3：学习可以用，工程里要非常谨慎**
  - 它属于极强的扩展点：一旦用错，系统会变得难以推理。

## 小结与下一章

- `DefaultListableBeanFactory#preInstantiateSingletons`：非 lazy 单例通常在 refresh 期间从这里开始批量创建（本章现象的触发点）
- `AbstractAutowireCapableBeanFactory#createBean`：创建入口（会先尝试“实例化前短路”）
- `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`：调用 `postProcessBeforeInstantiation` 的关键钩子
- `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`：短路扩展点（在“还没走默认实例化”前直接返回对象）
- `AbstractAutowireCapableBeanFactory#doCreateBean`：默认创建主流程（短路成功时通常不会走到这里）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansPreInstantiationLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`

上一章：[14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](14-post-processor-ordering.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[16. early reference 与循环依赖：getEarlyBeanReference](16-early-reference-and-circular.md)

<!-- BOOKIFY:END -->
