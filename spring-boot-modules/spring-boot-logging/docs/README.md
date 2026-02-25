# Spring Boot Logging：目录

## 导读

本页是「Spring Boot Logging：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议先把“日志级别如何被解析与应用”跑通，再进入 MDC/结构化日志等扩展。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [Logging 调用链（LoggingSystem 初始化与级别决策）](part-00-guide/03-logging-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/04-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/05-branch-decision-matrix.md)

## 顺读主线

- [日志级别与分类：为什么 debug 有时出现、有时不出现](part-01-logging-basics/01-logging-levels-and-categories.md)

## 进阶入口（可跑入口/关键分支）

- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBranchMatrixLabTest test`
- 可跑入口（Perf/Concurrency Lab）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
