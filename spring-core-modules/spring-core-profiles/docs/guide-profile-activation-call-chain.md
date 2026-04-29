# 03. Profiles 调用链（Environment → activeProfiles → 条件生效）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：Profiles 调用链（Environment → activeProfiles → 条件生效）展开，主线可以概括为：Profiles 的核心是 Environment：activeProfiles 决定哪些配置/bean 生效；很多条件装配最终都回到 environment/property sources。

    先跑 `SpringCoreProfilesLabTest`，把“profile 激活与 bean 选择”固化成断言，再按本章把 Environment 与条件决策串起来。

    需要下探源码时，可以从 `ConfigurableEnvironment` / `AbstractEnvironment#getActiveProfiles` / `ConditionEvaluator#shouldSkip` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Profiles）](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Profiles）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `SpringCoreProfilesLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：Profiles 的核心是 Environment：activeProfiles 决定哪些配置/bean 生效；很多条件装配最终都回到 environment/property sources。需要下探源码时，可以从 `ConfigurableEnvironment` / `AbstractEnvironment#getActiveProfiles` / `ConditionEvaluator#shouldSkip` 这些入口切入。


## 最短调用链

1. 启动期构建 Environment（加载配置，计算 activeProfiles）
2. 解析配置类/bean 时遇到 `@Profile` 或条件注解
3. `ConditionEvaluator` 根据 environment 决定 shouldSkip 与否
4. 最终“有哪些 bean 被注册/被排除”落到容器里

证据链入口：

- `SpringCoreProfilesLabTest` / `SpringCoreProfilesProfilePrecedenceLabTest`

## 小结与下一章

Profiles 的核心是 Environment：activeProfiles 决定哪些配置/bean 生效；很多条件装配最终都回到 environment/property sources。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreProfilesLabTest`
- Lab：`SpringCoreProfilesProfilePrecedenceLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
