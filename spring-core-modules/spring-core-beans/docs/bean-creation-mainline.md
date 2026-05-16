# Bean 创建主线：从 getBean 到最终暴露对象

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释单个 Bean 从 `getBean` 请求到最终暴露对象的创建主线。
    - 覆盖缓存命中、父工厂委托、scope、depends-on、实例化、属性填充、Aware、初始化回调、BeanPostProcessor 包装和 exposed object。
    - 本文只标出候选选择、三级缓存和 AOP 代理在创建链路中的位置，不展开它们的内部算法。

    观察对象：`getBean -> doGetBean -> createBean -> doCreateBean -> populateBean -> initializeBean -> exposed object`。
    主线位置：BeanDefinition 已注册之后，调用方拿到 Bean 之前。
    对照入口：`SpringCoreBeansBeanCreationTraceLabTest`、`SpringCoreBeansMainlineCallChainLabTest`。
<!-- CHAPTER-CARD:END -->

单个 Bean 的创建不是一次简单的构造器调用。`getBean()` 只是请求入口，容器需要先判断这个名字是否已经有可用对象，是否应该委托父工厂，是否受 scope 管理，是否必须先创建依赖，再决定如何拿到原始实例、填充属性、执行回调、应用后处理器，最后把可以对外暴露的对象放到缓存或 scope 中。

这条主线可以压缩成一条链：

```text
getBean
-> doGetBean
-> createBean
-> doCreateBean
-> populateBean
-> initializeBean
-> exposed object
```

读源码时不要只盯着构造器。很多“为什么拿到的是代理”“为什么字段初始化时还是 null”“为什么某个依赖先创建”的答案，都在构造器之前或之后的阶段里。

## 1. `getBean`：把外部请求规范化

`BeanFactory#getBean` 面向调用方：按名称、类型或参数获取 Bean。进入 `AbstractBeanFactory#doGetBean` 前后，容器会把别名解析成 canonical name，并处理 FactoryBean 的 `&` 前缀语义。

这个阶段的核心问题是：调用方到底要哪个名字下的对象。名字定位错误时，后面的创建算法再正确也只会创建或返回另一个 Bean。

## 2. `doGetBean`：先查已有对象，再决定是否创建

`doGetBean` 是请求收敛点。它首先检查 singleton 缓存：如果目标 Bean 已经创建完成，通常直接返回 cached singleton，再按需要处理 FactoryBean 产品或类型转换。

如果缓存未命中，`doGetBean` 会继续判断：

- 当前 BeanFactory 是否有本地 BeanDefinition；没有时是否应该委托 parent factory。
- Bean 是否已经处于创建中，是否存在循环依赖相关的早期引用机会。
- BeanDefinition 的 `depends-on` 是否要求先创建其他 Bean。
- 当前 Bean 的 scope 是 singleton、prototype，还是自定义 scope。

父工厂委托发生在本地找不到定义时。scope 决定对象复用和缓存边界：singleton 走共享实例缓存，prototype 每次创建新对象，自定义 scope 由 scope 实现决定对象获取和保存。`depends-on` 不是注入，它只声明创建顺序；即使属性上没有引用，容器也会先触发被依赖 Bean 的创建。

## 3. `createBean`：进入可被实例化前后处理器影响的窗口

`AbstractAutowireCapableBeanFactory#createBean` 是真正创建前的入口。这里会拿到 merged `RootBeanDefinition`，解析 bean class，并给实例化前后处理器机会。

最特殊的分支是实例化前短路：`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` 可以在构造器执行前直接返回替代对象。若返回非 null，默认构造、属性填充和普通初始化链路会被跳过，容器随后通常只应用 after-init 后处理。这个分支的完整解释属于 [pre-instantiation-short-circuit.md](pre-instantiation-short-circuit.md)。

如果没有短路，创建继续进入 `doCreateBean`。

## 4. `doCreateBean`：原始实例、属性和初始化的主线

`doCreateBean` 可以理解为三段：

```text
createBeanInstance
-> populateBean
-> initializeBean
```

`createBeanInstance` 负责拿到 raw instance。来源可能是构造器、工厂方法、supplier 或其他实例化策略。构造器参数如果需要自动装配，会在这里触发依赖解析；字段和 setter 注入还没发生。

singleton 循环依赖相关的 early reference 也在 `doCreateBean` 附近布置，但本文不展开三级缓存。这里先记住位置：原始实例创建后、属性填充前，容器可能为了打破 setter/field 循环依赖而提前暴露一个可获取早期引用的工厂。

## 5. `populateBean`：依赖注入和属性填充

`populateBean` 解决“原始实例需要哪些属性和注入点”的问题。传统 `PropertyValues`、`@Autowired` 字段、`@Autowired` 方法、`@Resource` 字段等都在这个大阶段被处理。自动装配处理器会把字段、方法参数等注入点包装成 `DependencyDescriptor`，再交给 BeanFactory 做依赖解析。

`SpringCoreBeansBeanCreationTraceLabTest` 用事件顺序固定了这个阶段：`service:constructed` 后，`postProcessProperties` 能看到 property values 中包含 `dependency`，但 setter 还没有执行；随后才出现 `service:setDependency`。这说明属性填充发生在构造器之后、初始化回调之前。

构造器注入不会等到这个阶段才获得依赖。`SpringCoreBeansMainlineCallChainLabTest` 聚合了主线 Lab，可以把构造器依赖、属性填充和初始化回调放到同一条创建链里观察。

## 6. `initializeBean`：Aware、初始化回调和 BPP 包装

`initializeBean` 处理创建后的初始化语义，典型顺序是：

```text
Aware callbacks
-> BeanPostProcessor#postProcessBeforeInitialization
-> init callbacks
-> BeanPostProcessor#postProcessAfterInitialization
```

Aware 回调让 Bean 获得容器相关对象，例如 `BeanNameAware`、`BeanFactoryAware`。其中部分 ApplicationContext 相关 Aware 依赖上下文安装的基础设施处理器。

初始化回调包括 `InitializingBean#afterPropertiesSet`、自定义 init method、`@PostConstruct` 等。不同入口由不同处理器或适配逻辑触发，但它们共同的边界是：属性填充已经完成，最终暴露对象还未必确定。

after-init `BeanPostProcessor` 可以返回包装对象。这个分支说明：`initializeBean` 结束时，容器对外暴露的对象未必还是原始实例，因此按具体类型取 Bean 时可能出现与原始类不匹配的结果。

## 7. exposed object：缓存和对外可见身份

创建完成后，容器保存和返回的是 exposed object。它可能就是 raw instance，也可能是 after-init 后处理器返回的代理或替代对象。对 singleton 来说，它会进入 singleton 缓存；对 prototype 来说，容器通常只负责创建和初始化，不保存共享实例；对自定义 scope 来说，由 scope 实现保存和返回对象。

这也是排障时最容易混淆的地方：构造器里 `this`、初始化回调里的对象、依赖方注入到的对象、最终 `getBean()` 返回的对象不一定是同一个 Java object。本文只解释主线位置；如果要追 early reference 与代理一致性，需要单独看循环依赖和代理阶段专题。

## 常见断点和判断

| 问题 | 先看哪里 |
| --- | --- |
| 为什么没有执行构造器 | `createBean` 的 before-instantiation 后处理器是否返回了对象 |
| 为什么依赖先被创建 | `doGetBean` 的 `depends-on` 和构造器/属性依赖解析 |
| 为什么字段在构造器里是 null | 字段注入发生在 `populateBean`，晚于构造器 |
| 为什么 `@PostConstruct` 能看到依赖 | 初始化回调晚于属性填充 |
| 为什么 `getBean` 返回代理 | after-init BPP 或 early reference 相关处理改变了 exposed object |
| 为什么同一个 Bean 多次返回不同对象 | scope 不是 singleton，或获取的是 FactoryBean 产品等特殊语义 |

创建主线的稳定读法是先确认阶段，再确认对象身份：当前看到的是定义、raw instance、正在填充的实例、初始化中的实例，还是最终 exposed object。
