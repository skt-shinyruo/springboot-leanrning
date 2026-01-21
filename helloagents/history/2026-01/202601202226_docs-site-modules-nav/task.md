# Task List：docs-site 导航扩展（模块文档 + 主线之书）

## 目标

- 将 docs-site 的侧边栏从“仅主线之书（Book）”扩展为“双入口”：模块文档导航 + 主线之书（Book）。

## Tasks

- [√] 在 `docs-site/mkdocs.yml` 增加 AUTO MODULE NAV 标记与模块文档入口（并保留 AUTO BOOK NAV）。
- [√] 扩展 `scripts/docs-site-sync.py`：生成模块文档导航（按主题→模块→章节）并与 Book 导航一起注入到 mkdocs.yml。
- [√] 新增 `docs/topics/index.md` 作为站点可用的主题索引页，避免根目录 `README.md` 与 `index.md` 冲突导致的 nav/link 问题。
- [√] 同步更新站点入口文案与知识库说明（docs/index.md、docs/book/001-start-here.md、docs-site/README.md、helloagents/wiki/overview.md、helloagents/CHANGELOG.md）。
- [√] 验证：`bash scripts/docs-site-build.sh` 构建通过（mkdocs build 成功）。

