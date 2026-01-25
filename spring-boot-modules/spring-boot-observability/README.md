# spring-boot-observability

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”讲透 **Spring Boot 可观测性（Observability）**在工程里的落点：

- HTTP 请求 → 观测信号（metrics/observations）的产生
- 观测对象在哪里生成、如何被聚合（`MeterRegistry`/`ObservationRegistry`）
- 为什么“可观测性”经常需要和 Web MVC / Logging / Security 联动理解

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/](docs/README.md)。

## Start Here（5 分钟闭环）

```bash
mvn -pl :spring-boot-observability -Dtest=BootObservabilityLabTest test
```

你应该能解释清楚：

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
