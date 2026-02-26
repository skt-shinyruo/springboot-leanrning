# Change Proposal: spring-core-beans 逐章内容深度完善（Solution）

## Requirement Background

当前 `spring-core-modules/spring-core-beans` 已经具备较完整的“主线叙事 + Labs + 断点/排障资产”，但仍存在一个典型问题：**章节之间深度不均衡**——有些章更偏概念/导航，有些章已经下沉到源码与断点；读者在真实排障/复述时，会遇到“知道概念但无法证明/无法分型定位/无法举反例”的断裂。

本方案的目标不是套统一模板，也不是补固定模块，而是**逐章阅读后**给出“与该章主题匹配”的内容级深挖策略，并把策略落到可执行的任务清单（后续可用 ~exec 落地）。

## Change Content

1. 为 `spring-core-beans` 全章节（README + Docs TOC + Part 00–05 + Appendix + deepening-strategies）逐章产出“补充/完善/深入”的具体策略。
2. 将逐章策略整理为可执行的 task list（按文件分组），便于迭代式落地与回归验证。
3. 补齐跨章节的一致性治理思路：术语/证据链入口/断点资产/Lab 映射的统一与互链。

## Impact Scope

- **Modules:**
  - `spring-core-modules/spring-core-beans`
- **Files (planned):**
  - `spring-core-modules/spring-core-beans/README.md`
  - `spring-core-modules/spring-core-beans/docs/**/*.md`（含 Part 00–05、Appendix、deepening-strategies）
  - （可选）`spring-core-modules/spring-core-beans/src/test/**`：为“证据链”补更贴近章节的 Lab/Explore 用例（落地阶段执行）

## Core Scenarios

### Requirement: 逐章内容深度完善
**Module:** spring-core-beans docs

目标：每一章都能在其主题范围内做到“可理解、可证明、可复现、可排障、可复述”。

#### Scenario: 读者“概念懂了但不会证明”
- 预期：每章提供至少一个“最短证据链入口”（关键方法/断点/Lab），并说明如何观察变量得出结论。

#### Scenario: 读者“遇到异常但不知道属于哪一层”
- 预期：每章按其主题给出可操作的“分型定位路径”（症状 → 分层/分支 → 第一断点 → 关键变量 → 可能原因）。

#### Scenario: 读者“会背结论但不会举反例/边界”
- 预期：每章至少补一个可复现反例或边界触发条件，用来区分相似概念（例如 depends-on 环 vs 循环依赖，@Order vs @Primary 等）。

## Risk Assessment

- **Risk:** 逐章改动量大，容易出现风格漂移、重复堆叠、交叉引用断裂。
  - **Mitigation:** 以“章主题优先”做增补，不强制同一结构；改动分批落地；每批跑回归并做链接/锚点校验。
- **Risk:** 新增/调整 Lab 可能引入不稳定测试。
  - **Mitigation:** Explore 用例默认不纳入回归；回归测试保持确定性；对版本敏感断点增加稳定性注记与替代入口。
