# Change Proposal: 补齐 Spring Core Fundamentals 剩余任务（AOP / Events / Tx）

## Requirement Background
本仓库此前已在 `spring-core-beans` 模块完成了深挖路线与面试点落位，并补齐了大量可断言的 Labs/Exercises。

但在 `spring-core-aop` / `spring-core-events` / `spring-core-tx` 三个基础模块中，仍存在少量“学习体验收口”缺口：
- `spring-boot:run` 的运行态输出不够结构化，难以把“看到的现象”快速绑定到“可断言入口”。
- `spring-core-tx` 缺少一个默认启用且断言稳定的 “自调用绕过事务” 最小复现实验（Lab）。
- `docs/progress.md` 的“最小打卡动作”目前只覆盖 `spring-core-beans`，不利于统一学习节奏。

## Change Content
1. 为 AOP / Events / Tx 的 DemoRunner 补齐统一风格的结构化输出（固定前缀 + 关键观察点）。
2. 为 Tx 增加一个稳定 Lab：自调用绕过 `@Transactional` 的最小复现 + 一种修复对比。
3. 在 `docs/progress.md` 增补三模块的最小闭环建议（含可选运行态观察）。

## Impact Scope
- **Modules:**
  - `spring-core-aop`
  - `spring-core-events`
  - `spring-core-tx`
  - `docs`
  - `helloagents`（知识库同步、变更记录与历史归档）
- **Files:**
  - DemoRunner：3 个模块的 `src/main/java/.../*DemoRunner.java`
  - Tx 新增 Lab：`spring-core-tx/src/test/java/.../*LabTest.java`
  - 进度清单：`docs/progress.md`
  - 知识库：`helloagents/wiki/modules/*`、`helloagents/CHANGELOG.md`、`helloagents/history/index.md`

## Core Scenarios

### Requirement: 运行态输出可作为稳定观察点
**Module:** `spring-core-aop` / `spring-core-events` / `spring-core-tx`
运行模块时，应能在控制台快速定位“关键机制结论”，且输出具有稳定 key 前缀，方便回看与对照 docs/Labs。

#### Scenario: AOP 代理与自调用现象可被观察
- 当执行 `mvn -pl spring-core-aop spring-boot:run`：
  - 输出包含代理类型（JDK/CGLIB）、被拦截方法、以及自调用导致 `inner(...)` 不触发 advice 的结论。

#### Scenario: Events 的同步线程与异常传播可被观察
- 当执行 `mvn -pl spring-core-events spring-boot:run`：
  - 输出展示 listener 与 publisher 的线程名一致（默认同步）。
  - 输出展示 listener 抛异常会回炸到 publisher（runner 捕获并打印）。

#### Scenario: Tx 的事务活跃状态与回滚/提交差异可被观察
- 当执行 `mvn -pl spring-core-tx spring-boot:run`：
  - 输出展示事务在 `@Transactional` 方法内为 active。
  - 输出展示回滚/提交前后数据条数差异。

### Requirement: Tx 自调用陷阱可稳定复现并对比修复
**Module:** `spring-core-tx`
提供一个默认启用的 Lab，复现：同类内 `this.inner()` 调用导致 `@Transactional` 不生效，并给出一种规避方式对比。

#### Scenario: 自调用导致“以为有事务，实际没有事务”
- 复现链路：外层方法调用同类内部标注 `@Transactional` 的 inner 方法并抛异常。
- 断言：数据未回滚（因为未进入事务），且可观测到事务未激活。

#### Scenario: 通过拆分 Bean 修复自调用问题
- 复现链路：外层方法调用另一个 Bean 的 `@Transactional` 方法并抛异常。
- 断言：数据回滚，且事务在 inner 方法内激活。

### Requirement: Progress 清单覆盖四模块最小闭环
**Module:** `docs`
在 `docs/progress.md` 中补齐 `spring-core-aop` / `spring-core-events` / `spring-core-tx` 的最小打卡动作（对齐 beans 的写法）。

#### Scenario: 学习者按清单可完成“跑通 + 断言 + 可选运行态观察”
- 每个模块给出 2–3 个推荐入口测试 + 可选 `spring-boot:run` 观察点。

## Risk Assessment
- **Risk:** DemoRunner 输出格式调整可能与现有 README 的描述存在轻微偏差。
- **Mitigation:** 同步更新 `docs/progress.md` 与 helloagents/wiki 的模块描述；保持原有关键结论不变，只增强结构化呈现。

