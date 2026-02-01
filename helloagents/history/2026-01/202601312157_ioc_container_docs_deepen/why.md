# Change Proposal: part-01-ioc-container 文档深度完善

## Requirement Background

`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/` 作为 IoC 容器入门到进阶的第一部分，已经覆盖了 Bean 心智模型、Bean 注册、依赖注入解析、作用域/生命周期、后置处理器、配置类增强、FactoryBean、循环依赖等核心主题。

当前痛点主要集中在：

1. **深度不均衡**：章节之间在“概念 → 源码主线 → 可复现实验 → 排错闭环”的深度一致性不足（个别章节缺少可运行实验或缺少源码主线定位）。
2. **未完内容存在**：少数章节仍包含“未完”标记，导致读者在关键点上断档。
3. **跨章链路需要更强的“闭环感”**：例如依赖解析 ↔ 作用域 ↔ 生命周期 ↔ 后置处理器 ↔ 配置类增强 ↔ FactoryBean ↔ 循环依赖之间的互相影响，需要在章节内给出更明确的“这一章解决什么、下一章接什么、遇到问题怎么定位”。

本次变更聚焦于：**逐章阅读后，按章节内容画像进行补充、完善与深入**，避免“固定补模板模块”的机械扩写。

## Change Content

1. **按章节画像深化**：每一章的补强点都由该章现有内容与主题决定，强调“本章关键分叉点/边界条件/调试抓手”。
2. **补齐未完与边界**：清理“未完”占位，补齐关键边界条件（例如循环依赖的可解/不可解边界、后置处理器注册时序的关键分界）。
3. **把“可运行实验”作为深度锚点（优先复用现有 Lab）**：优先引用 `spring-core-modules/spring-core-beans/src/test/java/.../part01_ioc_container/*LabTest.java` 等现有测试，作为每章“可复现、可调试”的抓手。
4. **跨章知识链路显式化**：在每章给出必要的“与其它章节互相影响点”的跳转提示（不追求统一模板，而是按主题最短路径补齐）。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: 020-01 Bean 心智模型（读者建立“容器的世界观”）
**Module:** spring-core-beans docs
本章要让读者获得“容器里发生了什么”的稳定心智模型：BeanDefinition/BeanFactory/ApplicationContext 的分工、Bean 创建主线、扩展点在主线上的位置、以及常见异常的分类方式。

#### Scenario: 读者能把问题定位到主线阶段
在看到“注入冲突、生命周期顺序异常、AOP 早期引用导致行为差异”等问题时：
- 能说清它大概发生在 `refresh`/`getBean`/`doCreateBean` 的哪个阶段；
- 能知道下一步该去哪个章节或哪个 Lab 用断点验证。

### Requirement: 02 Bean 注册（读者理解“BeanDefinition 从哪里来”）
**Module:** spring-core-beans docs
本章要让读者把 XML/@ComponentScan/@Bean/@Import/编程式注册等“入口”放到同一张图里，理解它们最终如何落到 BeanDefinitionRegistry，以及与后置处理器的关系。

#### Scenario: 读者能选择合适的注册方式并解释代价
- 能说清同样的 Bean 注册需求，采用不同入口的适用场景与限制；
- 能解释“注册发生的时点”为什么会影响后续（例如后置处理器、条件装配、配置类增强）。

### Requirement: 014-03 依赖注入解析（读者掌握“候选人怎么选出来”）
**Module:** spring-core-beans docs
本章要让读者能完整解释 `resolveDependency` 的分支决策：候选集合如何收集、如何过滤、如何在歧义时落到 @Primary/@Qualifier/优先级/名称等规则。

#### Scenario: 读者能把注入歧义变成可调试的决策树
- 给定一个注入点（字段/构造器参数/方法参数），能列出可能的候选集合；
- 能解释每一步过滤依据，并能通过 Lab/断点验证推理。

### Requirement: 015-04 Scope 与 Prototype（读者理解“生命周期边界”）
**Module:** spring-core-beans docs
本章要让读者理解 scope 不是“一个注解”，而是影响创建、缓存、注入语义与销毁边界的一组规则；特别是 prototype 注入 singleton、scoped proxy、以及 destroy 回调的边界。

#### Scenario: 读者能解释 prototype 注入 singleton 的真实语义
- 明确什么情况下会“看起来像 prototype 但其实不是每次新建”；  
- 明确 destroy 回调为什么不一定发生，以及如何正确管理资源。

### Requirement: 016-05 生命周期与回调（读者掌握“生命周期顺序与插槽”）
**Module:** spring-core-beans docs
本章要让读者把 @PostConstruct/InitializingBean/init-method/BeanPostProcessor/SmartInitializingSingleton/SmartLifecycle 等放到一条有序主线里，理解“为什么这个回调先/后发生”。

#### Scenario: 读者能定位并修复生命周期顺序问题
- 能解释常见“回调顺序不符合预期”的原因（尤其是代理/早期引用/后置处理器介入导致的差异）；  
- 能用断点与日志把问题定位到具体插槽。

### Requirement: 017-06 后置处理器（读者理解“容器可编程能力的核心”）
**Module:** spring-core-beans docs
本章要让读者建立 BFPP/BDRPP/BPP/InstantiationAware* 等类型的分类与时序模型，理解它们在 refresh 主线上的注册点与执行点。

#### Scenario: 读者能解释“为什么某个后置处理器没生效/生效太晚”
- 能识别是“注册时点问题”还是“排序问题”还是“过早 getBean 导致错过”；  
- 能把问题落到可验证的最小复现。

### Requirement: 018-07 配置类增强（读者理解“@Configuration 的代理语义”）
**Module:** spring-core-beans docs
本章要让读者理解 @Configuration 的 full/lite 模式、`proxyBeanMethods` 的语义与性能权衡，以及它与循环依赖、Bean 方法调用、代理对象的关联。

#### Scenario: 读者能正确选择 proxyBeanMethods 并避免语义坑
- 能解释 `proxyBeanMethods=false` 的收益与风险；  
- 能识别“跨 @Bean 方法调用导致的单例语义变化”并提供替代写法。

### Requirement: 08 FactoryBean（读者理解“工厂产物 vs 工厂本身”）
**Module:** spring-core-beans docs
本章要让读者把 FactoryBean 放到容器主线与依赖解析体系里：`&` 前缀、产物缓存、`getObjectType/isSingleton` 的关键语义，以及与循环依赖/AOP 的交互边界。

#### Scenario: 读者能解释拿到的是 FactoryBean 还是它生产的对象
- 遇到类型不匹配/提前初始化/循环依赖相关异常时，能判断是“FactoryBean 本身”还是“产物对象”的问题。

### Requirement: 09 循环依赖（读者掌握“三级缓存与早期引用边界”）
**Module:** spring-core-beans docs
本章要让读者理解循环依赖可解/不可解的边界：构造器注入 vs setter、prototype vs singleton、AOP 早期代理如何参与、以及 `allowCircularReferences/allowRawInjectionDespiteWrapping` 等开关的真实含义。

#### Scenario: 读者能给出可落地的解环方案
- 能把“现象”还原成容器内部的缓存/时序问题；  
- 能给出 2-3 个可落地改造路径（不局限于 @Lazy），并说明权衡。

## Risk Assessment

- **Risk:** 章节补充可能导致锚点变化、引用链接失效、风格漂移
- **Mitigation:** 尽量保留原有标题结构与锚点；新增内容以“补充段落/补充分节”为主；执行 Markdown 链接与锚点自检（见 task.md）

