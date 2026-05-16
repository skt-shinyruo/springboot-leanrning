# 知识地图：Spring Bean 文档归属

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这页用于定位 Spring Bean 问题应该看哪篇 owner 文档。
    - 它不解释完整机制，只给问题、主文档和最短验证入口。
    - 新增或改名文档时，先同步这张归属表，再同步 README。

    观察对象：Spring Bean 文档归属、Lab 证据和排障入口。
    主线位置：定义、容器、创建、注入、生命周期、扩展、暴露、Boot、AOT。
    对照入口：`SpringCoreBeansModuleContractLabTest`。
<!-- CHAPTER-CARD:END -->

这张表只负责归属定位：遇到问题时先找到唯一 owner 文档，再用对应 Lab 观察现象。除本页外，后续文档尚未创建时统一使用文件名代码格式，不提前建立 Markdown 链接。

| Owner 文档 | 问题 / 用途 | Lab 证据 |
| --- | --- | --- |
| [appendix-knowledge-map.md](appendix-knowledge-map.md) | 文档归属、README 目录和 Lab 引用是否保持同步 | `SpringCoreBeansModuleContractLabTest` |
| [bean-mental-model.md](bean-mental-model.md) | Bean 到底是 class、definition、原始实例、代理还是 FactoryBean 产品 | `SpringCoreBeansContainerLabTest`、`SpringCoreBeansBeanGraphDebugLabTest`、`SpringCoreBeansLifecycleRawVsProxyLabTest`、`SpringCoreBeansFactoryBeanDeepDiveLabTest` |
| [bean-definition-registration.md](bean-definition-registration.md) | BeanDefinition 从 XML、扫描、`@Bean`、`@Import`、编程式注册或自动配置进入容器 | `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`、`SpringCoreBeansComponentScanLabTest`、`SpringCoreBeansImportLabTest`、`SpringCoreBeansProgrammaticRegistrationLabTest`、`SpringCoreBeansAutoConfigurationLabTest` |
| [bean-definition-metadata-and-origin.md](bean-definition-metadata-and-origin.md) | scope、lazy、primary、候选标记、来源和 depends-on 如何成为容器决策输入 | `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`、`SpringCoreBeansBeanDefinitionOriginLabTest`、`SpringCoreBeansDependsOnLabTest` |
| [beanfactory-vs-applicationcontext.md](beanfactory-vs-applicationcontext.md) | BeanFactory 与 ApplicationContext 在 refresh、基础设施和预实例化上的职责边界 | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`、`SpringCoreBeansContainerLabTest` |
| [refresh-mainline.md](refresh-mainline.md) | `ApplicationContext#refresh()` 每个阶段改变了什么容器状态 | `SpringCoreBeansMainlineCallChainLabTest`、`SpringCoreBeansBootstrapInternalsLabTest`、`SpringCoreBeansContainerLabTest` |
| [container-bootstrap-and-infrastructure.md](container-bootstrap-and-infrastructure.md) | 注解处理、注入、事件、转换和 AOP 为什么依赖基础设施 Bean | `SpringCoreBeansBootstrapInternalsLabTest`、`SpringCoreBeansInfrastructureBeanRoleLabTest`、`SpringCoreBeansAwareInfrastructureLabTest` |
| [bean-creation-mainline.md](bean-creation-mainline.md) | 单个 Bean 从 `getBean` 到最终暴露对象的创建主线 | `SpringCoreBeansBeanCreationTraceLabTest`、`SpringCoreBeansMainlineCallChainLabTest` |
| [pre-instantiation-short-circuit.md](pre-instantiation-short-circuit.md) | 构造器执行前为何可能被 `postProcessBeforeInstantiation` 短路 | `SpringCoreBeansPreInstantiationLabTest` |
| [dependency-injection-resolution.md](dependency-injection-resolution.md) | 注入点如何形成需求、收集候选、过滤并收敛到结果或失败 | `SpringCoreBeansInjectionAmbiguityLabTest`、`SpringCoreBeansAutowireCandidateSelectionLabTest`、`SpringCoreBeansInjectionPhaseLabTest` |
| [dependency-descriptor-and-injection-point.md](dependency-descriptor-and-injection-point.md) | 注入点元数据里保存了什么，以及如何观察 descriptor | `SpringCoreBeansDependencyDescriptorMetadataLabTest`、`SpringCoreBeansProgrammaticResolveDependencyLabTest`、`DependencyDescriptorDumperLabTest` |
| [autowire-candidate-selection.md](autowire-candidate-selection.md) | type、qualifier、primary、priority、fallback、name 和集合排序如何组合 | `SpringCoreBeansAutowireCandidateSelectionLabTest`、`SpringCoreBeansAutowireCandidateSelectionExerciseTest`、`SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest`、`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` |
| [optional-and-provider-injection.md](optional-and-provider-injection.md) | 可选依赖、延迟获取和 Provider 系 API 的失败时机与创建时机 | `SpringCoreBeansOptionalInjectionLabTest`、`SpringCoreBeansJsr330InjectionLabTest`、`SpringCoreBeansLazyLabTest` |
| [resource-vs-autowired.md](resource-vs-autowired.md) | `@Resource` 的 name-first 与 `@Autowired` 的 type-first 如何不同 | `SpringCoreBeansResourceInjectionLabTest`、`SpringCoreBeansResourceResolutionLabTest` |
| [scope-and-prototype.md](scope-and-prototype.md) | singleton、prototype 的复用、创建和销毁边界 | `SpringCoreBeansLabTest`、`SpringCoreBeansPrototypeDestroySemanticsLabTest` |
| [custom-scope-and-scoped-proxy.md](custom-scope-and-scoped-proxy.md) | 自定义 scope 和 scoped proxy 如何改变对象获取边界 | `SpringCoreBeansCustomScopeLabTest` |
| [lazy-semantics.md](lazy-semantics.md) | `@Lazy` 影响注册、预实例化、依赖注入还是代理获取 | `SpringCoreBeansLazyLabTest` |
| [lifecycle-callbacks.md](lifecycle-callbacks.md) | Aware、init、destroy、BPP 前后置和代理包装的回调顺序 | `SpringCoreBeansLifecycleCallbackOrderLabTest`、`SpringCoreBeansLifecycleRawVsProxyLabTest` |
| [smart-initializing-singleton.md](smart-initializing-singleton.md) | 所有非懒 singleton 创建完之后的统一回调入口 | `SpringCoreBeansSmartInitializingSingletonLabTest` |
| [smart-lifecycle.md](smart-lifecycle.md) | 容器启动、停止阶段与 `SmartLifecycle` phase 顺序 | `SpringCoreBeansSmartLifecycleLabTest` |
| [early-reference-and-three-level-cache.md](early-reference-and-three-level-cache.md) | 循环依赖、early reference 和三级缓存的成功/失败边界 | `SpringCoreBeansCircularDependencyBoundaryLabTest`、`SpringCoreBeansEarlyReferenceLabTest`、`SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| [proxying-phase.md](proxying-phase.md) | AOP 或 BPP 代理在创建链路哪个阶段出现，调用方拿到什么对象 | `SpringCoreBeansProxyingPhaseLabTest`、`SpringCoreBeansLifecycleRawVsProxyLabTest` |
| [post-processors-overview.md](post-processors-overview.md) | BFPP、BDRPP、BPP 的职责、顺序和影响范围 | `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`、`SpringCoreBeansRegistryPostProcessorLabTest`、`SpringCoreBeansPostProcessorOrderingLabTest` |
| [beanpost-processors.md](beanpost-processors.md) | BeanPostProcessor 如何参与初始化前后、包装和编程式注册 | `SpringCoreBeansLifecycleRawVsProxyLabTest`、`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| [factorybean.md](factorybean.md) | FactoryBean 本身与产品对象如何创建、获取和暴露 | `SpringCoreBeansFactoryBeanDeepDiveLabTest`、`SpringCoreBeansFactoryBeanEdgeCasesLabTest` |
| [factorybean-type-matching.md](factorybean-type-matching.md) | FactoryBean 类型推断、`&` 前缀和产品类型匹配边界 | `SpringCoreBeansFactoryBeanDeepDiveLabTest`、`SpringCoreBeansFactoryBeanEdgeCasesLabTest`、`SpringCoreBeansServiceLoaderFactoryBeansLabTest` |
| [xml-bean-definition-reader.md](xml-bean-definition-reader.md) | XML 如何被解析为 BeanDefinition 并保留来源信息 | `SpringCoreBeansXmlBeanDefinitionReaderLabTest` |
| [properties-and-groovy-reader.md](properties-and-groovy-reader.md) | Properties 与 Groovy reader 如何提供外部定义输入 | `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`、`SpringCoreBeansGroovyBeanDefinitionReaderLabTest` |
| [xml-namespace-extension.md](xml-namespace-extension.md) | 自定义 XML namespace 如何扩展 BeanDefinition 注册 | `SpringCoreBeansXmlNamespaceExtensionLabTest` |
| [boot-auto-configuration-beans.md](boot-auto-configuration-beans.md) | Boot 自动配置如何注册默认 Bean、排序、退让和暴露条件报告 | `SpringCoreBeansAutoConfigurationLabTest`、`SpringCoreBeansAutoConfigurationBackoffTimingLabTest`、`SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`、`SpringCoreBeansConditionEvaluationReportLabTest` |
| [aot-native-overview.md](aot-native-overview.md) | AOT/Native 如何影响 Bean 推断、RuntimeHints 和 factories | `SpringCoreBeansAotRuntimeHintsLabTest`、`SpringCoreBeansRuntimeHintsBoundaryLabTest`、`SpringCoreBeansAotFactoriesLabTest` |
| [guide-quickstart-30min.md](guide-quickstart-30min.md) | 30 分钟内按最短路径跑通 Bean 主线和关键 Lab | `SpringCoreBeansLabTest`、`SpringCoreBeansMainlineCallChainLabTest` |
| [guide-mainline-timeline.md](guide-mainline-timeline.md) | 按时间线把 refresh、创建、注入、初始化和暴露串起来 | `SpringCoreBeansMainlineCallChainLabTest` |
| [guide-breakpoint-map.md](guide-breakpoint-map.md) | 调源码时每个问题应该停在哪些断点和 Lab 入口 | `SpringCoreBeansBreakpointPackLabTest` |
| [guide-deep-dive-guide.md](guide-deep-dive-guide.md) | 深挖学习顺序、阶段目标和验收入口 | `SpringCoreBeansModuleContractLabTest` |
| [appendix-common-pitfalls.md](appendix-common-pitfalls.md) | 常见误区、失败现象和最短观察入口 | `SpringCoreBeansTroubleshootingPlaybookLabTest`、`SpringCoreBeansExceptionNavigationLabTest` |
| [appendix-production-troubleshooting-checklist.md](appendix-production-troubleshooting-checklist.md) | 生产排障时按注册、候选、创建、代理、Boot/AOT 顺序自检 | `SpringCoreBeansTroubleshootingPlaybookLabTest`、`SpringCoreBeansExceptionNavigationLabTest`、`SpringCoreBeansModuleContractLabTest` |
