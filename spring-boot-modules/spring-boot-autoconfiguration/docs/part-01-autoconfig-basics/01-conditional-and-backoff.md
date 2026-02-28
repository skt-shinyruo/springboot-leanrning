# 01. 条件装配与 backoff（为什么它“有时生效、有时不生效”）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：条件装配与 backoff（为什么它“有时生效、有时不生效”）展开，主线可以概括为：`@ConditionalOn...` 是启动期 if；`@ConditionalOnMissingBean` 是“默认让位策略”；顺序与 primary 决定最终注入对象。

    先跑 `BootAutoConfigurationLabTest`，把三种结果（默认/装饰/用户覆盖）固化成断言，再对照本文把每个结果映射到对应条件与 backoff 规则。

    需要下探源码时，可以从 `ConditionEvaluator#shouldSkip` / `OnBeanCondition` / `DefaultListableBeanFactory#doResolveDependency` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 关键分支矩阵（AutoConfiguration）](../part-00-guide/05-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 90 - Common Pitfalls（springboot-autoconfiguration）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：`@ConditionalOn...` 是启动期 if；`@ConditionalOnMissingBean` 是“默认让位策略”；顺序与 primary 决定最终注入对象。需要下探源码时，可以从 `ConditionEvaluator#shouldSkip` / `OnBeanCondition` / `DefaultListableBeanFactory#doResolveDependency` 这些入口切入。


## 1. 条件装配：它不是魔法，是“启动期 if”

可以把下面这些注解理解成“在注册 BeanDefinition 之前的一次判断”：

- `@ConditionalOnProperty`
- `@ConditionalOnClass`
- `@ConditionalOnBean`

在真实项目里，当遇到“为什么加了 starter 但功能不出现”，第一反应应该是：

1) 相关 auto-config 有没有被导入（imports）？
2) 导入后是不是被 condition skip 了？

## 2. backoff：默认配置为什么要让位给用户配置？

核心目标是：让框架提供“默认好用”，但不阻止自定义。

最常见策略：

- `@ConditionalOnMissingBean`：当用户提供了同类型 bean，就不再创建默认 bean

应当能用断言证明这件事，而不是凭感觉：

- `BootAutoConfigurationLabTest#userBeanOverridesAutoConfig_backoffOccurs`

## 3. 顺序与叠加：为什么最终注入的是“那个”bean？

当存在多个同类型 bean 时，最终注入对象受以下因素影响：

- 是否有 `@Primary`
- 是否有 `@Qualifier`
- 注入点是否按 name 注入

## 小结与下一章

下一章进入常见坑：把最容易误判的点列出来，并绑定到可跑入口与断点锚点。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[part-00-guide/04-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->
