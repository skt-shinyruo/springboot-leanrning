# Task List: docs-site 导航收敛 + 主题 SSOT（YAML）+ /book/ 绝对链接清理

Directory: `helloagents/plan/202601202318_docs_site_nav_ssot_links/`

---

## 1. 主题 SSOT（YAML）与自动同步
- [√] 1.1 新增主题 SSOT：`docs/topics/topics.yml`（Boot/Core 分组 + topic id/label），verify why.md#core-scenarios
- [√] 1.2 在 `docs/README.md` 与 `docs/topics/index.md` 增加 AUTO 注入区段标记（BEGIN/END），verify why.md#core-scenarios
- [√] 1.3 新增同步脚本：`scripts/docs-topics-sync.py`（读取 YAML → 注入两个 Markdown），verify why.md#core-scenarios

## 2. 模块文档导航收敛（索引页驱动）
- [√] 2.1 改造 `scripts/docs-site-sync.py`：主题来源改为 YAML；AUTO MODULE NAV 输出为“组 → 主题 → 模块 →（概览/导读/附录）”，verify why.md#core-scenarios
- [√] 2.2 收敛规则落地：仅展开 `part-00-guide` 与 `appendix/*-90-*.md`/`appendix/*-99-*.md`，其余章节由 README/搜索访问，verify why.md#core-scenarios
- [√] 2.3 运行 `python3 scripts/docs-site-sync.py` 生成导航并检查 `docs-site/mkdocs.yml`，verify why.md#core-scenarios

## 3. 清理 `/book/` 绝对链接（GitHub Pages 子路径兼容）
- [√] 3.1 新增一次性修复脚本 `scripts/fix-abs-book-links.py`（按文件深度计算相对路径，保留 anchor），verify why.md#core-scenarios
- [√] 3.2 执行修复脚本，批量替换 `docs/**/*.md` 中的 `/book/` 绝对链接，verify why.md#core-scenarios

## 4. Security Check
- [√] 4.1 安全检查（脚本不执行外部命令/不写入敏感信息；批量替换提供 dry-run/统计输出），verify why.md#risk-assessment

## 5. Documentation Update
- [√] 5.2 更新 `helloagents/wiki/overview.md`：补充 docs-site 导航与 SSOT 说明，verify why.md#change-content
- [√] 5.3 更新 `helloagents/CHANGELOG.md`：记录本次结构调整，verify why.md#change-content

## 6. Verification
