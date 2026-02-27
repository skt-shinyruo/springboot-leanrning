# Business Case（综合案例）：把模块串成系统

本模块提供一个端到端的综合案例，用来把 Web、Security、Tx、JPA、Events 等分散主题串成一条可运行链路。它的价值不在于“再学一套 API”，而在于把边界放回真实调用链：一个请求从进入控制器到落库、发事件、回响应，中间哪些位置是代理边界、事务边界与一致性边界。

---

## 10 分钟入口：先跑通一次端到端链路

- `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`

运行后应能回答：端到端流程中关键的边界落在什么位置；异常与回滚如何影响最终结果；并发时哪些状态会出现竞争与可见性差异。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [架构与端到端流转](part-01-business-case/01-architecture-and-flow.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-business-case -Dtest=*ExerciseSolutionTest test`
- 并发/性能（并发下的业务边界证据链）：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseConcurrentOrderPlacementLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
