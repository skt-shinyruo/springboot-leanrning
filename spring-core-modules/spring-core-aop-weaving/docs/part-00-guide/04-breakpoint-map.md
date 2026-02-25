# 04. 断点地图（AspectJ Weaving Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（AspectJ Weaving Debugger Pack）
    - 怎么使用：先跑 LTW/CTW 两个 Branch Matrix 固化“是否织入/织入位置”的断言，再用断点在 advice 与 target 上观察 join point（call/execution/field/constructor）是否被拦截。
    - 原理：LTW 依赖 `-javaagent:aspectjweaver.jar`；CTW 依赖 ajc 编译期织入；两者的差异决定“什么时候发生织入”与“运行时是否需要 agent”。
    - 源码入口：本模块的 `*WeavingAspect` advice 方法 + 目标类方法（断点在 advice 最直接）
    - 推荐 Lab：`AspectjLtwBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南：把 weaving 的“结论 → 实验 → 排障路径”跑通](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本模块最重要的“分支”不是某个 if，而是 **运行方式**：LTW（带 agent） vs CTW（不带 agent）。
- 推荐证据链：
  - LTW：运行时 inputArgs 包含 `-javaagent:.../aspectjweaver.jar`，并且 advice 记录被写入日志
  - CTW：inputArgs 不包含 agent，但 advice 依然生效（因为类已被织入）

## 运行入口（建议先跑）

- Book Matrix（手动运行）：`AspectjWeavingBookMatrixLabTest`
- Branch Matrix（LTW）：`AspectjLtwBranchMatrixLabTest`
- Branch Matrix（CTW）：`AspectjCtwBranchMatrixLabTest`

推荐命令（建议直接跑模块测试，让 surefire 的 includes 分流 LTW/CTW）：

- `mvn -q -pl :spring-core-aop-weaving test`

## 断点（最有效：直接下在 advice 上）

- LTW advice：`com.learning.springboot.springcoreaopweaving.part02_ltw_fundamentals.LtwWeavingAspect`
- CTW advice：`com.learning.springboot.springcoreaopweaving.part03_ctw_fundamentals.CtwWeavingAspect`

## Watchpoints（建议）

- `ManagementFactory.getRuntimeMXBean().getInputArguments()`：是否带 agent（LTW/CTW 决策证据）
- invocation log（本模块的 InvocationLog）：记录哪些 join point 被拦截

## 排障入口（Playbook）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：LTW 依赖 `-javaagent:aspectjweaver.jar`；CTW 依赖 ajc 编译期织入；两者的差异决定“什么时候发生织入”与“运行时是否需要 agent”。
- 下一章：[第 44 章：04：关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`AspectjLtwBranchMatrixLabTest` / `AspectjCtwBranchMatrixLabTest`
- Lab：`AspectjLtwLabTest` / `AspectjCtwLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

