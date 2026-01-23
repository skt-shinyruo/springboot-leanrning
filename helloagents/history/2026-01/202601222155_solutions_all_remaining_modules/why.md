# Change Proposal: solutions_all_remaining_modules（剩余模块 Solutions 全量补齐）

## Requirement Background

当前仓库已形成统一的教学结构：

- Labs：`*LabTest.java`（默认启用，必须全绿）
- Exercises：`*ExerciseTest.java`（默认 `@Disabled`，学习者手动开启）

但仍有一批模块存在 “Exercises 有、Solutions 缺” 的断层：学习者做完练习后缺少可对照的参考实现，也缺少“答案是否符合预期”的回归锚点。

本变更目标：**对剩余所有缺口模块补齐 `*ExerciseSolutionTest.java`**，让每个主题模块都具备 “文档 → Lab（证据链）→ Exercise（动手）→ Solution（对照/回归）” 的闭环，并且不破坏默认回归（`mvn -q test` 全绿）。

## Change Content

1. 为所有“有 Exercise 但缺 Solution”的模块新增 `*ExerciseSolutionTest.java`（默认参与回归）。
2. Solutions 以“独立测试上下文”为默认策略：不修改原 `*ExerciseTest`（保持 `@Disabled`），避免污染主线 Labs。
3. 统一可复现与稳定性准则：避免基于耗时阈值的断言，优先断言线程边界/异常路径/可观测事实。
4. 补齐文档入口：更新模块目录页与 Book 工具页，让 Solutions 可发现、可检索、可直接运行。
5. 全量推广并发与性能可复现实验：为剩余模块补齐 “可回归且不 flaky” 的并发/性能样板 Lab，并接入 Book 专题页索引。
6. 同步更新知识库（`helloagents/wiki/**`）与变更记录（`helloagents/CHANGELOG.md`、`helloagents/history/index.md`），并归档方案包至 `helloagents/history/YYYY-MM/`。

## Impact Scope

- **Modules:**
  - Spring Boot：`springboot-basics` / `springboot-web-mvc` / `springboot-data-jpa` / `springboot-actuator` / `springboot-testing` / `springboot-business-case` / `springboot-security` / `springboot-web-client` / `springboot-cache`
  - Spring Core：`spring-core-aop` / `spring-core-aop-weaving` / `spring-core-tx` / `spring-core-validation` / `spring-core-resources` / `spring-core-profiles`
  - 补齐缺口：`spring-core-beans`（`part04_wiring_and_boundaries` 的 ExerciseSolution 缺口）
- **Files:** 以新增 `src/test/java/**/**ExerciseSolutionTest.java` 为主，必要时新增少量 testsupport（严格控制不引入外部服务依赖）。
- **APIs:** N/A（以教学测试为主，不新增对外 API）。
- **Data:** N/A（不引入真实外部数据库/生产依赖；如需存储，仅使用测试内存数据库或 fake 实现）。

## Core Scenarios

### Requirement: 剩余模块具备 Solutions 闭环
**Module:** multi-modules
每个缺口模块：新增 `*ExerciseSolutionTest`，能独立运行通过，并且不影响该模块既有 Labs 的证据链与默认回归。

#### Scenario: springboot-basics Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-basics test` 全绿

#### Scenario: springboot-web-mvc Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-web-mvc test` 全绿

#### Scenario: springboot-data-jpa Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-data-jpa test` 全绿

#### Scenario: springboot-actuator Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-actuator test` 全绿

#### Scenario: springboot-testing Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-testing test` 全绿

#### Scenario: springboot-business-case Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-business-case test` 全绿

#### Scenario: springboot-security Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-security test` 全绿

#### Scenario: springboot-web-client Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-web-client test` 全绿

#### Scenario: springboot-cache Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :springboot-cache test` 全绿

#### Scenario: spring-core-aop Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :spring-core-aop test` 全绿

#### Scenario: spring-core-aop-weaving Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :spring-core-aop-weaving test` 全绿

#### Scenario: spring-core-tx Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :spring-core-tx test` 全绿

#### Scenario: spring-core-validation Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :spring-core-validation test` 全绿

#### Scenario: spring-core-resources Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :spring-core-resources test` 全绿

#### Scenario: spring-core-profiles Exercise 对应 Solution 可运行且不影响主线 Labs
- `mvn -q -pl :spring-core-profiles test` 全绿

#### Scenario: spring-core-beans part04 ExerciseSolution 缺口补齐并保持默认回归
- `mvn -q -pl :spring-core-beans test` 全绿

### Requirement: Solutions 入口在文档中可发现
**Module:** docs
补齐“做题 → 对照 → 回归”的路径：学习者能从模块目录页与 Book 工具页快速定位到对应 Solution 的可跑入口。

#### Scenario: Book 工具页提供 Solutions 索引与约定
- `docs/book/exercises-and-solutions.md` 增加 Solutions 的发现入口（命名规则/运行方式/与 Exercises 的关系）

#### Scenario: 各模块目录页提供 Solution 可跑入口
- 对本批涉及模块的 `docs/<topic>/<module>/README.md` 增加 Solution 的可跑入口命令（与 Book 工具页互相链接）

### Requirement: 并发与性能可复现实验覆盖所有剩余模块
**Module:** multi-modules
对本批剩余模块补齐“并发与性能可复现实验”样板：每个模块至少 1 个，且不依赖耗时阈值（不 flaky）。

#### Scenario: 每个模块新增至少 1 个不 flaky 的并发与性能 Lab 并接入 Book 专题页
- 每个模块新增至少 1 个 `*LabTest.java`（建议放入 `part02_perf_concurrency`），以 latch/线程边界/异常路径/指标等“可观测事实”作为断言
- `docs/book/performance-and-concurrency.md` 增加这些 Lab 的入口索引
- `python3 scripts/generate-book-labs-index.py` 生成的 `docs/book/labs-index.md` 纳入新增 Lab
- `mvn -q test` 全绿

## Risk Assessment

- **Risk:** 一次性跨 16 个模块新增默认回归测试，容易引入 flaky（并发/异步/时序依赖）或环境依赖（网络/端口/外部服务）。
  - **Mitigation:** 统一“可复现实验范式”：避免耗时阈值断言；优先断言异常路径/线程边界/指标/日志；必要时使用 latch 与明确的等待工具（带超时诊断）。
- **Risk:** 为了 mock 外部交互（WebClient/Actuator/Security）可能引入新依赖，影响依赖树与兼容性。
  - **Mitigation:** 优先使用 Spring 测试内置能力（MockMvc/TestRestTemplate/WebTestClient/MockRestServiceServer），只有在确实缺能力时再评估新增依赖，并记录到 `helloagents/CHANGELOG.md`。
