# refresh 主线：上下文刷新顺序
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这一页只回答：`refresh()` 这条主线到底先做什么、后做什么？
    - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test`
    - 相邻主题只做跳转，不在本页重复展开。

    观察对象：ApplicationContext#refresh 从准备上下文到预实例化单例的阶段顺序。
    主线位置：容器与注册。
    对照入口：`SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`。
<!-- CHAPTER-CARD:END -->

## refresh 是上下文装配，不是单个 Bean 的创建细节

`refresh()` 是 `ApplicationContext` 级别的装配主线。它负责准备上下文状态、准备 `BeanFactory`、执行工厂级后处理器、注册实例级后处理器、初始化消息源和事件广播器，最后进入 `finishBeanFactoryInitialization` 触发非懒加载单例的预实例化。

所以读 `refresh()` 时要先看“容器阶段”而不是直接钻进某个 bean 的构造器。单个 bean 的构造、属性填充、初始化回调和代理替换属于创建主线，放到 [bean-creation-mainline.md](bean-creation-mainline.md) 继续展开。

## 主线时间线

`AbstractApplicationContext#refresh` 可以按这条顺序读：

1. `prepareRefresh()`：标记上下文进入刷新状态，准备环境校验和早期事件集合。
2. `obtainFreshBeanFactory()`：拿到本轮使用的 `ConfigurableListableBeanFactory`。
3. `prepareBeanFactory(beanFactory)`：注册类加载器、表达式解析器、`ApplicationContextAwareProcessor` 等上下文基础能力。
4. `postProcessBeanFactory(beanFactory)`：留给具体上下文子类追加定制。
5. `invokeBeanFactoryPostProcessors(beanFactory)`：执行 `BeanDefinitionRegistryPostProcessor` 和 `BeanFactoryPostProcessor`。
6. `registerBeanPostProcessors(beanFactory)`：把 `BeanPostProcessor` 实例注册到 bean 创建链路。
7. `initMessageSource()` / `initApplicationEventMulticaster()` / `onRefresh()` / `registerListeners()`：准备上下文级服务。
8. `finishBeanFactoryInitialization(beanFactory)`：冻结常规定义变更入口，预实例化非懒加载单例。
9. `finishRefresh()`：发布刷新完成事件并启动生命周期处理。

这条线的关键边界是：前半段还在完善容器的“定义和能力”，后半段才开始批量创建普通单例。

## 哪些阶段还在改 BeanDefinition

`invokeBeanFactoryPostProcessors` 是 `refresh()` 里最重要的定义变更窗口。这里会通过 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 执行两类逻辑：

- `BeanDefinitionRegistryPostProcessor` 可以继续向 `BeanDefinitionRegistry` 注册、删除或调整定义，例如 `ConfigurationClassPostProcessor` 解析 `@Configuration`、`@Bean`、`@Import`、扫描结果等。
- 常规 `BeanFactoryPostProcessor` 可以修改已经存在的 `BeanDefinition`，例如替换属性值、解析占位符或调整元数据。

这个窗口发生在 `registerBeanPostProcessors` 之前。原因很直接：`BeanPostProcessor` 影响的是 bean 实例创建，而 `BeanFactoryPostProcessor` 影响的是定义和工厂配置。定义还没收敛之前就批量创建普通 bean，会让后续定义变更失去意义，也容易导致过早实例化。

## 哪些阶段开始影响 Bean 实例

`registerBeanPostProcessors` 本身通常还不是创建所有业务 bean 的阶段，它主要把实例级拦截器挂到 `BeanFactory` 上。真正的批量单例创建窗口在 `AbstractApplicationContext#finishBeanFactoryInitialization`。

这个方法会完成类型转换服务、嵌入值解析器、LoadTimeWeaver 相关处理、临时类加载器清理等收尾，然后调用 `DefaultListableBeanFactory#preInstantiateSingletons`。从这里开始，非懒加载 singleton 会按定义顺序进入 `getBean` / `doGetBean` / `createBean` / `doCreateBean` 主线，构造器、属性填充、初始化回调和 `BeanPostProcessor` 都会真正作用到实例上。

`SpringCoreBeansMainlineCallChainLabTest` 把容器启动和创建主线聚合在一起，适合用来观察：`refresh()` 不是“创建一个 bean”的别名，而是把容器能力准备好，再统一触发该创建的单例。

## 源码阅读顺序

建议按上下文阶段读，不要一开始就跳进 `populateBean`：

1. `AbstractApplicationContext#refresh`：先建立完整阶段顺序。
2. `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：看定义注册和定义修改窗口怎样运行。
3. `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：看实例级处理器什么时候进入 bean 创建链路。
4. `AbstractApplicationContext#finishBeanFactoryInitialization`：看容器收尾和单例预实例化入口。
5. `DefaultListableBeanFactory#preInstantiateSingletons`：看非懒加载 singleton 怎样被批量触发创建。

读到 `preInstantiateSingletons` 之后，再跳到 [bean-creation-mainline.md](bean-creation-mainline.md) 追单个 bean 的创建细节。

## 用本模块怎么验证

最短命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test
```

这个 suite 聚合了容器基础、bootstrap 和 bean 创建 trace。想聚焦 `refresh()` 前半段的基础设施注册，可以单独运行：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBootstrapInternalsLabTest test
```

观察重点：

- 没有注册 annotation config processors 时，`GenericApplicationContext` 不会自动具备 `@Autowired`、`@PostConstruct` 和 `@Bean` 解析能力。
- 注册 processors 后，这些能力会在 `refresh()` 的后处理器阶段和创建阶段接入。
- 创建 trace 证明真正的实例构造、属性填充和初始化发生在单例创建窗口，而不是定义注册窗口。

## 相邻主题

- [container-bootstrap-and-infrastructure.md](container-bootstrap-and-infrastructure.md)：annotation config processors 怎样让容器具备注解能力。
- [bean-creation-mainline.md](bean-creation-mainline.md)：`getBean -> doGetBean -> createBean -> doCreateBean` 的单 bean 创建路径。
- [post-processors-overview.md](post-processors-overview.md)：BFPP、BDRPP、BPP 三类后处理器的阶段边界。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
