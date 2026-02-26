# spring-boot-basics

本模块用于学习 Spring Boot 的“最小闭环”：**应用启动**、**配置属性绑定（`@ConfigurationProperties`）**、**Profile（`dev`）切换**。

## Start Here（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

## 你将学到什么

- 理解一个最小的 Spring Boot 应用如何启动（从 `main` 到容器）
- 学会用 `@ConfigurationProperties` 绑定 `application.properties` 配置
- 学会用 Profile（例如 `dev`）切换配置与 Bean
- （进阶）在测试中覆盖 properties，并理解“配置优先级”

## 前置知识

- Java 17 / Maven 基础
- 能读懂 `application.properties` 的键值配置
- （可选）了解 Spring 的 Bean 概念（不要求深入）

## 关键命令

### 运行

- 默认配置（不指定 profile）：

```bash
mvn -pl :spring-boot-basics spring-boot:run
```

- 启用 `dev` profile：

```bash
mvn -pl :spring-boot-basics spring-boot:run -Dspring-boot.run.profiles=dev
```

运行后观察控制台输出：

- `activeProfiles` 是否变化
- `app.greeting` 是否来自 `application.properties` / `application-dev.properties`
- `greetingProvider` 是否从 `DefaultGreetingProvider` 切换到 `DevGreetingProvider`

### 测试

```bash
mvn -pl :spring-boot-basics test
```

## 推荐 docs 阅读顺序

建议按 “现象 → 覆盖规则 → 绑定机制 → 常见坑” 的顺序学习：

（docs 目录页：[`docs/README.md`](docs/README.md)）

1. [配置来源与 Profile 覆盖](docs/part-01-boot-basics/01-property-sources-and-profiles.md)
2. [`@ConfigurationProperties` 绑定与类型转换](docs/part-01-boot-basics/02-configuration-properties-binding.md)
3. [常见坑清单](docs/appendix/01-common-pitfalls.md)

对应的可运行实验（先跑后读）：
- `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java`
- `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java`
- `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`

## 概念 → 在本模块哪里能“看见”

| 你要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 你应该能解释清楚 |
| --- | --- | --- | --- |
| 默认配置加载 | [docs/part-01/01](docs/part-01-boot-basics/01-property-sources-and-profiles.md) | `BootBasicsDefaultLabTest` + `application.properties` | 默认 profile 与配置值来自哪里 |
| Profile 覆盖 | [docs/part-01/01](docs/part-01-boot-basics/01-property-sources-and-profiles.md) | `BootBasicsDevLabTest` + `application-dev.properties` | 为什么 dev 能覆盖默认配置 |
| 测试级覆盖优先级 | [docs/part-01/01](docs/part-01-boot-basics/01-property-sources-and-profiles.md) | `BootBasicsOverrideLabTest` | 为什么测试 properties 能覆盖文件配置 |
| 绑定与类型转换 | [docs/part-01/02](docs/part-01-boot-basics/02-configuration-properties-binding.md) | `AppProperties` + `BootBasicsDefaultLabTest` | string 配置如何变成 boolean/其他类型 |

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。Exercises 默认 `@Disabled`，建议逐个开启。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java` | 默认 profile 下的配置绑定与 Bean 选择 | ⭐ | 回到本 README 的“运行/观察” + 看 `application.properties` |
| Lab | `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java` | `dev` profile 的配置覆盖与 Bean 切换 | ⭐ | 对照 `application-dev.properties` 与 `@Profile` |
| Lab | `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java` | 测试级 property override 的优先级 | ⭐⭐ | 回看 `Environment` 的 property precedence |
| Exercise | `src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java` | 按提示完成“新增配置/优先级/条件装配/更快测试”等练习 | ⭐–⭐⭐ | 先从第 1 个 `@Disabled` 练习开始 |

## 常见 Debug 路径

- 配置没生效：先看 `environment.getActiveProfiles()`，再看 `environment.getProperty("app.xxx")`
- `@ConfigurationProperties` 没绑定：检查 prefix、字段命名与 kebab-case 映射、是否被扫描/启用
- Bean 没切换：确认 `@Profile` 条件、以及当前 profile 是否真的激活
- 测试覆盖不生效：检查“配置来源”叠加（test properties / system properties / application.properties）

## 扩展练习（可选）

- 新增一个配置项 `app.color`，并在启动输出里打印出来
- 增加一个新的 profile（例如 `prod`），让它输出不同的 greeting

## 参考

- Spring Boot Reference: Configuration Properties
