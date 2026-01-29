# 章节逐章补强建议（part-03-container-internals 容器内部）

说明：以下建议是按每个章节的主题与现有素材（入口方法/关键类型/对应实验）来给出，重点是让内容更“可复现、可讲述、可排障、可落地”。

### 第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
- 当前侧重点提示：入口: `AnnotationConfigUtils#registerAnnotationConfigProcessors`, `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`；类型: `GenericApplicationContext`, `AutowiredAnnotationBeanPostProcessor`；实验: `SpringCoreBeansBootstrapInternalsLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AnnotationConfigUtils#registerAnnotationConfigProcessors` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `GenericApplicationContext`, `AutowiredAnnotationBeanPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBootstrapInternalsLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`
- 当前侧重点提示：入口: `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`, `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`；类型: `BeanDefinition`, `BeanDefinitionRegistryPostProcessor`；实验: `SpringCoreBeansRegistryPostProcessorLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinition`, `BeanDefinitionRegistryPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansRegistryPostProcessorLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
- 当前侧重点提示：入口: `PostProcessorRegistrationDelegate#registerBeanPostProcessors`, `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`；类型: `Ordered`, `PriorityOrdered`；实验: `SpringCoreBeansPostProcessorOrderingLabTest`
- 补充与深化策略：
  - 补一张“容器启动扩展点时序图”：把 `BeanDefinitionRegistryPostProcessor` → `BeanFactoryPostProcessor` → `BeanPostProcessor` 的注册/回调顺序画成一张图，并在图上标注 `PriorityOrdered/Ordered` 的插队点。
  - 把 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `Ordered`, `PriorityOrdered` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansPostProcessorOrderingLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`
- 当前侧重点提示：入口: `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`, `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`；类型: `InstantiationAwareBeanPostProcessor`, `FailingService`；实验: `SpringCoreBeansPreInstantiationLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `InstantiationAwareBeanPostProcessor`, `FailingService` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansPreInstantiationLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
- 当前侧重点提示：入口: `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`, `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`；类型: `BeanNotOfRequiredTypeException`, `BeanPostProcessor`；实验: `SpringCoreBeansRawInjectionDespiteWrappingLabTest`
- 补充与深化策略：
  - 补一张“循环依赖决策/缓存状态变化图”：把 `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 的写入与读取时机画清楚，并标注“什么时候暴露 early reference，什么时候禁止”。
  - 把 `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanNotOfRequiredTypeException`, `BeanPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansRawInjectionDespiteWrappingLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“循环依赖排障 SOP”：分别覆盖 constructor 注入、prototype、AOP/代理、@Lazy 介入这几类边界，明确哪些场景 Spring 直接禁止、哪些场景可以靠 early reference 绕过。
### 17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`
- 当前侧重点提示：入口: `AbstractAutowireCapableBeanFactory#initializeBean`, `DisposableBeanAdapter#destroy`；类型: `CommonAnnotationBeanPostProcessor`, `SmartInitializingSingleton`；实验: `SpringCoreBeansLifecycleCallbackOrderLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractAutowireCapableBeanFactory#initializeBean` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `CommonAnnotationBeanPostProcessor`, `SmartInitializingSingleton` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansLifecycleCallbackOrderLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 18. 从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#doGetBean`, `AbstractAutowireCapableBeanFactory#doCreateBean`；类型: `BeanDefinition`, `BeanFactory`；实验: `SpringCoreBeansPreInstantiationLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#doGetBean` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `beanName` / `mbd` / `singletonObjects` / `earlySingletonObjects`，并解释每个变量变化代表的分支含义。
  - 围绕 `BeanDefinition`, `BeanFactory` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansPreInstantiationLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
