# Technical Design: 全站文档升级为“全书统一章节”（章节顺序 + 模块顺序）

## Technical Solution

### Core Technologies

- Python 脚本：用于解析目录 SSOT、生成全书顺序、批量 upsert 导航块（保持幂等）。
- MkDocs（Material）：以生成的 `docs-site/.generated/mkdocs.yml` 为入口，统一侧边栏导航与站点构建。

### Implementation Key Points

1) **全书顺序的 SSOT 设计**

- 模块顺序：新增一个“全书模块顺序配置”（建议放在 `scripts/` 下，便于被生成脚本读取），作为“最合理学习顺序”的唯一来源。
- 全书章节顺序：由“模块顺序 + 模块内章节顺序 + Book-only 主线章节（作为每个模块的主线节点）”拼接生成。

2) **MkDocs nav 生成策略（A：站点侧边栏）**

改造 `scripts/docs-site-sync.py` 的 nav 注入逻辑（`# BEGIN AUTO BOOK NAV` 区块）：

- 现状：只生成 Book-only（0–18 章 + 附录），模块 docs 只同步到站点输入目录但不进 nav。
- 目标：生成“全书目录”（分卷 → 模块 → 章节），使每个 `.md` 都出现在 nav 里，且顺序与全书 SSOT 一致。
- 标题策略：nav title 使用“短标题 + 全书章节号（可选）”，正文标题保持原来的 H1 叙事标题，不强制改写标题文本。

3) **章节重命名与链接重写（核心风险控制）**

- 先生成“旧路径 → 新路径”的映射表（包含所有受影响的 Markdown 文件）。
- 执行批量重命名（将 Chapter ID 固化到文件名；保留目录结构不变，避免把“路径变更”升级为“目录结构重构”）。
- 批量重写 Markdown 相对链接：
  - 覆盖 `docs/README.md`（模块 SSOT）中的链接目标；
  - 覆盖章节正文中的跨章引用（相对链接），以映射表为准；
  - 对外部链接与站内绝对链接保持不动（避免误伤）。

4) **章节自成顺序（全书 Prev/Next）**

新增一个批处理脚本（或在现有 `scripts/bookify-docs.py` 基础上扩展）：

- 输入：全书顺序列表（包含 book 主线页 + 模块章节页）。
- 输出：对每个章节 upsert 一个稳定的“全书导航块”（上一章/全书目录/下一章），用固定 marker 包裹，保证幂等。
- 放置位置：建议放在文末（与现有 BOOKIFY 尾部块共存或合并），避免破坏正文结构。

5) **模块顺序的四处一致（B/C/D）**

- B（`modules/index.md`）：由 `scripts/docs-site-sync.py` 生成时，按“全书模块顺序配置”输出，并按卷/学习阶段分组。
- C（根 `README.md`）：更新“推荐学习路线”部分，使模块列表顺序与全书一致，并链接到对应的“主线章节节点 + 模块目录页”。
- D（各模块 `docs/README.md`）：在目录页顶部加入一段“本模块在全书中的位置”说明，并提供：
  - 对应的 Book-only 主线章节入口（例如 Beans ↔ `book/02-ioc-container-mainline.md`）
  - 上一模块/下一模块跳转
  - 全书目录入口（Global TOC）

## Architecture Decision ADR

### ADR-001: 采用“全书统一章节号（Chapter ID）”，并将章节号固化到文件名与导航中

**Context:**  
你希望“一个 doc 就是一章”，不仅在站点导航里有顺序，也希望在仓库层面（文件名/链接）就能看见并自然排序。当前章节分散在 18 个模块目录与 `docs/book/` 下，且大量相对链接依赖现有路径，直接手工改会导致断链与顺序漂移。

**Decision:**  

**Rationale:**  
- 章节顺序不仅体现在 nav，也体现在文件系统与 IDE 浏览体验（按文件名自然排序就是阅读顺序）；
- 通过映射表与脚本重写链接，将“断链风险”从人工不可控变为可回归可验证；
- Chapter ID 固化后，后续新增章节只需插入到清单并重新运行脚本即可（保持幂等与一致性）。

**Alternatives:**  
- 方案：只生成 nav 与页面内 Prev/Next，不改文件名 → 拒绝原因：仓库层面无法自然排序，“一个 doc 就是一章”的直观性不足。
- 方案：把所有模块 docs 搬迁到统一目录（例如 `docs/chapters/`） → 拒绝原因：会推翻现有模块边界与自检脚本假设，影响更大。

**Impact:**  
- 需要同步更新各模块 `docs/README.md` 的链接目标（SSOT 本身也会变更）。

## Security and Performance

- **Security:** 仅涉及文档与脚本生成，不涉及生产环境、权限提升、密钥处理；避免在脚本中执行任何破坏性命令。
- **Performance:** nav 生成与批量 upsert 会扫描 Markdown 文件；通过增量/幂等策略与必要的缓存（可选）控制耗时。

## Testing and Deployment

- **Testing:**
- **Deployment:** 复用现有 GitHub Pages workflow（若启用），确保生成的站点在 CI 中仍可构建通过。
