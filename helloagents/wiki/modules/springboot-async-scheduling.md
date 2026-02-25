# springboot-async-scheduling

## Purpose

学习异步与调度：`@Async`、线程池、`@Scheduled` 与可测试策略。

## Module Overview

- **Responsibility:** 用最小示例与测试验证异步/调度行为与常见误区。
- **Status:** 🚧In Development
- **Last Updated:** 2026-02-23

## Specifications

### Requirement: 异步与调度学习闭环
**Module:** springboot-async-scheduling
覆盖线程切换、异常处理与调度触发。

#### Scenario: 异步执行发生在线程池
- 通过测试断言线程名/执行时机

#### Scenario: executor 选择规则可断言
- 默认 executor（单一/多 executor）选择规则、`@Async("beanName")` 显式选择、`AsyncConfigurer` 覆盖

#### Scenario: ThreadLocal/MDC 上下文传播边界可复现
- 默认不传播（线程切换后读取为 null）
- `TaskDecorator` 捕获+恢复可修复，并通过 finally 清理避免线程池复用导致的泄漏/串号

#### Scenario: `@Async` × `@Transactional` 事务边界可断言
- 调用方事务不会跨线程边界自动传播（异步线程默认 tx inactive）
- `@Async @Transactional` 组合时，事务发生在异步线程（需要能用断言证明“事务在哪个线程生效”）

#### Scenario: SecurityContext / RequestContext 传播与泄漏可复现
- SecurityContext 默认不跨线程；可用 Delegating* executor 正确传播并清理
- RequestContext 默认不跨线程；可用 TaskDecorator 传播；错误实现可确定性复现泄漏

#### Scenario: Spring Boot `spring.task.*` 自动装配可解释、可断言
- `spring.task.execution.*` 属性映射到默认 executor，并影响 `@Async` 线程名
- `spring.task.scheduling.*` 属性映射到默认 scheduler，并影响 `@Scheduled` 线程名

#### Scenario: 定时任务注册/异常语义可验证
- 注册断言（FixedRate/FixedDelay/Cron）优先，触发类断言最小化以避免 flaky
- 异常进入 ErrorHandler 且任务继续调度（可观测、可回归）

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** springboot-async-scheduling
把“@Async 代理语义/线程池选择/异常传播/调度触发与并发边界”落到可断言的默认 Lab 与断点入口。

#### Scenario: Guide 主线可作为导航图
- Guide 已补齐：@Async proxy 心智模型、Executor/Threading、异常处理、Scheduling 基础与排障入口

#### Scenario: 章节坑点可回归
- 每章至少 1 个可断言坑点，并绑定默认 `*LabTest#method` 作为证据链

## Dependencies

- 基于 `spring-core-events`/`spring-core-beans` 的基础概念（可选）

## Docs & 复现入口

- **Docs Index:** `spring-boot-modules/spring-boot-async-scheduling/docs/README.md`
- **Docs Guide:** `spring-boot-modules/spring-boot-async-scheduling/docs/part-00-guide/02-deep-dive-guide.md`
- **Call Chain:** `spring-boot-modules/spring-boot-async-scheduling/docs/part-00-guide/03-async-and-scheduling-call-chain.md`
- **Breakpoint Map:** `spring-boot-modules/spring-boot-async-scheduling/docs/part-00-guide/04-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-boot-modules/spring-boot-async-scheduling/docs/part-00-guide/05-branch-decision-matrix.md`
- **Playbook:** `spring-boot-modules/spring-boot-async-scheduling/docs/appendix/01-common-pitfalls.md`
- **Self-check:** `spring-boot-modules/spring-boot-async-scheduling/docs/appendix/02-self-check.md`
- **Run Demo:** `mvn -pl :spring-boot-async-scheduling spring-boot:run`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBranchMatrixLabTest.java`
- **Lab:** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java`
- **Lab（executor 选择矩阵）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingExecutorSelectionLabTest.java`
- **Lab（Proxy 类型与 final 边界）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingProxyTypeLabTest.java`
- **Lab（void 异常：handler method+args）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingUncaughtExceptionHandlerLabTest.java`
- **Lab（上下文传播/泄漏：TaskDecorator）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingContextPropagationLabTest.java`
- **Lab（事务边界：@Async × @Transactional）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingTransactionBoundaryLabTest.java`
- **Lab（SecurityContext 传播/清理）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSecurityContextPropagationLabTest.java`
- **Lab（RequestContext 传播/泄漏）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingRequestContextPropagationLabTest.java`
- **Lab（Boot spring.task.* 自动装配）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSpringTaskAutoConfigurationLabTest.java`
- **Lab (Scheduling):** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSchedulingLabTest.java`
- **Lab（Scheduling 注册断言）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSchedulingRegistrationLabTest.java`
- **Lab（Scheduling 异常语义）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSchedulingExceptionSemanticsLabTest.java`
- **Lab（@Scheduled + @Async）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingScheduledAsyncCombinationLabTest.java`
- **Exercise:** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseSolutionTest.java`
- **Lab（并发/性能：线程池饱和/拒绝策略）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part02_perf_concurrency/BootAsyncSchedulingExecutorSaturationLabTest.java`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；async/scheduling 示例集中在 `com.learning.springboot.bootasyncscheduling.part01_async_scheduling`
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_async_scheduling`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs）/ `testsupport`（稳定等待与断言工具）

## Change History

- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用
- [202601222034_solutions_perf_concurrency_batch01](../../history/2026-01/202601222034_solutions_perf_concurrency_batch01/) - ✅ 已执行：补齐 Exercises 对应 Solutions（默认参与回归）+ 新增并发/性能可复现 Lab（线程池饱和/拒绝策略）+ 提供稳定等待工具 `Waiter`
- [202602221600_async-scheduling-handbook-deepen](../../history/2026-02/202602221600_async-scheduling-handbook-deepen/) - ✅ 已执行：加深 Async/Scheduling 手册级证据链（executor 选择矩阵、proxy 类型边界、void 异常可观测、Scheduling 注册/异常语义、@Scheduled+@Async 组合）并回归无 flaky
- [202602222318_async-scheduling-context-propagation](../../history/2026-02/202602222318_async-scheduling-context-propagation/) - ✅ 已执行：新增 ThreadLocal/MDC 上下文传播证据链（默认不传播/TaskDecorator 修复/错误实现导致泄漏）并补齐 Pitfalls/Self-check/Branch Decision Matrix/Breakpoint Map 与索引入口
- [202602230008_async-scheduling-task-boundaries-tx-security-autoconfig](../../history/2026-02/202602230008_async-scheduling-task-boundaries-tx-security-autoconfig/) - ✅ 已执行：补齐任务边界证据链（@Async×@Transactional / SecurityContext / RequestContext）与 Boot `spring.task.*` 自动装配 Labs，并新增可运行 DemoRunner + 新增主线章节 126–128
- [202602230930_async-scheduling-humanize-writing](../../history/2026-02/202602230930_async-scheduling-humanize-writing/) - ✅ 已执行：docs 全量“人写化”改写（入口/主线/工具页/附录），README 改为双入口叙事，并将 DemoRunner 输出改为分节讲解；通过 `mvn -q -pl :spring-boot-async-scheduling test` 与 `mvn -q -pl :spring-boot-async-scheduling spring-boot:run` 验证
