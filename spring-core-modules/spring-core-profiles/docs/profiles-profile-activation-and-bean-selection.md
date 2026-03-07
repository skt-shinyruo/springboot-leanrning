# 01. Profile 激活与 Bean 选择
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Profile 激活与 Bean 选择展开，主线可以概括为：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。

    先运行 `SpringCoreProfilesLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。

    需要下探源码时，可以从 `org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Profiles）](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑（Spring Core Profiles）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreProfilesLabTest`

## 机制主线

本章目标：本章结束后应能回答下面三件事：
1. Profile 可以从哪里激活？（配置文件、环境变量、启动参数、测试注解）
2. `@Profile` 的语义是什么？（“是否注册这个 bean 定义”）
3. 当同一接口有多实现时，在不同 profile 下如何稳定选择到预期的实现？

## 最小可运行实验（Lab）

- Lab：`SpringCoreProfilesLabTest`
- 建议命令：`mvn -pl :spring-core-profiles test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

> 验证入口（可跑）：`SpringCoreProfilesLabTest`

对应验证入口（最小可复现）：
- `src/test/java/com/learning/springboot/springcoreprofiles/**`

## 常见坑与边界

### 坑点 1：把 default profile 当成 active profile，导致“我以为激活了但其实没有”

以为某个 profile（如 dev）已经生效，但实际 `Environment#getActiveProfiles()` 为空

`spring.profiles.default` 只是兜底；只有 `spring.profiles.active`（或等价来源）才算显式激活

- 默认 profiles 含 default：`SpringCoreProfilesProfilePrecedenceLabTest#defaultProfilesContainDefault_whenNoActiveProfilesConfigured`
- active 覆盖 default：`SpringCoreProfilesProfilePrecedenceLabTest#springProfilesActiveOverridesSpringProfilesDefault`

排障先锁定 active/default 的事实（测试里断言 Environment），再看 `@Profile`/negation/条件组合的生效结果

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreProfilesLabTest`

上一章：[00-deep-dive-guide](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
