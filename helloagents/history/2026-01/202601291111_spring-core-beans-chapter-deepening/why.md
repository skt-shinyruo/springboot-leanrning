# Change Proposal: spring-core-beans 逐章深度完善（Chapter-driven Deepening）

## Requirement Background

`spring-core-modules/spring-core-beans` 已经具备较完整的“可运行 Lab + 章节文档”体系，但当前目标是进一步把每一章从“能读懂/能跑通”推进到“能解释机制、能下断点证明、能排障定位、能对比边界条件”。

本次变更不采用固定补充清单或统一验收标准，而是**逐章阅读**后，按章节主题与现有内容的缺口，给出**具体的补充/完善/深入策略**，并将这些策略沉淀为可执行的任务清单，便于后续按章落地与回归验证。

## Change Content

1. 为 `docs/` 下每个章节输出“逐章补强策略”（与章节主题强绑定），包含：更深的机制主线、关键分支、证据链（方法级）、边界条件与反例、排障入口与观察点。
2. 统一增强章节间的“可导航性与可串联性”：补齐必要的导航锚点、增加跨章节引用与知识地图映射，让读者可从“症状/现象”快速定位到“章节 + Lab + 断点入口”。
3. 对需要“可证明”的章节，规划对应的 Lab/Test 增补（或强化断言），确保“读到的机制能在本仓库里跑出来/看出来”。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/**.md`
  - （按需要）`spring-core-modules/spring-core-beans/src/test/java/**`
- **APIs:** 无（文档与测试为主）
- **Data:** 无

## Core Scenarios

### Requirement: 逐章深度完善 spring-core-beans 文档
**Module:** spring-core-beans

目标：让每个章节都能回答“为什么这样、源码在哪里、断点怎么下、异常怎么定位、边界条件是什么”。

#### Scenario: 章节机制主线深化（方法级证据链）
读者需要从概念走到“证据链”：
- 输出每章关键主线（通常是 `refresh` → `getBean` → `doCreateBean` 的某个窗口）
- 为关键分支补充“条件 → 分支 → 结果”的可解释路径

#### Scenario: 边界条件、误区与反例补齐（可复现）
读者需要知道“哪些情况下不成立/会失败/会变成另一条路径”：
- 增加边界条件清单（如 `prototype`、`FactoryBean`、`@Lazy` 注入点、AOP/代理、AOT/Native 等）
- 提供反例与规避策略，并尽量能用本仓库的 Lab/Test 复现

#### Scenario: 排障入口与观察点收敛（从症状到第一断点）
读者遇到真实问题需要最短路径定位：
- 为每章补充“第一断点入口 + watch list”
- 与 `appendix/94-production-troubleshooting-checklist.md`、`appendix/92-knowledge-map.md` 建立更强映射

## Risk Assessment

- **Risk:** 章节增补与重排可能导致内部链接/导航锚点失效  
  **Mitigation:** 变更按章分批提交；每批次做链接与导航自检；优先保持相对路径稳定。
- **Risk:** 新增/增强 Lab 可能引入不稳定断言（受 Spring 版本/环境差异影响）  
  **Mitigation:** 断言以“机制级稳定点”为主（而非实现细节）；必要时用“最小可观察变量”替代 brittle 断言。
- **Risk:** 部分主题（AOT/Boot）与版本强相关，文档容易过时  
  **Mitigation:** 在章节显式标注适用版本/关键差异点，并把排障建议落到“证据链与观察点”，减少对结论的硬编码。

