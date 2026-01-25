# 第 153 章：常见坑（Spring Core Profiles）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：常见坑（Spring Core Profiles）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
    - 原理：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
    - 源码入口：`org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`
    - 推荐 Lab：`SpringCoreProfilesLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 152 章：Profile 激活与 Bean 选择](../part-01-profiles/152-01-profile-activation-and-bean-selection.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 154 章：自测题（Spring Core Profiles）](154-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[151-02-breakpoint-map.md](../part-00-guide/151-02-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[151-04-branch-decision-matrix.md](../part-00-guide/151-04-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[154-99-self-check.md](154-99-self-check.md)

- 本章主题：**常见坑（Spring Core Profiles）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreProfilesLabTest`

## 机制主线

- （本章主线内容暂以契约骨架兜底；建议结合源码与测试用例补齐主线解释。）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreProfilesLabTest`
- 建议命令：`mvn -pl :spring-core-profiles test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`SpringCoreProfilesLabTest`

1. **把 `@Profile` 当成运行期 if**：实际上它影响的是 bean 定义是否注册，而不是方法运行时的分支。
2. **忘记测试里显式设置 profile**：导致“本机能跑、CI 不稳定”的问题。
3. **多个 Profile 叠加时的预期不清**：应把“激活来源与优先级”写进验证用例。

1

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreProfilesLabTest` / `SpringCoreProfilesBookMatrixLabTest` / `SpringCoreProfilesBranchMatrixLabTest`

上一章：[151-04-branch-decision-matrix.md](../part-00-guide/151-04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[自检](154-99-self-check.md)

<!-- BOOKIFY:END -->
