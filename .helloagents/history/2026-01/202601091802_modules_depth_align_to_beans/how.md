# Technical Design: 全模块深挖对齐（对标 spring-core-beans）

## Technical Solution

### Core Technologies

- Java 17 / Maven 多模块
- Spring Boot 3.5.x（父 `pom.xml` 管理）
- JUnit 5 + AssertJ + Spring Boot Test
- docs 约定：`AG-CONTRACT` + `BOOKIFY`（章节尾部统一 “对应 Lab/Test + 上一章｜目录｜下一章”）

### Implementation Key Points

#### 1) Guide：从“骨架”升级为“深挖导航图”

对每个模块的 `docs/part-00-guide/00-deep-dive-guide.md` 统一补齐（对标 `spring-core-beans` 的写法）：

- 机制主线：时间线（发生顺序）、关键参与者（关键类/接口）、关键分支（典型现象与对应分支）
- 可跑入口：默认 Lab/Test（类名 + 关键方法），推荐命令（`mvn -pl <module> test`）
- 断点清单：入口方法 → 关键分支 → 数据结构/状态变化（建议加条件断点降噪）
- 与其它模块的联动：例如“代理是共同底座”（AOP/Tx/Method Validation/@Async）

#### 2) Pitfall：每章至少 1 个“可断言”的坑点/边界

统一采用可操作的坑点结构（写进每个章节的 F 区块）：

- Symptom（现象）：你看到了什么（状态码/异常/行为差异）
- Root Cause（根因）：对应哪个机制分支
- Verification（如何验证）：默认 Lab/Test 入口 + 关键断言点
- Breakpoints（推荐断点）：最快命中关键分支的位置
- Fix（修复建议）：学习用最小修复 + 工程推荐做法

约束：Verification 必须绑定默认 `*LabTest`；若现有 Labs 无法覆盖该坑点，则新增/扩展默认 Lab。

#### 3) Key Branch：每模块 2–5 个关键分支，默认 Lab 覆盖

关键分支提炼输入：

- 模块 docs/README 主线章节
- 现有占位内容（坑点待补齐/骨架兜底）
- 现有 Labs 覆盖空洞（缺少可断言分支）
- 真实工程常见误判点（尤其是“同现象不同根因”的分流）

关键分支优先级规则：

1. 最容易误判且成本高的分支（排障价值最高）
2. 能稳定断言的分支（适合默认回归）
3. 与其它模块强耦合的分支（形成知识网络）

#### 4) 默认 Lab 的稳定性策略（避免 flaky）

- Web 客户端：使用 MockWebServer（无真实网络）
- Async：使用线程名 + CountDownLatch + 明确超时（避免 sleep）
- Cache：使用手动 Ticker（过期可控、断言稳定）
- JPA：用 `flush()` + `clear()` 避免“一级缓存假象”；必要时用 SQL 计数/日志作为辅助（断言以行为为准）
- Security/MVC：优先用 MockMvc 固定 `resolvedException`/status/header（避免只看日志）

- 每批修改后跑：
  - `mvn -pl <module> test`（模块级）
  - 最终 `mvn test`（全仓库）
- 同步更新：
  - `helloagents/wiki/modules/<module>.md`（Last Updated / Docs & Labs / Change History）
  - `helloagents/CHANGELOG.md`（记录本次“全模块深挖对齐”）

## Architecture Decision ADR

### ADR-001: Docs as Teaching Narrative, Tests as Runtime SSOT

**Context:**  
当前多模块已具备 docs + tests 骨架，但部分结论缺少“可复现证据链”绑定，导致深挖体验不稳定。

**Decision:**  
采用“文档叙事 + 测试断言事实来源”的双 SSOT 约束：

- docs 用于承载“机制主线/排障分流/断点入口/学习路线”
- tests 用于承载“可复现的行为事实”（默认回归必须全绿）
- 每章至少 1 个 pitfall 必须绑定默认 Lab（可跑可断言）

**Rationale:**  
把深入程度从“写得像”变成“跑得出来”，并可持续回归。

**Alternatives:**  
- 仅补文档，不补 tests → 拒绝原因：结论不可验证，容易漂移。  
- 仅补 tests，不补文档 → 拒绝原因：读者难以把实验上升为可复述机制。  
- 全面重构章节结构 → 拒绝原因：风险高、断链成本大，不利于持续迭代。

**Impact:**  
需要增加一定数量的默认 Lab 用例与文档补齐工作；但可显著提升模块间一致性与可复现性。

## Security and Performance

- **Security:**
  - 不接入任何生产环境；不写入/输出真实密钥与敏感信息
  - 示例数据仅使用虚拟值（Alice/Bob、示例邮箱等）
  - 避免引入高风险依赖与不必要权限修改
- **Performance:**
  - 优先选择 slice / ApplicationContextRunner 等轻量测试形态
  - 异步测试严格控制超时与线程资源
  - 避免引入慢测试与不稳定外部依赖

## Testing and Deployment

- **Testing:**
  - 模块级：`mvn -pl <module> test`
  - 全仓库：`mvn test`
- **Deployment:** 无（学习仓库，非生产交付）

