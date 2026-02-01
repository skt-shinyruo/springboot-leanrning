# Change Proposal: spring-core-beans docs 逐章继续深化（按章差异化 + 跨模块互链）

## Requirement Background

`spring-core-modules/spring-core-beans/docs/**` 已具备较强的“可读 + 可跑 + 可断点”基础，但在持续迭代中仍会出现两类常见问题：

1. **深度不均匀**：部分章节的“结论/证据/复现/排错”链路足够强，部分章节虽信息完整但“读者很难把概念转成可验证步骤”（尤其是方法级证据链、最短排错路径与对照实验）。
2. **跨模块认知断层**：当章节触及 proxy/BPP/事务/self-invocation 等主题时，读者需要在 Beans ↔ AOP 之间切换；如果缺少“最短跳转建议 + 为什么要跳 + 跳过去验证什么”，理解成本会显著上升。

本次变更的目标不是引入固定模板或重排目录，而是在**不改文件路径**、不破坏既有阅读体验的前提下，做一次“逐章阅读 → 逐章补强”的继续深化：

- 每一章都以“本章实际内容”决定需要补什么：哪里该补证据链，哪里该补复现入口，哪里该补断点路线，哪里该补排错路径，哪里该补下一跳承接。
- 避免用同一套小标题/固定清单强行套版；同一类型信息（例如断点/用例）只在确实能降低读者成本时才补充。

**约束与偏好（来自用户明确输入）：**
- 范围：全量覆盖 `spring-core-modules/spring-core-beans/docs/**`
- 目标：全部都要“深度完善”，但不采用统一模板；按章节内容差异化补齐
- 跨模块互链：需要（以 Beans 侧为主，不扩散改动范围）
- 交付方式：只生成 solution package（不进入执行）
- 写法要求：不使用固定格式强行套版；应根据内容需要自然补齐信息

## Change Content

1. **逐章阅读并给出策略**：对 `spring-core-modules/spring-core-beans/docs/**` 的每一篇文档，给出“该章当前已有资产 + 继续深化的具体策略”（见方案包内 `audit/chapter-strategies.md`）。
2. **按章差异化补强**：执行阶段按 `audit/chapter-strategies.md` 逐章落地（不改路径，不强行统一小标题/固定骨架）。
3. **跨模块互链深化（Beans → AOP）**：对涉及 proxy/自调用/事务等内容的章节，补齐“为什么要跳 + 跳过去验证什么”，避免只贴链接不说明。
4. **全量质量门禁**：断链为 0、占位清理、回归测试通过。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`（docs 为主）
- **Files:** `spring-core-modules/spring-core-beans/docs/**`（含 README / deepening-strategies / appendix）
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: R1 chapter-specific-deepen
**Module:** spring-core-beans docs
对 beans docs 全量章节做“内容级继续深化”，但不采用统一模板；以每章的主题与现有内容为准，逐章补齐读者最容易卡住的那块拼图。

#### Scenario: S1 per-chapter-strategies
每章对应一段“继续深化策略”，包含该章已经提供的入口（源码锚点/用例/互链）与建议补强点；执行阶段按策略逐章落地。

#### Scenario: S2 beans-to-aop-links
当章节触及 proxy/BPP/self-invocation/事务等主题时：
- 提供就近的最短跳转建议（保持链接目标不变）
- 写清楚“为什么要跳 + 跳过去验证什么（断点/证据链）”，避免只给链接不说明

### Requirement: R2 quality-gates
**Module:** spring-core-beans docs（+ 回归测试）

#### Scenario: S1 self-check-pass
- `spring-core-modules/spring-core-beans/docs/**` 相对链接目标存在性检查 missing targets = 0
- Lab/Test 引用存在性检查通过（引用到的测试类/文件路径真实存在）
- `mvn -pl spring-core-modules/spring-core-beans test` 通过
- 不引入敏感信息（密钥/token/内网地址/个人信息）

## Risk Assessment

- **Risk:** 全量文档修改容易“过度统一”导致模板化、或产生跨章叙事不一致。
  - **Mitigation:** 以“章节缺口清单”驱动，优先补最短闭环与证据链；只在需要处增补，不做无意义重写；不改路径，批次化修改并持续跑自检与回归。
- **Risk:** 跨模块互链如果只给链接不解释，仍会增加读者负担。
  - **Mitigation:** 每次跨模块跳转都要明确“跳过去要验证什么”，并控制为 1–2 个最短下一跳。
