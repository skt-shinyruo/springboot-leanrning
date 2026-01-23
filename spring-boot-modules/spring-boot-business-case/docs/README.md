# Business Case（综合案例）：目录

> 这是“把模块串成系统”的入口：建议边跑边读，用端到端流程把理解固定下来。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/189-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/190-00-deep-dive-guide.md)

## 顺读主线

- [架构与端到端流转](part-01-business-case/191-01-architecture-and-flow.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[190-02-breakpoint-map.md](part-00-guide/190-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[190-04-branch-decision-matrix.md](part-00-guide/190-04-branch-decision-matrix.md)
- 排障 playbook：[192-90-common-pitfalls.md](appendix/192-90-common-pitfalls.md)
- 自检清单：[193-99-self-check.md](appendix/193-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
- 练习与答案（Exercises/Solutions 约定）：[exercises-and-solutions.md](../../book/exercises-and-solutions.md)
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-boot-business-case -Dtest=*ExerciseSolutionTest test`
- 并发/性能专题（可复现实验范式）：[performance-and-concurrency.md](../../book/performance-and-concurrency.md)
- 可跑入口（并发/性能 Lab - 并发下的业务边界证据链）：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseConcurrentOrderPlacementLabTest test`

## 排坑与自检

- [常见坑](appendix/192-90-common-pitfalls.md)
- [自检](appendix/193-99-self-check.md)
