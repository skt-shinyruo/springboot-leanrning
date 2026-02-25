# 01. Profile 激活与 Bean 选择
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Profile 激活与 Bean 选择
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
    - 原理：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
    - 源码入口：`org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`
    - 推荐 Lab：`SpringCoreProfilesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Profiles）](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑（Spring Core Profiles）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01. Profile 激活与 Bean 选择**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreProfilesLabTest`

## 机制主线

本章目标：你读完后应该能回答下面三件事：
1. Profile 可以从哪里激活？（配置文件、环境变量、启动参数、测试注解）
2. `@Profile` 的语义是什么？（“是否注册这个 bean 定义”）
3. 当同一接口有多实现时，在不同 profile 下如何稳定选择到预期的实现？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreProfilesLabTest`
- 建议命令：`mvn -pl :spring-core-profiles test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 验证入口（可跑）：`SpringCoreProfilesLabTest`

对应验证入口（最小可复现）：
- `src/test/java/com/learning/springboot/springcoreprofiles/**`

## 常见坑与边界

### 坑点 1：把 default profile 当成 active profile，导致“我以为激活了但其实没有”

- Symptom：你以为某个 profile（如 dev）已经生效，但实际 `Environment#getActiveProfiles()` 为空
- Root Cause：`spring.profiles.default` 只是兜底；只有 `spring.profiles.active`（或等价来源）才算显式激活
- Verification：
  - 默认 profiles 含 default：`SpringCoreProfilesProfilePrecedenceLabTest#defaultProfilesContainDefault_whenNoActiveProfilesConfigured`
  - active 覆盖 default：`SpringCoreProfilesProfilePrecedenceLabTest#springProfilesActiveOverridesSpringProfilesDefault`
- Fix：排障先锁定 active/default 的事实（测试里断言 Environment），再看 `@Profile`/negation/条件组合的生效结果

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreProfilesLabTest`

上一章：[00-deep-dive-guide](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->
