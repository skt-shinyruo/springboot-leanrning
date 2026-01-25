# 第 6 章：02：`@ConfigurationProperties` 绑定与类型转换
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：`@ConfigurationProperties` 绑定与类型转换
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。
    - 原理：配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。
    - 源码入口：`org.springframework.core.env.ConfigurableEnvironment` / `org.springframework.core.env.PropertySource` / `org.springframework.boot.context.properties.bind.Binder` / `org.springframework.boot.context.properties.ConfigurationPropertiesBinder`
    - 推荐 Lab：`BootBasicsDefaultLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 5 章：01：配置来源（PropertySources）与 Profile 覆盖](005-01-property-sources-and-profiles.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 7 章：90：常见坑清单（建议反复对照）](../appendix/007-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02：`@ConfigurationProperties` 绑定与类型转换**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBasicsDefaultLabTest` / `BootBasicsOverrideLabTest`
    - Test file：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`

## 机制主线

本章聚焦 `@ConfigurationProperties`：它如何把 `application.properties` 的键值绑定到 Java 对象，以及常见失败模式。

## 你应该观察到什么（What to observe）
<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「02：`@ConfigurationProperties` 绑定与类型转换」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.ConfigurableEnvironment`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「02：`@ConfigurationProperties` 绑定与类型转换」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.PropertySource`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「02：`@ConfigurationProperties` 绑定与类型转换」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.context.properties.bind.Binder`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootBasicsDefaultLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 机制解释（Why）

- `@ConfigurationProperties(prefix = "app")` 负责声明绑定前缀。
- Spring Boot 在启动阶段会创建 binder，把配置键映射到对象字段：
  - `featureEnabled` ↔ `feature-enabled`（kebab-case 映射）
  - 字段类型转换（string → boolean / number / enum 等）

- 只改了 `application.properties` 但没生效：可能是 profile 覆盖/测试覆盖导致你看的不是那份配置。
- 断言依赖完整异常全文：不同版本异常文本可能略变，建议断言关键片段即可（比如“绑定失败/类型转换失败”）。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBasicsDefaultLabTest` / `BootBasicsOverrideLabTest`
- 建议命令：`mvn -pl :spring-boot-basics test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口

- 绑定与读取：
  - `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java`
- 测试级覆盖后仍能绑定：
  - `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`
- Exercises（建议做）：
  - `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`
    - `exercise_addNewPropertyField`
    - `exercise_invalidPropertyType`

- `AppProperties` 的字段值来自 `application.properties`（默认）或 `application-dev.properties`（profile）或测试覆盖（test properties）。
- `Environment#getProperty("app.feature-enabled")` 返回的是字符串 `"true"/"false"`，但 `AppProperties#isFeatureEnabled()` 是 boolean：**类型转换发生在绑定阶段**。

## Debug 建议

- 绑定没生效：
  - 先确认是否启用了扫描（本模块使用 `@ConfigurationPropertiesScan`）
  - 再检查 prefix、字段名、kebab-case 映射是否正确
- 类型错误：
  - 先写一个“必然失败”的实验，用测试断言固定错误信息关键片段（Exercise 引导你做）

## 常见坑与边界

## 常见坑

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsOverrideLabTest`
- Exercise：`BootBasicsExerciseTest`
- Test file：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`

上一章：[part-01-boot-basics/01-property-sources-and-profiles.md](005-01-property-sources-and-profiles.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/007-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
