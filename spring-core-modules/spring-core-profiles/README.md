# spring-core-profiles

本模块用于系统学习 **Profiles** 与 **条件装配（Conditional Bean Registration）**。

包含内容：

- 使用 `@Profile` 进行 profile 条件装配（包含 `!dev` 这类表达式）
- 使用 `@ConditionalOnProperty` 进行基于配置项的装配（Spring Boot 自动配置中很常见）
- 保证每个场景都不会注入歧义（每个场景只存在一个 `GreetingProvider`）

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

## 本模块的学习产出

- `@Profile("dev")` 的 Bean 在什么时候会生效
- 使用配置项开关（例如 `app.mode=fancy`）切换行为
- 在测试中验证当前到底注入了哪个 Bean（以及为什么建议用 `ApplicationContextRunner`）

## 前置知识

- 建议先完成 `springboot-basics`（profile/配置加载的直觉）
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

## 推荐 docs 阅读顺序

> 本模块已补齐 `docs/`，建议按“先理解激活与选择 → 再用 runner 证明”的顺序：

1. [深挖指南](docs/part-00-guide/02-deep-dive-guide.md)
2. [Profile 激活与 Bean 选择](docs/part-01-profiles/01-profile-activation-and-bean-selection.md)
3. [常见坑清单](docs/appendix/01-common-pitfalls.md) + [自测题](docs/appendix/02-self-check.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesLabTest.java` | `@Profile`/`@ConditionalOnProperty` + `ApplicationContextRunner` | ⭐⭐ | 把每个场景的“最终注入 Bean”说清楚 |
| Exercise | `src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseTest.java` | 按提示新增 profile/开关/兜底 Bean 并写断言 | ⭐–⭐⭐ | 从“增加 prod provider”开始 |

## 常见 Debug 路径

- 条件不生效：先看 profile/属性是否真的传进来了（`Environment`）
- 注入歧义：同一场景下出现多个候选 Bean，优先让条件互斥而不是用 `@Primary` 兜底
- 测试建议：用 `ApplicationContextRunner` 把“场景”做小、做快（比起全量 `@SpringBootTest` 更适合学机制）

## 扩展练习（可选）

- 增加一个 `prod` profile 的 provider，并决定它是否应该覆盖 property toggle 的选择
- 增加第二个开关（例如 `app.language=en`），并按条件注册 provider
- （进阶）增加一个 `@ConditionalOnMissingBean` 的 fallback bean，并解释这种兜底模式

## 参考

- Spring Framework Reference：Bean Profiles
- Spring Boot Reference：Conditional auto-configuration annotations
