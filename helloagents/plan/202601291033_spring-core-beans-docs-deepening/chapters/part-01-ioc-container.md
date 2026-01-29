# 章节逐章补强建议（part-01-ioc-container IoC 容器）

说明：以下建议是按每个章节的主题与现有素材（入口方法/关键类型/对应实验）来给出，重点是让内容更“可复现、可讲述、可排障、可落地”。

### 第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `DefaultListableBeanFactory#resolveDependency`；类型: `NoUniqueBeanDefinitionException`, `NoSuchBeanDefinitionException`；实验: `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `NoUniqueBeanDefinitionException`, `NoSuchBeanDefinitionException` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAutowireCandidateSelectionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#doGetBean`, `DefaultSingletonBeanRegistry#destroySingletons`；类型: `ObjectProvider`, `CglibSubclassingInstantiationStrategy`；实验: `SpringCoreBeansContainerLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `AbstractBeanFactory#doGetBean` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `beanName` / `mbd` / `singletonObjects` / `earlySingletonObjects`，并解释每个变量变化代表的分支含义。
  - 围绕 `ObjectProvider`, `CglibSubclassingInstantiationStrategy` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansContainerLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- 当前侧重点提示：入口: `DefaultSingletonBeanRegistry#destroySingletons`, `DisposableBeanAdapter#destroy`；类型: `CommonAnnotationBeanPostProcessor`, `BeanPostProcessor`；实验: `SpringCoreBeansLifecycleCallbackOrderLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultSingletonBeanRegistry#destroySingletons` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `CommonAnnotationBeanPostProcessor`, `BeanPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansLifecycleCallbackOrderLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- 当前侧重点提示：入口: `AbstractApplicationContext#refresh`, `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`；类型: `BeanDefinition`, `BeanPostProcessor`；实验: `SpringCoreBeansContainerLabTest`
- 补充与深化策略：
  - 补一张“容器启动扩展点时序图”：把 `BeanDefinitionRegistryPostProcessor` → `BeanFactoryPostProcessor` → `BeanPostProcessor` 的注册/回调顺序画成一张图，并在图上标注 `PriorityOrdered/Ordered` 的插队点。
  - 把 `AbstractApplicationContext#refresh` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `this.startupShutdownMonitor` / `this.beanFactory` / `active` / `earlyApplicationEvents`，并解释每个变量变化代表的分支含义。
  - 围绕 `BeanDefinition`, `BeanPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansContainerLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- 当前侧重点提示：入口: `ConfigurationClassPostProcessor#processConfigBeanDefinitions`, `ConfigurationClassEnhancer#enhance`；类型: `ConfigA`, `ConfigurationClassEnhancer`；实验: `SpringCoreBeansContainerLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `ConfigurationClassPostProcessor#processConfigBeanDefinitions` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ConfigA`, `ConfigurationClassEnhancer` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansContainerLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 02. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#registerBeanDefinition`, `ClassPathBeanDefinitionScanner#doScan`；类型: `BeanDefinition`, `RootBeanDefinition`；实验: `SpringCoreBeansImportLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#registerBeanDefinition` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinition`, `RootBeanDefinition` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansImportLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 20 章：01. Bean 心智模型：从 BeanDefinition 到最终暴露对象
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- 当前侧重点提示：入口: `ApplicationContext#refresh`, `org.springframework.context.support.AbstractApplicationContext#refresh`；类型: `BeanDefinition`, `RootBeanDefinition`；实验: `SpringCoreBeansBeanCreationTraceLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `ApplicationContext#refresh` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinition`, `RootBeanDefinition` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBeanCreationTraceLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#getObjectForBeanInstance`, `FactoryBeanRegistrySupport#getObjectFromFactoryBean`；类型: `FactoryBean`, `T`；实验: `SpringCoreBeansFactoryBeanDeepDiveLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#getObjectForBeanInstance` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `FactoryBean`, `T` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansFactoryBeanDeepDiveLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 09. 循环依赖：现象、原因与规避（constructor vs setter）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- 当前侧重点提示：入口: `ConstructorResolver#autowireConstructor`, `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`；类型: `BeanCurrentlyInCreationException`, `ObjectProvider`；实验: `SpringCoreBeansCircularDependencyBoundaryLabTest`
- 补充与深化策略：
  - 补一张“循环依赖决策/缓存状态变化图”：把 `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 的写入与读取时机画清楚，并标注“什么时候暴露 early reference，什么时候禁止”。
  - 把 `ConstructorResolver#autowireConstructor` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanCurrentlyInCreationException`, `ObjectProvider` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansCircularDependencyBoundaryLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“循环依赖排障 SOP”：分别覆盖 constructor 注入、prototype、AOP/代理、@Lazy 介入这几类边界，明确哪些场景 Spring 直接禁止、哪些场景可以靠 early reference 绕过。
