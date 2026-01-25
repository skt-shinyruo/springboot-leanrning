# 第 201 章：01：日志级别与分类（为什么 debug 有时出现、有时不出现）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：日志级别与分类（为什么 debug 有时出现、有时不出现）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 200 章：04：关键分支矩阵](../part-00-guide/200-04-branch-decision-matrix.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 202 章：90 - Common Pitfalls（springboot-logging）](../appendix/202-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：01：日志级别与分类（为什么 debug 有时出现、有时不出现） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「01：日志级别与分类（为什么 debug 有时出现、有时不出现）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-logging -Dtest=BootLoggingLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 1. category 是什么？

在 SLF4J/Logback 体系里，logger 的 category 通常就是：

- 类名（最常见）
- 或者显式命名的字符串

配置里你写的 `logging.level.com.xxx=DEBUG` 本质就是在设置这个层级树上的节点。

## 2. effective level 是什么？

effective level 是“继承后最终生效的级别”，它决定：

- `logger.isDebugEnabled()` 是否为 true

## 3. 把日志变成证据链（不要只靠肉眼）

教学建议优先用：

- `OutputCaptureExtension`（本模块的 `BootLoggingLabTest`）

当你需要验证 MDC/结构化字段时，建议升级为：

- Logback `ListAppender`

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「01：日志级别与分类（为什么 debug 有时出现、有时不出现）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「01：日志级别与分类（为什么 debug 有时出现、有时不出现）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootLoggingLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[关键分支矩阵（If/Then 收敛）](../part-00-guide/200-04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/202-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
