# 章节深化路线（Wiring & Boundaries）

## 定位：Wiring & Boundaries 章节的深化方式

Wiring & Boundaries 章节处理的是工程里最容易误判的边界：Lazy、dependsOn、ResolvableDependency、父子容器、命名、FactoryBean、代理、占位符、转换和泛型匹配。深化时要优先把“看起来相似”的现象拆开，让读者能用第一断点入口快速定位到正确分支。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（注解驱动与注入）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（SpEL）：https://docs.spring.io/spring-framework/reference/core/expressions.html


本部分的再加深重点，是把工程边界和真实误区写成可复现反例与排障 SOP。读者不只需要知道结论，还需要知道相似现象之间的第一分流点。

## 执行化提示（边界章的“可复现反例”优先）

- 每章至少补 1 个误诊对照：现象相似但机制不同，例如 depends-on 环和普通循环依赖。
- 每章至少补 1 个第一断点入口 + 观察清单，让读者能在 1 分钟内把问题钉在正确分支。

### Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- 文件：`spring-core-modules/spring-core-beans/docs/lazy-semantics.md`
- 深化落点：
    - `SpringCoreBeansLazyLabTest`（再对照 `SpringCoreBeansLazyLabTest#lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“4. 代理类型边界：接口注入点 vs 类注入点（必须会排障）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### dependsOn：强制初始化顺序

- 文件：`spring-core-modules/spring-core-beans/docs/depends-on.md`
- 深化落点：
    - `SpringCoreBeansDependsOnLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#doGetBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“7. 排障决策表（初始化/关闭/异常消息 → 证据链）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### registerResolvableDependency：能注入但不是 Bean

- 文件：`spring-core-modules/spring-core-beans/docs/resolvable-dependency.md`
- 深化落点：
    - `SpringCoreBeansResolvableDependencyLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#resolvableDependencies` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“6. 排障决策表（能注入/不能 getBean/命中不了 → 证据链）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 父子 ApplicationContext：可见性与覆盖边界

- 文件：`spring-core-modules/spring-core-beans/docs/context-hierarchy.md`
- 深化落点：
    - `SpringCoreBeansContextHierarchyLabTest`（再对照 `SpringCoreBeansContextHierarchyLabTest.childContext_canSeeParentBeans_butParentCannotSeeChildBeans()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#doGetBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### Bean 名称与 alias

- 文件：`spring-core-modules/spring-core-beans/docs/wiring-bean-names-and-aliases.md`
- 深化落点：
    - `SpringCoreBeansBeanNameAliasLabTest`（再对照 `SpringCoreBeansBeanNameAliasLabTest.aliasResolvesToSameSingletonInstanceAsCanonicalName()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#transformedBeanName` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### FactoryBean 深潜：product vs factory、类型匹配、缓存语义

- 文件：`spring-core-modules/spring-core-beans/docs/wiring-factorybean-deep-dive.md`
- 深化落点：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansFactoryBeanDeepDiveLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#getObjectForBeanInstance` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### BeanDefinition 覆盖（overriding）

- 文件：`spring-core-modules/spring-core-beans/docs/bean-definition-overriding.md`
- 深化落点：
    - `SpringCoreBeansBeanDefinitionOverridingLabTest`（再对照 `SpringCoreBeansBeanDefinitionOriginLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“6. 排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 手工添加 BeanPostProcessor：顺序与陷阱

- 文件：`spring-core-modules/spring-core-beans/docs/programmatic-bpp-registration.md`
- 深化落点：
    - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered`（再对照 `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#addBeanPostProcessor` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“4. 排障分流：顺序问题 vs 时机问题（先分清楚再下手）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### SmartInitializingSingleton：单例创建完之后再做事

- 文件：`spring-core-modules/spring-core-beans/docs/smart-initializing-singleton.md`
- 深化落点：
    - `SpringCoreBeansSmartInitializingSingletonLabTest`（再对照 `SpringCoreBeansSmartInitializingSingletonLabTest#afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractApplicationContext#finishBeanFactoryInitialization` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### SmartLifecycle：start/stop 时机与 phase 顺序

- 文件：`spring-core-modules/spring-core-beans/docs/smart-lifecycle.md`
- 深化落点：
    - `SpringCoreBeansSmartLifecycleLabTest`（再对照 `SpringCoreBeansSmartLifecycleLabTest#smartLifecycleDoesNotAutoStart_whenIsAutoStartupIsFalse`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractApplicationContext#finishRefresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 自定义 Scope + scoped proxy

- 文件：`spring-core-modules/spring-core-beans/docs/custom-scope-and-scoped-proxy.md`
- 深化落点：
    - `SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly`（再对照 `SpringCoreBeansCustomScopeLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#doGetBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### FactoryBean 边界：getObjectType 返回 null

- 文件：`spring-core-modules/spring-core-beans/docs/wiring-factorybean-edge-cases.md`
- 深化落点：
    - `SpringCoreBeansFactoryBeanEdgeCasesLabTest`（再对照 `SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#getBeanNamesForType` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 注入阶段：field vs constructor（postProcessProperties）

- 文件：`spring-core-modules/spring-core-beans/docs/injection-phase.md`
- 深化落点：
    - `SpringCoreBeansInjectionPhaseLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#autowireConstructor` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“排障分流：这是定义层问题还是实例层问题？”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 代理产生阶段：BPP 如何换成 Proxy（self-invocation）

- 文件：`spring-core-modules/spring-core-beans/docs/proxying-phase.md`
- 深化落点：
    - `SpringCoreBeansBeanCreationTraceLabTest`（再对照 `SpringCoreBeansProxyingPhaseLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“2. proxy 的两种形态与类型边界（必须会排障）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### `@Resource` 注入：name-first

- 文件：`spring-core-modules/spring-core-beans/docs/resource-vs-autowired.md`
- 深化落点：
    - `SpringCoreBeansResourceInjectionLabTest#withoutAnnotationConfigProcessors_resourceIsIgnored`（再对照 `SpringCoreBeansResourceInjectionLabTest#registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#populateBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“5. 排障分流：三类问题，三条路”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 候选选择 vs 顺序：@Primary/@Priority/@Order/@Qualifier

- 文件：`spring-core-modules/spring-core-beans/docs/wiring-autowire-candidate-selection-primary-priority-order.md`
- 深化落点：
    - `SpringCoreBeansAutowireCandidateSelectionLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#doResolveDependency` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“4. 排障决策表（候选选择/排序：从异常到证据链）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### `@Value("${...}")` 占位符解析：strict vs non-strict

- 文件：`spring-core-modules/spring-core-beans/docs/value-placeholder-resolution.md`
- 深化落点：
    - `SpringCoreBeansValuePlaceholderResolutionLabTest`（再对照 `SpringCoreBeansValuePlaceholderResolutionLabTest#defaultEmbeddedValueResolver_resolvesExistingProperty_butLeavesMissingPlaceholderUnresolved`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#resolveEmbeddedValue` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“6. 排障分流：先确定问题停留针对“解析/求值/转换”的哪一步”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### MergedBeanDefinition：RootBeanDefinition 从哪里来？

- 文件：`spring-core-modules/spring-core-beans/docs/merged-bean-definition.md`
- 深化落点：
    - `SpringCoreBeansMergedBeanDefinitionLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#getMergedLocalBeanDefinition` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 类型转换：BeanWrapper / ConversionService / PropertyEditor

- 文件：`spring-core-modules/spring-core-beans/docs/type-conversion-and-beanwrapper.md`
- 深化落点：
    - `SpringCoreBeansTypeConversionLabTest`（再对照 `SpringCoreBeansBeansSupportUtilitiesLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#populateBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“机制主线：两条链路 + 一个决策点”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 泛型匹配陷阱：ResolvableType 与代理导致类型信息丢失

- 文件：`spring-core-modules/spring-core-beans/docs/generic-type-matching.md`
- 深化落点：
    - `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`（再对照 `SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canFailWhenTypeInfoIsLost`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `BeanDefinition#getResolvableType` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“0. 先建立一个“排障口径”：候选类型信息的三大来源”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### Environment/PropertySource：优先级与排障主线

- 文件：`spring-core-modules/spring-core-beans/docs/environment-and-propertysource.md`
- 深化落点：
    - `SpringCoreBeansEnvironmentPropertySourceLabTest`（再对照 `SpringCoreBeansProfileRegistrationLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `PropertySourcesPropertyResolver#getProperty` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“5. 使用方式：最小可用手段（按“排障优先级”排序）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### BeanFactory API 深入分析：接口族谱与手动 bootstrap 边界

- 文件：`spring-core-modules/spring-core-beans/docs/beanfactory-api-and-autowirecapablebeanfactory.md`
- 深化落点：
    - `SpringCoreBeansBeanFactoryApiLabTest`（再对照 `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。
