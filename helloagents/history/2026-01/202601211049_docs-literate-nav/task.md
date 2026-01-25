# Task List: 文档即目录（Literate Nav）

Directory: `helloagents/plan/202601211049_docs-literate-nav/`

---

## 1. docs/SUMMARY.md（目录即文档）
- [√] 1.1 新增 `docs/SUMMARY.md`，按“封面 → 模块文档（Boot/Core） → Book”组织导航树，verify why.md#core-scenarios

## 2. MkDocs 配置切换为 literate-nav
- [√] 2.1 更新 `docs-site/requirements.txt`：加入 `mkdocs-literate-nav` 依赖并固定版本，verify why.md#change-content
- [√] 2.2 更新 `docs-site/mkdocs.yml`：使用 literate-nav 插件读取 `docs/SUMMARY.md`（nav_file=SUMMARY.md），移除旧 AUTO 注入区段，verify why.md#core-scenarios

## 3. 构建链路简化

## 4. 下线旧的“独立目录配置/注入脚本”

## 5. 文档与知识库同步
- [√] 5.1 更新 `docs-site/README.md`：说明“目录即文档”的维护方式与依赖安装，verify why.md#change-content
- [√] 5.2 更新 `helloagents/wiki/overview.md` 与 `helloagents/CHANGELOG.md`：反映新的 SSOT（SUMMARY.md）与下线旧机制，verify why.md#change-content

## 6. Security Check
- [√] 6.1 安全检查：新增脚本仅做文件读写与校验，不执行危险命令/不写入敏感信息，verify why.md#risk-assessment

## 7. Verification
