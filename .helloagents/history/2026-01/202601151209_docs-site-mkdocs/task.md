# Task List：落地文档站点（MkDocs）

Directory: `helloagents/plan/202601151209_docs-site-mkdocs/`

--- 

## 1. 站点骨架（配置与入口）
- [√] 1.1 新增 `docs-site/mkdocs.yml`（Material 主题 + 中文 + 基础导航）
- [√] 1.2 新增 `docs/index.md`（站点首页）
- [√] 1.3 新增 `docs-site/requirements.txt`（MkDocs 依赖）
- [√] 1.4 新增 `docs-site/README.md`（使用说明）

## 2. 同步机制（SSOT → 站点输入目录）
- [√] 2.1 新增 `scripts/docs-site-sync.py`（生成 `docs-site/.generated/docs`）
- [√] 2.2 生成模块总览页 `modules/index.md`（按主线优先排序 + 全量列表）
- [√] 2.3 同步 `helloagents/wiki` 与各模块 `docs/`，并保留 `<module>/docs` ↔ `<module>/src` 相对结构

## 3. 本地命令入口

## 4. 仓库级维护
- [√] 4.1 更新 `.gitignore` 忽略 `docs-site/.generated/` 与 `docs-site/.site/`
- [√] 4.2 更新知识库：`helloagents/wiki/overview.md` 增加文档站点入口
- [√] 4.3 更新变更记录：`helloagents/CHANGELOG.md`

## 5. 验证
- [√] 5.1 运行 `python3 scripts/docs-site-sync.py`，确认生成目录与模块复制成功
