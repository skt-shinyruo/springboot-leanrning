# 第 200 章：04：关键分支矩阵（Logging）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Logging）
    - 怎么使用：把“日志出现/不出现”变成可复现分支：每个分支都有入口（测试）与断点锚点。
    - 原理：logger category → effective level → 输出端（appender）。
    - 源码入口：（logback）`Logger#isDebugEnabled`
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 200 章：02：断点地图](200-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 201 章：01：日志级别与分类](../part-01-logging-basics/201-01-logging-levels-and-categories.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 分支矩阵（最小闭环）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 断点锚点（Breakpoint） |
|---|---|---|---|---|
| debug 输出出现 | `logging.level...=DEBUG` | debug 日志出现在控制台输出 | `BootLoggingLabTest#debugLogIsPrintedWhenLevelIsDebug` | `Logger#isDebugEnabled` |

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](200-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-logging-basics/01-logging-levels-and-categories.md](../part-01-logging-basics/201-01-logging-levels-and-categories.md)

<!-- BOOKIFY:END -->
