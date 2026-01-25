# Change Proposal: solutions_perf_concurrency_batch01（Solutions 补齐 + 并发/性能可复现专题）

## Requirement Background

当前仓库已具备“按主题拆分模块 + docs 站点 + Labs/Exercises”这一套教学化基础设施，但存在两个明显缺口：

1. **Exercises 没有对应 Solution（可对照答案）**：多数模块只有 `*ExerciseTest`（且被 `@Disabled`），缺少默认参与回归的 `*ExerciseSolutionTest`，导致学习闭环缺少“参考实现 + 可验证”这一环。
2. **并发/性能专题缺少“可复现、不 flaky”的统一范式**：一些模块已经在正文里提到并发/线程边界，但缺少一份统一的写法指南与可复现样例（尤其是“线程池饱和/拒绝策略”“跨线程边界”“避免 time-based 断言”）。

## Change Content

1. 为新增主题模块补齐 `*ExerciseSolutionTest`，让 Exercises 具备可运行对照答案闭环
2. 为 Async/Scheduling 与 Events 增加一组“可复现并发/性能”Labs（避免 flaky），并沉淀统一写法
3. 新增 Book 级“性能与并发专题”页面：总结可复现并发测试范式（CountDownLatch/线程名/拒绝策略/避免 sleep）

## Impact Scope

- **Modules:**
  - `springboot-autoconfiguration`
  - `springboot-logging`
  - `springboot-observability`
  - `spring-core-spel`
  - `springboot-async-scheduling`
  - `spring-core-events`
- **Files:**
  - `docs/book/*`（新增专题页 + 目录接入）
  - `docs/*/*/README.md`（必要时补充入口链接）
  - `docs/book/labs-index.md`（重新生成）
  - `src/test/java/**`（新增 Solution/LabTest）
- **APIs:** 无对外 API 变更（学习工程内部结构增强）
- **Data:** 无数据模型变更

## Core Scenarios

### Requirement: 主题模块具备 Solutions 闭环
**Module:** springboot-autoconfiguration / springboot-logging / springboot-observability / spring-core-spel
为每个主题提供可运行的 Solution（对应 Exercise 的目标），并默认参与回归，保证学习时“先做题 → 再看答案 → 再打断点”可闭环。

#### Scenario: Exercise 对应 Solution 可运行且不影响主线 Labs
- Solution 以“独立测试上下文/独立配置”的方式实现，避免修改主线示例导致回归不稳定
- Solution 与现有 `*LabTest` 不互相干扰

### Requirement: 并发/性能专题可复现且不 flaky
**Module:** springboot-async-scheduling / spring-core-events / book
新增一组并发/性能相关 Labs，并沉淀统一写法，确保：

- 不依赖不稳定的时间窗口（避免以 `Thread.sleep`/耗时阈值做断言）
- 以 CountDownLatch、线程名、RejectedExecution（拒绝策略）等“可观测点”做断言

#### Scenario: 线程池饱和/拒绝策略可被确定性复现
- 小线程池 + 无队列（或极小队列）+ 阻塞任务 → 第二次提交触发拒绝
- 断言拒绝异常（或 future 异常完成）稳定可回归

#### Scenario: 事件异步分发/线程边界可被确定性断言
- `@Async` listener 与自定义 multicaster 两条路径分别可复现
- 以线程名/CountDownLatch 固化“是否异步”而不是依赖日志时序

**Module:** docs-site / scripts / helloagents

- `mvn -q test` 通过

## Risk Assessment

- **Risk:** 并发/调度测试天然容易 flaky（时间窗口/线程调度不确定）
  - **Mitigation:** 统一采用 latch/线程名/可观测对象断言；避免基于耗时阈值的断言；超时设置为“短但足够”（如 1s）并确保任务可释放退出
- **Risk:** 新增 Solution/LabTest 增加回归时间
  - **Mitigation:** 每个模块优先补齐“最小样板”，避免引入长时间 sleep 与外部依赖

