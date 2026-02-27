# 01. 日志级别与分类（为什么 debug 有时出现、有时不出现）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：日志级别与分类（为什么 debug 有时出现、有时不出现）
    - 怎么使用：先跑 `BootLoggingLabTest`，再对照本文把“配置项 → category → effective level”串起来。
    - 原理：日志是否输出取决于 effective level；配置用 logger category（包名/类名）做层级继承。
    - 源码入口：（logback）`LoggerContext` / `Logger#isDebugEnabled`
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 关键分支矩阵（Logging）](../part-00-guide/05-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 90 - Common Pitfalls（springboot-logging）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01. 日志级别与分类（为什么 debug 有时出现、有时不出现）**
- 建议入口：优先运行 `BootLoggingLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：日志是否输出取决于 effective level；配置用 logger category（包名/类名）做层级继承。
- 源码入口：（logback）`LoggerContext` / `Logger#isDebugEnabled`



## 1. category 是什么？

在 SLF4J/Logback 体系里，logger 的 category 通常就是：

- 类名（最常见）
- 或者显式命名的字符串

配置里写的 `logging.level.com.xxx=DEBUG` 本质就是在设置这个层级树上的节点。

## 2. effective level 是什么？

effective level 是“继承后最终生效的级别”，它决定：

- `logger.isDebugEnabled()` 是否为 true

## 3. 把日志变成证据链（不要只靠肉眼）

教学建议优先用：

- `OutputCaptureExtension`（本模块的 `BootLoggingLabTest`）

当需要验证 MDC/结构化字段时，建议升级为：

- Logback `ListAppender`

## 小结与下一章

- 小结：日志是否输出取决于 effective level；配置用 logger category（包名/类名）做层级继承。
- 下一章：[第 202 章：90 - Common Pitfalls（springboot-logging）](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-00-guide/04-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->
