# Spring Boot Async & Scheduling：读者导言

## 导读

本页是「Spring Boot Async & Scheduling：读者导言」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


如果你在项目里用过 `@Async` / `@Scheduled`，大概率也遇到过这些“说不清”的问题：

- 明明写了 `@Async`，怎么还是同步执行？
- 线程池到底是哪一个？我改了配置，为什么线程名没变？
- 异步里抛异常，调用方为什么像什么都没发生？
- 定时任务抛了异常，会不会就此停掉？为什么有时像“消失”了一样？

这组文档和配套代码想做的事很朴素：把上面这些问题拆到足够小、足够可验证，然后用测试把结论钉住。你可以把它当成一份偏工程视角的“异步与调度随手册”——不追求百科全书式罗列，而是把最常踩的边界讲透。

## 两条阅读路线

第一条路线适合“我想顺着读一篇文章，把主线打通”：

1. [主线时间线：为什么章节这么排](part-00-guide/01-mainline-timeline.md)
2. [深挖导读：这模块到底在深挖什么](part-00-guide/02-deep-dive-guide.md)
3. 主线顺读（按章节向下走）

第二条路线适合“我现在就是在排障/复现某个分支”：

- [断点地图：从哪里下断点更省时间](part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵：把常见分支写成 If/Then](part-00-guide/05-branch-decision-matrix.md)
- [常见坑：按症状写的排障短文](appendix/01-common-pitfalls.md)
- [自检：像习题册一样把主线复盘一遍](appendix/02-self-check.md)

## 主线章节（建议顺读）

- [01：`@Async` 心智模型：代理与线程切换](part-01-async-scheduling/01-async-proxy-mental-model.md)
- [02：Executor 与线程命名/并发边界](part-01-async-scheduling/02-executor-and-threading.md)
- [03：异常传播：Future vs void](part-01-async-scheduling/03-exceptions.md)
- [04：self-invocation：为什么异步有时不生效](part-01-async-scheduling/04-self-invocation.md)
- [05：`@Scheduled` 基础与可测试性](part-01-async-scheduling/05-scheduling-basics.md)
- [06：`@Async` × `@Transactional`：事务边界与执行线程](part-01-async-scheduling/06-async-and-transactions.md)
- [07：SecurityContext / RequestContext：默认丢失、传播与泄漏](part-01-async-scheduling/07-security-and-request-context.md)
- [08：Spring Boot `spring.task.*`：默认线程池/调度器与属性映射](part-01-async-scheduling/08-boot-spring-task-autoconfig.md)

## 可验证入口（如果你想把“理解”变成事实）

这模块的大多数结论，都能在对应的 `*LabTest#method` 里找到最小复现入口。常用的三个入口是：

- Book Matrix（主线最小集合）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- Branch Matrix（关键分支最小集合）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
- 线程池饱和/拒绝策略（确定性复现）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExecutorSaturationLabTest test`
