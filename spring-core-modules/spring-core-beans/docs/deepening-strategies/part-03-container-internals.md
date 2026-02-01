# 逐章内容级再加深建议（part-03-container-internals）

本 Part 的再加深重点：从“能讲流程”升级到“能讲算法/能讲关键分支/能用断点证明”，并强化与真实排障的连接。

## 执行化提示（Internals 章的最低交付）

- 每章至少给出 1 条“最短调用链 + 决策点 + 关键变量”的证据链（避免只讲概念）。
- 每章至少给出 1 个“现象→阶段→入口方法→判定标准”的排障分流，确保可迁移到生产问题。

### 第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
- 继续加深建议：
    - `SpringCoreBeansBootstrapInternalsLabTest`（再对照 `SpringCoreBeansResourceInjectionLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“补充：如何识别“基础设施 Bean”（`ROLE_INFRASTRUCTURE`）以及它对排障的意义”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 13. BDRPP：注册阶段动态加定义

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`
- 继续加深建议：
    - `SpringCoreBeansRegistryPostProcessorLabTest`（再对照 `SpringCoreBeansRegistryPostProcessorLabTest.beanDefinitionRegistryPostProcessor_canRegisterNewBeanDefinitions()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
- 继续加深建议：
    - `SpringCoreBeansPostProcessorOrderingLabTest`（再对照 `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `PostProcessorRegistrationDelegate#sortPostProcessors` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 15. 实例化前短路：postProcessBeforeInstantiation

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`
- 继续加深建议：
    - `SpringCoreBeansPreInstantiationLabTest`（再对照 `SpringCoreBeansPreInstantiationLabTest.withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 16. early reference 与循环依赖：getEarlyBeanReference

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
- 继续加深建议：
    - `SpringCoreBeansEarlyReferenceLabTest`（再对照 `SpringCoreBeansRawInjectionDespiteWrappingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultSingletonBeanRegistry#getSingleton` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“一页式最短证据链（10 分钟）：观察到 factory 层价值 + early 形态决策”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 17. 生命周期回调顺序：Aware / BPP / init / destroy

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`
- 继续加深建议：
    - `SpringCoreBeansLifecycleCallbackOrderLabTest`（再对照 `SpringCoreBeansLifecycleCallbackOrderLabTest.singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#doCreateBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 18. refresh → doCreateBean 主线（源码级）

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- 继续加深建议：
    - `SpringCoreBeansBootstrapInternalsLabTest`（再对照 `SpringCoreBeansRegistryPostProcessorLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：现象 → 阶段 → 关键方法 → 必看变量 → 对应 LabTest”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
