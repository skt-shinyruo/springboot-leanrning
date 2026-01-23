# 第 151 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把 Profiles 的关键分支（激活/优先级/条件装配）整理成矩阵表；每行都对应可跑入口与观察点。
    - 原理：分支发生在：activeProfiles 如何设置、ConditionEvaluator 如何决定 skip。
    - 源码入口：`AbstractEnvironment` / `ConditionEvaluator`
    - 推荐 Lab：`SpringCoreProfilesBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 151 章：02：断点地图（Profiles Debugger Pack）](151-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 152 章：01：Profile 激活与 Bean 选择（最小可复现主线）](../part-01-profiles/152-01-profile-activation-and-bean-selection.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreProfilesBranchMatrixLabTest`
- Lab：`SpringCoreProfilesProfilePrecedenceLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](151-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-profiles/01-profile-activation-and-bean-selection.md](../part-01-profiles/152-01-profile-activation-and-bean-selection.md)

<!-- BOOKIFY:END -->

