# task：spring-core-beans docs 全章逐章深化策略

> 说明：这里不设“统一固定标准”。每章给出的是“该章最值得更深一层的具体补充策略”，后续按批次逐步落地。

任务状态：
- `[ ]` Pending
- `[√]` Completed
- `[X]` Failed
- `[-]` Skipped

## 逐章策略（按 Docs TOC 顺序）

### 01. [ ] spring-core-beans 文档导航（Docs TOC）

- 文件：`spring-core-modules/spring-core-beans/docs/README.md`
- 现状速记：关键锚点：（本章未显式列出）；配套 Lab：（本章未显式列出）
- 深化策略：
  - 补充“按角色/按目标”的入口导航：源码进阶/面试复述/团队内训三条最短闭环路径（章节组合 + 对应 Lab/Test）。
  - 增加“按关键类/关键方法索引”：例如 `AbstractBeanFactory#doGetBean`、`AbstractAutowireCapableBeanFactory#doCreateBean`、`DefaultListableBeanFactory#doResolveDependency`、`AbstractAutowireCapableBeanFactory#populateBean`。
  - 增加“按症状索引”：把高频异常 message/现象直接链接到分支矩阵与对应章节（用于快速定位）。

### 02. [ ] 第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/010-03-mainline-timeline.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#finishRefresh` / `AbstractApplicationContext#prepareBeanFactory`；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansMainlineCallChainLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段说明 `BeanDefinition`/singleton caches/processor 列表的变化点，让时间线可在 debugger 里被验证。
  - 增加“同一条主线的分支树”：把关键分支（pre-instantiation/dependsOn/parent/prototype guard 等）用“触发条件→落点方法→观察点”形式展开。
  - 增加“可操作调试脚本”：给出条件断点模板与 watch list，让读者能复盘一次完整主线而不迷路。

### 03. [ ] 第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanFactoryApiLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段说明 `BeanDefinition`/singleton caches/processor 列表的变化点，让时间线可在 debugger 里被验证。
  - 增加“同一条主线的分支树”：把关键分支（pre-instantiation/dependsOn/parent/prototype guard 等）用“触发条件→落点方法→观察点”形式展开。
  - 增加“可操作调试脚本”：给出条件断点模板与 watch list，让读者能复盘一次完整主线而不迷路。

### 04. [ ] 第 11 章：关键分支矩阵（Branch Decision Matrix）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md`
- 现状速记：关键锚点：`AbstractBeanFactory#resolveEmbeddedValue` / `CommonAnnotationBeanPostProcessor#postProcessProperties` / `DefaultListableBeanFactory#doResolveDependency`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段说明 `BeanDefinition`/singleton caches/processor 列表的变化点，让时间线可在 debugger 里被验证。
  - 增加“同一条主线的分支树”：把关键分支（pre-instantiation/dependsOn/parent/prototype guard 等）用“触发条件→落点方法→观察点”形式展开。
  - 增加“可操作调试脚本”：给出条件断点模板与 watch list，让读者能复盘一次完整主线而不迷路。

### 05. [ ] 第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#initializeBean` / `AbstractBeanFactory#doGetBean`；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansMainlineCallChainLabTest`
- 深化策略：
  - 补充本章的“反例/误归因对照”：同一现象的不同根因如何在源码分支上被区分。
  - 增加“30 分钟闭环的失败排障段落”：断点不命中/测试过慢/环境配置干扰时的最短修复路径与替代操作。
  - 补充“最小心智模型解释”：用 `doGetBean/doCreateBean/populateBean/initializeBean` 四站点串起“定义→实例→注入→初始化/代理”的因果链，并给每站点一组可观察输出（变量/断言/日志）。

### 06. [ ] 第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansContainerLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段说明 `BeanDefinition`/singleton caches/processor 列表的变化点，让时间线可在 debugger 里被验证。
  - 增加“同一条主线的分支树”：把关键分支（pre-instantiation/dependsOn/parent/prototype guard 等）用“触发条件→落点方法→观察点”形式展开。
  - 增加“可操作调试脚本”：给出条件断点模板与 watch list，让读者能复盘一次完整主线而不迷路。

### 07. [ ] 第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段说明 `BeanDefinition`/singleton caches/processor 列表的变化点，让时间线可在 debugger 里被验证。
  - 增加“同一条主线的分支树”：把关键分支（pre-instantiation/dependsOn/parent/prototype guard 等）用“触发条件→落点方法→观察点”形式展开。
  - 增加“可操作调试脚本”：给出条件断点模板与 watch list，让读者能复盘一次完整主线而不迷路。

### 08. [ ] 第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- 现状速记：关键锚点：`ApplicationContext#refresh` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `ConstructorResolver#autowireConstructor`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 深化策略：
  - 更深一层讲“注入点信息承载体”：展开 `DependencyDescriptor`（required/annotations/resolvableType/field vs parameter），解释为什么同类型在不同注入点结果不同。
  - 把依赖解析讲成分支树：`DefaultListableBeanFactory#doResolveDependency` 的“快捷路径→resolvableDependencies→候选收集→候选收敛→集合解析→fallback”，并为每分支补一个典型现象/异常。
  - 补充“可扩展点”的真实用法：`AutowireCandidateResolver`/`DependencyComparator`/自定义 Qualifier 的职责边界与落点方法。

### 09. [ ] 第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `ApplicationContext#refresh` / `ConfigurableBeanFactory#destroyBean`；配套 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansCustomScopeLabTest` / `SpringCoreBeansLabTest`
- 深化策略：
  - 补充 prototype 的关键边界：`AbstractBeanFactory#doGetBean` 对 prototype 的 guard（例如 `isPrototypeCurrentlyInCreation`）以及为什么循环依赖救不了。
  - 增加“scope 注入到 singleton”的对照：`ObjectProvider`/`@Lookup`/scoped proxy 三种策略分别解决什么问题，关键落点类是什么。
  - 补充“销毁语义与资源释放”：prototype 默认不销毁，自定义 scope 如何管理生命周期，如何避免线程/请求上下文泄漏。

### 10. [ ] 第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- 现状速记：关键锚点：`AbstractApplicationContext#close` / `AbstractApplicationContext#doClose` / `AbstractApplicationContext#refresh`；配套 Lab：`SpringCoreBeansAwareInfrastructureLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest`
- 深化策略：
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 补充“线上排障路径”：回调不执行/执行两次/执行顺序异常时，如何从 `initializeBean`/`DisposableBeanAdapter` 相关证据链定位。

### 11. [ ] 第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`；配套 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
- 深化策略：
  - 更深一层讲 processor 的“时机与排序算法”：`PriorityOrdered`/`Ordered`/non-ordered 的分组与排序入口，以及为什么要分阶段 invoke/register。
  - 补充“默认基础设施处理器的存在理由”：`ConfigurationClassPostProcessor`/`AutowiredAnnotationBeanPostProcessor`/`CommonAnnotationBeanPostProcessor` 等分别解决什么能力。
  - 增加“排障：没生效/太晚生效/只对部分 bean 生效”：用 refresh 时序 + ordering 规则把问题归因到具体阶段。

### 12. [ ] 第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `ApplicationContext#refresh` / `BeanMethodInterceptor#intercept`；配套 Lab：`SpringCoreBeansContainerLabTest`
- 深化策略：
  - 更深一层讲配置类增强：`ConfigurationClassPostProcessor` 的解析/注册阶段，以及 `ConfigurationClassEnhancer` 如何把 `@Bean` 方法调用转成 `getBean`。
  - 补充 lite vs full 语义：`@Configuration(proxyBeanMethods=false)`、`@Component + @Bean`、static `@Bean` 等在源码里如何判定、为何行为不同。
  - 增加高频坑对照：跨配置类方法调用、自调用、final/private 限制、循环依赖与代理交织等场景的可推导结论。

### 13. [ ] 02. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyPropertyValues` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansComponentScanLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansImportLabTest`
- 深化策略：
  - 补充“注册入口的合流点”：把 scan/@Bean/@Import(programmatic)/XML/Boot auto-config 最终如何汇入 `BeanDefinitionRegistry#registerBeanDefinition` 讲清楚，并明确哪些发生在定义层、哪些发生在实例层。
  - 增加“命名与覆盖的组合问题”：`BeanNameGenerator`/alias/overriding 三者如何叠加导致“看似同名但不是同一个/覆盖发生但实例不变”。
  - 补充“来源可观察性”：如何追踪 `BeanDefinition` 的 source/metadata（资源路径、注解来源、registrar）以便定位“是谁注册的”。
  - 更深一层讲合并算法：child/parent BD 的合并规则、override 规则与缓存位置（何时生成、何时复用）。
  - 补充与 processor 的交互：`MergedBeanDefinitionPostProcessor` 何时被调用、能改什么、不能改什么。

### 14. [ ] 第 20 章：01. Bean 心智模型：从 BeanDefinition 到最终暴露对象

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `ApplicationContext#refresh` / `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 深化策略：
  - 补充“注册入口的合流点”：把 scan/@Bean/@Import(programmatic)/XML/Boot auto-config 最终如何汇入 `BeanDefinitionRegistry#registerBeanDefinition` 讲清楚，并明确哪些发生在定义层、哪些发生在实例层。
  - 增加“命名与覆盖的组合问题”：`BeanNameGenerator`/alias/overriding 三者如何叠加导致“看似同名但不是同一个/覆盖发生但实例不变”。
  - 补充“来源可观察性”：如何追踪 `BeanDefinition` 的 source/metadata（资源路径、注解来源、registrar）以便定位“是谁注册的”。
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。

### 15. [ ] 08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#getObjectForBeanInstance` / `AbstractBeanFactory#isTypeMatch`；配套 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 深化策略：
  - 补充 FactoryBean 的类型推断与缓存链路：`getObjectType`/`getTypeForFactoryBean`/`getObjectFromFactoryBean`，解释按类型注入/条件判断为何会受影响。
  - 增加 `&beanName` 的“取 factory vs 取 product”误用清单，并给出在源码里区分两者的最短证据链。
  - 补充与代理/循环依赖的交叉：FactoryBean 产物被代理时，early reference 与最终暴露对象可能不同，如何断言与排障。

### 16. [ ] 09. 循环依赖：现象、原因与规避（constructor vs setter）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractAutowireCapableBeanFactory#populateBean`；配套 Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 深化策略：
  - 把“可救/不可救”细分成可推导分类：setter 环、constructor 环、prototype 环、dependsOn 环，各自 fail-fast 点与原因。
  - 更深一层讲 early reference：`DefaultSingletonBeanRegistry` 的三级缓存 + `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference` 如何让代理介入。
  - 补充线上排障配方：定位环路边、选择打断手段（`@Lazy`/`ObjectProvider`/重构依赖）并说明取舍。

### 17. [ ] 第 19 章：11. 调试与自检：如何“看见”容器正在做什么

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutoProxyCreator#postProcessAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`；配套 Lab：`SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 深化策略：
  - 补充“从异常到证据链”的方法：把高频异常 message 映射到关键方法与关键变量，形成可复用定位套路。
  - 增加“可观察性工具化”：把 BeanDefinition 来源、候选集合、依赖图等信息固化到 dumper/测试输出中，减少靠猜。
  - 补充“调试开关与日志类别”：哪些 logger/category/flag 能把信息压缩到可用范围，避免信息噪声。

### 18. [ ] 09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md`
- 现状速记：关键锚点：`AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `ConfigurationClassPostProcessor#processConfigBeanDefinitions`；配套 Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest`
- 深化策略：
  - 更深一层讲配置类增强：`ConfigurationClassPostProcessor` 的解析/注册阶段，以及 `ConfigurationClassEnhancer` 如何把 `@Bean` 方法调用转成 `getBean`。
  - 补充 lite vs full 语义：`@Configuration(proxyBeanMethods=false)`、`@Component + @Bean`、static `@Bean` 等在源码里如何判定、为何行为不同。
  - 增加高频坑对照：跨配置类方法调用、自调用、final/private 限制、循环依赖与代理交织等场景的可推导结论。
  - 补充 Boot 自动装配主线的更细粒度角色：import selector/filter/listener/group/sorter 的职责边界与关键落点方法。
  - 更深一层讲“顺序 vs 条件”的关系：排序影响注册先后，条件评估的输入与时机差异如何导致 back-off/覆盖误判。

### 19. [ ] 第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `ApplicationContext#refresh` / `ApplicationContextRunner#run`；配套 Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` / `SpringCoreBeansAutoConfigurationLabTest`
- 深化策略：
  - 更深一层讲配置类增强：`ConfigurationClassPostProcessor` 的解析/注册阶段，以及 `ConfigurationClassEnhancer` 如何把 `@Bean` 方法调用转成 `getBean`。
  - 补充 lite vs full 语义：`@Configuration(proxyBeanMethods=false)`、`@Component + @Bean`、static `@Bean` 等在源码里如何判定、为何行为不同。
  - 增加高频坑对照：跨配置类方法调用、自调用、final/private 限制、循环依赖与代理交织等场景的可推导结论。
  - 补充 Boot 自动装配主线的更细粒度角色：import selector/filter/listener/group/sorter 的职责边界与关键落点方法。
  - 更深一层讲“顺序 vs 条件”的关系：排序影响注册先后，条件评估的输入与时机差异如何导致 back-off/覆盖误判。

### 20. [ ] 第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansResourceInjectionLabTest`
- 深化策略：
  - 补充本章的“反例/误归因对照”：同一现象的不同根因如何在源码分支上被区分。
  - 补充“基础设施处理器清单 + 注册位置”：把 `AnnotationConfigUtils` 的默认注册与 `prepareBeanFactory` 的默认能力对照讲清楚（每个处理器解决什么能力）。
  - 增加“注解失效故障树”：把 `@Autowired/@Value/@Resource/@Configuration/@Bean` 不生效的高频根因归类到“缺处理器/顺序不对/时机太晚/作用域不匹配”，并给出对应方法级证据链。

### 21. [ ] 13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansRegistryPostProcessorLabTest`
- 深化策略：
  - 补充“注册入口的合流点”：把 scan/@Bean/@Import(programmatic)/XML/Boot auto-config 最终如何汇入 `BeanDefinitionRegistry#registerBeanDefinition` 讲清楚，并明确哪些发生在定义层、哪些发生在实例层。
  - 增加“命名与覆盖的组合问题”：`BeanNameGenerator`/alias/overriding 三者如何叠加导致“看似同名但不是同一个/覆盖发生但实例不变”。
  - 补充“来源可观察性”：如何追踪 `BeanDefinition` 的 source/metadata（资源路径、注解来源、registrar）以便定位“是谁注册的”。
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。

### 22. [ ] 14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`；配套 Lab：`SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
- 深化策略：
  - 更深一层讲“注入点信息承载体”：展开 `DependencyDescriptor`（required/annotations/resolvableType/field vs parameter），解释为什么同类型在不同注入点结果不同。
  - 把依赖解析讲成分支树：`DefaultListableBeanFactory#doResolveDependency` 的“快捷路径→resolvableDependencies→候选收集→候选收敛→集合解析→fallback”，并为每分支补一个典型现象/异常。
  - 补充“可扩展点”的真实用法：`AutowireCandidateResolver`/`DependencyComparator`/自定义 Qualifier 的职责边界与落点方法。

### 23. [ ] 15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation` / `AbstractAutowireCapableBeanFactory#createBean` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansPreInstantiationLabTest`
- 深化策略：
  - 补充本章的“反例/误归因对照”：同一现象的不同根因如何在源码分支上被区分。
  - 更深一层补“短路点全景”：`resolveBeforeInstantiation`/`predictBeanType`/`determineCandidateConstructors`/FactoryBean type prediction 的触发条件与边界（尤其与 AOP 的交叉）。
  - 补充“短路导致的副作用清单”：哪些生命周期阶段会被跳过（populate/initialize/部分 BPP），哪些仍会执行；并给出可断点证明的最短链路与关键变量。

### 24. [ ] 16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `DefaultSingletonBeanRegistry#addSingletonFactory`；配套 Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 深化策略：
  - 把“可救/不可救”细分成可推导分类：setter 环、constructor 环、prototype 环、dependsOn 环，各自 fail-fast 点与原因。
  - 更深一层讲 early reference：`DefaultSingletonBeanRegistry` 的三级缓存 + `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference` 如何让代理介入。
  - 补充线上排障配方：定位环路边、选择打断手段（`@Lazy`/`ObjectProvider`/重构依赖）并说明取舍。

### 25. [ ] 17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`
- 现状速记：关键锚点：`AbstractApplicationContext#doClose` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#initializeBean`；配套 Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest`
- 深化策略：
  - 补充 prototype 的关键边界：`AbstractBeanFactory#doGetBean` 对 prototype 的 guard（例如 `isPrototypeCurrentlyInCreation`）以及为什么循环依赖救不了。
  - 增加“scope 注入到 singleton”的对照：`ObjectProvider`/`@Lookup`/scoped proxy 三种策略分别解决什么问题，关键落点类是什么。
  - 补充“销毁语义与资源释放”：prototype 默认不销毁，自定义 scope 如何管理生命周期，如何避免线程/请求上下文泄漏。
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。

### 26. [ ] 18. 从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段说明 `BeanDefinition`/singleton caches/processor 列表的变化点，让时间线可在 debugger 里被验证。
  - 增加“同一条主线的分支树”：把关键分支（pre-instantiation/dependsOn/parent/prototype guard 等）用“触发条件→落点方法→观察点”形式展开。
  - 增加“可操作调试脚本”：给出条件断点模板与 watch list，让读者能复盘一次完整主线而不迷路。

### 27. [ ] 第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#createBean` / `AbstractBeanFactory#doGetBean` / `ApplicationContext#refresh`；配套 Lab：`SpringCoreBeansLazyLabTest`
- 深化策略：
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 补充“线上排障路径”：回调不执行/执行两次/执行顺序异常时，如何从 `initializeBean`/`DisposableBeanAdapter` 相关证据链定位。

### 28. [ ] 19. dependsOn：强制初始化顺序（即使没有显式依赖）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-depends-on.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractBeanFactory#doGetBean` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties`；配套 Lab：`SpringCoreBeansDependsOnLabTest`
- 深化策略：
  - 补充 dependsOn 与生命周期/启动顺序的选型：与 `SmartLifecycle` phase 对照，明确什么时候应该用 phase、什么时候用 dependsOn。
  - 增加“图结构调试视角”：用 `getDependenciesForBean`/`getDependentBeans` 复盘依赖图并定位环。
  - 补充父子容器边界：dependsOn 名称解析与可见性在层级 context 下的表现（与 context hierarchy 章节联动）。

### 29. [ ] 20. registerResolvableDependency：能注入，但它不是 Bean

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md`
- 现状速记：关键锚点：`AbstractApplicationContext#prepareBeanFactory` / `AbstractAutowireCapableBeanFactory#invokeAwareMethods` / `AutowireUtils#resolveAutowiringValue`；配套 Lab：`SpringCoreBeansResolvableDependencyLabTest`
- 深化策略：
  - 更深一层讲 resolvableDependencies 的匹配细节：按可赋值关系命中、为何不走 `@Qualifier`/候选收敛分支。
  - 补充 `ObjectFactory` 值的解包链路：`AutowireUtils#resolveAutowiringValue` 何时触发、如何在断点里观察。
  - 增加“何时不该用”的反例：把业务对象塞进 resolvableDependencies 导致生命周期/AOP 失效的案例与替代方案。

### 30. [ ] 21. 父子 ApplicationContext：可见性与覆盖边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-context-hierarchy.md`
- 现状速记：关键锚点：`AbstractApplicationContext#getParent` / `AbstractApplicationContext#setParent` / `AbstractBeanFactory#containsBean`；配套 Lab：`SpringCoreBeansContextHierarchyLabTest`
- 深化策略：
  - 补充层级容器的可见性算法：`HierarchicalBeanFactory`/`BeanFactoryUtils` 在按名/按类型查找时的差异与优先级。
  - 增加“覆盖/隔离”的工程语义：父子容器同名/同类型 bean 的注入影响与排障路径。
  - 补充与 Boot/web 场景的联系：典型的 root/child context 下，为什么某些 bean 只能在其中一个容器可见。
  - 补充覆盖语义来源：容器 allowOverriding 与 Boot 配置（如 `spring.main.allow-bean-definition-overriding`）如何影响 `DefaultListableBeanFactory` 行为。
  - 更深一层讲“覆盖发生在定义层”：覆盖替换 BeanDefinition，不会回滚已创建的单例对象（并建议给出对照案例）。

### 31. [ ] 22. Bean 名称与 alias：同一个实例，多一个名字

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#transformedBeanName` / `BeanDefinitionReaderUtils#generateBeanName`；配套 Lab：`SpringCoreBeansBeanNameAliasLabTest`
- 深化策略：
  - 补充命名体系底层实现：`SimpleAliasRegistry` 的 alias map 语义（alias→canonicalName）与覆盖规则。
  - 增加“名字参与注入”的全入口：by-name fallback、`@Resource`、qualifier value 与 beanName 的交叉。
  - 补充团队命名策略：如何设计稳定 beanName/alias，降低重构时的注入破坏风险，并给反例。

### 32. [ ] 23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#getObjectForBeanInstance` / `AbstractBeanFactory#getType`；配套 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 深化策略：
  - 补充 FactoryBean 的类型推断与缓存链路：`getObjectType`/`getTypeForFactoryBean`/`getObjectFromFactoryBean`，解释按类型注入/条件判断为何会受影响。
  - 增加 `&beanName` 的“取 factory vs 取 product”误用清单，并给出在源码里区分两者的最短证据链。
  - 补充与代理/循环依赖的交叉：FactoryBean 产物被代理时，early reference 与最终暴露对象可能不同，如何断言与排障。

### 33. [ ] 24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md`
- 现状速记：关键锚点：`BeanDefinitionOverrideException#getBeanName` / `DefaultListableBeanFactory#doResolveDependency` / `DefaultListableBeanFactory#getBeanDefinition`；配套 Lab：`SpringCoreBeansBeanDefinitionOriginLabTest` / `SpringCoreBeansBeanDefinitionOverridingLabTest`
- 深化策略：
  - 补充“注册入口的合流点”：把 scan/@Bean/@Import(programmatic)/XML/Boot auto-config 最终如何汇入 `BeanDefinitionRegistry#registerBeanDefinition` 讲清楚，并明确哪些发生在定义层、哪些发生在实例层。
  - 增加“命名与覆盖的组合问题”：`BeanNameGenerator`/alias/overriding 三者如何叠加导致“看似同名但不是同一个/覆盖发生但实例不变”。
  - 补充“来源可观察性”：如何追踪 `BeanDefinition` 的 source/metadata（资源路径、注解来源、registrar）以便定位“是谁注册的”。
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。

### 34. [ ] 25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`；配套 Lab：`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
- 深化策略：
  - 更深一层讲“注入点信息承载体”：展开 `DependencyDescriptor`（required/annotations/resolvableType/field vs parameter），解释为什么同类型在不同注入点结果不同。
  - 把依赖解析讲成分支树：`DefaultListableBeanFactory#doResolveDependency` 的“快捷路径→resolvableDependencies→候选收集→候选收敛→集合解析→fallback”，并为每分支补一个典型现象/异常。
  - 补充“可扩展点”的真实用法：`AutowireCandidateResolver`/`DependencyComparator`/自定义 Qualifier 的职责边界与落点方法。
  - 更深一层讲 processor 的“时机与排序算法”：`PriorityOrdered`/`Ordered`/non-ordered 的分组与排序入口，以及为什么要分阶段 invoke/register。
  - 补充“默认基础设施处理器的存在理由”：`ConfigurationClassPostProcessor`/`AutowiredAnnotationBeanPostProcessor`/`CommonAnnotationBeanPostProcessor` 等分别解决什么能力。

### 35. [ ] 26. SmartInitializingSingleton：所有单例都创建完之后再做事

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractBeanFactory#doGetBean`；配套 Lab：`SpringCoreBeansSmartInitializingSingletonLabTest`
- 深化策略：
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 补充“线上排障路径”：回调不执行/执行两次/执行顺序异常时，如何从 `initializeBean`/`DisposableBeanAdapter` 相关证据链定位。
  - 补充 `SmartInitializingSingleton#afterSingletonsInstantiated` 的触发边界：只对单例、发生在何时、与 lazy/dependsOn 的关系。
  - 增加工程化用法：用它做容器一致性校验/依赖图校验/外部资源健康检查，并解释为何优于 @PostConstruct。

### 36. [ ] 27. SmartLifecycle：start/stop 时机与 phase 顺序

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishRefresh` / `DefaultLifecycleProcessor#onRefresh` / `DefaultLifecycleProcessor#startBeans`；配套 Lab：`SpringCoreBeansSmartLifecycleLabTest`
- 深化策略：
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 补充“线上排障路径”：回调不执行/执行两次/执行顺序异常时，如何从 `initializeBean`/`DisposableBeanAdapter` 相关证据链定位。
  - 更深一层讲 `DefaultLifecycleProcessor` 算法：phase 分组、启动正序、停止逆序，以及 `stop(Runnable)` 的意义。
  - 补充 phase 与 dependsOn 的边界：两者都影响顺序但语义不同，给出可落地选型建议。

### 37. [ ] 28. 自定义 Scope + scoped proxy：thread scope 的真实语义

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#registerScope` / `DefaultListableBeanFactory#registerScope`；配套 Lab：`SpringCoreBeansCustomScopeLabTest`
- 深化策略：
  - 补充 prototype 的关键边界：`AbstractBeanFactory#doGetBean` 对 prototype 的 guard（例如 `isPrototypeCurrentlyInCreation`）以及为什么循环依赖救不了。
  - 增加“scope 注入到 singleton”的对照：`ObjectProvider`/`@Lookup`/scoped proxy 三种策略分别解决什么问题，关键落点类是什么。
  - 补充“销毁语义与资源释放”：prototype 默认不销毁，自定义 scope 如何管理生命周期，如何避免线程/请求上下文泄漏。

### 38. [ ] 29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`
- 现状速记：关键锚点：`AbstractBeanFactory#getObjectForBeanInstance` / `AbstractBeanFactory#getType` / `AbstractBeanFactory#isTypeMatch`；配套 Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 深化策略：
  - 补充 FactoryBean 的类型推断与缓存链路：`getObjectType`/`getTypeForFactoryBean`/`getObjectFromFactoryBean`，解释按类型注入/条件判断为何会受影响。
  - 增加 `&beanName` 的“取 factory vs 取 product”误用清单，并给出在源码里区分两者的最短证据链。
  - 补充与代理/循环依赖的交叉：FactoryBean 产物被代理时，early reference 与最终暴露对象可能不同，如何断言与排障。

### 39. [ ] 30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#autowireConstructor` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#populateBean`；配套 Lab：`SpringCoreBeansInjectionPhaseLabTest`
- 深化策略：
  - 补充“定义层输入归一化主线”：XML/namespace/properties/groovy 最终如何变成 `BeanDefinition` 并注册到 registry。
  - 更深一层讲错误分型：解析错误/语义错误/资源错误分别怎么定位到具体输入片段与关键类。
  - 增加与其他章节的桥接：把定义层输入与后续合并/注入/值解析机制串起来，避免读者割裂。

### 40. [ ] 31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
- 深化策略：
  - 更深一层讲 processor 的“时机与排序算法”：`PriorityOrdered`/`Ordered`/non-ordered 的分组与排序入口，以及为什么要分阶段 invoke/register。
  - 补充“默认基础设施处理器的存在理由”：`ConfigurationClassPostProcessor`/`AutowiredAnnotationBeanPostProcessor`/`CommonAnnotationBeanPostProcessor` 等分别解决什么能力。
  - 增加“排障：没生效/太晚生效/只对部分 bean 生效”：用 refresh 时序 + ordering 规则把问题归因到具体阶段。

### 41. [ ] 32. `@Resource` 注入：为什么它更像“按名称找 Bean”？

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#populateBean` / `AnnotationConfigUtils#registerAnnotationConfigProcessors`；配套 Lab：`SpringCoreBeansResourceInjectionLabTest`
- 深化策略：
  - 补充 `@Resource` 的完整分支树：按名优先、何时回退按类型、与 `@Autowired/@Qualifier` 的冲突与边界。
  - 增加 alias 与 @Resource 交叉：为什么 alias 能/不能被命中（结合 `SimpleAliasRegistry` 证据链）。
  - 补充工程建议：团队内选择 @Resource/@Autowired 的策略与反例。

### 42. [ ] 33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`
- 现状速记：关键锚点：`AnnotationAwareOrderComparator#sort` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `DefaultListableBeanFactory#determineAutowireCandidate`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
- 深化策略：
  - 更深一层讲“注入点信息承载体”：展开 `DependencyDescriptor`（required/annotations/resolvableType/field vs parameter），解释为什么同类型在不同注入点结果不同。
  - 把依赖解析讲成分支树：`DefaultListableBeanFactory#doResolveDependency` 的“快捷路径→resolvableDependencies→候选收集→候选收敛→集合解析→fallback”，并为每分支补一个典型现象/异常。
  - 补充“可扩展点”的真实用法：`AutowireCandidateResolver`/`DependencyComparator`/自定义 Qualifier 的职责边界与落点方法。

### 43. [ ] 34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
- 现状速记：关键锚点：`AbstractApplicationContext#prepareBeanFactory` / `AbstractBeanFactory#resolveEmbeddedValue` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties`；配套 Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`
- 深化策略：
  - 补充 “${} vs #{}” 的职责边界：placeholder resolver 与 expression resolver 各自发生的阶段与落点方法。
  - 更深一层讲 strict/non-strict 来源：默认 embedded value resolver 安装点 vs `PropertySourcesPlaceholderConfigurer` 覆盖点。
  - 增加排障案例：placeholder 未解析/解析为错误类型/SpEL 与 placeholder 混用导致的误归因与修复路径。

### 44. [ ] 35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors` / `AbstractBeanFactory#getMergedLocalBeanDefinition` / `DefaultListableBeanFactory#getMergedBeanDefinition`；配套 Lab：`SpringCoreBeansMergedBeanDefinitionLabTest`
- 深化策略：
  - 补充“注册入口的合流点”：把 scan/@Bean/@Import(programmatic)/XML/Boot auto-config 最终如何汇入 `BeanDefinitionRegistry#registerBeanDefinition` 讲清楚，并明确哪些发生在定义层、哪些发生在实例层。
  - 增加“命名与覆盖的组合问题”：`BeanNameGenerator`/alias/overriding 三者如何叠加导致“看似同名但不是同一个/覆盖发生但实例不变”。
  - 补充“来源可观察性”：如何追踪 `BeanDefinition` 的 source/metadata（资源路径、注解来源、registrar）以便定位“是谁注册的”。
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。

### 45. [ ] 36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyPropertyValues` / `AbstractAutowireCapableBeanFactory#populateBean` / `AbstractBeanFactory#resolveEmbeddedValue`；配套 Lab：`SpringCoreBeansBeansSupportUtilitiesLabTest` / `SpringCoreBeansTypeConversionLabTest`
- 深化策略：
  - 补充 conversion pipeline：`BeanWrapperImpl`/`TypeConverterDelegate`/`ConversionService`/PropertyEditor 的选择顺序与排障入口。
  - 更深一层讲属性路径解析：nested path、auto-grow、集合/Map 属性写入的边界与误区。
  - 增加排障案例：`Failed to convert property value` 如何从 message 映射到具体属性与转换器。

### 46. [ ] 37. 泛型匹配与注入坑：ResolvableType 与代理导致的类型信息丢失

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`
- 现状速记：关键锚点：`BeanDefinition#getResolvableType` / `DefaultListableBeanFactory#doResolveDependency` / `DefaultListableBeanFactory#findAutowireCandidates`；配套 Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 深化策略：
  - 更深一层讲 resolvableDependencies 的匹配细节：按可赋值关系命中、为何不走 `@Qualifier`/候选收敛分支。
  - 补充 `ObjectFactory` 值的解包链路：`AutowireUtils#resolveAutowiringValue` 何时触发、如何在断点里观察。
  - 增加“何时不该用”的反例：把业务对象塞进 resolvableDependencies 导致生命周期/AOP 失效的案例与替代方案。
  - 补充泛型匹配的三个层次：实例类型、目标类型、`ResolvableType` 元数据分别何时被使用。
  - 更深一层讲代理导致的泛型信息缺失：JDK/CGLIB proxy 对可解析类型的影响与规避。

### 47. [ ] 38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#populateBean` / `AbstractBeanFactory#resolveEmbeddedValue` / `ConfigurableEnvironment#getProperty`；配套 Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansProfileRegistrationLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 深化策略：
  - 补充“从异常到证据链”的方法：把高频异常 message 映射到关键方法与关键变量，形成可复用定位套路。
  - 增加“可观察性工具化”：把 BeanDefinition 来源、候选集合、依赖图等信息固化到 dumper/测试输出中，减少靠猜。
  - 补充“调试开关与日志类别”：哪些 logger/category/flag 能把信息压缩到可用范围，避免信息噪声。
  - 补充 PropertySources 的演化时序：哪些在 refresh 前就确定，哪些在运行期才加入（特别是 Boot 环境扩展点）。
  - 更深一层讲 `@PropertySource` 的导入位置：与配置类解析/BeanDefinition 注册的时序关系。

### 48. [ ] 39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#populateBean` / `AbstractBeanFactory#doGetBean`；配套 Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 深化策略：
  - 补充 Boot 自动装配主线的更细粒度角色：import selector/filter/listener/group/sorter 的职责边界与关键落点方法。
  - 更深一层讲“顺序 vs 条件”的关系：排序影响注册先后，条件评估的输入与时机差异如何导致 back-off/覆盖误判。
  - 增加“可断言诊断”：在测试里断言某个 auto-config 是否生效/为何不生效（用 report + beanDefinition 来源做证据）。
  - 更深一层讲最小容器能力边界：哪些能力来自 `DefaultListableBeanFactory`，哪些必须由 ApplicationContext 承接。
  - 补充“容器外对象三段能力”再细化：autowire → initialize → destroy 的最佳实践与常见误用。

### 49. [ ] 第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md`
- 现状速记：关键锚点：`AotServices#factories` / `AotServices.Loader#load` / `ApplicationContext#refresh`；配套 Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
- 深化策略：
  - 补充 AOT 流程角色分工：processors/contributions/generation 的职责边界，避免把 RuntimeHints 当成万能药。
  - 更深一层讲动态能力的缺口分类：反射/代理/资源/序列化分别对应什么典型现象与修复策略。
  - 增加工程化策略：把 AOT 问题前置为 JVM 单测断言（Registrar + 断言），减少“打包阶段撞墙”。

### 50. [ ] 41. RuntimeHints 入门：把构建期契约跑通

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/41-runtimehints-basics.md`
- 现状速记：关键锚点：`Class#getDeclaredMethods` / `ClassLoader#getResource` / `Constructor#newInstance`；配套 Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
- 深化策略：
  - 补充 AOT 流程角色分工：processors/contributions/generation 的职责边界，避免把 RuntimeHints 当成万能药。
  - 更深一层讲动态能力的缺口分类：反射/代理/资源/序列化分别对应什么典型现象与修复策略。
  - 增加工程化策略：把 AOT 问题前置为 JVM 单测断言（Registrar + 断言），减少“打包阶段撞墙”。

### 51. [ ] 42. XML → BeanDefinitionReader：定义层解析与错误分型

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `BeanDefinitionParserDelegate#parseBeanDefinitionElement`；配套 Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 深化策略：
  - 补充“注册入口的合流点”：把 scan/@Bean/@Import(programmatic)/XML/Boot auto-config 最终如何汇入 `BeanDefinitionRegistry#registerBeanDefinition` 讲清楚，并明确哪些发生在定义层、哪些发生在实例层。
  - 增加“命名与覆盖的组合问题”：`BeanNameGenerator`/alias/overriding 三者如何叠加导致“看似同名但不是同一个/覆盖发生但实例不变”。
  - 补充“来源可观察性”：如何追踪 `BeanDefinition` 的 source/metadata（资源路径、注解来源、registrar）以便定位“是谁注册的”。
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。

### 52. [ ] 43. 容器外对象注入：AutowireCapableBeanFactory

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#initializeBean` / `AbstractAutowireCapableBeanFactory#populateBean`；配套 Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- 深化策略：
  - 更深一层讲“注入点信息承载体”：展开 `DependencyDescriptor`（required/annotations/resolvableType/field vs parameter），解释为什么同类型在不同注入点结果不同。
  - 把依赖解析讲成分支树：`DefaultListableBeanFactory#doResolveDependency` 的“快捷路径→resolvableDependencies→候选收集→候选收敛→集合解析→fallback”，并为每分支补一个典型现象/异常。
  - 补充“可扩展点”的真实用法：`AutowireCandidateResolver`/`DependencyComparator`/自定义 Qualifier 的职责边界与落点方法。
  - 更深一层讲最小容器能力边界：哪些能力来自 `DefaultListableBeanFactory`，哪些必须由 ApplicationContext 承接。
  - 补充“容器外对象三段能力”再细化：autowire → initialize → destroy 的最佳实践与常见误用。

### 53. [ ] 44. SpEL 与 `@Value("#{...}")`：表达式解析链路

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md`
- 现状速记：关键锚点：`AbstractBeanFactory#resolveEmbeddedValue` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `BeanFactory#resolveEmbeddedValue`；配套 Lab：`SpringCoreBeansSpelValueLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 深化策略：
  - 补充 “${} vs #{}” 的职责边界：placeholder resolver 与 expression resolver 各自发生的阶段与落点方法。
  - 更深一层讲 strict/non-strict 来源：默认 embedded value resolver 安装点 vs `PropertySourcesPlaceholderConfigurer` 覆盖点。
  - 增加排障案例：placeholder 未解析/解析为错误类型/SpEL 与 placeholder 混用导致的误归因与修复路径。

### 54. [ ] 45. 自定义 Qualifier：meta-annotation 与候选收敛

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md`
- 现状速记：关键锚点：`AutowireCandidateResolver#isAutowireCandidate` / `DefaultListableBeanFactory#determineAutowireCandidate` / `DefaultListableBeanFactory#findAutowireCandidates`；配套 Lab：`SpringCoreBeansCustomQualifierLabTest`
- 深化策略：
  - 更深一层讲“注入点信息承载体”：展开 `DependencyDescriptor`（required/annotations/resolvableType/field vs parameter），解释为什么同类型在不同注入点结果不同。
  - 把依赖解析讲成分支树：`DefaultListableBeanFactory#doResolveDependency` 的“快捷路径→resolvableDependencies→候选收集→候选收敛→集合解析→fallback”，并为每分支补一个典型现象/异常。
  - 补充“可扩展点”的真实用法：`AutowireCandidateResolver`/`DependencyComparator`/自定义 Qualifier 的职责边界与落点方法。

### 55. [ ] 46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/46-xml-namespace-extension.md`
- 现状速记：关键锚点：`BeanDefinitionParser#parse` / `BeanDefinitionParserDelegate#parseCustomElement` / `DefaultBeanDefinitionDocumentReader#parseBeanDefinitions`；配套 Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
- 深化策略：
  - 补充“定义层输入归一化主线”：XML/namespace/properties/groovy 最终如何变成 `BeanDefinition` 并注册到 registry。
  - 更深一层讲错误分型：解析错误/语义错误/资源错误分别怎么定位到具体输入片段与关键类。
  - 增加与其他章节的桥接：把定义层输入与后续合并/注入/值解析机制串起来，避免读者割裂。

### 56. [ ] 47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md`
- 现状速记：关键锚点：`AbstractBeanDefinitionReader#loadBeanDefinitions` / `DefaultListableBeanFactory#registerBeanDefinition` / `GroovyBeanDefinitionReader#loadBeanDefinitions`；配套 Lab：`SpringCoreBeansGroovyBeanDefinitionReaderLabTest` / `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- 深化策略：
  - 补充“注册入口的合流点”：把 scan/@Bean/@Import(programmatic)/XML/Boot auto-config 最终如何汇入 `BeanDefinitionRegistry#registerBeanDefinition` 讲清楚，并明确哪些发生在定义层、哪些发生在实例层。
  - 增加“命名与覆盖的组合问题”：`BeanNameGenerator`/alias/overriding 三者如何叠加导致“看似同名但不是同一个/覆盖发生但实例不变”。
  - 补充“来源可观察性”：如何追踪 `BeanDefinition` 的 source/metadata（资源路径、注解来源、registrar）以便定位“是谁注册的”。
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。

### 57. [ ] 48. 方法注入（Method Injection）：replaced-method / MethodReplacer

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/48-method-injection-replaced-method.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#createBeanInstance` / `AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection` / `AbstractBeanDefinition#getMethodOverrides`；配套 Lab：`SpringCoreBeansReplacedMethodLabTest`
- 深化策略：
  - 补充本章的“反例/误归因对照”：同一现象的不同根因如何在源码分支上被区分。
  - 补充“AOT/Native 风险与替代”：方法注入依赖 CGLIB 子类与方法覆写，在 AOT 场景更脆弱；说明替代方案与迁移策略。
  - 更深一层补“实现机制拆解”：把 `MethodOverride/ReplaceOverride/MethodReplacer` 三者如何在 `createBeanInstance` → `instantiateWithMethodInjection` 里被识别与织入讲透，并说明它与 `@Lookup` 的差异与选型。

### 58. [ ] 49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#getObjectForBeanInstance` / `BeanFactoryUtils#isFactoryDereference`；配套 Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
- 深化策略：
  - 补充 FactoryBean 的类型推断与缓存链路：`getObjectType`/`getTypeForFactoryBean`/`getObjectFromFactoryBean`，解释按类型注入/条件判断为何会受影响。
  - 增加 `&beanName` 的“取 factory vs 取 product”误用清单，并给出在源码里区分两者的最短证据链。
  - 补充与代理/循环依赖的交叉：FactoryBean 产物被代理时，early reference 与最终暴露对象可能不同，如何断言与排障。

### 59. [ ] 50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/50-property-editor-and-value-resolution.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyPropertyValues` / `AbstractBeanFactory#resolveEmbeddedValue` / `AbstractNestablePropertyAccessor#setPropertyValue`；配套 Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`
- 深化策略：
  - 补充“注册入口的合流点”：把 scan/@Bean/@Import(programmatic)/XML/Boot auto-config 最终如何汇入 `BeanDefinitionRegistry#registerBeanDefinition` 讲清楚，并明确哪些发生在定义层、哪些发生在实例层。
  - 增加“命名与覆盖的组合问题”：`BeanNameGenerator`/alias/overriding 三者如何叠加导致“看似同名但不是同一个/覆盖发生但实例不变”。
  - 补充“来源可观察性”：如何追踪 `BeanDefinition` 的 source/metadata（资源路径、注解来源、registrar）以便定位“是谁注册的”。
  - 补充生命周期回调的“来源分型”：JSR-250、接口回调、init/destroy-method、`SmartInitializingSingleton`、`Lifecycle/SmartLifecycle`，并说明它们各自发生的阶段与优先级。
  - 增加“BPP/代理对回调的影响”：回调发生在代理上还是目标上？哪些回调可能被短路/替换？给出可断点证明的路径。

### 60. [ ] 第 25 章：90. 常见坑清单（建议反复对照）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractAutowireCapableBeanFactory#populateBean`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest`
- 深化策略：
  - 补充更贴近真实项目的案例：把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产。
  - 增加索引化能力：支持从术语/异常 message/关键类方法反查到章节与 Lab。
  - 补充团队使用方式：如何用于 onboarding/面试官校准/故障复盘，形成闭环流程。

### 61. [ ] 第 26 章：99. 自测题：你是否真的理解了？

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#initializeBean`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 深化策略：
  - 补充更贴近真实项目的案例：把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产。
  - 增加索引化能力：支持从术语/异常 message/关键类方法反查到章节与 Lab。
  - 补充团队使用方式：如何用于 onboarding/面试官校准/故障复盘，形成闭环流程。

### 62. [ ] 91. 术语表（Glossary）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/91-glossary.md`
- 现状速记：关键锚点：`DefaultListableBeanFactory#registerBeanDefinition` / `DefaultSingletonBeanRegistry#getSingleton`；配套 Lab：`SpringCoreBeansContainerLabTest`
- 深化策略：
  - 补充更贴近真实项目的案例：把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产。
  - 增加索引化能力：支持从术语/异常 message/关键类方法反查到章节与 Lab。
  - 补充团队使用方式：如何用于 onboarding/面试官校准/故障复盘，形成闭环流程。

### 63. [ ] 92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractBeanFactory#resolveEmbeddedValue` / `CachedIntrospectionResults#forClass`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanDefinitionOverridingLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段说明 `BeanDefinition`/singleton caches/processor 列表的变化点，让时间线可在 debugger 里被验证。
  - 增加“同一条主线的分支树”：把关键分支（pre-instantiation/dependsOn/parent/prototype guard 等）用“触发条件→落点方法→观察点”形式展开。
  - 增加“可操作调试脚本”：给出条件断点模板与 watch list，让读者能复盘一次完整主线而不迷路。
  - 补充更贴近真实项目的案例：把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产。
  - 增加索引化能力：支持从术语/异常 message/关键类方法反查到章节与 Lab。

### 64. [ ] 93. 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/93-interview-playbook.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractAutowireCapableBeanFactory#initializeBean`；配套 Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest`
- 深化策略：
  - 补充更贴近真实项目的案例：把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产。
  - 增加索引化能力：支持从术语/异常 message/关键类方法反查到章节与 Lab。
  - 补充团队使用方式：如何用于 onboarding/面试官校准/故障复盘，形成闭环流程。

### 65. [ ] 94. 生产排障清单（Troubleshooting Checklist）：从症状到证据链

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractBeanFactory#doGetBean`；配套 Lab：`SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 深化策略：
  - 补充“从异常到证据链”的方法：把高频异常 message 映射到关键方法与关键变量，形成可复用定位套路。
  - 增加“可观察性工具化”：把 BeanDefinition 来源、候选集合、依赖图等信息固化到 dumper/测试输出中，减少靠猜。
  - 补充“调试开关与日志类别”：哪些 logger/category/flag 能把信息压缩到可用范围，避免信息噪声。
  - 补充更贴近真实项目的案例：把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产。
  - 增加索引化能力：支持从术语/异常 message/关键类方法反查到章节与 Lab。

### 66. [ ] 95. spring-beans Public API 索引（Spring Framework 6.2.15）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/95-spring-beans-public-api-index.md`
- 现状速记：关键锚点：（本章未显式列出）；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest` / `SpringCoreBeansIocBranchMatrixLabTest`
- 深化策略：
  - 补充“索引使用指南”：从需求/问题反推应查的 API 类别（BeanFactory/BeanWrapper/Reader…），并给 3 个示例路径。
  - 增加“API → 机制域 → 章节”回链：让索引不仅能查到类名，还能直接跳到主讲章节与对应 Lab。
  - 补充“版本更新策略”：建议脚本化生成与差异对比，避免手工维护漂移。

### 67. [ ] 96. spring-beans Public API Gap 清单（按包/机制域分批深化）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/96-spring-beans-public-api-gap.md`
- 现状速记：关键锚点：（本章未显式列出）；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest` / `SpringCoreBeansIocBranchMatrixLabTest`
- 深化策略：
  - 补充 gap 的优先级规则：哪些 gap 影响教学闭环（高频机制），哪些可后置（低频工具类）。
  - 增加“按包分批”的实施计划：每批关闭哪些 gap、如何验证（对应章节 + Lab + 断言入口）。
  - 补充“关闭证据”要求：每个 gap 归零必须能指到一个可运行入口或可复现证据链。

### 68. [ ] 97. Explore/Debug 用例（可选启用，不影响默认回归）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `CachedIntrospectionResults#acceptClassLoader` / `CachedIntrospectionResults#forClass`；配套 Lab：（本章未显式列出）
- 深化策略：
  - 补充“从异常到证据链”的方法：把高频异常 message 映射到关键方法与关键变量，形成可复用定位套路。
  - 增加“可观察性工具化”：把 BeanDefinition 来源、候选集合、依赖图等信息固化到 dumper/测试输出中，减少靠猜。
  - 补充“调试开关与日志类别”：哪些 logger/category/flag 能把信息压缩到可用范围，避免信息噪声。
  - 补充更贴近真实项目的案例：把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产。
  - 增加索引化能力：支持从术语/异常 message/关键类方法反查到章节与 Lab。

### 69. [ ] 98. Debugger Pack（断点包总入口）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest` / `SpringCoreBeansIocBranchMatrixLabTest`
- 深化策略：
  - 补充“断点包分组策略”：按阶段/机制/异常三视角分组，让 IDE 断点组可以直接复用。
  - 增加“watch list 速查表”：高频变量表达式（descriptor/mbd/pvs/singleton caches/beanPostProcessors）按场景给出。
  - 补充“断点成本提醒”：哪些断点非常热必须加条件，避免新手把 IDE 卡死。

### 70. [ ] 99. 团队内训讲义（Training Kit）：可直接开讲的课时脚本

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#populateBean` / `DefaultListableBeanFactory#determineAutowireCandidate`；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansMainlineCallChainLabTest`
- 深化策略：
  - 增加“课后作业与验收题库”：按课时给出必须跑的 Lab 与必须能复述的证据链（含追问）。
  - 补充“讲师手册”：每节课建议故事线、容易讲错的点、现场演示断点脚本（可直接照读）。
  - 增加“团队落地机制”：把教材纳入 onboarding/晋升/面试官校准的流程与节奏建议。

## 贯穿式支撑任务（每批迭代都要做）

- [ ] 安全与合规自检：确保新增示例/脚本不引入明文密钥，不建议高风险生产操作。
- [ ] 交叉一致性回归：互链标题、术语一致性、重复内容收敛到主讲章节。
- [ ] 可运行回归策略：若本批涉及 Lab/Test 变更，运行 `spring-core-beans` 模块测试并记录结果。
