# Task List: 文档统一目录（docs/）+ 按主题重组（去模块 docs）

Directory: `helloagents/plan/202601171641_docs_unify_by_topic/`

---

## 1. 统一目录与迁移执行
- [√] 1.1 创建统一目录骨架：新增 `docs/` 与主题子目录（beans/aop/tx/... + book），verify why.md#requirement-文档统一入口与可发现性-scenario-按主题集中查找
- [√] 1.2 迁移模块文档：将 18 个模块 `*/docs/**` 使用 `git mv` 迁移到 `docs/<topic>/**`，并删除模块下原 `docs/`，verify why.md#requirement-单一源文档（移除模块-docs）-scenario-去重与避免双写
- [√] 1.3 迁移 Book 文档：将 `docs-site/content/book/**` 使用 `git mv` 迁移到 `docs/book/**`，verify why.md#requirement-文档统一入口与可发现性-scenario-book-与主题文档共存

## 2. 全仓引用与导航更新
- [√] 2.1 更新全仓 Markdown 链接：将旧路径引用（`*/docs/`、`docs-site/content/book/`）批量替换为 `docs/` 新路径，verify why.md#requirement-单一源文档（移除模块-docs）-scenario-去重与避免双写
- [√] 2.2 更新入口文档：更新根 `README.md`、各模块 `README.md`、`docs-site/README.md` 中的文档链接与说明，verify why.md#requirement-文档统一入口与可发现性-scenario-按主题集中查找
- [√] 2.3 更新 docs-site（如保留）：将 docs-site 的输入源从旧 SSOT 切换为 `docs/`（更新 `scripts/docs-site-sync.py` / mkdocs 配置或直接下线 workflow），verify why.md#requirement-文档统一入口与可发现性-scenario-book-与主题文档共存

## 4. Security Check
- [√] 4.1 执行安全检查：确认无生产环境操作、无敏感信息写入、脚本删除不影响构建/测试的基础流程（per G9）

- [√] 5.1 全仓搜索：确保旧路径引用清零（例如 `spring-core-*/docs/`、`springboot-*/docs/`、`docs-site/content/book/`）

## 6. Knowledge Base Sync + 归档
- [√] 6.3 执行完成后：迁移方案包到 `helloagents/history/YYYY-MM/`
- [√] 6.4 更新 `helloagents/history/index.md`（登记与可追溯）
