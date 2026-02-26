# Change Proposal: spring-core-beans 深化（PostProcessors + 容器启动基础设施）源码解析补强

## Requirement Background

当前 `spring-core-beans` 的 docs/06（post-processors）与 docs/12（容器启动与基础设施处理器）已经能解释“概念是什么”和“如何用断点看见”，但仍有两个常见学习瓶颈：

1) **无法把 BFPP/BDRPP/BPP 的顺序与行为落到同一条源码主线**  
   例如：只知道“PriorityOrdered → Ordered → 无序”，但不知道 `PostProcessorRegistrationDelegate` 在源码里是如何“分组 + 反复扫描 + 先 registry 后 factory”的。

2) **对“注解为什么能工作”的解释缺少 bootstrap 视角**  
   例如：知道要注册 `AnnotationConfigUtils.registerAnnotationConfigProcessors(...)`，但说不清 `AnnotationConfigApplicationContext` 是如何默认装配这套基础设施，以及“进 registry”与“进 BeanFactory 的 BPP 列表”两者的区别与时机。

本次目标：把这两章升级为“**源码主线可复述、概念可落地、现象可复现**”的文档。

## Change Content
1. 在 docs/06 增补 `PostProcessorRegistrationDelegate` 的两段核心算法（invoke BFPP/BDRPP、register BPP），用精简伪代码与关键分叉解释“为什么顺序会改变结果”
2. 在 docs/06 补齐 “`static @Bean` 为什么常被推荐用于 BFPP/BPP” 的源码原因，并提供最小可运行实验（新增 Lab）
3. 在 docs/12 增补 “`AnnotationConfigApplicationContext` 默认如何装配 annotation processors” 的 bootstrap 主线解析（从构造器/reader 到注册 internal processors）
4. 在 docs/12 补齐 “处理器注册到 registry” 与 “处理器注册到 BeanFactory 的 BPP 列表” 的时机差异与排障结论（避免把两件事混为一谈）

## Impact Scope
- **Modules:** `spring-core-beans`
- **Files:**
  - `docs/beans/spring-core-beans/06-post-processors.md`
  - `docs/beans/spring-core-beans/12-container-bootstrap-and-infrastructure.md`
  - `spring-core-beans/src/test/java/...`（新增最小 Lab，帮助理解 `static @Bean` 的必要性）
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: 能复述 BFPP/BDRPP 的真实执行算法（而不是只背结论）
**Module:** `spring-core-beans`

<a id="scenario-bdrpp-bfpp-algorithm"></a>
#### Scenario: 为什么 BDRPP 会“先跑一轮 registry，再跑一轮 factory”
- **Given**：阅读 docs/06 新增“源码解析：invokeBeanFactoryPostProcessors”小节
- **Then**：
  - 能解释为什么 BDRPP 需要“反复扫描”（它可能注册新的 BDRPP）
  - 能解释为什么先 registry 再 factory（定义必须先稳定）

### Requirement: 能解释 BPP 的注册与应用算法（含 Checker/排序）
**Module:** `spring-core-beans`

<a id="scenario-register-bpp-algorithm"></a>
#### Scenario: 为什么某些 bean 会提示“没被所有 BPP 处理”
- **Given**：阅读 docs/06 新增“源码解析：registerBeanPostProcessors”小节
- **Then**：
  - 能解释 BPP 本身也是 bean，注册阶段需要实例化它们
  - 能解释“早创建”的 bean 为什么不会被后续 BPP 处理（不可追溯）

### Requirement: 能解释 `static @Bean` 的真正价值（避免早实例化与顺序陷阱）
**Module:** `spring-core-beans`

<a id="scenario-static-bean-for-postprocessor"></a>
#### Scenario: non-static BFPP 迫使配置类过早实例化，导致它错过普通 BPP
- **Given**：新增 Lab（本仓库可运行最小复现）
- **Then**：
  - 能解释“为什么配置类会被提前 new”
  - 能解释“为什么后注册的 BPP 不会 retroactively 生效”

### Requirement: 能解释注解基础设施的 bootstrap 主线（默认装配 vs 手工装配）
**Module:** `spring-core-beans`

<a id="scenario-annotation-processors-bootstrap"></a>
#### Scenario: AnnotationConfigApplicationContext vs GenericApplicationContext 行为差异的根因
- **Given**：阅读 docs/12 新增“源码解析：AnnotationConfigApplicationContext 的默认装配链路”
- **Then**：
  - 能解释 annotation processors 是如何被注册进 registry 的（internal*Processor bean definitions）
  - 能解释它们又是如何进入 BeanFactory 的 BPP 列表并参与实例创建链路的

## Risk Assessment
- **Risk:** 文档过度贴近某个 Spring 小版本内部细节  
  **Mitigation:** 以稳定主线（refresh / PostProcessorRegistrationDelegate / doCreateBean）为锚点，避免依赖具体日志文本与易变字段名
- **Risk:** 为了讲清楚“static @Bean”新增实验可能引入脆弱断言  
  **Mitigation:** 只断言“某个 bean 是否被某个 BPP 处理”（事件列表），不依赖日志输出与异常 message 文本

