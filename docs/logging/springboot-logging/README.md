# Spring Boot Logging：目录

> 建议先把“日志级别如何被解析与应用”跑通，再进入 MDC/结构化日志等扩展。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/199-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/200-00-deep-dive-guide.md)
3. [Logging 调用链（LoggingSystem 初始化与级别决策）](part-00-guide/200-01-logging-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/200-02-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/200-04-branch-decision-matrix.md)

## 顺读主线

- [日志级别与分类：为什么 debug 有时出现、有时不出现](part-01-logging-basics/201-01-logging-levels-and-categories.md)

## 进阶入口（可跑入口/关键分支）

- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBranchMatrixLabTest test`
- 可跑入口（Perf/Concurrency Lab）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/202-90-common-pitfalls.md)
- [自检](appendix/203-99-self-check.md)
