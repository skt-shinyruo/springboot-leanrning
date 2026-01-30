# 26. SmartInitializingSingleton：所有单例都创建完之后再做事
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：26. SmartInitializingSingleton：所有单例都创建完之后再做事
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`SmartInitializingSingleton#afterSingletonsInstantiated` / `AbstractApplicationContext#finishBeanFactoryInitialization` / `DefaultListableBeanFactory#preInstantiateSingletons`
    - 推荐 Lab：`SpringCoreBeansSmartInitializingSingletonLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](25-programmatic-bpp-registration.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[27. SmartLifecycle：phase 与 start/stop 顺序](27-smart-lifecycle-phase.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**26. SmartInitializingSingleton：所有单例都创建完之后再做事**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansSmartInitializingSingletonLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：“触发窗口”证据链（preInstantiateSingletons 完成后的回调触发点）。
    - B（边界反例）：反例：lazy 单例不在其中；过早初始化导致副作用。
    - C（排障 SOP）：排障：为什么 hook 没触发/触发顺序不符期望？
    - D（断点观察）：断点：afterSingletonsInstantiated 的触发点与执行顺序观察。
    - E（面试复述）：面试追问：它与 ContextRefreshedEvent 的差异与选择策略。
<!-- AE-DEEPENING:END -->
## 机制主线

有时候需要一个“容器已经把主要单例都创建完”的时机点，比如：

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
- 之后读者第一次 `getBean(lazy)` 才会创建它

## 2. 机制：它是 preInstantiateSingletons 的“收尾回调”

把它理解成：

- 容器在创建完所有非 lazy 单例后，给读者一个“做一次性事情”的机会

它比读者自己写 `ApplicationRunner` 更贴近容器内部生命周期。

- `AbstractApplicationContext#finishBeanFactoryInitialization`：refresh 中“创建单例”阶段的入口（会调用 preInstantiateSingletons）
- `DefaultListableBeanFactory#preInstantiateSingletons`：批量创建非 lazy 单例，并在末尾触发 SmartInitializingSingleton 回调
- `SmartInitializingSingleton#afterSingletonsInstantiated`：应能够拿到的“单例都创建完了”的明确时机点
- `DefaultSingletonBeanRegistry#getSingleton`：观察某个 bean 是否已经进入 singleton cache（解释 lazy bean 尚未创建）
- `AbstractBeanFactory#doGetBean`：后续第一次 `getBean(lazy)` 才会触发真正创建

### 机制讲透：条件 → 分支 → 结果

**条件**：bean 是 **非 lazy 的 singleton**，并实现了 `SmartInitializingSingleton`  
**分支**：`preInstantiateSingletons` 先创建全部非 lazy 单例 → 再统一回调  
**结果**：回调发生在“已创建单例集合稳定”之后，但 **不会包含 lazy 单例**  
**断点建议**：`DefaultListableBeanFactory#preInstantiateSingletons`

## 回调来源分型：SmartInitializingSingleton 在生命周期里处于哪一层？

把“回调”分两层看：

1) **单个 bean 级别的初始化回调**  
   - `@PostConstruct` / `InitializingBean#afterPropertiesSet` / `init-method`  
   - 发生在 **bean 自己的创建流程** 中（`populateBean` → `initializeBean`）
2) **容器级别的“全量就绪回调”**  
   - `SmartInitializingSingleton#afterSingletonsInstantiated`  
   - 发生在 **所有非 lazy 单例创建完成之后**

因此它与 `ApplicationRunner`/`CommandLineRunner` 的关系是：

- **更早、更底层**（挂在 BeanFactory 的 preInstantiateSingletons 之后）
- 只保证 **非 lazy 单例已完成创建**，并不保证外部系统已 fully ready

## 回调与代理交织：回调发生在 proxy 还是 target 上？

回调被触发时，容器会通过 `getBean(beanName)` 获取最终单例对象：

- 如果 BPP 在初始化后把 bean **替换为 proxy**，这里拿到的通常就是 **proxy**  
- 如果没有替换，回调就在 **目标对象** 上执行  

排障建议：

- 断点 `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：确认是否发生了“对象替换”  
- 断点 `DefaultListableBeanFactory#preInstantiateSingletons`：确认回调时拿到的是哪种类型  

入口：

- 入口测试（方法级）：`SpringCoreBeansSmartInitializingSingletonLabTest#afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans`
- 推荐跑法：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartInitializingSingletonLabTest#afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans test`

## 排障分流：这是定义层问题还是实例层问题？

- “回调没触发” → **实例层（生命周期时机）**：该 bean 是否是 singleton？context 是否真的 refresh？
- “回调里拿不到 lazy bean 实例” → **实例层语义**：这是预期；lazy-init 在 refresh 阶段不会创建（对照 [18](023-18-lazy-semantics.md)）
- “回调里 `getBean` 导致启动变慢” → **实例层行为**：读者把 lazy bean 全部提前创建了（本章第 3 节）
- “我以为它等价于 ApplicationRunner” → **生命周期粒度差异**：它更贴近 BeanFactory 的创建阶段（本章第 2 节 + `preInstantiateSingletons`）

## 4. 面试常问（SmartInitializingSingleton）

1) `SmartInitializingSingleton#afterSingletonsInstantiated` 触发于 refresh 的哪个阶段？为什么它早于 lazy bean 的创建？
2) 为什么它不等价于 `ApplicationRunner`？（提示：它挂在 BeanFactory 的 preInstantiateSingletons 尾部）
3) 在回调里调用 `getBean(lazy)` 会带来什么后果？如何判断是否能够“提前把 lazy 全部创建了”？

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
2) `SmartInitializingSingleton#afterSingletonsInstantiated`（在 Lab 里的实现）：观察回调触发时机与可见的单例集合
3) `DefaultSingletonBeanRegistry#getSingleton`：在回调里或断言点查看 lazy bean 是否已在缓存中
4) `AbstractBeanFactory#doGetBean`：在测试后半段第一次 `getBean(lazy)` 时观察真正创建发生在哪里

- 应能够解释清楚：为什么 `afterSingletonsInstantiated` 触发时 lazy bean 可能还没创建吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`
推荐断点：`DefaultListableBeanFactory#preInstantiateSingletons`、`SmartInitializingSingleton#afterSingletonsInstantiated`、`AbstractAutowireCapableBeanFactory#doCreateBean`

## 常见误区与边界

### 常见误区

- **误区 1：误以为它能看到 lazy bean 实例**
  - 它看到的是“已创建的单例”。lazy bean 可能还没创建。

- **误区 2：在回调里触发大量 `getBean`**
  - 会把 lazy bean 全部提前创建，可能导致启动变慢。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

## 自检要点
应能够解释清楚：

1) **`afterSingletonsInstantiated` 发生在 refresh 的哪个窗口？它与 `preInstantiateSingletons` 的关系是什么？**
2) **为什么 lazy 单例通常不在这个回调覆盖范围内？**（lazy-init 与按需创建语义）
3) **SmartInitializingSingleton 与 ContextRefreshedEvent 各适合做什么？如何选择并证明？**

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansSmartInitializingSingletonLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`

上一章：[25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](25-programmatic-bpp-registration.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[27. SmartLifecycle：phase 与 start/stop 顺序](27-smart-lifecycle-phase.md)

<!-- BOOKIFY:END -->
