# 05. 关键分支矩阵（Logging）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Logging）
    - 怎么使用：把“日志出现/不出现”变成可复现分支：每个分支都有入口（测试）与断点锚点。
    - 原理：logger category → effective level → 输出端（appender）。
    - 源码入口：（logback）`Logger#isDebugEnabled`
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Logging Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 日志级别与分类（为什么 debug 有时出现、有时不出现）](../part-01-logging-basics/01-logging-levels-and-categories.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 关键分支矩阵（Logging）**
- 建议入口：优先运行 `BootLoggingLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：logger category → effective level → 输出端（appender）。
- 源码入口：（logback）`Logger#isDebugEnabled`



## 分支矩阵（最小闭环）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 断点锚点（Breakpoint） |
|---|---|---|---|---|
| debug 输出出现 | `logging.level...=DEBUG` | debug 日志出现在控制台输出 | `BootLoggingLabTest#debugLogIsPrintedWhenLevelIsDebug` | `Logger#isDebugEnabled` |

## 小结与下一章

- 小结：logger category → effective level → 输出端（appender）。
- 下一章：[第 201 章：01：日志级别与分类](../part-01-logging-basics/01-logging-levels-and-categories.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-logging-basics/01-logging-levels-and-categories.md](../part-01-logging-basics/01-logging-levels-and-categories.md)

<!-- BOOKIFY:END -->
