# Task List: tutorials_style_deepen_all（结构对齐 + 模板统一 + 全模块深挖 + 主题扩展）

Directory: `helloagents/history/2026-01/202601221758_tutorials_style_deepen_all/`

> - 先稳住结构与入口（避免边改边塌）  
> - 再滚动深挖与扩展主题（可持续推进）

---

## 0. 本轮 DoD（验收标准）

- [√] 0.0.1 `mvn -q test` 全绿，verify why.md#requirement-r1-tutorials-style-structure-and-naming-s1-stable-structure-and-entrypoints
- [√] 0.0.4 每个模块具备统一入口（README → docs → test），verify why.md#requirement-r2-module-template-contract-s1-module-readme-and-docs-contract

---

## 1. 基线回归（开工前）

- [√] 1.1 运行 `mvn -q test` 并记录基线（失败先修到绿），verify why.md#requirement-r1-tutorials-style-structure-and-naming-s1-stable-structure-and-entrypoints

---

## 2. Solution 2：更强对齐 tutorials 风格（结构与命名）

- [-] 2.1 补充/固化模块命名与目录约定（写入 SSOT：`helloagents/project.md`），verify why.md#requirement-r1-tutorials-style-structure-and-naming-s1-stable-structure-and-entrypoints
- [-] 2.2 新增迁移映射表（旧路径 → 新路径 → 入口命令），放入 `docs/` 或 `helloagents/wiki/`，verify why.md#requirement-r1-tutorials-style-structure-and-naming-s1-stable-structure-and-entrypoints
  > Note: Deferred（后续需要结合实际迁移/重命名批次生成映射表）。
- [-] 2.3 如执行目录重命名：逐模块迁移 `spring-boot-modules/*`（每次只迁移 1 个模块），verify why.md#requirement-r1-tutorials-style-structure-and-naming-s1-stable-structure-and-entrypoints
  > Note: Deferred（本轮未做模块目录移动/重命名）。
- [-] 2.4 如执行目录重命名：逐模块迁移 `spring-core-modules/*`（每次只迁移 1 个模块），verify why.md#requirement-r1-tutorials-style-structure-and-naming-s1-stable-structure-and-entrypoints
  > Note: Deferred（本轮未做模块目录移动/重命名）。
  > Note: Deferred（后续与 2.3/2.4 绑定执行）。

---

## 3. Solution 1：模板统一 + 全模块深挖（主线优先）

### 3.1 模块契约（Module Contract）落地

- [-] 3.1.1 在 `helloagents/project.md` 明确“模块契约”（README/docs/tests 的最小要求），verify why.md#requirement-r2-module-template-contract-s1-module-readme-and-docs-contract
- [√] 3.1.2 为每个模块 README 套用统一结构（只做索引/导航），第一批：Beans/AOP/Tx/Web MVC，verify why.md#requirement-r2-module-template-contract-s1-module-readme-and-docs-contract
- [√] 3.1.3 为每个模块 docs/README 套用统一结构（part-00-guide + 深挖顺序 + 入口绑定），第一批：Beans/AOP/Tx/Web MVC，verify why.md#requirement-r2-module-template-contract-s1-module-readme-and-docs-contract

### 3.2 深挖资产范式：Call Chain / Breakpoint Map / Branch Matrix / Evidence Chain

- [√] 3.2.1 Web MVC：补齐/强化调用链与关键分支矩阵（含 400/415/406/异常链路），并绑定到 Lab 入口，verify why.md#requirement-r3-deepen-mechanics-and-evidence-chain-s1-call-chain-branch-matrix-labs
- [√] 3.2.2 AOP：新增“代理主线调用链”与“自调用/代理选择/边界限制”矩阵 Lab，verify why.md#requirement-r3-deepen-mechanics-and-evidence-chain-s1-call-chain-branch-matrix-labs
- [√] 3.2.3 Tx：新增“事务拦截器主线调用链”与“传播/回滚规则”矩阵 Lab，verify why.md#requirement-r3-deepen-mechanics-and-evidence-chain-s1-call-chain-branch-matrix-labs
- [√] 3.2.4 Beans：把既有深挖资产对齐统一范式（call-chain/矩阵/入口索引/断点包），verify why.md#requirement-r3-deepen-mechanics-and-evidence-chain-s1-call-chain-branch-matrix-labs
- [√] 3.2.5 第二批：Events/Async/Cache/Validation/Profiles/Resources/Security/Actuator/Web Client/Testing/Data JPA/Business Case（按模块滚动补齐），verify why.md#requirement-r3-deepen-mechanics-and-evidence-chain-s1-call-chain-branch-matrix-labs

### 3.3 性能与并发专题（可复现 + 不 flaky）

- [-] 3.3.1 Web MVC：补齐 async/timeout/线程切换的可复现案例与观察点，verify why.md#requirement-r4-performance-and-concurrency-deepen-s1-reproducible-perf-and-concurrency-cases
  > Note: Deferred（后续按“可复现 + 不 flaky”的标准单独收敛为性能/并发专题包）。
- [-] 3.3.2 Async/Scheduling 或 Events：补齐“异步边界/事务 after-commit/线程池隔离”的可复现案例，verify why.md#requirement-r4-performance-and-concurrency-deepen-s1-reproducible-perf-and-concurrency-cases
  > Note: Deferred（同 3.3.1）。

---

> 先新增少量高价值主题作为样板，再规模化复制，避免一口气铺太多导致质量不可控。

- [√] 4.1 新增模块 `springboot-autoconfiguration`（条件装配/顺序/回退时机），含 Lab/Exercise/docs 骨架，verify why.md#requirement-r5-expand-topics-with-scaffold-and-gates-s1-new-module-ready-from-day1
- [√] 4.2 新增模块 `springboot-observability`（metrics/tracing/日志关联），含 Lab/Exercise/docs 骨架，verify why.md#requirement-r5-expand-topics-with-scaffold-and-gates-s1-new-module-ready-from-day1
- [√] 4.3 新增模块 `springboot-logging`（日志框架/级别/结构化日志/常见坑），含 Lab/Exercise/docs 骨架，verify why.md#requirement-r5-expand-topics-with-scaffold-and-gates-s1-new-module-ready-from-day1
- [√] 4.4 新增模块 `spring-core-spel`（表达式解析/安全边界/性能），含 Lab/Exercise/docs 骨架，verify why.md#requirement-r5-expand-topics-with-scaffold-and-gates-s1-new-module-ready-from-day1

---

- [-] 5.1 增强一致性检查：检测“模块契约缺失项”（入口/断点地图/分支矩阵/调用链），verify why.md#requirement-r2-module-template-contract-s1-module-readme-and-docs-contract
- [-] 5.2 增强索引生成：Book/Labs/Exercises/Debugger Pack 的自动化生成与校验，verify why.md#requirement-r2-module-template-contract-s1-module-readme-and-docs-contract
  > Note: Deferred（本轮已使用现有脚本更新 labs-index；后续再扩展 exercises/debugger 自动化）。

---

## 6. Security Check（强制）

- [√] 6.1 安全自检（G9）：无生产环境操作、无明文密钥/Token、无破坏性脚本命令

---

## 7. 验证（阶段收尾）

- [√] 7.3 运行 `mvn -q test`

---

## 8. 知识库同步与归档（执行阶段强制）

- [√] 8.1 同步更新 `helloagents/wiki/**` 与 `helloagents/CHANGELOG.md`（记录结构/模板/深度/新增模块），verify why.md#requirement-r1-tutorials-style-structure-and-naming-s1-stable-structure-and-entrypoints
- [√] 8.2 执行完成后迁移方案包：`helloagents/plan/202601221758_tutorials_style_deepen_all/` → `helloagents/history/2026-01/202601221758_tutorials_style_deepen_all/`
- [√] 8.3 更新 `helloagents/history/index.md` 索引记录（✅Completed）
