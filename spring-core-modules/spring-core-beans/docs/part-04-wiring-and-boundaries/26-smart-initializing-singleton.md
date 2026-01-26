# 26. SmartInitializingSingleton：所有单例都创建完之后再做事

## 导读

- 本章主题：**26. SmartInitializingSingleton：所有单例都创建完之后再做事**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansSmartInitializingSingletonLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`

## 机制主线

有时候你需要一个“容器已经把主要单例都创建完”的时机点，比如：

- 想扫描容器里所有某类 bean，并建立索引
- 想做一次性校验（例如检查某些 bean 组合是否合法）

Spring 提供了一个非常明确的回调：

- `SmartInitializingSingleton#afterSingletonsInstantiated`

## 1. 现象：回调发生在“非 lazy 单例创建完成之后”

对应测试：

- `eager` 是普通单例（refresh 时创建）
- `lazy` 是 lazy-init（refresh 时不创建）
- `callback` 实现 `SmartInitializingSingleton`

观察点：

- `afterSingletonsInstantiated` 触发时，`lazy` 还不在 singleton cache（还没创建）
- 之后你第一次 `getBean(lazy)` 才会创建它

## 2. 机制：它是 preInstantiateSingletons 的“收尾回调”

把它理解成：

- 容器在创建完所有非 lazy 单例后，给你一个“做一次性事情”的机会

它比你自己写 `ApplicationRunner` 更贴近容器内部生命周期。

- `AbstractApplicationContext#finishBeanFactoryInitialization`：refresh 中“创建单例”阶段的入口（会调用 preInstantiateSingletons）
- `DefaultListableBeanFactory#preInstantiateSingletons`：批量创建非 lazy 单例，并在末尾触发 SmartInitializingSingleton 回调
- `SmartInitializingSingleton#afterSingletonsInstantiated`：你能拿到的“单例都创建完了”的明确时机点
- `DefaultSingletonBeanRegistry#getSingleton`：观察某个 bean 是否已经进入 singleton cache（解释 lazy bean 尚未创建）
- `AbstractBeanFactory#doGetBean`：后续第一次 `getBean(lazy)` 才会触发真正创建

入口：

- 入口测试（方法级）：`SpringCoreBeansSmartInitializingSingletonLabTest#afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans`
- 推荐跑法：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartInitializingSingletonLabTest#afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans test`

## 排障分流：这是定义层问题还是实例层问题？

- “回调没触发” → **实例层（生命周期时机）**：该 bean 是否是 singleton？context 是否真的 refresh？
- “回调里拿不到 lazy bean 实例” → **实例层语义**：这是预期；lazy-init 在 refresh 阶段不会创建（对照 [18](023-18-lazy-semantics.md)）
- “回调里 `getBean` 导致启动变慢” → **实例层行为**：你把 lazy bean 全部提前创建了（本章第 3 节）
- “我以为它等价于 ApplicationRunner” → **生命周期粒度差异**：它更贴近 BeanFactory 的创建阶段（本章第 2 节 + `preInstantiateSingletons`）

## 4. 一句话自检

1) `SmartInitializingSingleton#afterSingletonsInstantiated` 触发于 refresh 的哪个阶段？为什么它早于 lazy bean 的创建？
2) 为什么它不等价于 `ApplicationRunner`？（提示：它挂在 BeanFactory 的 preInstantiateSingletons 尾部）
3) 在回调里调用 `getBean(lazy)` 会带来什么后果？如何判断你是否“提前把 lazy 全部创建了”？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansSmartInitializingSingletonLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartInitializingSingletonLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`

- `SpringCoreBeansSmartInitializingSingletonLabTest.afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans()`

实验里：

## 源码锚点（建议从这里下断点）

- `DefaultListableBeanFactory#preInstantiateSingletons`：单例预实例化入口（SmartInitializingSingleton 回调发生在这段之后）
- `SmartInitializingSingleton#afterSingletonsInstantiated`：容器“基本就绪”的回调点（所有非 lazy 单例创建完成后）
- `AbstractApplicationContext#finishBeanFactoryInitialization`：refresh 主线里触发 preInstantiateSingletons 的阶段
- `DefaultSingletonBeanRegistry#getSingleton`：回调里再取 bean 的语义与边界（是否会触发额外创建）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`
  - `afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans()`

建议断点：

1) `DefaultListableBeanFactory#preInstantiateSingletons`：观察非 lazy 单例创建结束后的“收尾回调”位置
2) `SmartInitializingSingleton#afterSingletonsInstantiated`（你在 Lab 里的实现）：观察回调触发时机与可见的单例集合
3) `DefaultSingletonBeanRegistry#getSingleton`：在回调里或断言点查看 lazy bean 是否已在缓存中
4) `AbstractBeanFactory#doGetBean`：在测试后半段第一次 `getBean(lazy)` 时观察真正创建发生在哪里

- 你能解释清楚：为什么 `afterSingletonsInstantiated` 触发时 lazy bean 可能还没创建吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`
推荐断点：`DefaultListableBeanFactory#preInstantiateSingletons`、`SmartInitializingSingleton#afterSingletonsInstantiated`、`AbstractAutowireCapableBeanFactory#doCreateBean`

## 常见坑与边界

### 常见坑

- **坑 1：误以为它能看到 lazy bean 实例**
  - 它看到的是“已创建的单例”。lazy bean 可能还没创建。

- **坑 2：在回调里触发大量 `getBean`**
  - 会把 lazy bean 全部提前创建，可能导致启动变慢。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansSmartInitializingSingletonLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`

上一章：[25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](25-programmatic-bpp-registration.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[27. SmartLifecycle：phase 与 start/stop 顺序](27-smart-lifecycle-phase.md)

<!-- BOOKIFY:END -->
