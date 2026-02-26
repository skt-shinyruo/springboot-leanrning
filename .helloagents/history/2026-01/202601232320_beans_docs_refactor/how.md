# Technical Design: spring-core-beans 文档全量重构与深度补齐

## Technical Solution
### Core Technologies
- Markdown（文档主载体）
- MkDocs（站点构建）
- Java 17 + JUnit 5（可跑证据链）

### Implementation Key Points
- 先确立新的章节结构与主线时间线，再逐章对齐统一模板契约（输出/分支/验证/断点/排障/自检/入口块）。
- README 与 docs/README 作为“导航入口”，保持一致的阅读路径与推荐入口。
- 机制主线优先，调用链与关键分支矩阵用于解释“为什么如此”与“如何验证”。
- 排障条目与性能/并发专题必须绑定可跑入口与观察点。
- 文档 ↔ 测试入口保持 1:1 或 1:N 显式映射，避免隐式跳转。

## Architecture Design
（不涉及代码架构变化，属于文档结构重排与内容增强）

## Architecture Decision ADR
### ADR-001: 以统一模板契约驱动章节重构
**Context:** 章节已丰富但结构与输出不一致，读者路径与证据链密度不稳定。  
**Decision:** 采用统一模板契约（输出/分支/验证/断点/排障/自检/入口块）作为重写标准，并按主线时间线重排章节。  
**Rationale:** 提高可读性、可验证性与可维护性，降低链接漂移与新章节扩展成本。  
**Alternatives:** 保持现有结构，仅补缺失条目 → 无法解决入口与模板不一致问题。  
**Impact:** 需要系统性重写与链接维护，短期投入较大但长期收益明显。

## Security and Performance
- **Security:** 文档示例不泄露敏感信息；排障与错误页示例避免输出堆栈细节。
- **Performance:** 专题内容以“观察指标 + 可跑入口”呈现，避免误导性结论。

## Testing and Deployment
- **Testing:** `mvn -q -pl :spring-core-beans test`；关键章节对应 `-Dtest=Class#method` 入口回归。
