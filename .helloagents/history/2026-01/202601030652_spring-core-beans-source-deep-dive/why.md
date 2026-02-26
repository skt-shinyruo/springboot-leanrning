# Change Proposal: spring-core-beans 知识点 × 源码解析深挖方案（Deep Dive）

## Requirement Background

当前 `spring-core-beans` 模块已经具备较完整的 docs 与 Labs，但在“从知识点到源码主线”的桥接上仍然偏轻：学习者往往能复述结论，却很难做到：

1) 把现象稳定映射到 `ApplicationContext#refresh` 的阶段位置（定义层/实例层/最终暴露对象）  
2) 在真实排障时，快速选择正确的断点入口（而不是靠日志猜）  
3) 解释“为什么会这样”时能引用关键类/关键方法（而不是停留在概念口号）

本方案的目标不是新增业务功能，而是把 `spring-core-beans` 的核心知识点补齐为 **源码可解释、可断言、可排障** 的学习闭环。

⚠️ 需求澄清（本次新增）：这里的“给出代码”不仅是伪代码/片段化的 Spring Framework 源码逻辑描述；在必要时还需要直接引用本仓库 `src/` 下的代码（例如 `spring-core-beans/src/test/java/...` 的 Labs、以及 `spring-core-beans/src/main/java/...` 的 demo/组件），用于把知识点落到“可运行的最小示例”。

## Change Content
1. 为每个核心知识点补齐“源码主线入口 + 关键方法 + 观察点（变量/缓存/调用链）”
2. 将学习任务与本仓库已有 Labs 绑定：每个结论都能用一个可运行测试证明
3. 给出“最小断点地图”：从 `refresh()` 到 `doCreateBean()` 再到循环依赖与代理替换点
4. 形成一套可复用的排障路径：异常/现象 → 断点入口 → 关键状态 → 结论

## Impact Scope
- **Modules:** `spring-core-beans`
- **Files:** `docs/beans/spring-core-beans/01-bean-mental-model.md`、`docs/beans/spring-core-beans/01-bean-registration.md`、`docs/beans/spring-core-beans/03-dependency-injection-resolution.md`、`docs/beans/spring-core-beans/05-lifecycle-and-callbacks.md`、`docs/beans/spring-core-beans/08-circular-dependencies.md`；以及知识库记录 `helloagents/wiki/modules/spring-core-beans.md`、`helloagents/CHANGELOG.md`、`helloagents/history/index.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: 容器主线可解释（refresh 12 步）
**Module:** `spring-core-beans`

目标：能把“注册定义 / 执行 BFPP/BDRPP / 注册 BPP / 预实例化单例”等关键阶段，精确定位在 `AbstractApplicationContext#refresh` 的哪一步。

<a id="scenario-refresh-mainline-12-steps"></a>
#### Scenario: 从测试入口 Step Into refresh 并标注阶段
- **Given**：运行 `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`（或任意 `AnnotationConfigApplicationContext` 初始化用例）
- **Then**：
  - 能列出 refresh 的 10~12 个粗粒度步骤（不要求逐行源码）
  - 能指出 BFPP/BPP 分别在哪里发生、为什么会影响后续行为

### Requirement: 定义层的“来源与形态”可追踪
**Module:** `spring-core-beans`

目标：看到一个 bean 时，能回答“它是通过哪种输入注册进来的”，并能解释不同 BeanDefinition 的形态差异（scanned / @Bean factory method / imported）。

<a id="scenario-definition-registration-pipeline"></a>
#### Scenario: 定位 BeanDefinition 的来源（scan/@Bean/@Import）
- **Given**：阅读 `docs/01-bean-registration.md` + 运行 `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansImportLabTest`
- **Then**：
  - 能区分 `ScannedGenericBeanDefinition`、`AnnotatedGenericBeanDefinition`、`RootBeanDefinition` 等常见形态
  - 能根据 `BeanDefinition` 里的 source/resource 信息推断注册路径

### Requirement: 依赖注入解析的“候选选择算法”可解释
**Module:** `spring-core-beans`

目标：遇到 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` 时，不靠“经验猜”，而是能描述候选集合如何被筛选（type/name/qualifier/primary/priority），并能定位到核心方法。

<a id="scenario-di-resolution-algorithm"></a>
#### Scenario: 用断点看候选集合如何被确定化
- **Given**：运行 `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`
- **Then**：
  - 能指出候选收集入口与最终确定化入口（核心方法名）
  - 能解释 `@Qualifier` 与 `@Primary` 的差异与适用场景

### Requirement: 创建-注入-初始化三阶段可见（含 BPP 介入点）
**Module:** `spring-core-beans`

目标：能把实例创建拆成 instantiate → populate → initialize，并理解 `InstantiationAwareBeanPostProcessor` / `BeanPostProcessor` 分别介入在哪些点、能做什么（含“最终暴露对象可被替换为 proxy”）。

<a id="scenario-bean-creation-trace-and-bpp-hooks"></a>
#### Scenario: 对照事件序列验证 doCreateBean 的阶段划分
- **Given**：运行 `SpringCoreBeansBeanCreationTraceLabTest`
- **Then**：
  - 能把 test 中 events 序列映射到 Spring 源码中的关键方法
  - 能解释为什么 `getBean(Interface.class)` 能拿到 proxy，但按原始类类型 getBean 会失败

### Requirement: 代理创建点与“最终暴露对象”差异可定位
**Module:** `spring-core-beans`

目标：理解一个 Bean 可能在多个阶段被包装/替换为代理，并能定位发生点：before instantiation / early reference / after initialization。

<a id="scenario-proxy-replacement-three-places"></a>
#### Scenario: 定位“到底哪里被换成 proxy”的三个切入点
- **Given**：运行 `SpringCoreBeansProxyingPhaseLabTest`
- **Then**：
  - 能列出三个最常见的代理产生位置（对应方法名）
  - 能解释为什么 self-invocation 仍可能绕过代理

### Requirement: 循环依赖与 early reference（三级缓存）可解释
**Module:** `spring-core-beans`

目标：理解循环依赖不同形态（构造器 vs setter/field）为什么结果不同；理解三级缓存的目的与代价（尤其是 proxy 参与时）。

<a id="scenario-circular-deps-3-level-cache"></a>
#### Scenario: setter 循环依赖为何“有时能过”
- **Given**：运行 `SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`
- **Then**：
  - 能说明三级缓存分别存什么、什么时候命中
  - 能解释构造器循环依赖为何通常 fail-fast

### Requirement: @Configuration(proxyBeanMethods) 语义可解释（增强器）
**Module:** `spring-core-beans`

目标：理解 `proxyBeanMethods=true/false` 对 `@Bean` 方法调用语义的影响，并能定位到增强器与拦截器的工作点。

<a id="scenario-configuration-enhancement"></a>
#### Scenario: 对照两个 config 的差异解释“单例语义为何保留/丢失”
- **Given**：运行 `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsTruePreservesSingletonSemanticsForBeanMethodCalls` 与对应 false 用例
- **Then**：
  - 能解释 enhancer 的目的：把 `@Bean` 方法调用路由到容器 `getBean(...)`
  - 能指出关键增强类/关键拦截器

### Requirement: FactoryBean（product vs factory）语义可解释
**Module:** `spring-core-beans`

目标：解释 `FactoryBean` 的两类对象（product/factory）与 `&` 前缀语义，及其缓存/调用时机差异。

<a id="scenario-factorybean-semantics"></a>
#### Scenario: 解释为什么同名 getBean() 拿到的是 product
- **Given**：运行 `SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory`
- **Then**：
  - 能指出工厂对象与产品对象的分流入口（核心方法/条件）
  - 能解释 `isSingleton()` 对缓存语义的影响

## Risk Assessment
- **Risk:** 源码解析内容如果只给“概念描述”而缺少“观察点”，学习会退化成二手知识  
  **Mitigation:** 每个知识点都绑定一个 Lab + 指定断点入口 + 指定应观察的状态（缓存/候选集合/调用链）
- **Risk:** Spring 版本升级导致部分内部方法名调整  
  **Mitigation:** 优先围绕稳定主线（refresh/doCreateBean/resolveDependency）与可断言行为，不对细枝末节做强耦合
