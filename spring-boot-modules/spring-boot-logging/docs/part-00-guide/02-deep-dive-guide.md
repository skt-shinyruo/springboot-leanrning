# 02. 深挖导读：把“日志级别生效”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖导读：把“日志级别生效”落到源码与断点
    - 怎么使用：先跑 `BootLoggingLabTest`，把“debug 输出出现”固化成断言；再用断点回答：这条 debug 为什么会/不会输出？
    - 原理：日志不是“越多越好”，而是“可解释、可过滤、可关联”。第一步是把 level/category 的机制跑通。
    - 源码入口：（Boot）`LoggingSystem` /（SLF4J）`Logger` /（Logback）`Logger#isDebugEnabled`
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：springboot-logging](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. Logging 调用链（LoggingSystem 初始化与级别决策）](03-logging-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 深挖导读：把“日志级别生效”落到源码与断点**
- 目标：遇到“debug 没输出/日志太多/日志看不懂”时，你能先定位到：category 与 effective level。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootLoggingLabTest`

## 推荐抓手

- 先从“现象”入手：`BootLoggingLabTest` 用 OutputCapture 固化“应该出现的日志”
- 再回到“机制”：在 logger 的 `isDebugEnabled` 附近看 effective level

## 小结与下一章

- 小结：日志不是“越多越好”，而是“可解释、可过滤、可关联”。第一步是把 level/category 的机制跑通。
- 下一章：[第 200 章：01：Logging 调用链](03-logging-call-chain.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-00-guide/03-mainline-timeline.md](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/01-logging-call-chain.md](03-logging-call-chain.md)

<!-- BOOKIFY:END -->
