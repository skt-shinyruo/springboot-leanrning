# 章节深化路线（IoC Container）

## 定位：IoC Container 章节的深化方式

IoC Container 章节承载本模块的基础模型：注册、注入、生命周期、扩展点、FactoryBean、循环依赖和最终暴露对象。深化时要把这些主题从“概念解释”下压到“算法决策点”：入口方法在哪里、使用哪些元数据做判断、失败时应该如何分型。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（注解驱动与注入）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（Java Config / @Bean）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（Scopes）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html


本部分的再加深重点，是把注册、注入、生命周期和扩展点继续压到算法级决策点，并补齐真实工程边界，例如 FactoryBean、泛型、循环依赖和代理叠加。

## 执行化提示（IoC 核心章的“深度落点”）

- 优先把算法级决策点写进正文：入口方法（在哪里做选择）+ 关键变量（用什么信息做选择）+ 失败分型（为什么会 NoSuch/NoUnique）。
- 反例要可复现：每章至少绑定 1 个 Lab/断点闭环，用断言与观察清单证明边界触发条件。

### Bean 注册入口：扫描、@Bean、@Import、registrar

- 文件：`spring-core-modules/spring-core-beans/docs/ioc-bean-registration.md`
- 深化落点：
    - `SpringCoreBeansComponentScanLabTest`（再对照 `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#registerBeanDefinition` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“5. 排障决策表（注册相关：现象 → 分层 → 证据 → 修复）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 依赖注入解析：类型/名称/@Qualifier/@Primary

- 文件：`spring-core-modules/spring-core-beans/docs/dependency-injection-resolution.md`
- 深化落点：
    - `SpringCoreBeansAutowireCandidateSelectionLabTest`（再对照 `SpringCoreBeansBeanGraphDebugLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“机制主线：候选收集 → 候选收敛 → 最终注入”时，把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency），并在关键分支处点明触发条件与结果形态。

### Scope 与 prototype 注入陷阱

- 文件：`spring-core-modules/spring-core-beans/docs/scope-and-prototype.md`
- 深化落点：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“9. 排障决策表（scope/prototype：从“像单例”到“证据链”）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 生命周期：初始化、销毁与回调

- 文件：`spring-core-modules/spring-core-beans/docs/lifecycle-callbacks.md`
- 深化落点：
    - `SpringCoreBeansLifecycleCallbackOrderLabTest`（再对照 `SpringCoreBeansPrototypeDestroySemanticsLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#doCreateBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障决策表（生命周期/回调：从“没执行”到“证据链”）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）

- 文件：`spring-core-modules/spring-core-beans/docs/ioc-post-processors.md`
- 深化落点：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansPostProcessorOrderingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界（补一段“能落到源码的答案”）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### `@Configuration` 增强与 `@Bean` 语义

- 文件：`spring-core-modules/spring-core-beans/docs/configuration-and-bean-method.md`
- 深化落点：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalse_stillPreservesSingleton_whenUsingMethodParameterInjection`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### Bean 运行机制：从 BeanDefinition 到最终暴露对象

- 文件：`spring-core-modules/spring-core-beans/docs/bean-mental-model.md`
- 深化落点：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansBeanCreationTraceLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“4. 排障决策表（将主观判断转化为可验证结论）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）

- 文件：`spring-core-modules/spring-core-beans/docs/factorybean.md`
- 深化落点：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansFactoryBeanDeepDiveLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#getObjectForBeanInstance` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 循环依赖：现象、原因与规避

- 文件：`spring-core-modules/spring-core-beans/docs/circular-dependency.md`
- 深化落点：
    - `SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`（再对照 `SpringCoreBeansContainerLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `ConstructorResolver#autowireConstructor` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障配方：如何定位“环路边”并选择打断手段”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。
