# 第 4 章：00 - Deep Dive Guide（springboot-basics）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Deep Dive Guide（springboot-basics）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。
    - 原理：配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。
    - 源码入口：`org.springframework.core.env.ConfigurableEnvironment` / `org.springframework.core.env.PropertySource` / `org.springframework.boot.context.properties.bind.Binder` / `org.springframework.boot.context.properties.ConfigurationPropertiesBinder`
    - 推荐 Lab：`BootBasicsDefaultLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 3 章：主线时间线：Spring Boot Basics（已迁移到“主线之书”）](003-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 5 章：01：配置来源（PropertySources）与 Profile 覆盖](../part-01-boot-basics/005-01-property-sources-and-profiles.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00 - Deep Dive Guide（springboot-basics）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`

## 机制主线

本模块的目标不是背配置项，而是把“**配置从哪来**、**谁覆盖谁**、**Profile 到底影响了什么**”变成可断言的结论。

### 0) 时间线与关键参与者（把“配置”拆成可观察的链路）

时间线（从启动到可读）：

1. **配置被加载与合并**：多来源配置进入 `Environment` 的 `PropertySources`
2. **Profile 参与分流**：决定哪些配置文件参与、哪些 bean/配置类参与注册
3. **绑定发生**：`@ConfigurationProperties` 把字符串配置绑定成结构化对象
4. **业务读取**：你的 bean 读取绑定对象/Environment，行为由最终值决定

关键参与者（建议能点名并定位断点）：

- `Environment` / `PropertySources`：配置事实来源（最终值从这里读）
- `@Profile`：决定 bean/配置类是否参与注册（“Bean 切换”的关键开关）
- `@ConfigurationProperties`：把配置从散点收敛成对象（绑定失败/覆盖都可断言）

### 1) 配置从哪里来：PropertySources 的“合并 + 覆盖”

你可以把 Spring Boot 的配置理解成：

- 多个来源（properties/yaml、测试注入、系统属性、环境变量、命令行参数……）
- 最终合并到 `Environment` 的 `PropertySources` 里
- **同一个 key 的最终值由优先级决定**（不是“看你先写在哪里”）

本模块用最小配置 + 测试断言把“优先级”固定下来：

- 默认值可被 profile 配置覆盖
- profile 配置可被测试注入的 properties 覆盖

### 2) Profile 同时影响两条线：配置文件 + Bean 注册

Profile 很容易被误解为“只影响配置文件”。实际上它至少影响两条线：

1. **配置文件参与与否**（例如 `application-dev.properties`）
2. **Bean 是否注册**（例如 `@Profile("dev")` 控制某实现是否存在）

因此你要能分流判断：

- 这是“属性没覆盖”问题？
- 还是“Bean 没切换/没注册”问题？

### 3) `@ConfigurationProperties`：Binder 负责“字符串 → 结构化对象”

`@ConfigurationProperties` 的关键价值是让你把配置从“散落的 `@Value`”收敛到一个对象上。

学习时最稳定的观察点不是日志，而是：

- 直接断言绑定后的对象字段值（以及依赖它的 bean 行为）

### 4) 本模块的关键分支（2–5 条，默认可回归）

1. **默认 profile：属性绑定 → bean 行为**
   - 验证：`BootBasicsDefaultLabTest#loadsDefaultProfileConfigurationAndBean` / `BootBasicsDefaultLabTest#greetingProviderUsesBoundProperties`
2. **dev profile：profile 激活 → 配置覆盖 + Bean 切换**
   - 验证：`BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean` / `BootBasicsDevLabTest#devProfileOverridesFeatureFlag`
3. **测试注入覆盖：测试级 properties 的优先级更高**
   - 验证：`BootBasicsOverrideLabTest#testPropertiesOverrideApplicationProperties` / `BootBasicsOverrideLabTest#beansSeeOverriddenProperties`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。
  
建议断点（从“最终值不对”快速回到“来源与覆盖”）：

- profile 事实：`org.springframework.core.env.AbstractEnvironment#getActiveProfiles`
- 最终取值：`org.springframework.core.env.PropertySourcesPropertyResolver#getProperty`
- 绑定入口（可选深挖）：`org.springframework.boot.context.properties.bind.Binder#bind`

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
- 建议命令：`mvn -pl :spring-boot-basics test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 如何跑实验（建议）
- 运行本模块全部测试：`mvn -pl :spring-boot-basics test`

## Labs & Exercises 快速入口
- Labs（观察点 + 断言）：见 `docs/README.md` 的 “Labs & Exercises”

## 对应 Lab（可运行）

- `BootBasicsDefaultLabTest`
- `BootBasicsDevLabTest`
- `BootBasicsOverrideLabTest`
- `BootBasicsExerciseTest`

## 常见坑与边界

### 坑点 1：配置覆盖了，但 Bean 没切换（或反过来）

- Symptom：你看到属性值变了/没变，但注入到业务里的实现类与直觉不一致
- Root Cause：Profile 同时影响“配置文件参与”与“Bean 是否注册”，它们是两条线
- Verification：先跑 `BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean`，再对照 `BootBasicsDefaultLabTest#activeProfilesDoNotContainDevByDefault`
- Breakpoints（可选）：`org.springframework.core.env.AbstractEnvironment#getActiveProfiles`（确认 profile），以及绑定对象字段读取点（你的断言位置）
- Fix：先用测试断言锁住“profile 是否真的激活 + 最终属性值是什么”，再讨论 Bean 条件与实现切换

## 推荐阅读顺序
1. [01-property-sources-and-profiles](../part-01-boot-basics/005-01-property-sources-and-profiles.md)
2. [02-configuration-properties-binding](../part-01-boot-basics/006-02-configuration-properties-binding.md)
3. [90-common-pitfalls](../appendix/007-90-common-pitfalls.md)
4. [99-self-check](../appendix/008-99-self-check.md)

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
- Exercise：`BootBasicsExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-boot-basics/01-property-sources-and-profiles.md](../part-01-boot-basics/005-01-property-sources-and-profiles.md)

<!-- BOOKIFY:END -->
