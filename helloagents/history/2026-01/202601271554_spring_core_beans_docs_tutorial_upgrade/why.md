# Change Proposal: spring-core-beans 教程体系重构（源码进阶 / 面试 / 团队内训）

## Requirement Background

`spring-core-beans` 目前已经具备大量 docs 与配套 Labs/Exercises，但整体仍存在“学习者拿到后不知道怎么学、学完能解决什么问题、遇到现象不知道去哪下断点/怎么验证”的问题，导致：

- 对“源码进阶”读者：缺少可复用的主线叙事与分支决策套路，难以把概念落到可观察对象与调用链。
- 对“面试准备”读者：缺少结构化复述模板（以证据链为核心），无法把知识点组织成可答题的“主线 + 边界 + 反例”。
- 对“团队内训”场景：缺少可执行的教学脚本（按课时拆分）、统一的章节模板与维护机制，难以标准化交付。

本变更希望将该模块从“资料堆叠”升级为“可学习的课程体系”：明确学习路线与验收标准，统一章节结构，把每个主题落到 **实验入口（可运行）+ 断点观察点（可复用）+ 排障分流（可落地）+ 面试复述（可输出）**。

## Change Content

1. 重写模块入口与路线图：README / docs TOC 变成“课程入口”，提供 30 分钟快启 + 三条阅读路线 + 固定断点策略。
2. 以“主题→章节→Lab→断点→观察点”建立索引与跳读地图（可用于排障与面试复盘）。
3. 对核心主题进行“源码级”补强：每个主题至少包含主线、关键分支、反例/边界、可复现 Lab、面试问法。
4. 对内训场景补齐：按课时拆分的教学建议（含练习/讨论题/常见误区）。
5. 统一章节模板与维护规则：导航、格式、一句话自检、链接稳定性，降低后续增量维护成本。

## Impact Scope

- **Modules:**
  - `spring-core-modules/spring-core-beans`
  - `helloagents/wiki`（知识库同步）
- **Files:**
  - `spring-core-modules/spring-core-beans/README.md`
  - `spring-core-modules/spring-core-beans/docs/**`
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: R1-entry-learning-path
**Module:** spring-core-beans
把 README 与 docs/README 建成“可执行的课程入口”。

#### Scenario: S1-start-here-and-routes
面向三类读者（源码/面试/内训）提供明确入口与学习路线。
- 预期结果：读者 5 分钟内能开始跑第一个 Lab，并知道后续按哪条路线读。
- 预期结果：每条路线都有清晰的“学完能做到什么”的验收描述。

### Requirement: R2-beandefinition-and-registration
**Module:** spring-core-beans
讲清 BeanDefinition 体系与注册入口（scan / @Bean / @Import / registrar / programmatic）。

#### Scenario: S1-definition-vs-instance
读者能明确区分“定义层”与“实例层”，并能用断点证明。
- 预期结果：能解释 `registerBeanDefinition` vs `registerSingleton` 的根本差异。
- 预期结果：能定位“注册时机”对后处理器可见性的影响。

### Requirement: R3-di-resolution-and-candidate-selection
**Module:** spring-core-beans
讲清 DI 解析与候选选择：类型/名称/Qualifier/Primary/泛型匹配/集合注入。

#### Scenario: S1-ambiguity-and-qualifier
遇到多实现注入歧义时，能解释容器的决策顺序，并给出可复现证据链。
- 预期结果：能把“现象→关键方法→关键变量→结论”串成 1 分钟排障套路。

### Requirement: R4-lifecycle-and-callbacks
**Module:** spring-core-beans
讲清生命周期：实例化→属性填充→初始化→销毁，以及各种回调触发顺序与边界。

#### Scenario: S1-lifecycle-order
读者能说清并验证回调顺序，并能解释 prototype 销毁语义与常见误区。
- 预期结果：能用 1 个 Lab/断点把回调顺序“看见”。

### Requirement: R5-bfpp-bpp-and-extension-points
**Module:** spring-core-beans
讲清 BFPP/BPP/BDRPP 等扩展点：能做什么、不能做什么、在 refresh 的哪一段发生。

#### Scenario: S1-post-processor-timeline
把“处理器”从名词变成可观察对象：时机、顺序、对 BeanDefinition/bean instance 的影响。
- 预期结果：能解释“为什么代理会改变最终暴露对象”以及常见反例。

### Requirement: R6-circular-dependency-boundary
**Module:** spring-core-beans
讲清循环依赖：构造器为何失败、setter 为何有时成功、early reference 在哪里生效、边界与规避。

#### Scenario: S1-early-reference-and-proxy
读者能解释 `getEarlyBeanReference` 的作用与限制，并能判断是否会触发“raw injection despite wrapping”等边界问题。
- 预期结果：能把循环依赖问题分型并定位到正确断点。

### Requirement: R7-factorybean-and-product-vs-factory
**Module:** spring-core-beans
讲清 FactoryBean：product vs factory、`&` 前缀、类型匹配、缓存语义与边界。

#### Scenario: S1-factorybean-debugging
读者遇到“按类型找不到/类型不匹配/拿到的是 factory 还是 product”能快速排障。
- 预期结果：能用断点与断言证明 `FactoryBean#getObjectType` 的影响。

### Requirement: R8-configuration-enhancement-and-proxying
**Module:** spring-core-beans
讲清配置类增强与代理：`@Configuration`、CGLIB 增强、`proxyBeanMethods` 与 `@Bean` 语义、BPP 替换阶段。

#### Scenario: S1-proxying-phase
读者能解释“代理在什么时候产生、替换了什么对象、为什么影响注入与类型匹配”。
- 预期结果：能把代理相关问题定位到“实例化前短路 / 初始化后包装”等阶段。

### Requirement: R9-scope-and-scoped-proxy
**Module:** spring-core-beans
讲清 Scope 与 scoped proxy：singleton/prototype、自定义 scope、注入陷阱与修复方式。

#### Scenario: S1-prototype-in-singleton
读者能解释并修复“prototype 注入 singleton 看起来像单例”的现象。
- 预期结果：能比较 ObjectProvider/@Lookup/scoped proxy 等方案的边界。

### Requirement: R10-value-spel-type-conversion
**Module:** spring-core-beans
讲清值解析相关机制：`@Value("${...}")`、SpEL、类型转换（BeanWrapper/ConversionService/PropertyEditor）与属性绑定边界。

#### Scenario: S1-placeholder-and-conversion
读者遇到“占位符解析失败/类型转换失败/值解析走错链路”能快速定位阶段与入口。
- 预期结果：每类问题都有最短排障路径（章节 + 断点 + Lab）。

### Requirement: R11-interview-and-training-kit
**Module:** spring-core-beans
补齐面试与内训交付：复述模板、必问点、常见误区、课时安排与练习建议。

#### Scenario: S1-interview-output
读者能用“主线 + 分支 + 证据链”方式复述 Spring Beans 关键机制。
- 预期结果：每个主题至少提供 3 个高频问法 + 1 个反例/边界题。

## Risk Assessment

- **Risk:** 变更范围大，容易“越改越散”或长期拖延  
  **Mitigation:** 采用分阶段任务拆解（每任务≤2文件），阶段性验收（入口/索引/主题逐个闭环）。
- **Risk:** 文档链路被改断（内链、路径、导航失效）  
  **Mitigation:** 每阶段执行链接巡检与 TOC 校验；优先保证入口与导航稳定。
- **Risk:** 新增/调整实验导致回归测试波动  
  **Mitigation:** 默认回归保持稳定，Explore/Debug 用例保持可选开关；每阶段跑 `:spring-core-beans` 全量测试。

