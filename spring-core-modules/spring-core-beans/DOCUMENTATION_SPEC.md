# Spring Core Beans 详细文档写作规格

本文说明 `spring-core-modules/spring-core-beans/docs/` 下每个详细文档应该如何写。

这份规格只约束写作目标、边界和质量要求，不要求每篇文档采用固定章节模板。具体文档应该按照对应知识点的天然结构组织正文。

## 总原则

`KNOWLEDGE.md` 只作为拆分知识点和确定文档清单的来源。生成某一篇详细文档时，应把它当作独立文章写，读者不需要先读 `KNOWLEDGE.md` 才能理解正文。凡是该知识点必须解释的背景、概念、源码入口、运行证据、排障方法和常见误区，都应该在该文档中完整展开。

不要因为某个点在 `KNOWLEDGE.md` 中出现过就省略，也不要把详细文档写成总览的扩写版。每篇文档要围绕自己的问题建立完整理解。

详细文档之间不做惯性跳转。只有在继续展开会明显越过当前知识点边界，或者当前结论依赖另一个专门机制时，才插入必要跳转。不要在每篇末尾固定添加“相邻主题”。

面向读者的 `docs/*.md` 文档需要保留章节入口卡片，因为当前文档契约测试会检查 `<!-- CHAPTER-CARD:START -->` 和 `<!-- CHAPTER-CARD:END -->`。但章节入口卡片只是入口元信息，不代表正文结构必须统一。

新增 `docs/*.md` 后，需要同步更新模块 `README.md` 的文档目录。文档中引用的 `SpringCoreBeans*Test` 必须真实存在。

## 写作质量要求

每篇详细文档至少要做到：

- 问题明确：标题和开头能看出这篇文档解决什么问题。
- 主体自洽：不依赖读者先读总览文档。
- 机制可追踪：关键结论能落到 Spring 源码入口、容器阶段、运行现象或本模块 Lab。
- 边界清楚：说明这个机制能做什么、不能做什么、什么时候失效。
- 例子克制：例子服务机制解释，不堆配置语法大全。
- 跳转克制：只在解释边界需要时跳转。
- 排障有用：读完后知道遇到相关问题应该观察什么。

## 文档生成顺序

建议先生成主干文档，再生成分支专题：

```text
bean-mental-model.md
-> bean-definition-registration.md
-> bean-definition-metadata-and-origin.md
-> beanfactory-vs-applicationcontext.md
-> refresh-mainline.md
-> container-bootstrap-and-infrastructure.md
-> bean-creation-mainline.md
-> dependency-injection-resolution.md
-> autowire-candidate-selection.md
-> lifecycle-callbacks.md
-> post-processors-overview.md
-> early-reference-and-three-level-cache.md
-> proxying-phase.md
-> factorybean.md
-> boot-auto-configuration-beans.md
-> aot-native-overview.md
```

这批主干完成后，再补全命名、作用域、条件、Profile、外部输入、AOT 细分、Guide 和 Appendix。

## 核心模型与定义层文档

### `docs/bean-mental-model.md`

这篇应该写成 Spring Bean 的对象模型说明，不是注解入门。

正文重点区分 Java class、BeanDefinition、merged `RootBeanDefinition`、原始实例、early reference、最终暴露对象、代理对象、FactoryBean 产品。要解释为什么 `getBean()` 拿到的对象不一定等于构造器创建出来的原始对象。

适合用对照表和小型对象流转图。应重点写清“容器托管”到底托管了哪些语义：定义、创建、依赖、生命周期、scope、代理和销毁。

不要把它写成 Spring Bean 基础注解列表，也不要展开完整创建流程。创建流程只需要服务对象模型解释。

### `docs/bean-definition-registration.md`

这篇应该写 BeanDefinition 如何进入容器。

正文应按来源展开：XML、component scan、`@Bean`、`@Import`、条件注册、编程式注册、Boot 自动配置。每种来源都要说明它在什么阶段产生 BeanDefinition，注册进哪个 registry，是否会立刻创建 Bean，以及它对后续创建或候选选择留下了什么元数据。

重点不是罗列写法，而是比较注册时机、注册主体和排障观察点。遇到“类存在但没有 Bean”时，读者应知道先查 base package、filter、condition、profile、import 链路、自动配置 backoff，而不是先查构造器。

### `docs/bean-definition-metadata-and-origin.md`

这篇应该写 BeanDefinition 元数据如何影响后续行为。

正文重点讲 scope、lazy、primary、fallback、default candidate、autowire candidate、qualifier、role、source、resource description、factory method、init/destroy method、depends-on 等信息。每类元数据都要说明它影响哪个阶段：注册、候选选择、实例化、初始化、销毁、排障溯源。

这篇要把“元数据不是注释，而是容器决策输入”讲清楚。适合结合 origin/source 说明为什么生产排障时要先确认 BeanDefinition 从哪里来。

### `docs/merged-bean-definition.md`

这篇应该写 merged BeanDefinition 的形成和作用。

正文应围绕“原始 BeanDefinition 不是运行时最终定义”展开。讲清父子定义、模板定义、默认值、工厂方法、scope、属性值、方法覆盖等信息如何合并到 `RootBeanDefinition`。

重点解释为什么很多创建阶段看到的是 merged definition，而不是注册时的原始 definition。适合说明缓存、失效、重新合并时机，以及它如何影响构造器解析、属性填充和生命周期方法。

不要把它写成 XML parent bean 语法教程。

### `docs/bean-name-and-alias.md`

这篇应该写 bean name 与 alias 的定位语义。

正文要说明默认命名、显式命名、别名注册、规范化名称、FactoryBean 的 `&` 前缀、同名覆盖和按名称注入的关系。重点讲名字如何影响 `getBean`、`@Resource`、单值自动装配最后的名称收敛、异常信息和排障定位。

这篇不应该变成命名规范建议。它要回答：容器内部到底用哪个名字定位 Bean，以及名字错了会在哪些地方表现出来。

### `docs/bean-definition-overriding.md`

这篇应该写同名 BeanDefinition 冲突和覆盖。

正文应讲清同名定义出现时，Spring Framework 和 Spring Boot 的行为差异、覆盖开关、失败时机、用户 Bean 与自动配置 backoff 的区别。要区分“覆盖同名 BeanDefinition”和“自动配置条件退让”这两件事。

适合围绕实际排障问题写：为什么本地启动成功、测试失败；为什么自定义 Bean 没生效；为什么 Boot 报 overriding disabled。

## 容器抽象与启动主线文档

### `docs/beanfactory-vs-applicationcontext.md`

这篇应该写 BeanFactory 与 ApplicationContext 的职责边界。

正文不要只说 ApplicationContext 是 BeanFactory 的高级版本，而要说明二者在创建时机、refresh 生命周期、资源加载、环境、事件、国际化、自动注册基础设施、预实例化单例上的差异。

重点写“同样是 getBean，为什么 ApplicationContext 启动时已经做了很多 BeanFactory 不会自动做的事情”。适合用小型对照和启动阶段说明。

### `docs/beanfactory-api-and-autowirecapablebeanfactory.md`

这篇应该写 BeanFactory API 家族的边界。

正文重点区分 `getBean`、`containsBean`、`getType`、`getBeanNamesForType`、`resolveDependency`、`autowireBean`、`createBean`、`initializeBean`、`registerSingleton` 等 API。每个 API 要说明是否触发创建、是否注册到容器、是否执行 BeanPostProcessor、是否参与依赖解析。

这篇要特别讲清 `AutowireCapableBeanFactory` 对容器外对象的处理边界：它可以注入、初始化甚至应用后处理器，但不等于把对象变成标准 singleton Bean。

### `docs/refresh-mainline.md`

这篇应该写 `ApplicationContext#refresh()` 的主线。

正文应按时间线写：准备环境、获取 BeanFactory、加载/冻结 BeanDefinition、执行 BDRPP/BFPP、注册 BPP、初始化消息源和事件广播器、注册监听器、预实例化非懒单例、发布刷新完成事件。

重点是每个阶段改变了什么容器状态，以及为什么顺序不能颠倒。读者应该能沿着这篇文档打断点看 refresh。

不要展开单个 Bean 的完整 `doCreateBean` 细节；只说明 refresh 如何触发单例创建。

### `docs/container-bootstrap-and-infrastructure.md`

这篇应该写容器基础设施如何被装配起来。

正文重点解释注解处理、自动装配、事件、环境、转换服务、AOP 代理等能力为什么不是“天然生效”，而是由一批基础设施 Bean 和后处理器参与。要说明这些基础设施在 refresh 的哪个阶段注册，什么时候开始影响普通 Bean。

适合用“如果缺少某个基础设施会发生什么”来写，例如 `@Autowired` 不生效、`@PostConstruct` 不执行、事务注解没有代理。

## 创建主线与实例化文档

### `docs/bean-creation-mainline.md`

这篇应该写单个 Bean 从请求到最终暴露的创建链路。

正文按 `getBean -> doGetBean -> createBean -> doCreateBean -> populateBean -> initializeBean -> registerSingleton/exposed object` 推进。每个阶段都说明它解决的问题：缓存命中、父工厂委托、scope 判断、depends-on、实例化、属性填充、Aware、初始化回调、BPP 包装、最终暴露。

这篇可以完整解释主线，但要避免展开候选选择算法、三级缓存细节和 AOP 代理细节。提到这些机制时，只写它们在创建链路中的位置。

### `docs/pre-instantiation-short-circuit.md`

这篇应该写实例化前短路。

正文重点讲 `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` 如何在构造器执行前返回代理或替代对象，以及后续 `postProcessAfterInitialization` 如何配合。要解释这种机制和普通 after-init 包装的区别。

适合围绕“为什么构造器没有执行但容器里有 Bean”这个现象写。不要泛泛展开所有 BeanPostProcessor。

### `docs/constructor-and-factory-instantiation.md`

这篇应该写 Bean 的实例化方式。

正文应覆盖默认构造器、有参构造器、构造器自动装配、静态工厂方法、实例工厂方法、Supplier、`@Bean` 方法、FactoryBean 产品创建的差异。重点讲容器如何决定用哪种方式拿到原始实例，以及构造器参数如何进入依赖解析。

如果不单独生成这篇，也要把这些内容并入 `bean-creation-mainline.md`；但为了“每个点独立文档”，建议单独生成。

### `docs/configuration-and-bean-method.md`

这篇应该写 `@Configuration` 与 `@Bean` 方法语义。

正文重点讲 full configuration class、lite configuration、`proxyBeanMethods`、CGLIB 增强、`@Bean` 方法直接调用和容器调用的区别。要解释为什么同一个 `@Bean` 方法在不同配置模式下可能产生不同对象复用行为。

不要把它写成 Java Config 注解清单。核心问题是：`@Bean` 方法如何变成 BeanDefinition，以及配置类代理如何维护 singleton 语义。

### `docs/method-injection.md`

这篇应该写 lookup-method 和 replaced-method。

正文重点解释方法注入解决什么问题：singleton 中按需获取 prototype、运行时替换方法实现。要说明它依赖容器生成子类或方法覆盖，因此和 final class/method、AOT/Native 有明显边界。

不要把它写成推荐实践。应明确这是历史机制和特殊场景机制，现代代码通常优先考虑 `ObjectProvider` 等更直接的方式。

## 依赖注入与候选选择文档

### `docs/dependency-injection-resolution.md`

这篇应该写依赖解析的完整主线。

正文围绕“注入点向容器提出需求”展开：构造器参数、字段、方法参数如何形成 descriptor；容器如何按类型找候选；如何过滤候选；如何处理集合、Map、数组、Provider、Optional；单值注入如何收敛；失败时抛出什么异常。

这篇是依赖注入主线文档，可以讲完整流程，但不要深入每个注解的所有边界。具体注解差异放到对应专题。

### `docs/dependency-descriptor-and-injection-point.md`

这篇应该写注入点元数据。

正文重点说明 `DependencyDescriptor` 中保存了什么：依赖类型、泛型、required、注解、方法参数、字段、eager/lazy、containing class。还要说明 `InjectionPoint` 什么时候可用，为什么有些场景能拿到当前注入点。

适合和排障结合：看 descriptor 就是在看“注入点到底问容器要什么”。

### `docs/autowire-candidate-selection.md`

这篇应该写自动装配候选选择算法。

正文按决策过程写：收集类型候选、排除 `autowireCandidate=false`、处理 qualifier、自定义 qualifier、primary、priority、fallback/default candidate、名称匹配、集合排序、失败异常。要特别讲清 `@Order` 和单值候选选择不是一回事。

这篇可以细讲规则组合和边界情况，不要只罗列注解定义。

### `docs/optional-and-provider-injection.md`

这篇应该写可选注入和延迟获取。

正文应区分 `required=false`、`@Nullable`、`Optional<T>`、`ObjectProvider<T>`、`ObjectFactory<T>`、JSR-330 `Provider<T>`。重点比较它们在失败时机、是否延迟解析、是否每次重新取、是否支持 stream/ifAvailable、是否触发 Bean 创建上的差异。

适合围绕“没有候选时是否失败”和“什么时候真正创建依赖”来组织。

### `docs/resource-vs-autowired.md`

这篇应该写 `@Resource` 与 `@Autowired` 的本质差异。

正文重点讲 `@Resource` 的 name-first 语义、默认名称来源、按名称失败后的类型解析，以及 `@Autowired` 的 by-type 语义、qualifier 和名称收敛。要解释为什么同一组 Bean 下两个注解可能注入不同对象。

不要写成“哪个更好”的经验贴。要写容器解析规则和排障判断。

### `docs/qualifier-primary-priority-order.md`

这篇应该写 `@Qualifier`、`@Primary`、`@Priority`、`@Order` 各自控制哪一步。

正文要把四者放在候选选择流程里解释：qualifier 过滤候选，primary 选择优先普通候选，priority 参与单值优先级，order 主要影响集合/数组排序。重点纠正“`@Order` 解决单 Bean 冲突”的误区。

如果不单独生成这篇，相关内容必须在 `autowire-candidate-selection.md` 中足够完整。

### `docs/resolvable-dependency.md`

这篇应该写 resolvable dependency。

正文重点讲为什么某些对象可以注入但不是 Bean，例如 `BeanFactory`、`ApplicationContext`、`ResourceLoader`、`Environment` 等。要说明它们通过 `registerResolvableDependency` 或容器特殊逻辑参与依赖解析，不进入普通 BeanDefinition 注册表。

排障重点是：不要用 BeanDefinition 是否存在判断这类依赖能否注入。

### `docs/generic-type-matching.md`

这篇应该写泛型如何参与自动装配。

正文重点讲 `ResolvableType`、字段/方法参数泛型、集合元素类型、`Map<String, T>`、FactoryBean 类型、代理类型擦除或失真的影响。要解释为什么 `Repository<User>` 和 `Repository<Order>` 可以成为不同候选。

适合写代理、桥接方法、原始类型导致匹配不准的边界。

### `docs/custom-qualifier.md`

这篇应该写自定义 qualifier。

正文重点讲元注解、属性匹配、自定义 `AutowireCandidateResolver` 或 qualifier 解析逻辑。要说明它和普通 bean name、`@Primary` 的关系，以及 AOT 下自定义注解元数据可能需要额外提示。

## 生命周期、Scope 与销毁文档

### `docs/scope-and-prototype.md`

这篇应该写 scope 的复用和销毁边界。

正文应讲 singleton、prototype、request、session、application、自定义 scope 的本质差异。重点写 singleton 只代表容器内实例唯一，不保证线程安全；prototype 每次请求创建，但容器通常不负责完整销毁；singleton 注入 prototype 会固定住一个实例。

适合围绕“谁缓存、何时创建、谁销毁”组织。

### `docs/custom-scope-and-scoped-proxy.md`

这篇应该写自定义 Scope 和 scoped proxy。

正文要解释 Scope SPI 如何根据上下文保存和获取目标对象，scoped proxy 如何让长生命周期 Bean 安全引用短生命周期目标。重点讲调用方拿到的是代理，真实目标对象按当前 scope 上下文解析。

要覆盖上下文缺失、跨线程调用、目标对象销毁回调、代理类型选择等边界。

### `docs/lazy-semantics.md`

这篇应该写 lazy 的几种语义。

正文应区分 BeanDefinition lazy-init、`@Lazy` 标在 Bean 上、`@Lazy` 标在注入点上。重点讲它们分别延迟的是 Bean 创建、依赖解析还是注入代理，以及这对启动耗时、循环依赖、异常暴露时机的影响。

不要把 lazy 简化成启动优化开关。

### `docs/lifecycle-callbacks.md`

这篇应该写生命周期回调顺序。

正文按时间窗口组织：构造器、属性注入、Aware、`postProcessBeforeInitialization`、`@PostConstruct`、`afterPropertiesSet`、custom init、`postProcessAfterInitialization`、销毁回调。要说明每个窗口能看到什么状态，能不能拿到代理，异常会如何中断创建。

适合强调原始对象与最终暴露对象的差异。

### `docs/smart-initializing-singleton.md`

这篇应该写 `SmartInitializingSingleton`。

正文重点讲它在所有非懒 singleton 预实例化完成后回调，和单个 Bean init callback 不同。适合说明它用于需要观察容器内其他 singleton 已经就绪的场景。

要写清它不等于应用完全启动完成，也不覆盖 lazy Bean。

### `docs/smart-lifecycle.md`

这篇应该写 `SmartLifecycle`。

正文重点讲容器 start/stop 阶段、`autoStartup`、`phase`、启动顺序、停止顺序、异步 stop callback。要区分它和 Bean 初始化/销毁回调。

适合结合消息监听、连接管理、后台任务等场景说明。

### `docs/depends-on.md`

这篇应该写 `dependsOn`。

正文重点讲它表达初始化和销毁顺序，不表达依赖注入关系。要说明它如何影响 Bean 创建先后、销毁反向顺序，以及为什么它不能解决字段为空或候选冲突。

### `docs/destroy-dependency-graph.md`

这篇应该写销毁依赖图。

正文重点讲 dependent bean 关系如何记录，singleton 销毁顺序如何确定，`DisposableBean`、destroy method、`@PreDestroy`、dependent bean 反向销毁如何协作。

如果不单独生成这篇，相关内容应并入 `lifecycle-callbacks.md` 或 `depends-on.md`。

## 后处理器与扩展点文档

### `docs/post-processors-overview.md`

这篇应该写后处理器体系总览，但要以职责边界为主。

正文重点区分 BDRPP、BFPP、BPP、InstantiationAwareBPP、MergedBeanDefinitionPostProcessor、DestructionAwareBPP。核心问题是：它们改 BeanDefinition 还是改实例，发生在 refresh 哪个阶段，是否可能提前触发实例化。

这篇是后处理器入口文档，可以建立全局地图，但不要把每个接口细节都展开成长篇。

### `docs/beanfactory-post-processors.md`

这篇应该写 BeanFactoryPostProcessor。

正文重点讲 BFPP 在 Bean 实例化前修改已有 BeanDefinition 或 BeanFactory 配置。要说明它不应该获取普通 Bean，因为这可能提前触发实例化，导致 BPP 链不完整。

适合用 PropertySourcesPlaceholderConfigurer、ConfigurationClassPostProcessor 的不同职责边界作为例子。

### `docs/bdrpp-definition-registration.md`

这篇应该写 BeanDefinitionRegistryPostProcessor。

正文重点讲 BDRPP 为什么能在普通 BFPP 前新增、删除或改写 BeanDefinition。要说明它适合框架级注册扩展，例如扫描、Import、Mapper 注册一类机制。

如果不单独生成这篇，必须在 `post-processors-overview.md` 和 `bean-definition-registration.md` 中讲清。

### `docs/beanpost-processors.md`

这篇应该写 BeanPostProcessor 如何介入实例生命周期。

正文重点讲 before-init、after-init、InstantiationAwareBPP 的实例化前后窗口、属性注入窗口、DestructionAwareBPP 的销毁窗口。要解释代理、注解注入、生命周期注解为什么依赖 BPP。

不要把 BFPP 内容混进来。这里关注实例，不关注注册定义。

### `docs/post-processor-ordering.md`

这篇应该写后处理器排序。

正文重点讲 `PriorityOrdered`、`Ordered`、无序处理器的注册和执行顺序，以及手工添加 BPP 与自动检测 BPP 的排序差异。要说明排序错误如何影响代理、注入、配置解析。

### `docs/programmatic-bpp-registration.md`

这篇应该写编程式注册 BeanPostProcessor。

正文重点讲 `addBeanPostProcessor` 与声明为 Bean 的 BPP 差异：手工添加会立即进入链路，通常绕过容器自动排序和自动检测语义。适合讲框架内部使用和测试场景，不建议普通业务滥用。

### `docs/programmatic-registration.md`

这篇应该写编程式注册 Bean。

正文重点区分 `registerBeanDefinition`、`registerBean`、`registerSingleton`、`GenericApplicationContext#registerBean`。要说明注册的是定义还是实例，是否参与完整生命周期，是否能被后处理器增强，是否可被依赖解析发现。

## FactoryBean 与暴露对象文档

### `docs/factorybean.md`

这篇应该写 FactoryBean 的双重身份。

正文重点区分 FactoryBean 自身和 `getObject()` 生产的对象。要说明 `getBean("x")`、`getBean("&x")`、按类型查找、产品缓存、singleton/prototype 产品语义，以及 FactoryBean 初始化和产品创建不是同一件事。

适合围绕“容器最终暴露的是工厂还是产品”组织。

### `docs/factorybean-type-matching.md`

这篇应该写 FactoryBean 类型匹配。

正文重点讲 `getObjectType()` 的作用：按类型查找、自动装配、条件判断、AOT 推断都依赖它。要说明返回 null、返回不稳定类型、提前初始化 FactoryBean 的风险。

这篇要写清为什么 FactoryBean 的类型信息不是普通 class metadata 能完全解决的。

### `docs/built-in-factorybeans.md`

这篇应该写常见内置 FactoryBean。

正文重点不是列 API，而是说明几类内置 FactoryBean 解决什么问题：ServiceLoader、代理、JNDI、资源、事务、AOP 等。每类只讲容器语义：它生产什么对象、类型如何暴露、生命周期如何衔接。

## 循环依赖、三级缓存与代理文档

### `docs/circular-dependency.md`

这篇应该写循环依赖能解决什么、不能解决什么。

正文应区分构造器循环依赖、setter/field 循环依赖、prototype 循环依赖、AOP 代理参与循环依赖。重点讲 Spring 解决的是部分 singleton 属性注入循环依赖，不是所有对象图循环。

如果不单独生成这篇，核心内容需要并入 `early-reference-and-three-level-cache.md`。

### `docs/early-reference-and-three-level-cache.md`

这篇应该写 early reference 与三级缓存的协作。

正文不要只背 `singletonObjects`、`earlySingletonObjects`、`singletonFactories` 三个名字。要按对象流转写：原始实例创建、提前暴露 ObjectFactory、其他 Bean 解析依赖时拿 early reference、最终初始化完成后放入一级缓存。

重点讲 early reference 可能是代理，也可能是原始对象；以及什么时候会出现 raw injection despite wrapping。

### `docs/proxying-phase.md`

这篇应该写代理产生的阶段。

正文重点比较实例化前代理、early proxy、after-init proxy。要说明自动代理创建器如何借助 BPP 进入生命周期，为什么调用方拿到的可能不是原始对象，为什么自调用绕过代理。

适合结合事务、缓存、异步等注解解释“注解存在但行为不生效”的根因。

### `docs/self-invocation.md`

这篇应该写自调用失效。

正文重点讲代理模式下方法调用必须经过代理对象，`this.someMethod()` 不会经过代理，因此事务、缓存、异步等切面不触发。要区分 JDK proxy、CGLIB proxy 对自调用问题没有本质区别。

如果不单独生成这篇，相关内容必须在 `proxying-phase.md` 和 `transactional-bean.md` 中讲清。

### `docs/transactional-bean.md`

这篇应该写事务 Bean 的容器视角。

正文不要写事务传播大全，而要讲事务注解如何通过基础设施、Advisor、AutoProxyCreator、代理对象生效。重点讲代理创建时机、方法可见性、自调用、final 方法、调用方是否经过代理。

## 值解析、类型转换与外部输入文档

### `docs/environment-and-propertysource.md`

这篇应该写 Environment 与 PropertySource。

正文重点讲属性来源链、优先级、active profile、占位符解析的数据来源。要说明 Environment 是值解析输入，不等于 BeanDefinition 注册本身。

### `docs/value-placeholder-resolution.md`

这篇应该写 `${...}` 占位符解析。

正文重点讲 strict 与 non-strict 解析、默认值、嵌套占位符、`PropertySourcesPlaceholderConfigurer` 的参与阶段，以及占位符解析失败会在哪里暴露。

### `docs/spel-and-value-expression.md`

这篇应该写 `#{...}` SpEL 与 `@Value` 表达式。

正文重点讲占位符与 SpEL 的解析顺序、BeanExpressionResolver、类型转换、表达式访问 Bean 或 Environment 的边界。不要展开完整 SpEL 语法，那属于 SpEL 模块。

### `docs/type-conversion-and-beanwrapper.md`

这篇应该写属性绑定和类型转换。

正文重点讲 BeanWrapper、PropertyAccessor、ConversionService、PropertyEditor、TypeConverter 的职责边界。要说明构造器参数、属性填充、`@Value` 注入、XML 属性值如何经过转换。

### `docs/xml-bean-definition-reader.md`

这篇应该写 XML 如何变成 BeanDefinition。

正文应按读取链路写：Resource、EncodedResource、DocumentLoader、BeanDefinitionParserDelegate、`<bean>` 解析、默认命名、属性/构造器参数/集合/父子定义、注册。重点讲 XML 是 BeanDefinition 输入，不是另一套容器。

### `docs/properties-and-groovy-reader.md`

这篇应该写 Properties 和 Groovy BeanDefinitionReader。

正文重点比较这两类输入和 XML/注解的差异：表达能力、注册时机、资源加载、适用场景和维护成本。不要写成 Groovy 或 properties 语法手册。

### `docs/xml-namespace-extension.md`

这篇应该写 XML namespace 扩展。

正文重点讲 `spring.handlers`、`spring.schemas`、NamespaceHandler、BeanDefinitionParser、XSD、自定义标签到 BeanDefinition 的链路。要解释 namespace 扩展为什么常用于框架把复杂配置封装成简单标签。

### `docs/xml-fine-grained-definition-semantics.md`

这篇应该写 XML 细颗粒定义语义。

正文重点讲 constructor-arg、property、depends-on、autowire-candidate、parent、abstract、collection merge、lookup-method、replaced-method 等 XML 语义如何进入 BeanDefinition。不要复述 XML reader 主链路。

## 条件、Profile、父子容器与真实项目边界文档

### `docs/conditional-bean.md`

这篇应该写条件化 Bean。

正文重点讲 `@Conditional` 何时判断、ConditionContext 能看到什么、条件影响的是 BeanDefinition 是否注册还是 Bean 是否创建。要说明 Boot 条件注解也是这个模型上的扩展。

### `docs/profile.md`

这篇应该写 Profile。

正文重点讲 active/default profile、`@Profile` 如何影响注册、profile 表达式、配置类和 `@Bean` 方法上的差异。要明确 profile 不匹配通常意味着 BeanDefinition 根本没有进入容器。

### `docs/context-hierarchy.md`

这篇应该写父子容器。

正文重点讲 BeanFactory 查找的父委托、子容器覆盖父容器、事件传播、Environment 关系、Web 父子上下文常见问题。要说明依赖注入、`getBean`、按类型查找在父子层级中的可见性。

### `docs/boot-debugging-and-observability.md`

这篇应该写 Boot 环境下 Bean 问题如何观测。

正文重点讲 ConditionEvaluationReport、auto-configuration report、Actuator beans/conditions、日志、断点和 BeanDefinition origin。它是排障观测文档，不要重写自动配置原理。

## Spring Boot 自动配置文档

### `docs/boot-auto-configuration-beans.md`

这篇应该写 Boot 自动配置中的 Bean 如何出现或退让。

正文重点讲 auto-configuration import、条件注解、用户 Bean backoff、`@ConditionalOnMissingBean` 的判断时机、搜索策略、默认 Bean 与用户 Bean 的关系。要说明很多问题不是 Bean 创建失败，而是自动配置根本没有注册对应定义。

### `docs/boot-auto-configuration-ordering.md`

这篇应该写自动配置顺序。

正文重点讲 `@AutoConfigureBefore`、`@AutoConfigureAfter`、`@AutoConfigureOrder`、imports 文件顺序、条件判断时可见 BeanDefinition 集合。要解释顺序为什么会影响 backoff 和条件命中。

## AOT / Native 文档

### `docs/aot-native-overview.md`

这篇应该写 AOT/Native 对 Bean 机制的总体影响。

正文重点讲构建期分析如何提前处理 BeanDefinition、条件、反射、代理、资源、序列化和 RuntimeHints。要强调 JVM 可运行不代表 Native 可运行，因为 Native 限制动态反射、动态代理、资源扫描和运行时类生成。

不要写成 GraalVM 入门；它必须从 Spring Bean 机制角度写。

### `docs/aot-runtimehints.md`

这篇应该写 RuntimeHints。

正文重点讲反射、资源、代理、序列化、JNI 等 hint 如何补足构建期不可推断的信息。要结合 Bean 实例化、依赖注入、FactoryBean、SpEL、XML namespace 等场景说明 hint 缺失的表现。

### `docs/aot-xml-bean-definition-reader.md`

这篇应该写 AOT 下 XML reader 的边界。

正文重点讲 XML 资源可见性、命名空间处理器、反射构造、方法注入、类型推断在 Native 中的限制。不要重复 XML reader 的普通链路。

### `docs/aot-autowirecapablebeanfactory-external-objects.md`

这篇应该写 AOT 下容器外对象注入。

正文重点讲 `AutowireCapableBeanFactory` 给外部对象注入时，构建期是否能看见这些类型和反射需求。要说明运行时临时传入未知类型在 Native 中可能失败。

### `docs/aot-spel-and-value-expression.md`

这篇应该写 AOT 下 SpEL 和 `@Value`。

正文重点讲表达式访问类型、方法、字段、Bean、资源时对反射和资源 hint 的需求。不要讲完整 SpEL 语法。

### `docs/aot-custom-qualifier.md`

这篇应该写 AOT 下自定义 qualifier。

正文重点讲自定义注解元数据、运行时保留策略、反射访问和候选解析逻辑在 Native 中的边界。

### `docs/aot-xml-namespace-extension.md`

这篇应该写 AOT 下 XML namespace 扩展。

正文重点讲 NamespaceHandler、BeanDefinitionParser、自定义标签生成 BeanDefinition 时对类、资源和反射的要求。要说明普通 JVM 下动态加载成功，不代表 Native 下资源和类型都可见。

### `docs/aot-beandefinitionreader-other-inputs.md`

这篇应该写 AOT 下 Properties/Groovy 等其他输入。

正文重点讲动态脚本、运行时资源读取、Groovy 元编程和 Native 构建期封闭世界假设的冲突。要说明哪些输入适合作为构建期已知资源，哪些不适合 Native。

### `docs/aot-method-injection.md`

这篇应该写 AOT 下方法注入。

正文重点讲 lookup/replaced method 依赖运行时子类或方法覆盖，对 Native 类生成和反射可见性有额外要求。要说明为什么这种机制需要单独验证。

### `docs/aot-built-in-factorybeans.md`

这篇应该写 AOT 下内置 FactoryBean。

正文重点讲 FactoryBean 产品类型推断、ServiceLoader 资源、代理类、反射构造和 `getObjectType()` 对 AOT 的影响。

### `docs/aot-property-editor-and-value-resolution.md`

这篇应该写 AOT 下 PropertyEditor 和值解析。

正文重点讲 PropertyEditor 反射实例化、类型转换器注册、`@Value`、占位符、资源读取在 Native 中的限制。

## Guide 文档

Guide 文档只做学习路线、断点路线、命令编排和定位辅助。它们不是机制正文，不要把专题文档内容复制进去。

### `docs/guide-quickstart-30min.md`

这篇应该写 30 分钟快速闭环。

正文按时间安排读者做什么：先跑哪个 Lab，观察哪些输出，再读哪几个主干文档。它的价值是降低第一次进入模块的成本，不解释完整机制。

### `docs/guide-mainline-timeline.md`

这篇应该写主线时间线。

正文把配置输入、BeanDefinition、refresh、BFPP、BPP 注册、实例化、注入、初始化、代理、最终暴露、销毁串成一条线。每个节点只写观察点和代表入口，详细机制留给具体文档。

### `docs/guide-breakpoint-map.md`

这篇应该写断点地图。

正文按问题或阶段列断点：类、方法、关键变量、预期现象、对应 Lab。不要解释完整源码，只告诉读者在哪里停、看什么、怎么判断。

### `docs/guide-deep-dive-guide.md`

这篇应该写深挖路线。

正文按学习阶段组织，例如入门主线、容器内部、依赖解析、生命周期与代理、Boot/AOT。每阶段给阅读顺序和验证命令，不展开机制正文。

### `docs/guide-applicationcontext-refresh-call-chain.md`

这篇应该写 refresh 调用链导读。

正文只做 refresh 相关断点和调用链索引，把读者带到启动主线、基础设施、单 Bean 创建几个关键位置。不要复制 `refresh-mainline.md` 的正文。

### `docs/guide-branch-decision-matrix.md`

这篇应该写关键分支决策矩阵。

正文按现象列 If/Then：没有 BeanDefinition、候选冲突、拿到代理、FactoryBean 类型不匹配、循环依赖失败、自动配置退让。每条给最短观察入口。

### `docs/guide-why-index.md`

这篇应该写“为什么”问题索引。

正文按读者常见问题组织，例如“为什么有注解但没生效”“为什么 `getBean` 返回代理”“为什么加了 `@Lazy` 还创建了”。每个问题给简短判断和入口，不展开机制。

## Appendix 文档

Appendix 文档用于索引、速查、自检、排障和训练。它们可以引用主文档结论，但不要承载主机制正文。

### `docs/appendix-knowledge-map.md`

这篇应该写 owner 归属表。

正文按问题或症状映射到唯一主文档和最短 Lab。它的价值是定位，不是解释。每个知识点尽量只有一个 owner，避免读者不知道该看哪篇。

### `docs/appendix-common-pitfalls.md`

这篇应该写常见误区。

正文每条误区都要有错误理解、正确理解、如何验证。可以按定义层、创建层、注入层、生命周期、代理、Boot/AOT 分组。

### `docs/appendix-production-troubleshooting-checklist.md`

这篇应该写生产排障清单。

正文按排障顺序写：定义层、创建层、注入层、暴露层、行为层、Boot 条件层、AOT 层。每一步说明看什么证据，不写大段原理。

### `docs/appendix-debugger-pack.md`

这篇应该写调试包。

正文聚合常用断点组、入口测试和变量观察清单。适合给已经知道问题方向的人快速复制断点。

### `docs/appendix-explore-debug-tests.md`

这篇应该写 explore/debug 测试索引。

正文列出测试类、观察对象、适合验证的问题。不要解释测试背后的完整机制。

### `docs/appendix-glossary.md`

这篇应该写术语表。

正文给术语的一句话定义、所在阶段、必要时给一个最短例子。术语表不承担教程职责。

### `docs/appendix-interview-playbook.md`

这篇应该写面试复述结构。

正文把主文档结论组织成可口头表达的层次，例如“先讲主线，再讲扩展点，再讲边界”。不要为了面试简化到错误。

### `docs/appendix-self-check.md`

这篇应该写自检题。

正文用问题检查读者能否定位 owner、判断阶段、找到 Lab 和断点。答案应简洁，但要能指出判断依据。

### `docs/appendix-spring-beans-public-api-index.md`

这篇应该写 Spring Beans 公共 API 索引。

正文按 API 分类：BeanFactory、BeanDefinition、BeanWrapper、PropertyEditor、FactoryBean、Scope、后处理器。每个 API 说明用途和对应文档。

### `docs/appendix-spring-beans-public-api-gap.md`

这篇应该写公共 API 证据缺口。

正文记录哪些公共 API 还缺少 Lab 或文档支撑，作为维护清单。它不是读者教程。

### `docs/appendix-team-training-kit.md`

这篇应该写团队训练材料编排。

正文按课时组织阅读、Lab、讨论题和验收问题。它复用主文档，不重写主文档。

## Deepening 维护文档

`deepening-*.md` 是维护者文档，不是普通读者正文。它们应该写文档归属、维护风险、同步对象、验证命令。

### `docs/deepening-docs-root.md`

写 docs 根目录维护规则：扁平目录、README 顺序来源、链接契约、章节入口卡片契约。

### `docs/deepening-module-readme.md`

写模块 README 如何维护：入口顺序、最短命令、文档目录、避免 README 变成正文。

### `docs/deepening-ioc-container.md`

写容器与注册类文档的 owner 边界，避免 BeanDefinition、refresh、创建主线互相重复。

### `docs/deepening-container-internals.md`

写容器内部主线文档的维护边界，重点覆盖 refresh、创建、后处理器、循环依赖。

### `docs/deepening-wiring-and-boundaries.md`

写依赖解析、注入、Scope、FactoryBean、代理文档之间如何分工。

### `docs/deepening-boot-autoconfig.md`

写 Boot 自动配置文档和 Boot 排障文档的分工。

### `docs/deepening-aot-and-real-world.md`

写 AOT 文档与真实项目约束如何同步，哪些 JVM-only 结论不能直接迁移到 Native。

### `docs/deepening-appendix.md`

写 Appendix 文档如何只做索引、速查、自检和排障，不承担主机制解释。

### `docs/deepening-guide.md`

写 Guide 文档如何只做路线、断点和 Lab 编排。

### `docs/deepening-strategies.md`

写长期维护策略：owner 文档、support 文档、Lab 证据链、链接契约如何保持一致。

### `docs/deepening-module-rewrite-rationale.md`

写模块重写理由：为什么按知识点 owner 拆分，如何验收文档边界，如何避免回到大而全教程。

## 验收规则

生成具体文档后，至少检查：

- 每篇正文是否按知识点自然组织，而不是套统一模板。
- 每篇是否能独立读懂，不依赖 `KNOWLEDGE.md`。
- 跳转是否必要；不必要的“相邻主题”应删除。
- 文档中出现的 `SpringCoreBeans*Test` 是否真实存在。
- `docs/*.md` 是否在模块 `README.md` 中列出。
- 面向读者的 `docs/*.md` 是否包含章节入口卡片标记。
- 是否可以运行 `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test` 做文档契约验证。
