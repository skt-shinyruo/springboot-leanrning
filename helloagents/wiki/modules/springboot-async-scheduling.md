# springboot-async-scheduling

## Purpose

学习异步与调度：`@Async`、线程池、`@Scheduled` 与可测试策略。

## Module Overview

- **Responsibility:** 用最小示例与测试验证异步/调度行为与常见误区。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-22

## Specifications

### Requirement: 异步与调度学习闭环
**Module:** springboot-async-scheduling
覆盖线程切换、异常处理与调度触发。

#### Scenario: 异步执行发生在线程池
- 通过测试断言线程名/执行时机

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

- **Docs Index:** `docs/async-scheduling/springboot-async-scheduling/README.md`
- **Docs Guide:** `docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-00-deep-dive-guide.md`
- **Breakpoint Map:** `docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-02-breakpoint-map.md`
- **Branch Decision Matrix:** `docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-04-branch-decision-matrix.md`
- **Playbook:** `docs/async-scheduling/springboot-async-scheduling/appendix/124-90-common-pitfalls.md`
- **Self-check:** `docs/async-scheduling/springboot-async-scheduling/appendix/125-99-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBranchMatrixLabTest.java`
- **Lab:** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java`
- **Lab (Scheduling):** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSchedulingLabTest.java`
- **Exercise:** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseSolutionTest.java`
- **Lab（并发/性能：线程池饱和/拒绝策略）：** `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part02_perf_concurrency/BootAsyncSchedulingExecutorSaturationLabTest.java`
- **Book 专题页（方法论与样板索引）：** `docs/book/performance-and-concurrency.md`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；async/scheduling 示例集中在 `com.learning.springboot.bootasyncscheduling.part01_async_scheduling`
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_async_scheduling`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs）/ `testsupport`（稳定等待与断言工具）

## Change History

- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用
- [202601222034_solutions_perf_concurrency_batch01](../../history/2026-01/202601222034_solutions_perf_concurrency_batch01/) - ✅ 已执行：补齐 Exercises 对应 Solutions（默认参与回归）+ 新增并发/性能可复现 Lab（线程池饱和/拒绝策略）+ 提供稳定等待工具 `Waiter`
