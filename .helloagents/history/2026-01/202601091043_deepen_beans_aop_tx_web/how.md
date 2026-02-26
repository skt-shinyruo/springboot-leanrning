# Technical Design: 深化 Beans/AOP/Tx/Web 学习体系

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot 3.5.9（多模块 Maven 聚合）
- JUnit 5 + AssertJ（`spring-boot-starter-test`）
- Spring Test（`@SpringBootTest` / `@WebMvcTest` / `ApplicationContextRunner` 等，按模块现有风格选用）
- MockMvc（Web MVC 链路断言）
- Spring Security Test（在 Web MVC + Security 交互场景中使用，避免依赖日志手工观察）

### Implementation Key Points

1. **以测试为 SSOT（Single Source of Truth）**
   - 每个“关键结论/关键分支”必须对应至少一个 LabTest 或 ExerciseTest，可稳定复现并断言。
   - 文档中仅把日志作为辅助观察点；核心结论以断言为准。

2. **按主线逐模块推进（容器 → AOP → 事务 → Web）**
   - 每个模块分三层产出：主线文档（机制）→ 关键分支（边界/陷阱）→ Troubleshooting Checklist（从现象反推机制）。
   - 每个模块的 `docs/part-00-guide/00-deep-dive-guide.md` 统一结构：推荐阅读顺序、推荐测试入口、推荐断点。

3. **每个主题都要“可解释 + 可断点 + 可排障”**
   - 可解释：一条可复述的主线（call-chain / timeline）
   - 可断点：给出 3–5 个推荐断点（类/方法级）
   - 可排障：以“现象 → 定位 → 验证 → 修复”的 checklist 组织

4. **端到端主线作为收尾（可选）**
   - 在 Web 场景中串起 AOP/Tx 的关键边界，用于巩固跨模块定位能力。
   - 不以业务复杂度取胜，以“可控分支 + 可验证证据链”为目标。

## Architecture Decision ADR

### ADR-1: 文档结论以测试为准
**Context:** 教学类仓库中，日志/截图易随环境变化导致结论不稳定，难以复现与回归。  
**Decision:** 所有关键结论必须由可运行测试（Lab/Exercise）断言；文档以测试入口与断点入口为核心导航。  
**Rationale:** 测试可复现、可回归、可自动化；能把“看到的现象”绑定到“可断言入口”。  
**Alternatives:** 仅靠 `spring-boot:run` 日志观察 → 拒绝原因：不稳定且难以回归。  
**Impact:** 需要持续维护测试与文档一致性；但可显著提升学习与排障效率。

## Security and Performance

- **Security:**
  - 不引入任何明文 key/token；不接入生产环境服务。
  - Web/Security 相关 Labs 仅覆盖本地/测试环境可复现的行为，不做危险权限提升演示。
  - 对外请求（如 WebClient）场景优先使用 mock/stub，避免真实网络依赖。
- **Performance:**
  - Labs 尽量使用最小上下文（能用 `@WebMvcTest` 就不使用全量 `@SpringBootTest`）。
  - 避免引入脆弱且耗时的集成测试；必要时拆分为多个轻量对照用例。

## Testing and Deployment

- **Testing:**
  - 单模块：`bash scripts/test-module.sh <module>`
  - 全量：`bash scripts/test-all.sh`
- **Run (optional observation):**
  - 单模块运行：`bash scripts/run-module.sh <module>`（或 `mvn -pl <module> spring-boot:run`）
