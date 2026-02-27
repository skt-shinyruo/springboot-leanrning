# Spring Boot Logging：级别决策、分类与初始化链路

日志问题最常见的表象是“为什么 debug 有时出现、有时不出现”，而根因通常落在两处：日志系统初始化顺序，以及日志级别与分类（category）在启动期如何被解析并应用。本模块先把这条调用链跑通，再进入 MDC、结构化日志等扩展主题。

---

## 10 分钟入口：先确认“最终级别”如何决策

- `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`

运行后应能回答：LoggingSystem 在启动期何时初始化；某个 logger 的最终级别来自哪个配置来源；为何同一份配置在不同环境下看起来“时灵时不灵”。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [Logging 调用链（LoggingSystem 初始化与级别决策）](part-00-guide/03-logging-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/04-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/05-branch-decision-matrix.md)

## 顺读主线

- [日志级别与分类：为什么 debug 有时出现、有时不出现](part-01-logging-basics/01-logging-levels-and-categories.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBranchMatrixLabTest test`
- 并发/性能：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
