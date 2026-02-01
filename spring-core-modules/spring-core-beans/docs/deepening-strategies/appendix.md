# 逐章内容级再加深建议（appendix 工具章节）

Appendix 的再加深重点：把“工具页”做成可复用的训练与排障中枢——读者能从症状快速定位、能把答案用证据链证明、能用题库训练复述。

## 执行化提示（工具页的“复用价值”）

- 工具页不追求“讲新概念”，追求“减少定位成本”：每一项都应链接到章节正文/断点入口/Lab，形成可导航闭环。
- 工具页的最低标准：给出“它在证明什么”“如何观察到证据”“最常见误诊反例是什么”，否则工具页很难在真实排障中复用。

### 90. 常见误区清单

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md`
- 继续加深建议：
    - `SpringCoreBeansAutowireCandidateSelectionLabTest`（再对照 `SpringCoreBeansContainerLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 将坑点从“现象清单”收敛为“最短诊断路线”：每类现象给出第一入口断点与第一条排除项，并回链到对应章节/用例。

### 99. 自测题

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md`
- 继续加深建议：
    - `SpringCoreBeansLabTest`（再对照 `SpringCoreBeansContainerLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 将自检题的“答案”改为“验证路线”：每题后给出最短回链（去哪个章节/跑哪个用例/在哪个入口断点验证）。

### 91. 术语表

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/91-glossary.md`
- 继续加深建议：
    - `SpringCoreBeansContainerLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#registerBeanDefinition` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 术语表减少抽象解释，补“落到代码里是什么”：每个术语给出关键类/方法/数据结构，并回链到首次出现的章节。

### 92. 知识地图

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md`
- 继续加深建议：
    - `SpringCoreBeansBreakpointPackLabTest`（再对照 `SpringCoreBeansIocBranchMatrixLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultSingletonBeanRegistry#getSingleton` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 知识地图优先服务“快速定位”：把每个节点压缩为“常见现象 → 对应章节 → 最小可跑入口（测试方法名）”，避免过多枚举。

### 93. 面试复述模板

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/93-interview-playbook.md`
- 继续加深建议：
    - `SpringCoreBeansIocBranchMatrixLabTest`（再对照 `SpringCoreBeansInternalsBranchMatrixLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 面试复述页为每个高频题补“可验证证据”：明确可以用哪个测试 + 哪个断点证明，而不是只给口头答案。

### 94. 生产排障清单

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`
- 继续加深建议：
    - `SpringCoreBeansBreakpointPackLabTest`（再对照 `SpringCoreBeansIocBranchMatrixLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#registerBeanDefinition` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 生产排障清单按症状给分流：注入失败/代理不生效/循环依赖/配置不生效等，每类给出第一入口断点与对应章节/用例。

### 95/96. public API 索引与 gap

- 文件：
  - `spring-core-modules/spring-core-beans/docs/appendix/95-spring-beans-public-api-index.md`
  - `spring-core-modules/spring-core-beans/docs/appendix/96-spring-beans-public-api-gap.md`
- 继续加深建议：
    - `SpringCoreBeansBreakpointPackLabTest`（再对照 `SpringCoreBeansIocBranchMatrixLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#registerBeanDefinition` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 生产排障清单按症状给分流：注入失败/代理不生效/循环依赖/配置不生效等，每类给出第一入口断点与对应章节/用例。

### 97/98/99. Explore/Debug / Debugger Pack / Team Training

- 文件：
  - `spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md`
  - `spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md`
  - `spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md`
- 继续加深建议：
    - `SpringCoreBeansBreakpointPackLabTest`（再对照 `SpringCoreBeansIocBranchMatrixLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `DefaultListableBeanFactory#registerBeanDefinition` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 生产排障清单按症状给分流：注入失败/代理不生效/循环依赖/配置不生效等，每类给出第一入口断点与对应章节/用例。
