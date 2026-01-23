# 第 195 章：02：断点地图（AutoConfiguration Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（AutoConfiguration Debugger Pack）
    - 怎么使用：本页是“排障索引页”。遇到“auto-config 不生效/生效但不是你以为的结果”时，按 A→B→C 顺序下断点收敛原因。
    - 原理：先确认 imports 是否存在，再确认 condition 是否跳过，再确认 backoff 是否让位。
    - 源码入口：`AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome`
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 195 章：01：AutoConfiguration 调用链](195-01-autoconfiguration-import-call-chain.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 195 章：04：关键分支矩阵](195-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

上一章：[part-00-guide/01-autoconfiguration-call-chain.md](195-01-autoconfiguration-import-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](195-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
