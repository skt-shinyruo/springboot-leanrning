# Technical Design: 文档统一目录（docs/）+ 按主题重组（去模块 docs）

## Technical Solution

### Core Technologies

- Git（以 `git mv` 保留历史的方式迁移文件）
- Python 3 / Shell（用于批量重写链接与引用）
- Markdown（文档载体）
- （可选）MkDocs（如果继续保留 docs-site 站点构建）

### Implementation Key Points

1. **统一文档 SSOT：`docs/`**
   - `docs/` 成为唯一源文档目录；
   - 模块目录下不再保留 `*/docs/**`，避免双写。

2. **按主题重组目录（topic-first）**
   - 将每个模块的 docs 归入一个主题目录（同主题内容集中）；
   - 尽量保留原有章节内部结构（`part-*/appendix/*`），降低相对链接破坏面。

   建议的主题映射（可在实施时微调）：
   - `docs/beans/spring-core-beans/**` → `docs/beans/**`
   - `docs/aop/spring-core-aop/**` → `docs/aop/**`
   - `docs/aop/spring-core-aop-weaving/**` → `docs/aop-weaving/**`
   - `docs/tx/spring-core-tx/**` → `docs/tx/**`
   - `docs/events/spring-core-events/**` → `docs/events/**`
   - `docs/resources/spring-core-resources/**` → `docs/resources/**`
   - `docs/profiles/spring-core-profiles/**` → `docs/profiles/**`
   - `docs/validation/spring-core-validation/**` → `docs/validation/**`
   - `docs/basics/springboot-basics/**` → `docs/boot-basics/**`
   - `docs/web-mvc/springboot-web-mvc/**` → `docs/web-mvc/**`
   - `docs/security/springboot-security/**` → `docs/security/**`
   - `docs/data-jpa/springboot-data-jpa/**` → `docs/data-jpa/**`
   - `docs/cache/springboot-cache/**` → `docs/cache/**`
   - `docs/async-scheduling/springboot-async-scheduling/**` → `docs/async-scheduling/**`
   - `docs/actuator/springboot-actuator/**` → `docs/actuator/**`
   - `docs/web-client/springboot-web-client/**` → `docs/web-client/**`
   - `docs/testing/springboot-testing/**` → `docs/testing/**`
   - `docs/business-case/springboot-business-case/**` → `docs/business-case/**`

3. **Book 迁移到 `docs/book/`**
   - 将 `docs/book/**` 全量迁移到 `docs/book/**`；
   - Book 的 “redirect 页 / 工具页 / 主线章节页”保持在 `docs/book/` 下，避免散落多处。

4. **全仓引用批量重写**
   - 目标：旧路径引用清零（例如 `docs/beans/spring-core-beans/...`、`docs/book/...`）。
   - 策略：
     - 优先用映射表做“路径前缀替换”
     - 对少数复杂相对链接，按文件所在目录重新计算相对路径
     - 对站点绝对链接（如 `/book/...`）保持不动（属于站点路径而非仓库路径）

   - 删除/移除：
     - GitHub Actions 中用于文档“严格校验/阻塞”的步骤（例如 `mkdocs --strict`）

## Architecture Decision ADR

### ADR-001: 以 `docs/` 作为唯一文档事实来源（SSOT）
**Context:** 文档目前分散在 `*/docs/**` 与 `docs/book/**`，重组成本高且容易双写。  
**Decision:** 将全部文档源文件迁移到仓库根目录 `docs/`，模块目录不再保留 docs。  
**Rationale:** 统一入口 + 主题聚合更符合读者检索习惯；避免双写；便于后续继续重排。  
**Alternatives:** 保留模块 docs 作为 SSOT → 拒绝原因：与“统一文件夹 + 去模块 docs”目标冲突。  
**Impact:** 大规模文件移动与引用更新；需要一次性完成并通过全仓引用清理。

### ADR-002: 采用 topic-first 目录（按主题归类）
**Context:** 用户目标是“同类型文档进入同一子文件夹”（例如 beans）。  
**Decision:** 以主题目录承载模块 docs（beans/aop/tx/...），尽量保留原 part/appendix 子结构。  
**Rationale:** 满足“按主题聚合”，同时降低内部链接破坏面。  
**Alternatives:** docs/modules/<module>/... → 拒绝原因：不满足“同主题集中”的阅读诉求。  
**Impact:** 需要维护一张模块→主题的映射表；后续新文档应按主题放置。

## Security and Performance

- **Security:** 仅文件移动与文档内容改写，不涉及生产环境、密钥、PII；删除脚本时注意不误删构建/运行相关脚本。
- **Performance:** N/A（离线文档组织变更）。

## Testing and Deployment

  - 全仓搜索确保旧路径引用清零
  - （可选）`mvn -q test` 确保文档改动未影响构建流程
- **Deployment:** 若保留 docs-site Pages，需将 workflow 从 strict 改为非严格构建或改为手动触发；否则下线该 workflow。

