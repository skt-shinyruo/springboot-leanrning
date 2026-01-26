# Change Proposal: spring-core-beans 文档与 Labs 深化（证据链 + 边界 Case）

## Requirement Background

当前 `spring-core-modules/spring-core-beans` 的文档已经完成“章节结构契约化/教程化一致性”的第一轮补齐，但对 B/C 读者（能断点、能解释、能排障）来说，仍存在进一步深化空间：

1. **可跑 Lab 的覆盖仍偏“点状”**：某些高频边界（循环依赖、FactoryBean、泛型匹配、@Lazy 代理、MergedBeanDefinition 等）需要更多“可复现 + 可断言”的最小实验闭环。
2. **源码证据链仍可更强**：希望将关键机制落到“最短调用链 + 关键分支条件 + 关键数据结构变化（watch list）+ 最小源码片段/伪代码对照 + 调试入口点”。
3. **边界 case 的解释需要更细**：不仅“是什么”，还要“为什么会这样 + 如何验证 + 怎么排障定位 + 如何规避”。

## Change Content

1. 为高频边界/易错点补齐或新增 Labs（以 `mvn -pl :spring-core-beans -Dtest=... test` 可直接运行）。
2. 将相关章节升级到“证据链级”讲解（粒度：更像一本书的章节，包含最小源码片段/伪代码对照与 watch list）。
3. 将新增 Labs 入口与文档章节建立明确映射（章节 → 对应 Lab → 断点观察点 → 排障分流）。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/src/test/java/**`（新增/补齐 Labs）
  - `spring-core-modules/spring-core-beans/docs/**`（对应章节加深）
  - `helloagents/wiki/modules/spring-core-beans.md`（同步入口与变更说明）
  - `helloagents/CHANGELOG.md`（记录变更）
- **APIs:** 无对外 API 变更
- **Data:** 无

## Core Scenarios

### Requirement: 深化“高频边界/排障”章节的证据链与 Labs
**Module:** `spring-core-modules/spring-core-beans`
将以下边界主题补齐为“可跑 + 可断点 + 可复述 + 可排障”的教程：

- 循环依赖（early reference / singletonFactories / 三缓存）
- FactoryBean（`&` 语义、产品类型推断、类型匹配、实例化时机）
- 泛型/类型匹配（ResolvableType、泛型擦除、FactoryBean getObjectType 影响）
- `@Lazy` 代理（definition-lazy vs injection-point-lazy、代理类型差异、与循环依赖/类型匹配的交互）
- MergedBeanDefinition（合并发生在哪里、合并带来的“看起来不一致”）
- 属性占位符严格/非严格（ignoreUnresolvablePlaceholders、默认值、时机）
- `@Value` SpEL（解析入口、类型转换、失败形态、与占位符差异）
- 作用域/ScopedProxy（prototype 注入陷阱、scoped proxy 语义、custom scope 观测）
- SmartLifecycle phase（启动/停止顺序、phase 分组、超时与排障）
- 父子容器（可见性、同名覆盖/屏蔽、containsLocalBean、排障路径）
- BeanDefinition 覆盖（允许/禁止覆盖、覆盖来源定位、排障最短路径）

#### Scenario: 学习/调试
- 读者可以通过章节提供的 Lab 复现现象，并用断点观察关键数据结构变化。

#### Scenario: 排障/解释
- 遇到真实问题时，读者能按章节的“排障分流表”快速定位到最短调用链，并给出可复述的原因与修复策略。

## Risk Assessment

- **Risk:** 新增测试与文档内容可能增加维护成本/回归耗时。
- **Mitigation:** Lab 以“最小可断言”形式实现；探索性/高成本 case 使用 explore 开关隔离（不影响默认回归）；保持命名与现有 suites（BreakpointPack / TroubleshootingPlaybook）一致。

