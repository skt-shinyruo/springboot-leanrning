# Technical Design: docs-site 导航收敛 + 主题 SSOT（YAML）+ /book/ 绝对链接清理

## Technical Solution

### Core Technologies

- Python 3（现有 scripts 体系）
- MkDocs（docs-site）
- YAML（主题顺序/显示名 SSOT 文件）

### Implementation Key Points

1) **主题 SSOT（YAML）**
- 新增 `docs/topics/topics.yml`，定义：
  - 分组（Spring Boot / Spring Core）
  - 每个主题的 `id`（目录名）与 `label`（显示名）
  - 可选：Book（保持为独立入口）
- 新增 `scripts/docs-topics-sync.py`：
  - 读取 `docs/topics/topics.yml`
  - 将主题索引注入到：
    - `docs/README.md` 的 AUTO 区段
    - `docs/topics/index.md` 的 AUTO 区段
  - 作为 SSOT 同步入口，避免手工双写

2) **模块导航收敛（避免侧边栏爆炸）**
- 修改 `scripts/docs-site-sync.py` 的模块导航生成规则：
  - 侧边栏展示为：组 → 主题 → 模块 →（概览 + 导读 + 附录）
  - 模块层面：
    - 必选：`README.md`（概览）
    - 导读：仅展开 `part-00-guide/*.md`
    - 附录：仅展开 `appendix/*-90-*.md` 与 `appendix/*-99-*.md`（其余 appendix 通过 README/搜索进入）
  - 章节细节（Part 01+）不在侧边栏展开，避免 1k+ 条目污染导航

3) **清理 `/book/` 绝对链接**
- 新增 `scripts/fix-abs-book-links.py`（一次性执行）：
  - 扫描 `docs/**/*.md`
  - 将 `](/book/...)` 形式的链接改为相对路径：
    - `/book/` → `../../..../book/index.md`（按文件深度自动计算）
    - `/book/<slug>/` → `../../..../book/<slug>.md`
    - 保留 `#anchor`（如存在）
  - 扫描 `docs/**/*.md`，发现 `/book/` 绝对链接即失败

4) **构建/预览脚本顺序**
  - 先执行 `python3 scripts/docs-topics-sync.py`
  - 再执行 `python3 scripts/docs-site-sync.py`
  - 最后执行 mkdocs build/serve

## Architecture Decision ADR

### ADR-001: 主题顺序与显示名以 YAML 为 SSOT
**Context:** 主题顺序需要同时驱动 GitHub README、站点主题索引页、侧边栏注入脚本。  
**Decision:** 引入 `docs/topics/topics.yml` 作为唯一 SSOT，并提供同步脚本注入到 Markdown。  
**Rationale:** 单点维护 + 可机器校验 + 可扩展（以后加字段不破坏 Markdown 结构）。  
**Alternatives:** 继续以 `docs/README.md` 解析为 SSOT → 拒绝原因：Markdown 易变、解析脆弱、难表达分组。  
**Impact:** 维护者需要遵循“改 YAML→跑 sync”的工作流；脚本成为构建链路的一部分。

## Security and Performance

- **Security:** 仅处理仓库内 Markdown 文本，不引入外部网络访问；避免在脚本中执行不受控 shell；不写入敏感信息。

## Testing and Deployment

- **Testing:**
- **Deployment:** GitHub Pages workflow 保持不变；绝对链接修复后在子路径部署下跳转正确。

