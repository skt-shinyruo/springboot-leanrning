# Spring Boot Basics：从配置事实到行为边界

本模块讨论的不是“配置怎么写”，而是“配置为什么没按预期生效”。在 Spring Boot 中，配置的最终事实不在某个文件里，而在运行时的 `Environment` 中；而 `Environment` 的最终值会进一步影响 Bean 的注册、绑定与业务行为。

读完本模块后，读者应能把常见配置问题分成两类，并为每一类找到最短证据链：

- **值不对**：同名 key 在多个来源出现时，最终值来自哪里？
- **装配不对**：profile/条件装配导致某个实现没注册、或绑定对象形态与预期不一致？

---

## 10 分钟入口：先把“最终值”跑成事实

运行本模块的 Book Matrix：

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`

观察点（先钉事实，再解释原因）：

- `Environment#getActiveProfiles()`
- `Environment#getProperty("app.greeting")`（示例 key）

---

## 阅读路线（主线 → 排障 → 自证）

如果目标是顺着机制把主线跑通，可以按下面的顺序推进：

1. **先建立主线坐标**：主线时间线与深入导读
   - [主线时间线](part-00-guide/01-mainline-timeline.md)
   - [深挖导读](part-00-guide/02-deep-dive-guide.md)
2. **再跑两章正文**（本模块的“教材正文”）
   - [配置来源（PropertySources）与 Profile 覆盖](part-01-boot-basics/01-property-sources-and-profiles.md)
   - [`@ConfigurationProperties` 绑定与类型转换](part-01-boot-basics/02-configuration-properties-binding.md)
3. **遇到问题时回到排障入口**
   - [断点地图](part-00-guide/04-breakpoint-map.md)（优先：快速命中关键分支）
   - [关键分支矩阵](part-00-guide/05-branch-decision-matrix.md)（把现象收敛成 If/Then）
   - [常见坑](appendix/01-common-pitfalls.md) / [自检](appendix/02-self-check.md)

---

## 按问题查入口（从症状回到最短路径）

| 现象（读者视角） | 先看哪里 | 下一跳 |
| --- | --- | --- |
| “我改了配置但没生效” | [配置来源与 Profile 覆盖](part-01-boot-basics/01-property-sources-and-profiles.md) | 断点地图：`Environment#getProperty(...)` 取值点 |
| “profile 切换了但实现类没变” | [配置来源与 Profile 覆盖](part-01-boot-basics/01-property-sources-and-profiles.md) | 分支矩阵：profile/条件装配分支 |
| “绑定对象里字段是 null/转换失败” | [`@ConfigurationProperties` 绑定与类型转换](part-01-boot-basics/02-configuration-properties-binding.md) | 常见坑：绑定失败的典型边界 |

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-basics -Dtest=*ExerciseSolutionTest test`
- 并发/性能（Environment 并发读取一致性）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsEnvironmentConcurrencyLabTest test`

---

## 下一步（把配置接到容器主线）

配置最终会体现在 Bean 装配与代理边界上；下一跳通常是 `spring-core-beans`（IoC 容器）。
