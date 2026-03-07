# 01. 90 - Common Pitfalls（springboot-logging）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Common Pitfalls（springboot-logging）展开，主线可以概括为：大多数日志问题不是“加更多日志”，而是“让日志更可解释、可过滤、可关联”。

    遇到“日志太多/太少/看不懂/不好关联”时，用本页把问题收敛到 level/category/输出端/MDC 其中一个。

    对照入口：`BootLoggingLabTest`。需要下探源码时，可以从 （logback）`Logger#isDebugEnabled` / `Logger#callAppenders` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 日志级别与分类（为什么 debug 有时出现、有时不出现）](logging-basics-logging-levels-and-categories.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-logging）](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootLoggingLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：大多数日志问题不是“加更多日志”，而是“让日志更可解释、可过滤、可关联”。需要下探源码时，可以从 （logback）`Logger#isDebugEnabled` / `Logger#callAppenders` 这些入口切入。


## 坑 1：写了 debug 日志，但永远看不到

- 检查：category 是否正确（包名层级是否命中）
- 验证：断点 `Logger#isDebugEnabled`

## 坑 2：日志太多，把信号淹没了

- 先把关键链路 category 单独调高
- 把噪音 category 调低或关掉

## 坑 3：日志无法关联（跨线程/跨请求）

- 需要 MDC 或 traceId 等上下文（本仓库建议先从 MDC 开始）

## 小结与下一章

大多数日志问题不是“加更多日志”，而是“让日志更可解释、可过滤、可关联”。

下一章见：[第 203 章：99 - Self Check（springboot-logging）](appendix-self-check.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-01-logging-basics/01-logging-levels-and-categories.md](logging-basics-logging-levels-and-categories.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
