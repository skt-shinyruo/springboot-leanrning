# 章节逐章补强建议（模块 README）

### spring-core-beans
- 📍 文件：`spring-core-modules/spring-core-beans/README.md`
- 当前侧重点提示：入口: `AbstractApplicationContext#refresh`, `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`；类型: `FactoryBean`, `BeanDefinition`；实验: `SpringCoreBeansLabTest`
- 补充与深化策略：
  - 补一张“主链路/关键对象关系图”：把本章涉及的核心对象、入口方法、状态变化画成一张图（优先用时序图或状态机图），读者一眼能定位自己在流程的哪一段。
  - 把 `AbstractApplicationContext#refresh` 这一段的“观察点”补成表格：建议在 Debugger 里重点观察 `this.startupShutdownMonitor` / `this.beanFactory` / `active` / `earlyApplicationEvents`，并解释每个变量变化代表的分支含义。
  - 围绕 `FactoryBean`, `BeanDefinition` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
