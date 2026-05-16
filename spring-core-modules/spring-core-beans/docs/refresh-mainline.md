# ApplicationContext refresh 主线：容器状态如何一步步就绪

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文按时间线解释 `ApplicationContext#refresh()` 改变了哪些容器状态。
    - 覆盖准备上下文、获取 BeanFactory、处理 BeanDefinition、执行 BDRPP/BFPP、注册 BPP、初始化消息源/事件广播器/监听器、预实例化非懒 singleton 和完成刷新。
    - 本文不展开单个 Bean 的完整 `doCreateBean` 细节。

    观察对象：refresh 阶段顺序和每一步产生的容器状态。
    主线位置：ApplicationContext 启动主线。
    对照入口：`SpringCoreBeansMainlineCallChainLabTest`、`SpringCoreBeansBootstrapInternalsLabTest`、`SpringCoreBeansContainerLabTest`。
<!-- CHAPTER-CARD:END -->

`refresh()` 是 ApplicationContext 从“有配置输入”走向“可对外服务”的主线。它不是单纯调用一次 `getBean()`，而是按固定顺序准备环境、建立 BeanFactory、注册和改写 BeanDefinition、安装后处理器、初始化应用级设施、创建非懒 singleton，并发布完成事件。

理解 refresh 的关键是看每个阶段改变了什么状态，以及为什么后一个阶段依赖前一个阶段的结果。

## 时间线总览

```text
prepareRefresh
-> obtainFreshBeanFactory
-> prepareBeanFactory
-> postProcessBeanFactory
-> invokeBeanFactoryPostProcessors
-> registerBeanPostProcessors
-> initMessageSource
-> initApplicationEventMulticaster
-> onRefresh
-> registerListeners
-> finishBeanFactoryInitialization
-> finishRefresh
```

不同 ApplicationContext 子类在细节上会有差异，但主线意图一致：先把定义和工厂准备好，再让定义级扩展点运行，然后安装实例级扩展点，最后才集中创建普通非懒 singleton。

## 1. 准备上下文

`prepareRefresh` 标记上下文进入 active 状态，初始化早期属性源，校验必需属性，并准备早期事件集合。此时还没有开始创建普通业务 Bean。

这个阶段影响后续条件判断和环境读取。如果某些必须属性缺失，应该在这里或稍后的条件解析中暴露，而不是等业务 Bean 构造时才发现。

## 2. 获取和准备 BeanFactory

`obtainFreshBeanFactory` 负责得到本轮 refresh 使用的 BeanFactory。对可重复 refresh 的上下文，这可能意味着刷新内部工厂；对 `GenericApplicationContext`，通常是使用已有的 `DefaultListableBeanFactory`。

`prepareBeanFactory` 会把上下文能力接入 BeanFactory，例如 class loader、表达式解析器、属性编辑器、ApplicationContextAware 相关处理器、可解析依赖和默认环境 Bean。这里的重点是：BeanFactory 开始知道自己处在 ApplicationContext 里。

## 3. 处理 BeanDefinition

`postProcessBeanFactory` 是子类扩展点，可以在标准后处理器执行前对 BeanFactory 做额外准备。

随后 `invokeBeanFactoryPostProcessors` 执行 BeanDefinitionRegistryPostProcessor 和 BeanFactoryPostProcessor。BDRPP 可以新增、删除或改写 BeanDefinition；BFPP 可以修改已有定义或 BeanFactory 配置。配置类解析、`@Bean` 方法注册、component scan、`@Import` 链路等能力都发生在这个大阶段内。

顺序不能反过来：如果先创建普通 Bean，再解析配置类或执行 BFPP，就会错过定义改写窗口。`SpringCoreBeansContainerLabTest` 中 BFPP 在实例化前修改 `exampleBean` 的属性值，就是这个阶段价值的最小证明。

## 4. 注册 BeanPostProcessor

`registerBeanPostProcessors` 会找到 BeanPostProcessor 类型的 Bean，按 `PriorityOrdered`、`Ordered` 和普通处理器排序后加入 BeanFactory 的 BPP 链。

BPP 影响的是实例生命周期：实例化前短路、属性注入、Aware、初始化前后、代理包装、销毁回调等。它们必须在普通 Bean 大规模创建前注册，否则早创建的 Bean 可能错过注入或代理。

`SpringCoreBeansBootstrapInternalsLabTest` 说明如果没有注册注解配置处理器，`@Autowired` 和 `@PostConstruct` 不会自动生效；这背后就是相关 BPP 没有进入链路。

## 5. 初始化应用级设施

`initMessageSource` 准备国际化消息源。若用户注册了名为 `messageSource` 的 Bean，则使用用户提供的实现；否则创建默认实现。

`initApplicationEventMulticaster` 准备事件广播器。事件发布不是 BeanFactory 核心能力，它依赖 ApplicationContext 在 refresh 中建立广播设施。

`onRefresh` 是子类扩展点，例如 Web 或其他上下文可以在这里初始化特殊组件。本文只关注主线，不展开具体子类行为。

`registerListeners` 把静态添加的监听器和容器中的 `ApplicationListener` Bean 注册到广播器，并发布早期事件。注意监听器 Bean 的发现可能触发类型判断，但普通 singleton 的集中创建还在后面。

## 6. 预实例化非懒 singleton

`finishBeanFactoryInitialization` 是 refresh 中最接近普通业务对象创建的阶段。它会准备 conversion service、嵌入值解析器、LoadTimeWeaverAware 等细节，然后调用 `preInstantiateSingletons()` 创建所有非 lazy singleton。

这里会触发单个 Bean 创建主线，但 refresh 文档只需要记住边界：refresh 不是逐个解释 `doCreateBean`，而是在这个时间点把非懒 singleton 的创建集中拉起。构造器错误、依赖缺失、初始化异常、代理创建问题，很多都会在这里暴露。

lazy singleton 不会因为预实例化而创建，但如果它被非懒 Bean 依赖或被 `depends-on` 强制要求，仍可能在这个阶段被带出来。

## 7. 完成刷新

`finishRefresh` 清理上下文级缓存，初始化 lifecycle processor，发布 `ContextRefreshedEvent`，并把上下文标记为刷新完成。此时非懒 singleton 已创建，应用级事件系统也已可用；如果要在源码里观察完成边界，可以停在事件发布前后，确认 singleton 预实例化已经结束。

## 断点阅读建议

沿 refresh 读源码时，按状态变化停，而不是按方法数量停：

1. BeanDefinition 数量什么时候增加。
2. BDRPP/BFPP 何时执行，是否提前触发普通 Bean。
3. BPP 链什么时候安装完成。
4. message source、event multicaster、listeners 是否就绪。
5. `preInstantiateSingletons()` 前后 singleton 缓存发生了什么变化。
6. `ContextRefreshedEvent` 发布时，哪些 Bean 已经存在。

`SpringCoreBeansMainlineCallChainLabTest` 聚合了容器启动和创建主线相关 Lab，适合用来固定 refresh 与单 Bean 创建之间的边界。
