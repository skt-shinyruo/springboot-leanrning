# Change Proposal: beans + aop 基础知识可解释性升级（Why Index + 最短证据链）

## Requirement Background

当前现状（来自真实读者反馈）：

- 读者完整阅读 `spring-core-modules/spring-core-beans` 后，仍无法回答类似“为什么 Spring 使用三级缓存”这类**基础高频问题**。
- 这类问题的“难点”不在名词，而在：前置心智模型不稳（proxy/最终暴露对象可能变化）、论证链分散（章节需要拼图）、最短证据链不够明确（不知道跑哪个 Lab/看哪个断点）。

本变更的目标不是“再补一段解释”，而是补齐本项目在“基础知识交付”上的系统性能力：让读者能**找到答案**、**看懂答案**、**验证答案**、**把答案复述出来**。

## Change Content

1. 在 `spring-core-beans` 与 `spring-core-aop` 两个模块内，新增并推广“基础问题索引（Why Index）”能力：
   - 每个问题提供：一句话结论 → 为什么重要 → 最短证据链（Lab/断点/观察点）→ 常见误区对照 → 下一步章节导航。
2. 强化跨模块前置依赖回链（Beans ↔ AOP Proxy）：
   - 在 Beans 的 early reference / proxying phase 章节显式链接到 AOP 的 proxy 心智模型；
   - 在 AOP 的 AutoProxyCreator / 代理心智模型中显式链接到 Beans 的“代理替换发生在哪个阶段/为什么最终形态会变化”。
3. 统一“最短闭环入口”：
   - 在 docs/README（目录页）与模块 README 中补齐一眼可见的入口；
   - 将“读完仍不懂”的高频点（如三级缓存、raw vs wrapped、一致性保护、self-invocation）纳入可检索体系。

## Impact Scope

- **Modules:**
  - `spring-core-modules/spring-core-beans`
  - `spring-core-modules/spring-core-aop`
- **Files:**
  - Beans：`docs/README.md`、`docs/part-00-guide/*`、`docs/part-01-ioc-container/09-*`、`docs/part-03-container-internals/16-*`、`docs/part-04-wiring-and-boundaries/31-*`、模块 `README.md`
  - AOP：`docs/README.md`、`docs/part-00-guide/*`、`docs/part-01-proxy-fundamentals/030-*`、`docs/part-02-autoproxy-and-pointcuts/036-*`、模块 `README.md`
  - Knowledge Base：`helloagents/wiki/modules/spring-core-beans.md`、`helloagents/wiki/modules/spring-core-aop.md`、`helloagents/CHANGELOG.md`
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: 基础问题必须可检索、可复述、可验证
**Module:** spring-core-beans + spring-core-aop
为高频基础问题提供统一的“答案入口 + 证据链入口”，降低读者拼图成本。

#### Scenario: 读者提出“为什么 Spring 用三级缓存（不只是字段名）？”
在 docs 体系内：
- 30 秒内能从目录页/Why Index 直达答案入口；
- 10 分钟内能跑通一个最小 Lab 并在断点里看见三层命中与 early reference 形态决策；
- 能复述“第三层 factory 的价值与 proxy/最终形态一致性的关系”。

#### Scenario: 读者提出“为什么 AOP/事务有时不生效（self-invocation/入口没走 proxy）？”
在 AOP docs 体系内：
- 能直达 proxy 心智模型与 call path 证明路径；
- 能回链到 Beans 的“代理替换发生在哪个阶段”解释，建立统一心智模型。

## Risk Assessment

- **Risk:** 新增索引页后，如果入口不够显眼/缺少回链，可能仍然“写了但用不上”。
  - **Mitigation:** 强制在模块 docs/README 与模块 README 放置入口；在关键章节开头增加“Why Index 入口”链接。
- **Risk:** 跨模块链接变多，可能产生断链。
  - **Mitigation:** 在实现阶段增加一次全局链接存在性检查（基于 `rg` + 最小脚本或 Maven 验证步骤），并在方案任务中显式要求验证。

