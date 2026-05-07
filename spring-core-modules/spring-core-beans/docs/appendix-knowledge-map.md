# 知识地图（Knowledge Map）：主文档归属表
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这页只做归属与跳转，不重复解释机制。
    - 先按症状找到主文档，再用最短证据入口验证。
    - 维护支持文档时，优先检查这张表是否仍然一对一。

    观察对象：知识地图（主文档归属表）。
    主线位置：`ApplicationContext#refresh` 主线、BeanDefinition 注册、依赖解析、实例化、后处理器、AOT/Boot 叠加层。

    对照入口：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansModuleContractLabTest`。
<!-- CHAPTER-CARD:END -->

## 读法

这页是归属表，不是教程。读者在排障时先找到症状对应的主文档，再去主文档读机制和边界。

## 主文档归属

| 层级 | 症状或问题 | 主文档 | 最短证据入口 |
| --- | --- | --- | --- |
| 容器与注册 | Bean、BeanDefinition、单例缓存、最终暴露对象分别是什么关系？ | `bean-mental-model.md` | [`ioc-bean-mental-model.md`](ioc-bean-mental-model.md) / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` |
| 容器与注册 | BeanFactory 与 ApplicationContext 的能力差异是什么？ | `beanfactory-vs-applicationcontext.md` | [`SpringCoreBeansBeanFactoryVsApplicationContextLabTest`](../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanFactoryVsApplicationContextLabTest.java) |
| 容器与注册 | 一个 BeanDefinition 是如何被注册进容器的？ | `bean-definition-registration.md` | [`ioc-bean-registration.md`](ioc-bean-registration.md) / `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` / `SpringCoreBeansComponentScanLabTest` / `SpringCoreBeansImportLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` |
| 容器与注册 | BeanDefinition 的 primary/autowireCandidate/source/factoryMethod 等元数据如何支撑候选选择和来源排查？ | `bean-definition-metadata-and-origin.md` | `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` / `SpringCoreBeansBeanDefinitionOriginLabTest` |
| 容器与注册 | beanName 和 alias 如何影响定位、注入和排障？ | `bean-name-and-alias.md` | [`wiring-bean-names-and-aliases.md`](wiring-bean-names-and-aliases.md) / `SpringCoreBeansBeanNameAliasLabTest` |
| 容器与注册 | 同名 BeanDefinition 冲突时，谁生效、谁失败、什么时候失败？ | `bean-definition-overriding.md` | [`wiring-bean-definition-overriding.md`](wiring-bean-definition-overriding.md) / `SpringCoreBeansBeanDefinitionOverridingLabTest` |
| 容器与注册 | MergedBeanDefinition / RootBeanDefinition 在什么阶段形成，解决什么问题？ | `merged-bean-definition.md` | [`wiring-merged-bean-definition.md`](wiring-merged-bean-definition.md) / `SpringCoreBeansMergedBeanDefinitionLabTest` |
| 容器与注册 | `@Configuration`、`@Bean`、`proxyBeanMethods` 各自改变了什么？ | `configuration-and-bean-method.md` | [`ioc-configuration-enhancement.md`](ioc-configuration-enhancement.md) / `SpringCoreBeansContainerLabTest` |
| 容器与注册 | `@Import`、ImportSelector、ImportBeanDefinitionRegistrar 的边界在哪里？ | `import-selector-and-registrar.md` | [`SpringCoreBeansImportLabTest`](../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java) / `SpringCoreBeansImportExerciseTest` / `SpringCoreBeansImportExerciseSolutionTest` |
| 容器与注册 | `registerBeanDefinition`、`registerBean`、`registerSingleton` 的根本差异是什么？ | `programmatic-registration.md` | [`SpringCoreBeansProgrammaticRegistrationLabTest`](../src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java) |
| 容器与注册 | `refresh()` 这条主线到底先做什么、后做什么？ | `refresh-mainline.md` | [`internals-refresh-to-bean-creation-mainline.md`](internals-refresh-to-bean-creation-mainline.md) / `SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` |
| 容器与注册 | 为什么注解处理器、自动装配和基础设施能够在容器里生效？ | `container-bootstrap-and-infrastructure.md` | [`internals-container-bootstrap-and-infrastructure.md`](internals-container-bootstrap-and-infrastructure.md) / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInfrastructureBeanRoleLabTest` |
| 容器与注册 | BFPP / BDRPP / BPP 的职责边界是什么，分别属于定义阶段还是实例阶段？ | `post-processors-overview.md` | [`ioc-post-processors.md`](ioc-post-processors.md) / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` |
| 容器与注册 | BFPP 在什么时候修改已有 BeanDefinition，不能做什么？ | `beanfactory-post-processors.md` | [`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`](../src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java) |
| 容器与注册 | BDRPP 为什么能在普通 BFPP 之前新增或改写 BeanDefinition？ | `bdrpp-definition-registration.md` | [`internals-bdrpp-definition-registration.md`](internals-bdrpp-definition-registration.md) / `SpringCoreBeansRegistryPostProcessorLabTest` |
| 容器与注册 | BPP 如何介入实例创建，什么时候会把 bean 换成 proxy？ | `beanpost-processors.md` | [`wiring-proxying-phase-bpp-wraps-bean.md`](wiring-proxying-phase-bpp-wraps-bean.md) / `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansLifecycleRawVsProxyLabTest` |
| 容器与注册 | PriorityOrdered、Ordered、无序处理器的排序规则如何影响行为？ | `post-processor-ordering.md` | [`internals-post-processor-ordering.md`](internals-post-processor-ordering.md) / `SpringCoreBeansPostProcessorOrderingLabTest` |
| 容器与注册 | 手工添加 BeanPostProcessor 为什么会绕过容器排序？ | `programmatic-bpp-registration.md` | [`wiring-programmatic-bpp-registration.md`](wiring-programmatic-bpp-registration.md) / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| 容器与注册 | `postProcessBeforeInstantiation` 为什么能让构造器根本不执行？ | `pre-instantiation-short-circuit.md` | [`internals-pre-instantiation-short-circuit.md`](internals-pre-instantiation-short-circuit.md) / `SpringCoreBeansPreInstantiationLabTest` |
| 容器与注册 | `doGetBean()` / `doCreateBean()` 的主线是什么？ | `bean-creation-mainline.md` | [`internals-refresh-to-bean-creation-mainline.md`](internals-refresh-to-bean-creation-mainline.md) / `SpringCoreBeansBeanCreationTraceLabTest` |
| 依赖解析与注入 | 注入点到底向容器提出了什么需求？ | `dependency-injection-resolution.md` | [`ioc-dependency-injection-resolution.md`](ioc-dependency-injection-resolution.md) / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` |
| 依赖解析与注入 | DependencyDescriptor / InjectionPoint 里有哪些元数据可用于排障？ | `dependency-descriptor-and-injection-point.md` | `SpringCoreBeansDependencyDescriptorMetadataLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest` |
| 依赖解析与注入 | 候选 bean 是如何被收集、筛选、收敛的？ | `autowire-candidate-selection.md` | [`wiring-autowire-candidate-selection-primary-priority-order.md`](wiring-autowire-candidate-selection-primary-priority-order.md) / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansAutowireCandidateSelectionExerciseTest` / `SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest` |
| 依赖解析与注入 | `@Qualifier`、`@Primary`、`@Priority`、`@Order` 各自管哪一步？ | `qualifier-primary-priority-order.md` | [`wiring-autowire-candidate-selection-primary-priority-order.md`](wiring-autowire-candidate-selection-primary-priority-order.md) / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` |
| 依赖解析与注入 | `@Resource` 的 name-first 与 `@Autowired` 的 by-type 有何本质差异？ | `resource-vs-autowired.md` | [`wiring-resource-injection-name-first.md`](wiring-resource-injection-name-first.md) / `SpringCoreBeansResourceInjectionLabTest` / `SpringCoreBeansResourceResolutionLabTest` |
| 依赖解析与注入 | Optional、`required=false`、`ObjectProvider`、`Provider` 怎么表达可选与延迟？ | `optional-and-provider-injection.md` | `SpringCoreBeansOptionalInjectionLabTest` / `SpringCoreBeansJsr330InjectionLabTest` |
| 依赖解析与注入 | 为什么有些对象能注入，但它们不是 Bean？ | `resolvable-dependency.md` | [`wiring-resolvable-dependency.md`](wiring-resolvable-dependency.md) / `SpringCoreBeansResolvableDependencyLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest` |
| 依赖解析与注入 | 泛型信息如何参与注入匹配，代理为什么会让它失真？ | `generic-type-matching.md` | [`wiring-generic-type-matching-pitfalls.md`](wiring-generic-type-matching-pitfalls.md) / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` |
| 依赖解析与注入 | field injection 与 constructor injection 处在什么阶段，观察点有什么不同？ | `injection-phase.md` | [`wiring-injection-phase-field-vs-constructor.md`](wiring-injection-phase-field-vs-constructor.md) / `SpringCoreBeansInjectionPhaseLabTest` / `SpringCoreBeansInjectionPhaseMatrixLabTest` |
| 生命周期、Scope 与代理边界 | singleton、prototype、其他 scope 的行为边界是什么？ | `scope-and-prototype.md` | [`ioc-scope-and-prototype.md`](ioc-scope-and-prototype.md) / `SpringCoreBeansLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest` |
| 生命周期、Scope 与代理边界 | 自定义 Scope 与 scoped proxy 如何改变注入对象和目标对象的关系？ | `custom-scope-and-scoped-proxy.md` | [`wiring-custom-scope-and-scoped-proxy.md`](wiring-custom-scope-and-scoped-proxy.md) / `SpringCoreBeansCustomScopeLabTest` |
| 生命周期、Scope 与代理边界 | lazy-init 与注入点 `@Lazy` 分别延迟了什么？ | `lazy-semantics.md` | [`wiring-lazy-semantics.md`](wiring-lazy-semantics.md) / `SpringCoreBeansLazyLabTest` |
| 生命周期、Scope 与代理边界 | `dependsOn` 如何强制初始化顺序，为什么它不是依赖注入规则？ | `depends-on.md` | [`wiring-depends-on.md`](wiring-depends-on.md) / `SpringCoreBeansDependsOnLabTest` |
| 生命周期、Scope 与代理边界 | Aware、init、destroy、`@PostConstruct` 的顺序如何理解？ | `lifecycle-callbacks.md` | [`ioc-lifecycle-and-callbacks.md`](ioc-lifecycle-and-callbacks.md) / `SpringCoreBeansLifecycleCallbackOrderLabTest` |
| 生命周期、Scope 与代理边界 | `SmartInitializingSingleton` 为什么要等所有单例都创建完？ | `smart-initializing-singleton.md` | [`wiring-smart-initializing-singleton.md`](wiring-smart-initializing-singleton.md) / `SpringCoreBeansSmartInitializingSingletonLabTest` |
| 生命周期、Scope 与代理边界 | `SmartLifecycle` 的 start/stop 与 phase 顺序如何工作？ | `smart-lifecycle.md` | [`wiring-smart-lifecycle-phase.md`](wiring-smart-lifecycle-phase.md) / `SpringCoreBeansSmartLifecycleLabTest` |
| 生命周期、Scope 与代理边界 | 循环依赖究竟解决了什么，解决不了什么？ | `circular-dependency.md` | [`ioc-circular-dependencies.md`](ioc-circular-dependencies.md) / `SpringCoreBeansCircularDependencyBoundaryLabTest` |
| 生命周期、Scope 与代理边界 | early reference 与三级缓存如何协作？ | `early-reference-and-three-level-cache.md` | [`internals-early-reference-and-circular.md`](internals-early-reference-and-circular.md) / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| 生命周期、Scope 与代理边界 | BPP 在哪个窗口把 bean 包装成 proxy，自调用为什么绕过它？ | `proxying-phase.md` | [`wiring-proxying-phase-bpp-wraps-bean.md`](wiring-proxying-phase-bpp-wraps-bean.md) / `SpringCoreBeansProxyingPhaseLabTest` |
| 生命周期、Scope 与代理边界 | FactoryBean 的产品对象和工厂对象如何区分？ | `factorybean.md` | [`ioc-factorybean.md`](ioc-factorybean.md) / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` |
| 生命周期、Scope 与代理边界 | FactoryBean 的类型匹配边界在哪里，`getObjectType()` 为什么关键？ | `factorybean-type-matching.md` | `SpringCoreBeansFactoryBeanEdgeCasesLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` |
| 生命周期、Scope 与代理边界 | 父子 ApplicationContext 的可见性和覆盖边界是什么？ | `context-hierarchy.md` | [`wiring-context-hierarchy.md`](wiring-context-hierarchy.md) / `SpringCoreBeansContextHierarchyLabTest` |
| 生命周期、Scope 与代理边界 | BeanFactory API 与 AutowireCapableBeanFactory 的边界是什么？ | `beanfactory-api-and-autowirecapablebeanfactory.md` | [`wiring-beanfactory-api-deep-dive.md`](wiring-beanfactory-api-deep-dive.md) / `SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest` |
| 值解析、转换与外部输入 | Environment / PropertySource 如何决定值从哪里来？ | `environment-and-propertysource.md` | [`wiring-environment-and-propertysource.md`](wiring-environment-and-propertysource.md) / `SpringCoreBeansEnvironmentPropertySourceLabTest` |
| 值解析、转换与外部输入 | `${...}` 占位符何时 strict，何时 non-strict？ | `value-placeholder-resolution.md` | [`wiring-value-placeholder-resolution-strict-vs-non-strict.md`](wiring-value-placeholder-resolution-strict-vs-non-strict.md) / `SpringCoreBeansValuePlaceholderResolutionLabTest` |
| 值解析、转换与外部输入 | `#{...}` SpEL 与 `${...}` 占位符的解析顺序是什么？ | `spel-and-value-expression.md` | [`aot-spel-and-value-expression.md`](aot-spel-and-value-expression.md) / `SpringCoreBeansSpelValueLabTest` |
| 值解析、转换与外部输入 | BeanWrapper、ConversionService、PropertyEditor 各负责哪一段？ | `type-conversion-and-beanwrapper.md` | [`wiring-type-conversion-and-beanwrapper.md`](wiring-type-conversion-and-beanwrapper.md) / `SpringCoreBeansTypeConversionLabTest` / `SpringCoreBeansPropertyEditorLabTest` / `SpringCoreBeansPropertyEditorResolutionLabTest` |
| 值解析、转换与外部输入 | XML 如何变成 BeanDefinition？ | `xml-bean-definition-reader.md` | `SpringCoreBeansXmlBeanDefinitionReaderLabTest` |
| 值解析、转换与外部输入 | Properties / Groovy 这类输入如何变成 BeanDefinition？ | `properties-and-groovy-reader.md` | `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` |
| 值解析、转换与外部输入 | XML namespace 扩展如何把自定义标签变成定义？ | `xml-namespace-extension.md` | [`aot-xml-namespace-extension.md`](aot-xml-namespace-extension.md) / `SpringCoreBeansXmlNamespaceExtensionLabTest` |
| 值解析、转换与外部输入 | lookup-method / replaced-method 解决的是什么动态取对象问题？ | `method-injection.md` | `SpringCoreBeansReplacedMethodLabTest` |
| 值解析、转换与外部输入 | Spring 内置 FactoryBean 的常见形态有哪些？ | `built-in-factorybeans.md` | [`aot-built-in-factorybeans-gallery.md`](aot-built-in-factorybeans-gallery.md) / `SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` |
| Boot 叠加后的变化 | Auto-configuration 的顺序为什么会影响条件命中？ | `boot-auto-configuration-ordering.md` | [`boot-auto-config-ordering.md`](boot-auto-config-ordering.md) / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` |
| Boot 叠加后的变化 | Boot 自动装配如何决定一个 Bean 出现还是退回 backoff？ | `boot-auto-configuration-beans.md` | [`boot-spring-boot-auto-configuration.md`](boot-spring-boot-auto-configuration.md) / `SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` |
| AOT / Native | RuntimeHints 为什么是构建期契约？ | `aot-runtimehints.md` | [`aot-runtimehints-basics.md`](aot-runtimehints-basics.md) / `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansRuntimeHintsBoundaryLabTest` |
| AOT / Native | AOT 语境下 XML BeanDefinitionReader 的边界是什么？ | `aot-xml-bean-definition-reader.md` | [`aot-xml-bean-definition-reader.md`](aot-xml-bean-definition-reader.md) / `SpringCoreBeansXmlBeanDefinitionReaderLabTest` |
| AOT / Native | 容器外对象注入在 AOT 下怎样成立？ | `aot-autowirecapablebeanfactory-external-objects.md` | [`aot-autowirecapablebeanfactory-external-objects.md`](aot-autowirecapablebeanfactory-external-objects.md) / `SpringCoreBeansAutowireCapableBeanFactoryLabTest` |
| AOT / Native | SpEL / Value 在 AOT 下会遇到什么约束？ | `aot-spel-and-value-expression.md` | [`aot-spel-and-value-expression.md`](aot-spel-and-value-expression.md) / `SpringCoreBeansSpelValueLabTest` |
| AOT / Native | 自定义 Qualifier 在 AOT 下要补什么契约？ | `aot-custom-qualifier.md` | [`aot-custom-qualifier-meta-annotation.md`](aot-custom-qualifier-meta-annotation.md) / `SpringCoreBeansCustomQualifierLabTest` |
| AOT / Native | XML namespace 扩展在 AOT 下为什么需要额外约束？ | `aot-xml-namespace-extension.md` | [`aot-xml-namespace-extension.md`](aot-xml-namespace-extension.md) / `SpringCoreBeansXmlNamespaceExtensionLabTest` |
| AOT / Native | Properties / Groovy 等输入在 AOT 下有哪些边界？ | `aot-beandefinitionreader-other-inputs.md` | [`aot-beandefinitionreader-other-inputs-properties-groovy.md`](aot-beandefinitionreader-other-inputs-properties-groovy.md) / `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` |
| AOT / Native | 方法注入在 AOT 下为什么需要单独验证？ | `aot-method-injection.md` | [`aot-method-injection-replaced-method.md`](aot-method-injection-replaced-method.md) / `SpringCoreBeansReplacedMethodLabTest` |
| AOT / Native | 内置 FactoryBean 在 AOT 下有哪些特殊要求？ | `aot-built-in-factorybeans.md` | [`aot-built-in-factorybeans-gallery.md`](aot-built-in-factorybeans-gallery.md) / `SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` |
| AOT / Native | PropertyEditor / 值解析在 AOT 下如何落地？ | `aot-property-editor-and-value-resolution.md` | [`aot-property-editor-and-value-resolution.md`](aot-property-editor-and-value-resolution.md) / `SpringCoreBeansPropertyEditorResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest` / `SpringCoreBeansTypeConversionLabTest` |
| AOT / Native | 为什么 JVM 可运行不等于 Native 可运行？ | `aot-native-overview.md` | [`aot-aot-and-native-overview.md`](aot-aot-and-native-overview.md) / `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansRuntimeHintsBoundaryLabTest` / `SpringCoreBeansAotFactoriesLabTest` |

## 支持文档

| 支持文档 | 职责 | 禁止承担的内容 |
| --- | --- | --- |
| `guide-applicationcontext-refresh-call-chain.md` | refresh 调用链导读、断点入口 | 不复写 Bean 创建主线机制 |
| `guide-branch-decision-matrix.md` | 分支矩阵与选路 | 不重复解释单个机制 |
| `guide-breakpoint-map.md` | 断点地图与观察点 | 不做教程正文 |
| `guide-deep-dive-guide.md` | 深入阅读顺序 | 不承担新的知识点 |
| `guide-mainline-timeline.md` | 主线时间线 | 不扩写成机制总览 |
| `guide-quickstart-30min.md` | 30 分钟最小闭环入口 | 不复述正文机制 |
| `guide-why-index.md` | Why Index / 常见为什么的入口 | 不展开完整机制长文 |
| `appendix-common-pitfalls.md` | 常见误区对照 | 不重写主文档机制 |
| `appendix-debugger-pack.md` | 断点包总入口 | 不写教程正文 |
| `appendix-explore-debug-tests.md` | Explore/Debug 用例索引 | 不解释机制主线 |
| `appendix-glossary.md` | 术语表 | 不扩写为章节正文 |
| `appendix-interview-playbook.md` | 面试复述模板 | 不覆盖机制解释 |
| `appendix-knowledge-map.md` | 归属索引与跳转 | 不重复机制长文 |
| `appendix-production-troubleshooting-checklist.md` | 生产排障清单 | 不解释正文机制 |
| `appendix-self-check.md` | 文档导航自检 | 不承担新知识点 |
| `appendix-spring-beans-public-api-index.md` | 公共 API 索引 | 不写使用教程 |
| `appendix-spring-beans-public-api-gap.md` | API 缺口与最小 Labs | 不复写正文长解释 |
| `appendix-team-training-kit.md` | 内训讲义脚本 | 不替代主文档 |
| `deepening-aot-and-real-world.md` | AOT/实战维护说明 | 不写教程正文 |
| `deepening-appendix.md` | 附录维护边界 | 不解释 Bean 机制 |
| `deepening-boot-autoconfig.md` | Boot 深化路线维护 | 不复述 Boot 主文档 |
| `deepening-container-internals.md` | Internals 深化路线维护 | 不复述主线机制 |
| `deepening-docs-root.md` | 站点结构说明 | 不写章节正文 |
| `deepening-guide.md` | Guide 维护边界 | 不承载新知识点 |
| `deepening-ioc-container.md` | IoC Container 维护说明 | 不重复主文档机制 |
| `deepening-module-readme.md` | README 维护说明 | 不覆盖 README 入口职责 |
| `deepening-module-rewrite-rationale.md` | 重写理由与边界 | 不讲 Bean 机制正文 |
| `deepening-strategies.md` | 维护策略总览 | 不替代主文档 |
| `deepening-wiring-and-boundaries.md` | Wiring 维护说明 | 不展开机制教程 |
| `boot-debugging-and-observability.md` | Boot 调试与可观测入口 | 不解释新的 Bean 机制 |

## 小结

这页只做三件事：给出主文档归属、给出最短证据入口、收束支持文档边界。
