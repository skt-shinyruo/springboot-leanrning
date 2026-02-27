# Spring Boot Testing：切片、上下文与证据链

本模块讨论 Spring Boot 的测试边界：何时使用切片测试（slice），何时需要完整上下文；`@MockBean` 覆盖真实 Bean 时会影响哪些链路；以及如何让“机制理解”能在测试中重复验证，而不是依赖一次性的调试结论。

---

## 10 分钟入口：先把测试边界跑通

- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`

运行后应能回答：当前测试启动的上下文范围是什么（切片还是全量）；Mock 的覆盖点在哪里；为何相同代码在不同测试注解下表现不同。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [slice 与 mocking](part-01-testing/01-slice-and-mocking.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-testing -Dtest=*ExerciseSolutionTest test`
- 并发/性能（TestContextCache 复用边界证据链）：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingTestContextCacheLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
