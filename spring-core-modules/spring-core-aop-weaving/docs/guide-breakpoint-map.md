# 04. 断点地图（AspectJ Weaving）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（AspectJ Weaving）展开，主线可以概括为：LTW 依赖 `-javaagent:aspectjweaver.jar`；CTW 依赖 ajc 编译期织入；两者的差异决定“什么时候发生织入”与“运行时是否需要 agent”。

    先跑 LTW/CTW 两个 Branch Matrix 固化“是否织入/织入位置”的断言，再用断点在 advice 与 target 上观察 join point（call/execution/field/constructor）是否被拦截。

    对照入口：`AspectjLtwBranchMatrixLabTest`。需要下探源码时，可以从 本模块的 `*WeavingAspect` advice 方法 + 目标类方法（断点在 advice 最直接） 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南：把 weaving 的“结论 → 实验 → 排障路径”跑通](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- 本模块最重要的“分支”不是某个 if，而是 **运行方式**：LTW（带 agent） vs CTW（不带 agent）。
- 证据链：
  - LTW：运行时 inputArgs 包含 `-javaagent:.../aspectjweaver.jar`，并且 advice 记录被写入日志
  - CTW：inputArgs 不包含 agent，但 advice 依然生效（因为类已被织入）

## 运行入口（先运行）

- Book Matrix（手动运行）：`AspectjWeavingBookMatrixLabTest`
- Branch Matrix（LTW）：`AspectjLtwBranchMatrixLabTest`
- Branch Matrix（CTW）：`AspectjCtwBranchMatrixLabTest`

运行命令（直接运行模块测试，让 surefire 的 includes 分流 LTW/CTW）：

- `mvn -q -pl :spring-core-aop-weaving test`

## 断点（最有效：直接下在 advice 上）

- LTW advice：`com.learning.springboot.springcoreaopweaving.part02_ltw_fundamentals.LtwWeavingAspect`
- CTW advice：`com.learning.springboot.springcoreaopweaving.part03_ctw_fundamentals.CtwWeavingAspect`

## 观察点

- `ManagementFactory.getRuntimeMXBean().getInputArguments()`：是否带 agent（LTW/CTW 决策证据）
- invocation log（本模块的 InvocationLog）：记录哪些 join point 被拦截

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

LTW 依赖 `-javaagent:aspectjweaver.jar`；CTW 依赖 ajc 编译期织入；两者的差异决定“什么时候发生织入”与“运行时是否需要 agent”。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`AspectjLtwBranchMatrixLabTest` / `AspectjCtwBranchMatrixLabTest`
- Lab：`AspectjLtwLabTest` / `AspectjCtwLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

