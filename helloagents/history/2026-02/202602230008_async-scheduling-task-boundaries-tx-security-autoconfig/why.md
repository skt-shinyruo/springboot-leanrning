# Change Proposal: spring-boot-async-scheduling 加深（三）——任务边界（事务 / SecurityContext / RequestContext）与 Boot `spring.task.*` 自动装配

## Requirement Background

`spring-boot-async-scheduling` 已经覆盖了 `@Async` / executor 选择 / proxy 边界 / 异常语义 / `@Scheduled` 注册与异常语义 / 组合注解等主线，并通过 Labs 把结论固化为可回归断言。

当把这些机制带到真实工程，会出现第三类“高频、隐蔽、代价大”的问题：**跨线程之后，很多“以为会跟着走”的东西其实不会走**，以及 **Spring Boot 的默认装配让行为看起来“有点像魔法”，但缺少可断言解释**。

典型症状包括：

1. 事务边界误解：在一个 `@Transactional` 方法里调用 `@Async`，以为异步逻辑也在同一个事务里（或能共享回滚语义）。
2. 安全/请求上下文丢失：在调用方线程里有 `SecurityContext` / `RequestContext`（或类似 ThreadLocal/MDC），到了异步线程就变成 null；更糟糕的是线程池复用导致串号/泄漏。
3. Boot 默认装配不可见：`spring.task.execution.*` / `spring.task.scheduling.*` 配置究竟影响了哪个 bean？`@Async` / `@Scheduled` 到底用的哪个 executor/scheduler？为什么日志提示 “No TaskScheduler bean found”？

本轮加深的目标：把以上三类问题全部落到 **“能复现 → 能断言 → 能定位 → 能修复”** 的手册级闭环（docs + tests + 可运行 DemoRunner）。

## Change Content

1. 新增三条证据链 Labs：
   - `@Async × @Transactional`：事务是否存在、事务在哪个线程、事务是否跟随调用方传播
   - `SecurityContext / RequestContext`：默认不传播、正确传播、错误实现导致泄漏
   - Spring Boot `spring.task.*` 自动装配：默认 executor/scheduler 的创建、属性映射、与 `@EnableAsync/@EnableScheduling` 的交互
2. 新增 `src/main` 可运行示例（DemoRunner）：
   - 通过 `mvn -pl :spring-boot-async-scheduling spring-boot:run` 直接观察线程名/上下文/自动装配结果
3. 文档体系加深（新增主线章节 + 更新矩阵入口/断点地图/排障 Playbook/自检题）。

## Impact Scope

- **Modules:** `spring-boot-modules/spring-boot-async-scheduling`
- **Files:**
  - `pom.xml`（依赖补齐：spring-tx / spring-security-core / spring-web（test scope）等）
  - `src/main/java/**`（新增 Application + DemoRunner + 示例 Service）
  - `src/test/java/**`（新增 LabTests，并纳入 Book/Branch Matrix）
  - `docs/**`（新增章节与更新入口页）
  - `helloagents/wiki/modules/springboot-async-scheduling.md` / `helloagents/CHANGELOG.md` / `helloagents/history/index.md`

## Core Scenarios

### Requirement: `@Async` × `@Transactional` 事务边界可断言
**Module:** springboot-async-scheduling
把“事务在哪个线程、是否传播、异常与回滚语义在哪一侧生效”落到可断言证据链。

#### Scenario: 调用方事务不会跨 `@Async` 线程边界自动传播
- 调用方线程事务 active，但异步线程事务 inactive（默认）
- 以 `TransactionSynchronizationManager` 的状态作为稳定观测点

#### Scenario: `@Async @Transactional` 的事务发生在异步线程（不是调用方）
- 同一个方法同时具备两类 AOP：async 拦截负责切线程，tx 拦截负责开事务
- 通过断言证明“事务在 async 线程里开启并生效”

### Requirement: SecurityContext / RequestContext 上下文传播与泄漏可复现
**Module:** springboot-async-scheduling
把“默认丢失 + 正确传播 + 错误实现会泄漏”写成可回归实验。

#### Scenario: 默认不传播（丢失）
- 调用方线程有 Authentication/RequestAttributes
- 异步线程读取为 null

#### Scenario: 正确传播（并保证 finally 清理）
- 使用 Spring Security 的 Delegating* executor 或 `TaskDecorator` 捕获/恢复
- 第二次任务不会读到上一次残留（无串号）

#### Scenario: 错误实现导致线程池复用串号（泄漏）
- 捕获到 null 时跳过装饰 / 未在 finally 恢复/清理
- 通过“单线程池 + 两次任务”确定性复现

### Requirement: Spring Boot `spring.task.*` 自动装配可解释、可断言
**Module:** springboot-async-scheduling
把默认 executor/scheduler 的来源与属性映射写成 Labs，并对齐 `@Async/@Scheduled` 的实际行为。

#### Scenario: `spring.task.execution.*` 映射到默认 TaskExecutor 的线程名/池参数
- 属性 -> bean -> 线程名（证据链）

#### Scenario: `spring.task.scheduling.*` 映射到默认 TaskScheduler 的线程名/池参数
- pool.size 与 thread-name-prefix 可观测

### Requirement: DemoRunner 可运行示例（非 web）
**Module:** springboot-async-scheduling
提供 `spring-boot:run` 的可运行入口，用日志/输出复述关键结论。

#### Scenario: 一次运行输出关键观测点
- 输出 executor/scheduler bean 信息
- 输出 async/tx/security/request 的“默认 vs 修复”对比结果

## Risk Assessment

- **Risk:** 新增依赖可能引入理解噪音或构建成本上升（尤其是 web/servlet 相关依赖）。
  - **Mitigation:** 依赖保持最小化：事务使用 `ResourcelessTransactionManager`；Security 只引入 `spring-security-core`；RequestContext 相关依赖尽量限制在 test scope。
- **Risk:** 并发/调度测试可能 flaky。
  - **Mitigation:** 禁用长 sleep；使用 `CompletableFuture#get(timeout)`、`CountDownLatch`、注册断言与线程名前缀观察点；控制线程池为单线程确保复现稳定。
