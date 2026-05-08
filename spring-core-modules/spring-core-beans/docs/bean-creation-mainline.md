# Bean 创建主线：doGetBean 到 doCreateBean
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这一页只回答：`doGetBean()` / `doCreateBean()` 的主线是什么？
    - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest test`
    - 相邻主题只做跳转，不在本页重复展开。

    观察对象：依赖解析、实例化、属性填充、初始化、最终暴露对象的单 Bean 创建路径。
    主线位置：容器与注册。
    对照入口：`SpringCoreBeansBeanCreationTraceLabTest`。
<!-- CHAPTER-CARD:END -->

## 这条线从 getBean 开始

单个 bean 的创建主线可以先记成：

```text
getBean -> doGetBean -> createBean -> doCreateBean
```

`refresh()` 的 `finishBeanFactoryInitialization` 会通过 `preInstantiateSingletons` 批量触发非懒加载单例，但进入某一个 bean 后，核心路径仍然是上面这条线。外部显式调用 `context.getBean(...)` 时也是同一个入口，只是触发时机不同。

这页只看单个 bean 从“名字或类型查找”到“最终对象暴露”的主线。候选依赖如何选择，放到 [dependency-injection-resolution.md](dependency-injection-resolution.md)；代理创建细节，放到 [proxying-phase.md](proxying-phase.md)。

## doGetBean 决定拿缓存还是创建

`AbstractBeanFactory#doGetBean` 先处理名字规范化、`FactoryBean` 前缀、父子工厂委托、depends-on、scope 等分支。对 singleton 来说，它会先尝试从 singleton 缓存拿已有对象；缓存没有命中时，才进入创建回调。

这个阶段的判断点是“要不要创建”：

- 已经创建过的 singleton，直接复用缓存中的运行时对象。
- 正在创建中的 singleton，某些循环依赖场景可能拿到 early reference。
- 没有现成对象时，根据 merged `BeanDefinition` 进入 `createBean`。

所以 `doGetBean` 不是简单的构造器包装器。它先决定查找、复用、父工厂委托、scope 和创建入口，之后才轮到 `AbstractAutowireCapableBeanFactory#createBean`。

## doCreateBean 的几个窗口

`AbstractAutowireCapableBeanFactory#createBean` 做创建前解析和短路机会，然后进入 `doCreateBean`。`doCreateBean` 可以按三个大窗口读：

1. 实例化：通过构造器、工厂方法或供应器拿到原始对象。
2. 属性填充：调用 `populateBean`，让 `InstantiationAwareBeanPostProcessor` 和依赖注入逻辑参与。
3. 初始化：调用 `initializeBean`，执行 aware、before-init BPP、init callbacks、after-init BPP。

`SpringCoreBeansBeanCreationTraceLabTest` 记录的顺序是：

```text
dependency:constructed
service:constructed
iabpp:afterInstantiation(service)
iabpp:postProcessProperties(service,hasDependencyProperty=true,dependencyInjected=false)
service:setDependency
bpp:beforeInitialization(service)
service:afterPropertiesSet
bpp:afterInitialization(service):replacedByJdkProxy
```

这串事件把创建主线拆得很清楚：构造发生在属性注入之前；`postProcessProperties` 能看到待注入的 property values，但此时 setter 还没真正执行；初始化回调发生在属性注入之后；after-init BPP 还可以替换最终暴露对象。

## populateBean 之前和之后能观察到什么

`AbstractAutowireCapableBeanFactory#populateBean` 负责属性填充窗口。它不是只做 setter 调用，中间会给 `InstantiationAwareBeanPostProcessor` 留两个重要观察点：

- `postProcessAfterInstantiation`：实例已经构造出来，属性还没有填充。返回 `false` 可以阻止默认属性填充继续执行。
- `postProcessProperties`：容器已经准备好 `PropertyValues`，处理器可以检查或改写属性值，也可以执行注解驱动注入。

Lab 里的 `TraceInstantiationAwareBpp` 在 `postProcessProperties` 记录到 `hasDependencyProperty=true`，同时 `dependencyInjected=false`。随后才出现 `service:setDependency`。这说明属性元数据已经可见，但 setter 注入还没完成。

如果这里看到 `@Autowired` 生效，那不是字段自己会注入，而是 `AutowiredAnnotationBeanPostProcessor` 在这个窗口参与了属性解析和注入。处理器从哪里来，回到 [container-bootstrap-and-infrastructure.md](container-bootstrap-and-infrastructure.md)。

## initializeBean 之后可能换成另一个对象

`AbstractAutowireCapableBeanFactory#initializeBean` 会执行初始化阶段：

1. aware 回调。
2. `BeanPostProcessor#postProcessBeforeInitialization`。
3. `afterPropertiesSet`、自定义 init method 等初始化回调。
4. `BeanPostProcessor#postProcessAfterInitialization`。

最后一步可以返回原对象，也可以返回另一个对象。`SpringCoreBeansBeanCreationTraceLabTest` 的 `ProxyReplacingBpp` 在 `postProcessAfterInitialization` 中返回了 JDK proxy，所以 `context.getBean(WorkService.class)` 拿到的是代理对象，而不是 `TraceableService` 原始实例。

这个替换会影响类型查找。Lab 里 `context.getBean("service", TraceableService.class)` 抛出 `BeanNotOfRequiredTypeException`，因为最终暴露对象已经是基于接口的 JDK proxy，不再是具体类 `TraceableService`。更深入的代理创建、JDK proxy 与 CGLIB 的差异、early proxy 暴露，放到 [proxying-phase.md](proxying-phase.md)。

## 源码阅读顺序

建议按这条线设置断点：

1. `AbstractBeanFactory#doGetBean`：看缓存、父工厂、scope、depends-on 和创建入口。
2. `AbstractAutowireCapableBeanFactory#createBean`：看创建前短路、类型解析和进入真正创建。
3. `AbstractAutowireCapableBeanFactory#doCreateBean`：看实例化、early exposure、属性填充、初始化怎样串起来。
4. `AbstractAutowireCapableBeanFactory#populateBean`：看 `postProcessAfterInstantiation`、`postProcessProperties` 和 setter 注入顺序。
5. `AbstractAutowireCapableBeanFactory#initializeBean`：看 before-init、init callback、after-init，以及最终对象是否被替换。

如果是从容器启动读下来，先在 [refresh-mainline.md](refresh-mainline.md) 找到 `finishBeanFactoryInitialization` 和 `preInstantiateSingletons`，再从这里进入单 bean 主线。

## 用本模块怎么验证

最短命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest test
```

这个 Lab 同时验证三件事：

- 构造、`postProcessAfterInstantiation`、`postProcessProperties`、setter 注入、before-init BPP、`afterPropertiesSet`、after-init BPP 的顺序。
- `postProcessProperties` 发生时，依赖属性值已经可见，但 setter 还没有把依赖放到字段上。
- after-init BPP 能把最终暴露对象替换成 JDK proxy，导致按具体类查同名 bean 失败。

需要看这条线如何被 `refresh()` 批量触发时，运行：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test
```

## 相邻主题

- [refresh-mainline.md](refresh-mainline.md)：`refresh()` 怎样触发非懒加载单例创建。
- [container-bootstrap-and-infrastructure.md](container-bootstrap-and-infrastructure.md)：实例级处理器怎样先被注册进容器。
- [dependency-injection-resolution.md](dependency-injection-resolution.md)：属性填充时依赖候选怎样解析。
- [proxying-phase.md](proxying-phase.md)：after-init 代理替换和代理类型边界。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
