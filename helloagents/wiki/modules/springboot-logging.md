# springboot-logging

## Purpose

学习 Spring Boot 日志系统的初始化链路（LoggingSystem）、日志级别与 category 决策、以及“为什么我的 debug 不输出”的常见坑，并提供可断言的实验闭环。

## Module Overview

- **Responsibility:** 用测试固定 logging 行为（级别、category、输出捕获），并把关键断点入口收敛为 Debugger Pack。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Start Here（路线图 / 断点地图 / 第一个可运行入口）

- 路线图：`helloagents/wiki/learning-path.md`
- Docs Start Here：`spring-boot-modules/spring-boot-logging/docs/README.md`
- 调用链（LoggingSystem 初始化与级别决策）：`spring-boot-modules/spring-boot-logging/docs/part-00-guide/03-logging-call-chain.md`
- 断点地图：`spring-boot-modules/spring-boot-logging/docs/part-00-guide/04-breakpoint-map.md`
- 第一个可运行入口（3 分钟开跑）：
  - `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingLabTest.java`

## Specifications

### Requirement: 日志行为可被测试固化
**Module:** springboot-logging
覆盖：日志级别、category、不同 logger 的输出差异，避免用“看控制台”作为学习证据。

#### Scenario: Debug 输出可被断言
- 使用 `OutputCaptureExtension` 固定 DEBUG 级别输出是否出现

## Docs & 复现入口

- **Docs Index:** `spring-boot-modules/spring-boot-logging/docs/README.md`
- **Deep Dive Guide:** `spring-boot-modules/spring-boot-logging/docs/part-00-guide/02-deep-dive-guide.md`
- **Call Chain:** `spring-boot-modules/spring-boot-logging/docs/part-00-guide/03-logging-call-chain.md`
- **Breakpoint Map:** `spring-boot-modules/spring-boot-logging/docs/part-00-guide/04-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-boot-modules/spring-boot-logging/docs/part-00-guide/05-branch-decision-matrix.md`
- **Playbook:** `spring-boot-modules/spring-boot-logging/docs/appendix/01-common-pitfalls.md`
- **Self-check:** `spring-boot-modules/spring-boot-logging/docs/appendix/02-self-check.md`
- **Lab（并发/性能：MDC ThreadLocal 隔离可断言复现）：** `spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part02_perf_concurrency/BootLoggingConcurrencyLabTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingExerciseSolutionTest.java`

## Change History

- [202601221758_tutorials_style_deepen_all](../../history/2026-01/202601221758_tutorials_style_deepen_all/) - ✅ 已执行：新增 `springboot-logging` 模块（logging system/级别/输出捕获）+ docs 骨架 + Labs；并纳入 docs/SUMMARY 与 labs-index
- [202601222034_solutions_perf_concurrency_batch01](../../history/2026-01/202601222034_solutions_perf_concurrency_batch01/) - ✅ 已执行：新增 Exercises 对应 Solution（MDC + Logback ListAppender 固化断言），默认参与回归验证
