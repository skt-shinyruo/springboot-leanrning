# Change Proposal: 文档统一目录（docs/）+ 按主题重组（去模块 docs）

## Requirement Background

当前仓库的文档分散在两类位置：

1. 各模块自己的 `*/docs/**`（18 个模块，内容较多）
2. “主线之书”位于 `docs/book/**`（Book-only）

这会带来几个典型问题：

- **读者找路成本高**：同一主题的内容分散在不同模块路径里，很难“按主题聚合阅读”。
- **维护成本高**：一旦发生结构调整，需要同时处理多处入口与相对链接，改动面大、容易漏。

---

## Change Content

1. 新增统一文档根目录：`docs/`
2. 将 `*/docs/**` 全量迁移到 `docs/<topic>/**`，并按主题重新组织（同主题文档进入同一子目录）
3. 将 `docs/book/**` 全量迁移到 `docs/book/**`
4. 全仓更新所有引用：Markdown 链接 / 脚本 / docs-site 导航 / 说明文档

---

## Impact Scope

- **Modules:** 18 个模块（仅影响文档路径）+ `docs-site`
- **Files:** 大量 Markdown 文件移动/重命名 + 多处引用批量重写 + 删除/修改若干脚本与 CI workflow
- **APIs:** N/A
- **Data:** N/A

---

## Core Scenarios

### Requirement: 文档统一入口与可发现性
**Module:** docs
读者无需先进入某个模块目录，直接从 `docs/` 就能找到目标主题。

#### Scenario: 按主题集中查找
读者要学习 Bean/DI 相关内容时，能在 `docs/beans/` 一站式找到目录页与全部章节。
- 预期：同主题文档集中存放；目录结构清晰；入口 README 可直接跳转。

#### Scenario: Book 与主题文档共存
读者可从 `docs/book/` 顺读主线之书，也可从 `docs/<topic>/` 深挖细节。
- 预期：Book 章节与工具页在同一目录下；同时保留主题文档入口。

### Requirement: 单一源文档（移除模块 docs）
**Module:** all modules
迁移后不再保留 `*/docs/**` 目录，只保留 `docs/` 作为源文档。

#### Scenario: 去重与避免双写
维护者只需要修改 `docs/`，不会发生“模块 docs 与统一 docs 两份不一致”的问题。
- 预期：模块目录下无 `docs/`；所有引用已指向 `docs/`。

**Module:** scripts + CI + docs-site

- 预期：相关脚本与 workflow 被移除或改为非严格/非阻塞。

---

## Risk Assessment

- **Risk:** 大规模移动导致链接断裂、入口失效
  - **Mitigation:** 使用明确的迁移映射表 + 全仓批量替换 + `git mv` 保留历史；最终用全仓搜索确保旧路径引用清零。
- **Risk:** docs-site 构建/导航规则与新路径不兼容
  - **Mitigation:** 将 docs-site 的输入源切换到 `docs/`（或降低为非严格构建），保证基本可用；必要时下线 Pages workflow。

