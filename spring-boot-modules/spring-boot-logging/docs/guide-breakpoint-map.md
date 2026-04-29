# 04. 断点地图（Logging）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Logging）展开，主线可以概括为：先看 effective level，再看输出端（appender）。

    当遇到“debug 没输出/日志级别不对/日志太多”时，用本页断点把问题收敛到 category/effective level/输出端其中一个。

    对照入口：`BootLoggingLabTest`。需要下探源码时，可以从 （logback）`Logger#isDebugEnabled` / `Logger#filterAndLog_*` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. Logging 调用链（LoggingSystem 初始化与级别决策）](guide-logging-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵（Logging）](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootLoggingLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：先看 effective level，再看输出端（appender）。需要下探源码时，可以从 （logback）`Logger#isDebugEnabled` / `Logger#filterAndLog_*` 这些入口切入。


## A. 级别决策：effective level 到底是多少？

- 断点入口：`ch.qos.logback.classic.Logger#isDebugEnabled`

调试时重点观察：`effectiveLevelInt` / 继承链（parent logger）


## B. 输出端：有没有被过滤/重定向？

- 断点入口：`ch.qos.logback.classic.Logger#callAppenders`

调试时重点观察：appender 列表与 filter 链。


## C. 教学闭环：用测试固化

- 入口：`BootLoggingLabTest`
- 目标：把“应该出现的日志”变成断言（避免只靠肉眼看控制台）

## 小结与下一章

先看 effective level，再看输出端（appender）。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootLoggingLabTest`

上一章：[guide-logging-call-chain.md](guide-logging-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
