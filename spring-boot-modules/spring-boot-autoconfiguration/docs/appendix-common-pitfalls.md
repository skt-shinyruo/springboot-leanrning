# 常见坑清单
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕常见坑清单（springboot-autoconfiguration）展开，主线可以概括为：大多数误判来自：只看某一个条件注解，而忽略了 imports 与 backoff（或忽略了多个 bean 的选择规则）。

    当遇到“功能没生效/bean 不存在/注入对象不对”时，用本页把问题收敛到 imports/condition/backoff/顺序其中一个分支。

    对照入口：`BootAutoConfigurationLabTest`。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 条件装配与 backoff（为什么它“有时生效、有时不生效”）](autoconfig-basics-conditional-and-backoff.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[自检题](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：大多数误判来自：只看某一个条件注解，而忽略了 imports 与 backoff（或忽略了多个 bean 的选择规则）。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。


## 坑 1：以为“没生效”就是 property 没配对

- 更常见的情况：auto-config 根本没被导入（imports 缺失/被 exclude）
- 验证入口：断点 `AutoConfigurationImportSelector#selectImports`

## 坑 2：以为 property 能覆盖一切，但实际是 backoff 让位

- 典型表现：配了 enabled=true，但默认 bean 还是没出现
- 根因：或某个 starter 提供了同类型 bean，触发 `@ConditionalOnMissingBean` backoff

## 坑 3：以为“装饰器没生效”是条件没命中，但实际是注入选择规则

- 典型表现：容器里有两个同类型 bean，但拿到的不是预期中的那个
- 验证：看是否 `@Primary` / 是否有 `@Qualifier`

## 小结与下一章

大多数误判来自：只看某一个条件注解，而忽略了 imports 与 backoff（或忽略了多个 bean 的选择规则）。

下一章见：[自检题](appendix-self-check.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootAutoConfigurationLabTest`

上一章：[autoconfig-basics-conditional-and-backoff.md](autoconfig-basics-conditional-and-backoff.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
