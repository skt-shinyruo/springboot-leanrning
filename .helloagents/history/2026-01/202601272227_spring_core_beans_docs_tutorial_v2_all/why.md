# Change Proposal: spring-core-beans docs 全量“教程化 v2”（源码进阶 + 面试标准答案）

## Requirement Background

用户目标非常明确：`spring-core-modules/spring-core-beans/docs` 必须具备“**教程的样子**”，同时满足两类核心诉求：

1. **源码进阶**：能把关键机制落到“可追踪到方法级”的调用链，并能通过断点与观察点拿到证据链。  
2. **面试输出**：能直接复述“标准答案”，且答案必须可被证据链证明（不是背书）。

当前 `docs` 已经具备良好的基础结构（Part 划分、BOOKIFY 导航、Lab/Test 入口、断点/观察点等），但“教程化 v2”的缺口集中在两点：

- **面试输出覆盖不均**：不少章节缺少明确的“面试标准答案/复述结构”，导致读者“看懂但讲不出来”。  
- **排障表达不够统一**：少数章节仍缺少“现象 → 分层 → 证据 → 修复 → 验证”的决策式表达，导致“看起来深但不好用”。

本变更目标：对 `spring-core-beans/docs` **全量文档**进行统一升级，使其在“按章学习/按现象排障/按题训练面试”三种使用方式下都能闭环。

## Change Content

1. 定义并落地统一的“教程章契约（Chapter Contract）”：每章必须具备 10 分钟最小闭环 / 30 分钟深挖闭环 / 3 分钟复述闭环。
2. 统一每章的“证据链写法”：入口（Lab/Test）→ 断点（3–5 个稳定锚点）→ watch list（关键变量）→ 可观察结论（一句话）。
3. 全量补齐/升级“面试标准答案”输出（按章或按索引引用），确保每个关键机制点都能被复述且可证明。
4. 全量补齐/升级“排障决策表/分流”表达：用表格把现象快速映射到最短断点与修复策略。
5. 强化全局索引页：`docs/README.md` 中显式提供“双轨阅读路线”（源码进阶路线 / 面试冲刺路线），并与 Appendix（93/94/98）互链。
6. 引入“覆盖审计”机制：以清单方式确保 70 篇文档均被纳入本次优化范围（不漏章）。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/**/*.md`（全量优化）
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: R1-docs-tutorial-contract
**Module:** spring-core-beans
为所有章节落地“教程章契约”，保证每章都可跑/可看见/可复述。

#### Scenario: S1-10-30-3-closure
- 10 分钟：读者能跑通本章最小 Lab/Test 并看到预期现象
- 30 分钟：读者能命中关键断点并观察到决定性变量
- 3 分钟：读者能复述标准答案（结论 + 证据链 + 反例）

### Requirement: R2-method-level-evidence-chain
**Module:** spring-core-beans
把核心机制写成“方法级证据链”（稳定锚点），而非堆类名。

#### Scenario: S2-breakpoint-to-conclusion
- 给定一个现象，读者能在 3–5 个断点内得到可验证结论

### Requirement: R3-interview-standard-answers
**Module:** spring-core-beans
为关键机制点提供面试标准答案，并确保能回指到本仓库可跑的证据链入口。

#### Scenario: S3-answer-with-proof
- 每个标准答案至少绑定 1 个 Lab/Test + 1 条方法级调用链锚点

### Requirement: R4-troubleshooting-decision-table
**Module:** spring-core-beans
为关键现象提供决策表（现象→分层→证据→修复→验证），提升“教程可用性”。

#### Scenario: S4-fast-triage
- 读者能用表格 2–3 步将问题归类并定位到最短修复路径

## Risk Assessment

- **Risk:** 全量文档修改导致 diff 很大、review 成本高  
  **Mitigation:** 任务按 Part 分批执行，阶段性验证；每批次完成后运行最小回归测试并更新索引。
- **Risk:** 方法级锚点受 Spring 小版本变化影响  
  **Mitigation:** 优先使用稳定锚点（refresh/BDRPP/BPP/registerBeanDefinition/doCreateBean），并在文档中显式标注版本基线（Spring 6.2.x / Boot 3.5.x）。

