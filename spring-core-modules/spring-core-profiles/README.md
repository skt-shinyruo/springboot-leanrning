# spring-core-profiles

本模块用于系统学习 **Profiles** 与 **条件装配（Conditional Bean Registration）**。

包含内容：

- 使用 `@Profile` 进行 profile 条件装配（包含 `!dev` 这类表达式）
- 使用 `@ConditionalOnProperty` 进行基于配置项的装配（Spring Boot 自动配置中很常见）
- 保证每个场景都不会注入歧义（每个场景只存在一个 `GreetingProvider`）


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](docs/appendix-self-check.md)

## 本模块完成后应能解释的内容

- `@Profile("dev")` 的 Bean 在什么时候会生效
- 使用配置项开关（例如 `app.mode=fancy`）切换行为
- 在测试中验证当前到底注入了哪个 Bean（以及为什么用 `ApplicationContextRunner`）

## 前置知识

- 先完成 `springboot-basics`（profile/配置加载的预期）
- （可选）了解 Spring Boot 的条件注解常见用法（`@ConditionalOnProperty`）

## 关键命令

### 运行

默认（不指定 profile，也不指定额外配置）：

```bash
mvn -pl :spring-core-profiles spring-boot:run
```

启用 `dev` profile：

```bash
mvn -pl :spring-core-profiles spring-boot:run -Dspring-boot.run.profiles=dev
```

启用配置项开关：

```bash
mvn -pl :spring-core-profiles spring-boot:run -Dspring-boot.run.arguments=--app.mode=fancy
```

运行后观察控制台输出：

- activeProfiles
- `app.mode`
- 当前生效的 `GreetingProvider` 实现类

### 测试

```bash
mvn -pl :spring-core-profiles test
```

## docs 阅读顺序

> 本模块已补齐 `docs/`，按“先理解激活与选择 → 再用 runner 证明”的顺序：

1. [深挖指南](docs/guide-deep-dive-guide.md)
2. [Profile 激活与 Bean 选择](docs/profiles-profile-activation-and-bean-selection.md)
3. [常见坑清单](docs/appendix-common-pitfalls.md) + [自测题](docs/appendix-self-check.md)

## 实验/练习索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。练习默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesLabTest.java` | `@Profile`/`@ConditionalOnProperty` + `ApplicationContextRunner` | ⭐⭐ | 把每个场景的“最终注入 Bean”说清楚 |
| Exercise | `src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseTest.java` | 按提示新增 profile/开关/兜底 Bean 并写断言 | ⭐–⭐⭐ | 从“增加 prod provider”开始 |

## 常见 Debug 路径

- 条件不生效：先看 profile/属性是否真的传进来了（`Environment`）
- 注入歧义：同一场景下出现多个候选 Bean，优先让条件互斥而不是用 `@Primary` 兜底
- 测试动作：用 `ApplicationContextRunner` 把“场景”做小、做快（比起全量 `@SpringBootTest` 更适合学机制）

## 扩展练习（可选）

- 增加一个 `prod` profile 的 provider，并决定它是否应该覆盖 property toggle 的选择
- 增加第二个开关（例如 `app.language=en`），并按条件注册 provider
- （进阶）增加一个 `@ConditionalOnMissingBean` 的 fallback bean，并解释这种兜底模式

## 参考

- Spring Framework Reference：Bean Profiles
- Spring Boot Reference：Conditional auto-configuration annotations

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Profiles：激活条件与 Bean 选择

Profile 的核心语义不是“加载哪个配置文件”，而是决定哪些配置片段与哪些 Bean 会进入容器。环境不一致、条件不生效、Bean 缺失等问题，往往可以先回到 profile 的事实：到底激活了哪些 profile、这些 profile 如何影响条件注册与装配结果。

---

### 10 分钟入口：先把“激活事实”钉住
- `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`

运行后应能回答：active profiles 的最终值是什么；哪些 Bean 因 profile 条件进入或退出容器；同一配置在不同启动参数下为何会产生不同的 bean graph。

### 从这里开始（顺读路径）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线
- [Profile 激活与 Bean 选择](docs/profiles-profile-activation-and-bean-selection.md)

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-core-profiles -Dtest=*ExerciseSolutionTest test`
- 并发/性能（Environment 并发读取边界）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesEnvironmentConcurrencyLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
