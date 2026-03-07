# 05. 关键分支矩阵（AutoConfiguration）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：关键分支矩阵（AutoConfiguration）展开，主线可以概括为：imports 决定候选集合；Condition 决定是否注册；backoff 决定是否让位；顺序决定最终形态。

    把“条件装配”变成可复现矩阵：每个分支都能落到一个最小入口（Lab）与一个断点锚点。

    对照入口：`BootAutoConfigurationLabTest`。需要下探源码时，可以从 `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（AutoConfiguration Debugger Pack）](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 条件装配与 backoff（为什么它“有时生效、有时不生效”）](autoconfig-basics-conditional-and-backoff.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：imports 决定候选集合；Condition 决定是否注册；backoff 决定是否让位；顺序决定最终形态。需要下探源码时，可以从 `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。


## 分支矩阵（最小闭环）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 断点锚点（Breakpoint） |
|---|---|---|---|---|
| 默认装配 | 不设置任何 property | 创建默认 GreetingService | `BootAutoConfigurationLabTest#autoConfigCreatesDefaultBeanWhenEnabled` | `ConditionEvaluator#shouldSkip` |
| 装饰器开启 | `demo.greeting.decorate=true` | 产生 primary 装饰 bean（LOG 前缀） | `BootAutoConfigurationLabTest#decoratorCreatesPrimaryBeanWhenEnabled` | `OnBeanCondition#getMatchOutcome` |
| 用户覆盖（backoff） | 用户自定义 `GreetingService` | 默认 bean 不再创建（让位） | `BootAutoConfigurationLabTest#userBeanOverridesAutoConfig_backoffOccurs` | `OnBeanCondition#evaluateConditionalOnMissingBean` |

## 小结与下一章

imports 决定候选集合；Condition 决定是否注册；backoff 决定是否让位；顺序决定最终形态。

下一章见：[第 196 章：01：条件装配与 backoff](autoconfig-basics-conditional-and-backoff.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-autoconfig-basics/01-conditional-and-backoff.md](autoconfig-basics-conditional-and-backoff.md)

<!-- BOOKIFY:END -->
