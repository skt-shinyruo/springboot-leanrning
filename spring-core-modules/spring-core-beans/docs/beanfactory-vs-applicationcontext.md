# BeanFactory 与 ApplicationContext：按行为看容器边界

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文按行为解释 BeanFactory 和 ApplicationContext，而不是只讲继承关系。
    - 覆盖创建时机、refresh 生命周期、资源、环境、事件、国际化、基础设施注册和 singleton 预实例化。
    - 重点回答：为什么同样调用 `getBean()`，ApplicationContext 启动时已经做了更多事情。

    观察对象：核心 Bean 容器与应用级上下文设施的行为差异。
    主线位置：容器抽象选择和 refresh 启动边界。
    对照入口：`SpringCoreBeansBeanFactoryVsApplicationContextLabTest`、`SpringCoreBeansContainerLabTest`。
<!-- CHAPTER-CARD:END -->

`BeanFactory` 是 Spring IoC 的核心容器接口：保存 BeanDefinition，按需创建 Bean，处理依赖关系和生命周期基础步骤。`ApplicationContext` 建立在这个核心之上，把资源加载、环境、事件、国际化、应用级生命周期和一批基础设施装配纳入统一的 refresh 主线。

所以二者的差异不只是“低级/高级”。真正影响排障的是行为：什么时候加载定义，什么时候注册后处理器，什么时候创建 singleton，能不能发布事件，能不能解析 message，能不能直接读取资源。

## 行为对照

| 行为 | BeanFactory | ApplicationContext |
| --- | --- | --- |
| BeanDefinition 注册 | 支持，通常由调用方或 reader 操作 | refresh 前半段组织 reader、scanner、配置类解析等流程 |
| Bean 创建时机 | 通常按 `getBean()` 懒触发 | refresh 末尾会预实例化非懒 singleton |
| 后处理器装配 | 可手工添加或注册 | refresh 中自动发现、排序、注册并应用 |
| Environment | 不是核心职责 | 上下文标准能力，可被条件、占位符、selector 使用 |
| ResourceLoader | 不是核心职责 | `getResource()` 等资源能力是上下文能力 |
| ApplicationEventPublisher | 不提供 | 可注册监听器并发布上下文事件 |
| MessageSource | 不提供 | 可按 locale 解析国际化消息 |
| 应用生命周期 | 无统一 refresh 完成事件 | 有 refresh、start/stop、close 等上下文边界 |

`SpringCoreBeansBeanFactoryVsApplicationContextLabTest` 的第一个实验用 `DefaultListableBeanFactory` 注册 `RootBeanDefinition` 后直接 `getBean()`，证明 BeanFactory 可以独立创建 Bean，但它不是 `ApplicationEventPublisher`、`MessageSource` 或 `ResourceLoader`。

第二个实验用 `GenericApplicationContext` 注册 `messageSource`、listener 并执行 `refresh()`，证明 ApplicationContext 同时具备事件、资源、环境和国际化能力，且会在 refresh 完成时发布 `ContextRefreshedEvent`。

## 创建时机差异

裸 BeanFactory 更接近按需容器。注册定义后，如果没有调用 `getBean()`，普通 singleton 通常不会被创建。调用 `getBean()` 时，BeanFactory 才沿创建链路解析依赖、实例化、初始化并返回对象。

ApplicationContext 的默认启动体验不同。`refresh()` 末尾会调用 singleton 预实例化逻辑，提前创建所有非 lazy singleton。这也是为什么很多 Spring 应用在启动期就暴露构造器错误、依赖缺失或初始化失败，而不是等第一次业务请求才失败。

lazy Bean、prototype Bean 和某些 FactoryBean 产品仍有自己的创建边界。ApplicationContext 并不意味着所有对象都会在启动时创建；它只是把非懒 singleton 的创建纳入 refresh 完成前的主线。

## refresh 带来的容器状态变化

ApplicationContext 的关键不是“包了一层 BeanFactory”，而是 `refresh()`。refresh 会准备环境，创建或刷新内部 BeanFactory，加载 BeanDefinition，执行 BeanDefinitionRegistryPostProcessor 和 BeanFactoryPostProcessor，注册 BeanPostProcessor，初始化 message source、event multicaster 和 listeners，最后预实例化 singleton 并发布完成事件。

这些步骤解释了一个常见现象：同样是 `getBean()`，在 ApplicationContext 中取 Bean 时，很多基础设施早已就位；而在手工 BeanFactory 中，如果你没有添加对应处理器，注解注入、生命周期注解或 ApplicationContextAware 回调可能不会发生。

`SpringCoreBeansContainerLabTest` 中 BeanFactoryPostProcessor 修改定义、BeanPostProcessor 修改实例的实验说明了这些处理器会改变容器行为。ApplicationContext 会把处理器发现和注册流程标准化；BeanFactory 则需要调用方自己装配。

## 资源、环境、事件和国际化

ApplicationContext 统一了很多 BeanFactory 不关心的应用级能力。

Environment 提供属性、profile 和系统环境输入，供条件注册、占位符和 import selector 使用。ResourceLoader 让上下文用统一语法读取 classpath、文件或其他资源。MessageSource 让容器按 locale 解析消息。ApplicationEventPublisher 让 Bean 和框架发布应用事件，并由监听器接收。

这些能力不是 Bean 创建本身的必要条件，但它们影响真实应用中的注册和运行。例如条件注册可能依赖 Environment；配置文件加载依赖 Resource；事件监听器需要 event multicaster；国际化错误消息依赖 MessageSource。

## 基础设施自动装配

ApplicationContext 常见子类会在合适时机注册或发现基础设施。注解配置上下文会注册配置类处理器、自动装配处理器、CommonAnnotationBeanPostProcessor 等，让 `@Configuration`、`@Bean`、`@Autowired`、`@PostConstruct` 等能力参与 refresh。

这不是 BeanFactory 天然具备的魔法。BeanFactory 能执行 BeanPostProcessor，但处理器是否存在、顺序是否正确、是否足够早注册，取决于调用方或 ApplicationContext refresh 主线。

## 选择和排障

当你只需要一个可编程的 IoC 核心、希望精确控制定义注册和对象创建时，BeanFactory 足够直接。测试某个创建分支、手工注册少量定义、验证候选选择时，`DefaultListableBeanFactory` 经常更清晰。

当你需要接近真实应用的行为，包括配置类解析、Environment、事件、资源、message source、基础设施处理器和启动期 singleton 校验时，应使用 ApplicationContext。多数“Spring 应用里为什么这样”的问题都必须放在 refresh 后的 ApplicationContext 里观察。

排障时先确认自己面对的是哪一种容器行为。如果使用 BeanFactory 复现实验，缺少注解处理器可能是实验环境问题；如果使用 ApplicationContext，则要把 refresh 阶段已经做过的注册、处理和预实例化纳入判断。
