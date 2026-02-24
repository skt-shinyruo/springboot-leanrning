# Change Proposal: spring-boot-async-scheduling 加深（二）：线程上下文传播（ThreadLocal/MDC）证据链

## Requirement Background

在真实项目里，`@Async` 的“最大坑”往往不是能不能切线程，而是：

- **切线程后上下文丢失**：ThreadLocal / MDC / SecurityContext / RequestContext 等不再可用
- **上下文泄漏**：线程池复用线程，如果你“只复制不清理”，会把上一次任务的上下文泄漏到下一次任务

这类问题在排障时表现为：

- 日志 traceId 丢失（链路断了）
- 业务上下文（租户/用户/灰度标记）丢失
- 偶发“串号/串上下文”导致极难复现的线上事故

当前模块已经把 executor 选择、proxy 边界、异常语义、`@Scheduled` 注册/异常语义等关键分支固化成可回归 Labs，但“上下文传播”仍缺少一个**可断言、可教学**的最小闭环。

## Change Content

1. 新增 context propagation Lab：用 ThreadLocal 作为最小模型复现“默认不传播”与“TaskDecorator 修复”，并额外固化“无泄漏（restore/clear）”的断言。
2. 文档加深：把 ThreadLocal/MDC 的机制边界与修复策略写进第 120 章（executor/线程模型）与 Pitfalls/Self-check，并在 Branch Matrix 文档中加入对应分支与证据入口。

## Impact Scope

- **Modules:** `spring-boot-modules/spring-boot-async-scheduling`
- **Files:** `src/test/java/**`（新增 Lab）+ `docs/**`（补齐机制解释与证据入口）+ 矩阵入口聚合
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: ThreadLocal/MDC 跨线程的边界可复现
**Module:** springboot-async-scheduling

#### Scenario: 默认不传播（证据链）
- 条件：调用线程设置 ThreadLocal 值，调用 `@Async` 方法读取该值
- 期望：异步线程读取不到（值为 null），并能证明确实发生了线程切换（线程名前缀断言）

#### Scenario: TaskDecorator 修复 + 无泄漏（证据链）
- 条件：配置 `ThreadPoolTaskExecutor#setTaskDecorator(...)` 捕获并恢复上下文
- 期望：
  - 异步线程能读取到调用线程的上下文
  - 任务执行后上下文被清理/恢复，不会泄漏到后续任务（同一线程复用时也成立）

## Risk Assessment

- **Risk:** 上下文传播测试容易被误写成 flaky（依赖 sleep / 并发时序）
- **Mitigation:** 采用 `CompletableFuture#get(timeout)` + 单线程 executor（线程复用可控）+ 明确“线程名/上下文值”双断言，避免依赖时间窗口。

