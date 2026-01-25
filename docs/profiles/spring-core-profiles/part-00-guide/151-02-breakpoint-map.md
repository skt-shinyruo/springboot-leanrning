# 第 151 章：02：断点地图（Profiles Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Profiles Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
    - 原理：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
    - 源码入口：`org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`
    - 推荐 Lab：`SpringCoreProfilesProfilePrecedenceLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 151 章：00 - Deep Dive Guide（spring-core-profiles）](151-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 151 章：04：关键分支矩阵（Branch Decision Matrix）](151-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Profiles Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
- 回到主线：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- Profiles 排障的第一步：确认 activeProfiles（不要先猜）。
- 第二步：确认“哪些配置类/bean 被跳过”（ConditionEvaluator 是决定点）。

## 运行入口（建议先跑）

- Book Matrix：`SpringCoreProfilesBookMatrixLabTest`
- Branch Matrix：`SpringCoreProfilesBranchMatrixLabTest`

## 断点（Profile 与条件装配）

- `org.springframework.core.env.AbstractEnvironment#getActiveProfiles`
- `org.springframework.context.annotation.ConditionEvaluator#shouldSkip`
- `org.springframework.context.annotation.ProfileCondition#matches`

## Watchpoints（建议）

- `environment.getActiveProfiles()` / `getDefaultProfiles()`
- bean 是否注册（从 `BeanDefinitionRegistry` 或测试断言处观察）
- property 覆盖：`Environment#getProperty(key)`

## 排障入口（Playbook）

- 常见坑：[`../appendix/153-90-common-pitfalls.md`](../appendix/153-90-common-pitfalls.md)
- 自检：[`../appendix/154-99-self-check.md`](../appendix/154-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreProfilesProfilePrecedenceLabTest` / `SpringCoreProfilesBookMatrixLabTest` / `SpringCoreProfilesBranchMatrixLabTest`

上一章：[Profile 激活与 Bean 选择](../part-01-profiles/152-01-profile-activation-and-bean-selection.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[151-04-branch-decision-matrix.md](151-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
