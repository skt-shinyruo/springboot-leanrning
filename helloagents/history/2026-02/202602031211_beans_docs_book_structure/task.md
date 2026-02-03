# Task List: spring-core-beans 文档书籍化目录重整

Directory: `helloagents/plan/202602031211_beans_docs_book_structure/`

---

## 1. spring-core-beans docs
- [√] 1.1 新增 `spring-core-modules/spring-core-beans/docs/SUMMARY.md`：按书籍顺序编排 Part/章节目录，verify why.md#core-scenarios
- [√] 1.2 重构 `spring-core-modules/spring-core-beans/docs/README.md` 的目录段落：入口导读保留，详细目录迁移到 SUMMARY，verify why.md#core-scenarios

## 2. docs-site 镜像同步
- [-] 2.1 同步新增 `docs-site/.generated/docs/spring-core-beans/docs/SUMMARY.md`（该目录为生成产物且在 .gitignore 中，建议走生成流程刷新），depends on task 1.1
- [-] 2.2 同步更新 `docs-site/.generated/docs/spring-core-beans/docs/README.md`（同上），depends on task 1.2

## 3. Security Check
- [√] 3.1 扫描变更：确保无敏感信息、无外链注入、无 EHRB 风险

## 4. Documentation Update (Knowledge Base)
- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：入口与目录位置说明
- [√] 4.2 更新 `helloagents/CHANGELOG.md`：记录本次文档结构调整

## 5. Testing
- [√] 5.1 运行目录链接存在性校验脚本：SUMMARY 中每个链接目标必须存在
