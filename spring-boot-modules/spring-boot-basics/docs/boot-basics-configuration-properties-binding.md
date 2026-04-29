# 02. `@ConfigurationProperties` 绑定与类型转换
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（把配置变成对象）"

    在 Boot 里，`Environment` 里的属性值本质上是字符串；`@ConfigurationProperties` 的价值是把它们绑定成类型安全对象，并把“覆盖/类型转换/绑定失败”变成可断言的事实，而不是只凭日志猜测。

    - 最小证据入口：`BootBasicsDefaultLabTest` / `BootBasicsOverrideLabTest`
    - 练习入口：`BootBasicsExerciseTest`（新增字段、构造绑定失败）
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 配置来源（PropertySources）与 Profile 覆盖](boot-basics-property-sources-and-profiles.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 常见坑清单（排查时对照）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootBasicsDefaultLabTest`（见文末“对应实验/测试”），对照 `Environment#getProperty(...)` 的字符串与绑定对象的类型值，建立““绑定=取值+转换”的基础判断。


## 可能已经见过这个现象：Environment 里是字符串，注入到 Bean 里却成了 boolean

在 `BootBasicsDefaultLabTest` 里有两条断言很值得对照着看：

- `Environment#getProperty("app.feature-enabled")` 返回 `"false"`（字符串）
- `AppProperties#isFeatureEnabled()` 却是 `false`（boolean）

这不是“Spring 变隐式机制”，而是 **绑定阶段发生了类型转换**：Binder 把字符串转换成目标字段的类型。

### 1) `@ConfigurationProperties(prefix = "app")`：声明“要绑定哪一段配置”

在本模块里，配置对象是 `AppProperties`：

- 前缀：`app`
- 字段：`name` / `greeting` / `featureEnabled`

这意味着它会尝试从 `Environment` 里读取：

- `app.name`
- `app.greeting`
- `app.feature-enabled`（注意 kebab-case）

### 2) Binder：负责“取值 + 绑定 + 类型转换”

无需记住 Binder 的全部细节，只需要抓住两个稳定事实：

1. **最终取值仍来自 `Environment`**（上一章的结论仍然成立）
2. **绑定会做类型转换**（字符串 → boolean/number/enum...）

本模块用 `BootBasicsOverrideLabTest` 证明了另一件很实用的事：当在测试里覆盖属性值时，绑定结果也会跟着变——因为绑定读取的是“最终值”。

### 3) 绑定的前提：配置类必须被扫描/启用

本模块的启用方式是 `BootBasicsApplication` 上的 `@ConfigurationPropertiesScan`。如果在自己的项目里遇到“写了 `@ConfigurationProperties` 但怎么也注入不到值”，第一件事不是怀疑配置文件，而是确认它是不是被扫描到了。

## 怎么验证（tests 就是最短证据链）

- 默认绑定 + 读取原始属性：`BootBasicsDefaultLabTest#canReadRawPropertyValuesFromEnvironment`
- 测试覆盖后仍能绑定：`BootBasicsOverrideLabTest#testPropertiesOverrideApplicationProperties`

运行命令：

- `mvn -q -pl :spring-boot-basics test`

## Debug 入口（够用版）

当排查绑定问题时，更快收敛的两个入口是：

- `org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor#postProcessBeforeInitialization`（绑定发生点）
- `org.springframework.boot.context.properties.bind.Binder#bind`（绑定与转换发生处）

如果想确认“最终值来自哪个来源”，回到上一章的取值点：

- `org.springframework.core.env.PropertySourcesPropertyResolver#getProperty`

## 常见坑与边界

### 坑点 1：prefix 写错 / key 写错

最常见也最隐蔽：以为写的是 `app.feature-enabled`，但实际 key 少了/多了一个字符。因为它不会像“编译错误”那样提醒，只会表现为“绑定结果一直是默认值”。

### 坑点 2：kebab-case 映射误判

Java 字段 `featureEnabled` 对应的配置 key 是 `feature-enabled`。如果写成 `featureEnabled`，可能会得到“表面上像没绑定”的错觉。

### 坑点 3：类型转换失败（别断言整段异常文本）

练习里构造一个必然失败的例子（例如 `app.feature-enabled=not-a-boolean`）。断言时只抓关键片段（“绑定失败/类型转换失败”），不要依赖完整异常全文（不同版本可能有细微差异）。

## 练习（做 2 个就够）

- 新增字段并验证绑定：`BootBasicsExerciseTest#exercise_addNewPropertyField`
- 构造类型错误并断言失败：`BootBasicsExerciseTest#exercise_invalidPropertyType`

## 小结与下一章

- 本章把“字符串配置 → 类型安全对象”讲清楚后，附录会把最常见的误解整理成排障短文与自测题。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsOverrideLabTest`
- Exercise：`BootBasicsExerciseTest`
- 测试文件：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java` / `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`

上一章：[boot-basics-property-sources-and-profiles.md](boot-basics-property-sources-and-profiles.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
