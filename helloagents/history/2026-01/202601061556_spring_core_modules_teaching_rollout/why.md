# Change Proposal: Spring Core 系列模块教学化规范全量推广（对齐 spring-core-beans）

## Requirement Background

当前仓库中 `spring-core-beans` 已形成“可阅读 + 可跳转 + 可运行 + 可回溯”的教学化闭环：

- 文档书本化（Part + 编号 + 目录页 + 附录）
- 每章可落到仓库代码（Labs/Exercises/Solutions），并能通过测试断言验证结论
- 关键机制具备可观察性与断点入口（debugging/observability）
- 规范化自检（Markdown 相对链接检查）
- 知识库（`helloagents/wiki/`）同步记录变更与演进脉络

但其它 `spring-core-*` 模块虽然已有基础文档与测试，仍存在一致性问题，典型表现包括：

- docs 内链/跨 Part 链接的相对路径写法不一致，且存在断链
- 使用 `docs/07` 这类“章节缩写引用”，容易被误解为真实路径，且不可跳转/不可脚本校验
- “章节 ↔ 可跑实验入口”对照不完整：读完章节后不知道跑哪个 Lab/Exercise 来验证
- 自检脚本默认覆盖范围有限（需要人为逐个模块执行）

本变更目标是把 `spring-core-beans` 的形态与约束，推广到其它 `spring-core-*` 模块，并通过脚本把结论固化成“可断言”的标准。

## Change Content

1. 统一 `spring-core-*` 模块 docs 的书本化约定（Part/编号/目录页/附录）
2. 全量修复 docs 内链（含跨模块链接），并用脚本保证“链接目标必存在”
3. 统一替换 `docs/xx` 这类缩写引用为真实章节链接（可点击、可校验）
4. 强化“每章至少一个可跑入口”的闭环：在章节中提供 Lab/Exercise/Solution 对照，并可被脚本校验
5. 升级/补齐自检脚本：覆盖全部 `spring-core-*` docs，并增加“章节↔实验映射”的一致性检查
6. 同步更新知识库：记录每个模块的变更与迁移路径，避免“文档与代码不一致”

## Impact Scope

- **Modules:**
  - `spring-core-aop`
  - `spring-core-aop-weaving`
  - `spring-core-events`
  - `spring-core-profiles`
  - `spring-core-resources`
  - `spring-core-tx`
  - `spring-core-validation`
  - `scripts/`（自检脚本）
  - `helloagents/wiki/`（模块知识库同步）
- **Files:**
  - 各模块 `docs/**/*.md`
  - 各模块 `src/test/java/**/*LabTest.java`（必要时新增）
  - `scripts/check-md-relative-links.py`（可能扩展默认覆盖范围）
  - 新增 `scripts/check-teaching-coverage.py`（章节↔实验映射校验）
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: docs-link-integrity
**Module:** scripts + all `spring-core-*`

#### Scenario: check-md-relative-links-passes
- 对所有 `spring-core-*` 的 `docs/` 执行 Markdown 相对链接自检，结果为 `[OK]`，missing targets 为 0。
- 对于跨模块引用（例如 tx ↔ aop），相对路径可正确解析到仓库中的真实文件。

### Requirement: docs-no-shorthand-references
**Module:** all `spring-core-*`

#### Scenario: no-docs-xx-shorthand
- 文档正文中不再出现 `docs/07`、`docs/06/12/...` 这类“章节缩写引用”。
- 需要引用章节时，一律使用真实 Markdown 链接（指向 `docs/part-*/NN-*.md`）。

### Requirement: chapter-lab-closure
**Module:** all `spring-core-*`

#### Scenario: each-chapter-has-runnable-entry
- 每个章节（以 `docs/README.md` 中列出的 `.md` 为准）至少提供一个可跑入口：
  - `*LabTest.java`（优先）
  - 或 Exercise/Solution（如该模块已有练习体系）
- 章节对照中引用的代码/测试路径可在仓库中找到（可被脚本校验）。

### Requirement: minimum-labs-per-module
**Module:** all `spring-core-*`

#### Scenario: each-module-has-at-least-2-lab-tests
- 每个模块至少拥有 2 个可跑 `*LabTest.java`（`spring-core-profiles` 需要补齐到 2）。

### Requirement: docs-index-and-numbering
**Module:** all `spring-core-*`

#### Scenario: docs-readme-covers-all-chapters
- `docs/README.md` 作为目录页，列出所有章节，并按编号/Part 组织。
- 章节文件命名遵循 `NN-*.md`，并与 Part 目录语义对齐。

### Requirement: knowledge-base-sync
**Module:** helloagents wiki

#### Scenario: module-wiki-change-history-updated
- `helloagents/wiki/modules/<module>.md` 的 Change History 记录本次推广变更（可追溯到方案包与关键文件）。

## Risk Assessment

- **Risk:** 多模块同时移动/重排文件，容易引入断链与阅读路径混乱
- **Risk:** 新增/调整测试可能导致整体回归变慢或失败
  - **Mitigation:** 优先用 `ApplicationContextRunner` 等轻量方式写 Lab；按模块逐个 `mvn -pl <module> test` 验证
- **Risk:** 工作区当前存在未提交改动，可能与本次变更混杂
  - **Mitigation:** 执行阶段以“可复现验证”为准（脚本 + tests），并在知识库中记录关键决策与变更点
