# Task List: spring-core-beans 知识点 × 源码解析深挖方案（Deep Dive）

Directory: `helloagents/history/2026-01/202601030652_spring-core-beans-source-deep-dive/`

---

## 1. spring-core-beans（源码主线与知识点闭环）

- [-] 1.1 准备：确保 IDE 可 Step Into Spring 源码（必要时执行 `mvn -pl spring-core-beans -DskipTests dependency:sources`），验证 why.md#scenario-refresh-mainline-12-steps（本次执行聚焦补文档，不进入断点）
- [-] 1.2 跑 `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`（或任意 context.refresh 用例），从 `AbstractApplicationContext#refresh` 进入，写出 12 步主线（每步 1 行即可）并标注 BFPP/BPP 所在步骤，验证 why.md#scenario-refresh-mainline-12-steps（本次执行聚焦补文档，不进入断点）

- [-] 1.3 跑 `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansImportLabTest`，挑 1 个你关心的 beanName，读取其 `BeanDefinition` 元信息并推断来源（scan/@Bean/@Import），验证 why.md#scenario-definition-registration-pipeline（本次执行聚焦补文档，不进入断点）
- [-] 1.4 源码追踪：在 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 下断点，确认 BDRPP/BFPP 的两阶段执行与排序（PriorityOrdered/Ordered/无序），验证 why.md#scenario-definition-registration-pipeline（本次执行聚焦补文档，不进入断点）

- [-] 1.5 跑 `SpringCoreBeansInjectionAmbiguityLabTest`，在 `DefaultListableBeanFactory#doResolveDependency` 下断点，记录候选集合与最终确定化选择路径（至少写出 3 个关键方法名），验证 why.md#scenario-di-resolution-algorithm（本次执行聚焦补文档，不进入断点）
- [-] 1.6 跑 `SpringCoreBeansAutowireCandidateSelectionLabTest`，回答：`@Primary/@Qualifier/@Priority/@Order` 分别影响“候选过滤/确定化选择/排序链路”的哪一部分，并给出一个反例证明 `@Order` 不是“选择候选”的手段，验证 why.md#scenario-di-resolution-algorithm（本次执行聚焦补文档，不进入断点）

- [-] 1.7 跑 `SpringCoreBeansBeanCreationTraceLabTest`，对照 events 序列，把每一段映射到源码方法（至少覆盖 instantiate/populate/initialize/afterInit replacement），验证 why.md#scenario-bean-creation-trace-and-bpp-hooks（本次执行聚焦补文档，不进入断点）
- [-] 1.8 源码追踪：在 `AbstractAutowireCapableBeanFactory#doCreateBean` / `populateBean` / `initializeBean` 下断点，确认 BPP/IABPP 的插入点与返回值如何影响“最终暴露对象”，验证 why.md#scenario-bean-creation-trace-and-bpp-hooks（本次执行聚焦补文档，不进入断点）

- [-] 1.9 跑 `SpringCoreBeansProxyingPhaseLabTest`，在以下入口分别观察是否/何时产生 proxy：`resolveBeforeInstantiation`、`getEarlyBeanReference`、`postProcessAfterInitialization`，验证 why.md#scenario-proxy-replacement-three-places（本次执行聚焦补文档，不进入断点）

- [-] 1.10 跑 `SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`，在 `DefaultSingletonBeanRegistry#getSingleton` 下断点，观察三级缓存命中路径（singletonFactories/earlySingletonObjects/singletonObjects），验证 why.md#scenario-circular-deps-3-level-cache（本次执行聚焦补文档，不进入断点）
- [-] 1.11 跑 `SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`，解释为什么构造器循环依赖更难解（与 instantiate 阶段强相关），验证 why.md#scenario-circular-deps-3-level-cache（本次执行聚焦补文档，不进入断点）

- [-] 1.12 跑 `SpringCoreBeansContainerLabTest` 的两个 `proxyBeanMethods` 用例，定位增强器生效位置（配置类被 enhance 的时机），并用一句话解释“为什么 true 保住单例语义、false 会产生额外实例”，验证 why.md#scenario-configuration-enhancement（本次执行聚焦补文档，不进入断点）

- [-] 1.13 跑 `SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory`，定位 `FactoryBean` 分流入口（name vs &name），并解释 `isSingleton()` 对 product 缓存的影响，验证 why.md#scenario-factorybean-semantics（本次执行聚焦补文档，不进入断点）

## 2. Security Check
- [√] 2.1 确认本方案仅涉及本地源码阅读与单元测试运行，不连接生产环境、不写入任何密钥/Token/个人敏感信息（per G9）

## 3. Testing
- [-] 3.1 关键用例回归：`mvn -pl spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`（已被 3.2 覆盖）
- [√] 3.2 完整模块回归（可选）：`mvn -pl spring-core-beans test`

## 4. Documentation（本次必做：把源码解析写进 docs，并引用仓库 src 代码）
- [√] 4.1 更新 `docs/beans/spring-core-beans/01-bean-mental-model.md`：补齐 refresh/定义层/实例层/最终暴露对象的源码主线，并引用本仓库对应 Lab 代码片段
- [√] 4.2 更新 `docs/beans/spring-core-beans/01-bean-registration.md`：补齐 ConfigurationClassPostProcessor/扫描/@Import/registrar 的源码解析，并引用本仓库示例代码片段
- [√] 4.3 更新 `docs/beans/spring-core-beans/03-dependency-injection-resolution.md`：补齐 `doResolveDependency` 候选收集与确定化路径的源码解析，并引用本仓库注入歧义相关 Lab 代码片段
- [√] 4.4 更新 `docs/beans/spring-core-beans/05-lifecycle-and-callbacks.md`：补齐 doCreateBean 三阶段与回调顺序的源码解析，并引用本仓库 lifecycle Lab/示例代码片段
- [√] 4.5 更新 `docs/beans/spring-core-beans/08-circular-dependencies.md`：补齐三级缓存与 early reference 的源码解析，并引用本仓库循环依赖最小复现代码片段

## 5. Knowledge Base Sync & Migration
- [√] 5.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：记录本次 docs 源码解析补强与入口
- [√] 5.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`：登记本次变更
- [√] 5.3 迁移方案包到 `helloagents/history/2026-01/202601030652_spring-core-beans-source-deep-dive/`
