# 05. 关键分支矩阵（AutoConfiguration）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵（AutoConfiguration）展开，主线可以概括为：imports 决定候选集合；Condition 决定是否注册；backoff 决定是否让位；顺序决定最终形态。

    把“条件装配”变成可复现矩阵：每个分支都能落到一个最小入口（Lab）与一个断点锚点。

    对照入口：`BootAutoConfigurationLabTest`。需要下探源码时，可以从 `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（AutoConfiguration）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 条件装配与 backoff（为什么它“有时生效、有时不生效”）](autoconfig-basics-conditional-and-backoff.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：imports 决定候选集合；Condition 决定是否注册；backoff 决定是否让位；顺序决定最终形态。需要下探源码时，可以从 `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。


## 分支矩阵（最小闭环）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 断点锚点（Breakpoint） |
|---|---|---|---|---|
| 默认装配 | 不设置任何 property | 创建默认 GreetingService | `BootAutoConfigurationLabTest#autoConfigCreatesDefaultBeanWhenEnabled` | `ConditionEvaluator#shouldSkip` |
| 装饰器开启 | `demo.greeting.decorate=true` | 产生 primary 装饰 bean（LOG 前缀） | `BootAutoConfigurationLabTest#decoratorCreatesPrimaryBeanWhenEnabled` | `OnBeanCondition#getMatchOutcome` |
| 用户覆盖（backoff） | 用户自定义 `GreetingService` | 默认 bean 不再创建（让位） | `BootAutoConfigurationLabTest#userBeanOverridesAutoConfig_backoffOccurs` | `OnBeanCondition#evaluateConditionalOnMissingBean` |

## 小结与下一章

imports 决定候选集合；Condition 决定是否注册；backoff 决定是否让位；顺序决定最终形态。

下一章见：[01：条件装配与 backoff](autoconfig-basics-conditional-and-backoff.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootAutoConfigurationLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[autoconfig-basics-conditional-and-backoff.md](autoconfig-basics-conditional-and-backoff.md)

<!-- BOOKIFY:END -->
