# 逐章内容级再加深建议（part-04-wiring-and-boundaries）

本 Part 的再加深重点：工程边界与真实误区（Lazy/dependsOn/resolvable dependency/层级/命名/FactoryBean/代理/占位符/转换/泛型匹配等），要求每章都能提供可复现反例与排障 SOP。

## 执行化提示（边界章的“可复现反例”优先）

- 每章至少补 1 个“误诊对照”：现象相似但机制不同（例如 depends-on 环 vs 循环依赖）。
- 每章至少补 1 个“第一断点入口 + watch list”：让读者能在 1 分钟内把问题钉在正确分支。

### 18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md`
- 继续加深建议：
    - `SpringCoreBeansLazyLabTest`（再对照 `SpringCoreBeansLazyLabTest#lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“4. 代理类型边界：接口注入点 vs 类注入点（必须会排障）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 19. dependsOn：强制初始化顺序

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-depends-on.md`
- 继续加深建议：
    - `SpringCoreBeansDependsOnLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#doGetBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“7. 排障决策表（初始化/关闭/异常消息 → 证据链）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 20. registerResolvableDependency：能注入但不是 Bean

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md`
- 继续加深建议：
    - `SpringCoreBeansResolvableDependencyLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#resolvableDependencies` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“6. 排障决策表（能注入/不能 getBean/命中不了 → 证据链）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 21. 父子 ApplicationContext：可见性与覆盖边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-context-hierarchy.md`
- 继续加深建议：
    - `SpringCoreBeansContextHierarchyLabTest`（再对照 `SpringCoreBeansContextHierarchyLabTest.childContext_canSeeParentBeans_butParentCannotSeeChildBeans()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#doGetBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 22. Bean 名称与 alias

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md`
- 继续加深建议：
    - `SpringCoreBeansBeanNameAliasLabTest`（再对照 `SpringCoreBeansBeanNameAliasLabTest.aliasResolvesToSameSingletonInstanceAsCanonicalName()`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#transformedBeanName` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 23. FactoryBean 深潜：product vs factory、类型匹配、缓存语义

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`
- 继续加深建议：
    - `SpringCoreBeansContainerLabTest`（再对照 `SpringCoreBeansFactoryBeanDeepDiveLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#getObjectForBeanInstance` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 24. BeanDefinition 覆盖（overriding）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md`
- 继续加深建议：
    - `SpringCoreBeansBeanDefinitionOverridingLabTest`（再对照 `SpringCoreBeansBeanDefinitionOriginLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“6. 排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 25. 手工添加 BeanPostProcessor：顺序与陷阱

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`
- 继续加深建议：
    - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered`（再对照 `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#addBeanPostProcessor` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“4. 排障分流：顺序问题 vs 时机问题（先分清楚再下手）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 26. SmartInitializingSingleton：单例创建完之后再做事

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md`
- 继续加深建议：
    - `SpringCoreBeansSmartInitializingSingletonLabTest`（再对照 `SpringCoreBeansSmartInitializingSingletonLabTest#afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractApplicationContext#finishBeanFactoryInitialization` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 27. SmartLifecycle：start/stop 时机与 phase 顺序

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`
- 继续加深建议：
    - `SpringCoreBeansSmartLifecycleLabTest`（再对照 `SpringCoreBeansSmartLifecycleLabTest#smartLifecycleDoesNotAutoStart_whenIsAutoStartupIsFalse`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractApplicationContext#finishRefresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 28. 自定义 Scope + scoped proxy

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
- 继续加深建议：
    - `SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly`（再对照 `SpringCoreBeansCustomScopeLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#doGetBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 29. FactoryBean 边界：getObjectType 返回 null

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`
- 继续加深建议：
    - `SpringCoreBeansFactoryBeanEdgeCasesLabTest`（再对照 `SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#getBeanNamesForType` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 30. 注入阶段：field vs constructor（postProcessProperties）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md`
- 继续加深建议：
    - `SpringCoreBeansInjectionPhaseLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#autowireConstructor` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“排障分流：这是定义层问题还是实例层问题？”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 31. 代理产生阶段：BPP 如何换成 Proxy（self-invocation）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`
- 继续加深建议：
    - `SpringCoreBeansBeanCreationTraceLabTest`（再对照 `SpringCoreBeansProxyingPhaseLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“2. proxy 的两种形态与类型边界（必须会排障）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 32. `@Resource` 注入：name-first

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`
- 继续加深建议：
    - `SpringCoreBeansResourceInjectionLabTest#withoutAnnotationConfigProcessors_resourceIsIgnored`（再对照 `SpringCoreBeansResourceInjectionLabTest#registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#populateBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“5. 排障分流：三类问题，三条路”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 33. 候选选择 vs 顺序：@Primary/@Priority/@Order/@Qualifier

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`
- 继续加深建议：
    - `SpringCoreBeansAutowireCandidateSelectionLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#doResolveDependency` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“4. 排障决策表（候选选择/排序：从异常到证据链）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 34. `@Value("${...}")` 占位符解析：strict vs non-strict

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
- 继续加深建议：
    - `SpringCoreBeansValuePlaceholderResolutionLabTest`（再对照 `SpringCoreBeansValuePlaceholderResolutionLabTest#defaultEmbeddedValueResolver_resolvesExistingProperty_butLeavesMissingPlaceholderUnresolved`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#resolveEmbeddedValue` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“6. 排障分流：先确定问题停留针对“解析/求值/转换”的哪一步”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 35. MergedBeanDefinition：RootBeanDefinition 从哪里来？

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`
- 继续加深建议：
    - `SpringCoreBeansMergedBeanDefinitionLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractBeanFactory#getMergedLocalBeanDefinition` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“常见误区与边界”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 36. 类型转换：BeanWrapper / ConversionService / PropertyEditor

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`
- 继续加深建议：
    - `SpringCoreBeansTypeConversionLabTest`（再对照 `SpringCoreBeansBeansSupportUtilitiesLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractAutowireCapableBeanFactory#populateBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“机制主线：两条链路 + 一个决策点”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 37. 泛型匹配陷阱：ResolvableType 与代理导致类型信息丢失

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`
- 继续加深建议：
    - `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`（再对照 `SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canFailWhenTypeInfoIsLost`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `BeanDefinition#getResolvableType` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“0. 先建立一个“排障口径”：候选类型信息的三大来源”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 38. Environment/PropertySource：优先级与排障主线

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md`
- 继续加深建议：
    - `SpringCoreBeansEnvironmentPropertySourceLabTest`（再对照 `SpringCoreBeansProfileRegistrationLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `PropertySourcesPropertyResolver#getProperty` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“5. 使用方式：最小可用手段（按“排障优先级”排序）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 39. BeanFactory API 深入分析：接口族谱与手动 bootstrap 边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md`
- 继续加深建议：
    - `SpringCoreBeansBeanFactoryApiLabTest`（再对照 `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“常见误区与边界”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
