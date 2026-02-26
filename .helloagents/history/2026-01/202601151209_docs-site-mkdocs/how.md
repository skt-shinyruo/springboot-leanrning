# How：实现方案（MkDocs 聚合站）

## 方案概览

采用 `MkDocs + Material for MkDocs` 作为静态站点生成器，提供：

- 中文 UI + 全文搜索
- 侧边栏主题导航（以“主线模块 + 模块总览”为核心）
- 最小依赖与可维护的配置

## SSOT 与同步策略

- **SSOT**：各模块自身的 `<module>/docs/**` 与 `helloagents/wiki/**`
- **站点输入目录（generated）**：`docs-site/.generated/docs`
- 通过同步脚本 `scripts/docs-site-sync.py` 将 SSOT 复制到生成目录
  - 保留原始目录结构：`<module>/docs` 与 `<module>/src` 的相对关系不变
  - 解决 `docs` 内指向 `../../src/...` 的源码链接在站点内的可访问性

## 构建/预览方式

## 风险与规避

- 风险：站点输入目录与 build 输出被误提交
  - 规避：在 `.gitignore` 中忽略 `docs-site/.generated/` 与 `docs-site/.site/`
- 风险：环境缺少 MkDocs 依赖
  - 规避：提供 `docs-site/requirements.txt` 并在 `docs-site/README.md` 中明确安装与运行方式

