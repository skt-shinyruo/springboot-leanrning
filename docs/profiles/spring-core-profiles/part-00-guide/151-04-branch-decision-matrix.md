# 第 151 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
    - 原理：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
    - 源码入口：`org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`
    - 推荐 Lab：`SpringCoreProfilesProfilePrecedenceLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 151 章：02：断点地图（Profiles Debugger Pack）](151-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 152 章：01：Profile 激活与 Bean 选择（最小可复现主线）](../part-01-profiles/152-01-profile-activation-and-bean-selection.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
- 回到主线：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「04：关键分支矩阵（Branch Decision Matrix）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesProfilePrecedenceLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| Profile 激活 | 设置 activeProfiles | `@Profile` bean 被选中/跳过 | `SpringCoreProfilesLabTest` | activeProfiles |
| Profile 优先级 | 多种方式设置 profile/属性 | 最终生效顺序符合预期 | `SpringCoreProfilesProfilePrecedenceLabTest` | PropertySources 命中 |
| 条件装配 | `@Profile`/条件 | `ConditionEvaluator#shouldSkip` 决策 | `SpringCoreProfilesProfilePrecedenceLabTest` | shouldSkip 返回值 |

## 推荐运行命令

- `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/153-90-common-pitfalls.md`](../appendix/153-90-common-pitfalls.md)
- 自检：[`../appendix/154-99-self-check.md`](../appendix/154-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.annotation.Profile`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.annotation.ConditionEvaluator#shouldSkip`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreProfilesProfilePrecedenceLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreProfilesProfilePrecedenceLabTest` / `SpringCoreProfilesLabTest` / `SpringCoreProfilesBranchMatrixLabTest`

上一章：[151-02-breakpoint-map.md](151-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/153-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
