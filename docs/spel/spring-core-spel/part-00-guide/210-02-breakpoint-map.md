# 第 210 章：02：断点地图（SpEL Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（SpEL Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 210 章：01：SpEL 调用链](210-01-spel-call-chain.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 210 章：04：关键分支矩阵](210-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（SpEL Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「02：断点地图（SpEL Debugger Pack）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## A. parser：表达式字符串怎么变成 AST？

- 建议断点：`SpelExpressionParser#parseExpression`
- 你要看的：解析出来的 expression 与内部 AST（如果你进一步深入）

## B. evaluation：为什么取不到值？

- 建议断点：`SpelExpression#getValue`
- 你要看的：evaluation context 的 root object 与 variables

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[SpEL 调用链（parse → AST → evaluate）](210-01-spel-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[关键分支矩阵（If/Then 收敛）](210-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
