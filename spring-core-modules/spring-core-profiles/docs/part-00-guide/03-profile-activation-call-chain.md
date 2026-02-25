# 03. Profiles 调用链（Environment → activeProfiles → 条件生效）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Profiles 调用链（Environment → activeProfiles → 条件生效）
    - 怎么使用：先跑 `SpringCoreProfilesLabTest`，把“profile 激活与 bean 选择”固化成断言，再按本文把 Environment 与条件决策串起来。
    - 原理：Profiles 的核心是 Environment：activeProfiles 决定哪些配置/bean 生效；很多条件装配最终都回到 environment/property sources。
    - 源码入口：`ConfigurableEnvironment` / `AbstractEnvironment#getActiveProfiles` / `ConditionEvaluator#shouldSkip`
    - 推荐 Lab：`SpringCoreProfilesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Profiles）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Profiles Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. Profiles 调用链（Environment → activeProfiles → 条件生效）**
- 建议入口：优先运行 `SpringCoreProfilesLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：Profiles 的核心是 Environment：activeProfiles 决定哪些配置/bean 生效；很多条件装配最终都回到 environment/property sources。
- 源码入口：`ConfigurableEnvironment` / `AbstractEnvironment#getActiveProfiles` / `ConditionEvaluator#shouldSkip`



## 最短调用链

1. 启动期构建 Environment（加载配置，计算 activeProfiles）
2. 解析配置类/bean 时遇到 `@Profile` 或条件注解
3. `ConditionEvaluator` 根据 environment 决定 shouldSkip 与否
4. 最终“有哪些 bean 被注册/被排除”落到容器里

证据链入口：

- `SpringCoreProfilesLabTest` / `SpringCoreProfilesProfilePrecedenceLabTest`

## 小结与下一章

- 小结：Profiles 的核心是 Environment：activeProfiles 决定哪些配置/bean 生效；很多条件装配最终都回到 environment/property sources。
- 下一章：[第 151 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreProfilesLabTest`
- Lab：`SpringCoreProfilesProfilePrecedenceLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
