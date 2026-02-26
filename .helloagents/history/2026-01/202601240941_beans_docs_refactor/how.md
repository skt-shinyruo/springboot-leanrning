# Technical Design: spring-core-beans 文档分批重构与测试闭环

## Technical Solution
### Core Technologies
- Markdown（文档主载体）
- MkDocs（站点构建）
- Java 17 + JUnit 5（可跑证据链）

### Implementation Key Points
- 以“Part 为批次”进行文档重构：先统一章节结构与模板，再逐章补齐主线/分支/断点/排障/性能并发。
- README 与 docs/README 作为唯一入口，保持阅读路径与索引一致。
- 每章绑定对应 Lab/Exercise 测试入口，做到文档→测试→断点闭环。

## Architecture Design
不涉及代码架构变化，属于文档结构重排与内容增强。

## Architecture Decision ADR
### ADR-001: 采用分批重构与统一模板契约
**Context:** 一次性全量重排易导致链接漂移与验证缺失。
**Decision:** 按 Part 分批次重写，每批次使用统一模板契约与测试闭环。
**Rationale:** 降低风险、便于逐章验证与回滚。
**Alternatives:** 一次性全量重构 → 风险高、验证成本高。
**Impact:** 交付节奏更稳，但需要多轮自检与回归。

## Security and Performance
- **Security:** 不引入外部依赖，不涉及敏感信息与生产环境操作。
- **Performance:** 分批执行文档与测试，避免一次性回归开销过高。

## Testing and Deployment
- **Testing:** 按章节对应 Lab/Exercise 运行；新增/补齐测试后执行关键回归。
- **Deployment:** 文档调整无需发布流程变更。
