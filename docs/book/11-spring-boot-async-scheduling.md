# 11 Spring Boot Async & Scheduling：线程池、异常传播与上下文边界

## 学习目标

- 能解释 `@Async` / `@Scheduled` 的生效机制：代理边界、线程切换与默认线程池来源。
- 能区分三类常见问题：异步不生效 / 异步异常“消失” / 上下文（Security/MDC/Request）丢失或泄漏。
- 能把异步与事务、日志、观测串联起来（跨线程时语义会变化）。

## 概念框架

- **代理与生效条件**：
  - `@Async`/方法安全/事务等共享一个事实：必须经过代理，self-invocation 会绕过。
- **执行器（Executor）与调度器（Scheduler）**：
  - 线程池选择通常受 Boot 默认配置与 `spring.task.*` 配置影响，需用测试验证“到底是哪一个”。
- **异常传播**：
  - Future/CompletableFuture 与 void 返回值在异常传播上完全不同；需要明确“调用方能不能感知”。
- **上下文传播**：
  - SecurityContext、MDC、RequestContext 默认可能不跨线程，需要显式策略；错误传播会导致泄漏与串线。
- **与事务的关系**：
  - `@Async` 与 `@Transactional` 叠加时，事务边界与执行线程可能错位（参见 [05 Tx](05-spring-core-tx.md)）。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
  - 测试类：[`BootAsyncSchedulingBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-boot-async-scheduling/docs/README.md`](../../spring-boot-modules/spring-boot-async-scheduling/docs/README.md)
- 导航型文档（用于定位 self-invocation/线程池/事务叠加）：
  - `@Async` 心智模型：[`part-01-async-scheduling/01-async-proxy-mental-model.md`](../../spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/01-async-proxy-mental-model.md)
  - self-invocation：[`part-01-async-scheduling/04-self-invocation.md`](../../spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/04-self-invocation.md)
  - `@Async` × `@Transactional`：[`part-01-async-scheduling/06-async-and-transactions.md`](../../spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/06-async-and-transactions.md)

## 常见误区

- 以为 `@Async` 让方法“自动变快”。异步是边界与并发模型，不是性能魔法；错误的线程池/队列会让系统更慢。
- 以为异步异常会像同步异常一样返回给调用方。需要按返回类型（Future vs void）分型。
- 以为上下文会自动传播。SecurityContext/MDC/RequestContext 需要显式策略，否则就是“默认丢失/偶发串线”。

## 练习

- 练习 1（异步是否生效的证据链）：
  - 运行 `BootAsyncSchedulingBookMatrixLabTest`；
  - 选择一个“异步不生效”入口，回答两问：
  - 调用是否经过代理（必要时回看 [04 AOP](04-spring-core-aop.md)）？
  - 实际执行线程来自哪个 Executor（线程名/配置映射证据）？
- 练习 2（与事务叠加的边界）：
  - 选择一个 `@Async` × `@Transactional` 场景，记录：
  - 事务边界是否跨线程；
  - 失败时回滚/可见性是否符合预期（参见 [05 Tx](05-spring-core-tx.md)）。

## 小结

- 异步与调度的难点不在语法，而在边界：代理、线程池、异常、上下文传播。
- 下一章进入 Cache，把“缓存作为性能边界”放到可验证主线里，避免只靠直觉调参。

## 延伸阅读

- 代理边界（self-invocation 同类问题）：[`04-spring-core-aop.md`](04-spring-core-aop.md)
- 事务边界（跨线程语义变化）：[`05-spring-core-tx.md`](05-spring-core-tx.md)
- 日志与 MDC（跨线程关联）：[`13-observability-and-actuator.md`](13-observability-and-actuator.md)

---

[← 上一章](10-spring-boot-web-client.md) | [目录](README.md) | [下一章 →](12-spring-boot-cache.md)

