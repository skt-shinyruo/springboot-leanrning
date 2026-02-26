# 章节逐章补强建议（appendix 附录）

说明：以下建议是按每个章节的主题与现有素材（入口方法/关键类型/对应实验）来给出，重点是让内容更“可复现、可讲述、可排障、可落地”。

### 第 25 章：90. 常见误区清单（建议反复对照）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/01-common-pitfalls.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `DefaultListableBeanFactory#determineAutowireCandidate`；类型: `ObjectProvider`, `Ordered`；实验: `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ObjectProvider`, `Ordered` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAutowireCandidateSelectionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 26 章：99. 自测题：是否能够真的理解了？
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/11-self-check.md`
- 当前侧重点提示：入口: `ApplicationContext#refresh`, `org.springframework.context.support.AbstractApplicationContext#refresh`；类型: `BeanDefinition`, `ImportSelector`；实验: `SpringCoreBeansLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `ApplicationContext#refresh` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinition`, `ImportSelector` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 91. 术语表（Glossary）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/02-glossary.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#registerBeanDefinition`, `DefaultSingletonBeanRegistry#getSingleton`；类型: `ApplicationContext`, `Environment`；实验: `SpringCoreBeansContainerLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#registerBeanDefinition` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ApplicationContext`, `Environment` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansContainerLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/03-knowledge-map.md`
- 当前侧重点提示：入口: `DefaultSingletonBeanRegistry#getSingleton`, `CommonAnnotationBeanPostProcessor#postProcessProperties`；实验: `SpringCoreBeansBreakpointPackLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultSingletonBeanRegistry#getSingleton` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 补“关键类型/接口地图”：把本章涉及的接口族（如 *Aware/*PostProcessor/*FactoryBean/*Scope 等）按“参与时机”分组，并给出每组最常见的误用方式。
  - 把本章与 `SpringCoreBeansBreakpointPackLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 93. 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/04-interview-playbook.md`
- 当前侧重点提示：入口: `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`, `PostProcessorRegistrationDelegate#registerBeanPostProcessors`；类型: `ApplicationContext`, `CommonAnnotationBeanPostProcessor`；实验: `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ApplicationContext`, `CommonAnnotationBeanPostProcessor` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansValuePlaceholderResolutionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 94. 生产排障清单（Troubleshooting Checklist）：从症状到证据链
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/05-production-troubleshooting-checklist.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#registerBeanDefinition`, `DefaultListableBeanFactory#doResolveDependency`；类型: `BeanDefinitionStoreException`, `FactoryBean`；实验: `SpringCoreBeansBreakpointPackLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#registerBeanDefinition` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinitionStoreException`, `FactoryBean` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBreakpointPackLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 95. spring-beans Public API 索引（Spring Framework 6.2.15）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/06-spring-beans-public-api-index.md`
- 当前侧重点提示：类型: `DefaultListableBeanFactory`, `FactoryBean`；实验: `SpringCoreBeansBreakpointPackLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把本章的“关键入口/出口”补成清单：用 5~8 个具体方法名串起链路（入口 → 决策点 → 结果），避免读者只记概念不知从哪里下手读源码。
  - 围绕 `DefaultListableBeanFactory`, `FactoryBean` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBreakpointPackLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 96. spring-beans Public API Gap 清单（按包/机制域分批深化）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/07-spring-beans-public-api-gap.md`
- 当前侧重点提示：入口: `doCreateBean`；类型: `FactoryBean`, `ResolvableDependency`；实验: `SpringCoreBeansBreakpointPackLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `doCreateBean` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `FactoryBean`, `ResolvableDependency` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBreakpointPackLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 97. Explore/Debug 用例（可选启用，不影响默认回归）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/08-explore-debug-tests.md`
- 当前侧重点提示：入口: `CachedIntrospectionResults#forClass`, `DefaultSingletonBeanRegistry#getSingleton`；类型: `CachedIntrospectionResults`, `DefaultSingletonBeanRegistry`；实验: `SpringCoreBeansSingletonCacheExploreTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `CachedIntrospectionResults#forClass` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `CachedIntrospectionResults`, `DefaultSingletonBeanRegistry` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansSingletonCacheExploreTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 98. Debugger Pack（断点包总入口）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/09-debugger-pack.md`
- 当前侧重点提示：入口: `AbstractApplicationContext#refresh`, `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`；实验: `SpringCoreBeansBreakpointPackLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractApplicationContext#refresh` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `this.startupShutdownMonitor` / `this.beanFactory` / `active` / `earlyApplicationEvents`，并解释每个变量变化代表的分支含义。
  - 补“关键类型/接口地图”：把本章涉及的接口族（如 *Aware/*PostProcessor/*FactoryBean/*Scope 等）按“参与时机”分组，并给出每组最常见的误用方式。
  - 把本章与 `SpringCoreBeansBreakpointPackLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 99. 团队内训讲义（Training Kit）：可直接开讲的课时脚本
- 📍 文件：`spring-core-modules/spring-core-beans/docs/appendix/10-team-training-kit.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `DefaultListableBeanFactory#determineAutowireCandidate`；实验: `SpringCoreBeansBreakpointPackLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 补“关键类型/接口地图”：把本章涉及的接口族（如 *Aware/*PostProcessor/*FactoryBean/*Scope 等）按“参与时机”分组，并给出每组最常见的误用方式。
  - 把本章与 `SpringCoreBeansBreakpointPackLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
