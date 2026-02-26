# Change Proposal: docs-site 导航收敛 + 主题 SSOT（YAML）+ /book/ 绝对链接清理

## Requirement Background

当前仓库已经具备：

- `docs/` 作为统一文档源（SSOT）
- `docs-site/` 基于 MkDocs 构建站点
- `scripts/docs-site-sync.py` 自动注入侧边栏导航（Book + 模块文档）

但随着模块章节越来越多，会出现三个明显摩擦：

1) **侧边栏爆炸**：模块文档导航把所有 `part-*/*.md` 都展开，阅读入口变“信息噪音”，也可能影响渲染性能。  
2) **路径不稳定**：大量 Markdown 使用 `/book/...` 绝对链接，在 GitHub Pages 子路径部署时容易跳错。  
3) **主题顺序难维护**：主题顺序/显示名散落在多个文档里，缺少机器可读 SSOT，后续改名/重排容易不一致。

## Change Content

1. 引入 `docs/topics/topics.yml` 作为“主题顺序/显示名/分组”的 SSOT，并自动同步：
   - `docs/README.md`（面向仓库浏览的主题索引）
   - `docs/topics/index.md`（面向站点的主题索引页）
   - MkDocs 模块导航的主题顺序与显示名
2. 收敛模块导航规模：侧边栏只展示“模块索引级入口”（README + Guide + Pitfalls/Self-check），章节细节通过模块 README/索引页与搜索进入。
3. 全仓清理 `/book/` 绝对链接：将其替换为相对链接，保证在 GitHub Pages 子路径部署下仍可正确跳转。

## Impact Scope

- **Modules:** `docs-site`（构建/导航），全量 `docs/**/*.md`（链接修复，非内容重写）
- **Files:** 新增/调整 scripts + docs 入口页 + mkdocs 配置
- **APIs:** 无（仅文档站点与脚本）
- **Data:** 无

## Core Scenarios

### Requirement: 主题 SSOT（YAML）与自动同步
**Module:** docs-site / docs

#### Scenario: 调整主题顺序与显示名（单点维护）
- 给定：维护者修改 `docs/topics/topics.yml`（顺序/显示名/分组）
- 期望：
  - `docs/README.md` 与 `docs/topics/index.md` 自动同步到同一份顺序与显示名
  - `scripts/docs-site-sync.py` 注入的侧边栏“模块文档”主题顺序与显示名同步更新

### Requirement: 模块导航收敛为“索引页驱动”
**Module:** docs-site

#### Scenario: 侧边栏可扫读、可定位
- 给定：模块下存在大量 `part-*/*.md`
- 期望：
  - 侧边栏不展开全部章节
  - 每个模块至少提供：概览（README）、导读（part-00-guide）、常见坑/自检（appendix 90/99）
  - 其它章节仍可通过模块 README/索引页与站内搜索访问

### Requirement: 清理 `/book/` 绝对链接
**Module:** docs

#### Scenario: GitHub Pages 子路径部署可跳转
- 给定：文档中存在形如 `](/book/)`、`](/book/172-web-client-mainline/)` 的绝对链接
- 期望：
  - 全部替换为相对链接（指向 `docs/book/*.md`）

## Risk Assessment

- **Risk:** 批量替换链接可能引入断链/错误指向（尤其是带 hash 的链接或特殊写法）。  
  **Mitigation:** 使用脚本做可重复的变更；替换后运行 `scripts/check-md-relative-links.py` 与 MkDocs build 校验；对异常样本做白名单/手工修复。
- **Risk:** 主题 SSOT 从 Markdown 转为 YAML 后，旧习惯（直接改 `docs/README.md`）可能导致回滚或冲突。  
  **Mitigation:** 在 README/站点说明中明确“YAML 为 SSOT”，并用脚本注入区段避免手改误伤。

