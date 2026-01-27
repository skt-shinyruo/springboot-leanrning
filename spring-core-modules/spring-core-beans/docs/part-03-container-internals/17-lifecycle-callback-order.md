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

## 3. 源码调用链（方法级）：初始化与销毁发生在哪里？

这章你只需要记住两个“串联点”：

- 初始化串联点：`AbstractAutowireCapableBeanFactory#initializeBean`
- 销毁串联点：`DisposableBeanAdapter#destroy`

初始化（init）最短链路（方法级）：

1) `AbstractAutowireCapableBeanFactory#doCreateBean`
2) `AbstractAutowireCapableBeanFactory#populateBean`（注入发生点：早于 init callbacks）
3) `AbstractAutowireCapableBeanFactory#initializeBean`
   - `invokeAwareMethods`（Aware 系列）
   - `applyBeanPostProcessorsBeforeInitialization`（before-init BPP；`@PostConstruct` 常在这里附近触发）
   - `invokeInitMethods`（`afterPropertiesSet` / initMethod）
   - `applyBeanPostProcessorsAfterInitialization`（after-init BPP；代理/替换经常发生在这里）

销毁（destroy）最短链路（方法级）：

1) `AbstractApplicationContext#doClose`
2) `DefaultSingletonBeanRegistry#destroySingletons`（默认只管 singleton）
3) `DisposableBeanAdapter#destroy`
   - `DestructionAwareBeanPostProcessor#postProcessBeforeDestruction`
   - `@PreDestroy`
   - `DisposableBean#destroy` / destroyMethod

## 4. 排障决策表（生命周期：从“没执行”到“证据链”）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `@PostConstruct` 没执行 | 没注册对应 BPP；或目标对象根本不是容器创建的 | `beanFactory.getBeanPostProcessors()` 是否包含 `CommonAnnotationBeanPostProcessor`；断点 `applyBeanPostProcessorsBeforeInitialization` 是否命中目标 beanName | 确保完整 bootstrap；不要自己 `new`；必要时注册 annotation processors | `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` |
| `afterPropertiesSet/initMethod` 没执行 | 没走到 `initializeBean`；或被短路/替换导致误判 | 断点 `AbstractAutowireCapableBeanFactory#initializeBean`；看 `exposedObject` 是否被替换 | 先确认创建主线；如存在代理/短路，分清 raw vs exposed | `SpringCoreBeansLifecycleCallbackOrderLabTest` |
| `@PreDestroy` 没执行 | context 没 close；或是 prototype（默认不托管销毁） | 断点 `AbstractApplicationContext#doClose` / `destroySingletons`；prototype 不会进入 `disposableBeans` | 确保关闭容器；prototype 要显式销毁（`destroyBean`） | `SpringCoreBeansLifecycleCallbackOrderLabTest` |
| “顺序很怪/有时不触发” | bean 创建过早错过 BPP；或 BPP 自己依赖复杂 bean | 断点 `registerBeanPostProcessors` 与目标 bean 创建时机对照；看 BPP 的依赖链 | 避免在 BFPP/BDRPP 阶段触发目标 bean；给 BPP 降低依赖 | 结合 ordering 与 programmatic BPP 章节 |

## 5. 面试常问（生命周期：顺序、触发者与边界）

### Q1：初始化阶段的回调顺序是什么？`@PostConstruct` 在哪？

- 标准答案（可复述）：
  - constructor → `populateBean`（注入）→ Aware → before-init BPP（这里附近触发 `@PostConstruct`）→ init callbacks（`afterPropertiesSet/initMethod`）→ after-init BPP（可能返回 proxy）。
- 证据链（方法级）：
  - `AbstractAutowireCapableBeanFactory#initializeBean`
  - `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`
- 最小复现：
  - `SpringCoreBeansLifecycleCallbackOrderLabTest`

### Q2：为什么 prototype 默认不会走 `@PreDestroy`？

- 标准答案（可复述）：
  - prototype 的销毁默认不由容器统一托管；容器只负责创建与注入，不会在 close 时回收它创建过的所有 prototype 实例。
- 证据链（方法级）：
  - `DefaultSingletonBeanRegistry#destroySingletons`（只管 singleton）
  - `ConfigurableBeanFactory#destroyBean`（手动销毁入口）
- 最小复现：
  - `SpringCoreBeansLifecycleCallbackOrderLabTest.prototypeBeans_areNotDestroyedByContainerByDefault`

### Q3：为什么说 `postProcessAfterInitialization` 一定晚于 init callbacks？

- 标准答案（可复述）：
  - 因为 `initializeBean` 的内部顺序固定：before-init BPP → `invokeInitMethods` → after-init BPP；after-init BPP 的返回值决定最终暴露对象。
- 证据链（方法级）：
  - `AbstractAutowireCapableBeanFactory#initializeBean`

## 6. 一句话自检

你应该能回答：

1) init callbacks 为什么夹在 before/after-init BPP 之间？
2) prototype 默认为什么不会触发销毁回调？
3) 当你怀疑“回调没执行/执行顺序怪”，你会先看哪两个断点？（提示：`initializeBean` 与 `destroySingletons`）

## 常见坑与边界

> 注意：顺序表的意义是“能定位”，不是“每次都一模一样”。当 BPP 数量与排序变化时（见 [14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](14-post-processor-ordering.md)、[25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)），你看到的实际调用栈会变化，但大方向依然稳定。

- **坑 1：在 `@PostConstruct` 做重 IO**
  - 会拉长启动时间，也更难测试与复用。
- **坑 2：误以为 prototype 会自动销毁**
  - 你必须明确“谁负责 close/cleanup”，否则资源泄漏很隐蔽。
- **坑 3：BPP 本身也是特殊 bean**
  - BPP 会很早被实例化、很早被注册；在 BPP 构造器里依赖复杂 bean，容易触发“过早创建”与“错过后续处理器”。

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
