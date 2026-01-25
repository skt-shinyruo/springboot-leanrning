# 第 202 章：90 - Common Pitfalls（springboot-logging）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Common Pitfalls（springboot-logging）
    - 怎么使用：遇到“日志太多/太少/看不懂/不好关联”时，用本页把问题收敛到 level/category/输出端/MDC 其中一个。
    - 原理：大多数日志问题不是“加更多日志”，而是“让日志更可解释、可过滤、可关联”。
    - 源码入口：（logback）`Logger#isDebugEnabled` / `Logger#callAppenders`
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 201 章：01：日志级别与分类](../part-01-logging-basics/201-01-logging-levels-and-categories.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 203 章：99 - Self Check（springboot-logging）](203-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 坑 1：写了 debug 日志，但永远看不到

- 检查：category 是否正确（包名层级是否命中）
- 验证：断点 `Logger#isDebugEnabled`

## 坑 2：日志太多，把信号淹没了

- 先把关键链路 category 单独调高
- 把噪音 category 调低或关掉

## 坑 3：日志无法关联（跨线程/跨请求）

- 需要 MDC 或 traceId 等上下文（本仓库建议先从 MDC 开始）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-01-logging-basics/01-logging-levels-and-categories.md](../part-01-logging-basics/201-01-logging-levels-and-categories.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](203-99-self-check.md)

<!-- BOOKIFY:END -->
