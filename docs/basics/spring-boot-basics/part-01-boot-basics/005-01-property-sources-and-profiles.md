# 第 5 章：01：配置来源（PropertySources）与 Profile 覆盖
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：配置来源（PropertySources）与 Profile 覆盖
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。
    - 原理：配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。
    - 源码入口：`org.springframework.core.env.ConfigurableEnvironment` / `org.springframework.core.env.PropertySource` / `org.springframework.boot.context.properties.bind.Binder` / `org.springframework.boot.context.properties.ConfigurationPropertiesBinder`
    - 推荐 Lab：`BootBasicsDefaultLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 4 章：00 - Deep Dive Guide（springboot-basics）](../part-00-guide/004-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 6 章：02：`@ConfigurationProperties` 绑定与类型转换](006-02-configuration-properties-binding.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：配置来源（PropertySources）与 Profile 覆盖**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
    - Test file：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`

## 机制主线


## 你应该观察到什么（What to observe）
<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「01：配置来源（PropertySources）与 Profile 覆盖」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.ConfigurableEnvironment`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「01：配置来源（PropertySources）与 Profile 覆盖」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.PropertySource`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「01：配置来源（PropertySources）与 Profile 覆盖」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.context.properties.bind.Binder`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootBasicsDefaultLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 机制解释（Why）

你可以把 Spring Boot 的配置加载理解成两件事：

2) **根据 Profile 决定哪些配置文件与哪些 Bean 生效**
- `@ActiveProfiles("dev")` 会在测试中激活 dev profile。
- `application-dev.properties` 只在 `dev` 激活时参与。
- Bean 切换通常来自 `@Profile` 或条件装配（本模块用“不同实现类 + profile”的方式让现象更直观）。

- 把“Profile 覆盖”误当成“Bean 一定会切换”：配置覆盖和 Bean 注册是两条线，Bean 是否存在取决于条件注解是否匹配。
- 误判优先级：同一个 key 可能来自多个来源，建议用测试固化“谁覆盖谁”的结论。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
- 建议命令：`mvn -pl :spring-boot-basics test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

本章把 `springboot-basics` 里“配置从哪里来、谁覆盖谁”讲清楚，并对应到可运行的实验测试。

## 实验入口（先跑再看）

- 默认配置（无 dev profile）：
  - `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java`
- 启用 dev profile（覆盖配置 + Bean 切换）：
  - `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java`
- 测试级覆盖（properties precedence）：
  - `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`

1. **默认情况下**，`Environment#getActiveProfiles()` 不包含 `dev`，并且会存在 `default` profile。
2. **当启用 `dev` profile**：
   - `application-dev.properties` 覆盖 `application.properties` 的同名 key（例如 `app.greeting`）
   - 同一注入点的 Bean 也会被 profile 条件影响（例如 `GreetingProvider` 的实现切换）
3. **当测试通过 `@SpringBootTest(properties = {...})` 提供覆盖配置**：
   - 即使 `application.properties` 里有默认值，也会被测试注入的值覆盖（`BootBasicsOverrideLabTest`）

1) **把多个来源的配置聚合成一个 Environment**
- 配置来源（PropertySource）可能来自：
  - `application.properties` / `application-<profile>.properties`
  - 测试注入的 properties（`@SpringBootTest(properties = ...)`）
  - system properties / 环境变量 / 命令行参数（本模块练习会引导你补齐）

## Debug 建议

- 配置到底有没有进来：优先用 `Environment#getProperty("app.xxx")` + 断言，不要只看日志。
- Profile 是否真的生效：打断点看 `environment.getActiveProfiles()`。
- 想继续深挖条件装配与 profile：去看 `spring-core-profiles` 模块（更偏机制与条件评估）。

## 常见坑与边界


## 常见坑


## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
- Test file：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/004-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-boot-basics/02-configuration-properties-binding.md](006-02-configuration-properties-binding.md)

<!-- BOOKIFY:END -->
