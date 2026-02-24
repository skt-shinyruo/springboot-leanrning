# Change Proposal: spring-boot-async-scheduling 手册级内容加深（Async + Scheduling）

## Requirement Background

当前 `spring-boot-modules/spring-boot-async-scheduling` 模块已经具备：

- 可运行的最小示例代码（`@Async` / self-invocation / 线程名观测点）
- tests-first 的 Lab/Exercise 入口（`ApplicationContextRunner` + 断言）
- 文档主线章节与排障入口（Docs + Breakpoint Map + Branch Matrix）

但在“做成 Spring 知识手册”的目标下，仍存在两个主要缺口：

1. **机制解释不够落地**：部分章节仍偏“结论骨架”，缺少可复现的证据链（调用链、分支条件、可断言点）。
2. **关键边界缺少覆盖**：executor 选择优先级、`@Scheduled` 异常语义、`@Scheduled + @Async` 组合、self-invocation 的多种修法对比等高频坑位，需要用“可跑实验”固化。

## Change Content

1. 新增/强化“手册级”示例代码与实验测试：覆盖 executor 选择、代理类型边界、异常传播、调度注册与异常语义、组合注解等关键分支。
2. 加深主线文档内容：把每个结论落到“可断言的证据入口 + 源码锚点 + 分支矩阵”。
3. 重整 Matrix 入口：Book Matrix 只保留主线最小集合；Branch Matrix 聚合关键分支与排坑入口，避免重复。

## Impact Scope

- **Modules:** `spring-boot-modules/spring-boot-async-scheduling`
- **Files:**
  - 示例代码：`spring-boot-modules/spring-boot-async-scheduling/src/main/java/**`
  - 实验测试：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/**`
  - 文档：`spring-boot-modules/spring-boot-async-scheduling/docs/**`
- **APIs:** 无对外 API（本模块 `web-application-type=none`，以 tests-first 为主）
- **Data:** 无

## Core Scenarios

### Requirement: @Async 的“机制证据链”可复现
**Module:** spring-boot-async-scheduling
将 `@Async` 的关键分支（是否启用、是否走代理、executor 如何选择、异常如何传播、self-invocation 是否绕过）写成可运行的实验并在文档中引用。

#### Scenario: executor 选择优先级可断言
- 条件：存在多个 executor（不同名字/类型），或 `@Async("...")` 显式选择
- 期望：
  - 默认 executor 选择规则清晰且可在测试中断言
  - 显式 `@Async("beanName")` 能稳定切到指定 executor

#### Scenario: void 异步异常可观测
- 条件：`@Async void` 抛异常
- 期望：
  - 异常不会传回调用方，但会进入 `AsyncUncaughtExceptionHandler`
  - handler 能拿到 method + args（用于日志/监控）

### Requirement: @Scheduled 的“注册/触发/异常语义”讲清楚
**Module:** spring-boot-async-scheduling

#### Scenario: 任务注册结果可确定性验证
- 条件：启用 `@EnableScheduling` 并声明不同类型的 `@Scheduled`
- 期望：测试能断言任务被注册为 FixedRateTask / FixedDelayTask / CronTask（不依赖真实时间触发）。

#### Scenario: 任务执行异常的后果可验证
- 条件：`@Scheduled` 方法抛出异常
- 期望：明确它是否会继续被后续调度、异常由谁处理，并提供可跑实验固化语义。

#### Scenario: @Scheduled + @Async 组合的执行线程可观测
- 条件：同一方法同时标注 `@Scheduled` 与 `@Async`
- 期望：能用断言证明“调度触发线程”和“真实执行线程”的区别，帮助解释为何需要把耗时逻辑卸载到 async executor。

## Risk Assessment

- **Risk:** 时间相关测试容易 flaky（调度触发/并发时序）
- **Mitigation:** 优先断言“注册结果/数据结构”（`ScheduledTaskHolder`），触发类测试使用 `CountDownLatch` + 上限超时 + 线程名前缀作为稳定观测点，避免长 `Thread.sleep`。
