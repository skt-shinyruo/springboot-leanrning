# Task List: spring-core-beans 文档教程化全量补齐

Directory: `helloagents/history/2026-01/202601261138_beans_docs_all_tutorial/`

---

## 1. spring-core-beans 文档修复与补强

- [√] 1.1 重构 `part-03-container-internals/18-refresh-to-bean-creation-mainline.md` 为“教程契约”章节：补齐导读/要点/实验入口/机制主线/排障分流/自检/BOOKIFY，并保持原有主线与分支决策表可读
- [√] 1.2 清理并补齐 beans docs 中空段落/重复标题（排障分流/常见坑与边界/面试常问），统一 Markdown 层级（`##` 区块 + `###` 子项）
- [√] 1.3 为关键章节补齐“面试常问”问答（至少 3 组/章）与“排障分流”定位套路（定义层 vs 实例层）

## 2. Security Check

- [√] 2.1 执行安全自检（G9）：确认无敏感信息写入、无生产环境操作、无危险命令残留

## 3. Testing

- [√] 3.1 运行测试：`mvn -pl :spring-core-beans test`
- [√] 3.2 构建文档站：`python3 -m mkdocs build -f docs-site/mkdocs.yml`

## 4. Documentation (Knowledge Base)

- [√] 4.1 同步更新 `helloagents/wiki/modules/spring-core-beans.md`（补充本次“全章教程化补齐”进展与入口）
- [√] 4.2 更新 `helloagents/CHANGELOG.md`（记录本次文档体系增强）

## 5. Migration

- [√] 5.1 迁移方案包到 `helloagents/history/2026-01/202601261138_beans_docs_all_tutorial/`，并更新 `helloagents/history/index.md`
