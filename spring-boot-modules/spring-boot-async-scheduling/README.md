# spring-boot-async-scheduling

这个模块不是“写个异步 demo 看看日志”的那种项目，它更像一组可回归的笔记：用 tests 把异步与调度里最常踩的边界钉住（代理、线程池选择、异常语义、自调用、调度注册与触发）。

它默认不启动 Web 服务（`spring.main.web-application-type=none`），入口主要在 tests 和 `DemoRunner`。

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

docs 目录页：[`docs/README.md`](docs/README.md)

1. [`@Async` 心智模型：代理与线程切换](docs/part-01-async-scheduling/119-01-async-proxy-mental-model.md)
2. [Executor 与线程命名/并发边界](docs/part-01-async-scheduling/120-02-executor-and-threading.md)
3. [异常传播：Future vs void](docs/part-01-async-scheduling/121-03-exceptions.md)
4. [self-invocation：为什么异步有时不生效](docs/part-01-async-scheduling/122-04-self-invocation.md)
5. [`@Scheduled` 基础与可测试性](docs/part-01-async-scheduling/123-05-scheduling-basics.md)
6. [`@Async` × `@Transactional`：事务边界与执行线程](docs/part-01-async-scheduling/126-06-async-and-transactions.md)
7. [SecurityContext / RequestContext：默认丢失、传播与泄漏](docs/part-01-async-scheduling/127-07-security-and-request-context.md)
8. [Spring Boot `spring.task.*`：默认线程池/调度器与属性映射](docs/part-01-async-scheduling/128-08-boot-spring-task-autoconfig.md)
9. [常见坑清单](docs/appendix/124-90-common-pitfalls.md)

## Labs / Exercises（按用途）

如果你想按用途挑测试入口，可以从下面这些类开始（它们基本覆盖了 docs 主线）。

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

Exercises（默认 `@Disabled`，更像练习题）：

- `src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`
