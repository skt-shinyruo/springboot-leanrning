# Technical Design: spring-core-beans 文档书籍化目录重整

## Technical Solution
### Core Technologies
- Markdown（结构化目录与导航）
- 本地脚本（生成/校验目录链接）

### Implementation Key Points
- **目录 SSOT**：新增 `docs/SUMMARY.md`，按“书籍阅读顺序”列出 Part 与章节
- **入口职责收敛**：`docs/README.md` 保留导读与路线/运行方式/症状导航，将“全量目录链接列表”迁移到 SUMMARY
- **镜像同步**：对 `docs-site/.generated/docs/spring-core-beans/docs` 下同路径文件做同样变更
- **知识库同步**：更新 `helloagents/wiki/modules/spring-core-beans.md` 指向新的目录文件

## Security and Performance
- **Security:** 仅文档结构调整，不引入外部依赖与敏感信息
- **Performance:** 无运行时影响

## Testing and Deployment
- **Testing:** 执行目录链接存在性校验（SUMMARY 中每个相对路径必须存在）
- **Deployment:** 无需部署；如需站点更新，按现有 docs-site 流程重新 build

