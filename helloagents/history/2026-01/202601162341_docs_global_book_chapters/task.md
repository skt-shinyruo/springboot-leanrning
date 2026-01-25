# Task List: 全站文档升级为“全书统一章节”（章节顺序 + 模块顺序）

Directory: `helloagents/plan/202601162341_docs_global_book_chapters/`

---

## 1. 全书顺序 SSOT（模块顺序 + 章节顺序）
- [√] 1.1 新增“全书模块顺序配置”（例如 `scripts/book-order.yml`），定义 18 模块的推荐学习顺序，并映射到对应 Book-only 主线章节（verify why.md#change-content）
- [√] 1.2 实现“全书章节清单生成器”（新脚本，或复用现有解析逻辑）：按模块顺序读取各模块 `docs/README.md` 的链接顺序，产出全书章节列表（包含 book 主线页 + 模块章节页），并输出一个可调试的清单文件（verify why.md#core-scenarios）
- [√] 1.3 基于全书章节列表生成“旧路径 → 新路径”映射表：为每个章节分配全局唯一 Chapter ID（`001-` 前缀），并给出目标文件名（verify why.md#change-content）

## 2. 批量重命名与链接重写（Chapter ID 固化）
- [√] 2.1 执行批量重命名：将 Chapter ID 前缀固化到 Markdown 文件名（保留原目录结构），并记录变更摘要（verify why.md#change-content）
- [√] 2.2 批量更新模块 `docs/README.md`：将章节链接目标更新为新文件名（SSOT 同步），verify why.md#change-content
- [√] 2.3 批量重写 Markdown 相对链接：按映射表修复正文中的跨章引用（含跨模块引用），verify why.md#risk-assessment

## 3. 站点导航（A：MkDocs nav）
- [√] 3.1 改造 `scripts/docs-site-sync.py`：生成“全书目录（分卷→模块→章节）”并注入到 `docs-site/.generated/mkdocs.yml`（替换仅 Book-only 的行为），verify why.md#change-content
- [√] 3.2 调整 nav 标题策略：确保侧边栏展示稳定短标题，并可选显示“全书章节号”（不强制改写正文 H1），verify why.md#change-content

## 4. 页面内全书导航（章节自成顺序）
- [√] 4.1 新增/扩展批处理脚本：为全书章节 upsert “上一章｜全书目录｜下一章”导航块（使用稳定 marker，幂等），覆盖 book 页面 + 全模块 docs 页面，verify why.md#requirement-全站任何一章都能顺读到底
- [√] 4.2 与现有 BOOKIFY 尾部块协作：避免重复导航、避免把脚本生成块当作“正文可跑入口”误解析，verify why.md#risk-assessment

## 5. 模块顺序的四处一致（B/C/D）
- [√] 5.1 更新 `scripts/docs-site-sync.py` 生成的 `modules/index.md`：按全书模块顺序输出，并给出“推荐顺序（主线）”分组，verify why.md#requirement-模块顺序在四处一致
- [√] 5.2 更新根 `README.md`：模块推荐顺序与全书一致，并补齐“主线章节节点 + 模块目录页”双入口，verify why.md#requirement-模块顺序在四处一致
- [√] 5.3 批量更新各模块 `docs/README.md`：增加“本模块在全书中的位置”说明 + 前后模块跳转 + 全书目录入口，verify why.md#requirement-模块顺序在四处一致

## 6. Security Check
- [√] 6.1 执行安全检查（仅脚本与文档改造）：确认无生产环境操作、无敏感信息写入、无破坏性命令引入（per G9）

## 7. Verification

## 8. Knowledge Base Sync + 归档
- [√] 8.1 更新 `helloagents/CHANGELOG.md`：记录“全书统一章节顺序/模块顺序/全书 nav/全书 prev-next”改造
- [√] 8.2 执行完成后：迁移方案包到 `helloagents/history/YYYY-MM/`
- [√] 8.3 更新 `helloagents/history/index.md`（登记与可追溯）
