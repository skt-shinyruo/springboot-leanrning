# Spring Boot Async & Scheduling：线程边界与上下文传播

本模块讨论异步与调度在工程中的真实边界：线程从哪里切换、代理何时生效、异常如何传播、定时任务在失败时如何表现，以及安全/请求上下文在异步线程中为何默认丢失、如何传播与如何避免泄漏。

异步类问题常以“现象模糊”出现，例如：

- `@Async` 标注存在，但方法仍同步执行；
- 线程池配置已改动，但线程名/并发边界没有变化；
- 异步线程抛异常，调用方看起来“什么也没发生”；
- 定时任务发生异常后像是“消失”，难以复现与定位。

本模块的组织方式是把这些现象拆成可运行实验，用断言与断点把分支固定下来。

---

## 10 分钟入口：先跑通一次“代理 + 线程切换”

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`

运行后应能回答：异步代理在何处创建、线程池如何选择、异常传播到哪一层会被吞掉或包装。

---

## 阅读路线（主线 → 排障）

主线阅读（按章节推进）：

1. [主线时间线：为什么章节这样排列](part-00-guide/01-mainline-timeline.md)
2. [深挖导读：本模块的深挖边界](part-00-guide/02-deep-dive-guide.md)
3. 进入正文顺读（见下节“主线章节”）

排障阅读（从症状回到最短证据链）：

- [断点地图：优先命中的锚点](part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵：把现象收敛成 If/Then](part-00-guide/05-branch-decision-matrix.md)
- [常见坑：按症状组织的排障短文](appendix/01-common-pitfalls.md)
- [自检：用问题把主线复盘一遍](appendix/02-self-check.md)

---

## 主线章节（建议顺读）

- [01：`@Async` 心智模型：代理与线程切换](part-01-async-scheduling/01-async-proxy-mental-model.md)
- [02：Executor 与线程命名/并发边界](part-01-async-scheduling/02-executor-and-threading.md)
- [03：异常传播：Future vs void](part-01-async-scheduling/03-exceptions.md)
- [04：self-invocation：为什么异步有时不生效](part-01-async-scheduling/04-self-invocation.md)
- [05：`@Scheduled` 基础与可测试性](part-01-async-scheduling/05-scheduling-basics.md)
- [06：`@Async` × `@Transactional`：事务边界与执行线程](part-01-async-scheduling/06-async-and-transactions.md)
- [07：SecurityContext / RequestContext：默认丢失、传播与泄漏](part-01-async-scheduling/07-security-and-request-context.md)
- [08：Spring Boot `spring.task.*`：默认线程池/调度器与属性映射](part-01-async-scheduling/08-boot-spring-task-autoconfig.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix（主线最小集合）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- Branch Matrix（关键分支最小集合）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
- 线程池饱和/拒绝策略（确定性复现）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExecutorSaturationLabTest test`
