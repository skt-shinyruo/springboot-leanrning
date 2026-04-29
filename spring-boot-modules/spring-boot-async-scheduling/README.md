# spring-boot-async-scheduling

这个模块不是“写个异步 demo 看看日志”的那种项目，它更像一组可回归的笔记：用 tests 把异步与调度里最常踩的边界钉住（代理、线程池选择、异常语义、自调用、调度注册与触发）。

它默认不启动 Web 服务（`spring.main.web-application-type=none`），入口主要在 tests 和 `DemoRunner`。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix-self-check.md`](docs/appendix-self-check.md)

## 关键命令

```bash
mvn -pl :spring-boot-async-scheduling test
```

运行（观察 `DemoRunner` 输出，不会常驻）：

```bash
mvn -pl :spring-boot-async-scheduling spring-boot:run
```

## 两个入口（先从这里走，足够覆盖 80%）

- 主线最小集合（Book Matrix）：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java`
  - 作用：把 `@Async` / executor / `@Scheduled` 的主线跑通（少而关键）
- 关键分支最小集合（Branch Matrix）：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBranchMatrixLabTest.java`
  - 作用：把“为什么没生效/线程不对/异常去哪了”写成 If/Then，适合排障

## docs 主线（顺着读一遍就够）

目录：见本 README 的「目录（唯一顺序来源）」

1. [`@Async` 心智模型：代理与线程切换](docs/async-scheduling-async-proxy-mental-model.md)
2. [Executor 与线程命名/并发边界](docs/async-scheduling-executor-and-threading.md)
3. [异常传播：Future vs void](docs/async-scheduling-exceptions.md)
4. [self-invocation：为什么异步有时不生效](docs/async-scheduling-self-invocation.md)
5. [`@Scheduled` 基础与可测试性](docs/async-scheduling-scheduling-basics.md)
6. [`@Async` × `@Transactional`：事务边界与执行线程](docs/async-scheduling-async-and-transactions.md)
7. [SecurityContext / RequestContext：默认丢失、传播与泄漏](docs/async-scheduling-security-and-request-context.md)
8. [Spring Boot `spring.task.*`：默认线程池/调度器与属性映射](docs/async-scheduling-boot-spring-task-autoconfig.md)
9. [常见坑清单](docs/appendix-common-pitfalls.md)

## 实验/练习（按用途）

如果希望按用途挑选测试入口，可以从下面这些类开始（它们基本覆盖了 docs 主线）。

- `@Async` 主线：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java`
- executor 选择：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingExecutorSelectionLabTest.java`
- proxy 类型与 `final` 边界：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingProxyTypeLabTest.java`
- void 异常的可观测性：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingUncaughtExceptionHandlerLabTest.java`
- ThreadLocal/MDC 上下文传播与泄漏：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingContextPropagationLabTest.java`
- `@Async × @Transactional`：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingTransactionBoundaryLabTest.java`
- SecurityContext / RequestContext：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSecurityContextPropagationLabTest.java` / `BootAsyncSchedulingRequestContextPropagationLabTest.java`
- Boot `spring.task.*` 自动装配：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSpringTaskAutoConfigurationLabTest.java`
- `@Scheduled`：开关/触发/注册/异常语义：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSchedulingLabTest.java` / `BootAsyncSchedulingSchedulingRegistrationLabTest.java` / `BootAsyncSchedulingSchedulingExceptionSemanticsLabTest.java`
- `@Scheduled + @Async` 组合：`src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingScheduledAsyncCombinationLabTest.java`

性能与并发边界（确定性复现）：

- 线程池饱和与拒绝策略：`src/test/java/com/learning/springboot/bootasyncscheduling/part02_perf_concurrency/BootAsyncSchedulingExecutorSaturationLabTest.java`

练习（默认 `@Disabled`，更像练习题）：

- `src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Async & Scheduling：线程边界与上下文传播

本模块讨论异步与调度在工程中的真实边界：线程从哪里切换、代理何时生效、异常如何传播、定时任务在失败时如何表现，以及安全/请求上下文在异步线程中为何默认丢失、如何传播与如何避免泄漏。

异步类问题常以“现象模糊”出现，例如：

- `@Async` 标注存在，但方法仍同步执行；
- 线程池配置已改动，但线程名/并发边界没有变化；
- 异步线程抛异常，调用方表面上“什么也没发生”；
- 定时任务发生异常后像是“消失”，难以复现与定位。

本模块的组织方式是把这些现象拆成可运行实验，用断言与断点把分支固定下来。

---

### 10 分钟入口：先跑通一次“代理 + 线程切换”
- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`

运行后应能回答：异步代理在何处创建、线程池如何选择、异常传播到哪一层会被吞掉或包装。

---

### 阅读路线（主线 → 排障）
主线阅读（按章节推进）：

1. [主线时间线：为什么章节这样排列](docs/guide-mainline-timeline.md)
2. [深挖导读：本模块的深挖边界](docs/guide-deep-dive-guide.md)
3. 进入正文顺读（见下节“主线章节”）

排障阅读（从症状回到最短证据链）：

- [断点地图：优先命中的锚点](docs/guide-breakpoint-map.md)
- [关键分支矩阵：把现象收敛成 If/Then](docs/guide-branch-decision-matrix.md)
- [常见坑：按症状组织的排障短文](docs/appendix-common-pitfalls.md)
- [自检：用问题把主线复盘一遍](docs/appendix-self-check.md)

---

### 主线章节（顺读路径）
- [01：`@Async` 心智模型：代理与线程切换](docs/async-scheduling-async-proxy-mental-model.md)
- [02：Executor 与线程命名/并发边界](docs/async-scheduling-executor-and-threading.md)
- [03：异常传播：Future vs void](docs/async-scheduling-exceptions.md)
- [04：self-invocation：为什么异步有时不生效](docs/async-scheduling-self-invocation.md)
- [05：`@Scheduled` 基础与可测试性](docs/async-scheduling-scheduling-basics.md)
- [06：`@Async` × `@Transactional`：事务边界与执行线程](docs/async-scheduling-async-and-transactions.md)
- [07：SecurityContext / RequestContext：默认丢失、传播与泄漏](docs/async-scheduling-security-and-request-context.md)
- [08：Spring Boot `spring.task.*`：默认线程池/调度器与属性映射](docs/async-scheduling-boot-spring-task-autoconfig.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix（主线最小集合）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- Branch Matrix（关键分支最小集合）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
- 线程池饱和/拒绝策略（确定性复现）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExecutorSaturationLabTest test`
