# 01. 90 - Common Pitfalls（springboot-autoconfiguration）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Common Pitfalls（springboot-autoconfiguration）展开，主线可以概括为：大多数误判来自：只看某一个条件注解，而忽略了 imports 与 backoff（或忽略了多个 bean 的选择规则）。

    当遇到“功能没生效/bean 不存在/注入对象不对”时，用本页把问题收敛到 imports/condition/backoff/顺序其中一个分支。

    对照入口：`BootAutoConfigurationLabTest`。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 条件装配与 backoff（为什么它“有时生效、有时不生效”）](autoconfig-basics-conditional-and-backoff.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-autoconfiguration）](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：大多数误判来自：只看某一个条件注解，而忽略了 imports 与 backoff（或忽略了多个 bean 的选择规则）。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome` 这些入口切入。


## 坑 1：以为“没生效”就是 property 没配对

- 更常见的情况：auto-config 根本没被导入（imports 缺失/被 exclude）
- 验证入口：断点 `AutoConfigurationImportSelector#selectImports`

## 坑 2：以为 property 能覆盖一切，但实际是 backoff 让位

- 典型表现：配了 enabled=true，但默认 bean 还是没出现
- 根因：或某个 starter 提供了同类型 bean，触发 `@ConditionalOnMissingBean` backoff

## 坑 3：以为“装饰器没生效”是条件没命中，但实际是注入选择规则

- 典型表现：容器里有两个同类型 bean，但拿到的不是直觉里的那个
- 验证：看是否 `@Primary` / 是否有 `@Qualifier`

## 小结与下一章

大多数误判来自：只看某一个条件注解，而忽略了 imports 与 backoff（或忽略了多个 bean 的选择规则）。

下一章见：[第 198 章：99 - Self Check（springboot-autoconfiguration）](appendix-self-check.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[part-01-autoconfig-basics/01-conditional-and-backoff.md](autoconfig-basics-conditional-and-backoff.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
