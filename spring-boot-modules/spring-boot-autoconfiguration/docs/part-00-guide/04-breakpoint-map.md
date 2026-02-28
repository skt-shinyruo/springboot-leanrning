# 04. 断点地图（AutoConfiguration Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕02：断点地图（AutoConfiguration Debugger Pack）展开，主线可以概括为：先确认 imports 是否存在，再确认 condition 是否跳过，再确认 backoff 是否让位。

    本页是“排障索引页”。遇到“auto-config 不生效/生效但不是直觉里的结果”时，按 A→B→C 顺序下断点收敛原因。

    对照入口：`BootAutoConfigurationLabTest`。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）](03-autoconfiguration-import-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（AutoConfiguration）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：先确认 imports 是否存在，再确认 condition 是否跳过，再确认 backoff 是否让位。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。


## A. 导入链（imports）：有没有被导入？

### A1. `AutoConfigurationImportSelector#selectImports`

调试时建议重点盯：最终选出来的 auto-config 类名列表。

- 常见原因：
  - 被 exclude 了（配置/注解）
  - classpath 上根本没有对应 imports 记录

## B. 条件决策（Condition）：为什么被跳过？

### B1. `ConditionEvaluator#shouldSkip`

调试时建议重点盯：当前 element（配置类或 @Bean 方法）为什么 shouldSkip=true。


### B2. `OnBeanCondition#getMatchOutcome`

调试时建议重点盯：`@ConditionalOnBean/@ConditionalOnMissingBean` 的 match 结果与原因描述。


## C. backoff（让位）：为什么默认 bean 没被注册？

### C1. `OnBeanCondition#evaluateConditionalOnMissingBean`

调试时建议重点盯：缺失判断到底缺的是什么（type/name/annotation）


## 观察点（Watch List）

- imports 列表（最终候选集合）
- `ConditionOutcome#getMessage()`（解释为什么匹配/不匹配）
- 容器里当前的 bean names（用于验证“到底有没有那个 bean”）

## 小结与下一章

先确认 imports 是否存在，再确认 condition 是否跳过，再确认 backoff 是否让位。

下一章见：[第 195 章：04：关键分支矩阵](05-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[part-00-guide/01-autoconfiguration-call-chain.md](03-autoconfiguration-import-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
