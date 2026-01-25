# 文档站点（MkDocs）

本目录用于把“模块内文档（`*/docs/`）+ 全站目录（`docs/SUMMARY.md`）”构建成一个可搜索的静态站点（MkDocs Material）。

## 1. 关键约定（SSOT）

- **单一源文档（SSOT）**：模块文档以各模块目录内的 `*/docs/` 为事实来源；仓库根 `docs/` 仅保留 `docs/SUMMARY.md` 作为全站导航目录。
- **文档即目录（SSOT）**：站点导航/侧边栏目录以 `docs/SUMMARY.md` 为唯一事实来源（按该文件的顺序与层级展示）。
  - 目录维护：只需要修改 `docs/SUMMARY.md`
  - 站点配置：`docs-site/mkdocs.yml` 不再维护 `nav:` 大块 YAML

实现方式：使用 `mkdocs-literate-nav` 插件读取 `SUMMARY.md`。

## 2. 本地使用

### 2.1 安装依赖（MkDocs + 插件）

依赖列表见：`docs-site/requirements.txt`。

#### 方式 A（推荐）：使用 venv

> `docs-site/.venv/` 已加入 `.gitignore`，不会被提交。

```bash
python3 -m venv docs-site/.venv
docs-site/.venv/bin/python -m pip install -r docs-site/requirements.txt
```

> ⚠️ 如果你的系统缺少 venv 支持（例如 Debian/Ubuntu 报 `python3-venv` 缺失），请先安装对应包后再创建 venv。

#### 方式 B：安装到用户目录（不推荐，但更“省事”）

> ⚠️ 仅建议在你理解 PEP 668/系统 Python 约束的前提下使用。

```bash
python3 -m pip install --user --break-system-packages -r docs-site/requirements.txt
```

### 2.2 启动预览

```bash
cd docs-site
mkdocs serve -f mkdocs.yml
```

### 2.3 构建

```bash
cd docs-site
mkdocs build -f mkdocs.yml
```

## 3. 目录维护方式（文档即目录）

站点目录文件：`docs/SUMMARY.md`

维护规则（建议）：

- 目录只放“索引级入口”（模块 README + Guide + Pitfalls/Self-check），深挖页通过站内搜索与文内链接进入
- 链接一律使用相对 `docs/` 根目录的路径（例如 `book/062-webmvc-mainline.md`）
- 目录层级使用 Markdown 列表缩进表达

## 4. GitHub Pages（自动构建 + 发布）

本仓库已提供 GitHub Actions workflow，可在 GitHub 上自动构建并发布站点到 GitHub Pages：

- Workflow 文件：不再提供（如需发布请自行配置）
- 触发条件：
  - `push` 到 `main/master`：构建 + 发布
  - `workflow_dispatch`：手动触发
