# Spring Events：目录

> 建议先把事件模型与同步执行跑通，再进入异步与事务事件；事件问题的根因几乎都在“时序与边界”上。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/127-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/128-00-deep-dive-guide.md)

## 顺读主线

- [事件心智模型](part-01-event-basics/129-01-event-mental-model.md)
- [多监听器与顺序](part-01-event-basics/130-02-multiple-listeners-and-order.md)
- [条件与 payload](part-01-event-basics/131-03-condition-and-payload.md)
- [同步与异常](part-01-event-basics/132-04-sync-and-exceptions.md)
- [异步监听](part-02-async-and-transactional/133-05-async-listener.md)
- [异步 multicaster](part-02-async-and-transactional/134-06-async-multicaster.md)
- [事务事件](part-02-async-and-transactional/135-07-transactional-event-listener.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[128-02-breakpoint-map.md](part-00-guide/128-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[128-04-branch-decision-matrix.md](part-00-guide/128-04-branch-decision-matrix.md)
- 排障 playbook：[136-90-common-pitfalls.md](appendix/136-90-common-pitfalls.md)
- 自检清单：[137-99-self-check.md](appendix/137-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
- 可跑入口（Branch Matrix - 基础事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
- 可跑入口（Branch Matrix - 异步/事务事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`

## 排坑与自检

- [常见坑](appendix/136-90-common-pitfalls.md)
- [自检](appendix/137-99-self-check.md)
