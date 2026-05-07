# 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单收敛原因。

    观察对象：17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansLifecycleCallbackOrderLabTest`。需要下探源码时，可以从 `BeanPostProcessor#postProcessBeforeInitialization` / `InitializingBean#afterPropertiesSet` / `BeanPostProcessor#postProcessAfterInitialization` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：回调顺序决定了对象处在哪个形态

生命周期顺序是解释很多“表面上不按预期发生”的容器行为的钥匙：注入为什么发生在回调之前？代理到底在哪一步出现？为什么 `@PreDestroy` 有时永远不会触发？

这一章聚焦两个高频问题：

1. 一个 singleton bean 从创建到初始化，会按什么顺序触发 Aware、BPP、`@PostConstruct/afterPropertiesSet/initMethod`？
2. 为什么 prototype 默认不会在容器关闭时触发 `@PreDestroy/destroyMethod`？

先运行 `SpringCoreBeansLifecycleCallbackOrderLabTest`，把回调顺序与 prototype 边界跑成断言；再回正文对照 `initializeBean` 与 `DisposableBeanAdapter#destroy` 两个“串联点”，读者就能把“触发/不触发”定位回源码分支。

- 最小运行入口：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleCallbackOrderLabTest test`
- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（Scopes，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html
- 官方文档对照（容器扩展点，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`


## 机制主线：初始化链与销毁链分开看

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

很多“容器行为”只有把生命周期顺序看清楚才能解释。

### 回调来源分型（触发时机 / 优先级）

| 回调类型 | 触发时机 | 备注 |
| --- | --- | --- |
| `@PostConstruct/@PreDestroy` | before-init / destroy | 依赖 `CommonAnnotationBeanPostProcessor` |
| `InitializingBean/DisposableBean` | init / destroy | 接口回调，位置稳定 |
| `initMethod/destroyMethod` | init / destroy | 配置级回调 |
| `SmartInitializingSingleton` | 单例创建完成后 | 容器就绪后回调 |
| `Lifecycle/SmartLifecycle` | start/stop 阶段 | 受 phase 顺序影响 |

## 一个可断言的顺序：比看日志更可靠

本节的目标不是背顺序，而是让读者在对象行为不对时，能判断它到底处在生命周期的哪一段、被哪些扩展点改过。

下面给一个“够读者排障”的顺序表（把它当成 `initializeBean` 周边的时间线）：

1. 实例化（constructor / factory method）
2. 属性填充（依赖注入）→ `populateBean`
3. Aware 回调（`BeanNameAware`/`BeanFactoryAware` 等）
4. `BeanPostProcessor#postProcessBeforeInitialization`
5. `@PostConstruct`（由 `InitDestroyAnnotationBeanPostProcessor` 触发）
6. `InitializingBean#afterPropertiesSet`
7. 自定义 initMethod（`@Bean(initMethod=...)`）
8. `BeanPostProcessor#postProcessAfterInitialization`（代理/包装经常在这里发生，见 [代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](proxying-phase.md)）

销毁阶段（容器关闭时，singleton 才会默认触发）：

1. `DestructionAwareBeanPostProcessor#postProcessBeforeDestruction`
2. `@PreDestroy`（同样由注解后处理器触发）
3. `DisposableBean#destroy`
4. 自定义 destroyMethod（`@Bean(destroyMethod=...)`）

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
  - `singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`（初始化顺序：aware → BPP before → @PostConstruct → afterPropertiesSet/initMethod → BPP after）

该用例只断言关键节点的相对顺序；读者无需依赖日志，按断言与断点即可复核。

学习重点：

- init callbacks 都发生在 BPP(before) 与 BPP(after) 之间
- destroy callbacks 发生在 context close 阶段

### 1.1 关键分支条件（决定“触发/不触发”）

- `mbd.isSingleton()`：决定是否进入统一销毁链路
- `mbd.hasInitMethod()` / `mbd.hasDestroyMethod()`：决定是否调用自定义回调
- `beanFactory.hasDestructionAwareBeanPostProcessors()`：决定是否触发 `@PreDestroy` 等回调

### 1.2 回调与代理交织：回调到底发生在谁身上？

- `@PostConstruct` / `afterPropertiesSet`：发生在 raw bean（proxy 还没产生）
- after-init BPP：可能返回 proxy，最终暴露对象可能不是 raw
- `@PreDestroy`：通常由 DestructionAwareBPP 触发，目标仍指向 raw/target

## prototype 为什么默认不走销毁回调？

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
  - `prototypeBeans_areNotDestroyedByContainerByDefault()`（证据：close context 时 prototype 不会触发 destroy 回调）

prototype 的语义是：

- 容器帮读者创建并注入
- **但容器通常不负责管理它的“生命周期终点”**

所以：

- `@PreDestroy` / destroyMethod 可能不会被调用
- 清理资源需要调用方自己管理（或引入额外机制）

入口：

最小复现入口（方法级）：

- `SpringCoreBeansLifecycleCallbackOrderLabTest.singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`
- `SpringCoreBeansLifecycleCallbackOrderLabTest.prototypeBeans_areNotDestroyedByContainerByDefault()`

断点入口（闭环版）：

1. `AbstractAutowireCapableBeanFactory#doCreateBean`：创建主线（串起实例化/注入/初始化）
2. `AbstractAutowireCapableBeanFactory#populateBean`：注入发生点（验证：注入早于 init callbacks）
3. `AbstractAutowireCapableBeanFactory#initializeBean`：初始化串联点（aware → before-init → init callbacks → after-init）
4. `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`：`@PostConstruct` 触发点
5. `DisposableBeanAdapter#destroy`：销毁链路统一入口（close context 时命中）

应当看到：

- singleton 的 init callbacks 稳定发生在 BPP(before) 与 BPP(after) 之间
- prototype 在容器 close 时不会被自动 destroy（除非读者自己显式管理）

## 可复现闭环（基于 `SpringCoreBeansLifecycleCallbackOrderLabTest`）

完成该组用例后，至少需要复述 3 条结论：

1. **注解回调依赖基础设施处理器**
   - 断点：`registerAnnotationConfigProcessors`
   - 断言：不注册 → `@PostConstruct` 不触发
2. **回调顺序可被稳定断言**
   - 断点：`initializeBean`
   - 断言：Aware → before-init → init → after-init
3. **prototype 默认不进入销毁链路**
   - 断点：`destroySingletons`
   - 断言：prototype 不在 `disposableBeans`

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- “`@PostConstruct` 没触发/注入为 null” → **优先定义层/基础设施问题**：容器是否具备注解处理器？（见 [12](container-bootstrap-and-infrastructure.md)）
- “`@PreDestroy` 没触发” → **优先实例层/生命周期语义问题**：是不是 prototype？context 是否真的 close？（本章第 2 节）
- “BPP 里依赖复杂 bean 导致顺序怪异” → **实例层 + 顺序问题**：BPP 本身会很早创建/注册，必要时拆分依赖（对照 [14](post-processor-ordering.md)、[25](programmatic-bpp-registration.md)）
- “误认为 destroy 回调一定会执行” → **实例层 + scope 语义问题**：prototype 的销毁不由容器托管（本章第 2 节）

## 源码调用链（方法级）：初始化与销毁发生在哪里？

这章读者只需要记住两个“串联点”：

- 初始化串联点：`AbstractAutowireCapableBeanFactory#initializeBean`
- 销毁串联点：`DisposableBeanAdapter#destroy`

初始化（init）最短链路（方法级）：

1. `AbstractAutowireCapableBeanFactory#doCreateBean`
2. `AbstractAutowireCapableBeanFactory#populateBean`（注入发生点：早于 init callbacks）
3. `AbstractAutowireCapableBeanFactory#initializeBean`
   - `invokeAwareMethods`（Aware 系列）
   - `applyBeanPostProcessorsBeforeInitialization`（before-init BPP；`@PostConstruct` 常在这里附近触发）
   - `invokeInitMethods`（`afterPropertiesSet` / initMethod）
   - `applyBeanPostProcessorsAfterInitialization`（after-init BPP；代理/替换经常发生在这里）

销毁（destroy）最短链路（方法级）：

1. `AbstractApplicationContext#doClose`
2. `DefaultSingletonBeanRegistry#destroySingletons`（默认只管 singleton）
3. `DisposableBeanAdapter#destroy`
   - `DestructionAwareBeanPostProcessor#postProcessBeforeDestruction`
   - `@PreDestroy`
   - `DisposableBean#destroy` / destroyMethod

## 排障决策表（生命周期：从“没执行”到“证据链”）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `@PostConstruct` 没执行 | 没注册对应 BPP；或目标对象根本不是容器创建的 | `beanFactory.getBeanPostProcessors()` 是否包含 `CommonAnnotationBeanPostProcessor`；断点 `applyBeanPostProcessorsBeforeInitialization` 是否命中目标 beanName | 确保完整 bootstrap；不要自己 `new`；必要时注册 annotation processors | `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` |
| `afterPropertiesSet/initMethod` 没执行 | 没走到 `initializeBean`；或被短路/替换导致误判 | 断点 `AbstractAutowireCapableBeanFactory#initializeBean`；看 `exposedObject` 是否被替换 | 先确认创建主线；如存在代理/短路，分清 raw vs exposed | `SpringCoreBeansLifecycleCallbackOrderLabTest` |
| `@PreDestroy` 没执行 | context 没 close；或是 prototype（默认不托管销毁） | 断点 `AbstractApplicationContext#doClose` / `destroySingletons`；prototype 不会进入 `disposableBeans` | 确保关闭容器；prototype 要显式销毁（`destroyBean`） | `SpringCoreBeansLifecycleCallbackOrderLabTest` |
| “顺序很怪/有时不触发” | bean 创建过早错过 BPP；或 BPP 自己依赖复杂 bean | 断点 `registerBeanPostProcessors` 与目标 bean 创建时机对照；看 BPP 的依赖链 | 避免在 BFPP/BDRPP 阶段触发目标 bean；给 BPP 降低依赖 | 结合 ordering 与 programmatic BPP 章节 |

## 面试常问（生命周期：顺序、触发者与边界）

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

## 验收口径：能把回调映射到 `initializeBean`

需要能回答：

1. init callbacks 为什么夹在 before/after-init BPP 之间？
2. prototype 默认为什么不会触发销毁回调？
3. 当读者怀疑“回调未执行/执行顺序异常”，可优先关注哪两个断点？（提示：`initializeBean` 与 `destroySingletons`）

## 边界：prototype 与 singleton 的销毁语义不同

> 注意：顺序表的意义是“能定位”，不是“每次都一模一样”。当 BPP 数量与排序变化时（见 [顺序（Ordering）：PriorityOrdered / Ordered / 无序](post-processor-ordering.md)、[手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](programmatic-bpp-registration.md)），观察到的实际调用栈会变化，但大方向依然稳定。

- **误区 1：在 `@PostConstruct` 做重 IO**
  - 会拉长启动时间，也更难测试与复用。
- **误区 2：误以为 prototype 会自动销毁**
  - 必须明确“谁负责 close/cleanup”，否则资源泄漏很隐蔽。
- **误区 3：BPP 本身也是特殊 bean**
  - BPP 会很早被实例化、很早被注册；在 BPP 构造器里依赖复杂 bean，容易触发“过早创建”与“错过后续处理器”。

## 小结：生命周期顺序要用断言固定

- `AbstractAutowireCapableBeanFactory#doCreateBean`：单个 bean 创建主流程（实例化 → 注入 → 初始化）
- `AbstractAutowireCapableBeanFactory#populateBean`：属性填充阶段（`@Autowired/@Resource` 等注入发生在这一段）
- `AbstractAutowireCapableBeanFactory#initializeBean`：初始化阶段（aware → before-init → init callbacks → after-init）
- `DisposableBeanAdapter#destroy`：销毁链路的统一入口（`@PreDestroy/DisposableBean/destroyMethod` 会在这里串起来）
- `AbstractApplicationContext#doClose`：context close 阶段触发销毁回调（prototype 默认不在这里被销毁）

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`

<!-- BOOKIFY:END -->
