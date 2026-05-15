# 容器启动与基础设施 Bean
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这一页只回答：为什么注解处理器、自动装配和基础设施能够在容器里生效？
    - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBootstrapInternalsLabTest test`
    - 相邻主题只做跳转，不在本页重复展开。

    观察对象：注解处理器、内部 infrastructure Bean、role 标记和 bootstrap 注册窗口。
    主线位置：容器与注册。
    对照入口：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInfrastructureBeanRoleLabTest`。
<!-- CHAPTER-CARD:END -->

## 基础设施 Bean 先让容器具备能力

`@Autowired`、`@PostConstruct` 和 `@Configuration` 不是 `GenericApplicationContext` 天生就会处理的语法。它们分别依赖容器里已经注册好的处理器：

- `@Autowired` 需要 `AutowiredAnnotationBeanPostProcessor` 在属性填充阶段参与依赖注入。
- `@PostConstruct` 需要 `CommonAnnotationBeanPostProcessor` 在初始化前识别生命周期回调。
- `@Configuration` 和 `@Bean` 需要 `ConfigurationClassPostProcessor` 在工厂后处理器阶段解析配置类并注册新的 `BeanDefinition`。

这些处理器本身也是 bean definitions。bootstrap 的第一步不是创建业务 bean，而是先把这些“让容器会做事”的基础设施定义放进 `BeanFactory`。

## AnnotationConfigUtils 注册了哪些关键处理器

`AnnotationConfigUtils#registerAnnotationConfigProcessors` 是注解容器能力的集中注册入口。它会向 registry 放入一组内部处理器定义，其中最常见的是：

| 处理器 | 类型 | 主要能力 |
| --- | --- | --- |
| `ConfigurationClassPostProcessor` | `BeanDefinitionRegistryPostProcessor` / `BeanFactoryPostProcessor` | 解析 `@Configuration`、`@Bean`、`@ComponentScan`、`@Import` 等定义来源。 |
| `AutowiredAnnotationBeanPostProcessor` | `InstantiationAwareBeanPostProcessor` | 解析 `@Autowired`、`@Value`，参与属性注入。 |
| `CommonAnnotationBeanPostProcessor` | `BeanPostProcessor` / `InstantiationAwareBeanPostProcessor` | 解析 Jakarta Common Annotations，例如 `@PostConstruct`、`@PreDestroy`、`@Resource`。 |

这些 processor 的位置不同：`ConfigurationClassPostProcessor` 改的是定义集合；`AutowiredAnnotationBeanPostProcessor` 和 `CommonAnnotationBeanPostProcessor` 进入单个 bean 的创建链路。把它们都叫“基础设施 Bean”，不是说它们在同一个生命周期阶段执行，而是说它们共同提供容器能力。

## 没有这些处理器会发生什么

`SpringCoreBeansBootstrapInternalsLabTest` 用 `GenericApplicationContext` 做了最小对照。第一组测试只注册 `Dependency` 和 `Target`，然后直接 `refresh()`：

- `Target` 上的 `@Autowired` 字段保持 `null`。
- `@PostConstruct` 方法没有被调用。

同一个测试里先调用 `AnnotationConfigUtils.registerAnnotationConfigProcessors(context)`，再注册相同的业务类，`@Autowired` 和 `@PostConstruct` 就会生效。

配置类也是同样的道理。只把 `Config` 注册进 `GenericApplicationContext`，`@Bean exampleBean()` 不会自动变成 `ExampleBean` 的定义，查 `ExampleBean` 会得到 `NoSuchBeanDefinitionException`。注册 annotation config processors 后，`ConfigurationClassPostProcessor` 会在 `refresh()` 的工厂后处理器阶段解析配置类，`ExampleBean` 才进入定义表。

## ROLE_INFRASTRUCTURE 的排障价值

`BeanDefinition#ROLE_INFRASTRUCTURE` 是定义元数据里的分类标记，不是一个新的生命周期阶段。它的价值在排障和源码阅读：

- 列 bean definitions 时，可以快速区分“业务定义”和“容器能力定义”。
- 追踪某个注解为什么生效时，可以先确认对应 processor definition 是否存在。
- 打印来源时，role 能帮助判断当前看到的是用户 bean，还是 Spring 为上下文启动注册的内部处理器。

`SpringCoreBeansInfrastructureBeanRoleLabTest` 注册 annotation config processors 后，读取 `AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME` 和 `AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME` 的 `BeanDefinition`，断言它们都是 `BeanDefinition.ROLE_INFRASTRUCTURE`；同一个上下文里的 `userService` 仍然是 `ROLE_APPLICATION`。

## 源码阅读顺序

建议按“注册定义 -> 执行定义级处理器 -> 执行实例级处理器”的顺序看：

1. `AnnotationConfigUtils#registerAnnotationConfigProcessors`：看内部 processor definitions 的 bean name、role 和 class。
2. `ConfigurationClassPostProcessor`：看 `@Configuration` / `@Bean` 解析怎样发生在 `BeanFactoryPostProcessor` 窗口。
3. `AutowiredAnnotationBeanPostProcessor`：看 `@Autowired` 元数据怎样在属性填充阶段被解析和注入。
4. `CommonAnnotationBeanPostProcessor`：看 `@PostConstruct` 怎样接入初始化前回调。
5. `BeanDefinition#ROLE_INFRASTRUCTURE`：回到定义元数据，确认 role 只是分类，不决定执行时机。

如果要把这些处理器放回完整启动顺序，接着读 [refresh-mainline.md](refresh-mainline.md)。

## 用本模块怎么验证

最短命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBootstrapInternalsLabTest test
```

重点看三个方法：

- `withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied()`：证明 `@Autowired` 和 `@PostConstruct` 不会在裸 `GenericApplicationContext` 中自动生效。
- `registerAnnotationConfigProcessors_enablesAutowiredAndPostConstruct()`：证明注册基础设施处理器后，上述注解能力接入创建流程。
- `configurationClassIsNotParsedWithoutConfigurationClassPostProcessor()`：证明 `@Bean` 解析依赖 `ConfigurationClassPostProcessor`。

再运行：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansInfrastructureBeanRoleLabTest test
```

这个 Lab 用 role 对照基础设施定义和业务定义，适合配合 `BeanDefinitionOriginDumper.dump(...)` 看来源。

## 相邻主题

- [refresh-mainline.md](refresh-mainline.md)：这些处理器在 `refresh()` 的哪个窗口注册和执行。
- [post-processors-overview.md](post-processors-overview.md)：BFPP、BDRPP、BPP 的职责边界。
- [bean-creation-mainline.md](bean-creation-mainline.md)：`AutowiredAnnotationBeanPostProcessor` 等实例级处理器怎样参与单 bean 创建。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
