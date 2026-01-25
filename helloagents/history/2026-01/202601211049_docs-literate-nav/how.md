# Technical Design: 文档即目录（Literate Nav）

## Technical Solution

### Core Technologies

- MkDocs
- mkdocs-literate-nav（将 Markdown 目录作为 nav 的解析插件）

### Implementation Key Points

1) **导航 SSOT：docs/SUMMARY.md**

- 新增 `docs/SUMMARY.md`，用 Markdown list 描述完整导航树：
  - 站点封面（`docs/index.md`）
  - 模块文档（按 Spring Boot / Spring Core 分组；每个模块收敛到 README + part-00-guide + appendix 90/99）
  - 主线之书（Book）入口与章节顺读（保持现有章节文件名与顺序）

2) **MkDocs 配置改为“nav include SUMMARY”**

- `docs-site/mkdocs.yml`：
  - 增加 `literate-nav` 插件
  - `nav` 使用 `!include SUMMARY.md`（从 docs_dir 下读取 SUMMARY）
  - 删除原先由脚本注入的大段 nav 内容与 AUTO 标记

3) **构建链路简化**

  - 不再运行 nav 注入脚本
  - 直接执行 `mkdocs build/serve`

4) **旧机制下线**

- 删除/移除：
  - `docs/topics/topics.yml`
  - `scripts/docs-topics-sync.py`
  - `scripts/docs-site-sync.py`（或至少从构建链路中移除）
  - 移除对上述脚本的依赖

## Security and Performance

- **Security:** 仅调整文档站点与本地脚本；不接触外部服务；不写入任何敏感信息。
- **Performance:** 侧边栏目录规模收敛；MkDocs 构建 nav 规模降低。

## Testing and Deployment

