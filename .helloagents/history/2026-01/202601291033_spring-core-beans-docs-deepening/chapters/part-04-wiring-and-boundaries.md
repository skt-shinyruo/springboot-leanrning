# 章节逐章补强建议（part-04-wiring-and-boundaries 装配与边界）

说明：以下建议是按每个章节的主题与现有素材（入口方法/关键类型/对应实验）来给出，重点是让内容更“可复现、可讲述、可排障、可落地”。

### 第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/01-lazy-semantics.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#preInstantiateSingletons`, `DefaultListableBeanFactory#doResolveDependency`；实验: `SpringCoreBeansLazyLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `DefaultListableBeanFactory#preInstantiateSingletons` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `beanDefinitionNames` / `frozenBeanDefinitionNames` / `mbd` / `preInstantiationResolved`，并解释每个变量变化代表的分支含义。
  - 补“关键类型/接口地图”：把本章涉及的接口族（如 *Aware/*PostProcessor/*FactoryBean/*Scope 等）按“参与时机”分组，并给出每组最常见的误用方式。
  - 把本章与 `SpringCoreBeansLazyLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 19. dependsOn：强制初始化顺序（即使没有显式依赖）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/02-depends-on.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#doGetBean`, `DefaultSingletonBeanRegistry#destroySingletons`；类型: `DefaultSingletonBeanRegistry`, `ObjectProvider`；实验: `SpringCoreBeansDependsOnLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#doGetBean` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `beanName` / `mbd` / `singletonObjects` / `earlySingletonObjects`，并解释每个变量变化代表的分支含义。
  - 围绕 `DefaultSingletonBeanRegistry`, `ObjectProvider` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansDependsOnLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 20. registerResolvableDependency：能注入，但它不是 Bean
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `AutowireUtils#resolveAutowiringValue`；类型: `BeanFactory`, `ApplicationContext`；实验: `SpringCoreBeansResolvableDependencyLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanFactory`, `ApplicationContext` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansResolvableDependencyLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 21. 父子 ApplicationContext：可见性与覆盖边界
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/04-context-hierarchy.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#doGetBean`, `AbstractApplicationContext#setParent`；类型: `ApplicationContext`；实验: `SpringCoreBeansContextHierarchyLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#doGetBean` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `beanName` / `mbd` / `singletonObjects` / `earlySingletonObjects`，并解释每个变量变化代表的分支含义。
  - 围绕 `ApplicationContext` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansContextHierarchyLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 22. Bean 名称与 alias：同一个实例，多一个名字
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md`
- 当前侧重点提示：入口: `SimpleAliasRegistry#canonicalName`, `SimpleAliasRegistry#registerAlias`；实验: `SpringCoreBeansBeanNameAliasLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `SimpleAliasRegistry#canonicalName` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 补“关键类型/接口地图”：把本章涉及的接口族（如 *Aware/*PostProcessor/*FactoryBean/*Scope 等）按“参与时机”分组，并给出每组最常见的误用方式。
  - 把本章与 `SpringCoreBeansBeanNameAliasLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#getObjectForBeanInstance`, `FactoryBeanRegistrySupport#getObjectFromFactoryBean`；类型: `FactoryBean`, `Value`；实验: `SpringCoreBeansContainerLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#getObjectForBeanInstance` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `FactoryBean`, `Value` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansContainerLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#registerBeanDefinition`, `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding`；类型: `BeanDefinitionOverrideException`, `NoUniqueBeanDefinitionException`；实验: `SpringCoreBeansBeanDefinitionOverridingLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#registerBeanDefinition` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinitionOverrideException`, `NoUniqueBeanDefinitionException` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBeanDefinitionOverridingLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md`
- 当前侧重点提示：入口: `PostProcessorRegistrationDelegate#registerBeanPostProcessors`, `DefaultListableBeanFactory#addBeanPostProcessor`；类型: `BeanPostProcessor`, `PriorityOrdered`；实验: `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanPostProcessor`, `PriorityOrdered` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 26. SmartInitializingSingleton：所有单例都创建完之后再做事
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md`
- 当前侧重点提示：入口: `SmartInitializingSingleton#afterSingletonsInstantiated`, `DefaultListableBeanFactory#preInstantiateSingletons`；类型: `ApplicationRunner`, `SmartInitializingSingleton`；实验: `SpringCoreBeansSmartInitializingSingletonLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `SmartInitializingSingleton#afterSingletonsInstantiated` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ApplicationRunner`, `SmartInitializingSingleton` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansSmartInitializingSingletonLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 27. SmartLifecycle：start/stop 时机与 phase 顺序
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md`
- 当前侧重点提示：入口: `DefaultLifecycleProcessor#startBeans`, `DefaultLifecycleProcessor#stopBeans`；类型: `SmartLifecycle`, `DefaultLifecycleProcessor`；实验: `SpringCoreBeansSmartLifecycleLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultLifecycleProcessor#startBeans` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `SmartLifecycle`, `DefaultLifecycleProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansSmartLifecycleLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 28. 自定义 Scope + scoped proxy：thread scope 的真实语义
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#doGetBean`, `SimpleThreadScope#get`；类型: `SimpleThreadScope`, `ObjectProvider`；实验: `SpringCoreBeansCustomScopeLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#doGetBean` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `beanName` / `mbd` / `singletonObjects` / `earlySingletonObjects`，并解释每个变量变化代表的分支含义。
  - 围绕 `SimpleThreadScope`, `ObjectProvider` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansCustomScopeLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#getBeanNamesForType`, `FactoryBeanRegistrySupport#getTypeForFactoryBean`；类型: `FactoryBean`, `ResolvableType`；实验: `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#getBeanNamesForType` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `FactoryBean`, `ResolvableType` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansFactoryBeanEdgeCasesLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`
- 当前侧重点提示：入口: `AutowiredAnnotationBeanPostProcessor#postProcessProperties`, `AbstractAutowireCapableBeanFactory#autowireConstructor`；类型: `AutowiredAnnotationBeanPostProcessor`, `FieldInjectedTarget`；实验: `SpringCoreBeansInjectionPhaseLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `AutowiredAnnotationBeanPostProcessor#postProcessProperties` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `AutowiredAnnotationBeanPostProcessor`, `FieldInjectedTarget` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansInjectionPhaseLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`
- 当前侧重点提示：入口: `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`, `AbstractAutowireCapableBeanFactory#doCreateBean`；类型: `BeanNotOfRequiredTypeException`, `NoSuchBeanDefinitionException`；实验: `SpringCoreBeansProxyingPhaseLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanNotOfRequiredTypeException`, `NoSuchBeanDefinitionException` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansProxyingPhaseLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 32. `@Resource` 注入：为什么它更像“按名称找 Bean”？
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/15-resource-injection-name-first.md`
- 当前侧重点提示：入口: `CommonAnnotationBeanPostProcessor#postProcessProperties`, `CommonAnnotationBeanPostProcessor#autowireResource`；类型: `CommonAnnotationBeanPostProcessor`, `AutowiredAnnotationBeanPostProcessor`；实验: `SpringCoreBeansResourceInjectionLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `CommonAnnotationBeanPostProcessor#postProcessProperties` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `CommonAnnotationBeanPostProcessor`, `AutowiredAnnotationBeanPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansResourceInjectionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`
- 当前侧重点提示：入口: `AnnotationAwareOrderComparator#sort`, `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`；类型: `NoUniqueBeanDefinitionException`, `T`；实验: `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `AnnotationAwareOrderComparator#sort` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `NoUniqueBeanDefinitionException`, `T` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAutowireCandidateSelectionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#resolveEmbeddedValue`, `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`；类型: `PropertySourcesPlaceholderConfigurer`, `AutowiredAnnotationBeanPostProcessor`；实验: `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#resolveEmbeddedValue` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `PropertySourcesPlaceholderConfigurer`, `AutowiredAnnotationBeanPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansValuePlaceholderResolutionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#getMergedLocalBeanDefinition`, `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`；类型: `RootBeanDefinition`, `MergedBeanDefinitionPostProcessor`；实验: `SpringCoreBeansMergedBeanDefinitionLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#getMergedLocalBeanDefinition` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `RootBeanDefinition`, `MergedBeanDefinitionPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansMergedBeanDefinitionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`
- 当前侧重点提示：入口: `TypeConverterDelegate#convertIfNecessary`, `BeanWrapperImpl#setPropertyValue`；类型: `ConversionService`, `PropertyEditor`；实验: `SpringCoreBeansTypeConversionLabTest`
- 补充与深化策略：
  - 补一张“类型转换路线图”：`PropertyEditor` 与 `ConversionService` 的优先级、触发点、失败回退路径画清楚，并补 2 个最容易踩坑的类型样例。
  - 把 `TypeConverterDelegate#convertIfNecessary` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ConversionService`, `PropertyEditor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansTypeConversionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“转换失败排障 SOP”：从 `TypeMismatchException`/`ConversionFailedException` 反查到触发点（DataBinder/BeanWrapper/属性填充），并给出如何打印/定位具体 propertyPath 的方法。
### 37. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md`
- 当前侧重点提示：入口: `GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`, `DefaultListableBeanFactory#doResolveDependency`；类型: `ResolvableType`, `Handler`；实验: `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ResolvableType`, `Handler` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-environment-and-propertysource.md`
- 当前侧重点提示：入口: `PropertySourcesPropertyResolver#getProperty`, `AbstractBeanFactory#resolveEmbeddedValue`；类型: `MutablePropertySources`, `BeanDefinition`；实验: `SpringCoreBeansProfileRegistrationLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `PropertySourcesPropertyResolver#getProperty` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `MutablePropertySources`, `BeanDefinition` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansProfileRegistrationLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“配置值排障 SOP”：从“最终取值”反推“来源”（PropertySource 顺序/占位符解析/默认值），给出如何输出 property resolution trace 的做法。
### 39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md`
- 当前侧重点提示：入口: `AutowiredAnnotationBeanPostProcessor#postProcessProperties`, `DefaultListableBeanFactory#addBeanPostProcessor`；类型: `ApplicationContext`, `DefaultListableBeanFactory`；实验: `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AutowiredAnnotationBeanPostProcessor#postProcessProperties` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ApplicationContext`, `DefaultListableBeanFactory` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
