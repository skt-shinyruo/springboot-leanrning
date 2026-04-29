# 02. 深挖导读：把“日志级别生效”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕把“日志级别生效”落到源码与断点展开，主线可以概括为：日志不是“越多越好”，而是“可解释、可过滤、可关联”。第一步是把 level/category 的机制跑通。

    先跑 `BootLoggingLabTest`，把“debug 输出出现”固化成断言；再用断点回答：这条 debug 为什么会/不会输出？

    需要下探源码时，可以从 （Boot）`LoggingSystem` /（SLF4J）`Logger` /（Logback）`Logger#isDebugEnabled` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：springboot-logging](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[03. Logging 调用链（LoggingSystem 初始化与级别决策）](guide-logging-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 目标：遇到“debug 没输出/日志太多/日志看不懂”时，能先定位到：category 与 effective level。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootLoggingLabTest`

## 证据抓手

- 先从“现象”入手：`BootLoggingLabTest` 用 OutputCapture 固化“应该出现的日志”
- 再回到“机制”：在 logger 的 `isDebugEnabled` 附近看 effective level

## 小结与下一章

日志不是“越多越好”，而是“可解释、可过滤、可关联”。第一步是把 level/category 的机制跑通。

下一章见：[01：Logging 调用链](guide-logging-call-chain.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootLoggingLabTest`

上一章：[guide-mainline-timeline.md](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-logging-call-chain.md](guide-logging-call-chain.md)

<!-- BOOKIFY:END -->
