# Task List: 文档归并（以模块 */docs 为 SSOT，docs 仅保留总目录）

Directory: `helloagents/plan/202601251755_docs_modularize_nav/`

---

## 1. 目录与站点配置
- [√] 1.1 调整 MkDocs 配置为从仓库根构建文档：更新 `docs-site/mkdocs.yml`（`docs_dir` + `nav_file`），并同步更新 `docs-site/README.md`
- [√] 1.2 生成新的全站目录文件：重建 `docs/SUMMARY.md`（链接指向 `spring-boot-modules/**/docs/**.md` 与 `spring-core-modules/**/docs/**.md`，覆盖全部模块文档）

## 2. 文档迁移与清理（以模块 docs 为 SSOT）
- [√] 2.1 清理 Book：删除 `docs/book/`，并移除 `docs/SUMMARY.md`、`README.md`、`helloagents/wiki/*` 中对 Book 的入口引用
- [√] 2.2 清理根 `docs/`：删除除 `docs/SUMMARY.md` 以外的所有内容（模块副本/主题索引等），确保根 docs 仅保留目录文件
- [√] 2.3 批量修复模块 docs 内的旧链接：清理/替换 `Book TOC`、`book/*.md`、以及指向旧 `docs/<topic>/<module>/...` 的路径，统一回收为可解析的相对链接

## 3. 脚本工具链适配新 SSOT
- [√] 3.1 更新 `scripts/bookify-docs.py`：SSOT 改为 `<module>/docs/README.md`，并确保生成的导航为 `Docs TOC`
- [√] 3.2 更新 `scripts/check-chapter-contract.py`：SSOT 目标扫描改为以 `docs/SUMMARY.md` 为全站 SSOT（按仓库根解析目标路径），移除对 `docs/book` 的强依赖
- [√] 3.3 更新 `scripts/generate-docs-chapter-list.py`、`scripts/upsert-chapter-cards.py`、`scripts/rewrite-docs-book-style.py`、`scripts/rewrite-chapters-booklike-v2.py`：统一按模块 docs 根工作

## 4. 知识库与入口同步
- [√] 4.1 更新根 `README.md`：移除 Book 相关内容，入口改为“全站目录（`docs/SUMMARY.md`）+ 模块 docs”
- [√] 4.2 更新知识库：修正 `helloagents/wiki/*` 中所有旧 `docs/` 路径引用，补充新结构说明；更新 `helloagents/CHANGELOG.md`

## 5. Security Check
- [√] 5.1 执行安全检查：确认无敏感信息引入；确认批量删除前后可回退；确认脚本不进行危险操作（rm -rf 仅作用于 repo 内目标目录）

## 6. 验证与回归
- [√] 6.1 断链检查：运行 `python3 scripts/check-md-relative-links.py --root spring-boot-modules` 与 `python3 scripts/check-md-relative-links.py --root spring-core-modules`
- [√] 6.2 MkDocs 构建验证：在 `docs-site/` 下运行 `python3 -m mkdocs build -f mkdocs.yml`，确认目录可解析、页面可渲染
