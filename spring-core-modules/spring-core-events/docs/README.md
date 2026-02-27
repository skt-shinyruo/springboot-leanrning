# Spring Events：时序、边界与监听器语义

事件机制的难点几乎都落在“时序与边界”：事件何时发布、监听器何时执行、异常如何传播、异步边界如何改变因果关系、事务事件又如何与提交/回滚绑定。本模块先把同步事件的基本语义跑通，再进入异步监听与事务事件（`@TransactionalEventListener`）的分支。

---

## 10 分钟入口：先把同步事件跑成事实

- `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`

运行后应能回答：同一事件在多个监听器之间的顺序如何决定；异常会如何影响发布方；切换到异步后，这些语义在哪些地方发生变化。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [事件心智模型](part-01-event-basics/01-event-mental-model.md)
- [多监听器与顺序](part-01-event-basics/02-multiple-listeners-and-order.md)
- [条件与 payload](part-01-event-basics/03-condition-and-payload.md)
- [同步与异常](part-01-event-basics/04-sync-and-exceptions.md)
- [异步监听](part-02-async-and-transactional/01-async-listener.md)
- [异步 multicaster](part-02-async-and-transactional/02-async-multicaster.md)
- [事务事件](part-02-async-and-transactional/03-transactional-event-listener.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
- Branch Matrix（基础事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
- Branch Matrix（异步/事务事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`
- 并发/性能：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsConcurrencyLabTest test`
- Solutions（Exercises 对应回归：异步 multicaster 边界）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsExerciseSolutionTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
