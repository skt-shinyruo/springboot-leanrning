# spring-boot-observability

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”讲透 **Spring Boot 可观测性（Observability）**在工程里的落点：

- HTTP 请求 → 观测信号（metrics/observations）的产生
- 观测对象在哪里生成、如何被聚合（`MeterRegistry`/`ObservationRegistry`）
- 为什么“可观测性”经常需要和 Web MVC / Logging / Security 联动理解

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 docs/。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

完成标准（应能解释清楚）：

- 一次 HTTP 请求之后，为什么会出现 `http.server.requests` 这类指标
- 这些指标是在哪一层产生的（FilterChain/MVC/Actuator/Observation）
- 如何把“观测信号”变成可回归断言（而不是依赖日志/肉眼）

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-observability spring-boot:run
```

### 测试

```bash
mvn -pl :spring-boot-observability test
```

## Labs / Exercises 索引

> Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 |
| --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityLabTest.java` | HTTP → metrics/observations 的最小闭环 | ⭐⭐ |
| Lab（Perf/Concurrency） | `src/test/java/com/learning/springboot/bootobservability/part02_perf_concurrency/BootObservabilityConcurrencyLabTest.java` | Observation scope（ThreadLocal）隔离与“并发不串 scope”验证 | ⭐⭐ |
| Exercise | `src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityExerciseTest.java` | 增加自定义 tag 并固化断言 | ⭐⭐–⭐⭐⭐ |

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Observability：请求链路到观测信号

本模块以一次 HTTP 请求为主线，把“观测信号从哪里来”跑成事实：metrics/observations 在哪一层被创建、标签（tag）在何处决定、以及这些信号如何落到 meter 与 registry。tracing 与 log correlation 属于更复杂的组合，本模块先把基础链路固定下来，再讨论扩展边界。

---

### 10 分钟入口：先跑通 `http.server.requests` 的来源
- `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`

运行后应能回答：一次请求会触发哪些 observation；meter 的创建与命名发生在何处；为何某些 tag 在某些场景下会缺失。

### 从这里开始（建议顺序）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)
3. [Observability 调用链（请求 → observation → meter）](docs/guide-http-observation-call-chain.md)
4. [断点地图（排障优先）](docs/guide-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](docs/guide-branch-decision-matrix.md)

### 顺读主线
- [HTTP metrics：`http.server.requests` 从哪里来](docs/observability-basics-http-metrics-and-observations.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBranchMatrixLabTest test`
- 并发/性能：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityConcurrencyLabTest test`

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
