# 01. 配置来源（PropertySources）与 Profile 覆盖
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（谁覆盖谁）"

    这章解决的不是“怎么写配置”，而是“为什么我写了配置却没生效”。在 Spring Boot 里，同一个 key 的最终值取决于 `PropertySources` 的优先级；Profile 则同时影响“哪些配置文件参与”与“哪些 Bean 会被注册”。

    - 最小证据入口：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
    - 一眼观察点：`Environment#getActiveProfiles()`、`Environment#getProperty("app.greeting")`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-basics）](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. `@ConfigurationProperties` 绑定与类型转换](boot-basics-configuration-properties-binding.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 问题：为什么改了配置却没有生效

在一个实际的 Spring Boot 工程里，同一个配置 key 往往不止出现一次：默认配置文件、profile 配置文件、环境变量、命令行参数、测试覆盖……都可能提供同名值。问题因此变成了一个“事实问题”：

> 运行时的最终值到底是什么？它来自哪个来源？又会影响哪些行为？

本章用三条最小实验把事实钉住，再解释 `PropertySources` 与 Profile 的分工。读者不需要背优先级表，但需要知道如何在断点里看到“命中了哪个来源”。

---

## 实验：三条最小证据链（把覆盖关系跑成事实）

这三个入口覆盖了本模块最常见的三种组合：

- **默认**：默认配置参与，profile 未显式激活
  - `BootBasicsDefaultLabTest#loadsDefaultProfileConfigurationAndBean`
- **dev profile**：profile 配置参与，值与实现类一起切换
  - `BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean`
- **测试覆盖**：测试声明的 `properties` 覆盖应用配置
  - `BootBasicsOverrideLabTest#testPropertiesOverrideApplicationProperties`

每次运行后，至少固定两件事实：

1. `Environment#getActiveProfiles()` 的返回值；
2. `Environment#getProperty("app.greeting")` 的最终值（示例 key）。

如果实验同时断言了注入实现类，则再补充第三件事实：最终注入的是哪个实现。

---

## 解释：PropertySources 负责“值”，Profile 同时影响“值”与“装配”

### 1) PropertySources：多个来源合并成一个最终视图

从运行时视角看，配置的事实入口只有一个：`Environment`。不同来源（文件/环境/命令行/测试覆盖）在启动过程中被收集起来，形成一组有序的 `PropertySources`。同名 key 的取值并不是“平均一下”，而是按顺序命中第一个可用来源。

因此，排障时与其争论“是不是 `application-dev.properties` 没加载”，不如把问题落到断点上：最终取值点在哪里、命中了哪个 `PropertySource`。

### 2) Profile：不仅决定“哪些配置参与”，也影响“哪些 Bean 存在”

Profile 容易被理解为“配置文件开关”，但在工程里它经常同时影响两条线：

- **配置线**：`application-<profile>.properties` 是否参与合并；
- **装配线**：某些实现类是否被注册（例如 `@Profile("dev")` 的 Bean 是否存在）。

这也是本模块实验会同时断言“属性值”和“注入实现类”的原因：当行为不对时，先区分是“值不对”还是“装配不对”，才能避免走错排障方向。

---

## 观察点：在断点里确认“命中了哪个来源”

本章不要求把整条启动链路背下来，但至少应能在调试器里命中两个入口：

- `org.springframework.core.env.PropertySourcesPropertyResolver#getProperty`：最终取值点（可用于确认命中来源）
- `org.springframework.core.env.AbstractEnvironment#getActiveProfiles`：profile 的事实来源

当 `getProperty(...)` 返回的值与预期不一致时，优先看两类信息：

- 当前 `PropertySources` 的顺序（谁在前，谁就可能覆盖谁）；
- 命中的 `PropertySource` 名称与来源（文件/系统属性/命令行/测试覆盖）。

---

## 边界：属性覆盖 ≠ Bean 一定切换

在工程里常见的误判是把两条线混为一谈：看到 `app.greeting` 的值变了，就认为实现类一定会切换；或者看到实现类切换了，就认为属性一定来自某个 profile 文件。事实上：

- 属性值属于 `Environment` 的最终事实；
- 实现类是否切换属于条件注册与装配结果；
- 两者可以相关，但不是必然绑定。

对照实验入口可快速验证这一点：

- 属性与 Bean 同时变化：`BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean`
- 仅覆盖属性但不切换 Bean：`BootBasicsOverrideLabTest#beansSeeOverriddenProperties`

---

## 小结与下一章

- 排障的第一步是固定事实：active profiles 是什么、最终属性值是什么；不要先从文件与日志猜原因。
- Profile 同时影响“配置参与者”与“装配参与者”；先分清是哪一条线出问题。
- 下一章进入 `@ConfigurationProperties`：把字符串配置绑定为类型安全对象，并把转换失败/缺失字段做成可断言的边界。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
- Test file：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-boot-basics/02-configuration-properties-binding.md](boot-basics-configuration-properties-binding.md)

<!-- BOOKIFY:END -->
