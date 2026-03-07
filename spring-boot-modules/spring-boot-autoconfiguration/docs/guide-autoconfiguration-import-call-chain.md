# 03. AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）展开，主线可以概括为：imports 决定“候选集合”，Condition 决定“是否注册”，backoff 决定“是否让位给用户配置”。

    先跑 `BootAutoConfigurationLabTest`，再按本文从 `AutoConfigurationImportSelector` 走到 `ConditionEvaluator`，最后回到断点地图把入口固化。

    需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConfigurationClassPostProcessor#processConfigBeanDefinitions` / `ConditionEvaluator#shouldSkip` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖导读：把“自动配置导入 + 条件决策”落到源码与断点](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（AutoConfiguration Debugger Pack）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：imports 决定“候选集合”，Condition 决定“是否注册”，backoff 决定“是否让位给用户配置”。需要下探源码时，可以从 `AutoConfigurationImportSelector#selectImports` / `ConfigurationClassPostProcessor#processConfigBeanDefinitions` / `ConditionEvaluator#shouldSkip` 这些入口切入。


## 应能复述的“最短调用链”

### 第 0 段：AutoConfiguration 的候选集合从哪里来？

核心机制是：Spring Boot 会从 classpath 上读取 auto-config 列表（imports），再把它们当作“配置类”交给 Spring 的配置类处理链。

关键入口（按排障优先级）：

1. `AutoConfigurationImportSelector#selectImports`
2. `AutoConfigurationImports#load`（读取 imports 文件并聚合）

### 第 1 段：这些候选什么时候变成 BeanDefinition？

auto-config 最终会进入 Spring 的“配置类解析与注册”主线（可以把它理解成：配置类也会被解析成 BeanDefinition）。

关键入口：

1. `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
2. `ConfigurationClassParser#parse`
3. `ConfigurationClassBeanDefinitionReader#loadBeanDefinitions`

### 第 2 段：为什么它会/不会注册？

条件装配的核心抓手是：

- `ConditionEvaluator#shouldSkip`

当看到 `@ConditionalOnProperty/@ConditionalOnClass/@ConditionalOnBean/@ConditionalOnMissingBean` 时，不要把它当成“注解魔法”，把它当成：

> 配置类/`@Bean` 方法在注册前的一次 if 判断。

### 第 3 段：backoff（让位）发生在什么时候？

最常见的 backoff 是：

- `@ConditionalOnMissingBean`

它会在“容器已有/将有某个 bean”时决定不再注册默认 bean，最终表现为：

- 能拿到用户自定义 bean，但拿不到默认 bean

本模块的证据链入口：

- `BootAutoConfigurationLabTest#userBeanOverridesAutoConfig_backoffOccurs`

## 小结与下一章

- 本章把 imports/condition/backoff 串成了一条可复述调用链；下一章给出“断点/观察点清单”，用于真实项目排障。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
