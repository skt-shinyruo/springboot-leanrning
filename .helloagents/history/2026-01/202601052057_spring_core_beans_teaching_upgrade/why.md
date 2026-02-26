# Change Proposal: spring-core-beans 教学闭环（源码级）全量深化

## Requirement Background

`spring-core-beans` 模块已经具备较好的“书本化目录 + 可运行 Lab”基础，但与 build-teaching-repo 的目标仍存在差距：

- 章节深度不完全一致：部分章节已能做到源码级解释与断点闭环，但全量章节尚未统一达到“调用链主线 + 关键分支/边界条件”的深度要求。
- 练习题与参考答案不成对：已有 Exercise（默认禁用、避免破坏回归），但缺少对应 Solution（默认参与回归），导致“可验证契约”无法持续成立。
- 目录索引与检索体验仍有提升空间：`docs/README.md` 偏目录 TOC，对应的 Lab/Exercise/Solution 映射与“从问题到入口”的检索路径需要更明确。

本变更希望把模块从“能跑通”升级为“全章源码级可验证 + 可做练习 + 可对答案”，并且默认参与回归，避免知识点漂移。

## Change Content

1. **全章节源码级深化（B）**：为每章补齐/统一 Deep Dive（源码主线调用链 + 关键分支/边界条件 + 断点入口与观察点），形成可复述主线。
2. **Exercise → Solution 全量补齐（A）**：为现有 Exercise 增加对应 Solution（默认参与回归），确保答案持续可验证。
3. **索引与互链优化（C）**：增强 `docs/beans/spring-core-beans/README.md` 的章节映射能力（章节 ↔ Lab ↔ Exercise ↔ Solution），并补齐检索入口（从“现象/问题”定位到“章节 + 可运行入口”）。
4. **持续验证**：所有改动必须通过模块默认回归与 docs 相对链接检查，防止“文档漂移/断链”。

## Impact Scope

- **Modules:**
  - `spring-core-beans`
  - `helloagents`（知识库同步、变更记录）
- **Files:**
  - `docs/beans/spring-core-beans/**`（全量章节）
  - `spring-core-beans/src/test/java/**`（新增/补齐 Solution，必要时补小型辅助类）
  - `helloagents/wiki/modules/spring-core-beans.md`（更新现状与入口）
  - `helloagents/CHANGELOG.md`（记录变更）
- **APIs:** 无对外 API 变更（内部教学与测试入口变更）
- **Data:** 无数据结构变更

## Core Scenarios

### Requirement: 全章节源码级 Deep Dive 统一补齐
**Module:** spring-core-beans
将每章提升为“源码级可验证”：提供调用链主线、关键分支/边界条件、断点入口与观察点，并与可运行 Lab 对齐。

#### Scenario: 每章都能从“现象”追到“源码主线”
- 给出最小复现入口（Lab）
- 给出调用链草图（入口 → 决策点 → 产物/副作用）
- 至少解释 1 个关键分支/边界条件（触发条件 + 走向差异 + 观察点）

### Requirement: Exercise 对应 Solution（默认参与回归）
**Module:** spring-core-beans
对已有 Exercise 增加 Solution，并让 Solution 默认参与回归，保证答案持续可验证。

#### Scenario: 读者能“做练习”并“对照答案”
- Exercise 保持默认不执行（不破坏回归）
- Solution 默认执行并可通过
- 文档能够指向 Exercise 与 Solution 的入口

### Requirement: Docs 索引/互链/检索体验优化
**Module:** spring-core-beans
增强 `docs/README.md` 与附录地图，使读者能从目录与检索快速定位到：章节、Lab、Exercise、Solution。

#### Scenario: 从“问题关键字”快速跳到“章节 + 可运行入口”
- 在 docs 目录页提供可检索的映射结构（章节 ↔ 入口）
- 章节内统一“复现入口/运行命令/断点建议/练习题指引”的结构化块

### Requirement: 持续验证（回归 + 链接检查）
**Module:** spring-core-beans
所有增量必须可回归：测试全绿、docs 内链无断裂，且变更能在 CI 中持续稳定。

#### Scenario: 变更不会引入知识点漂移与断链
- 模块测试通过
- docs 相对链接检查通过

## Risk Assessment

- **Risk:** 改动面大，容易出现文档断链/入口漂移/测试不稳定。
  - **Mitigation:** 分 Part 分批提交；每批次都跑模块测试与 docs 链接检查；优先复用现有 Lab，不引入环境依赖。
- **Risk:** Solution 默认参与回归会增加维护成本。
  - **Mitigation:** Solution 优先复用 Lab 入口与最小实现；避免依赖脆弱日志；尽量以稳定断言为准。
