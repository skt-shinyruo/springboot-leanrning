# 01. 主线时间线：Spring Profiles
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Profiles
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
    - 原理：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
    - 源码入口：`org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`
    - 推荐 Lab：`SpringCoreProfilesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 149 章：Profiles 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 深挖指南（Spring Core Profiles）](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：Profile 如何影响 Bean 的装配选择，以及它与配置源/环境变量的配合方式。
    - 读完你应该能复述：**激活 profiles → 条件匹配 → Bean 是否注册** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → 仅 1 章主线 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`SpringCoreProfilesLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Profiles —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
- 回到主线：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本章是「第 150 章：主线时间线：Spring Profiles」的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
建议先跑 `SpringCoreProfilesLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Profiles」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- Profile 是“选择器”：它决定哪一组 Bean 定义会进入容器。
- 当你遇到“本地/测试/生产行为不一致”，优先回到这里确认 profiles 的激活与匹配。

## 主线时间线（建议顺读）

1. Profile 的激活方式与 Bean 选择规则
   - 阅读：[01-profile-activation-and-bean-selection.md](../part-01-profiles/01-profile-activation-and-bean-selection.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/01-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/02-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Profiles」的生效时机/顺序/边界；断点/入口：`org.springframework.context.annotation.Profile`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Profiles」的生效时机/顺序/边界；断点/入口：`org.springframework.context.annotation.ConditionEvaluator#shouldSkip`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Profiles」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreProfilesLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
