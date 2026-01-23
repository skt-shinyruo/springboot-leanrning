# 第 200 章：02：断点地图（Logging Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Logging Debugger Pack）
    - 怎么使用：当你遇到“debug 没输出/日志级别不对/日志太多”时，用本页断点把问题收敛到 category/effective level/输出端其中一个。
    - 原理：先看 effective level，再看输出端（appender）。
    - 源码入口：（logback）`Logger#isDebugEnabled` / `Logger#filterAndLog_*`
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 200 章：01：Logging 调用链](200-01-logging-call-chain.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 200 章：04：关键分支矩阵](200-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## A. 级别决策：effective level 到底是多少？

- 建议断点：`ch.qos.logback.classic.Logger#isDebugEnabled`
- 你要看的：`effectiveLevelInt` / 继承链（parent logger）

## B. 输出端：有没有被过滤/重定向？

- 建议断点：`ch.qos.logback.classic.Logger#callAppenders`
- 你要看的：appender 列表与 filter 链

## C. 教学闭环：用测试固化

- 入口：`BootLoggingLabTest`
- 目标：把“应该出现的日志”变成断言（避免只靠肉眼看控制台）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-00-guide/01-logging-call-chain.md](200-01-logging-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](200-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
