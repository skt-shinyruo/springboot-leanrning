# 17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）

## 导读

- 本章主题：**17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`

## 机制主线

很多“容器行为”只有把生命周期顺序看清楚才能解释。

## 1. 一个可断言的顺序（比看日志更可靠）

读者 C 的目标不是“背顺序”，而是：**当你看到一个对象行为不对时，能判断它到底处在生命周期的哪一段、被哪些扩展点改过**。

下面给一个“够你排障”的顺序表（把它当成 `initializeBean` 周边的时间线）：

1. 实例化（constructor / factory method）
2. 属性填充（依赖注入）→ `populateBean`
3. Aware 回调（`BeanNameAware`/`BeanFactoryAware` 等）
4. `BeanPostProcessor#postProcessBeforeInitialization`
5. `@PostConstruct`（由 `InitDestroyAnnotationBeanPostProcessor` 触发）
6. `InitializingBean#afterPropertiesSet`
7. 自定义 initMethod（`@Bean(initMethod=...)`）
8. `BeanPostProcessor#postProcessAfterInitialization`（代理/包装经常在这里发生，见 [31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）

销毁阶段（容器关闭时，singleton 才会默认触发）：

1. `DestructionAwareBeanPostProcessor#postProcessBeforeDestruction`
2. `@PreDestroy`（同样由注解后处理器触发）
3. `DisposableBean#destroy`
4. 自定义 destroyMethod（`@Bean(destroyMethod=...)`）

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
  - `singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`（初始化顺序：aware → BPP before → @PostConstruct → afterPropertiesSet/initMethod → BPP after）

- constructor
- BeanNameAware / BeanFactoryAware
- BeanPostProcessor.beforeInit
- `@PostConstruct`
- `InitializingBean.afterPropertiesSet`
- `initMethod`
- BeanPostProcessor.afterInit
-（容器关闭时）`@PreDestroy` → `DisposableBean.destroy` → `destroyMethod`

学习重点：

- init callbacks 都发生在 BPP(before) 与 BPP(after) 之间
- destroy callbacks 发生在 context close 阶段

## 2. prototype 为什么默认不走销毁回调？

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
  - `prototypeBeans_areNotDestroyedByContainerByDefault()`（证据：close context 时 prototype 不会触发 destroy 回调）

prototype 的语义是：

- 容器帮你创建并注入
- **但容器通常不负责管理它的“生命周期终点”**

所以：

- `@PreDestroy` / destroyMethod 可能不会被调用
- 清理资源需要调用方自己管理（或引入额外机制）

入口：

最小复现入口（方法级）：

- `SpringCoreBeansLifecycleCallbackOrderLabTest.singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`
- `SpringCoreBeansLifecycleCallbackOrderLabTest.prototypeBeans_areNotDestroyedByContainerByDefault()`

推荐断点（闭环版）：

1) `AbstractAutowireCapableBeanFactory#doCreateBean`：创建主线（串起实例化/注入/初始化）
2) `AbstractAutowireCapableBeanFactory#populateBean`：注入发生点（验证：注入早于 init callbacks）
3) `AbstractAutowireCapableBeanFactory#initializeBean`：初始化串联点（aware → before-init → init callbacks → after-init）
4) `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`：`@PostConstruct` 触发点
5) `DisposableBeanAdapter#destroy`：销毁链路统一入口（close context 时命中）

你应该看到：

- singleton 的 init callbacks 稳定发生在 BPP(before) 与 BPP(after) 之间
- prototype 在容器 close 时不会被自动 destroy（除非你自己显式管理）

## 排障分流：这是定义层问题还是实例层问题？

- “`@PostConstruct` 没触发/注入为 null” → **优先定义层/基础设施问题**：容器是否具备注解处理器？（见 [12](022-12-container-bootstrap-and-infrastructure.md)）
- “`@PreDestroy` 没触发” → **优先实例层/生命周期语义问题**：是不是 prototype？context 是否真的 close？（本章第 2 节）
- “BPP 里依赖复杂 bean 导致顺序怪异” → **实例层 + 顺序问题**：BPP 本身会很早创建/注册，必要时拆分依赖（对照 [14](14-post-processor-ordering.md)、[25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)）
- “我以为 destroy 回调一定会执行” → **实例层 + scope 语义问题**：prototype 的销毁不由容器托管（本章第 2 节）

## 4. 一句话自检

- 常问：初始化阶段的回调顺序是什么？`@PostConstruct` 在 BPP 的哪个切面里？
  - 答题要点：constructor → populate（注入）→ aware → BPP before-init → `@PostConstruct` → `afterPropertiesSet` → initMethod → BPP after-init。
- 常见追问：为什么 prototype 默认不会触发 `@PreDestroy`？
  - 答题要点：prototype 只负责创建与注入，生命周期末端默认不由容器统一回收；close context 时只会销毁 singleton（除非自定义 scope/显式销毁）。
- 常见追问：如何用断点证明“BPP after-init 一定发生在 init callbacks 之后”？
  - 答题要点：以 `initializeBean` 为入口，看它内部顺序：before-init BPP → invokeInitMethods → after-init BPP。

## 面试常问（生命周期回调顺序）

- 常问：初始化阶段的回调顺序是什么？BPP before/after-init 与 `@PostConstruct` 谁先谁后？
  - 答题要点：constructor → aware → populate（注入）→ BPP before-init → `@PostConstruct` → `afterPropertiesSet` → initMethod → BPP after-init。
- 常见追问：为什么 prototype 默认不会走销毁回调（`@PreDestroy`）？
  - 答题要点：prototype 的生命周期末端默认不由容器托管；容器负责创建，但不负责统一回收（除非自定义 scope/显式销毁）。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleCallbackOrderLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`

- `SpringCoreBeansLifecycleCallbackOrderLabTest.singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`

实验使用一个记录器把关键阶段串起来：

- `SpringCoreBeansLifecycleCallbackOrderLabTest.prototypeBeans_areNotDestroyedByContainerByDefault()`

## 源码锚点（建议从这里下断点）

- `AbstractAutowireCapableBeanFactory#doCreateBean` / `populateBean` / `initializeBean`（创建全链路）
- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`（`@PostConstruct` 触发点）
- `AbstractAutowireCapableBeanFactory#invokeInitMethods`（`afterPropertiesSet` / initMethod）
- `DisposableBeanAdapter#destroy`（销毁全链路）
- `AbstractApplicationContext#doClose`（close context 触发销毁）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
  - `singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`
  - `prototypeBeans_areNotDestroyedByContainerByDefault()`

建议断点：

1) 参与实验的目标 bean 构造器：观察“构造器先执行，但此时注入未发生”
2) `AbstractAutowireCapableBeanFactory#initializeBean`：观察 init 回调链如何被串起来
3) `BeanPostProcessor#postProcessBeforeInitialization`（例如 `CommonAnnotationBeanPostProcessor`）：观察 `@PostConstruct` 的触发时机
4) `BeanPostProcessor#postProcessAfterInitialization`：观察它一定发生在 init callbacks 之后（本章自检题的答案）
5) `DisposableBeanAdapter#destroy`：在测试里 close context 时命中，观察销毁回调顺序

- 你能解释清楚：为什么 `postProcessAfterInitialization` 一定发生在 init callbacks 之后吗？
- 你能解释清楚：为什么 prototype 默认不会触发 `@PreDestroy` 吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
推荐断点：`AbstractAutowireCapableBeanFactory#initializeBean`、`BeanPostProcessor#postProcessBeforeInitialization`、`DisposableBeanAdapter#destroy`

## 常见坑与边界

> 注意：顺序表的意义是“能定位”，不是“每次都一模一样”。当 BPP 数量与排序变化时（见 [14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](14-post-processor-ordering.md)、[25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)），你看到的实际调用栈会变化，但大方向依然稳定。

## 3. 常见坑

- **坑 1：在 `@PostConstruct` 做重 IO**
  - 会拉长启动时间；也更难测试。

- **坑 2：误以为 prototype 会自动销毁**
  - 你必须知道：谁负责 close/cleanup。

- **坑 3：BeanPostProcessor 本身也是特殊 bean**
  - BPP 会很早被实例化、很早被注册。
  - 因此在 BPP 的构造器里依赖复杂 bean，可能导致“过早创建”与“错过后续处理器”。

## 小结与下一章

- `AbstractAutowireCapableBeanFactory#doCreateBean`：单个 bean 创建主流程（实例化 → 注入 → 初始化）
- `AbstractAutowireCapableBeanFactory#populateBean`：属性填充阶段（`@Autowired/@Resource` 等注入发生在这一段）
- `AbstractAutowireCapableBeanFactory#initializeBean`：初始化阶段（aware → before-init → init callbacks → after-init）
- `DisposableBeanAdapter#destroy`：销毁链路的统一入口（`@PreDestroy/DisposableBean/destroyMethod` 会在这里串起来）
- `AbstractApplicationContext#doClose`：context close 阶段触发销毁回调（prototype 默认不在这里被销毁）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`

上一章：[16. early reference 与循环依赖：getEarlyBeanReference](16-early-reference-and-circular.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[18. @Lazy 的真实语义：延迟的是谁、延迟到哪一步](../part-04-wiring-and-boundaries/023-18-lazy-semantics.md)

<!-- BOOKIFY:END -->
