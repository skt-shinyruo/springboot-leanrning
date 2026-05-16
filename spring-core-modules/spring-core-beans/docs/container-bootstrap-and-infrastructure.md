# 容器启动与基础设施：注解能力为什么会生效

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释注解处理、依赖注入、生命周期注解、事件、类型转换和 AOP 为什么都是基础设施支撑的容器能力。
    - 重点说明基础设施何时注册，以及何时开始影响普通 Bean。
    - 读完后应能判断“注解存在但不生效”是业务代码问题还是基础设施缺失。

    观察对象：配置类处理器、自动装配处理器、生命周期处理器、事件设施、转换设施和代理设施。
    主线位置：refresh 前半段注册基础设施，普通 Bean 创建阶段应用基础设施。
    对照入口：`SpringCoreBeansBootstrapInternalsLabTest`、`SpringCoreBeansInfrastructureBeanRoleLabTest`、`SpringCoreBeansAwareInfrastructureLabTest`。
<!-- CHAPTER-CARD:END -->

Spring 的很多能力看起来像“注解天然生效”，实际都依赖容器基础设施。`@Configuration` 能解析 `@Bean`，是因为配置类处理器参与 BeanDefinition 阶段；`@Autowired` 能注入字段，是因为自动装配处理器进入 BeanPostProcessor 链；`@PostConstruct` 能执行，是因为生命周期注解处理器在初始化前窗口调用它；AOP 能创建代理，是因为自动代理创建器作为后处理器参与实例生命周期。

基础设施不是业务 Bean 的附属说明，而是一组会改变容器行为的 BeanDefinition、BeanPostProcessor、BeanFactoryPostProcessor、事件组件和解析组件。

## 缺少基础设施时会发生什么

`SpringCoreBeansBootstrapInternalsLabTest` 给出最小对照：在 `GenericApplicationContext` 中只注册 `Dependency` 和 `Target`，不注册 annotation config processors，refresh 后 `@Autowired` 字段仍为 null，`@PostConstruct` 也没有执行。

同一个实验中调用 `AnnotationConfigUtils.registerAnnotationConfigProcessors(context)` 后，`@Autowired` 和 `@PostConstruct` 才开始工作。配置类也是同样逻辑：没有 `ConfigurationClassPostProcessor` 时，注册一个带 `@Bean` 方法的配置类并不会自动生成 `ExampleBean` 的 BeanDefinition；注册注解处理器后，`@Bean` 方法才会被解析为定义。

这说明注解本身只是元数据。容器必须有对应处理器读取元数据，并在正确阶段应用它。

## 基础设施注册阶段

基础设施大致分三类进入容器。

第一类是定义级处理器，例如 `ConfigurationClassPostProcessor`。它们通常作为 BeanDefinitionRegistryPostProcessor 或 BeanFactoryPostProcessor 在 refresh 前半段执行，负责解析配置类、扫描、import 和 `@Bean` 方法，甚至继续新增 BeanDefinition。

第二类是实例级处理器，例如自动装配处理器、CommonAnnotationBeanPostProcessor、自动代理创建器。它们作为 BeanPostProcessor 在普通 Bean 创建前注册到 BeanFactory，随后在实例化、属性填充、初始化前后或销毁阶段介入。

第三类是上下文级设施，例如 message source、event multicaster、listeners、conversion service、environment 和 resource loader。它们不是都表现为普通业务 Bean，但会被 refresh 主线接入上下文和 BeanFactory。

## 基础设施什么时候影响普通 Bean

定义级基础设施先影响 registry。没有配置类处理器，就没有从 `@Bean` 方法派生出来的 BeanDefinition；没有 scanner 相关流程，就不会有扫描注册的组件。

实例级基础设施影响创建过程。自动装配处理器在属性填充阶段解析注入点；生命周期注解处理器在初始化前调用 `@PostConstruct`；自动代理创建器可能在实例化前短路、early reference 或 after-init 阶段返回代理。

上下文级设施影响应用行为。事件广播器在事件发布时分发监听器；conversion service 在属性绑定、值注入和类型转换中被使用；Environment 为条件、profile、占位符和 import selector 提供输入。

顺序很重要。普通 Bean 如果在 BPP 链完整前被提前创建，可能错过代理、注入或生命周期处理。这类问题通常表现为“只有某些 Bean 注解不生效”，根因是过早实例化或处理器注册时机不对。

## role：基础设施 Bean 的可观察标记

`SpringCoreBeansInfrastructureBeanRoleLabTest` 说明通过 `AnnotationConfigUtils.registerAnnotationConfigProcessors` 注册的处理器通常标记为 `BeanDefinition.ROLE_INFRASTRUCTURE`，而普通 `userService` 是 `ROLE_APPLICATION`。

role 的价值是观测和分类。排查 Bean 列表时，先把基础设施 Bean 和应用 Bean 分开，可以避免把 `AutowiredAnnotationBeanPostProcessor`、配置类处理器、自动代理创建器这类容器能力对象误认为业务对象。

role 不表示这个 Bean 不重要。相反，很多业务注解能否生效正取决于这些 role 为 infrastructure 的对象是否存在、是否足够早注册、顺序是否正确。

## Aware 回调也有边界

`SpringCoreBeansAwareInfrastructureLabTest` 展示了一个细分边界：`BeanFactoryAware` 可以由 BeanFactory 创建流程直接回调；`ApplicationContextAware` 需要 ApplicationContext 相关的 BeanPostProcessor 介入。

在裸 `DefaultListableBeanFactory` 中，`AwareBean` 可以拿到 BeanFactory，但拿不到 ApplicationContext。手工添加一个 `ManualApplicationContextAwareProcessor` 后，`ApplicationContextAware` 才被调用。这个实验说明：即使都是 Aware，也要区分哪些是 BeanFactory 原生处理，哪些依赖上下文基础设施。

## 注解能力与基础设施对照

| 能力 | 依赖的基础设施类型 | 缺失时的典型现象 |
| --- | --- | --- |
| `@Configuration` / `@Bean` | 配置类处理器，定义级后处理器 | 配置类本身是 Bean，但 `@Bean` 方法没有变成定义 |
| `@Autowired` | 自动装配 BeanPostProcessor | 字段或方法参数没有注入 |
| `@PostConstruct` / `@PreDestroy` | 生命周期注解处理器 | 初始化或销毁回调不执行 |
| ApplicationContextAware | ApplicationContextAwareProcessor | BeanFactoryAware 有值，但 ApplicationContextAware 为空 |
| 事件监听 | event multicaster 和 listeners 注册 | 事件发布后没有监听器响应 |
| 类型转换 | conversion service、type converter、property editor | 字符串或配置值无法转换为目标类型 |
| AOP | 自动代理创建器、advisor、proxy factory | 注解存在但调用没有经过代理行为 |

## 排障顺序

遇到“注解不生效”时，先确认基础设施，而不是先怀疑注解拼写：

1. 使用的是裸 BeanFactory、GenericApplicationContext，还是带注解启动能力的 ApplicationContext？
2. annotation config processors 是否注册到 registry？
3. BeanPostProcessor 链在普通 Bean 创建前是否已经安装完成？
4. 目标 Bean 是否被过早创建，导致错过 BPP？
5. 基础设施 Bean 的 role、顺序和来源是否符合预期？
6. 如果是 AOP，调用方拿到并调用的是代理还是 raw target？

基础设施视角能解释很多“同样的注解在这里有效、在那里无效”的问题：差别不在注解，而在容器启动过程中是否把对应能力装配进来了。
