# spring-boot-async-scheduling

本模块用于学习异步与调度的高频机制点：

- `@Async`：代理、executor/线程池、异常传播、self-invocation 陷阱
- `@Scheduled`：fixedDelay/fixedRate 的直觉、如何写出**稳定不 flaky** 的调度测试

本模块以 tests-first 为主，不启动 Web 服务（`spring.main.web-application-type=none`）。

## 关键命令

```bash
mvn -pl :spring-boot-async-scheduling test
```

## 推荐 docs 阅读顺序

（docs 目录页：[`docs/README.md`](docs/README.md)）

1. [`@Async` 心智模型：代理与线程切换](docs/part-01-async-scheduling/119-01-async-proxy-mental-model.md)
2. [Executor 与线程命名/并发边界](docs/part-01-async-scheduling/120-02-executor-and-threading.md)
3. [异常传播：Future vs void](docs/part-01-async-scheduling/121-03-exceptions.md)
4. [self-invocation：为什么异步有时不生效](docs/part-01-async-scheduling/122-04-self-invocation.md)
5. [`@Scheduled` 基础与可测试性](docs/part-01-async-scheduling/123-05-scheduling-basics.md)
6. [常见坑清单](docs/appendix/124-90-common-pitfalls.md)

## Labs / Exercises 索引

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java` | `@Async` 代理/线程/异常、自调用陷阱；`@Scheduled` 可测实验 | ⭐⭐ | 逐个跑测试并用断点跟线程 |
| Exercise | `src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java` | 扩展更多 async/scheduling 场景与断言 | ⭐⭐–⭐⭐⭐ | 从“自定义 executor”开始 |
