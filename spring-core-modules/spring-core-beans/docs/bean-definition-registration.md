# BeanDefinition 注册：Bean 如何进入容器

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 BeanDefinition 从不同配置输入进入容器的路径。
    - 覆盖 XML、component scan、`@Bean`、`@Import`、条件注册、编程式注册和 Boot 自动配置。
    - 重点是注册时机、注册表、是否立即创建实例，以及排障时应该观察什么。

    观察对象：BeanDefinition 来源、注册时机和注册后留下的元数据。
    主线位置：refresh 前半段，普通 Bean 实例化之前。
    对照入口：`SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`、`SpringCoreBeansComponentScanLabTest`、`SpringCoreBeansImportLabTest`、`SpringCoreBeansProgrammaticRegistrationLabTest`、`SpringCoreBeansAutoConfigurationLabTest`。
<!-- CHAPTER-CARD:END -->

Bean 要被容器管理，第一步通常不是创建对象，而是把一条 BeanDefinition 放进 `BeanDefinitionRegistry`。这条定义描述“将来如何创建和管理对象”，不等于对象已经存在。只有少数路径，例如 `registerSingleton`，会绕过定义层直接放入一个既有实例。

排查“类存在但没有 Bean”时，应该先看注册链路，而不是先看构造器。类可能没有被扫描到，`@Bean` 方法可能没有被配置类解析，`@Import` 可能没有生效，条件可能不匹配，Boot 自动配置可能退让，或者实例是通过 `registerSingleton` 放进去而没有 BeanDefinition。

## 注册入口对照

| 来源 | 何时产生定义 | 注册主体 | 是否立即创建实例 | 关键诊断 |
| --- | --- | --- | --- | --- |
| XML | reader 读取资源并解析 `<bean>` 时 | `BeanDefinitionReader` 写入 registry | 否 | resource、bean class、factory method、parent、property |
| component scan | 配置类处理器解析扫描规则时 | scanner 注册扫描到的候选 | 否 | base package、include/exclude filter、默认 bean name |
| `@Bean` | 配置类被解析成 bean method metadata 时 | `ConfigurationClassPostProcessor` 相关流程 | 否 | `factoryBeanName`、`factoryMethodName` |
| `@Import` | 配置类解析 import 链时 | imported config、selector 或 registrar | 否 | import 来源、环境属性、registrar 写入的定义 |
| 条件注册 | 注册候选进入 registry 前判断 | condition evaluator | 否；不匹配通常没有定义 | condition outcome、environment、classpath、已有候选 |
| 编程式注册 | 用户代码调用 registry/context API 时 | `registerBeanDefinition`、`registerBean` 等 | 定义层注册否；singleton 注册是既有实例 | 是否有 BeanDefinition、是否参与注入和 BPP |
| Boot 自动配置 | 自动配置类被导入并解析时 | Boot import + 配置类处理流程 | 否 | auto-config 是否导入、condition、backoff |

## XML：外部资源变成定义

XML 的本质是 BeanDefinition 输入。XML reader 从 `Resource` 读取文档，把 `<bean>` 的 class、scope、constructor-arg、property、init-method、destroy-method 等信息解析为定义，再注册到 registry。这个阶段不会因为 `<bean>` 存在就立刻创建业务对象；对象创建仍由后续 `getBean()` 或 singleton 预实例化触发。

XML 排障的第一观察点是资源是否被加载，其次才是定义内容。若资源没进入 reader，后面不会有 BeanDefinition，也不会有候选选择或生命周期问题。

## Component Scan：扫描候选不等于实例化

component scan 先在指定 base package 下找 stereotype 注解候选，再把候选类包装成 BeanDefinition。`SpringCoreBeansComponentScanLabTest` 展示了默认命名和显式命名都能注册 Bean，也展示了 exclude filter 可以让一个已注解的类完全不进入容器。

所以“类上有 `@Component` 但没有 Bean”的常见原因是扫描边界或过滤规则，而不是类本身不能实例化。只要定义没注册，后续构造器、依赖注入、初始化都不会发生。

## `@Bean`：方法成为工厂元数据

`@Bean` 方法不是简单地“调用一次方法”。配置类解析阶段会为该方法注册 BeanDefinition，定义中保留 `factoryBeanName` 和 `factoryMethodName`。后续创建 Bean 时，容器根据这些元数据调用工厂方法取得 raw instance。

`SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` 对比了扫描类和 `@Bean` 方法：扫描注册的定义通常有 bean class 且 `factoryMethodName` 为空；`@Bean` 方法注册的定义能观察到具体 factory method。这是判断 Bean 来源的重要证据。

## `@Import`：把额外定义并入注册主线

`@Import` 有三种常见形态。直接 import 配置类时，被 import 的配置类继续参与配置类解析，它的 `@Bean` 方法会变成定义。`ImportSelector` 根据 `Environment` 或注解元数据返回要导入的类名。`ImportBeanDefinitionRegistrar` 则可以直接拿到 `BeanDefinitionRegistry` 并编程式写入定义。

`SpringCoreBeansImportLabTest` 分别覆盖了这三类行为：普通 import 带来额外配置，selector 根据 `demo.selector.mode` 选择配置，registrar 把构造参数写进 `registeredMessage` 的 BeanDefinition。

## 条件注册：不匹配通常意味着没有定义

条件不是“创建时再决定要不要实例化”的通用开关。对配置类和 `@Bean` 方法而言，条件判断发生在定义注册前后非常早的阶段；不匹配时通常 BeanDefinition 根本不会进入 registry。

`SpringCoreBeansAutoConfigurationLabTest` 中，缺少 `demo.feature.enabled=true` 时 `PropertyGatedBean` 不存在；classpath 被过滤时 `ClasspathGatedBean` 不存在；用户提供 `DemoGreeting` 时，`@ConditionalOnMissingBean` 让自动配置默认 Bean 退让。排障时应先看条件报告、环境属性、classpath 和已有 BeanDefinition，而不是追单 Bean 创建。

## 编程式注册：定义层和实例层要分开

`registerBeanDefinition` 和 `GenericApplicationContext#registerBean` 都属于定义层注册。它们把“如何创建对象”交给容器，因此后续能参与依赖注入、初始化回调和 BeanPostProcessor。

`registerSingleton` 是实例层注册。它把一个已经存在的对象放进 singleton 缓存，不会 retroactive 地补做依赖注入、初始化或 BPP 包装。`SpringCoreBeansProgrammaticRegistrationLabTest` 用同一个 `Target` 对比了这点：定义层注册的对象能被注入并被 BPP 标记；`registerSingleton` 放入的既有实例不会自动获得这些处理。

## Boot 自动配置：也是定义注册，不是魔法创建

Boot 自动配置最终仍回到配置类解析和 BeanDefinition 注册模型。自动配置类被导入后，其中的 `@Bean` 方法、条件注解和排序规则决定哪些默认定义进入 registry。`@ConditionalOnMissingBean` 的退让结果是默认定义不注册或不成为候选，而不是先创建再删除。

因此 Boot 下的 Bean 问题要同时观察自动配置是否被导入、条件是否匹配、用户定义是否已经足够早地出现，以及 backoff 判断看到的是哪些 BeanDefinition。

## 最短诊断顺序

当一个 Bean “应该存在但不存在”时，按下面顺序查：

1. registry 里有没有 BeanDefinition；如果没有，问题在注册阶段。
2. 如果是扫描来源，查 base package、filter 和 bean name。
3. 如果是 `@Bean` 来源，查配置类是否被解析，以及 factory method 元数据。
4. 如果是 import 来源，查 import selector 或 registrar 是否执行。
5. 如果是条件来源，查 environment、classpath、profile、已有候选和 condition outcome。
6. 如果只有 singleton 实例没有 BeanDefinition，确认是否使用了 `registerSingleton`。

这能把“创建失败”和“根本没注册”分开，避免在错误阶段排障。
