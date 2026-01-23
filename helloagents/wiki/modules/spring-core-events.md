# spring-core-events

## Purpose

学习 Spring 应用事件：发布/订阅、顺序、condition、同步/异步与事务边界集成。

## Module Overview

- **Responsibility:** 通过最小示例与 Labs/Exercises 展示事件系统的机制与常见坑。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-22

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
  - 对应测试类：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBookMatrixLabTest.java`

## Specifications

### Source Layout
- docs：`docs/events/spring-core-events/README.md`（目录页）
- docs：`docs/events/spring-core-events/part-00-guide/`（深挖指南）
- docs：`docs/events/spring-core-events/part-01-event-basics/`（事件基础）
- docs：`docs/events/spring-core-events/part-02-async-and-transactional/`（异步与事务事件）
- docs：`docs/events/spring-core-events/appendix/`（常见坑/自测题）
- src(main)：`spring-core-modules/spring-core-events/src/main/java/com/learning/springboot/springcoreevents/SpringCoreEventsApplication.java`（入口，包名保持不变）
- src(main)：`spring-core-modules/spring-core-events/src/main/java/com/learning/springboot/springcoreevents/part01_event_basics/**`
- src(test)：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/**`
- src(test)：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/**`

### Docs Index
- 入口：`docs/events/spring-core-events/README.md`
- 断点地图：`docs/events/spring-core-events/part-00-guide/128-02-breakpoint-map.md`
- 关键分支矩阵：`docs/events/spring-core-events/part-00-guide/128-04-branch-decision-matrix.md`
- 排障 playbook：`docs/events/spring-core-events/appendix/136-90-common-pitfalls.md`
- 自检清单：`docs/events/spring-core-events/appendix/137-99-self-check.md`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`

### Requirement: 事件系统学习闭环
**Module:** spring-core-events
通过测试实验覆盖同步/异步、异常传播、多监听器顺序与 condition/payload。

#### Scenario: 默认同步与异常传播
- listener 抛异常能传播回 publisher（可断言）
- `spring-boot:run` 可观察线程与异常传播（结构化前缀 `EVENTS:`）

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** spring-core-events
把“同步/异步/异常传播/事务事件回调”写成可断言主线，并补齐默认 Lab 入口与章节坑点证据链。

#### Scenario: 关键分支可被稳定断言
- 默认同步与异常传播可断言
- 自定义 multicaster + TaskExecutor 的异步分发可通过默认 Lab 稳定复现

### Labs & 复现入口
- `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java`
- `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsMechanicsLabTest.java`
- `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsListenerFilteringLabTest.java`
- `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsTransactionalEventLabTest.java`
- `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsAsyncMulticasterLabTest.java`
- Solution（Exercises 对应答案回归）：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseSolutionTest.java`
- Book 专题页（并发/性能可复现范式）：`docs/book/performance-and-concurrency.md`

## Change History

- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 覆盖补齐）
- [202601092110_depth_align_v2_batch01_sec_jpa_events_client](../../history/2026-01/202601092110_depth_align_v2_batch01_sec_jpa_events_client/) - ✅ 已执行：batch01 深挖对齐 v2（补齐 listener filtering 默认 Lab + async/tx 章节坑点入口 + 自测入口补齐）
- [202601071034_all_modules_docs_ag_contract](../../history/2026-01/202601071034_all_modules_docs_ag_contract/) - ✅ 已执行：全模块 docs 章节结构整理（A–G 结构 + 对应 Lab/Test 入口块）；后续不再推荐 A–G 作为写作规范/闸门
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601061556_spring_core_modules_teaching_rollout](../../history/2026-01/202601061556_spring_core_modules_teaching_rollout/) - ✅ 已执行：补齐 docs/07（TransactionalEventListener）可运行闭环（新增事务事件 Lab + docs 入口块），并对齐 docs 目录页/入口块规范与自检脚本
- [202601021322_complete_spring_core_fundamentals_remaining](../../history/2026-01/202601021322_complete_spring_core_fundamentals_remaining/) - ✅ 已执行：补齐 `EventsDemoRunner` 结构化输出（线程/异常传播）与 throwing listener（特定输入触发）
- [202601041046_spring-core-part-structure-sync](../../history/2026-01/202601041046_spring-core-part-structure-sync/) - ✅ 已执行：对齐 docs Part 目录结构与 src/main+src/test 分包结构（语义化 Part 命名），并修复 README/跨模块引用路径
- [202601222034_solutions_perf_concurrency_batch01](../../history/2026-01/202601222034_solutions_perf_concurrency_batch01/) - ✅ 已执行：补齐 Exercises 对应 Solutions（多 listener/order/async/condition/async multicaster），并把“异步分发线程边界”纳入可回归证据链

## Dependencies

- 基于 `spring-core-beans` 的 IoC/Bean 基础（学习路径依赖）
- 测试实验引入 `spring-tx`（用于 `@TransactionalEventListener` 的事务同步回调语义）
