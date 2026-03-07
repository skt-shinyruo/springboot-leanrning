# spring-boot-logging

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”讲透 **Spring Boot 日志（Logging）**在工程里的落点：

- 日志级别如何由配置驱动（`logging.level.*`）
- 为什么“日志要可断言”（避免只靠肉眼看控制台）
- MDC/traceId 等上下文如何影响排障效率（本模块以最小示范为主）

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 docs/。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

完成标准（应能解释清楚）：

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

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Logging：级别决策、分类与初始化链路

日志问题最常见的表象是“为什么 debug 有时出现、有时不出现”，而根因通常落在两处：日志系统初始化顺序，以及日志级别与分类（category）在启动期如何被解析并应用。本模块先把这条调用链跑通，再进入 MDC、结构化日志等扩展主题。

---

### 10 分钟入口：先确认“最终级别”如何决策
- `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`

运行后应能回答：LoggingSystem 在启动期何时初始化；某个 logger 的最终级别来自哪个配置来源；为何同一份配置在不同环境下看起来“时灵时不灵”。

### 从这里开始（建议顺序）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)
3. [Logging 调用链（LoggingSystem 初始化与级别决策）](docs/guide-logging-call-chain.md)
4. [断点地图（排障优先）](docs/guide-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](docs/guide-branch-decision-matrix.md)

### 顺读主线
- [日志级别与分类：为什么 debug 有时出现、有时不出现](docs/logging-basics-logging-levels-and-categories.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBranchMatrixLabTest test`
- 并发/性能：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingConcurrencyLabTest test`

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
