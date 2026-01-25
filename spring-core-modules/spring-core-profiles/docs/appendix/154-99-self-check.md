# 第 154 章：自测题（Spring Core Profiles）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：自测题（Spring Core Profiles）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
    - 原理：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
    - 源码入口：`org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`
    - 推荐 Lab：`SpringCoreProfilesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 153 章：常见坑（Spring Core Profiles）](153-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 155 章：Validation 主线](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/151-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/151-04-branch-decision-matrix.md)

- 本章主题：**自测题（Spring Core Profiles）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreProfilesLabTest`

## 机制主线

这一章用“最小实验 + 可断言证据链”复盘两条主线：

1. profile 的事实来源与优先级（active/default）
2. `@Profile`/negation 对 bean 注册的影响（是否存在/选择哪一个实现）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreProfilesLabTest`
- 建议命令：`mvn -pl :spring-core-profiles test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 验证入口（可跑）：`SpringCoreProfilesLabTest`

1. 你能列出 profile 的常见激活来源与优先级吗？
2. `@Profile` 影响的是“bean 的创建”还是“bean 定义的注册”？为什么？
3. 你会用什么方式写一个最小可复现用例，验证某个 bean 只在指定 profile 下存在？

## 常见坑与边界

### 坑点 1：只改配置不做断言，导致 profile 行为靠“感觉”而不是事实

- Symptom：你改了 profile 配置，看到的行为时好时坏；不知道是 profile 没生效还是 bean 条件没命中
- Root Cause：profile 影响 bean 注册属于容器阶段行为；没有断言就很难确认“事实”
- Verification：
  - active/default 的事实：`SpringCoreProfilesProfilePrecedenceLabTest#defaultProfilesContainDefault_whenNoActiveProfilesConfigured`
  - negation 生效边界：`SpringCoreProfilesLabTest#profileNegationActivatesNonDevConfigurationWhenDevIsNotActive`
- Fix：用 `ApplicationContextRunner` 把“active profiles + bean 是否存在/类型”写成断言，再进行配置调整

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreProfilesLabTest`

上一章：[90-common-pitfalls](153-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
