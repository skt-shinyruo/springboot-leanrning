# Technical Design: 移除 docs 强制检查与相关引用

## Technical Solution
### Implementation Key Points
- 删除 `scripts/` 中与文档检查、站点构建/预览、索引生成相关的脚本。
- 删除或裁剪 `.github/workflows/` 中与 docs-site 构建/发布相关的流程。
- 批量清理 `docs/`、`docs-site/`、`helloagents/wiki/`、`helloagents/history/` 以及根 `README.md` 中的相关引用。

## Security and Performance
- **Security:** 仅删除脚本与文本，不引入明文敏感信息
- **Performance:** 无运行时影响

## Testing and Deployment
- **Testing:** 不再提供文档检查脚本
- **Deployment:** 如仍需发布 docs-site，可改为手工执行 MkDocs（不通过脚本封装）
