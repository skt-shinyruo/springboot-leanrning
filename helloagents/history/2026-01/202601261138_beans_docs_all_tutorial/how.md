# Technical Design: spring-core-beans 文档教程化全量补齐

## Technical Solution

### Core Technologies

- Markdown 文档（MkDocs 渲染）
- Docs 站点导航：`docs-site/mkdocs.yml`（`docs_dir: ..`）+ `docs/SUMMARY.md`（literate-nav）
- 可运行验证入口：`spring-core-modules/spring-core-beans/src/test/java/.../*LabTest.java`

### Implementation Key Points

1. **章节结构统一（教程契约）**
   - 为缺失或不完整的章节补齐：导读/要点/实验/机制主线/排障分流/常见坑/面试常问/自检/BOOKIFY。
   - 避免“只有标题没有内容”的空章节，避免重复标题。
2. **主线章书本化**
   - 保留 `refresh()` → `doCreateBean()` 主线叙事与分支决策表；
   - 增加统一导读、推荐 Lab、排障分流、自检与 BOOKIFY 导航，融入整本书的上下章阅读。
3. **Markdown 层级修复**
   - 将“常见坑/面试/排障”等大段落收敛为一个 `##` 区块；
   - 其子项使用 `###` 级标题或列表，避免出现“上级标题为空”的结构缺陷。
4. **链接稳定性**
   - 不改动 `docs/SUMMARY.md` 的 SSOT 地位；
   - 如需修改章节标题或文件内容，保持文件路径不变，避免引入全站断链。

## Security and Performance

- **Security:** 不引入密钥/令牌/外部服务连接；不执行生产环境操作；文档仅引用仓库内路径与公开类名。
- **Performance:** 文档改动不影响运行时性能；测试回归保证行为不变。

## Testing and Deployment

- **Testing:** 运行 `mvn -pl :spring-core-beans test`（确保所有 LabTest 可回归）。
- **Docs Build:** 运行 `python3 -m mkdocs build -f docs-site/mkdocs.yml`（确保 SUMMARY 链接与站点构建正常）。

