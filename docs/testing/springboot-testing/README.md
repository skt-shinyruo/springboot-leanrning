# Spring Boot Testing：目录

> 建议先把“测试切片选择”这件事做对，再把 mocking 与边界控制好，让机制理解可以被重复验证。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/183-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/184-00-deep-dive-guide.md)

## 顺读主线

- [slice 与 mocking](part-01-testing/185-01-slice-and-mocking.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[184-02-breakpoint-map.md](part-00-guide/184-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[184-04-branch-decision-matrix.md](part-00-guide/184-04-branch-decision-matrix.md)
- 排障 playbook：[186-90-common-pitfalls.md](appendix/186-90-common-pitfalls.md)
- 自检清单：[187-99-self-check.md](appendix/187-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :springboot-testing -Dtest=BootTestingBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :springboot-testing -Dtest=BootTestingBranchMatrixLabTest test`
- 练习与答案（Exercises/Solutions 约定）：[exercises-and-solutions.md](../../book/exercises-and-solutions.md)
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :springboot-testing -Dtest=*ExerciseSolutionTest test`
- 并发/性能专题（可复现实验范式）：[performance-and-concurrency.md](../../book/performance-and-concurrency.md)
- 可跑入口（并发/性能 Lab - TestContextCache 复用边界证据链）：`mvn -q -pl :springboot-testing -Dtest=BootTestingTestContextCacheLabTest test`

## 排坑与自检

- [常见坑](appendix/186-90-common-pitfalls.md)
- [自检](appendix/187-99-self-check.md)
