# 05. 关键分支矩阵（Logging）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：关键分支矩阵（Logging）展开，主线可以概括为：logger category → effective level → 输出端（appender）。

    把“日志出现/不出现”变成可复现分支：每个分支都有入口（测试）与断点锚点。

    对照入口：`BootLoggingLabTest`。需要下探源码时，可以从 （logback）`Logger#isDebugEnabled` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Logging Debugger Pack）](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 日志级别与分类（为什么 debug 有时出现、有时不出现）](logging-basics-logging-levels-and-categories.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootLoggingLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：logger category → effective level → 输出端（appender）。需要下探源码时，可以从 （logback）`Logger#isDebugEnabled` 这些入口切入。


## 分支矩阵（最小闭环）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 断点锚点（Breakpoint） |
|---|---|---|---|---|
| debug 输出出现 | `logging.level...=DEBUG` | debug 日志出现在控制台输出 | `BootLoggingLabTest#debugLogIsPrintedWhenLevelIsDebug` | `Logger#isDebugEnabled` |

## 小结与下一章

logger category → effective level → 输出端（appender）。

下一章见：[第 201 章：01：日志级别与分类](logging-basics-logging-levels-and-categories.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-logging-basics/01-logging-levels-and-categories.md](logging-basics-logging-levels-and-categories.md)

<!-- BOOKIFY:END -->
