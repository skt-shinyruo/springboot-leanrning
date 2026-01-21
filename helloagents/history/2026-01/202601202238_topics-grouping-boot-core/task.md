# Task List：主题分组（Spring Boot vs Spring Core）

## 目标

- 将“模块文档”主题按 **Spring Boot / Spring Core** 两组展示，并统一主题顺序与显示名（README 为 SSOT）。

## Tasks

- [√] 调整 `docs/README.md`：按 Boot/Core 分组重排主题索引（保持脚本可解析的 bullet 结构）。
- [√] 调整 `docs/topics/index.md`：对齐同样的 Boot/Core 分组与入口链接。
- [√] 更新 `scripts/docs-site-sync.py`：在 AUTO MODULE NAV 输出中引入 Boot/Core 两级分组（组 → 主题 → 模块 → 章节）。
- [√] 运行 `python3 scripts/docs-site-sync.py` 更新 `docs-site/mkdocs.yml` 的 AUTO MODULE NAV。
- [√] 验证：`bash scripts/docs-site-build.sh` 构建通过。

