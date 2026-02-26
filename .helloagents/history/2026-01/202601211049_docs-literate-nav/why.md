# Change Proposal: 文档即目录（Literate Nav）

## Requirement Background

当前仓库的文档站点已具备“模块文档 + 主线之书（Book）”的双入口，但目录的维护仍依赖脚本注入与额外配置文件。

你提出的目标是：

- **文档即目录**：目录本身就是一份 Markdown 文档，按文档顺序即站点导航顺序
- **不再维护独立目录配置**：不希望再额外维护一份“目录/导航”配置（例如 YAML/脚本生成 nav 的方式）

## Change Content

1. 引入 MkDocs `literate-nav`：用 `docs/SUMMARY.md` 作为站点唯一导航（目录本身就是文档）。
2. `docs-site/mkdocs.yml` 的 `nav` 直接引用 `docs/SUMMARY.md`（不再由脚本注入）。
3. 清理与下线旧的“导航注入/主题 SSOT 脚本与配置”（避免双轨维护）。

## Impact Scope

- **Modules:** `docs-site`（站点导航机制变更），`docs/`（新增 SUMMARY）
- **Files:** mkdocs 配置、构建脚本、文档说明、若干脚本/配置删除
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: 文档即目录（单一导航 SSOT）
**Module:** docs-site

#### Scenario: 维护者只改一份文档即可调整目录
- 给定：维护者修改 `docs/SUMMARY.md` 中的链接顺序/层级
- 期望：
  - MkDocs 侧边栏按 SUMMARY 的顺序展示
  - 不需要再修改 `docs-site/mkdocs.yml` 的 nav 或跑“注入脚本”

### Requirement: 下线独立目录配置与注入脚本
**Module:** scripts / docs-site

#### Scenario: 避免双轨与不一致
- 给定：仓库存在旧的 topics.yml / topics-sync / docs-site-sync 注入逻辑
- 期望：
  - 这些机制被移除或不再参与构建链路
  - 构建入口脚本保持简单可理解

## Risk Assessment

- **Risk:** SUMMARY 作为唯一目录文件，初次整理可能漏掉部分页面入口。  
  **Mitigation:** 目录收敛为“模块索引级入口”（README + Guide + Pitfalls/Self-check），深挖页面通过模块 README/站内搜索进入。
- **Risk:** 引入新 MkDocs 插件会增加依赖。  
  **Mitigation:** 依赖固定版本写入 `docs-site/requirements.txt`，并在 `docs-site/README.md` 明确安装方式与用途。

