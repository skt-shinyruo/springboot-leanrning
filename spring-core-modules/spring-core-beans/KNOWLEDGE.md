# Spring Bean 知识点深度清单

本文是 Spring Bean 的独立总览文档，目标是把 Bean 相关知识点按容器主线串起来，而不是只罗列注解。阅读时抓住一条核心线：

```text
配置输入 -> BeanDefinition -> BeanFactory 后处理 -> Bean 实例化
-> 依赖注入 -> Aware 回调 -> 初始化 -> BeanPostProcessor 包装
-> 最终暴露对象 -> 销毁
```

Spring Bean 的本质不是某个 Java 类，而是由 Spring 容器根据 `BeanDefinition` 管理的一组对象语义：如何定义、何时创建、如何注入、怎样初始化、是否代理、以什么作用域暴露、什么时候销毁。

## 本文定位与读法

`KNOWLEDGE.md` 是模块级知识总览，放在模块根目录，和 `README.md` 并列。它的职责不是替代 `docs/` 下的专题文档，而是把这个模块的知识面、主线顺序和排障坐标压到一页里。

建议按这几个层次读：

- `README.md`：入口、目录、最短命令和模块边界。
- `KNOWLEDGE.md`：建立完整知识地图，知道 Bean 机制有哪些关键点。
- `docs/appendix-knowledge-map.md`：按问题定位唯一 owner 文档。
- `docs/guide-breakpoint-map.md`：需要调源码或跑 Lab 时找断点。
- `docs/*.md`：专题深挖，不在总览里重复完整机制。

最短验证入口：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test
```

## 知识点导航

| 层级 | 需要掌握的问题 | 本文入口 | 深挖文档 |
| --- | --- | --- | --- |
| 定义层 | BeanDefinition 从哪里来、元数据如何影响后续创建和候选选择 | Bean 的本质、容器核心抽象、Bean 的来源 | `docs/bean-mental-model.md`、`docs/bean-definition-registration.md`、`docs/bean-definition-metadata-and-origin.md` |
| 容器层 | BeanFactory、ApplicationContext、refresh 主线如何协作 | 容器核心抽象、容器启动与 refresh 主线 | `docs/beanfactory-vs-applicationcontext.md`、`docs/beanfactory-api-and-autowirecapablebeanfactory.md`、`docs/refresh-mainline.md`、`docs/container-bootstrap-and-infrastructure.md` |
| 创建层 | 什么时候实例化、如何选构造器、哪里可能被短路 | Bean 创建主线、实例化方式 | `docs/bean-creation-mainline.md`、`docs/pre-instantiation-short-circuit.md` |
| 注入层 | 注入点如何提出需求，候选如何收集、过滤、收敛 | 依赖注入方式、自动装配候选选择 | `docs/dependency-injection-resolution.md`、`docs/dependency-descriptor-and-injection-point.md`、`docs/autowire-candidate-selection.md`、`docs/optional-and-provider-injection.md`、`docs/resource-vs-autowired.md` |
| 生命周期层 | Aware、init、destroy、Smart 回调如何排序 | 生命周期回调、初始化和销毁边界 | `docs/lifecycle-callbacks.md`、`docs/smart-initializing-singleton.md`、`docs/smart-lifecycle.md` |
| 作用域层 | singleton、prototype、Web scope、自定义 scope 的复用边界 | Bean 作用域、Lazy 语义 | `docs/scope-and-prototype.md`、`docs/custom-scope-and-scoped-proxy.md`、`docs/lazy-semantics.md` |
| 扩展层 | BFPP、BDRPP、BPP、FactoryBean 如何扩展容器 | 后处理器体系、FactoryBean、编程式注册 | `docs/post-processors-overview.md`、`docs/beanpost-processors.md`、`docs/factorybean.md` |
| 暴露层 | 调用方拿到的是原始对象、early reference、FactoryBean product 还是 proxy | 循环依赖、三级缓存、代理与 AOP | `docs/early-reference-and-three-level-cache.md`、`docs/proxying-phase.md`、`docs/factorybean-type-matching.md` |
| 外部输入层 | XML、Properties、Groovy、namespace、值解析如何进入定义和属性 | Bean 的来源、值解析与类型转换 | `docs/xml-bean-definition-reader.md`、`docs/properties-and-groovy-reader.md`、`docs/xml-namespace-extension.md` |
| Boot / AOT 层 | 自动配置和 Native 构建如何改变 Bean 的注册与推断 | Spring Boot 自动配置中的 Bean、AOT 与 Native | `docs/boot-auto-configuration-beans.md`、`docs/aot-native-overview.md` |

## 1. Bean 的本质

Bean 是由 Spring IoC 容器托管的对象。托管意味着容器掌握它的定义、创建、依赖关系、生命周期回调、作用域和最终暴露形式。

普通对象通常由业务代码直接 `new` 出来。Spring Bean 不是简单的 `new`，而是经过容器创建流程产生的对象。这个流程里会有构造器选择、依赖解析、属性填充、生命周期回调、后处理器增强、AOP 代理等步骤。

理解 Bean 时要区分四个概念：

| 概念 | 含义 | 排障价值 |
| --- | --- | --- |
| Java class | 类型本身 | 判断对象能否被实例化、能否被代理 |
| BeanDefinition | Bean 的元数据 | 判断 Bean 是否被注册、scope 是什么、是否 lazy、是否 primary |
| Bean instance | 原始实例 | 判断构造器、属性填充、初始化是否执行 |
| Exposed object | 容器最终暴露对象 | 判断拿到的是原始对象、FactoryBean 产品、early reference 还是 proxy |

很多 Spring 问题的根源，是把这四层混为一谈。例如 `getBean()` 拿到的对象未必是构造器创建出来的原始实例，它可能已经被 `BeanPostProcessor` 包装成代理。

## 2. 容器核心抽象

### BeanFactory

`BeanFactory` 是最基础的 IoC 容器接口，负责 Bean 的注册信息管理、获取、创建和依赖解析。它强调按需创建，只有当 Bean 被请求或预实例化时才真正创建对象。

核心能力包括：

- 根据名称或类型获取 Bean。
- 判断 Bean 是否存在。
- 判断 Bean 类型、作用域、别名。
- 解析依赖并触发 Bean 创建。

### ApplicationContext

`ApplicationContext` 是更完整的应用级容器，建立在 `BeanFactory` 之上。它不仅提供 Bean 管理，还整合了事件、国际化、资源加载、环境配置、自动刷新等能力。

常见增强能力：

- `Environment` 和 `PropertySource`。
- `ApplicationEventPublisher` 事件机制。
- `ResourceLoader` 资源加载。
- `MessageSource` 国际化。
- 自动注册并执行各种后处理器。
- 默认预实例化非懒加载 singleton。

实际 Spring Boot 应用中使用的通常是 `ApplicationContext`，但真正负责 Bean 创建细节的核心仍然是内部的 `BeanFactory`，常见实现是 `DefaultListableBeanFactory`。

### BeanFactory API 家族

`BeanFactory` 是最小契约，真实容器通常还实现多个更细的接口。排障时要知道自己调用的是哪一层能力。

| 接口或工具 | 关注点 | 常见排障问题 |
| --- | --- | --- |
| `BeanFactory` | 单个 Bean 的获取、存在性、类型、别名 | `getBean()` 返回产品、代理还是原始对象 |
| `ListableBeanFactory` | 枚举 Bean、按类型查找多个候选 | `getBeansOfType()` 是否触发 eager init |
| `HierarchicalBeanFactory` | 父子工厂查找 | 当前容器没有但父容器有 |
| `ConfigurableBeanFactory` | scope、别名、类型转换、BPP、依赖关系注册 | 框架扩展修改容器内部策略 |
| `AutowireCapableBeanFactory` | 创建或初始化容器外对象、解析依赖 | 第三方对象需要 Spring 注入但不是普通 Bean |
| `BeanFactoryUtils` | 跨层级查找辅助 | 父子容器中同名或同类型 Bean 的聚合 |

`AutowireCapableBeanFactory` 尤其容易被误用。`autowireBean()` 可以给已有对象做依赖注入，`initializeBean()` 可以执行初始化和 BPP，但它不会把这个对象自动变成普通 singleton Bean；是否注册、是否销毁、是否参与候选选择，需要调用方自己负责。

### BeanDefinition

`BeanDefinition` 是 Bean 的定义元数据。容器不是直接拿注解或 XML 创建对象，而是先把各种输入统一转换成 `BeanDefinition`，再按定义创建 Bean。

重要字段包括：

- `beanClass`：目标类型。
- `scope`：作用域，如 singleton、prototype。
- `lazyInit`：是否懒加载。
- `primary`：是否优先候选。
- `fallback`：是否只在没有普通候选时兜底。
- `defaultCandidate`：是否参与默认候选判断，常影响条件注解和默认注入。
- `autowireCandidate`：是否参与自动装配。
- `dependsOn`：依赖的初始化顺序。
- `parentName`：父定义名称，创建前会参与 merged definition。
- `abstract`：是否只是模板定义，不能直接实例化。
- `factoryBeanName` / `factoryMethodName`：工厂方法创建信息。
- `initMethodName` / `destroyMethodName`：初始化和销毁方法。
- constructor arguments：构造器参数。
- property values：属性注入值。
- source：定义来源，用于排查。

Spring 的重要设计是把来源差异收敛到统一模型。XML、注解扫描、`@Bean` 方法、Import、自动配置、手工注册，最终都要落到 BeanDefinition 或 singleton 实例注册上。

### MergedBeanDefinition

创建 Bean 时，Spring 不一定直接使用最初注册的 `BeanDefinition`。它会把父子定义、默认值、工厂方法、scope、init/destroy 方法等信息合并成更接近创建阶段使用的视图，常见表现就是 `RootBeanDefinition` 或 merged definition。

这层视图的价值是：

- 把分散来源的定义元数据收敛成创建阶段可以直接使用的结构。
- 缓存类型推断、构造器解析、工厂方法等中间结果。
- 让排障从“谁注册了它”推进到“创建时最终看到的定义是什么”。

如果源码里看到注册时的定义和创建时的定义不完全一样，不要急着判断被篡改；先确认是否经过了 merge。排查 owner 是 `docs/merged-bean-definition.md`。

### BeanDefinition 继承与模板定义

BeanDefinition 继承不是 Java 类继承，而是定义元数据继承。子定义可以继承父定义的 class、scope、构造器参数、属性值、init/destroy 方法等元数据，再覆盖其中一部分。

常见形态：

- XML 里的 `<bean parent="...">`。
- `GenericBeanDefinition#setParentName(...)`。
- `abstract=true` 的模板定义。
- 子定义覆盖或补充父定义的属性、构造器参数和方法元数据。

排障价值在于：注册表里看到的子定义可能不完整，但创建阶段看到的是合并后的 RootBeanDefinition。尤其是 XML 或框架批量注册场景，很多重复配置会放在抽象父定义里，真正实例化的是子定义。

边界：

- `abstract=true` 的定义不能直接 `getBean()` 创建。
- 子定义覆盖同名属性，不是和父属性简单并存。
- 集合属性是否 merge 取决于集合元数据，不是所有集合都会自动合并。
- 注解和 Java 配置日常较少直接使用父子 BeanDefinition，但底层 merged definition 仍是创建阶段的重要视图。

### BeanDefinition 的角色与来源

BeanDefinition 除了描述如何创建对象，还携带排障和框架边界信息。

| 元数据 | 含义 | 常见用途 |
| --- | --- | --- |
| `role` | application、support、infrastructure 等角色 | 区分业务 Bean 与框架基础设施 Bean |
| `source` | 定义来源 | 追踪 XML、配置类、扫描类、自动配置来源 |
| `synthetic` | 是否为框架合成定义 | 避免把内部定义误当业务入口 |
| `primary` | 是否作为默认优先候选 | 单值注入多候选时收敛 |
| `autowireCandidate` | 是否参与自动装配 | 让 Bean 可被名称获取但不参与注入 |

基础设施 Bean 很多不是业务功能本身，而是让容器能力成立的组件，例如注解处理器、自动代理创建器、事件广播器、转换服务等。排查“为什么注解没生效”时，除了看业务 Bean，还要看对应基础设施 Bean 是否存在且注册时机正确。

## 3. Bean 的来源

Bean 可以来自多种输入：

| 来源 | 典型方式 | 特点 |
| --- | --- | --- |
| XML | `<bean>` | 显式、历史悠久、适合看清底层模型 |
| 注解扫描 | `@Component`、`@Service`、`@Repository`、`@Controller` | 按 classpath 扫描注册 BeanDefinition |
| Java 配置 | `@Configuration` + `@Bean` | 用方法表达工厂逻辑 |
| Import | `@Import`、`ImportSelector`、`ImportBeanDefinitionRegistrar` | 框架扩展和模块装配常用 |
| 条件注册 | `@Conditional` | 根据环境、类、属性、已有 Bean 决定是否注册 |
| 编程式注册 | `registerBeanDefinition`、`registerBean`、`registerSingleton` | 框架基础设施常用 |
| Boot 自动配置 | `@AutoConfiguration` 及条件注解 | 提供默认 Bean，用户自定义时 backoff |

深层点在于：不同来源的优先级、注册时机和条件判断时机不同。尤其是 Spring Boot 自动配置，很多问题不是 Bean 创建失败，而是 BeanDefinition 根本没有注册，或因为已有 Bean 导致自动配置退让。

### Component Scan 深水区

注解扫描不是“看到 `@Component` 就一定注册”。扫描过程大致是：

```text
classpath 候选资源
-> metadata reader 读取类元数据
-> include / exclude filter 判断是否是候选组件
-> 生成 BeanDefinition
-> BeanNameGenerator 生成名称
-> ScopeMetadataResolver 决定 scope / scoped proxy
-> 注册到 BeanDefinitionRegistry
```

需要掌握的边界：

- stereotype 注解可以是直接注解，也可以是元注解组合，例如自定义 `@UseCase` 标注了 `@Component`。
- `includeFilters` / `excludeFilters` 会在注册前改变候选集合，被过滤掉的类不会进入 BeanDefinition 表。
- `BeanNameGenerator` 可以改变默认 bean name，影响按名称查找、`@Resource` 和单值注入最后的名称收敛。
- `ScopeMetadataResolver` 可以让扫描到的 BeanDefinition 带上 request/session/custom scope 或 scoped proxy 语义。
- `@Indexed` 和 `spring.components` 索引可以优化候选发现，但它改变的是扫描性能和候选发现方式，不改变 Bean 创建主线。

排查“类上明明有注解却没有 Bean”时，先看扫描 base package、过滤器、条件注解和索引，再看创建阶段。

### Import 机制

`@Import` 是很多 Spring 扩展的入口。它不只是“导入一个配置类”，还可以通过不同接口影响 BeanDefinition 注册。

| 形态 | 作用 |
| --- | --- |
| 普通 class | 把配置类或组件类作为候选导入 |
| `ImportSelector` | 根据条件返回一组要导入的类名 |
| `DeferredImportSelector` | 延后导入，Boot 自动配置依赖这种时机 |
| `ImportBeanDefinitionRegistrar` | 直接操作 `BeanDefinitionRegistry` 注册定义 |

排查 Import 问题时，重点看它发生在配置类解析阶段，而不是普通 Bean 创建阶段。Mapper 扫描、Enable 注解、自动配置选择，很多都绕不开这条线。

## 4. Bean 命名与别名

Bean 名称是容器定位 Bean 的主键。类型只负责候选筛选，真正注册和缓存都以名称为核心。

默认规则：

- `@Component` 默认使用类名首字母小写，如 `userService`。
- `@Bean` 默认使用方法名。
- 可以通过 `@Component("name")`、`@Bean("name")` 显式指定。
- 可以给 Bean 配置 alias。

深层影响：

- 按类型注入无法唯一匹配时，字段名或参数名可能参与收敛。
- `@Resource` 默认 name-first，和 `@Autowired` 的 by-type-first 不同。
- 同名 BeanDefinition 是否允许覆盖，取决于容器配置。
- Bean 名是 singleton 缓存、依赖关系记录、销毁顺序的重要索引。

## 5. Bean 作用域

作用域决定 Bean 实例的复用边界。

| Scope | 含义 | 生命周期特点 |
| --- | --- | --- |
| singleton | 一个容器一个实例 | Spring 完整管理创建和销毁 |
| prototype | 每次获取创建新实例 | Spring 创建并初始化，不负责完整销毁 |
| request | 每个 HTTP 请求一个实例 | Web 容器上下文相关 |
| session | 每个 HTTP Session 一个实例 | Web 会话相关 |
| application | ServletContext 级别一个实例 | Web 应用级别 |
| websocket | WebSocket 会话级别 | WebSocket 场景 |
| custom scope | 自定义作用域 | 需要实现 `Scope` |

重点：

- Spring singleton 是每个容器一个实例，不是 JVM 全局单例。
- singleton 不等于线程安全。线程安全取决于 Bean 是否有共享可变状态。
- prototype 注入到 singleton 时，默认只在 singleton 创建时注入一次，不会每次使用都创建新 prototype。要解决这个问题，需要 `ObjectProvider`、`@Lookup`、scoped proxy 或显式工厂。
- request/session scope 注入到 singleton 时通常需要代理，否则生命周期短的对象无法直接注入到生命周期长的对象中。

### 自定义 Scope 与 scoped proxy

自定义 Scope 本质上是把“对象实例存放在哪里、何时创建、何时销毁”这件事交给自定义策略。`Scope` 的关键方法包括：

- `get(String name, ObjectFactory<?> objectFactory)`：按作用域获取对象，不存在时用 `ObjectFactory` 创建。
- `remove(String name)`：从当前作用域移除对象。
- `registerDestructionCallback(String name, Runnable callback)`：登记销毁回调。
- `resolveContextualObject(String key)`：解析上下文对象。
- `getConversationId()`：返回当前作用域会话标识。

scoped proxy 解决的是生命周期错配问题。singleton 依赖 request/session/custom scope Bean 时，注入点拿到的是一个稳定代理；每次调用代理方法时，再按当前 scope 找真实目标对象。

这带来三个排障点：

- 注入对象的 class 可能是代理类型，不是目标类型。
- 异步线程里如果没有 request/session 上下文，代理解析目标会失败。
- 代理是稳定的，目标对象不是稳定的；不要把目标状态误认为 singleton 状态。

## 6. 容器启动与 refresh 主线

`ApplicationContext.refresh()` 是理解 Spring 容器启动的主线。

简化流程：

```text
prepareRefresh()
-> obtainFreshBeanFactory()
-> prepareBeanFactory()
-> postProcessBeanFactory()
-> invokeBeanFactoryPostProcessors()
-> registerBeanPostProcessors()
-> initMessageSource()
-> initApplicationEventMulticaster()
-> onRefresh()
-> registerListeners()
-> finishBeanFactoryInitialization()
-> finishRefresh()
```

和 Bean 强相关的关键阶段：

- 加载并注册 BeanDefinition。
- 执行 `BeanFactoryPostProcessor` 和 `BeanDefinitionRegistryPostProcessor`。
- 注册 `BeanPostProcessor`。
- 预实例化非懒加载 singleton。
- 发布容器刷新完成事件。

一个常见误区是认为注解扫描到 Bean 后马上创建对象。实际上扫描阶段主要产生 BeanDefinition，真正实例化通常发生在后面的 singleton 预实例化阶段或第一次 `getBean()` 时。

### 容器基础设施 Bean

容器启动过程中会注册一批基础设施 Bean。它们通常不是业务对象，但会决定注解、代理、事件、类型转换等能力是否存在。

典型基础设施包括：

- 处理 `@Autowired` 的 `AutowiredAnnotationBeanPostProcessor`。
- 处理 `@PostConstruct`、`@PreDestroy`、`@Resource` 的 `CommonAnnotationBeanPostProcessor`。
- 创建 AOP 代理的 auto-proxy creator。
- 解析 `${...}` 的 placeholder configurer。
- 转换属性和构造器参数的 `ConversionService`。
- 事件广播器、消息源、环境对象等上下文组件。

所以“某个注解不生效”不一定是业务类写错，也可能是对应基础设施没有注册、注册太晚、被覆盖，或者目标 Bean 过早实例化导致错过了后处理器。

## 7. Bean 创建主线

Bean 创建的核心入口通常可以沿着这条源码线看：

```text
AbstractBeanFactory.getBean()
-> doGetBean()
-> DefaultSingletonBeanRegistry.getSingleton()
-> AbstractAutowireCapableBeanFactory.createBean()
-> doCreateBean()
-> createBeanInstance()
-> populateBean()
-> initializeBean()
```

`doCreateBean()` 可以拆成三段：

1. 实例化：选择构造器或工厂方法，得到原始对象。
2. 属性填充：解析依赖并注入字段、setter 或属性。
3. 初始化：执行 Aware、init 回调和 BeanPostProcessor。

如果启用了 AOP，最终暴露对象可能在初始化后被包装成代理。容器缓存的 singleton 通常是最终暴露对象，而不是一定是原始对象。

## 8. 实例化方式

Spring 支持多种 Bean 实例化路径：

- 构造器实例化。
- 静态工厂方法。
- 实例工厂方法。
- `@Bean` 方法。
- `FactoryBean` 生产对象。
- `Supplier` 注册。
- `InstantiationAwareBeanPostProcessor` 短路创建代理。

构造器实例化时，Spring 需要解决：

- 使用哪个构造器。
- 构造器参数从哪里来。
- 参数按类型、名称还是显式配置匹配。
- 是否存在循环依赖。
- 类型转换是否可行。

如果只有一个构造器，现代 Spring 通常可以自动使用它。多个构造器时，可以通过 `@Autowired` 指定候选构造器。

## 9. 依赖注入方式

常见注入方式：

| 注入方式 | 适用场景 | 风险 |
| --- | --- | --- |
| 构造器注入 | 强依赖、不可变对象、必需依赖 | 构造器循环依赖无法解决 |
| Setter 注入 | 可选依赖、后置配置 | 对象可能短暂处于半初始化状态 |
| 字段注入 | 示例代码或框架内部少量场景 | 隐藏依赖、测试困难、不利于不可变 |
| 方法注入 | 特殊生命周期或动态查找 | 语义不如构造器清晰 |
| `ObjectProvider` 注入 | 延迟、可选、多候选处理 | 容易把依赖解析逻辑散落到业务代码 |

工程实践中，业务 Bean 优先使用构造器注入。它能让依赖在类型签名中显性化，也让对象更容易测试和保持不可变。

## 10. 自动装配候选选择

依赖解析不是简单按类型查找。Spring 会根据 `DependencyDescriptor` 描述的注入点需求，收集候选、过滤候选、排序并收敛到最终 Bean。

典型规则：

1. 根据类型找候选 Bean。
2. 排除 `autowireCandidate=false` 的 Bean。
3. 使用 `@Qualifier` 或自定义 qualifier 过滤。
4. 处理泛型匹配，如 `Repository<User>`。
5. 使用 `@Primary` 选择优先候选。
6. 区分普通候选、`@Fallback` 兜底候选和 `defaultCandidate`。
7. 使用 `@Priority` 或 `@Order` 处理排序。
8. 使用注入点名称进行最后收敛。
9. 若仍无法唯一确定，则抛出异常。

集合注入与单值注入不同：

- `List<T>`：注入所有匹配 Bean，通常按顺序排序。
- `Set<T>`：注入所有匹配 Bean，顺序语义弱于 List。
- `Map<String, T>`：key 是 Bean 名，value 是 Bean 实例。
- `ObjectProvider<T>`：延迟获取、可选获取、流式获取。
- `Optional<T>`：表达依赖可以不存在。

### DependencyDescriptor 与 InjectionPoint

Spring 自动装配时，注入点会被抽象成 `DependencyDescriptor`。它不是只保存一个目标类型，还会描述这个依赖的上下文。

常见信息包括：

- 依赖类型和泛型信息。
- 字段、方法参数或构造器参数位置。
- 是否 required。
- 注入点上的注解，例如 `@Qualifier`、`@Lazy`、自定义 qualifier。
- 参数名或字段名。
- 是否 eager 解析。
- 嵌套类型，例如 `Optional<T>`、`ObjectProvider<T>`、`List<T>`。

`InjectionPoint` 更偏向“当前正在注入哪里”的运行期视图。业务代码一般不需要直接使用它，但框架扩展、诊断日志、自定义解析逻辑会用它判断注入来源。

排障时如果只看候选 Bean 列表，容易漏掉“注入点到底要求什么”。例如同样是 `PaymentClient`，`@Qualifier("stripe") PaymentClient`、`Optional<PaymentClient>`、`List<PaymentClient>`、`PaymentClient paymentClient` 对容器提出的是不同需求。

### Optional 与 Provider 的差异

`Optional<T>` 表达依赖可不存在，但一旦注入完成，它只是一个值容器。`ObjectProvider<T>` 保留了延迟访问容器的能力。

| 形态 | 语义 |
| --- | --- |
| `Optional<T>` | 创建当前 Bean 时尝试解析一次，可不存在 |
| `ObjectProvider<T>.getObject()` | 使用时解析，必须存在且能唯一确定 |
| `ObjectProvider<T>.getIfAvailable()` | 使用时解析，不存在返回 null 或默认值 |
| `ObjectProvider<T>.getIfUnique()` | 使用时解析，候选唯一才返回 |
| `ObjectProvider<T>.stream()` | 使用时拉取所有候选，适合插件列表 |

`ObjectProvider` 很适合解决懒加载、可选依赖、多候选列表、prototype 动态获取等问题，但不要把大量业务分支写成运行期 `getIfAvailable()`，否则依赖关系会变得隐蔽。

### required、Nullable、Fallback 与 defaultCandidate

候选选择里有几组容易混淆的语义：

| 机制 | 作用点 | 典型含义 |
| --- | --- | --- |
| `@Autowired(required = false)` | 注入点 | 依赖不存在时不失败，但多个候选冲突仍可能失败 |
| `@Nullable` | 注入点 | 依赖可以解析为 null，表达可空语义 |
| `Optional<T>` | 注入点 | 创建当前 Bean 时尝试解析一次，结果封装为 Optional |
| `ObjectProvider<T>` | 注入点 | 保留到使用时再解析的能力 |
| `@Fallback` | 候选 Bean | 普通候选不存在时才作为兜底候选参与 |
| `defaultCandidate=false` | 候选 BeanDefinition | 不作为默认候选，常用于基础设施或只希望显式引用的 Bean |

`@Primary` 表达“多个普通候选里优先选我”，`@Fallback` 表达“有普通候选时先不要选我”。这两个方向相反，适合默认实现、兼容实现、测试替身等不同场景。Boot 条件注解和默认候选判断也可能参考 `defaultCandidate`，所以排查自动配置 backoff 时，不要只看类型是否存在，还要看候选元数据。

### 自定义候选解析

`@Qualifier` 不只是字符串。Spring 会把注入点上的 qualifier 注解和候选 BeanDefinition 上的 qualifier 元数据交给 `AutowireCandidateResolver` 判断。

常见扩展入口：

- 自定义 qualifier 注解，例如 `@Region("cn")`、`@Tenant("internal")`。
- `CustomAutowireConfigurer`：把自定义注解注册为 qualifier 类型。
- 编程式 `AutowireCandidateQualifier`：框架注册 BeanDefinition 时直接写入 qualifier 元数据。
- 自定义 `AutowireCandidateResolver`：极少数框架级场景，用来改变候选判断策略。

团队里如果有多租户、多区域、多协议实现，优先用语义化 qualifier，而不是把 bean name 当业务路由规则。

## 11. `@Autowired` 与 `@Resource`

`@Autowired` 是 Spring 注解，核心语义是 by-type，然后结合 qualifier、primary、名称等规则收敛。

`@Resource` 是 JSR-250 注解，常见语义是 name-first。它会优先根据名称找 Bean，找不到时再按类型。

`@Inject` / `@Named` 来自 JSR-330 / Jakarta Inject。Spring 支持它们，但语义更接近标准依赖注入注解，而不是 Spring 专属注解。

差异带来的排障点：

- 字段名变化可能影响 `@Resource` 注入结果。
- 同类型多个 Bean 时，`@Autowired` 更依赖 `@Qualifier`、`@Primary`。
- `@Resource(name = "...")` 更像显式按名称绑定。
- `@Inject` 默认 required，通常配合 `@Named` 或 `Provider<T>` 表达名称和延迟。
- `jakarta.inject.Provider<T>` 能延迟获取，但 API 能力少于 Spring 的 `ObjectProvider<T>`。

不要只记注解名字，要记它们向容器提出的依赖需求不同。

## 12. `@Primary`、`@Qualifier`、`@Priority`、`@Order`

这些注解经常被混用，但作用阶段不同。

| 注解 | 作用 |
| --- | --- |
| `@Primary` | 单值注入多个候选时优先选择 |
| `@Fallback` | 普通候选不存在时作为兜底候选 |
| `@Qualifier` | 按限定符过滤候选 |
| `@Priority` | 候选优先级，可参与单值选择和排序 |
| `@Order` | 集合或处理器排序，不等同于单值候选选择 |

经验规则：

- 需要指定某个 Bean：优先用 `@Qualifier`。
- 需要提供默认实现：可以用 `@Primary`。
- 需要提供兜底实现：可以用 `@Fallback`。
- 需要集合顺序：使用 `@Order` 或实现 `Ordered`。
- 不要用 `@Primary` 掩盖模块边界混乱，否则后续扩展容易出现隐式依赖。

自定义 qualifier 也是重要扩展点。它不是简单字符串匹配，而是把注入点注解和候选 Bean 的 qualifier 元数据进行匹配。团队里如果有多租户、多区域、多协议实现，可以用语义化 qualifier 替代到处写字符串 Bean 名。

## 13. 后处理器体系

Spring 的强大扩展性主要来自后处理器。

### BeanFactoryPostProcessor

`BeanFactoryPostProcessor` 作用于 BeanDefinition 阶段，此时 Bean 通常还没有实例化。它可以修改 BeanDefinition，例如替换属性值、改变 scope、解析占位符。

典型实现：

- `PropertySourcesPlaceholderConfigurer`。
- 自定义 BeanDefinition 调整器。

注意：普通 `BeanFactoryPostProcessor` 不适合提前获取业务 Bean，因为那会打乱容器创建节奏，导致某些后处理器还没注册就触发实例化。

### BeanDefinitionRegistryPostProcessor

`BeanDefinitionRegistryPostProcessor` 比普通 BFPP 更早，能在后处理器阶段继续注册新的 BeanDefinition。

典型用途：

- 扫描并注册 Mapper。
- 注册框架基础设施 Bean。
- 根据外部配置动态注册 Bean。

它解决的是“定义层扩展”问题，不是实例增强问题。

### BeanPostProcessor

`BeanPostProcessor` 作用于 Bean 实例阶段，可以在初始化前后修改或替换 Bean。

典型实现：

- `AutowiredAnnotationBeanPostProcessor`：处理 `@Autowired`。
- `CommonAnnotationBeanPostProcessor`：处理 `@PostConstruct`、`@PreDestroy`、`@Resource`。
- `AnnotationAwareAspectJAutoProxyCreator`：创建 AOP 代理。

很多“魔法”不是 Bean 自己完成的，而是某个 BPP 在创建流程中介入完成的。

### 后处理器排序

后处理器的顺序会直接改变容器行为。Spring 通常按三组处理：

1. 实现 `PriorityOrdered` 的处理器。
2. 实现 `Ordered` 的处理器。
3. 没有顺序接口的处理器。

同一组内再按 order 值排序。定义阶段后处理器和实例阶段后处理器都有类似问题，但它们影响的对象不同：BFPP/BDRPP 影响 BeanDefinition，BPP 影响 Bean 实例。

特别注意手工调用 `beanFactory.addBeanPostProcessor(...)` 的场景。手工添加的 BPP 会按添加顺序进入列表，可能绕开常规自动检测和排序逻辑。框架代码可以这么做，业务代码通常不应该这么做。

### 提前实例化与短路

后处理器阶段最危险的动作之一，是过早调用 `getBean()` 获取业务 Bean。这会让目标 Bean 在所有 BPP 注册完成前被创建，结果可能是：

- `@Autowired` 或 `@Resource` 行为异常。
- `@PostConstruct` 没有按预期执行。
- AOP 代理没有创建。
- 后续拿到的是未增强对象。

另一个高级入口是 `InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation()`。它可以在构造器执行前直接返回代理或替代对象，从而让目标类构造器根本不执行。遇到“为什么构造器没进断点但 Bean 存在”时，要检查这类短路。

## 14. 生命周期回调

完整生命周期可以这样理解：

```text
BeanDefinition 注册
-> 实例化
-> 属性填充
-> Aware 回调
-> BeanPostProcessor before initialization
-> @PostConstruct
-> InitializingBean.afterPropertiesSet()
-> custom init-method
-> BeanPostProcessor after initialization
-> Bean 可用
-> @PreDestroy
-> DisposableBean.destroy()
-> custom destroy-method
```

常见 Aware 接口：

- `BeanNameAware`
- `BeanClassLoaderAware`
- `BeanFactoryAware`
- `ApplicationContextAware`
- `EnvironmentAware`
- `ResourceLoaderAware`
- `ApplicationEventPublisherAware`

Aware 接口会增强 Bean 对容器的感知，也会增加耦合。业务代码中应少用，框架扩展代码中更常见。

### SmartInitializingSingleton

`SmartInitializingSingleton.afterSingletonsInstantiated()` 会在所有非懒加载 singleton 创建完成后回调。它解决的问题不是“当前 Bean 初始化”，而是“整个 singleton 图基本就绪之后再做一次动作”。

适合场景：

- 校验多个 singleton 之间的最终关系。
- 在所有候选 Bean 都可见后构建只读索引。
- 启动后检查配置一致性。

不适合场景：

- 替代普通依赖注入。
- 做长时间阻塞 IO。
- 假设 lazy Bean 或 prototype Bean 也已经创建。

### SmartLifecycle

`SmartLifecycle` 管的是容器运行态的 start/stop，不是普通 init/destroy。它常用于消息监听、调度器、连接管理、后台任务等需要随容器启动停止的组件。

关键语义：

- `isAutoStartup()` 决定容器刷新后是否自动启动。
- `getPhase()` 决定 start/stop 顺序；phase 小的先 start，phase 大的先 stop。
- `stop(Runnable callback)` 用于异步停止，停止完成后必须调用 callback。

它适合管理“活着运行”的组件，而不是普通服务对象。普通 Bean 初始化完成不代表系统已经可以接流量，`SmartLifecycle` 经常承担运行态开关边界。

## 15. 初始化和销毁边界

初始化回调的顺序通常是：

```text
@PostConstruct
-> InitializingBean.afterPropertiesSet()
-> custom init-method
```

销毁回调的顺序通常是：

```text
@PreDestroy
-> DisposableBean.destroy()
-> custom destroy-method
```

需要注意：

- singleton Bean 由容器完整管理销毁。
- prototype Bean 容器只负责创建、注入和初始化，不负责自动调用销毁回调。
- 非 Web 容器中 request/session scope 不存在。
- JVM 直接退出或进程被强杀时，销毁回调可能没有机会执行。

### 销毁依赖图

Spring 不只是按注册顺序销毁 singleton。容器会记录依赖关系，例如 A 注入了 B、A `dependsOn` B，或者框架显式注册 dependent bean。销毁时通常要先销毁依赖方，再销毁被依赖方，避免被依赖对象提前失效。

相关概念：

- dependent bean：依赖当前 Bean 的其他 Bean。
- dependencies for bean：当前 Bean 依赖的其他 Bean。
- disposable bean：需要销毁回调的 Bean。
- inferred destroy method：`close`、`shutdown` 等可推断销毁方法。
- `DestructionAwareBeanPostProcessor`：销毁前扩展点，例如处理 `@PreDestroy`。

排查关闭阶段问题时，既要看生命周期回调本身，也要看依赖图是否把销毁顺序导向了预期结果。

## 16. FactoryBean

`FactoryBean<T>` 是一种特殊 Bean。它本身被 Spring 管理，但 `getBean("name")` 返回的是它生产的产品对象。

规则：

- `getBean("foo")`：获取 FactoryBean 创建的产品。
- `getBean("&foo")`：获取 FactoryBean 本身。
- `getObjectType()` 会影响按类型查找和自动装配。
- `isSingleton()` 决定产品对象是否按 singleton 语义缓存。

常见场景：

- MyBatis Mapper 代理。
- JNDI 对象。
- ServiceLoader 集成。
- 复杂代理或第三方对象适配。

区分 `BeanFactory` 与 `FactoryBean`：

- `BeanFactory` 是容器。
- `FactoryBean` 是容器中的特殊工厂 Bean。

### FactoryBean 类型匹配

`FactoryBean` 的难点不在 `getObject()`，而在“产品对象还没创建时，容器如何知道它是什么类型”。这依赖 `getObjectType()`。

`getObjectType()` 的影响包括：

- 按类型查找是否能找到产品对象。
- 自动装配候选是否包含这个 FactoryBean 的产品。
- 条件注解如 `@ConditionalOnBean`、`@ConditionalOnMissingBean` 的判断是否准确。
- AOT/native 场景能否提前推断类型。

如果 `getObjectType()` 返回 null 或返回过宽的接口类型，容器可能在类型匹配阶段看不到真实产品，导致“按名称能拿到，按类型注入失败”的问题。

### 常见内置 FactoryBean

Spring 内部和生态里有不少 FactoryBean 形态：

- JNDI 对象查找。
- ServiceLoader 集成。
- 代理对象创建。
- MyBatis Mapper 这类接口代理。
- 复杂第三方对象的适配包装。

看到一个 Bean 的定义是工厂类型时，要先判断调用方需要的是工厂本身还是产品对象。`&beanName` 是排查 FactoryBean 本体的关键入口。

## 17. `@Configuration` 与 `@Bean`

`@Configuration` 类通常会被 CGLIB 增强。增强的关键目的，是让同一个配置类内部调用 `@Bean` 方法时，也能走容器语义，避免直接调用方法产生多个对象。

示例语义：

```java
@Configuration
class AppConfig {
    @Bean
    Service service() {
        return new Service(repository());
    }

    @Bean
    Repository repository() {
        return new Repository();
    }
}
```

在默认 `proxyBeanMethods = true` 时，`repository()` 调用会被代理拦截，返回容器中的 singleton。

在 `proxyBeanMethods = false` 时，配置类不再用这种方法代理。优点是启动成本更低，缺点是配置类内部直接调用 `@Bean` 方法会退化成普通 Java 方法调用。

使用建议：

- `@Bean` 方法之间没有互相调用时，可以考虑 `proxyBeanMethods = false`。
- 需要依赖其他 Bean 时，更推荐通过方法参数注入，而不是直接调用另一个 `@Bean` 方法。

### `@Bean` 高级语义

`@Bean` 不只出现在 full `@Configuration` 类里，也可以出现在普通组件或 lite 配置类里。lite 模式下，`@Bean` 方法之间的 Java 调用不会被 CGLIB 拦截，调用结果就是普通方法返回值，不自动等同于容器 singleton。

需要额外掌握的点：

- static `@Bean` 方法适合注册 `BeanFactoryPostProcessor` 这类需要很早创建的基础设施，避免过早实例化配置类本身。
- `@Bean(autowireCandidate = false)` 可以让 Bean 可按名称获取，但不参与普通自动装配。
- `@Bean(defaultCandidate = false)` 可以让 Bean 不作为默认候选，避免影响默认注入或部分条件判断。
- `@Bean(bootstrap = Bean.Bootstrap.BACKGROUND)` 可标记支持后台初始化的 Bean，但是否并发创建还取决于容器 bootstrap executor 等启动配置。
- `@ImportResource` 可以把 XML 定义并入 Java 配置模型，适合迁移老项目或接入仍依赖 XML namespace 的框架。
- `@PropertySource` 影响 `Environment` 的 `PropertySource` 集合，不直接创建业务 Bean，但会影响后续占位符和值解析。

排查配置类时，要先判断它是 full 还是 lite，再判断 `@Bean` 方法是作为工厂方法被容器调用，还是被业务代码直接当普通 Java 方法调用。

### 方法注入

方法注入解决的是“长生命周期 Bean 需要每次动态获取短生命周期 Bean”的问题。经典形态是 `lookup-method` 或 `@Lookup`。

例如 singleton 每次处理请求都需要一个新的 prototype worker，如果直接构造器注入 prototype，只会在 singleton 创建时注入一次。`@Lookup` 可以让 Spring 生成子类覆盖方法，每次调用方法时回到容器获取目标 Bean。

使用边界：

- 目标方法不能是 final。
- 目标类通常需要能被子类化。
- AOT/native 场景需要额外验证。
- 业务上更简单的方案通常是注入 `ObjectProvider<Worker>`。

`replaced-method` 是更老的 XML 能力，用另一个方法替换原方法实现。现代业务代码很少使用，但读 XML BeanDefinition 或历史项目时需要认识它。

## 18. 循环依赖

循环依赖指 Bean A 依赖 Bean B，Bean B 又依赖 Bean A。

Spring 默认只能解决部分 singleton 的 setter 或字段注入循环依赖。构造器循环依赖通常无法解决，因为两个对象都必须先完成构造才能注入对方。

可以解决的典型场景：

```text
A 实例化 -> 暴露早期引用 -> A 注入 B
B 实例化 -> B 注入 A 的早期引用 -> B 初始化完成
A 继续完成初始化
```

无法解决或高风险场景：

- 构造器循环依赖。
- prototype 循环依赖。
- 循环依赖中混入复杂 AOP 代理。
- 最终注入的是 early reference，但后续又被包装成不同代理。

工程上不要依赖循环依赖作为设计手段。它通常说明职责边界不清，应该拆出第三个协作者，或改用事件、回调、发布订阅等结构。

### 循环依赖开关与异常边界

循环依赖能否被尝试解决，还受容器配置影响。底层 `AbstractAutowireCapableBeanFactory` 有 `allowCircularReferences` 语义；Spring Boot 应用还可能通过 `spring.main.allow-circular-references` 控制是否允许循环依赖。

常见失败形态：

- `BeanCurrentlyInCreationException`：当前 Bean 正在创建，又被创建链路再次请求。
- 构造器循环依赖：没有“先实例化、后填充属性”的窗口。
- prototype 循环依赖：prototype 不进入 singleton 三级缓存。
- raw injection despite wrapping：循环依赖中先注入了原始对象，后续又被代理包装，导致依赖方持有的不是最终暴露对象。

所以看到循环依赖异常时，不要只问“Spring 为什么没帮我解决”。要先确认开关是否允许、依赖发生在构造器还是属性填充、scope 是否是 singleton、AOP 是否要求 early proxy 和最终 proxy 一致。

## 19. 三级缓存

Spring 解决 singleton 循环依赖依赖三级缓存：

| 缓存 | 含义 |
| --- | --- |
| `singletonObjects` | 一级缓存，完整初始化后的 singleton |
| `earlySingletonObjects` | 二级缓存，提前暴露的早期对象 |
| `singletonFactories` | 三级缓存，生产 early reference 的 ObjectFactory |

三级缓存的关键不是“多放几个 Map”，而是在实例化之后、属性填充之前，容器有机会提前暴露一个引用。如果 AOP 参与，这个 early reference 可能是早期代理，而不是原始对象。

主线：

```text
实例化 A
-> 放入 singletonFactories
-> A 填充属性时需要 B
-> 创建 B
-> B 填充属性时需要 A
-> 从 singletonFactories 取得 A 的 early reference
-> B 完成
-> A 继续完成
```

这套机制只对 singleton 创建过程有效，不是通用对象图算法。

## 20. Lazy 语义

`@Lazy` 有两种常见位置，语义不同。

放在 Bean 定义上：

```java
@Lazy
@Component
class HeavyService {
}
```

表示这个 Bean 不在容器启动时预实例化，而是在第一次需要时创建。

放在注入点上：

```java
class Client {
    Client(@Lazy HeavyService heavyService) {
        this.heavyService = heavyService;
    }
}
```

表示注入一个延迟代理，真正调用时再解析目标 Bean。

风险：

- 启动更快，但错误可能从启动期推迟到运行期。
- 延迟代理会改变对象类型和调试体验。
- 懒加载不应成为掩盖设计问题或启动问题的默认手段。

## 21. 代理与 AOP

Spring AOP 通常通过代理实现。容器最终暴露的是代理对象，代理对象在方法调用时织入事务、缓存、异步、安全等逻辑。

两种主要代理：

| 代理方式 | 基础 | 特点 |
| --- | --- | --- |
| JDK 动态代理 | 接口 | 代理对象实现接口，不是目标类子类 |
| CGLIB 代理 | 子类 | 代理对象是目标类子类，受 final 限制 |

AOP 与 Bean 生命周期的关系：

- 目标 Bean 先完成实例化和初始化。
- `BeanPostProcessor` 在初始化后可能返回代理。
- singleton 缓存中保存的通常是代理。
- 调用方从容器拿到的是代理。

深层影响：

- `this` 自调用不会经过代理。
- private 方法无法被常规 Spring AOP 增强。
- final 类或 final 方法影响 CGLIB。
- 注解放在接口还是实现类上，会影响某些代理和扫描语义。

## 22. 自调用失效

自调用是 Spring AOP 最常见陷阱之一。

```java
class OrderService {
    public void outer() {
        inner();
    }

    @Transactional
    public void inner() {
    }
}
```

`outer()` 内部调用 `inner()` 是 `this.inner()`，没有经过代理对象，因此 `@Transactional` 不会按预期生效。

常见受影响注解：

- `@Transactional`
- `@Async`
- `@Cacheable`
- `@Retryable`
- 方法安全相关注解

解决方向：

- 把被增强方法拆到另一个 Bean。
- 通过代理对象调用。
- 使用 AspectJ weaving，而不是基于代理的 Spring AOP。

工程上最清晰的方式通常是拆分职责，让跨切面边界发生在 Bean 与 Bean 之间。

## 23. 事务 Bean

`@Transactional` 本质上通常依赖 Spring AOP 代理。它不是编译器语法，也不是方法自己开启事务。

事务生效需要满足：

- Bean 被 Spring 容器管理。
- 调用经过代理对象。
- 方法符合代理可拦截条件。
- 已启用事务管理。
- 存在合适的 `PlatformTransactionManager`。

常见失效原因：

- 同类自调用。
- 方法不是 public，或代理机制无法拦截。
- final 方法或 final 类。
- 异常被内部捕获，事务拦截器看不到异常。
- 异常类型不符合 rollback 规则。
- 多数据源事务管理器选择错误。

理解事务时，不要只看注解，要看调用是否穿过代理。

## 24. 值解析与类型转换

Bean 创建过程中不仅注入 Bean，也会注入配置值。

常见值来源：

- `application.properties` / `application.yml`
- 系统属性。
- 环境变量。
- 命令行参数。
- `PropertySource`。
- 测试属性。

常见表达方式：

- `${...}`：属性占位符。
- `#{...}`：SpEL 表达式。
- `@Value`：注入值。
- `@ConfigurationProperties`：批量绑定配置对象。

深层流程：

```text
PropertySource 提供原始值
-> placeholder resolver 解析 ${...}
-> SpEL resolver 解析 #{...}
-> ConversionService / PropertyEditor 转换目标类型
-> BeanWrapper 写入属性或构造器参数
```

排障重点：

- 值是否存在于 `Environment`。
- 占位符是否 strict。
- 目标类型是否有转换器。
- SpEL 是否在 AOT/native 场景受限。

### Environment 与 PropertySource

`Environment` 不是简单的配置 Map，而是一组有顺序的 `PropertySource`。同一个 key 出现在多个来源时，优先级决定最终值。

常见排查顺序：

- key 是否存在。
- 来自哪个 `PropertySource`。
- 是否被命令行参数、测试属性、环境变量覆盖。
- profile 激活后是否加载了额外配置。
- 占位符解析发生时，对应 `PropertySource` 是否已经可见。

这解释了为什么“配置文件里明明有值，Bean 里却不是这个值”：容器看到的是 `Environment` 的最终解析结果，不是单个文件。

### 占位符、SpEL 与类型转换顺序

常见顺序可以理解为：

```text
原始字符串
-> ${...} 占位符解析
-> #{...} SpEL 求值
-> ConversionService / PropertyEditor 转目标类型
-> BeanWrapper 写入属性
```

占位符关注“值从哪里来”，SpEL 关注“表达式如何计算”，类型转换关注“字符串或对象如何变成目标类型”。这三层问题要分开排查。

### XML、Properties、Groovy 与 namespace 输入

外部定义输入最终也要转成 BeanDefinition。

| 输入 | 作用 |
| --- | --- |
| XML BeanDefinitionReader | 把 `<bean>`、构造器参数、属性、scope 等转成 BeanDefinition |
| Properties BeanDefinitionReader | 用 properties 风格描述 Bean 定义 |
| Groovy BeanDefinitionReader | 用 Groovy DSL 描述 Bean 定义 |
| XML namespace extension | 把自定义标签解析成一个或多个 BeanDefinition |

XML namespace 是很多旧框架集成的关键。一个 `<tx:annotation-driven>` 或自定义标签，背后可能注册的是后处理器、advisor、parser 产生的一组基础设施 Bean。排查这类问题时，不要只看 XML 标签，要看标签最终注册了哪些 BeanDefinition。

### XML 细颗粒定义语义

XML 最能暴露底层 BeanDefinition 模型，很多历史项目和框架集成仍会遇到这些语义：

| XML 语义 | 对应含义 | 排障关注点 |
| --- | --- | --- |
| inner bean | 属性或构造器参数里的匿名 BeanDefinition | 没有独立 bean name，生命周期依附外层注入点 |
| `idref` | 注入另一个 Bean 的名字字符串 | 校验目标名字存在，但注入值仍是字符串 |
| `<null/>` | 显式注入 null | 和没有配置属性不是一回事 |
| `list` / `set` / `map` / `props` | 集合属性注入 | 元素值解析、类型转换和 merge 规则 |
| `merge=true` | 子定义集合合并父定义集合 | 只对支持 merge 的集合元数据生效 |
| `p` namespace | 属性注入简写 | 可读性换简洁性，底层仍是 property values |
| `c` namespace | 构造器参数简写 | 参数名、索引和类型匹配仍要能解析 |
| `util:*` namespace | 注册集合、常量、属性等辅助 Bean | 常产生额外 BeanDefinition |

XML 的默认值也会影响定义，例如 `default-lazy-init`、`default-autowire`、`default-init-method`、`default-destroy-method`。它们通常在读取 XML 时落到 BeanDefinition 或 defaults 上，再在 merged definition 阶段变成创建所需的最终视图。

## 25. 条件化 Bean

`@Conditional` 是 Spring 条件注册的底层扩展点。Spring Boot 在它之上提供大量条件注解：

- `@ConditionalOnClass`
- `@ConditionalOnMissingClass`
- `@ConditionalOnBean`
- `@ConditionalOnMissingBean`
- `@ConditionalOnProperty`
- `@ConditionalOnResource`
- `@ConditionalOnWebApplication`
- `@ConditionalOnSingleCandidate`

Boot 自动配置的核心不是“强行注册所有默认 Bean”，而是：

```text
条件满足 -> 用户没有提供替代 Bean -> 注册默认 Bean
```

这就是 backoff 机制。用户自定义 Bean 后，自动配置通常退让。

排障时要看：

- 自动配置类是否被导入。
- 条件是否命中。
- 目标 Bean 是否已存在。
- 条件判断发生时，相关 BeanDefinition 是否已经注册。
- `ConditionEvaluationReport` 中的 match / did not match 原因。

## 26. Profile

`@Profile` 根据当前激活 profile 决定 Bean 是否注册。本质上也是条件注册。

适合场景：

- dev/test/prod 环境差异。
- 本地 mock 与真实实现切换。
- 不同部署环境的基础设施差异。

不适合场景：

- 复杂业务开关。
- 运行时频繁切换。
- 大量组合条件。

Profile 的关键是影响 BeanDefinition 是否进入容器，而不只是影响 Bean 是否创建。

## 27. Bean 覆盖

同名 BeanDefinition 可能发生覆盖。是否允许覆盖取决于容器配置和 Spring Boot 版本配置。

覆盖的风险：

- 被覆盖 BeanDefinition 完全消失。
- 排障时看到的 Bean 不是以为的那个来源。
- 自动配置和用户配置顺序不同会导致结果变化。

推荐做法：

- 用明确命名避免冲突。
- 用条件注册表达默认实现。
- 用 `@Primary` 或 `@Qualifier` 解决多实现选择。
- 不要把覆盖当作模块扩展机制。

## 28. `dependsOn`

`dependsOn` 表达初始化顺序，不表达注入关系。

例如 A `dependsOn` B，表示创建 A 前先创建 B，但不代表 A 持有 B 的引用。

适合场景：

- 某些基础设施必须先初始化。
- 依赖外部副作用，如驱动注册、系统属性准备。

不适合场景：

- 代替构造器注入。
- 解决业务依赖关系。
- 掩盖 Bean 初始化副作用过重的问题。

## 29. 父子容器

Spring 支持 `ApplicationContext` 层级结构。

规则：

- 子容器可以查找父容器 Bean。
- 父容器不能查找子容器 Bean。
- 子容器可以定义同名 Bean 遮蔽父容器 Bean。

典型场景：

- 传统 Spring MVC 中 root context 与 servlet context。
- 多模块系统隔离。
- 测试上下文分层。

排障点：

- Bean 是否在当前容器，还是父容器。
- 同名 Bean 是否被子容器遮蔽。
- 事件传播是否跨父子容器。

## 30. 编程式注册

常见 API：

| API | 注册对象 | 特点 |
| --- | --- | --- |
| `registerBeanDefinition` | BeanDefinition | 完整进入容器创建流程 |
| `registerBean` | BeanDefinition 或 Supplier 语义 | 函数式注册，仍可参与容器流程 |
| `registerSingleton` | 已有实例 | 直接放入 singleton 注册表，生命周期能力较弱 |

深层区别：

- 注册 BeanDefinition，容器还掌握如何创建、注入、后处理。
- 注册 singleton 实例，通常表示对象已经创建，容器无法完整介入实例化前后的所有步骤。

框架扩展更常用 BeanDefinition 注册，因为它保留了容器生命周期和后处理器能力。

## 31. Resolvable Dependency

有些对象可以被注入，但它们不是普通 Bean。Spring 可以注册 resolvable dependency，例如：

- `BeanFactory`
- `ApplicationContext`
- `Environment`
- `ResourceLoader`
- `ApplicationEventPublisher`

这些依赖通常是容器基础设施对象。它们能被解析，但不一定以普通 BeanDefinition 形式存在。

排障时如果 `getBean()` 找不到某个类型，但它又能被注入，就要考虑 resolvable dependency。

## 32. 泛型匹配

Spring 使用 `ResolvableType` 保留和解析泛型信息。泛型可以参与候选选择。

例如：

```java
interface Repository<T> {
}

class UserRepository implements Repository<User> {
}

class OrderRepository implements Repository<Order> {
}
```

注入 `Repository<User>` 时，Spring 可以优先匹配 `UserRepository`。

风险点：

- 原始类型会丢失泛型信息。
- JDK 代理和 CGLIB 代理可能影响类型观察。
- FactoryBean 的 `getObjectType()` 不准确会影响按类型查找。
- 复杂继承层级下泛型解析可能不符合直觉。

## 33. AOT 与 Native

Spring AOT 在构建期分析应用上下文，生成部分代码和运行时提示，减少运行期反射、扫描和动态推断。

和 Bean 相关的影响：

- 运行期动态注册 BeanDefinition 可能受限。
- 反射访问需要 RuntimeHints。
- CGLIB 代理、JDK 代理需要提前知道。
- SpEL、`@Value`、FactoryBean 类型推断可能需要更明确的元数据。
- XML namespace、方法注入、自定义 qualifier 等扩展点需要额外验证。

关键判断：

```text
JVM 可运行 != Native 可运行
```

只要依赖运行期扫描、反射、动态代理、动态类型推断，就需要考虑 AOT/native 约束。

### AOT 对 Bean 机制的具体影响

| 主题 | AOT 下的关注点 |
| --- | --- |
| RuntimeHints | 反射、资源、代理、序列化等运行期访问需要提前声明 |
| XML Reader | 构建期能否稳定解析 XML，namespace handler 是否可见 |
| SpEL / `@Value` | 表达式是否依赖运行期动态类型或反射访问 |
| FactoryBean | `getObjectType()` 是否足够准确，能否构建期推断产品类型 |
| 方法注入 | CGLIB 子类、lookup-method、replaced-method 是否能被提前生成或提示 |
| 自定义 qualifier | 注解元数据是否能在构建期保留和匹配 |
| PropertyEditor | 依赖反射或无默认构造器的 editor 需要额外验证 |
| 外部对象注入 | `AutowireCapableBeanFactory` 注入容器外对象时，目标类型是否可分析 |

AOT 的思路是把“运行期猜测”尽量前移到“构建期确定”。所以越依赖动态注册、动态代理、反射扫描、运行期条件分支的 Bean 机制，越需要明确元数据和 RuntimeHints。

## 34. Spring Boot 自动配置中的 Bean

Spring Boot 不是替代 Spring Bean 模型，而是在 Spring Bean 模型上叠加自动配置。

Boot 自动配置常见模式：

```java
@AutoConfiguration
@ConditionalOnClass(SomeLibrary.class)
class SomeAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    SomeService someService() {
        return new SomeService();
    }
}
```

含义：

- classpath 上有某个库，自动配置才生效。
- 用户没有提供同类 Bean 时，Boot 提供默认 Bean。
- 用户自定义 Bean 后，默认 Bean backoff。

排障重点：

- 自动配置有没有被导入。
- 条件注解有没有匹配。
- 用户 Bean 是否提前出现。
- 自动配置顺序是否影响 `@ConditionalOnMissingBean` 判断。
- 使用 Actuator conditions 或 `ConditionEvaluationReport` 看证据。

### 自动配置顺序与 backoff 时机

`@ConditionalOnMissingBean` 判断的是“当前条件评估时，容器里是否已经有匹配的 BeanDefinition 或 Bean”。如果用户 Bean 注册得更晚，自动配置可能已经创建了默认定义；如果用户 Bean 注册得更早，默认定义会 backoff。

因此排查 Boot Bean 问题时要同时看三件事：

- 自动配置类的导入顺序。
- 用户配置类和自动配置类的相对顺序。
- 条件判断用的是类型、名称、注解还是单候选。

Boot 的默认 Bean 不是“低优先级 Bean”这么简单。很多时候它根本不会注册，或者在条件命中后正常注册，然后再参与普通 Spring Bean 创建流程。

## 35. 设计原则

写 Bean 时建议遵守这些原则：

- 业务 Bean 尽量无状态。
- singleton Bean 不保存请求级可变状态。
- 强依赖使用构造器注入。
- 可选依赖使用 `ObjectProvider`、`Optional` 或 setter。
- 不要在构造器里做复杂外部 IO。
- 初始化逻辑放到明确的生命周期阶段。
- 避免业务代码直接调用 `ApplicationContext.getBean()`。
- 避免循环依赖。
- 需要跨切面能力时，让调用穿过 Bean 代理边界。
- 框架扩展代码再考虑 Aware、后处理器和编程式注册。

一个健康的 Bean 设计应该让依赖关系从构造器或配置中能读出来，而不是隐藏在运行期查找、静态工具类或容器回调里。

## 36. 常见误区

| 误区 | 正确认知 |
| --- | --- |
| Bean 就是 JavaBean | Bean 是容器托管对象，不要求符合 JavaBean 规范 |
| singleton 一定线程安全 | singleton 只是容器内实例唯一，不保证线程安全 |
| 加了注解就一定生效 | 注解通常需要对应后处理器或代理机制 |
| `@Transactional` 一定开事务 | 调用必须经过事务代理 |
| `@Lazy` 只是启动优化 | 它会改变创建时机，有时还会引入代理 |
| prototype 会被容器自动销毁 | prototype 通常不由容器完整销毁 |
| `@Order` 能解决单 Bean 注入冲突 | 单值候选选择主要看 primary、qualifier、priority、名称 |
| `@Fallback` 和 `@Primary` 都是默认 Bean | `@Primary` 是优先普通候选，`@Fallback` 是没有普通候选时才兜底 |
| 类上有 `@Component` 就一定注册 | component scan 的 base package、filter、条件和索引都会影响候选是否入表 |
| `@Inject` 和 `@Autowired` 完全一样 | Spring 支持 JSR-330，但 `Provider`、`@Named`、required 语义和 Spring 专属扩展不同 |
| `AutowireCapableBeanFactory.autowireBean()` 会注册 Bean | 它只给已有对象做注入，不自动加入 singleton 注册表 |
| XML 集合子定义会自动合并父定义 | 是否合并取决于集合元数据的 merge 语义 |
| `dependsOn` 表达依赖注入 | 它只表达初始化顺序 |
| `getBean()` 一定返回原始对象 | 可能返回代理或 FactoryBean 产品 |
| Boot 自动配置总是生效 | 条件不匹配或用户 Bean 存在时会退让 |

## 37. 排障路径

遇到 Bean 问题时，按层排查比直接猜注解更可靠。

### 定义层

先确认 BeanDefinition 是否存在：

- 是否被扫描到。
- 扫描 base package、include/exclude filter 是否允许它成为候选。
- 条件是否匹配。
- profile 是否激活。
- 是否被同名覆盖。
- 是否只是 `abstract` 父定义或模板定义。
- 是否被自动配置 backoff。
- Bean 名是否正确。

### 创建层

再确认 Bean 是否能创建：

- 构造器是否可选中。
- 依赖是否能解析。
- 候选是否被 `autowireCandidate=false`、`defaultCandidate=false`、`@Fallback` 或 qualifier 排除。
- 类型转换是否成功。
- 是否有循环依赖。
- 循环依赖开关、scope、AOP early proxy 是否允许当前形态。
- 初始化回调是否抛异常。
- BeanPostProcessor 是否提前触发了创建。

### 暴露层

最后确认调用方拿到的是什么：

- 原始对象还是代理。
- FactoryBean 产品还是 FactoryBean 本身。
- early reference 还是最终代理。
- 父容器 Bean 还是子容器 Bean。
- scoped proxy 还是真实目标对象。

### 行为层

如果 Bean 创建正常但行为异常，重点看：

- 方法调用是否经过代理。
- 注解对应的基础设施是否启用。
- 事务、缓存、异步等切面顺序是否符合预期。
- 请求上下文、线程上下文是否存在。

## 38. 可运行证据入口

总览文档只负责把知识点讲成地图；真正确认理解，要回到可运行证据。

| 目标 | 命令或入口 |
| --- | --- |
| 最小模块验证 | `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test` |
| 文档链接和模块契约 | `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test` |
| 主文档归属 | `docs/appendix-knowledge-map.md` |
| 断点入口 | `docs/guide-breakpoint-map.md` |
| 30 分钟快速闭环 | `docs/guide-quickstart-30min.md` |
| 主线顺序 | `docs/guide-mainline-timeline.md` |
| 深入阅读顺序 | `docs/guide-deep-dive-guide.md` |
| 常见误区 | `docs/appendix-common-pitfalls.md` |
| 生产排障 | `docs/appendix-production-troubleshooting-checklist.md` |

建议的学习闭环：

```text
读 KNOWLEDGE.md 建地图
-> 看 README.md 选入口
-> 跑最小 Lab 固定事实
-> 按 appendix-knowledge-map.md 找 owner 文档
-> 用 guide-breakpoint-map.md 对源码断点
-> 回到自检或常见误区确认边界
```

## 39. 源码阅读入口

建议按这条线阅读源码：

```text
ApplicationContext.refresh()
-> PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()
-> PostProcessorRegistrationDelegate.registerBeanPostProcessors()
-> DefaultListableBeanFactory.preInstantiateSingletons()
-> AbstractBeanFactory.getBean()
-> AbstractBeanFactory.doGetBean()
-> AbstractAutowireCapableBeanFactory.createBean()
-> AbstractAutowireCapableBeanFactory.doCreateBean()
-> AbstractAutowireCapableBeanFactory.createBeanInstance()
-> AbstractAutowireCapableBeanFactory.populateBean()
-> AbstractAutowireCapableBeanFactory.initializeBean()
-> DefaultSingletonBeanRegistry.getSingleton()
```

对应要观察的对象：

- `BeanDefinition`：定义是否正确。
- merged `RootBeanDefinition`：父子定义、默认值、工厂方法和候选元数据最终长什么样。
- `ClassPathBeanDefinitionScanner`：扫描候选类、过滤器、bean name 和 scope 元数据。
- `DependencyDescriptor`：注入点提出了什么需求。
- `AutowireCandidateResolver`：qualifier、fallback、default candidate、lazy 等候选规则怎样判断。
- `BeanPostProcessor` 列表：谁会介入实例。
- singleton 三层缓存：循环依赖和 early reference。
- dependent bean 关系：销毁顺序和依赖图。
- exposed object：最终暴露给调用方的对象。

## 40. 一句话总结

Spring Bean 的深层模型是：用 `BeanDefinition` 描述对象，用 `BeanFactory` 创建和缓存对象，用依赖解析连接对象，用生命周期回调稳定对象，用后处理器增强对象，用 scope 决定复用边界，用代理承载横切能力。掌握这条主线，比记住零散注解更重要。
