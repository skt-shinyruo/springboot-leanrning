# 章节逐章补强建议（part-02-boot-autoconfig Boot 自动装配）

说明：以下建议是按每个章节的主题与现有素材（入口方法/关键类型/对应实验）来给出，重点是让内容更“可复现、可讲述、可排障、可落地”。

### 第 19 章：11. 调试与自检：如何“看见”容器正在做什么
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`
- 当前侧重点提示：入口: `DefaultListableBeanFactory#doResolveDependency`, `AutowiredAnnotationBeanPostProcessor#postProcessProperties`；类型: `ApplicationContextRunner`, `ConditionEvaluationReport`；实验: `SpringCoreBeansAutoConfigurationLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `DefaultListableBeanFactory#doResolveDependency` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `ApplicationContextRunner`, `ConditionEvaluationReport` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAutoConfigurationLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md`
- 当前侧重点提示：入口: `ConditionEvaluator#shouldSkip`, `AutoConfigurationImportSelector#selectImports`；类型: `AutoConfigurationSorter`, `AutoConfigurationImportSorter`；实验: `SpringCoreBeansAutoConfigurationOrderingLabTest`
- 补充与深化策略：
  - 补一张“容器启动扩展点时序图”：把 `BeanDefinitionRegistryPostProcessor` → `BeanFactoryPostProcessor` → `BeanPostProcessor` 的注册/回调顺序画成一张图，并在图上标注 `PriorityOrdered/Ordered` 的插队点。
  - 把 `ConditionEvaluator#shouldSkip` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `AutoConfigurationSorter`, `AutoConfigurationImportSorter` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAutoConfigurationOrderingLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
- 当前侧重点提示：入口: `ConditionEvaluator#shouldSkip`, `OnBeanCondition#getMatchOutcome`；类型: `AutoConfigurationImportSorter`, `ConditionEvaluationReport`；实验: `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `ConditionEvaluator#shouldSkip` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `AutoConfigurationImportSorter`, `ConditionEvaluationReport` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
