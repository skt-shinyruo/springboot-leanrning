# Change Proposal: spring-core-beans 将 A–E 策略写入各章节正文

## Requirement Background

`spring-core-modules/spring-core-beans` 已具备较完整的“章节体系 + 可运行 Lab/Test + 断点观察点 + 排障/面试附录”闭环。

当前需求是进一步把“内容级再加深”的 **A–E 维度策略**真正落到**每一章正文**里，让读者在阅读章节时即可得到：

- A：源码证据链（方法级下沉）
- B：边界条件与反例（可复现）
- C：生产排障分型与 SOP（从症状到第一断点）
- D：断点与 watch list 强化（可观察）
- E：面试复述与追问（可证明）

同时强调：不采用“固定模板化填空”的统一标准，而是按章节主题差异化补强“最缺的环节”，避免重复堆叠既有内容。

## Change Content

1. 为每个章节在合适位置补充“内容级再加深（A–E）”提示块（章节内可直接使用，避免仅存在于策略目录）。
2. 对已深度覆盖 A–E 的章节：以“证据链入口/反例/排障入口/断点组/追问”做**更可复用的收敛**，避免重复改写正文。
3. 保持原章节可读性与导航一致性：不破坏现有结构与 BOOKIFY 导航；新增内容以“小块增量”为主。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/**/*.md`（逐章插入/补强 A–E 深化内容）
  - `helloagents/wiki/modules/spring-core-beans.md`（SSOT 同步）
  - `helloagents/CHANGELOG.md`（变更记录）
  - `helloagents/history/index.md`（方案包索引）
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: A–E 深化策略写入各章正文
**Module:** spring-core-beans

读者读到任意章节时，应能够直接从正文获得：

#### Scenario: 从“读懂”到“可证明”
- 章内给出至少一个方法级证据链入口（从哪里下断点/看什么对象/如何判定分支）
- 章内给出至少一个边界/反例提示（哪些情况下不成立/容易误诊）
- 章内给出最短排障 SOP（症状→分层→第一断点→观察点→修复/验证）

## Risk Assessment

- **Risk:** 全章批量改动容易引入重复与噪音，影响阅读体验  
  **Mitigation:** 以“增量小块 + 章节差异化”为原则；新增内容优先放在 `## 机制主线` 前的提示块，避免散落各处。
- **Risk:** 批量插入可能造成重复插入/冲突  
  **Mitigation:** 使用明确的块标记（start/end），保证幂等可维护。

