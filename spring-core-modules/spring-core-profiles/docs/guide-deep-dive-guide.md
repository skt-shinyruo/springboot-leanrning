# 02. 深挖指南（Spring Core Profiles）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕深挖指南（Spring Core Profiles）展开，主线可以概括为：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。

    先运行 `SpringCoreProfilesLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。

    需要下探源码时，可以从 `org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Profiles](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. Profile 激活与 Bean 选择](profiles-profile-activation-and-bean-selection.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreProfilesLabTest`

## 机制主线

Profiles 的“深挖主线”是把“预期激活了 dev”为何没生效拆成可断言问题：

1. **profile 从哪里来**：active/default 的来源与优先级
2. **profile 影响什么**：不仅影响配置文件，更影响 Bean/Configuration 是否参与注册
3. **如何排障**：先锁定 Environment 的 active/default，再看条件匹配与 negation 语义

### 1) 时间线：profile 如何决定“哪些配置类/bean 生效”

1. Spring 构建 `Environment`（含 activeProfiles 与 defaultProfiles）
2. `@Profile`/条件注解在 bean 注册阶段被评估
3. 符合条件的配置类/bean 进入上下文，不符合的直接跳过

### 2) 关键参与者

- `Environment#getActiveProfiles` / `#getDefaultProfiles`：profile 证据来源
- `spring.profiles.active`：显式激活 profile（优先级最高）
- `spring.profiles.default`：当 active 为空时才兜底生效
- `@Profile("dev")` 与 `@Profile("!dev")`：正向匹配与否定匹配（negation）
- `ApplicationContextRunner`：用最小上下文把 profile 行为写成可回归测试

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **默认 profile：未配置 active 时，defaultProfiles 包含 default**
   - 验证：`SpringCoreProfilesProfilePrecedenceLabTest#defaultProfilesContainDefault_whenNoActiveProfilesConfigured`
2. **优先级：active 覆盖 default（default 只是兜底）**
   - 验证：`SpringCoreProfilesProfilePrecedenceLabTest#springProfilesActiveOverridesSpringProfilesDefault`
3. **多 active profiles：逗号分隔依然生效，dev 存在则 dev 生效**
   - 验证：`SpringCoreProfilesProfilePrecedenceLabTest#multipleActiveProfilesStillActivateDev_andDisableNegationProfile`
4. **negation 语义：`!dev` 在 dev 不激活时生效，在 dev 激活时失效**
   - 验证：`SpringCoreProfilesLabTest#profileNegationActivatesNonDevConfigurationWhenDevIsNotActive` / `SpringCoreProfilesLabTest#profileNegationDeactivatesNonDevConfigurationWhenDevIsActive`
5. **profile 与其他条件组合：dev 可以覆盖其他 conditional 选择**
   - 验证：`SpringCoreProfilesLabTest#devProfileWinsOverNonDevConditionals`

## 源码与断点


断点入口（排障最短路径）：

- 先锁定 profile 的“事实”：
  - 在 `ApplicationContextRunner` 运行块里断言 `Environment#getActiveProfiles` / `#getDefaultProfiles`
- 再看 bean 是否真的注册：
  - 对照 `SpringCoreProfilesLabTest#configurationClassesArePartOfTheContextWhenActivated`，用“有没有 bean”确认条件是否生效

## 最小可运行实验（Lab）

- Lab：`SpringCoreProfilesLabTest`
- 运行命令：`mvn -pl :spring-core-profiles test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

> 验证入口（可跑）：`SpringCoreProfilesLabTest`

阅读顺序：
1. 先看 Profile 的激活来源与优先级（Part 01）
2. 再用测试验证“同一接口的多实现如何被选择”（Part 01）
3. 最后对照常见坑（Appendix）

配套验证入口：
- 实验/练习：见 `src/test/java/com/learning/springboot/springcoreprofiles/**`

## 常见坑与边界

如果是带着线上问题来的，先对照本模块 Appendix（common pitfalls/self-check），再回到主线章节逐一核对。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreProfilesLabTest`

上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01-profile-activation-and-bean-selection](profiles-profile-activation-and-bean-selection.md)

<!-- BOOKIFY:END -->
