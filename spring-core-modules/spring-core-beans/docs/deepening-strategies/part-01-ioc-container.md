# 逐章内容级再加深建议（part-01-ioc-container）

## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（注解驱动与注入）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（Java Config / @Bean）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（Scopes）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html


本 Part 的再加深重点：把注册/注入/生命周期/扩展点进一步下压到“算法级决策点”，并补齐真实工程边界（FactoryBean/泛型/循环依赖/代理叠加）。

## 执行化提示（IoC 核心章的“深度落点”）

- 优先把“算法级决策点”写进正文：入口方法（在哪里做选择）+ 关键变量（用什么信息做选择）+ 失败分型（为什么会 NoSuch/NoUnique）。
- 反例要可复现：每章至少绑定 1 个 Lab/断点闭环，用断言与 watch list 证明“边界触发条件”。

### 02. Bean 注册入口：扫描、@Bean、@Import、registrar

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- 继续加深建议：
    - `SpringCoreBeansComponentScanLabTest`（再对照 `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#registerBeanDefinition` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“5. 排障决策表（注册相关：现象 → 分层 → 证据 → 修复）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 03. 依赖注入解析：类型/名称/@Qualifier/@Primary

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- 继续加深建议：
    - `SpringCoreBeansAutowireCandidateSelectionLabTest`（再对照 `SpringCoreBeansBeanGraphDebugLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“机制主线：候选收集 → 候选收敛 → 最终注入”时，建议把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency），并在关键分支处点明触发条件与结果形态。

### 04. Scope 与 prototype 注入陷阱

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- 继续加深建议：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“9. 排障决策表（scope/prototype：从“像单例”到“证据链”）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 05. 生命周期：初始化、销毁与回调

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- 继续加深建议：
    - `SpringCoreBeansLifecycleCallbackOrderLabTest`（再对照 `SpringCoreBeansPrototypeDestroySemanticsLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#doCreateBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障决策表（生命周期/回调：从“没执行”到“证据链”）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- 继续加深建议：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansPostProcessorOrderingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“常见误区与边界（补一段“能落到源码的答案”）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 07. `@Configuration` 增强与 `@Bean` 语义

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- 继续加深建议：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalse_stillPreservesSingleton_whenUsingMethodParameterInjection`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“常见误区与边界”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 01. Bean 运行机制：从 BeanDefinition 到最终暴露对象

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- 继续加深建议：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansBeanCreationTraceLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“4. 排障决策表（将主观判断转化为可验证结论）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- 继续加深建议：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansFactoryBeanDeepDiveLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#getObjectForBeanInstance` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“常见误区与边界”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 09. 循环依赖：现象、原因与规避

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- 继续加深建议：
    - `SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`（再对照 `SpringCoreBeansContainerLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `ConstructorResolver#autowireConstructor` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障配方：如何定位“环路边”并选择打断手段”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
