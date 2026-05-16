# BeanDefinition 元数据与来源：容器决策的输入

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 BeanDefinition 元数据如何影响注册、候选选择、实例化、初始化、销毁和排障。
    - 覆盖 scope、lazy、primary、fallback/default candidate、autowire candidate、qualifier、role、source/origin、factory method、init/destroy methods 和 depends-on。
    - 核心结论：元数据不是注释，而是容器后续决策的输入。

    观察对象：BeanDefinition flags、来源信息和阶段影响。
    主线位置：定义注册之后，候选选择、创建和销毁之前。
    对照入口：`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`、`SpringCoreBeansBeanDefinitionOriginLabTest`、`SpringCoreBeansDependsOnLabTest`。
<!-- CHAPTER-CARD:END -->

BeanDefinition 的价值不只是记录“用哪个 class 创建 Bean”。它保存了大量会被容器反复读取的元数据：是否 singleton、是否 lazy、是否能自动装配、是否 primary、来自哪个资源、通过哪个工厂方法创建、初始化和销毁时要调用什么，以及是否必须先创建其他 Bean。

这些信息会在不同阶段发挥作用。排障时如果只看最终对象，就会错过容器为什么选择它、跳过它、提前创建它或按某个顺序销毁它的原因。

## 元数据影响阶段

| 元数据 | 主要影响阶段 | 作用 |
| --- | --- | --- |
| scope | 创建与缓存 | 决定对象复用边界，例如 singleton 缓存或 prototype 每次创建 |
| lazy | refresh 预实例化、依赖解析 | 控制非懒 singleton 是否启动期创建；被依赖或 depends-on 时仍可能提前创建 |
| primary | 单值候选选择 | 多个类型候选中优先选择 primary |
| fallback/default candidate | 候选选择和默认退让 | 表达普通候选不足时的兜底或默认候选语义 |
| autowire candidate | 候选过滤 | 为 false 时 Bean 仍存在，但不会作为自动装配候选 |
| qualifier | 候选过滤 | 与注入点 qualifier 匹配，缩小候选集合 |
| role | 观测与工具分类 | 区分应用 Bean、支持 Bean、基础设施 Bean |
| source/origin/resource | 排障溯源 | 回答定义从配置类、XML、registrar、自动配置还是手工注册而来 |
| factory method | 实例化 | 指示容器调用哪个工厂 Bean 和方法取得 raw instance |
| init/destroy methods | 初始化与销毁 | 决定生命周期阶段额外调用的方法 |
| depends-on | 创建与销毁顺序 | 强制先创建依赖 Bean，并在销毁时反向处理 |

## 候选选择：存在不代表可注入

`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` 展示了三个重要事实。

第一，`BeanDefinition#setPrimary(true)` 即使没有 `@Primary` 注解，也会参与单值自动装配选择。容器看的是定义元数据，不是只看源码注解。

第二，`autowireCandidate=false` 的 Bean 仍然可以通过名称或类型显式获取，但在自动装配候选匹配中会被忽略。所以“容器里有这个 Bean”不能推出“它能被 `@Autowired` 选中”。

第三，qualifier 可以直接存在于 BeanDefinition 的 qualifier metadata 中，并被 `QualifierAnnotationAutowireCandidateResolver` 用来匹配注入点。这解释了为什么某些框架生成的 Bean 没有源码注解，却能按 qualifier 注入。

`primary`、`fallback`、`default candidate` 和 qualifier 的共同点是：它们控制候选集合如何收敛。它们不负责创建对象，也不保证对象已经初始化。

## 创建阶段：scope、lazy、factory method

scope 决定容器是否缓存创建结果。singleton 的最终暴露对象进入 singleton 缓存；prototype 每次请求重新创建，容器通常不负责完整销毁；Web scope 还会把对象复用边界交给请求或会话上下文。

lazy 主要影响 ApplicationContext refresh 末尾的非懒 singleton 预实例化。标记为 lazy 的 singleton 不会因为 refresh 的预实例化步骤而创建，但只要它被其他 Bean 依赖、被显式 `getBean()`、或被 `depends-on` 强制要求，仍然会创建。

factory method 元数据决定 raw instance 的来源。`SpringCoreBeansBeanDefinitionOriginLabTest` 中，自动配置和用户配置的 `DemoService` 都来自 `@Bean` 方法，因此能观察到 `factoryMethodName` 和 `factoryBeanName`；手工 `withBean("manualBean", ManualBean.class, ManualBean::new)` 注册的定义则更接近直接 class/supplier 来源。

## 初始化和销毁：init、destroy、depends-on

init method 和 destroy method 是生命周期阶段的调用指令。它们不改变候选选择，但会影响对象完成初始化的边界，以及容器关闭时要执行的清理动作。

`depends-on` 表达的是顺序，不是注入关系。`SpringCoreBeansDependsOnLabTest` 证明 `second` 可以在没有字段引用 `first` 的情况下强制 `first` 先构造；也证明循环 depends-on 会 fail fast。

`depends-on` 还会突破 lazy。Lab 中 `lazyDependency` 虽然是 lazy，但 `dependent` 声明 depends-on 后，refresh 创建 `dependent` 前会先触发 `lazyDependency` 创建。销毁时则按依赖边反向执行：dependent 先销毁，dependency 后销毁。

## role 与基础设施识别

`role` 不直接决定业务注入结果，但对观测非常重要。应用 Bean 通常是 `ROLE_APPLICATION`，容器处理器、自动代理创建器、配置类处理器等多为 `ROLE_INFRASTRUCTURE`。当 Bean 列表很长时，先按 role 分层可以快速区分“业务对象”和“让容器能力生效的对象”。

不要把 role 当成安全边界。它主要服务工具、诊断和可读性；真正参与创建或候选选择的仍是其他元数据和后处理器行为。

## source/origin：生产排障先问从哪里来

同一个类型的 Bean 可能来自用户配置、自动配置、扫描、XML、registrar 或测试注册。来源不同，修复方式完全不同。`SpringCoreBeansBeanDefinitionOriginLabTest` 使用 dumper 对比了 auto-config、user config 和 manual bean，说明 BeanDefinition 能回答“谁注册了这个 Bean”。

生产排障中，先确认 origin 可以避免误删配置。例如一个默认 Bean 没有出现，可能不是创建失败，而是 `@ConditionalOnMissingBean` 因为用户 Bean 已存在而退让；一个 Bean 名称存在但没有定义，可能是 `registerSingleton` 放入的既有实例。

## 快速判断表

| 现象 | 优先观察的元数据 |
| --- | --- |
| Bean 存在但注入不到 | `autowireCandidate`、qualifier、primary、fallback/default candidate |
| 启动时被提前创建 | lazy、depends-on、非懒 singleton、被其他 Bean 依赖 |
| 拿到的对象来自工厂方法 | `factoryBeanName`、`factoryMethodName`、source |
| 销毁顺序不符合预期 | dependent bean map、depends-on、destroy method |
| Bean 列表里混入大量框架对象 | role、source、resource description |

元数据是容器的决策输入。理解它们分别在哪个阶段被读取，比记住某个注解名称更有排障价值。
