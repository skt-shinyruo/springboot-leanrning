# Spring Boot Async & Scheduling：目录

> 建议先把 @Async 的代理主线跑通，再处理线程池与异常，最后进入 @Scheduled 的触发链路与边界。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/117-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/118-00-deep-dive-guide.md)

## 顺读主线

- [@Async 心智模型](part-01-async-scheduling/119-01-async-proxy-mental-model.md)
- [Executor 与线程模型](part-01-async-scheduling/120-02-executor-and-threading.md)
- [异常处理](part-01-async-scheduling/121-03-exceptions.md)
- [self-invocation](part-01-async-scheduling/122-04-self-invocation.md)
- [@Scheduled 基础](part-01-async-scheduling/123-05-scheduling-basics.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[118-02-breakpoint-map.md](part-00-guide/118-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[118-04-branch-decision-matrix.md](part-00-guide/118-04-branch-decision-matrix.md)
- 排障 playbook：[124-90-common-pitfalls.md](appendix/124-90-common-pitfalls.md)
- 自检清单：[125-99-self-check.md](appendix/125-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
- 并发/性能专题（可复现实验范式）：[performance-and-concurrency.md](../../book/performance-and-concurrency.md)
- 可跑入口（线程池饱和/拒绝策略）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExecutorSaturationLabTest test`

## 排坑与自检

- [常见坑](appendix/124-90-common-pitfalls.md)
- [自检](appendix/125-99-self-check.md)
