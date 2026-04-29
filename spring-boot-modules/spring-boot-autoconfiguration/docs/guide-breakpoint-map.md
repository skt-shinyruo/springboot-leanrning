# 04. 断点地图（AutoConfiguration）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（AutoConfiguration）展开，主线可以概括为：先确认 imports 是否存在，再确认 condition 是否跳过，再确认 backoff 是否让位。

    本页是“排障索引页”。遇到“auto-config 不生效/生效但不是预期中的结果”时，按 A→B→C 顺序下断点收敛原因。

    对照入口：`BootAutoConfigurationLabTest`。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）](guide-autoconfiguration-import-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵（AutoConfiguration）](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：先确认 imports 是否存在，再确认 condition 是否跳过，再确认 backoff 是否让位。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。


## A. 导入链（imports）：有没有被导入？

### A1. `AutoConfigurationImportSelector#selectImports`

调试时重点观察：最终选出来的 auto-config 类名列表。

- 常见原因：
  - 被 exclude 了（配置/注解）
  - classpath 上根本没有对应 imports 记录

## B. 条件决策（Condition）：为什么被跳过？

### B1. `ConditionEvaluator#shouldSkip`

调试时重点观察：当前 element（配置类或 @Bean 方法）为什么 shouldSkip=true。


### B2. `OnBeanCondition#getMatchOutcome`

调试时重点观察：`@ConditionalOnBean/@ConditionalOnMissingBean` 的 match 结果与原因描述。


## C. backoff（让位）：为什么默认 bean 没被注册？

### C1. `OnBeanCondition#evaluateConditionalOnMissingBean`

调试时重点观察：缺失判断到底缺的是什么（type/name/annotation）


## 观察点

- imports 列表（最终候选集合）
- `ConditionOutcome#getMessage()`（解释为什么匹配/不匹配）
- 容器里当前的 bean names（用于验证“到底有没有那个 bean”）

## 小结与下一章

先确认 imports 是否存在，再确认 condition 是否跳过，再确认 backoff 是否让位。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootAutoConfigurationLabTest`

上一章：[guide-autoconfiguration-import-call-chain.md](guide-autoconfiguration-import-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
