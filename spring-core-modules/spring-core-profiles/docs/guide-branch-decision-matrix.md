# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：关键分支矩阵（Branch Decision Matrix）展开，主线可以概括为：分支发生在：activeProfiles 如何设置、ConditionEvaluator 如何决定 skip。

    把 Profiles 的关键分支（激活/优先级/条件装配）整理成矩阵表；每行都对应可跑入口与观察点。

    对照入口：`SpringCoreProfilesBranchMatrixLabTest`。需要下探源码时，可以从 `AbstractEnvironment` / `ConditionEvaluator` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Profiles Debugger Pack）](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. Profile 激活与 Bean 选择](profiles-profile-activation-and-bean-selection.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `SpringCoreProfilesBranchMatrixLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：分支发生在：activeProfiles 如何设置、ConditionEvaluator 如何决定 skip。需要下探源码时，可以从 `AbstractEnvironment` / `ConditionEvaluator` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| Profile 激活 | 设置 activeProfiles | `@Profile` bean 被选中/跳过 | `SpringCoreProfilesLabTest` | activeProfiles |
| Profile 优先级 | 多种方式设置 profile/属性 | 最终生效顺序符合预期 | `SpringCoreProfilesProfilePrecedenceLabTest` | PropertySources 命中 |
| 条件装配 | `@Profile`/条件 | `ConditionEvaluator#shouldSkip` 决策 | `SpringCoreProfilesProfilePrecedenceLabTest` | shouldSkip 返回值 |

## 推荐运行命令

- `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](appendix-self-check.md)

## 小结与下一章

分支发生在：activeProfiles 如何设置、ConditionEvaluator 如何决定 skip。

下一章见：[第 152 章：01：Profile 激活与 Bean 选择（最小可复现主线）](profiles-profile-activation-and-bean-selection.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreProfilesBranchMatrixLabTest`
- Lab：`SpringCoreProfilesProfilePrecedenceLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-profiles/01-profile-activation-and-bean-selection.md](profiles-profile-activation-and-bean-selection.md)

<!-- BOOKIFY:END -->

