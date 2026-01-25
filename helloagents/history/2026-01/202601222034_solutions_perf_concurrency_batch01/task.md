# Task List: solutions_perf_concurrency_batch01（Solutions 补齐 + 并发/性能可复现专题）

Directory: `helloagents/history/2026-01/202601222034_solutions_perf_concurrency_batch01/`

---

## 1. Solutions（新增主题模块）

- [√] 1.1 为 `springboot-autoconfiguration` 新增 `BootAutoConfigurationExerciseSolutionTest`（独立 AutoConfiguration 分支 + backoff 断言），verify why.md#requirement-主题模块具备-solutions-闭环-scenario-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.2 为 `springboot-observability` 新增 `BootObservabilityExerciseSolutionTest`（commonTags/MeterFilter 固化 tag 断言），verify why.md#requirement-主题模块具备-solutions-闭环-scenario-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.3 为 `springboot-logging` 新增 `BootLoggingExerciseSolutionTest`（MDC + Logback ListAppender 固化断言），verify why.md#requirement-主题模块具备-solutions-闭环-scenario-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.4 为 `spring-core-spel` 新增 `SpringCoreSpelExerciseSolutionTest`（变量/函数注册），verify why.md#requirement-主题模块具备-solutions-闭环-scenario-exercise-对应-solution-可运行且不影响主线-labs

## 2. 并发/性能（可复现 Labs + 专题沉淀）

- [√] 2.1 `springboot-async-scheduling`：新增并发/线程池饱和 Lab（拒绝策略可复现），verify why.md#requirement-并发性能专题可复现且不-flaky-scenario-线程池饱和拒绝策略可被确定性复现
- [√] 2.2 `spring-core-events`：新增 Exercises 对应 Solution（多 listener/order/async/async multicaster/condition），verify why.md#requirement-并发性能专题可复现且不-flaky-scenario-事件异步分发线程边界可被确定性断言
- [√] 2.3 `spring-core-spel` 或 `springboot-observability`：补齐一个“并发可复现”Lab（多线程求值或并发请求计数），verify why.md#requirement-并发性能专题可复现且不-flaky-scenario-线程池饱和拒绝策略可被确定性复现

## 3. 文档：并发/性能专题

## 4. Security Check（强制）

- [√] 4.1 安全自检（G9）：无生产环境操作、无明文密钥/Token、无破坏性脚本命令

- [√] 5.2 运行 `mvn -q test`

## 6. Knowledge Base Sync + Archive（强制）

- [√] 6.1 同步更新 `helloagents/wiki/**` 与 `helloagents/CHANGELOG.md`（记录本批新增 Solutions/专题/新增 Labs）
- [√] 6.2 迁移方案包：`helloagents/plan/202601222034_solutions_perf_concurrency_batch01/` → `helloagents/history/2026-01/202601222034_solutions_perf_concurrency_batch01/`
- [√] 6.3 更新 `helloagents/history/index.md` 索引记录（✅Completed）
