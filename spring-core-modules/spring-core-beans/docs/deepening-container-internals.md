# 章节深化路线（Container Internals）

## 定位：Container Internals 章节的深化方式

Container Internals 章节要把 `refresh()` 主线、后处理器算法、缓存边界和生命周期窗口讲成可验证的执行过程。深化时不以“流程更长”为目标，而以“关键分支能被断点证明”为目标。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（容器扩展点）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


本部分的再加深重点，是从“能讲流程”升级到“能讲算法、能讲关键分支、能用断点证明”，并强化与真实排障的连接。

## 执行化提示（Internals 章的最低交付）

- 每章至少给出 1 条“最短调用链 + 决策点 + 关键变量”的证据链，避免只讲概念。
- 每章至少给出 1 个“现象 → 阶段 → 入口方法 → 判定标准”的排障分流，确保能迁移到生产问题。

### 容器启动与基础设施处理器：为什么注解能工作？

- 文件：`spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md`
- 深化落点：
    - `SpringCoreBeansBootstrapInternalsLabTest`（再对照 `SpringCoreBeansResourceInjectionLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“补充：如何识别“基础设施 Bean”（`ROLE_INFRASTRUCTURE`）以及它对排障的意义”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### BDRPP：注册阶段动态加定义

- 文件：`spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md`
- 深化落点：
    - `SpringCoreBeansRegistryPostProcessorLabTest`（再对照 `SpringCoreBeansRegistryPostProcessorLabTest.beanDefinitionRegistryPostProcessor_canRegisterNewBeanDefinitions()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 顺序（Ordering）：PriorityOrdered / Ordered / 无序

- 文件：`spring-core-modules/spring-core-beans/docs/post-processor-ordering.md`
- 深化落点：
    - `SpringCoreBeansPostProcessorOrderingLabTest`（再对照 `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `PostProcessorRegistrationDelegate#sortPostProcessors` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 实例化前短路：postProcessBeforeInstantiation

- 文件：`spring-core-modules/spring-core-beans/docs/pre-instantiation-short-circuit.md`
- 深化落点：
    - `SpringCoreBeansPreInstantiationLabTest`（再对照 `SpringCoreBeansPreInstantiationLabTest.withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### early reference 与循环依赖：getEarlyBeanReference

- 文件：`spring-core-modules/spring-core-beans/docs/early-reference-and-three-level-cache.md`
- 深化落点：
    - `SpringCoreBeansEarlyReferenceLabTest`（再对照 `SpringCoreBeansRawInjectionDespiteWrappingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultSingletonBeanRegistry#getSingleton` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“一页式最短证据链（10 分钟）：观察到 factory 层价值 + early 形态决策”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 生命周期回调顺序：Aware / BPP / init / destroy

- 文件：`spring-core-modules/spring-core-beans/docs/internals-lifecycle-callback-order.md`
- 深化落点：
    - `SpringCoreBeansLifecycleCallbackOrderLabTest`（再对照 `SpringCoreBeansLifecycleCallbackOrderLabTest.singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#doCreateBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### refresh → doCreateBean 主线（源码级）

- 文件：`spring-core-modules/spring-core-beans/docs/internals-refresh-to-bean-creation-mainline.md`
- 深化落点：
    - `SpringCoreBeansBootstrapInternalsLabTest`（再对照 `SpringCoreBeansRegistryPostProcessorLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：现象 → 阶段 → 关键方法 → 必看变量 → 对应实验/测试”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。
