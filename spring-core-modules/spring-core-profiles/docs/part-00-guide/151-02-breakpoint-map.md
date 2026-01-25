# 第 151 章：02：断点地图（Profiles Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Profiles Debugger Pack）
    - 怎么使用：先跑 `SpringCoreProfilesBranchMatrixLabTest` 固化“Profile 激活/优先级”的断言，再用断点观察 profile 如何影响条件装配与配置覆盖。
    - 原理：activeProfiles → ConditionEvaluator 决定是否跳过 bean/config → 最终 bean 集合与属性值不同。
    - 源码入口：`org.springframework.core.env.AbstractEnvironment` / `org.springframework.context.annotation.ConditionEvaluator`
    - 推荐 Lab：`SpringCoreProfilesBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 151 章：00 - Deep Dive Guide（spring-core-profiles）](151-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 151 章：04：关键分支矩阵（Branch Decision Matrix）](151-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

- Matrix：`SpringCoreProfilesBranchMatrixLabTest`
- Lab：`SpringCoreProfilesProfilePrecedenceLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](151-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](151-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

