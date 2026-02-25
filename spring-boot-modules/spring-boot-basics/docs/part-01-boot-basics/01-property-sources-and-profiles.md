# 01. 配置来源（PropertySources）与 Profile 覆盖
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（谁覆盖谁）"

    这章解决的不是“怎么写配置”，而是“为什么我写了配置却没生效”。在 Spring Boot 里，同一个 key 的最终值取决于 `PropertySources` 的优先级；Profile 则同时影响“哪些配置文件参与”与“哪些 Bean 会被注册”。

    - 最小证据入口：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
    - 一眼观察点：`Environment#getActiveProfiles()`、`Environment#getProperty("app.greeting")`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-basics）](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. `@ConfigurationProperties` 绑定与类型转换](02-configuration-properties-binding.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01. 配置来源（PropertySources）与 Profile 覆盖**
- 建议入口：优先运行 `BootBasicsDefaultLabTest`（默认）与 `BootBasicsDevLabTest`（dev）（见文末“对应 Lab/Test”），先把 active profiles 与最终属性值钉住，再解释“谁覆盖谁”。



## 先把结论放前面：最终值不在某个文件里，而在 `Environment`

你在项目里“改配置没生效”时，最容易掉进一个误区：一直盯着某个 `application.properties` 文件找原因。

但 Spring Boot 真正在运行时依赖的事实来源是 `Environment`。文件、环境变量、命令行参数、测试覆盖……最后都会汇总到这里。排障时不要猜，先把两件事实固定下来：

- 当前到底激活了哪些 profile：`environment.getActiveProfiles()`
- 你关心的 key 的最终值是什么：`environment.getProperty("app.greeting")`

这两句一旦确定，很多争论（“是不是 dev 配置没加载”“是不是测试覆盖了”）会直接结束。

## 机制主线

### 1) PropertySources：多个来源合并成一个“最终视图”

在这模块里，你会看到三种最常见的来源叠加：

1. 默认配置（`application.properties`）
2. dev profile 配置（`application-dev.properties`，只有 profile 激活时才参与）
3. 测试覆盖（`@SpringBootTest(properties = ...)`，它会把同名 key 盖掉）

它们不会各自“独立生效”，而是一起组成 `Environment` 里的 `PropertySources`。同名 key 取哪个值，取决于优先级；而优先级最直观的判断方式，是看测试断言“最终值是什么”。

### 2) Profile 不只是“文件开关”，它也会影响 Bean 注册

Profile 经常被误解成“只决定 `application-<profile>.properties` 参不参与”。在这个模块里你还会看到另一条线：

- 某些 Bean 的存在与否也受 `@Profile` 控制

所以你可能会遇到两种完全不同的问题：

- 属性值不对（属于 `Environment` 的问题）
- Bean 实现没切换（属于条件注册/`@Profile` 的问题）

这就是为什么本模块的 Lab 同时断言“属性值”和“注入的实现类”。

### 3) 这模块用三条证据链把覆盖关系钉住

你不需要记住一整张优先级表，只需要把本模块的三条最小证据链跑通：

- 默认 profile：属性来自默认文件，Bean 使用 default 实现  
  - `BootBasicsDefaultLabTest#loadsDefaultProfileConfigurationAndBean`
- dev profile：属性被 dev 文件覆盖，Bean 切换到 dev 实现  
  - `BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean`
- 测试覆盖：测试覆盖的 properties 优先级更高  
  - `BootBasicsOverrideLabTest#testPropertiesOverrideApplicationProperties`

## 怎么排（先确定事实，再解释原因）

当你怀疑“配置没生效”时，我推荐的顺序是：

1. **先看最终事实**：`Environment#getActiveProfiles()` + `Environment#getProperty(...)`（不要先看日志）
2. **再看绑定对象**：`AppProperties` 里最终是什么值（类型安全、好断言）
3. **最后看行为结果**：哪个实现类被注入？输出/行为是否符合最终值？

这三步分别对应本模块的三组 Lab，你可以直接复跑对照。

## 源码与断点（够用版）

这章不要求你把整条配置链路背下来；但你至少应该知道两个“排障入口”：

- `org.springframework.core.env.PropertySourcesPropertyResolver#getProperty`：最终取值点（会告诉你“命中了哪个来源”）
- `org.springframework.core.env.AbstractEnvironment#getActiveProfiles`：profile 事实来源

## 常见坑与边界

### 坑点 1：把“属性覆盖”误当成“Bean 一定会切换”

你改了 `app.greeting`，发现输出变了，于是以为“实现类也会跟着切换”；或者反过来，看到实现类变了，以为“属性一定来自 dev 文件”。这两条线其实是独立的。

证据入口：

- 属性 + Bean 同时变化：`BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean`
- 只覆盖属性但不切换 Bean：`BootBasicsOverrideLabTest#beansSeeOverriddenProperties`

### 坑点 2：只盯文件，不看最终值

如果你没有先断言 `environment.getProperty("app.xxx")` 的最终值，那么后面所有分析都可能是“基于错误前提的推理”。

## 小结与下一章

- 本章把“谁覆盖谁”讲清楚后，下一章进入 `@ConfigurationProperties`：把字符串配置变成类型安全对象，并把“类型转换/绑定失败”变成可断言的事实。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
- Test file：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-boot-basics/02-configuration-properties-binding.md](02-configuration-properties-binding.md)

<!-- BOOKIFY:END -->
