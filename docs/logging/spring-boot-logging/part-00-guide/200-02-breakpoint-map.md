# 第 200 章：02：断点地图（Logging Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Logging Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 200 章：01：Logging 调用链](200-01-logging-call-chain.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 200 章：04：关键分支矩阵](200-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Logging Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「02：断点地图（Logging Debugger Pack）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-logging -Dtest=BootLoggingLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## A. 级别决策：effective level 到底是多少？

- 建议断点：`ch.qos.logback.classic.Logger#isDebugEnabled`
- 你要看的：`effectiveLevelInt` / 继承链（parent logger）

## B. 输出端：有没有被过滤/重定向？

- 建议断点：`ch.qos.logback.classic.Logger#callAppenders`
- 你要看的：appender 列表与 filter 链

## C. 教学闭环：用测试固化

- 入口：`BootLoggingLabTest`
- 目标：把“应该出现的日志”变成断言（避免只靠肉眼看控制台）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[Logging 调用链（LoggingSystem 初始化与级别决策）](200-01-logging-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[关键分支矩阵（If/Then 收敛）](200-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
