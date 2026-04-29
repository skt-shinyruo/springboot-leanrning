# spring-boot-basics

本模块用于学习 Spring Boot 的“最小闭环”：**应用启动**、**配置属性绑定（`@ConfigurationProperties`）**、**Profile（`dev`）切换**。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix-self-check.md`](docs/appendix-self-check.md)

## 本模块完成后应能解释的内容

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

## docs 阅读顺序

按 “现象 → 覆盖规则 → 绑定机制 → 常见坑” 的顺序学习：

（目录：见本 README 的「目录（唯一顺序来源）」）

1. [配置来源与 Profile 覆盖](docs/boot-basics-property-sources-and-profiles.md)
2. [`@ConfigurationProperties` 绑定与类型转换](docs/boot-basics-configuration-properties-binding.md)
3. [常见坑清单](docs/appendix-common-pitfalls.md)

对应的可运行实验（先跑后读）：
- `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java`
- `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java`
- `src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| 默认配置加载 | [boot-basics-property-sources-and-profiles.md](docs/boot-basics-property-sources-and-profiles.md) | `BootBasicsDefaultLabTest` + `application.properties` | 默认 profile 与配置值来自哪里 |
| Profile 覆盖 | [boot-basics-property-sources-and-profiles.md](docs/boot-basics-property-sources-and-profiles.md) | `BootBasicsDevLabTest` + `application-dev.properties` | 为什么 dev 能覆盖默认配置 |
| 测试级覆盖优先级 | [boot-basics-property-sources-and-profiles.md](docs/boot-basics-property-sources-and-profiles.md) | `BootBasicsOverrideLabTest` | 为什么测试 properties 能覆盖文件配置 |
| 绑定与类型转换 | [boot-basics-configuration-properties-binding.md](docs/boot-basics-configuration-properties-binding.md) | `AppProperties` + `BootBasicsDefaultLabTest` | string 配置如何变成 boolean/其他类型 |

## 实验/练习索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。练习默认 `@Disabled`，逐个开启。

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

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Basics：从配置事实到行为边界

本模块讨论的不是“配置怎么写”，而是“配置为什么没按预期生效”。在 Spring Boot 中，配置的最终事实不在某个文件里，而在运行时的 `Environment` 中；而 `Environment` 的最终值会进一步影响 Bean 的注册、绑定与业务行为。

读完本模块后，读者应能把常见配置问题分成两类，并为每一类找到最短证据链：

- **值不对**：同名 key 在多个来源出现时，最终值来自哪里？
- **装配不对**：profile/条件装配导致某个实现没注册、或绑定对象形态与预期不一致？

---

### 10 分钟入口：先把“最终值”跑成事实
运行本模块的 Book Matrix：

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`

观察点（先钉事实，再解释原因）：

- `Environment#getActiveProfiles()`
- `Environment#getProperty("app.greeting")`（示例 key）

---

### 阅读路线（主线 → 排障 → 自证）
如果目标是顺着机制把主线跑通，可以按下面的顺序推进：

1. **先建立主线坐标**：主线时间线与深入导读
   - [主线时间线](docs/guide-mainline-timeline.md)
   - [深挖导读](docs/guide-deep-dive-guide.md)
2. **再跑两章正文**（本模块的“教材正文”）
   - [配置来源（PropertySources）与 Profile 覆盖](docs/boot-basics-property-sources-and-profiles.md)
   - [`@ConfigurationProperties` 绑定与类型转换](docs/boot-basics-configuration-properties-binding.md)
3. **遇到问题时回到排障入口**
   - [断点地图](docs/guide-breakpoint-map.md)（优先：快速命中关键分支）
   - [关键分支矩阵](docs/guide-branch-decision-matrix.md)（把现象收敛成 If/Then）
   - [常见坑](docs/appendix-common-pitfalls.md) / [自检](docs/appendix-self-check.md)

---

### 按问题查入口（从症状回到最短路径）
| 现象（读者视角） | 先看哪里 | 下一跳 |
| --- | --- | --- |
| “配置已修改但没有生效” | [配置来源与 Profile 覆盖](docs/boot-basics-property-sources-and-profiles.md) | 断点地图：`Environment#getProperty(...)` 取值点 |
| “profile 切换了但实现类没变” | [配置来源与 Profile 覆盖](docs/boot-basics-property-sources-and-profiles.md) | 分支矩阵：profile/条件装配分支 |
| “绑定对象里字段是 null/转换失败” | [`@ConfigurationProperties` 绑定与类型转换](docs/boot-basics-configuration-properties-binding.md) | 常见坑：绑定失败的典型边界 |

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-boot-basics -Dtest=*ExerciseSolutionTest test`
- 并发/性能（Environment 并发读取一致性）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsEnvironmentConcurrencyLabTest test`

---

### 下一步（把配置接到容器主线）
配置最终会体现在 Bean 装配与代理边界上；下一跳通常是 `spring-core-beans`（IoC 容器）。
