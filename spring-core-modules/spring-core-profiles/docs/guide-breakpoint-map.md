# 04. 断点地图（Profiles）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Profiles）展开，主线可以概括为：activeProfiles → ConditionEvaluator 决定是否跳过 bean/config → 最终 bean 集合与属性值不同。

    先跑 `SpringCoreProfilesBranchMatrixLabTest` 固化“Profile 激活/优先级”的断言，再用断点观察 profile 如何影响条件装配与配置覆盖。

    需要下探源码时，可以从 `org.springframework.core.env.AbstractEnvironment` / `org.springframework.context.annotation.ConditionEvaluator` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Profiles）](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- Profiles 排障的第一步：确认 activeProfiles（不要先猜）。
- 第二步：确认“哪些配置类/bean 被跳过”（ConditionEvaluator 是决定点）。

## 运行入口（先运行）

- Book Matrix：`SpringCoreProfilesBookMatrixLabTest`
- Branch Matrix：`SpringCoreProfilesBranchMatrixLabTest`

## 断点（Profile 与条件装配）

- `org.springframework.core.env.AbstractEnvironment#getActiveProfiles`
- `org.springframework.context.annotation.ConditionEvaluator#shouldSkip`
- `org.springframework.context.annotation.ProfileCondition#matches`

## 观察点

- `environment.getActiveProfiles()` / `getDefaultProfiles()`
- bean 是否注册（从 `BeanDefinitionRegistry` 或测试断言处观察）
- property 覆盖：`Environment#getProperty(key)`

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

activeProfiles → ConditionEvaluator 决定是否跳过 bean/config → 最终 bean 集合与属性值不同。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreProfilesBranchMatrixLabTest`
- Lab：`SpringCoreProfilesProfilePrecedenceLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

