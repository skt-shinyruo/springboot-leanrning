# 为什么要做（Why）

## 背景

当前仓库已经完成了 **Book Matrix（最小可运行测试入口）** 的全模块推广，使每个主题模块都具备“可一键跑通主线”的回归入口。但在“深入学习 + 工程化排障”的体验上，仍存在明显断层：

- **关键分支矩阵（Branch Matrix）缺失/不统一**：多数模块缺少“关键分支/边界条件”的统一入口（只靠散落的 `*LabTest`，很难知道哪些是主线、哪些是分支、哪些是排障证据链）。
- **断点地图（Breakpoint / Watchpoint Map）缺失**：除 `springboot-web-mvc` 与 `spring-core-beans` 外，其它模块没有系统化的断点/观察点清单，读者很难把文档叙事与代码调用链对齐。
- **排障 Playbook 结构不一致**：虽然各模块已有 `common-pitfalls` / `self-check`，但缺少统一的“复现入口 + 证据收集 + 分支决策表”结构，导致排障步骤可复制性不足。

`springboot-web-mvc` 已具备较成熟的“测试入口 + 调试入口 + 排障叙事”组合，因此本轮以它为模板，将同类能力推广到剩余模块，并与现有 Book Matrix 协同工作。

## 目标（Goals）

- 为所有剩余模块补齐并统一：
  - **关键分支矩阵**（面向“关键分支/边界条件/决策点”的最小可运行入口 + 文档化矩阵）
  - **断点地图**（入口断点、关键分支断点、Watchpoint 建议、常见定位路径）
  - **排障 Playbook**（按“症状 → 复现 → 证据 → 决策 → 修复”的结构，提供可复制流程）
- 保持与现有 **Book Matrix** 的层次关系清晰：
  - Book Matrix：主线最小集合（“先跑通”）
  - Branch Matrix：关键分支最小集合（“再覆盖关键分支”）
  - Breakpoint Map / Playbook：把“怎么调、怎么查、怎么定位”固化为文档入口

## 非目标（Non-goals）

- 不在本轮做“全站文档重排/大规模重命名编号”的大改造（除非为新增入口必须）。
- 不追求一次性把每个模块的所有边界条件都覆盖到极致：先提供**可持续扩展的统一骨架**，后续按模块迭代加深矩阵项与测试覆盖。

## 验收标准（Success Criteria）

- 每个模块（除特殊说明外）至少具备：
  - `BranchMatrixLabTest`（或等价的分支聚合入口）
  - 断点地图文档页面
  - 关键分支矩阵文档页面
  - `common-pitfalls` 中包含统一的“排障 Playbook”结构块（或与其等价的可复制排障结构）
- 工具链验证通过：
  - `mvn -q test`

## 风险与约束（Risks & Constraints）

- **JUnit Suite 选类可见性**：大量 `*LabTest` 为 package-private，跨包聚合会编译失败；必须遵循“Suite 与被选测试同包”或“将被选测试改为 public”的策略。
- **AOP Weaving（LTW/CTW）执行环境差异**：存在 surefire 多 execution + `-javaagent` 的约束；分支矩阵/断点地图需要明确拆分入口与运行方式。
