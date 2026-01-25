# 第 202 章：90 - Common Pitfalls（springboot-logging）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Common Pitfalls（springboot-logging）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 201 章：01：日志级别与分类](../part-01-logging-basics/201-01-logging-levels-and-categories.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 203 章：99 - Self Check（springboot-logging）](203-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Common Pitfalls（springboot-logging） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Common Pitfalls（springboot-logging）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-logging -Dtest=BootLoggingLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 坑 1：写了 debug 日志，但永远看不到

- 检查：category 是否正确（包名层级是否命中）
- 验证：断点 `Logger#isDebugEnabled`

## 坑 2：日志太多，把信号淹没了

- 先把关键链路 category 单独调高
- 把噪音 category 调低或关掉

## 坑 3：日志无法关联（跨线程/跨请求）

- 需要 MDC 或 traceId 等上下文（本仓库建议先从 MDC 开始）

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Common Pitfalls（springboot-logging）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Common Pitfalls（springboot-logging）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootLoggingLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[日志级别与分类：为什么 debug 有时出现、有时不出现](../part-01-logging-basics/201-01-logging-levels-and-categories.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[自检](203-99-self-check.md)

<!-- BOOKIFY:END -->
