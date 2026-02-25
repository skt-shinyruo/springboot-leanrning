# Change Proposal: spring-core-beans Ordering & Programmatic BPP Deepening

## Requirement Background
`spring-core-beans` 模块的文档已覆盖了 BFPP/BPP 的“现象级”结论与实验入口，但在以下两点上仍不够“源码算法级”：

1. **Ordering 的算法细节不足**：`PostProcessorRegistrationDelegate` 的分组、排序、循环发现（BDRPP）与“internal BPP 重新注册”这类关键机制，需要以“伪代码 + 状态变量 + 关键分支”的方式落到文档中，才能把现象解释闭环。
2. **programmatic addBeanPostProcessor 的机制解释不足**：需要明确 `addBeanPostProcessor` 的语义是“直接改 list”，绕开容器排序流程；并补齐“时机陷阱（只对之后创建的 bean 生效）”等更常见坑点。

目标：把 `03-post-processor-ordering.md` 与 `08-programmatic-bpp-registration.md` 补成“算法级 + 可复现”版本，读者无需跑断点也能理解主线；必要时通过本仓库 `src/test/java` 的最小 Lab 复现关键现象。

## Change Content
1. 为 `03-post-processor-ordering.md` 增加源码解析小节：BFPP/BDRPP 与 BPP 的真实执行/注册算法（简化伪代码 + 关键集合/状态说明），以及 `AnnotationAwareOrderComparator` 的 order 解析规则。
2. 为 `08-programmatic-bpp-registration.md` 增加源码解析小节：`addBeanPostProcessor` 的 list 语义、为何绕过排序、以及“添加太晚不影响已创建 bean”的时机陷阱。
3. 视需要补充/增强可复现 Lab：让 `@Order`/order 数值与“注册顺序 vs 排序顺序”可直接用测试验证。
4. 同步更新知识库索引与变更记录。

## Impact Scope
- **Modules:** `spring-core-beans`
- **Files (Docs):**
  - `docs/beans/spring-core-beans/03-post-processor-ordering.md`
  - `docs/beans/spring-core-beans/08-programmatic-bpp-registration.md`
- **Files (Labs, if needed):**
  - `spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansPostProcessorOrderingLabTest.java`
- **Knowledge Base:**
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`

## Core Scenarios

### Requirement: Ordering（分组 + 排序 + 执行/注册时机）
**Module:** spring-core-beans
需要能解释清楚：当存在多个 BFPP/BPP 时，容器如何分组、如何排序、最终如何决定执行顺序，以及哪些“看似顺序问题”其实是“时机问题”。

#### Scenario: BFPP/BDRPP（定义层）顺序与循环发现
- 解释 `invokeBeanFactoryPostProcessors` 的两阶段目标：先 BDRPP 再 BFPP
- 解释 BDRPP 的循环发现（前一个 BDRPP 可能注册新的 BDRPP）
- 给出“稳定可复现”的观察点（只断言相对顺序）

#### Scenario: BPP（实例层）注册顺序与 internal BPP 重新注册
- 解释 `registerBeanPostProcessors` 的三段注册（PriorityOrdered/Ordered/others）
- 解释 internal BPP 为什么会“挪到最后”（影响包裹顺序）
- 给出 `@Order/Ordered` 的 order 值解析规则

### Requirement: programmatic addBeanPostProcessor（绕过排序 + 时机陷阱）
**Module:** spring-core-beans

#### Scenario: 手工注册先于自动发现
- 解释“为什么更早加入 list 就更早执行”
- 用 Lab 复现（programmatic BPP 在 bean-defined BPP 之前执行）

#### Scenario: Ordered 不生效（绕过排序）
- 解释 `addBeanPostProcessor` 只做 list 操作，不会触发容器排序
- 用 Lab 复现（执行顺序 = 注册顺序）

#### Scenario: 添加太晚只影响之后创建的 bean
- 解释“已创建 bean 不会 retroactive 再走 BPP”
- 给出与 BDRPP/BFPP 早期 `getBean()` 触发过早实例化的对应关系

## Risk Assessment
- **Risk:** 过度依赖容器内部 post-processor 的完整顺序，可能随 Spring 版本变化导致文档与测试不稳定。
- **Mitigation:** 以“相对顺序 + 可控扩展点”为主；只对稳定规则（分组、order 值比较、list 语义）做断言与结论。

