# Change Proposal: 全模块文档书本化升级（A–G 章节契约 + 跨模块主线）

## Requirement Background

目前仓库已经完成了“最小书本化骨架”的建设：

1. 各模块都有 `docs/README.md` 作为章节目录（SSOT）；
2. 章节末尾已有统一的导航（上一章｜目录｜下一章）与“对应 Lab/Test”入口块；

但在“像书一样可连续阅读”的层面仍存在明显不一致：

- 章节内部结构差异很大：有的偏“讲义/笔记”，有的偏“索引/清单”，读者跨模块学习需要反复适应；
- 同一类知识点在不同章节的呈现方式不稳定，导致“找结论/找主线/找断点/找实验”的操作成本高；
- `docs/README.md` 的章节顺序与 Part 归类在部分模块仍有优化空间（你已允许重排编号/Part 归类/文件移动）。

本次变更目标：将 **18 个模块、全部章节**统一升级为同一套“章节契约（A–G）”，并把跨模块学习主线落实到根 `README.md`，让读者可以用一致的方式读完任意一章、并能快速回到可运行验证入口。

## Product Analysis

### Target Users and Scenarios

- **User Groups:**
  - 初学者：需要稳定的“先看结论→再看主线→最后跑实验”的阅读节奏
  - 有经验开发者：遇到问题时希望快速定位“边界/坑点/断点入口”
  - 讲师/复盘者：需要可复用的章节结构（便于授课、复盘、面试复述）
- **Usage Scenarios:**
  - 连续阅读：从 `docs/README.md` 开始按章节顺序读完整个模块
  - 跳读排障：直接打开某一章，1–2 次跳转定位到可运行验证入口
  - 跨模块串联：从根 `README.md` 的路线图选择下一模块继续学习
- **Core Pain Points:**
  - 章节结构不统一导致“阅读方法”成本高于“知识点理解”
  - 部分章节缺少明确的“核心结论/断点入口/实验建议”，读完难以复述与验证

### Value Proposition and Success Metrics

- **Value Proposition:** 用 A–G 章节契约把“知识点”变成“可连续阅读 + 可复现 + 可排障”的统一体验。
- **Success Metrics:**
  - 覆盖范围：`pom.xml` 的 18 个模块、`docs/README.md` 引用的全部章节（预计约 190 章）
  - 结构一致性：全部章节满足 A–G 七个二级标题（`##`）存在性要求
  - 可跑入口：全部章节包含“对应 Lab/Test”并至少引用 1 个真实存在的 `*LabTest`
  - 目录可读：各模块 `docs/README.md` 顺序与 Part 归类优化后仍保持 0 断链

### Humanistic Care

- 用一致的章节结构降低学习焦虑：读者不需要“先理解作者的写法”
- 避免信息过载：A–G 结构强调“先结论、再主线、再源码/断点、最后实验”
- 避免引入敏感信息：文档不粘贴敏感配置、凭据、生产地址等

## Change Content

1. **A–G 章节契约落地（全量）**：为所有章节重写/重排为统一结构，确保“定位→结论→主线→源码/断点→实验→坑点→小结”稳定存在。
2. **模块级目录优化（允许重排）**：按主线阅读体验调整 `docs/README.md` 的 Part 归类、顺序与编号，并在必要时移动/重命名章节文件以对齐目录 SSOT。
3. **跨模块主线（根 README）**：在根 `README.md` 提供“书本化跨模块阅读路线”，并链接到每个模块的 `docs/README.md`（目录页 SSOT）。

## Impact Scope

- **Modules:** `pom.xml` 中全部 18 个模块（`spring-core-*` + `springboot-*`）
- **Files:**
  - `*/docs/README.md`（目录页：顺序/Part/编号/链接）
  - `*/docs/**/*.md`（章节：A–G 重写/重排 + 入口块/LabTest 引用 + 导航）
  - `README.md`（跨模块主线）
  - `scripts/*`（新增/增强契约检查与批处理脚本）
  - `helloagents/wiki/*`、`helloagents/CHANGELOG.md`、`helloagents/history/index.md`（知识库同步）
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: all-modules-ag-contract
**Module:** all
将所有章节统一升级为 A–G 章节契约，并保证每章具备可运行闭环入口（至少 1 个 `*LabTest`）。

#### Scenario: ag-contract-and-labtest
在任意模块中打开任意章节（不包含 `docs/README.md`）：
- 存在 7 个二级标题（`##`）：A/B/C/D/E/F/G（标题文本允许中英文混排，但必须显式出现 A–G 标识）
- 存在“对应 Lab/Test”区块，且至少引用 1 个真实存在的 `*LabTest`（可带 `#method`）
- 章节仍保留“上一章｜目录｜下一章”导航，且相对链接可达

### Requirement: docs-readme-reorder-and-part-normalization
**Module:** all
允许并完成 `docs/README.md` 的顺序/编号/Part 归类优化，使其成为稳定的“章节主线 SSOT”。

#### Scenario: docs-readme-mainline-order
对任意模块：
- `docs/README.md` 的章节列表顺序能支撑连续阅读（guide → 主线 parts → appendix）
- 章节链接目标文件存在，且与章节编号/Part 归类一致
- 目录页不出现“重复链接导致顺序歧义”（必要时将“快速定位”与“主线目录”分区处理）

### Requirement: root-readme-cross-module-mainline
**Module:** repo root
根 `README.md` 提供跨模块的“书本化主线”，并可一键跳转到各模块目录页 SSOT。

#### Scenario: cross-module-route-is-executable
- 根 `README.md` 提供明确的跨模块学习顺序（至少覆盖 `springboot-*` 主线 + `spring-core-*` 机制线）
- 每个模块入口链接指向：`<module>/docs/README.md`
- 读者按路线能持续阅读：模块内（上一章/下一章）+ 模块间（根 README 指引）

## Risk Assessment

- **Risk:** 章节数量大（约 190 章），批量重写容易引入结构不一致或遗漏
- **Risk:** 强制“每章至少 1 个 LabTest”可能导致部分章节只剩“通用入口”而缺少针对性
  - **Mitigation:** 先保证硬性合规（每章至少 1 个 LabTest），再在后续迭代中逐章收敛到“章级最小实验”
- **Risk:** 允许 `docs/README.md` 重排与文件移动，可能引入断链
  - **Mitigation:** 每次变更后跑断链检查；必要时提供回滚策略（以 README 为 SSOT 可再生成）
