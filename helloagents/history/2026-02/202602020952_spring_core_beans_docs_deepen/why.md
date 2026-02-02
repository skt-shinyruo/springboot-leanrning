# Change Proposal: spring-core-beans 文档逐章深度完善（机制 / 源码 / 排障）

## Requirement Background

`spring-core-beans` 模块的文档体系已经具备很好的“教程化骨架”（章节学习卡片、Lab/Test 入口、断点地图、知识地图、附录工具链，以及 `deepening-strategies/` 的按章深化建议）。

本次改造的背景与动机主要来自一个新的目标读者画像：

- 读者已有 Spring 使用经验，不满足于“会用 API”，更希望获得 **机制解释 + 源码入口 + 可排障路径** 的可证明能力；
- 希望每章都能更快回答：**这章解决什么生产问题？第一断点在哪里？关键变量是什么？怎么用 Lab 自证？**
- 同时希望补齐“权威对照”：允许加入 Spring Framework / Spring Boot 官方文档链接，并标注适用版本（避免读者只记二手结论）。

## Change Content

1. 先产出 “逐章深度完善草案（交付方式 B）”：对每个章节给出更贴合本章主题的补充/完善/深入方向（偏机制+源码+排障），并给出推荐的官方文档对照入口。
2. 执行期按草案逐章落盘到文档正文：
   - 允许新增/合并/重排小节（目标是缩短证据链、减少误判、强化下一跳），但尽量避免引入“为了统一而统一”的固定模板。
3. 将 `deepening-strategies/` 作为“继续加深建议”的事实来源（SSOT），并与各章正文中的 `AE-DEEPENING` 提示块对齐，提升可执行性与差异化。
4. 同步更新知识库（`helloagents/wiki/modules/spring-core-beans.md`）与变更记录（`helloagents/CHANGELOG.md`），并在执行完成后迁移 solution package 到 `helloagents/history/`。

## Impact Scope

- **Modules:** `spring-core-beans`
- **Files（执行期预计影响）：**
  - `spring-core-modules/spring-core-beans/README.md`
  - `spring-core-modules/spring-core-beans/docs/**/*.md`（含 `deepening-strategies/` 与 `appendix/`）
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`

## Core Scenarios

### Requirement: 逐章深度完善（机制 / 源码 / 排障）
**Module:** spring-core-beans  
对每个章节给出更贴合主题的补强方向，并在落盘时让正文更“可证明、可排障”。

#### Scenario: 读者能把主观判断变成可验证结论
- 能从异常/现象定位到“第一入口方法”
- 能解释关键分支与关键变量（watch list）
- 能用 Lab/Test 稳定复现并验证修复

### Requirement: 官方文档对齐（版本标注）
**Module:** spring-core-beans  
允许加入 Spring Framework / Spring Boot 官方 reference 链接，并标注适用版本。

#### Scenario: 读者能快速核对权威定义与边界
- 每章提供 1–2 个相关 reference 入口（优先指向具体页面而非仅首页）
- 明确标注适用版本：Spring Boot `3.5.9`；Spring Framework `6.2.x`（本仓库文档索引基线 `6.2.15`）

### Requirement: 结构优化与跨章跳转（允许重排）
**Module:** spring-core-beans  
允许对章节结构进行新增/合并/重排，以减少重复、增强“下一跳”与证据链完整度。

#### Scenario: 读者不迷路、不会误判层级
- 导航页能把“现象 → 第一断点 → 下一章”压到最短
- “继续加深”提示块按章差异化（不是统一口号）
- 工具型章节（索引/清单/快照）明确“如何用/别怎么用”

## Risk Assessment

- **Risk：大规模文档变更导致链接断裂/结构漂移**  
  **Mitigation：** 分批落盘（按 Part 逐步推进），每批都做链接与站点构建校验（MkDocs build）。
- **Risk：引入过度统一模板导致信息密度下降**  
  **Mitigation：** 以“章目标/读者动作/证据链”为中心做差异化补强，目录型/索引型章节保持“送读者到下一步动作”的定位，不强行教程化。
- **Risk：版本不一致导致结论偏差**  
  **Mitigation：** 在涉及版本敏感的章节显式标注适用版本，并将官方文档链接作为权威对照；必要时在正文标注“在 X 版本之后/之前”的差异点。

