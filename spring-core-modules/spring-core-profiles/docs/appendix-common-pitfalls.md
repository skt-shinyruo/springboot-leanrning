# 01. 常见坑（Spring Core Profiles）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕常见坑（Spring Core Profiles）展开，主线可以概括为：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。

    先运行 `SpringCoreProfilesLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。

    需要下探源码时，可以从 `org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. Profile 激活与 Bean 选择](profiles-profile-activation-and-bean-selection.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. 自测题（Spring Core Profiles）](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

### 排障骨架（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，可以按下面 6 步收敛问题（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/观察点/关键日志收齐：[04-breakpoint-map.md](guide-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](guide-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](appendix-self-check.md)


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreProfilesLabTest`

## 最小可运行实验（Lab）

- Lab：`SpringCoreProfilesLabTest`
- 运行命令：`mvn -pl :spring-core-profiles test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`SpringCoreProfilesLabTest`

1. **把 `@Profile` 当成运行期 if**：实际上它影响的是 bean 定义是否注册，而不是方法运行时的分支。
2. **忘记测试里显式设置 profile**：导致“本机能跑、CI 不稳定”的问题。
3. **多个 Profile 叠加时的预期不清**：应把“激活来源与优先级”写进验证用例。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreProfilesLabTest`

上一章：[01-profile-activation-and-bean-selection](profiles-profile-activation-and-bean-selection.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[99-self-check](appendix-self-check.md)

<!-- BOOKIFY:END -->
