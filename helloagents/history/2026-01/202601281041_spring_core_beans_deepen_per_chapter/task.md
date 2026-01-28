# task：spring-core-beans docs 全章逐章深化策略

> 说明：这里不设“统一固定标准”。每章只给出围绕该章主题的具体深化动作（机制/边界/真实场景），避免纯格式性改动。

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
  - 补充“源码进阶读法”：告诉读者每章要盯的关键类/关键方法，以及如何在 IDE 里用断点把它跑通。
  - 补充“问题驱动索引”：按常见现象/异常把读者送到对应章节（并标注最短证据链入口方法）。
  - 补充“核心对象词典”：用 1 页把 BeanDefinition/merged BD/early reference/exposed object 等对象关系讲清楚，避免读者在术语上耗时。

### 02. [√] 第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/010-03-mainline-timeline.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#finishRefresh` / `AbstractApplicationContext#prepareBeanFactory`；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansMainlineCallChainLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段明确哪些数据结构会变化（definition registry / singleton caches / processor 列表），并指出如何在断点里验证。
  - 补充“分支判断最小集”：把主线中的高频分支（prototype/dependsOn/parent/FactoryBean/type match）用触发条件+必看变量说明清楚。
  - 补充“证据链样例”：以 `AbstractApplicationContext#finishBeanFactoryInitialization` 为例，给出 1 次完整的“现象→断点→变量→结论”示范。

### 03. [√] 第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanFactoryApiLabTest`
- 深化策略：
  - 补充“最小心智模型解释”：用 3–5 个关键对象/方法把 IoC 的因果链串起来，避免只记名词。
  - 补充“新手易卡点的修复路径”：断点不命中/测试过慢/IDE 卡死等问题的最短排障策略。
  - 补充“学习闭环”：围绕 `SpringCoreBeansAutowireCandidateSelectionLabTest` 说明跑完应当得到的 3 个可复述结论（每个结论对应一个方法级证据点）。

### 04. [√] 第 11 章：关键分支矩阵（Branch Decision Matrix）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md`
- 现状速记：关键锚点：`AbstractBeanFactory#resolveEmbeddedValue` / `CommonAnnotationBeanPostProcessor#postProcessProperties` / `DefaultListableBeanFactory#doResolveDependency`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段明确哪些数据结构会变化（definition registry / singleton caches / processor 列表），并指出如何在断点里验证。
  - 补充“分支判断最小集”：把主线中的高频分支（prototype/dependsOn/parent/FactoryBean/type match）用触发条件+必看变量说明清楚。
  - 补充“证据链样例”：以 `AbstractBeanFactory#resolveEmbeddedValue` 为例，给出 1 次完整的“现象→断点→变量→结论”示范。

### 05. [√] 第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#initializeBean` / `AbstractBeanFactory#doGetBean`；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansMainlineCallChainLabTest`
- 深化策略：
  - 补充“最小心智模型解释”：用 3–5 个关键对象/方法把 IoC 的因果链串起来，避免只记名词。
  - 补充“新手易卡点的修复路径”：断点不命中/测试过慢/IDE 卡死等问题的最短排障策略。
  - 补充“学习闭环”：围绕 `SpringCoreBeansBreakpointPackLabTest` 说明跑完应当得到的 3 个可复述结论（每个结论对应一个方法级证据点）。

### 06. [√] 第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansContainerLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段明确哪些数据结构会变化（definition registry / singleton caches / processor 列表），并指出如何在断点里验证。
  - 补充“分支判断最小集”：把主线中的高频分支（prototype/dependsOn/parent/FactoryBean/type match）用触发条件+必看变量说明清楚。
  - 补充“证据链样例”：以 `AbstractApplicationContext#refresh` 为例，给出 1 次完整的“现象→断点→变量→结论”示范。

### 07. [√] 第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 深化策略：
  - 补充“阶段内关键对象变化”：每个阶段明确哪些数据结构会变化（definition registry / singleton caches / processor 列表），并指出如何在断点里验证。
  - 补充“分支判断最小集”：把主线中的高频分支（prototype/dependsOn/parent/FactoryBean/type match）用触发条件+必看变量说明清楚。
  - 补充“证据链样例”：以 `AbstractApplicationContext#refresh` 为例，给出 1 次完整的“现象→断点→变量→结论”示范。

### 08. [√] 第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- 现状速记：关键锚点：`ApplicationContext#refresh` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `ConstructorResolver#autowireConstructor`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 深化策略：
  - 补充“DependencyDescriptor 深挖”：required/annotations/resolvableType/field vs parameter 等字段如何影响解析结果，并给出 2 个对照注入点。
  - 把依赖解析写成“分支树”：快捷路径（Optional/Provider/@Lazy/@Value）→ resolvableDependencies → 候选收集 → 候选收敛 → 集合解析/排序 → fallback，每个分支给一个典型现象。
  - 补充“关键变量解释”：围绕 `ApplicationContext#refresh`，解释 candidates/primary/qualifier/dependencyName 等变量各自的含义与决策地位。
  - 针对“机制主线：候选收集 → 候选收敛 → 最终注入”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansAutowireCandidateSelectionLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 09. [√] 第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `ApplicationContext#refresh` / `ConfigurableBeanFactory#destroyBean`；配套 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansCustomScopeLabTest` / `SpringCoreBeansLabTest`
- 深化策略：
  - 补充 prototype 的关键边界：prototype 的创建 guard、循环依赖为何不可救、以及 scope 与 singleton 缓存的根本差异。
  - 补充“把 scope 注入到 singleton”的三种方案对照：ObjectProvider/@Lookup/scoped proxy，各自的优缺点与落点类。
  - 补充“销毁语义”：prototype 默认不销毁；自定义 scope 如何回收；如何避免线程/请求上下文泄漏。
  - 针对“1. singleton vs prototype：到底“一”指什么？”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansContainerLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 10. [√] 第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- 现状速记：关键锚点：`AbstractApplicationContext#close` / `AbstractApplicationContext#doClose` / `AbstractApplicationContext#refresh`；配套 Lab：`SpringCoreBeansAwareInfrastructureLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest`
- 深化策略：
  - 补充“回调来源分型”：JSR-250、接口回调、init/destroy-method、SmartInitializingSingleton、Lifecycle/SmartLifecycle 的触发时机与优先级。
  - 补充“回调与代理交织”：回调发生在代理还是目标？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 针对“1. 源码级生命周期骨架：把顺序落到关键方法”补细：把本段涉及的分支条件/关键变量补齐到能推导结论的程度。
  - 补充可复现闭环：以 `SpringCoreBeansAwareInfrastructureLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 11. [√] 第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`；配套 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
- 深化策略：
  - 补充“processor 时机+排序算法”：PriorityOrdered/Ordered/无序的分组与排序入口，为什么要分阶段 invoke/register。
  - 补充“默认基础设施处理器的存在理由”：哪些处理器负责让注解生效、哪些只影响定义层、哪些影响实例层。
  - 针对“1. BFPP：`BeanFactoryPostProcessor`”补细：用 1 个具体例子说明该分支如何改变最终行为（最好能在 Lab 里跑出差异）。
  - 补充可复现闭环：以 `SpringCoreBeansContainerLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 12. [√] 第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `ApplicationContext#refresh` / `BeanMethodInterceptor#intercept`；配套 Lab：`SpringCoreBeansContainerLabTest`
- 深化策略：
  - 补充配置类解析主线：ConfigurationClassPostProcessor 如何解析/注册 @Bean/@Import，哪些发生在定义层、哪些发生在实例层。
  - 补充增强机制细节：ConfigurationClassEnhancer 如何把 @Bean 方法调用变成 getBean（并解释 proxyBeanMethods=false 的行为差异）。
  - 补充高频坑：跨配置类调用、自调用、final/private 限制、循环依赖与代理交织等场景的可推导结论。
  - 针对“1. 两种配置方式的核心差异”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansContainerLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 13. [√] 02. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyPropertyValues` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansComponentScanLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansImportLabTest`
- 深化策略：
  - 针对“章节验收口径（10/30/3：教程化闭环）”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansComponentScanLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。
  - 补充“关键分支解释”：围绕 `AbstractApplicationContext#refresh`，把本章最重要的 if/then 分支写成可推导结论，并给出必看变量。

### 14. [√] 第 20 章：01. Bean 心智模型：从 BeanDefinition 到最终暴露对象

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `ApplicationContext#refresh` / `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 深化策略：
  - 针对“机制主线：三层模型 + 一个“最终对象”概念”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansBeanCreationTraceLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。
  - 补充“关键分支解释”：围绕 `AbstractApplicationContext#refresh`，把本章最重要的 if/then 分支写成可推导结论，并给出必看变量。

### 15. [√] 08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#getObjectForBeanInstance` / `AbstractBeanFactory#isTypeMatch`；配套 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 深化策略：
  - 补充类型推断与缓存链路：getObjectType/getTypeForFactoryBean/getObjectFromFactoryBean 如何影响按类型查找、条件判断与注入。
  - 补充“& 前缀”的证据链：在源码里区分取 factory vs 取 product 的最短路径与常见误用。
  - 补充与代理/循环依赖的交叉：FactoryBean 产物与 early reference 的一致性边界，如何断言与排障。
  - 针对“1. `FactoryBean` 的核心语义”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansContainerLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 16. [√] 09. 循环依赖：现象、原因与规避（constructor vs setter）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractAutowireCapableBeanFactory#populateBean`；配套 Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 深化策略：
  - 把“可救/不可救”分类讲透：setter 环、constructor 环、prototype 环、dependsOn 环，各自 fail-fast 点与原因。
  - 补充 early reference 细节：三级缓存 + SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference 如何让代理介入。
  - 补充排障配方：如何定位环路边、选择打断手段（@Lazy/ObjectProvider/重构依赖）并说明取舍。
  - 针对“机制主线：为什么 constructor 死、setter 有时能活？”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansCircularDependencyBoundaryLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 17. [√] 第 19 章：11. 调试与自检：如何“看见”容器正在做什么

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutoProxyCreator#postProcessAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`；配套 Lab：`SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 深化策略：
  - 补充 Boot 自动装配的角色分工：导入清单/排序/条件评估/注册定义的关键类与落点方法。
  - 补充“顺序 vs 条件”的因果：排序影响注册先后，条件评估时机差异如何导致 back-off/覆盖误判。
  - 补充可断言诊断：用测试断言某个 auto-config 是否生效/为何不生效（report + definition source 证据）。
  - 针对“0. 观测对象总览：你其实只是在看 5 类东西”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansAutoConfigurationLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 18. [√] 09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md`
- 现状速记：关键锚点：`AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `ConfigurationClassPostProcessor#processConfigBeanDefinitions`；配套 Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest`
- 深化策略：
  - 补充 Boot 自动装配的角色分工：导入清单/排序/条件评估/注册定义的关键类与落点方法。
  - 补充“顺序 vs 条件”的因果：排序影响注册先后，条件评估时机差异如何导致 back-off/覆盖误判。
  - 补充可断言诊断：用测试断言某个 auto-config 是否生效/为何不生效（report + definition source 证据）。
  - 针对“机制主线：顺序不定义，就会“看起来像偶发””补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 19. [√] 第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `ApplicationContext#refresh` / `ApplicationContextRunner#run`；配套 Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` / `SpringCoreBeansAutoConfigurationLabTest`
- 深化策略：
  - 补充 Boot 自动装配的角色分工：导入清单/排序/条件评估/注册定义的关键类与落点方法。
  - 补充“顺序 vs 条件”的因果：排序影响注册先后，条件评估时机差异如何导致 back-off/覆盖误判。
  - 补充可断言诊断：用测试断言某个 auto-config 是否生效/为何不生效（report + definition source 证据）。
  - 针对“1. 先说结论：Boot 做了什么？”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 20. [√] 第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansResourceInjectionLabTest`
- 深化策略：
  - 补充“processor 时机+排序算法”：PriorityOrdered/Ordered/无序的分组与排序入口，为什么要分阶段 invoke/register。
  - 补充“默认基础设施处理器的存在理由”：哪些处理器负责让注解生效、哪些只影响定义层、哪些影响实例层。
  - 针对“1. 现象：同样是 Spring 容器，不同启动方式结果不一样”补细：用 1 个具体例子说明该分支如何改变最终行为（最好能在 Lab 里跑出差异）。
  - 补充可复现闭环：以 `SpringCoreBeansBootstrapInternalsLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 21. [√] 13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansRegistryPostProcessorLabTest`
- 深化策略：
  - 针对“1. 心智模型：先有“定义”，后有“实例””补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansRegistryPostProcessorLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。
  - 补充“关键分支解释”：围绕 `AbstractApplicationContext#refresh`，把本章最重要的 if/then 分支写成可推导结论，并给出必看变量。

### 22. [√] 14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`；配套 Lab：`SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
- 深化策略：
  - 补充“processor 时机+排序算法”：PriorityOrdered/Ordered/无序的分组与排序入口，为什么要分阶段 invoke/register。
  - 补充“默认基础设施处理器的存在理由”：哪些处理器负责让注解生效、哪些只影响定义层、哪些影响实例层。
  - 针对“1. 规则总览（记住这三层就够）”补细：用 1 个具体例子说明该分支如何改变最终行为（最好能在 Lab 里跑出差异）。
  - 补充可复现闭环：以 `SpringCoreAopMultiProxyStackingLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 23. [√] 15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation` / `AbstractAutowireCapableBeanFactory#createBean` / `AbstractAutowireCapableBeanFactory#doCreateBean`；配套 Lab：`SpringCoreBeansPreInstantiationLabTest`
- 深化策略：
  - 补充“processor 时机+排序算法”：PriorityOrdered/Ordered/无序的分组与排序入口，为什么要分阶段 invoke/register。
  - 补充“默认基础设施处理器的存在理由”：哪些处理器负责让注解生效、哪些只影响定义层、哪些影响实例层。
  - 针对“1. 现象：构造器抛异常会让 refresh 直接失败”补细：用 1 个具体例子说明该分支如何改变最终行为（最好能在 Lab 里跑出差异）。
  - 补充可复现闭环：以 `SpringCoreBeansPreInstantiationLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 24. [√] 16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `DefaultSingletonBeanRegistry#addSingletonFactory`；配套 Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 深化策略：
  - 把“可救/不可救”分类讲透：setter 环、constructor 环、prototype 环、dependsOn 环，各自 fail-fast 点与原因。
  - 补充 early reference 细节：三级缓存 + SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference 如何让代理介入。
  - 补充排障配方：如何定位环路边、选择打断手段（@Lazy/ObjectProvider/重构依赖）并说明取舍。
  - 针对“机制主线：early reference 的“时机”与“形态””补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansCircularDependencyBoundaryLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 25. [√] 17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`
- 现状速记：关键锚点：`AbstractApplicationContext#doClose` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#initializeBean`；配套 Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest`
- 深化策略：
  - 补充“回调来源分型”：JSR-250、接口回调、init/destroy-method、SmartInitializingSingleton、Lifecycle/SmartLifecycle 的触发时机与优先级。
  - 补充“回调与代理交织”：回调发生在代理还是目标？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 针对“1. 一个可断言的顺序（比看日志更可靠）”补细：把本段涉及的分支条件/关键变量补齐到能推导结论的程度。
  - 补充可复现闭环：以 `SpringCoreBeansBootstrapInternalsLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 26. [√] 18. 从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest`
- 深化策略：
  - 针对“0. 先把“主线地图”记住：容器做两件事”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansBeanCreationTraceLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。
  - 补充“关键分支解释”：围绕 `AbstractApplicationContext#finishBeanFactoryInitialization`，把本章最重要的 if/then 分支写成可推导结论，并给出必看变量。

### 27. [ ] 第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#createBean` / `AbstractBeanFactory#doGetBean` / `ApplicationContext#refresh`；配套 Lab：`SpringCoreBeansLazyLabTest`
- 深化策略：
  - 补充“回调来源分型”：JSR-250、接口回调、init/destroy-method、SmartInitializingSingleton、Lifecycle/SmartLifecycle 的触发时机与优先级。
  - 补充“回调与代理交织”：回调发生在代理还是目标？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 针对“1. lazy-init bean：refresh 阶段不创建”补细：把本段涉及的分支条件/关键变量补齐到能推导结论的程度。
  - 补充可复现闭环：以 `SpringCoreBeansLazyLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 28. [√] 19. dependsOn：强制初始化顺序（即使没有显式依赖）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-depends-on.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractBeanFactory#doGetBean` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties`；配套 Lab：`SpringCoreBeansDependsOnLabTest`
- 深化策略：
  - 补充 dependsOn 与 lifecycle/phase 的选型边界：什么时候该用 SmartLifecycle phase，什么时候只能用 dependsOn。
  - 补充依赖图调试：如何用 dependentBeanMap/dependenciesForBeanMap 复盘“启动顺序/销毁顺序/环路”。
  - 补充父子容器边界：层级 context 下 name 解析与可见性的表现（并给一个对照案例）。
  - 针对“机制主线：它解决的是“顺序”，不是“注入””补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansDependsOnLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 29. [√] 20. registerResolvableDependency：能注入，但它不是 Bean

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md`
- 现状速记：关键锚点：`AbstractApplicationContext#prepareBeanFactory` / `AbstractAutowireCapableBeanFactory#invokeAwareMethods` / `AutowireUtils#resolveAutowiringValue`；配套 Lab：`SpringCoreBeansResolvableDependencyLabTest`
- 深化策略：
  - 补充“DependencyDescriptor 深挖”：required/annotations/resolvableType/field vs parameter 等字段如何影响解析结果，并给出 2 个对照注入点。
  - 把依赖解析写成“分支树”：快捷路径（Optional/Provider/@Lazy/@Value）→ resolvableDependencies → 候选收集 → 候选收敛 → 集合解析/排序 → fallback，每个分支给一个典型现象。
  - 补充“关键变量解释”：围绕 `AbstractApplicationContext#prepareBeanFactory`，解释 candidates/primary/qualifier/dependencyName 等变量各自的含义与决策地位。
  - 针对“机制主线：它是“可解析依赖”，不是“可获取 Bean””补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansResolvableDependencyLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 30. [√] 21. 父子 ApplicationContext：可见性与覆盖边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-context-hierarchy.md`
- 现状速记：关键锚点：`AbstractApplicationContext#getParent` / `AbstractApplicationContext#setParent` / `AbstractBeanFactory#containsBean`；配套 Lab：`SpringCoreBeansContextHierarchyLabTest`
- 深化策略：
  - 补充层级查找算法：按名/按类型在父子容器中的优先级与回退规则（并指出关键入口方法）。
  - 补充覆盖/隔离的工程语义：父子容器同名/同类型 bean 如何影响注入与排障。
  - 补充典型场景：root/child context 下“为什么某个 bean 只在一侧可见”的可推导解释。
  - 针对“1. 现象：child 能看到 parent，parent 看不到 child”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansContextHierarchyLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 31. [√] 22. Bean 名称与 alias：同一个实例，多一个名字

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#transformedBeanName` / `BeanDefinitionReaderUtils#generateBeanName`；配套 Lab：`SpringCoreBeansBeanNameAliasLabTest`
- 深化策略：
  - 补充 alias 的底层语义：SimpleAliasRegistry 的 alias→canonicalName 映射与覆盖规则。
  - 补充名字参与注入的入口集合：@Resource/by-name fallback/qualifier value 与 beanName 的交叉。
  - 补充工程建议：如何设计稳定 beanName/alias，避免重构时注入被破坏（给反例）。
  - 针对“1. 现象：两个名字拿到的是同一个对象”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansBeanNameAliasLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 32. [√] 23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#getObjectForBeanInstance` / `AbstractBeanFactory#getType`；配套 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 深化策略：
  - 补充类型推断与缓存链路：getObjectType/getTypeForFactoryBean/getObjectFromFactoryBean 如何影响按类型查找、条件判断与注入。
  - 补充“& 前缀”的证据链：在源码里区分取 factory vs 取 product 的最短路径与常见误用。
  - 补充与代理/循环依赖的交叉：FactoryBean 产物与 early reference 的一致性边界，如何断言与排障。
  - 针对“1. 最重要的规则：`&` 前缀”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansContainerLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 33. [√] 24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md`
- 现状速记：关键锚点：`BeanDefinitionOverrideException#getBeanName` / `DefaultListableBeanFactory#doResolveDependency` / `DefaultListableBeanFactory#getBeanDefinition`；配套 Lab：`SpringCoreBeansBeanDefinitionOriginLabTest` / `SpringCoreBeansBeanDefinitionOverridingLabTest`
- 深化策略：
  - 补充覆盖语义来源：allowOverriding/Boot 配置如何影响 DefaultListableBeanFactory 行为。
  - 补充“定义层覆盖 vs 实例缓存”：覆盖替换 BeanDefinition，不会回滚已创建单例对象（建议给出对照）。
  - 补充排障：覆盖了但注入仍旧/覆盖没发生，如何用注册顺序+definition source+singleton caches 证明。
  - 针对“1. allowBeanDefinitionOverriding=true：最后一个 wins”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansBeanDefinitionOriginLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 34. [√] 25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`；配套 Lab：`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
- 深化策略：
  - 补充“processor 时机+排序算法”：PriorityOrdered/Ordered/无序的分组与排序入口，为什么要分阶段 invoke/register。
  - 补充“默认基础设施处理器的存在理由”：哪些处理器负责让注解生效、哪些只影响定义层、哪些影响实例层。
  - 针对“机制主线：两条注册路径 + 一个“不可逆”事实”补细：用 1 个具体例子说明该分支如何改变最终行为（最好能在 Lab 里跑出差异）。
  - 补充可复现闭环：以 `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 35. [√] 26. SmartInitializingSingleton：所有单例都创建完之后再做事

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractBeanFactory#doGetBean`；配套 Lab：`SpringCoreBeansSmartInitializingSingletonLabTest`
- 深化策略：
  - 补充“回调来源分型”：JSR-250、接口回调、init/destroy-method、SmartInitializingSingleton、Lifecycle/SmartLifecycle 的触发时机与优先级。
  - 补充“回调与代理交织”：回调发生在代理还是目标？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 针对“1. 现象：回调发生在“非 lazy 单例创建完成之后””补细：把本段涉及的分支条件/关键变量补齐到能推导结论的程度。
  - 补充可复现闭环：以 `SpringCoreBeansSmartInitializingSingletonLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 36. [√] 27. SmartLifecycle：start/stop 时机与 phase 顺序

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishRefresh` / `DefaultLifecycleProcessor#onRefresh` / `DefaultLifecycleProcessor#startBeans`；配套 Lab：`SpringCoreBeansSmartLifecycleLabTest`
- 深化策略：
  - 补充“回调来源分型”：JSR-250、接口回调、init/destroy-method、SmartInitializingSingleton、Lifecycle/SmartLifecycle 的触发时机与优先级。
  - 补充“回调与代理交织”：回调发生在代理还是目标？哪些回调可能被短路/替换？给出可断点证明的路径。
  - 针对“1. 现象：start 按 phase 升序，stop 反向”补细：把本段涉及的分支条件/关键变量补齐到能推导结论的程度。
  - 补充可复现闭环：以 `SpringCoreBeansSmartLifecycleLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 37. [√] 28. 自定义 Scope + scoped proxy：thread scope 的真实语义

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#registerScope` / `DefaultListableBeanFactory#registerScope`；配套 Lab：`SpringCoreBeansCustomScopeLabTest`
- 深化策略：
  - 补充 prototype 的关键边界：prototype 的创建 guard、循环依赖为何不可救、以及 scope 与 singleton 缓存的根本差异。
  - 补充“把 scope 注入到 singleton”的三种方案对照：ObjectProvider/@Lookup/scoped proxy，各自的优缺点与落点类。
  - 补充“销毁语义”：prototype 默认不销毁；自定义 scope 如何回收；如何避免线程/请求上下文泄漏。
  - 针对“1. 注册自定义 scope（thread）”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansCustomScopeLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 38. [√] 29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`
- 现状速记：关键锚点：`AbstractBeanFactory#getObjectForBeanInstance` / `AbstractBeanFactory#getType` / `AbstractBeanFactory#isTypeMatch`；配套 Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 深化策略：
  - 补充类型推断与缓存链路：getObjectType/getTypeForFactoryBean/getObjectFromFactoryBean 如何影响按类型查找、条件判断与注入。
  - 补充“& 前缀”的证据链：在源码里区分取 factory vs 取 product 的最短路径与常见误用。
  - 补充与代理/循环依赖的交叉：FactoryBean 产物与 early reference 的一致性边界，如何断言与排障。
  - 针对“1. 现象：getBeanNamesForType(..., allowEagerInit=false) 找不到 unknownValue”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansFactoryBeanEdgeCasesLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 39. [√] 30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#autowireConstructor` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#populateBean`；配套 Lab：`SpringCoreBeansInjectionPhaseLabTest`
- 深化策略：
  - 补充“DependencyDescriptor 深挖”：required/annotations/resolvableType/field vs parameter 等字段如何影响解析结果，并给出 2 个对照注入点。
  - 把依赖解析写成“分支树”：快捷路径（Optional/Provider/@Lazy/@Value）→ resolvableDependencies → 候选收集 → 候选收敛 → 集合解析/排序 → fallback，每个分支给一个典型现象。
  - 补充“关键变量解释”：围绕 `AbstractAutowireCapableBeanFactory#autowireConstructor`，解释 candidates/primary/qualifier/dependencyName 等变量各自的含义与决策地位。
  - 针对“1. 现象：field injection 在构造器里拿不到依赖”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansInjectionPhaseLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 40. [√] 31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`；配套 Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
- 深化策略：
  - 补充代理产生的三大替换点：before-instantiation short-circuit / early reference / after-initialization，并明确各自的适用处理器类型。
  - 补充 self-invocation 的可推导解释：为什么代理存在但方法没进 advice，断点该打在哪里证明。
  - 补充“时机问题”诊断：bean 创建过早错过 BPP 的典型触发点与修复手段。
  - 针对“机制主线：容器允许“换对象””补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansBeanCreationTraceLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 41. [√] 32. `@Resource` 注入：为什么它更像“按名称找 Bean”？

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#populateBean` / `AnnotationConfigUtils#registerAnnotationConfigProcessors`；配套 Lab：`SpringCoreBeansResourceInjectionLabTest`
- 深化策略：
  - 补充“DependencyDescriptor 深挖”：required/annotations/resolvableType/field vs parameter 等字段如何影响解析结果，并给出 2 个对照注入点。
  - 把依赖解析写成“分支树”：快捷路径（Optional/Provider/@Lazy/@Value）→ resolvableDependencies → 候选收集 → 候选收敛 → 集合解析/排序 → fallback，每个分支给一个典型现象。
  - 补充“关键变量解释”：围绕 `AbstractAutowireCapableBeanFactory#doCreateBean`，解释 candidates/primary/qualifier/dependencyName 等变量各自的含义与决策地位。
  - 针对“机制主线：`@Resource` 的三个关键事实”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansResourceInjectionLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 42. [√] 33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`
- 现状速记：关键锚点：`AnnotationAwareOrderComparator#sort` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `DefaultListableBeanFactory#determineAutowireCandidate`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
- 深化策略：
  - 补充“DependencyDescriptor 深挖”：required/annotations/resolvableType/field vs parameter 等字段如何影响解析结果，并给出 2 个对照注入点。
  - 把依赖解析写成“分支树”：快捷路径（Optional/Provider/@Lazy/@Value）→ resolvableDependencies → 候选收集 → 候选收敛 → 集合解析/排序 → fallback，每个分支给一个典型现象。
  - 补充“关键变量解释”：围绕 `AnnotationAwareOrderComparator#sort`，解释 candidates/primary/qualifier/dependencyName 等变量各自的含义与决策地位。
  - 针对“机制主线：先问“注入的是一个，还是一组？””补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansAutowireCandidateSelectionLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 43. [√] 34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
- 现状速记：关键锚点：`AbstractApplicationContext#prepareBeanFactory` / `AbstractBeanFactory#resolveEmbeddedValue` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties`；配套 Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`
- 深化策略：
  - 补充 “${} vs #{}” 的职责边界：placeholder resolver 与 expression resolver 各自发生的阶段与落点方法。
  - 补充 strict/non-strict 的来源与差异：默认 embedded value resolver vs PropertySourcesPlaceholderConfigurer 的覆盖。
  - 补充排障案例：placeholder 未解析/解析为错误类型/SpEL 混用导致误归因的修复路径。
  - 针对“机制主线：`@Value` 严不严格，取决于 resolver”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansValuePlaceholderResolutionLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 44. [√] 35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors` / `AbstractBeanFactory#getMergedLocalBeanDefinition` / `DefaultListableBeanFactory#getMergedBeanDefinition`；配套 Lab：`SpringCoreBeansMergedBeanDefinitionLabTest`
- 深化策略：
  - 补充合并算法细节：child/parent BeanDefinition 的合并规则与缓存位置，为什么你在 registry 里看到的不是最终形态。
  - 补充与 MergedBeanDefinitionPostProcessor 的交互：它何时被调用、能改什么、不能改什么。
  - 补充排障案例：为什么你改了 BD 但创建时仍“像没改”（用 merged BD 证据链解释）。
  - 针对“2. merged 到底“合并”了什么？”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansMergedBeanDefinitionLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 45. [√] 36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyPropertyValues` / `AbstractAutowireCapableBeanFactory#populateBean` / `AbstractBeanFactory#resolveEmbeddedValue`；配套 Lab：`SpringCoreBeansBeansSupportUtilitiesLabTest` / `SpringCoreBeansTypeConversionLabTest`
- 深化策略：
  - 补充 conversion pipeline：BeanWrapper/TypeConverterDelegate/ConversionService/PropertyEditor 的选择顺序与排障入口。
  - 补充属性路径解析：nested path、集合/Map 属性写入、auto-grow 的边界与误区。
  - 补充异常定位：Failed to convert property value 如何映射到具体属性路径与转换器。
  - 针对“机制主线：两条链路 + 一个决策点”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansBeansSupportUtilitiesLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 46. [√] 37. 泛型匹配与注入坑：ResolvableType 与代理导致的类型信息丢失

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`
- 现状速记：关键锚点：`BeanDefinition#getResolvableType` / `DefaultListableBeanFactory#doResolveDependency` / `DefaultListableBeanFactory#findAutowireCandidates`；配套 Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 深化策略：
  - 补充泛型匹配三层视角：实例类型/目标类型/ResolvableType 元数据分别何时被使用。
  - 补充代理导致的泛型信息缺失：JDK/CGLIB proxy 对可解析类型的影响与规避策略。
  - 补充工程建议：如何用接口注入+qualifier+显式命名降低泛型歧义。
  - 针对“0. 先建立一个“排障口径”：候选类型信息的三大来源”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 47. [√] 38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#populateBean` / `AbstractBeanFactory#resolveEmbeddedValue` / `ConfigurableEnvironment#getProperty`；配套 Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansProfileRegistrationLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 深化策略：
  - 补充 PropertySources 演化时序：哪些在 refresh 前确定，哪些在运行期才加入（特别是 Boot 环境扩展）。
  - 补充 @PropertySource 的导入位置：与配置类解析/BeanDefinition 注册的时序关系。
  - 补充排障案例：属性覆盖不生效/生效太晚/只对部分 bean 生效，如何用时序与证据链证明。
  - 针对“1. 是什么：Environment 抽象解决的是什么问题？”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansEnvironmentPropertySourceLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 48. [√] 39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#populateBean` / `AbstractBeanFactory#doGetBean`；配套 Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 深化策略：
  - 补充最小容器边界：哪些能力来自 DefaultListableBeanFactory，哪些必须由 ApplicationContext 承接。
  - 补充容器外对象三段能力的细节：autowire/initialize/destroy 的正确用法与常见误用。
  - 补充集成案例：在非 Spring 管理对象上复用注入/初始化为何常失败，如何用证据链定位。
  - 针对“1. 是什么：BeanFactory 在 Spring 体系里的位置”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansBeanFactoryApiLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 49. [√] 第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md`
- 现状速记：关键锚点：`AotServices#factories` / `AotServices.Loader#load` / `ApplicationContext#refresh`；配套 Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
- 深化策略：
  - 补充 AOT 流程的角色分工：哪些问题靠 RuntimeHints 解决，哪些需要别的 AOT 贡献（避免把它当万能药）。
  - 补充缺口分类：反射/代理/资源/序列化分别对应什么现象与修复策略（并给出最小断言方法）。
  - 补充工程化策略：把 native 风险前置为 JVM 单测断言（Registrar + 断言），减少打包阶段撞墙。
  - 针对“1. 结论先行：AOT/Native 改变了什么？”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansAotFactoriesLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 50. [√] 41. RuntimeHints 入门：把构建期契约跑通

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/41-runtimehints-basics.md`
- 现状速记：关键锚点：`Class#getDeclaredMethods` / `ClassLoader#getResource` / `Constructor#newInstance`；配套 Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
- 深化策略：
  - 补充 AOT 流程的角色分工：哪些问题靠 RuntimeHints 解决，哪些需要别的 AOT 贡献（避免把它当万能药）。
  - 补充缺口分类：反射/代理/资源/序列化分别对应什么现象与修复策略（并给出最小断言方法）。
  - 补充工程化策略：把 native 风险前置为 JVM 单测断言（Registrar + 断言），减少打包阶段撞墙。
  - 针对“机制主线：把“运行期能力需求”前置成“构建期契约””补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansAotRuntimeHintsLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 51. [√] 42. XML → BeanDefinitionReader：定义层解析与错误分型

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `BeanDefinitionParserDelegate#parseBeanDefinitionElement`；配套 Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 深化策略：
  - 补充“定义层输入归一化主线”：XML/namespace/properties/groovy 如何变成 BeanDefinition 并注册到 registry。
  - 补充错误分型：解析错误/语义错误/资源错误分别如何定位到具体输入片段与关键类。
  - 针对“1. 结论先行：XML 的价值不在“写法”，而在“链路””补细：把该解析分支的关键对象（reader/parser/delegate）与关键数据结构补齐。
  - 补充可复现闭环：以 `SpringCoreBeansXmlBeanDefinitionReaderLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 52. [√] 43. 容器外对象注入：AutowireCapableBeanFactory

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#initializeBean` / `AbstractAutowireCapableBeanFactory#populateBean`；配套 Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- 深化策略：
  - 补充最小容器边界：哪些能力来自 DefaultListableBeanFactory，哪些必须由 ApplicationContext 承接。
  - 补充容器外对象三段能力的细节：autowire/initialize/destroy 的正确用法与常见误用。
  - 补充集成案例：在非 Spring 管理对象上复用注入/初始化为何常失败，如何用证据链定位。
  - 针对“1. 结论先行：注入 ≠ 生命周期托管 ≠ 代理替换”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansAutowireCapableBeanFactoryLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 53. [√] 44. SpEL 与 `@Value("#{...}")`：表达式解析链路

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md`
- 现状速记：关键锚点：`AbstractBeanFactory#resolveEmbeddedValue` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `BeanFactory#resolveEmbeddedValue`；配套 Lab：`SpringCoreBeansSpelValueLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 深化策略：
  - 补充 “${} vs #{}” 的职责边界：placeholder resolver 与 expression resolver 各自发生的阶段与落点方法。
  - 补充 strict/non-strict 的来源与差异：默认 embedded value resolver vs PropertySourcesPlaceholderConfigurer 的覆盖。
  - 补充排障案例：placeholder 未解析/解析为错误类型/SpEL 混用导致误归因的修复路径。
  - 针对“1. 先跑 Lab：把“链路拆分”固定成断言”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansSpelValueLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 54. [√] 45. 自定义 Qualifier：meta-annotation 与候选收敛

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md`
- 现状速记：关键锚点：`AutowireCandidateResolver#isAutowireCandidate` / `DefaultListableBeanFactory#determineAutowireCandidate` / `DefaultListableBeanFactory#findAutowireCandidates`；配套 Lab：`SpringCoreBeansCustomQualifierLabTest`
- 深化策略：
  - 补充“DependencyDescriptor 深挖”：required/annotations/resolvableType/field vs parameter 等字段如何影响解析结果，并给出 2 个对照注入点。
  - 把依赖解析写成“分支树”：快捷路径（Optional/Provider/@Lazy/@Value）→ resolvableDependencies → 候选收集 → 候选收敛 → 集合解析/排序 → fallback，每个分支给一个典型现象。
  - 补充“关键变量解释”：围绕 `AutowireCandidateResolver#isAutowireCandidate`，解释 candidates/primary/qualifier/dependencyName 等变量各自的含义与决策地位。
  - 针对“1. 结论先行：自定义 Qualifier 的本质”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansCustomQualifierLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 55. [√] 46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/46-xml-namespace-extension.md`
- 现状速记：关键锚点：`BeanDefinitionParser#parse` / `BeanDefinitionParserDelegate#parseCustomElement` / `DefaultBeanDefinitionDocumentReader#parseBeanDefinitions`；配套 Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
- 深化策略：
  - 补充“定义层输入归一化主线”：XML/namespace/properties/groovy 如何变成 BeanDefinition 并注册到 registry。
  - 补充错误分型：解析错误/语义错误/资源错误分别如何定位到具体输入片段与关键类。
  - 针对“1. 是什么：namespace 扩展解决的是什么问题？”补细：把该解析分支的关键对象（reader/parser/delegate）与关键数据结构补齐。
  - 补充可复现闭环：以 `SpringCoreBeansXmlNamespaceExtensionLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 56. [√] 47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md`
- 现状速记：关键锚点：`AbstractBeanDefinitionReader#loadBeanDefinitions` / `DefaultListableBeanFactory#registerBeanDefinition` / `GroovyBeanDefinitionReader#loadBeanDefinitions`；配套 Lab：`SpringCoreBeansGroovyBeanDefinitionReaderLabTest` / `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- 深化策略：
  - 补充“定义层输入归一化主线”：XML/namespace/properties/groovy 如何变成 BeanDefinition 并注册到 registry。
  - 补充错误分型：解析错误/语义错误/资源错误分别如何定位到具体输入片段与关键类。
  - 针对“1. 是什么：为什么要有 BeanDefinitionReader 家族？”补细：把该解析分支的关键对象（reader/parser/delegate）与关键数据结构补齐。
  - 补充可复现闭环：以 `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 57. [√] 48. 方法注入（Method Injection）：replaced-method / MethodReplacer

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/48-method-injection-replaced-method.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#createBeanInstance` / `AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection` / `AbstractBeanDefinition#getMethodOverrides`；配套 Lab：`SpringCoreBeansReplacedMethodLabTest`
- 深化策略：
  - 补充实现机制拆解：MethodOverride/ReplaceOverride/MethodReplacer 如何在 createBeanInstance → instantiateWithMethodInjection 被识别并织入。
  - 补充与 @Lookup 的差异与选型：两者都属于方法注入，但触发点/生成方式/适用场景不同。
  - 补充 AOT/Native 风险与替代：方法注入依赖 CGLIB 子类覆写，在 AOT 场景更脆弱；给出迁移策略。
  - 针对“1. 是什么：它解决什么问题？不解决什么问题？”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansReplacedMethodLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 58. [√] 49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md`
- 现状速记：关键锚点：`AbstractBeanFactory#doGetBean` / `AbstractBeanFactory#getObjectForBeanInstance` / `BeanFactoryUtils#isFactoryDereference`；配套 Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
- 深化策略：
  - 补充类型推断与缓存链路：getObjectType/getTypeForFactoryBean/getObjectFromFactoryBean 如何影响按类型查找、条件判断与注入。
  - 补充“& 前缀”的证据链：在源码里区分取 factory vs 取 product 的最短路径与常见误用。
  - 补充与代理/循环依赖的交叉：FactoryBean 产物与 early reference 的一致性边界，如何断言与排障。
  - 针对“1. 是什么：内置 FactoryBean 解决的是什么问题？”补一个“机制讲透”小节：把本段核心结论写成“条件→分支→结果”的推导，并用 1 个断点证明。
  - 补充可复现闭环：以 `SpringCoreBeansBuiltInFactoryBeansLabTest` 为主入口，把本章关键结论各自绑定到一个可断言场景。

### 59. [√] 50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/50-property-editor-and-value-resolution.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyPropertyValues` / `AbstractBeanFactory#resolveEmbeddedValue` / `AbstractNestablePropertyAccessor#setPropertyValue`；配套 Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`
- 深化策略：
  - 补充 “${} vs #{}” 的职责边界：placeholder resolver 与 expression resolver 各自发生的阶段与落点方法。
  - 补充 strict/non-strict 的来源与差异：默认 embedded value resolver vs PropertySourcesPlaceholderConfigurer 的覆盖。
  - 补充排障案例：placeholder 未解析/解析为错误类型/SpEL 混用导致误归因的修复路径。
  - 补充 conversion pipeline：BeanWrapper/TypeConverterDelegate/ConversionService/PropertyEditor 的选择顺序与排障入口。
  - 补充属性路径解析：nested path、集合/Map 属性写入、auto-grow 的边界与误区。

### 60. [√] 第 25 章：90. 常见坑清单（建议反复对照）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractAutowireCapableBeanFactory#populateBean`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansAutowireCandidateSelectionLabTest` 类似的可复现/可断言入口，避免只能口述。

### 61. [√] 第 26 章：99. 自测题：你是否真的理解了？

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `AbstractAutowireCapableBeanFactory#initializeBean`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansAutowireCandidateSelectionLabTest` 类似的可复现/可断言入口，避免只能口述。

### 62. [√] 91. 术语表（Glossary）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/91-glossary.md`
- 现状速记：关键锚点：`DefaultListableBeanFactory#registerBeanDefinition` / `DefaultSingletonBeanRegistry#getSingleton`；配套 Lab：`SpringCoreBeansContainerLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansContainerLabTest` 类似的可复现/可断言入口，避免只能口述。

### 63. [√] 92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractBeanFactory#resolveEmbeddedValue` / `CachedIntrospectionResults#forClass`；配套 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanDefinitionOverridingLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansAutowireCandidateSelectionLabTest` 类似的可复现/可断言入口，避免只能口述。

### 64. [√] 93. 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/93-interview-playbook.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractAutowireCapableBeanFactory#initializeBean`；配套 Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansAotFactoriesLabTest` 类似的可复现/可断言入口，避免只能口述。

### 65. [√] 94. 生产排障清单（Troubleshooting Checklist）：从症状到证据链

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`
- 现状速记：关键锚点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractBeanFactory#doGetBean`；配套 Lab：`SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansAotRuntimeHintsLabTest` 类似的可复现/可断言入口，避免只能口述。

### 66. [√] 95. spring-beans Public API 索引（Spring Framework 6.2.15）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/95-spring-beans-public-api-index.md`
- 现状速记：关键锚点：（本章未显式列出）；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest` / `SpringCoreBeansIocBranchMatrixLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansBreakpointPackLabTest` 类似的可复现/可断言入口，避免只能口述。

### 67. [√] 96. spring-beans Public API Gap 清单（按包/机制域分批深化）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/96-spring-beans-public-api-gap.md`
- 现状速记：关键锚点：（本章未显式列出）；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest` / `SpringCoreBeansIocBranchMatrixLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansBreakpointPackLabTest` 类似的可复现/可断言入口，避免只能口述。

### 68. [√] 97. Explore/Debug 用例（可选启用，不影响默认回归）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#doCreateBean` / `CachedIntrospectionResults#acceptClassLoader` / `CachedIntrospectionResults#forClass`；配套 Lab：（本章未显式列出）
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐可复现/可断言入口，避免只能口述。

### 69. [√] 98. Debugger Pack（断点包总入口）

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md`
- 现状速记：关键锚点：`AbstractApplicationContext#finishBeanFactoryInitialization` / `AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest` / `SpringCoreBeansIocBranchMatrixLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansBreakpointPackLabTest` 类似的可复现/可断言入口，避免只能口述。

### 70. [√] 99. 团队内训讲义（Training Kit）：可直接开讲的课时脚本

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md`
- 现状速记：关键锚点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#populateBean` / `DefaultListableBeanFactory#determineAutowireCandidate`；配套 Lab：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansMainlineCallChainLabTest`
- 深化策略：
  - 把条目从“知识点列表”升级为“现象→证据链→修复→验证”的可复盘资产（每条给出 1 个最短入口方法 + 关键变量）。
  - 补充“误归因对照”：把最常见的错误归因写成可推导的反例（说明为什么错、错在什么分支/阶段）。
  - 补充可运行入口：为关键条目补齐与 `SpringCoreBeansBreakpointPackLabTest` 类似的可复现/可断言入口，避免只能口述。

## 贯穿式支撑任务（每批迭代都要做）

- [√] 安全与合规自检：确保新增示例/脚本不引入明文密钥，不建议高风险生产操作。
- [√] 术语与结论一致性回归：对同一机制的关键结论不自相矛盾（以代码行为为准）。
- [-] 可运行回归策略：若本批涉及 Lab/Test 变更，运行 `spring-core-beans` 模块测试并记录结果。（本批未改测试，未运行）
