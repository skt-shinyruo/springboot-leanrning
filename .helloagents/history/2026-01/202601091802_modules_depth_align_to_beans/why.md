# Change Proposal: 全模块深挖对齐（对标 spring-core-beans）

## Requirement Background

本仓库已经具备“可运行 + 可回归”的学习工程骨架（多模块 + docs + Labs/Exercises），并且全仓库测试可全绿。

但在多个模块中仍存在三类常见缺口，导致学习体验容易停留在“概念懂了但不会排障/不会深挖”的层面：

1. **Guide 仍以“契约骨架”兜底**：缺少把主题映射到时间线、关键类、关键分支与断点入口的“导航图”。
2. **章节缺少可断言的坑点/边界**：读者看完结论后缺少“可复现证据链”，很难把知识沉淀为稳定能力。
3. **关键分支缺少默认 Lab 覆盖**：某些关键机制/边界只在 Exercise 中出现，导致“默认回归=教材事实来源”不够强。

本变更的目标是把除 `spring-core-beans` 与 `springboot-web-mvc` 之外的所有模块，统一提升到接近 `spring-core-beans` 的“深挖可解释性”：

- 主线讲得清：能复述“它解决什么问题/关键约束/常见坑”；
- 证据链跑得通：关键结论能用默认 Lab 复现并断言；
- 排障能落地：能从“现象 → 分支定位 → 断点入口 → 修复策略”走完闭环。

## Change Content

1. **统一补齐模块 Guide 的机制主线**：把“时间线 / 关键参与者 / 关键分支 / 推荐断点 / 最小可运行实验”写实，并绑定可运行入口。
2. **每章补齐至少 1 个可断言坑点/边界**：坑点必须能回到默认 Lab 的测试方法（或在补齐后新增默认 Lab）。
3. **每模块提炼 2–5 个关键分支并确保默认 Lab 覆盖**：优先覆盖“最容易误判、且能稳定断言”的机制分支。
5. **知识库同步**：更新 `helloagents/wiki/modules/*.md` 与 `helloagents/CHANGELOG.md`，记录模块深化进度与变更索引。

## Impact Scope

- **Modules:**
  - `springboot-basics`
  - `springboot-actuator`
  - `springboot-testing`
  - `springboot-business-case`
  - `springboot-security`
  - `springboot-web-client`
  - `springboot-async-scheduling`
  - `springboot-cache`
  - `springboot-data-jpa`
  - `spring-core-aop`
  - `spring-core-aop-weaving`
  - `spring-core-events`
  - `spring-core-validation`
  - `spring-core-resources`
  - `spring-core-tx`
  - `spring-core-profiles`
- **Files:**
  - `*/docs/**.md`（补齐机制主线、坑点与边界、断点入口、可跑入口块）
  - `*/src/test/java/**`（新增/补强默认 Lab 证据链）
  - `helloagents/wiki/modules/*.md`（模块状态与变更历史同步）
  - `helloagents/CHANGELOG.md`（变更记录）
- **APIs:** 无新增对外 API（教育工程内部使用）
- **Data:** 无生产数据变更；仅测试/示例数据

## Core Scenarios

### Requirement: Module Guide Mainline Alignment
**Module:** All target modules
把每个模块的 `docs/part-00-guide/00-deep-dive-guide.md` 从“目录/口号”升级为“深挖导航图”。

#### Scenario: Guide Is A Navigation Map
读者只读 Guide 就能快速定位：
- 机制主线（时间线/关键参与者/关键分支）
- 推荐断点（能看到关键分支发生处）
- 推荐可跑入口（默认 Lab/Test 与建议命令）

### Requirement: Chapter Pitfall Evidence Linkage
**Module:** All target modules
每个章节至少包含 1 个“可断言的坑点/边界”。

#### Scenario: Each Chapter Has One Verifiable Pitfall
- 坑点写清：Symptom / Root Cause / Verification / Breakpoints / Fix
- Verification 必须指向默认启用的 `*LabTest#method`（或在本次补齐后新增默认 Lab）

### Requirement: Key Branch Default Labs
**Module:** All target modules
每个模块提炼 2–5 个关键分支，确保默认 Lab 覆盖并可回归。

#### Scenario: Key Branches Are Covered By Default Labs
- 关键分支可被稳定断言（避免只靠日志）
- 关键分支在 docs 中能一跳定位到对应 Lab/Test

### Requirement: Consistency Gates
**Module:** All target modules

#### Scenario: Repo Gates Stay Green
- `mvn -q test` 全绿
- 不再出现 “契约骨架兜底 / 坑点待补齐” 等占位内容

## Risk Assessment

- **Risk:** 工作量大，容易出现“模块间深度不均衡、风格不一致”。  
- **Risk:** 异步/并发/缓存相关新增测试可能 flaky。  
  **Mitigation:** 使用稳定观察点（线程名、CountDownLatch、手动 Ticker、固定超时）避免 sleep；必要时把复杂场景放到非默认 Exercise。
- **Risk:** 文档标题/导航调整导致断链。  
- **Risk:** “深入程度”变成主观叙述而非可验证结论。  
  **Mitigation:** 以测试断言与可复现实验为事实来源；文档结论必须绑定可跑入口。

