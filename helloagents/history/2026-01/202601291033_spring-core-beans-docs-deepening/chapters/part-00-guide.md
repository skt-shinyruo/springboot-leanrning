# 章节逐章补强建议（part-00-guide 指南）

说明：以下建议是按每个章节的主题与现有素材（入口方法/关键类型/对应实验）来给出，重点是让内容更“可复现、可讲述、可排障、可落地”。

### 第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/02-mainline-timeline.md`
- 当前侧重点提示：入口: `AbstractApplicationContext#refresh`, `AbstractApplicationContext#prepareBeanFactory`；实验: `SpringCoreBeansMainlineCallChainLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractApplicationContext#refresh` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `this.startupShutdownMonitor` / `this.beanFactory` / `active` / `earlyApplicationEvents`，并解释每个变量变化代表的分支含义。
  - 补“关键类型/接口地图”：把本章涉及的接口族（如 *Aware/*PostProcessor/*FactoryBean/*Scope 等）按“参与时机”分组，并给出每组最常见的误用方式。
  - 把本章与 `SpringCoreBeansMainlineCallChainLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/03-deep-dive-guide.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `AbstractApplicationContext#refresh`；类型: `RootBeanDefinition`, `DependencyDescriptor`；实验: `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `RootBeanDefinition`, `DependencyDescriptor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAutowireCandidateSelectionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 11 章：关键分支矩阵（Branch Decision Matrix）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/04-branch-decision-matrix.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `CommonAnnotationBeanPostProcessor#postProcessProperties`；类型: `BeanCreationException`, `UnsatisfiedDependencyException`；实验: `SpringCoreBeansContainerLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanCreationException`, `UnsatisfiedDependencyException` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansContainerLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/05-quickstart-30min.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `org.springframework.context.support.AbstractApplicationContext#refresh`；类型: `PropertyValues`, `TextFormatter`；实验: `SpringCoreBeansBreakpointPackLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `PropertyValues`, `TextFormatter` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBreakpointPackLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/06-applicationcontext-refresh-call-chain.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#doGetBean`, `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`；类型: `BeanDefinition`, `BeanFactory`；实验: `SpringCoreBeansContainerLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractBeanFactory#doGetBean` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `beanName` / `mbd` / `singletonObjects` / `earlySingletonObjects`，并解释每个变量变化代表的分支含义。
  - 围绕 `BeanDefinition`, `BeanFactory` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansContainerLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/07-breakpoint-map.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `ApplicationContext#refresh`；类型: `BeanDefinition`, `DefaultListableBeanFactory`；实验: `SpringCoreBeansLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinition`, `DefaultListableBeanFactory` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
