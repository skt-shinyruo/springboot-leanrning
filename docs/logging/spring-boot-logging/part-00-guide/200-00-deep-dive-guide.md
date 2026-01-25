# 第 200 章：00. 深挖导读：把“日志级别生效”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖导读：把“日志级别生效”落到源码与断点
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 199 章：03：主线时间线](199-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 200 章：01：Logging 调用链](200-01-logging-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：深挖导读：把“日志级别生效”落到源码与断点 —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- 本章主题：**00. 深挖导读：把“日志级别生效”落到源码与断点**
- 目标：遇到“debug 没输出/日志太多/日志看不懂”时，你能先定位到：category 与 effective level。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootLoggingLabTest`

## 推荐抓手

- 先从“现象”入手：`BootLoggingLabTest` 用 OutputCapture 固化“应该出现的日志”
- 再回到“机制”：在 logger 的 `isDebugEnabled` 附近看 effective level

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「深挖导读：把“日志级别生效”落到源码与断点」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「深挖导读：把“日志级别生效”落到源码与断点」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootLoggingLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[主线时间线](199-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Logging 调用链（LoggingSystem 初始化与级别决策）](200-01-logging-call-chain.md)

<!-- BOOKIFY:END -->
