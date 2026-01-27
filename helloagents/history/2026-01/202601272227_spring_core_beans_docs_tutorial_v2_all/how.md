# Technical Design: spring-core-beans docs 全量“教程化 v2”（源码进阶 + 面试标准答案）

## Technical Solution

### Core Technologies
- Markdown（现有文档体系）
- Maven + JUnit（用于提供可运行证据链入口，回归验证不破坏现有 Labs）

### Implementation Key Points

1. **双轨同源（推荐）**
   - 源码进阶轨：章节内提供“方法级最短调用链 + 断点闭环 + watch list + 结论”
   - 面试输出轨：章节内提供“3 分钟复述要点”，标准答案以 Appendix/93 为主 SSOT，章节做映射与补充
   - 排障轨：章节内提供“本章特化决策表”，全局排障清单以 Appendix/94 为主 SSOT

2. **覆盖审计（确保 70 篇都被处理）**
   - 以 `docs/**/*.md` 的全量清单为范围基线
   - 在执行阶段按 Part 分批修改，并在 task.md 中显式标记“已处理/待处理”

3. **章节契约（10/30/3）落地方式**
   - 10 分钟：导读区块明确“跑哪个 Lab/Test + 预期现象”
   - 30 分钟：给出 3–5 个断点与 5–8 个 watch list
   - 3 分钟：给出可复述标准答案（或明确映射到 Appendix/93 的具体小节）

4. **一致性治理**
   - `docs/README.md` 作为目录 SSOT，增加“源码进阶路线 / 面试冲刺路线”
   - 统一跨文档互链：面试题库（93）/ 排障清单（94）/ Debugger Pack（98）

## Security and Performance
- **Security:** 仅文档改动，不引入密钥/生产地址/第三方依赖
- **Performance:** 文档体量增加但受控；以索引/表格/折叠结构降低阅读负担

## Testing and Verification
- 分批完成后至少跑 1 个最小回归（推荐：与当批内容相关的 `*LabTest`）
- 全量完成后跑模块回归：`mvn -pl :spring-core-beans test`

