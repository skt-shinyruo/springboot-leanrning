# Change Proposal: Spring Core Beans 学习闭环计划（First Pass）

## Requirement Background

当前学习聚焦在 `spring-core-beans` 模块。常见卡点不是 API 记不住，而是“概念没有落到容器主线”，导致在 Scope、生命周期、代理、自动装配、循环依赖等场景里不断出现误判。

本计划的目标是把这些问题拆成一组**可复现、可断言、可下断点验证**的学习任务：每一个“结论”都能用本仓库已有的 docs/Lab 证明。

说明：该方案包为早期的 First Pass 学习任务清单。根据反馈，本次不再新增额外的 docs 闭环文件；此处仅保留方案包用于追溯与复盘。

## Change Content
1. 建立 Bean 的三层心智模型：Inputs → BeanDefinition(Definitions) → Instances(最终暴露对象)
2. 理清 BeanDefinition 的主要注册入口：扫描 / `@Bean` / `@Import` / registrar
3. 掌握 DI 解析与候选确定化：Type/Name/`@Qualifier`/`@Primary`/`@Resource`
4. Scope 的真实语义与 prototype 注入陷阱
5. 生命周期：创建/注入/初始化/销毁的阶段感与回调顺序
6. 扩展点：BDRPP/BFPP/BPP 的介入位置与顺序影响
7. 循环依赖与 early reference：为什么“有时能过、有时必炸”
8. 调试与排障闭环：从现象/异常快速定位到正确断点入口

## Impact Scope
- **Modules:** `spring-core-beans`
- **Files:** 主要使用 `docs/beans/spring-core-beans/*`、`spring-core-beans/src/test/*`（必要时补充查看 `spring-core-beans/src/main/*`）
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: Bean Mental Model (Inputs-Definitions-Instances)
**Module:** `spring-core-beans`

目标：能准确区分 `BeanDefinition`、原始实例、最终 `getBean()` 暴露对象（可能是 proxy），并把它们放回 `refresh()` 主线解释“为什么”。

<a id="scenario-prove-definition--instance-lab--debugger"></a>
#### Scenario: Prove definition != instance (Lab + Debugger)
- **Given**：运行 `SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance`
- **Then**：
  - 能说明 `BeanDefinition`（定义层）与 bean instance（实例层）是不同概念对象
  - 能解释 BFPP 更像“改配方”，BPP 更像“改做出来的菜/甚至换成 proxy”

### Requirement: Bean Registration Entrances (Scan-Bean-Import-Registrar)
**Module:** `spring-core-beans`

目标：看到一个 Bean 时，能回答“它是从哪个入口注册进来的”，并能定位到对应的容器阶段。

<a id="scenario-trace-where-beandefinition-comes-from"></a>
#### Scenario: Trace where BeanDefinition comes from
- **Given**：阅读 `docs/01-bean-registration.md`，运行 `SpringCoreBeansBootstrapInternalsLabTest`
- **Then**：
  - 能区分扫描注册 vs `@Bean` vs `@Import`
  - 能解释“为什么注解能工作”（基础设施处理器在哪里注册、何时生效）

### Requirement: DI Resolution (Type-Name-Qualifier-Primary-Resource)
**Module:** `spring-core-beans`

目标：遇到注入歧义时，能预测候选集合如何被筛选，能用确定化手段修复并解释优先级。

<a id="scenario-resolve-ambiguity-deterministically"></a>
#### Scenario: Resolve ambiguity deterministically
- **Given**：运行 `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`
- **Then**：
  - 能用 `@Qualifier` / `@Primary` 解释并修复 `NoUniqueBeanDefinitionException`
  - 能说明 `@Order` 不是“候选选择”的依据（避免误用）

### Requirement: Scope & Prototype Pitfalls
**Module:** `spring-core-beans`

目标：解释 prototype 与 singleton 的差异，并能说明“prototype 注入 singleton 看起来像单例”的根因与修复方式。

<a id="scenario-explain-prototype-injected-into-singleton"></a>
#### Scenario: Explain prototype injected into singleton
- **Given**：运行 `SpringCoreBeansLabTest`（prototype 场景相关用例）
- **Then**：
  - 能解释“注入发生在创建期”，而不是“每次调用都会重新解析”
  - 能给出至少一种修复方案（Provider/ObjectFactory/Lookup 等思路）

### Requirement: Lifecycle Callbacks & Order
**Module:** `spring-core-beans`

目标：能把生命周期拆成 instantiate → populate → initialize，并解释常见回调的触发时机与顺序。

<a id="scenario-verify-callback-timing-and-order"></a>
#### Scenario: Verify callback timing and order
- **Given**：运行 `SpringCoreBeansLifecycleCallbackOrderLabTest`
- **Then**：
  - 能说明 `@PostConstruct` 为什么在容器启动期执行
  - 能说明 prototype 默认不受容器统一销毁管理（为什么“看不到 destroy”）

### Requirement: Post-Processors (BDRPP-BFPP-BPP) and Ordering
**Module:** `spring-core-beans`

目标：能回答“定义层/实例层分别在哪被改写”，并理解 ordering 为什么会改变最终结果（包含 proxy/短路等）。

<a id="scenario-observe-definitioninstance-mutation-points"></a>
#### Scenario: Observe definition/instance mutation points
- **Given**：运行 `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
- **Then**：
  - 能说清 BDRPP、BFPP、BPP 的触发顺序与职责边界
  - 能说明为什么“顺序不对”会导致注入、代理、条件装配等行为改变

### Requirement: Circular Dependencies & Early Reference
**Module:** `spring-core-beans`

目标：理解循环依赖的几种形态（构造器/字段/Setter），以及 early reference 如何影响“有时能过、有时必炸”。

<a id="scenario-reproduce-and-explain-early-exposure"></a>
#### Scenario: Reproduce and explain early exposure
- **Given**：运行 `SpringCoreBeansEarlyReferenceLabTest`
- **Then**：
  - 能说明 early reference 发生的阶段与作用（解决一部分循环依赖）
  - 能解释“为什么构造器循环依赖通常无解”

### Requirement: Debugging Playbook (Symptom → Breakpoint)
**Module:** `spring-core-beans`

目标：遇到注入失败/代理不生效/Bean 覆盖等问题时，能从异常快速落到正确的断点入口，形成稳定排障闭环。

<a id="scenario-locate-root-cause-via-breakpoint-map"></a>
#### Scenario: Locate root cause via breakpoint map
- **Given**：运行 `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`
- **Then**：
  - 能基于异常信息选择断点入口（候选集合、最终注入点、依赖关系图）
  - 能在“最终暴露对象可能是 proxy”的前提下定位替换发生点

## Risk Assessment
- **Risk:** 学习任务容易“只读文档、不跑实验”，结论无法稳定落地
- **Mitigation:** 每个任务都绑定一个 Lab 与一个明确观察点（断点/断言），完成后写出可复述结论
