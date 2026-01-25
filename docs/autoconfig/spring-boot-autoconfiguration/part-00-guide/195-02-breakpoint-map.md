# 第 195 章：02：断点地图（AutoConfiguration Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（AutoConfiguration Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 195 章：01：AutoConfiguration 调用链](195-01-autoconfiguration-import-call-chain.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 195 章：04：关键分支矩阵](195-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（AutoConfiguration Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「02：断点地图（AutoConfiguration Debugger Pack）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## A. 导入链（imports）：有没有被导入？

### A1. `AutoConfigurationImportSelector#selectImports`

- 你要看的：最终选出来的 auto-config 类名列表
- 常见原因：
  - 被 exclude 了（配置/注解）
  - classpath 上根本没有对应 imports 记录

## B. 条件决策（Condition）：为什么被跳过？

### B1. `ConditionEvaluator#shouldSkip`

- 你要看的：当前 element（配置类或 @Bean 方法）为什么 shouldSkip=true

### B2. `OnBeanCondition#getMatchOutcome`

- 你要看的：`@ConditionalOnBean/@ConditionalOnMissingBean` 的 match 结果与原因描述

## C. backoff（让位）：为什么默认 bean 没被注册？

### C1. `OnBeanCondition#evaluateConditionalOnMissingBean`

- 你要看的：缺失判断到底缺的是什么（type/name/annotation）

## 观察点（Watch List）

- imports 列表（最终候选集合）
- `ConditionOutcome#getMessage()`（解释为什么匹配/不匹配）
- 容器里当前的 bean names（用于验证“到底有没有那个 bean”）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）](195-01-autoconfiguration-import-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[关键分支矩阵（If/Then 收敛）](195-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
