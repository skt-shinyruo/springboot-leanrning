# 知识地图（Knowledge Map）：主文档归属表
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这页只做归属与跳转，不重复解释机制。
    - 先按症状找到主文档，再用最短证据入口验证。
    - 维护支持文档时，优先检查这张表是否仍然一对一。

    观察对象：知识地图（主文档归属表）。
    主线位置：定义层、创建层、依赖解析、生命周期、Boot 与 AOT 叠加层。
    对照入口：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansModuleContractLabTest`。
<!-- CHAPTER-CARD:END -->

## 读法

这页是归属表，不是教程。读者在排障时先找到症状对应的主文档，再去主文档读边界和最短证据入口。

## 主文档归属

| 层级 | 症状或问题 | 主文档 | 最短证据入口 |
| --- | --- | --- | --- |
| 容器与注册 | Bean、BeanDefinition、单例缓存、最终暴露对象分别是什么关系？ | [bean-mental-model.md](bean-mental-model.md) | `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` |
| 容器与注册 | BeanFactory 与 ApplicationContext 的能力差异是什么？ | [beanfactory-vs-applicationcontext.md](beanfactory-vs-applicationcontext.md) | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` |
| 容器与注册 | 一个 BeanDefinition 是如何被注册进容器的？ | [bean-definition-registration.md](bean-definition-registration.md) | `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` / `SpringCoreBeansComponentScanLabTest` |
| 容器与注册 | BeanDefinition 的 primary/autowireCandidate/source/factoryMethod 等元数据如何支撑候选选择和来源排查？ | [bean-definition-metadata-and-origin.md](bean-definition-metadata-and-origin.md) | `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` / `SpringCoreBeansBeanDefinitionOriginLabTest` |
| 容器与注册 | beanName 和 alias 如何影响定位、注入和排障？ | [bean-name-and-alias.md](bean-name-and-alias.md) | `SpringCoreBeansBeanNameAliasLabTest` |
| 容器与注册 | 同名 BeanDefinition 冲突时，谁生效、谁失败、什么时候失败？ | [bean-definition-overriding.md](bean-definition-overriding.md) | `SpringCoreBeansBeanDefinitionOverridingLabTest` |
| 容器与注册 | MergedBeanDefinition / RootBeanDefinition 在什么阶段形成，解决什么问题？ | [merged-bean-definition.md](merged-bean-definition.md) | `SpringCoreBeansMergedBeanDefinitionLabTest` |
| 容器与注册 | `@Configuration`、`@Bean`、`proxyBeanMethods` 各自改变了什么？ | [configuration-and-bean-method.md](configuration-and-bean-method.md) | `SpringCoreBeansContainerLabTest` |
| 容器与注册 | `@Import`、ImportSelector、ImportBeanDefinitionRegistrar 的边界在哪里？ | [import-selector-and-registrar.md](import-selector-and-registrar.md) | `SpringCoreBeansImportLabTest` / `SpringCoreBeansImportExerciseTest` / `SpringCoreBeansImportExerciseSolutionTest` |
| 容器与注册 | `registerBeanDefinition`、`registerBean`、`registerSingleton` 的根本差异是什么？ | [programmatic-registration.md](programmatic-registration.md) | `SpringCoreBeansProgrammaticRegistrationLabTest` |
| 容器与注册 | `refresh()` 这条主线到底先做什么、后做什么？ | [refresh-mainline.md](refresh-mainline.md) | `SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` |
| 容器与注册 | 为什么注解处理器、自动装配和基础设施能够在容器里生效？ | [container-bootstrap-and-infrastructure.md](container-bootstrap-and-infrastructure.md) | `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInfrastructureBeanRoleLabTest` |
| 容器与注册 | BFPP / BDRPP / BPP 的职责边界是什么，分别属于定义阶段还是实例阶段？ | [post-processors-overview.md](post-processors-overview.md) | `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` |
| 容器与注册 | BFPP 在什么时候修改已有 BeanDefinition，不能做什么？ | [beanfactory-post-processors.md](beanfactory-post-processors.md) | `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` |
| 容器与注册 | BDRPP 为什么能在普通 BFPP 之前新增或改写 BeanDefinition？ | [bdrpp-definition-registration.md](bdrpp-definition-registration.md) | `SpringCoreBeansRegistryPostProcessorLabTest` |
| 容器与注册 | BPP 如何介入实例创建，什么时候会把 bean 换成 proxy？ | [beanpost-processors.md](beanpost-processors.md) | `SpringCoreBeansLifecycleRawVsProxyLabTest` / `SpringCoreBeansProxyingPhaseLabTest` |
| 容器与注册 | PriorityOrdered、Ordered、无序处理器的排序规则如何影响行为？ | [post-processor-ordering.md](post-processor-ordering.md) | `SpringCoreBeansPostProcessorOrderingLabTest` |
| 容器与注册 | 手工添加 BeanPostProcessor 为什么会绕过容器排序？ | [programmatic-bpp-registration.md](programmatic-bpp-registration.md) | `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| 容器与注册 | `postProcessBeforeInstantiation` 为什么能让构造器根本不执行？ | [pre-instantiation-short-circuit.md](pre-instantiation-short-circuit.md) | `SpringCoreBeansPreInstantiationLabTest` |
| 容器与注册 | `doGetBean()` / `doCreateBean()` 的主线是什么？ | [bean-creation-mainline.md](bean-creation-mainline.md) | `SpringCoreBeansBeanCreationTraceLabTest` |
| 依赖解析与注入 | 注入点到底向容器提出了什么需求？ | [dependency-injection-resolution.md](dependency-injection-resolution.md) | `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` |
| 依赖解析与注入 | DependencyDescriptor / InjectionPoint 里有哪些元数据可用于排障？ | [dependency-descriptor-and-injection-point.md](dependency-descriptor-and-injection-point.md) | `SpringCoreBeansDependencyDescriptorMetadataLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest` |
| 依赖解析与注入 | 候选 bean 是如何被收集、筛选、收敛的？ | [autowire-candidate-selection.md](autowire-candidate-selection.md) | `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansAutowireCandidateSelectionExerciseTest` / `SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest` |
| 依赖解析与注入 | `@Qualifier`、`@Primary`、`@Priority`、`@Order` 各自管哪一步？ | [qualifier-primary-priority-order.md](qualifier-primary-priority-order.md) | `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` |
| 依赖解析与注入 | `@Resource` 的 name-first 与 `@Autowired` 的 by-type 有何本质差异？ | [resource-vs-autowired.md](resource-vs-autowired.md) | `SpringCoreBeansResourceInjectionLabTest` / `SpringCoreBeansResourceResolutionLabTest` |
| 依赖解析与注入 | Optional、`required=false`、`ObjectProvider`、`Provider` 怎么表达可选与延迟？ | [optional-and-provider-injection.md](optional-and-provider-injection.md) | `SpringCoreBeansOptionalInjectionLabTest` / `SpringCoreBeansJsr330InjectionLabTest` |
| 依赖解析与注入 | 为什么有些对象能注入，但它们不是 Bean？ | [resolvable-dependency.md](resolvable-dependency.md) | `SpringCoreBeansResolvableDependencyLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest` |
| 依赖解析与注入 | 泛型信息如何参与注入匹配，代理为什么会让它失真？ | [generic-type-matching.md](generic-type-matching.md) | `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` |
| 依赖解析与注入 | field injection 与 constructor injection 处在什么阶段，观察点有什么不同？ | [injection-phase.md](injection-phase.md) | `SpringCoreBeansInjectionPhaseLabTest` / `SpringCoreBeansInjectionPhaseMatrixLabTest` |
| 生命周期、Scope 与代理边界 | singleton、prototype、其他 scope 的行为边界是什么？ | [scope-and-prototype.md](scope-and-prototype.md) | `SpringCoreBeansLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest` |
| 生命周期、Scope 与代理边界 | 自定义 Scope 与 scoped proxy 如何改变注入对象和目标对象的关系？ | [custom-scope-and-scoped-proxy.md](custom-scope-and-scoped-proxy.md) | `SpringCoreBeansCustomScopeLabTest` |
| 生命周期、Scope 与代理边界 | lazy-init 与注入点 `@Lazy` 分别延迟了什么？ | [lazy-semantics.md](lazy-semantics.md) | `SpringCoreBeansLazyLabTest` |
| 生命周期、Scope 与代理边界 | `dependsOn` 如何强制初始化顺序，为什么它不是依赖注入规则？ | [depends-on.md](depends-on.md) | `SpringCoreBeansDependsOnLabTest` |
| 生命周期、Scope 与代理边界 | Aware、init、destroy、`@PostConstruct` 的顺序如何理解？ | [lifecycle-callbacks.md](lifecycle-callbacks.md) | `SpringCoreBeansLifecycleCallbackOrderLabTest` |
| 生命周期、Scope 与代理边界 | `SmartInitializingSingleton` 为什么要等所有单例都创建完？ | [smart-initializing-singleton.md](smart-initializing-singleton.md) | `SpringCoreBeansSmartInitializingSingletonLabTest` |
| 生命周期、Scope 与代理边界 | `SmartLifecycle` 的 start/stop 与 phase 顺序如何工作？ | [smart-lifecycle.md](smart-lifecycle.md) | `SpringCoreBeansSmartLifecycleLabTest` |
| 生命周期、Scope 与代理边界 | 循环依赖究竟解决了什么，解决不了什么？ | [circular-dependency.md](circular-dependency.md) | `SpringCoreBeansCircularDependencyBoundaryLabTest` |
| 生命周期、Scope 与代理边界 | early reference 与三级缓存如何协作？ | [early-reference-and-three-level-cache.md](early-reference-and-three-level-cache.md) | `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| 生命周期、Scope 与代理边界 | BPP 在哪个窗口把 bean 包装成 proxy，自调用为什么绕过它？ | [proxying-phase.md](proxying-phase.md) | `SpringCoreBeansProxyingPhaseLabTest` |
| 生命周期、Scope 与代理边界 | FactoryBean 的产品对象和工厂对象如何区分？ | [factorybean.md](factorybean.md) | `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` |
| 生命周期、Scope 与代理边界 | FactoryBean 的类型匹配边界在哪里，`getObjectType()` 为什么关键？ | [factorybean-type-matching.md](factorybean-type-matching.md) | `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest` |
| 生命周期、Scope 与代理边界 | 父子 ApplicationContext 的可见性和覆盖边界是什么？ | [context-hierarchy.md](context-hierarchy.md) | `SpringCoreBeansContextHierarchyLabTest` |
| 生命周期、Scope 与代理边界 | BeanFactory API 与 AutowireCapableBeanFactory 的边界是什么？ | [beanfactory-api-and-autowirecapablebeanfactory.md](beanfactory-api-and-autowirecapablebeanfactory.md) | `SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest` |
| 值解析、转换与外部输入 | Environment / PropertySource 如何决定值从哪里来？ | [environment-and-propertysource.md](environment-and-propertysource.md) | `SpringCoreBeansEnvironmentPropertySourceLabTest` |
| 值解析、转换与外部输入 | `${...}` 占位符何时 strict，何时 non-strict？ | [value-placeholder-resolution.md](value-placeholder-resolution.md) | `SpringCoreBeansValuePlaceholderResolutionLabTest` |
| 值解析、转换与外部输入 | `#{...}` SpEL 与 `${...}` 占位符的解析顺序是什么？ | [spel-and-value-expression.md](spel-and-value-expression.md) | `SpringCoreBeansSpelValueLabTest` |
| 值解析、转换与外部输入 | BeanWrapper、ConversionService、PropertyEditor 各负责哪一段？ | [type-conversion-and-beanwrapper.md](type-conversion-and-beanwrapper.md) | `SpringCoreBeansTypeConversionLabTest` / `SpringCoreBeansPropertyEditorLabTest` / `SpringCoreBeansPropertyEditorResolutionLabTest` |
| 值解析、转换与外部输入 | XML 如何变成 BeanDefinition？ | [xml-bean-definition-reader.md](xml-bean-definition-reader.md) | `SpringCoreBeansXmlBeanDefinitionReaderLabTest` |
| 值解析、转换与外部输入 | Properties / Groovy 这类输入如何变成 BeanDefinition？ | [properties-and-groovy-reader.md](properties-and-groovy-reader.md) | `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` |
| 值解析、转换与外部输入 | XML namespace 扩展如何把自定义标签变成定义？ | [xml-namespace-extension.md](xml-namespace-extension.md) | `SpringCoreBeansXmlNamespaceExtensionLabTest` |
| 值解析、转换与外部输入 | lookup-method / replaced-method 解决的是什么动态取对象问题？ | [method-injection.md](method-injection.md) | `SpringCoreBeansReplacedMethodLabTest` |
| 值解析、转换与外部输入 | Spring 内置 FactoryBean 的常见形态有哪些？ | [built-in-factorybeans.md](built-in-factorybeans.md) | `SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` |
| Boot 叠加后的变化 | Auto-configuration 的顺序为什么会影响条件命中？ | [boot-auto-configuration-ordering.md](boot-auto-configuration-ordering.md) | `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` |
| Boot 叠加后的变化 | Boot 自动装配如何决定一个 Bean 出现还是退回 backoff？ | [boot-auto-configuration-beans.md](boot-auto-configuration-beans.md) | `SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` |
| AOT / Native | RuntimeHints 为什么是构建期契约？ | [aot-runtimehints.md](aot-runtimehints.md) | `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansRuntimeHintsBoundaryLabTest` |
| AOT / Native | AOT 语境下 XML BeanDefinitionReader 的边界是什么？ | [aot-xml-bean-definition-reader.md](aot-xml-bean-definition-reader.md) | `SpringCoreBeansXmlBeanDefinitionReaderLabTest` |
| AOT / Native | 容器外对象注入在 AOT 下怎样成立？ | [aot-autowirecapablebeanfactory-external-objects.md](aot-autowirecapablebeanfactory-external-objects.md) | `SpringCoreBeansAutowireCapableBeanFactoryLabTest` |
| AOT / Native | SpEL / Value 在 AOT 下会遇到什么约束？ | [aot-spel-and-value-expression.md](aot-spel-and-value-expression.md) | `SpringCoreBeansSpelValueLabTest` |
| AOT / Native | 自定义 Qualifier 在 AOT 下要补什么契约？ | [aot-custom-qualifier.md](aot-custom-qualifier.md) | `SpringCoreBeansCustomQualifierLabTest` |
| AOT / Native | XML namespace 扩展在 AOT 下为什么需要额外约束？ | [aot-xml-namespace-extension.md](aot-xml-namespace-extension.md) | `SpringCoreBeansXmlNamespaceExtensionLabTest` |
| AOT / Native | Properties / Groovy 等输入在 AOT 下有哪些边界？ | [aot-beandefinitionreader-other-inputs.md](aot-beandefinitionreader-other-inputs.md) | `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` |
| AOT / Native | 方法注入在 AOT 下为什么需要单独验证？ | [aot-method-injection.md](aot-method-injection.md) | `SpringCoreBeansReplacedMethodLabTest` |
| AOT / Native | 内置 FactoryBean 在 AOT 下有哪些特殊要求？ | [aot-built-in-factorybeans.md](aot-built-in-factorybeans.md) | `SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` |
| AOT / Native | PropertyEditor / 值解析在 AOT 下如何落地？ | [aot-property-editor-and-value-resolution.md](aot-property-editor-and-value-resolution.md) | `SpringCoreBeansPropertyEditorResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest` / `SpringCoreBeansTypeConversionLabTest` |
| AOT / Native | 为什么 JVM 可运行不等于 Native 可运行？ | [aot-native-overview.md](aot-native-overview.md) | `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansRuntimeHintsBoundaryLabTest` / `SpringCoreBeansAotFactoriesLabTest` |
## 支持文档

| 支持文档 | 职责 | 禁止承担的内容 |
| --- | --- | --- |
| [guide-applicationcontext-refresh-call-chain.md](guide-applicationcontext-refresh-call-chain.md) | 把 refresh 相关问题路由到 `refresh-mainline.md`、`container-bootstrap-and-infrastructure.md` 和 `bean-creation-mainline.md`。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [guide-branch-decision-matrix.md](guide-branch-decision-matrix.md) | 把异常或现象分派到唯一主文档和最短 Lab。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [guide-breakpoint-map.md](guide-breakpoint-map.md) | 列出调试入口、关键方法和对应 Lab，不展开主文档正文。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [guide-deep-dive-guide.md](guide-deep-dive-guide.md) | 给进阶阅读顺序和回跳路径。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [guide-mainline-timeline.md](guide-mainline-timeline.md) | 把 refresh、创建、注入、初始化、代理窗口放在一条路线图里。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [guide-quickstart-30min.md](guide-quickstart-30min.md) | 给第一次运行、第一张图和第一批跳转。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [guide-why-index.md](guide-why-index.md) | 按“为什么”问题把读者送到主文档。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-common-pitfalls.md](appendix-common-pitfalls.md) | 把误区映射到 owner 文档，不解释 owner 的正文。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-debugger-pack.md](appendix-debugger-pack.md) | 聚合断点组、入口方法和 Lab 命令。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-explore-debug-tests.md](appendix-explore-debug-tests.md) | 索引 explore/debug 测试和它们观察的对象。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-glossary.md](appendix-glossary.md) | 给术语的一句话定义和 owner 链接。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-interview-playbook.md](appendix-interview-playbook.md) | 把主文档结论整理为可复述结构。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-production-troubleshooting-checklist.md](appendix-production-troubleshooting-checklist.md) | 给排障检查顺序、证据入口和回跳主文档。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-self-check.md](appendix-self-check.md) | 用问题检查读者是否能定位 owner、Lab 和断点。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-spring-beans-public-api-gap.md](appendix-spring-beans-public-api-gap.md) | 列出还需要公共 API 证据支撑的缺口。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-spring-beans-public-api-index.md](appendix-spring-beans-public-api-index.md) | 索引公共 API、相关 owner 和可运行入口。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [appendix-team-training-kit.md](appendix-team-training-kit.md) | 把 owner 文档、Lab 和自检题编排成课时。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [boot-debugging-and-observability.md](boot-debugging-and-observability.md) | 聚合 ConditionEvaluationReport、Actuator、日志和断点入口。 | 不重复主文档机制，只保留导航、索引、证据入口或维护检查。 |
| [deepening-aot-and-real-world.md](deepening-aot-and-real-world.md) | AOT owner 页与真实项目约束如何保持一致。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-appendix.md](deepening-appendix.md) | 附录如何保持索引、速查和自检职责。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-boot-autoconfig.md](deepening-boot-autoconfig.md) | Boot owner 页与 Boot 支持页的分工。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-container-internals.md](deepening-container-internals.md) | refresh、创建、后处理器和循环依赖文档的归属维护。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-docs-root.md](deepening-docs-root.md) | docs/ 扁平目录、README 顺序来源和链接检查的维护规则。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-guide.md](deepening-guide.md) | Guide 页面如何只做路线、断点和 Lab 编排。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-ioc-container.md](deepening-ioc-container.md) | 容器与注册 owner 页的边界维护。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-module-readme.md](deepening-module-readme.md) | README 如何保持入口和目录职责。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-module-rewrite-rationale.md](deepening-module-rewrite-rationale.md) | 为什么按知识点 owner 重写，以及怎样验收这套边界。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-strategies.md](deepening-strategies.md) | 长期维护 owner、support 和 Lab 证据链的策略。 | 不写成教程正文，不复述主文档机制。 |
| [deepening-wiring-and-boundaries.md](deepening-wiring-and-boundaries.md) | 依赖解析、注入、Scope、FactoryBean 和代理边界如何防止重复。 | 不写成教程正文，不复述主文档机制。 |
## 维护检查

- 主文档新增、删除或改名时，先更新本表，再更新 README 目录。
- 支持文档只能保存路线、索引、断点、Lab 和维护规则。
- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test` 是最短链接与引用契约。
