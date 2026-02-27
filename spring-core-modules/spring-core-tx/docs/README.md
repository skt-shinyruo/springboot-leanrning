# Spring Core Tx（事务）：边界、传播与回滚

本模块把事务相关问题放回同一条可运行主线：事务边界如何建立、`@Transactional` 代理在何处介入、回滚规则如何判定、传播行为如何影响嵌套调用，以及 `TransactionTemplate` 如何作为显式边界工具用于调试与工程化收敛。

事务类问题在排障时最常见的误判是“以为走了事务，实际上没走代理”；因此本模块优先把“代理主线 + 边界事实”跑通，再进入回滚与传播等分支。

---

## 10 分钟入口：先确认事务边界与代理是否生效

- `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`

运行后应能回答：事务在何处开始/提交/回滚；拦截器链条在哪个入口触发；自调用等场景为何会绕过代理边界。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [事务拦截器调用链（从 @Transactional 到 commit/rollback）](part-00-guide/03-transaction-interceptor-call-chain.md)

## 顺读主线

- [事务边界](part-01-transaction-basics/01-transaction-boundary.md)
- [@Transactional 代理](part-01-transaction-basics/02-transactional-proxy.md)
- [回滚规则](part-01-transaction-basics/03-rollback-rules.md)
- [传播行为](part-01-transaction-basics/04-propagation.md)
- [TransactionTemplate](part-02-template-and-debugging/01-transaction-template.md)
- [事务调试](part-02-template-and-debugging/02-debugging.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 事务拦截器调用链（源码主线锚点）：[03-transaction-interceptor-call-chain.md](part-00-guide/03-transaction-interceptor-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- Branch Matrix（事务主分支）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
- Branch Matrix（常见坑聚合）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-core-tx -Dtest=*ExerciseSolutionTest test`
- 并发/性能（ThreadLocal 边界证据链）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxThreadLocalBoundaryLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
