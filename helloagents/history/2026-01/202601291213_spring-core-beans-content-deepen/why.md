# Change Proposal: spring-core-beans 内容级再加深（全章 A–E 维度）

## Requirement Background

`spring-core-modules/spring-core-beans` 已具备“章节体系 + 可运行 Lab/Test + 断点观察点 + 排障/面试附录”的完整学习闭环。

本次需求是在既有基础上做**内容级再加深**（不是格式补齐、不是统一模板化改写），并且覆盖你明确指定的 5 个维度：

- A：源码证据链（方法级下沉）
- B：边界条件与反例（可复现）
- C：生产排障分型与 SOP（从症状到第一断点）
- D：断点与 watch list 强化（可观察）
- E：面试复述与追问（可证明）

范围为 `spring-core-beans` **全量章节**（含目录页与 Appendix），并允许为“可证明”补齐/增强对应的 Lab/Test。

## Change Content

1. **逐章精读并“差异化加深”**：对每个章节基于其主题与现有内容，补充更深入的机制链路、关键分支、边界反例与排障路径，避免“统一标准化填空”。
2. **强化“症状→章节→证据链→Lab”的快速定位**：目录页与关键工具型章节新增/增强快速索引与跳转路径，提升实战排障效率。
3. **用可运行验证兜底**：对新增的关键结论与边界条件，优先通过现有 Lab/Test 固化；必要时新增/增强测试用例，保证可回归验证。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/**.md`（全章内容增量深化）
  - `spring-core-modules/spring-core-beans/src/test/java/**`（按需要新增/增强可证明 Lab/Test）
  - `helloagents/wiki/modules/spring-core-beans.md`（SSOT 同步）
  - `helloagents/CHANGELOG.md`（变更记录）
- **APIs:** 无（文档与测试为主）
- **Data:** 无

## Core Scenarios

### Requirement: 全章内容级再加深（A–E）
**Module:** spring-core-beans

目标：让读者面对真实问题时，不仅“知道概念”，还能在最短时间内给出：
结论 → 方法级证据链 → 边界反例 → 排障入口 → 可运行证明。

#### Scenario: 从“叙事”升级到“证据链”
- 对关键结论补充最短调用链、关键 if/return 分支、必看变量
- 让读者能在 IDE 里通过断点与 watch list 看见决策发生点

#### Scenario: 从“例子”升级到“边界”
- 通过反例/失败分型解释“哪些情况下不成立”
- 尽量让边界反例可由本仓库 Lab/Test 复现（或新增小用例）

#### Scenario: 从“定位章节”升级到“定位断点”
- 对高频症状给出第一断点入口、关键观察对象、最短章节链路
- 与 Appendix（知识地图/排障清单/Debugger Pack）形成闭环

## Risk Assessment

- **Risk:** 全量章节改动导致 diff 很大、review 成本高  
  **Mitigation:** 以“差异化增量”为原则，优先新增能带来可观察/可证明价值的内容；避免重复改写既有段落。
- **Risk:** 新增/增强 Lab/Test 可能引入不稳定断言（依赖实现细节）  
  **Mitigation:** 断言以机制级稳定点为主；对版本敏感的行为显式标注适用条件，并避免 brittle 的细节断言。
- **Risk:** 文档新增内容可能造成跨章引用与导航断链  
  **Mitigation:** 保持路径稳定；新增内容以“附加小节/补强块”为主；每批次完成后做链接自检与模块全量测试回归。

