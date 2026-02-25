# Spring Events：目录

## 导读

本页是「Spring Events：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议先把事件模型与同步执行跑通，再进入异步与事务事件；事件问题的根因几乎都在“时序与边界”上。

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
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
- 可跑入口（Branch Matrix - 基础事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
- 可跑入口（Branch Matrix - 异步/事务事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`
- 可跑入口（Perf/Concurrency Lab）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsConcurrencyLabTest test`
- 可跑入口（Exercises 对应 Solution - 异步 multicaster 边界）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsExerciseSolutionTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
