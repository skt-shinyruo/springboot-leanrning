# spring-boot-testing

本模块用于学习 Spring Boot 的测试入门：

- `@WebMvcTest`：只加载 Web 层（Controller），更快、更聚焦
- `@SpringBootTest`：加载完整应用上下文，更接近集成测试

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

## 本模块的学习产出

- 理解“测试切片（slice test）”的意义
- 会写一个 `@WebMvcTest` + `@MockBean` 的 Controller 测试
- 会写一个 `@SpringBootTest(webEnvironment=RANDOM_PORT)` 的端到端测试
- 理解 `@MockBean` 在 full context 里“覆盖真实 Bean”的效果与边界

## 前置知识

- 建议先完成 `spring-boot-web-mvc`（更容易理解 Controller 测试）
- 了解 JUnit 的基本概念（断言、测试方法）

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-testing spring-boot:run
```

默认端口：`8083`

### 快速验证

```bash
curl 'http://localhost:8083/api/greeting?name=Bob'
```

### 测试

```bash
mvn -pl :spring-boot-testing test
```

## 推荐 docs 阅读顺序

（目录：见本 README 的「目录（唯一顺序来源）」）

1. [导读](docs/guide-deep-dive-guide.md)
2. [Slice vs Full Context](docs/testing-slice-and-mocking.md)
3. [常见坑清单](docs/appendix-common-pitfalls.md)
4. [自测题](docs/appendix-self-check.md)

对应的可运行实验（先跑后读）：
- `src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`
- `src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java`
- `src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java` | `@WebMvcTest` + `@MockBean`（更快、更聚焦） | ⭐ | 对照 controller 入口与 mock 的依赖 |
| Lab | `src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java` | `@SpringBootTest(RANDOM_PORT)` 端到端验证 | ⭐ | 体会“真实链路”与启动开销 |
| Lab | `src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java` | full context 里 `@MockBean` 覆盖真实 Bean | ⭐⭐ | 分清“替换 Bean”与“只 mock 一层” |
| Exercise | `src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java` | 按提示补充更多 slice/JSON/MockBean 练习 | ⭐–⭐⭐ | 从“增加一个 slice 测试”开始 |

## 常见 Debug 路径

- `@WebMvcTest` 报 Bean 缺失：先确认缺的是不是应当 mock 的依赖（`@MockBean`）
- `@SpringBootTest` 很慢：优先用 slice test 学机制，再用全量验证集成链路
- `@MockBean` 不生效：确认 mock 的类型是否与真实注入点一致（接口/实现类差异）

## 扩展练习（可选）

- 为 `GreetingService` 增加一个“敏感词过滤”逻辑，并补充相应测试
- 增加一个 `@JsonTest`（或类似切片）来学习 JSON 序列化测试

## 参考

- Spring Boot Testing

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Testing：切片、上下文与证据链

本模块讨论 Spring Boot 的测试边界：何时使用切片测试（slice），何时需要完整上下文；`@MockBean` 覆盖真实 Bean 时会影响哪些链路；以及如何让“机制理解”能在测试中重复验证，而不是依赖一次性的调试结论。

---

### 10 分钟入口：先把测试边界跑通
- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`

运行后应能回答：当前测试启动的上下文范围是什么（切片还是全量）；Mock 的覆盖点在哪里；为何相同代码在不同测试注解下表现不同。

### 从这里开始（建议顺序）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线
- [slice 与 mocking](docs/testing-slice-and-mocking.md)

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-testing -Dtest=*ExerciseSolutionTest test`
- 并发/性能（TestContextCache 复用边界证据链）：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingTestContextCacheLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
