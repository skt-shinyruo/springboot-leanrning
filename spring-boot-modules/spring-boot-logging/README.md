# spring-boot-logging

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”讲透 **Spring Boot 日志（Logging）**在工程里的落点：

- 日志级别如何由配置驱动（`logging.level.*`）
- 为什么“日志要可断言”（避免只靠肉眼看控制台）
- MDC/traceId 等上下文如何影响排障效率（本模块以最小示范为主）

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/](docs/README.md)。

## Start Here（5 分钟闭环）

```bash
mvn -pl :spring-boot-logging -Dtest=BootLoggingLabTest test
```

你应该能解释清楚：

- 为什么 debug 日志“有时出现、有时不出现”（配置与 logger category）
- 如何用测试固化“某条日志应该出现/不应该出现”（把日志当成可验证信号）

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-logging spring-boot:run
```

### 测试

```bash
mvn -pl :spring-boot-logging test
```

## Labs / Exercises 索引

> Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 |
| --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingLabTest.java` | logging.level 生效与可断言闭环 | ⭐⭐ |
| Lab（Perf/Concurrency） | `src/test/java/com/learning/springboot/bootlogging/part02_perf_concurrency/BootLoggingConcurrencyLabTest.java` | MDC（ThreadLocal）隔离与“并发不串线”验证 | ⭐⭐ |
| Exercise | `src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingExerciseTest.java` | MDC 相关练习（把上下文加入日志并固化断言） | ⭐⭐–⭐⭐⭐ |
