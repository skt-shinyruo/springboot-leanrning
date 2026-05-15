# BeanFactoryPostProcessor：修改已有定义
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这一页只回答：BFPP 在什么时候修改已有 BeanDefinition，不能做什么？
    - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansStaticBeanFactoryPostProcessorLabTest test`
    - 相邻主题只做跳转，不在本页重复展开。

    观察对象：BeanFactoryPostProcessor 在普通 Bean 创建前修改 BeanDefinition 的窗口和禁止过早取 Bean 的边界。
    主线位置：容器与注册。
    对照入口：`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`。
<!-- CHAPTER-CARD:END -->

## BFPP 改的是定义，不是普通 Bean 实例

`BeanFactoryPostProcessor#postProcessBeanFactory` 运行在定义已经加载之后、普通 singleton 预实例化之前，并且早于 `PostProcessorRegistrationDelegate#registerBeanPostProcessors`。这个窗口适合读取和修改 `BeanDefinition`，例如改 property values、占位符解析结果、scope、lazy 标记或其他工厂级配置。

它不适合处理普通 bean 实例，因为此时实例级 `BeanPostProcessor` 还没有注册完成。BFPP 如果强行 `getBean()`，不是“提前拿一个对象备用”，而是让这个 bean 在缺少完整 BPP 链的情况下被创建。

## static @Bean 为什么重要

用 `@Configuration` 暴露 BFPP 时，`static @Bean` 的意义是：Spring 可以调用这个工厂方法创建 BFPP，而不必先实例化配置类本身。这样配置类仍然可以等到 BPP 注册完成后再作为普通配置 bean 创建。

`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` 的 static 分支事件顺序是：

```text
bfpp:factoryMethodInvoked
bfpp:invoked
bpp:constructed
config:constructed
bpp:markedConfig
```

这说明 BFPP 先被创建和执行，随后 BPP 注册完成，配置类实例才被创建，并且能被 `MarkingBpp` 标记。

源码阅读时可以把 `ConfigurationClassEnhancer` 只放在“配置类本身为什么有特殊生命周期”这个问题上理解：配置类会被增强以支撑 `@Bean` 方法语义，因此不应该为了拿到一个 BFPP 而把配置类本身过早当成普通 singleton 创建。

## 非 static BFPP 的早期实例化问题

非 static `@Bean` 方法需要一个配置类实例才能调用工厂方法。问题出在 BFPP 阶段还早于 BPP 注册，所以配置类会先被构造，后续注册的 BPP 再也补不上这一轮实例创建。

同一个 Lab 的非 static 分支事件顺序是：

```text
config:constructed
bfpp:factoryMethodInvoked
bfpp:invoked
bpp:constructed
```

这里没有 `bpp:markedConfig`。配置类已经在 BFPP 创建前被构造出来，而 `MarkingBpp` 是之后才构造并注册的。这个对照就是 Spring 文档里建议 BFPP 的 `@Bean` 方法声明为 static 的原因。

## BFPP 阶段不要 getBean

BFPP 可以通过 `beanFactory.getBeanDefinition(...)` 改定义，也可以操作 `ConfigurableListableBeanFactory` 的配置；它不应该通过 `getBean()` 触发普通业务 bean 创建。

风险有两个：

- BPP 尚未注册完成，目标 bean 会错过初始化前后处理器、自动代理、标记类处理器等实例级逻辑。
- 普通 bean 一旦创建，后续 BFPP 再改它的 `BeanDefinition` 已经无法影响这个实例。

如果需要证明这个边界，`SpringCoreBeansRegistryPostProcessorLabTest` 的 `earlyTarget` / `lateTarget` 更直观：后处理器阶段 `getBean("earlyTarget")` 创建的对象没有被 BPP 标记，而正常晚创建的 `lateTarget` 被标记。

## 源码阅读顺序

建议按这条线读：

1. `AbstractApplicationContext#refresh`：先确认 `invokeBeanFactoryPostProcessors` 早于 `registerBeanPostProcessors` 和 `finishBeanFactoryInitialization`。
2. `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：看 Spring 怎样找出并执行 BFPP。
3. `BeanFactoryPostProcessor#postProcessBeanFactory`：看自定义 BFPP 拿到的是 `ConfigurableListableBeanFactory`，主要操作对象是定义和工厂配置。
4. `ConfigurationClassEnhancer`：只在追踪 `@Configuration` 类为什么不应被 BFPP 工厂方法过早实例化时阅读。

读到 BDRPP 时不要在这里展开，跳到 [bdrpp-definition-registration.md](bdrpp-definition-registration.md)。

## 用本模块怎么验证

运行：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansStaticBeanFactoryPostProcessorLabTest test
```

对照两个测试：

- `nonStaticBeanFactoryPostProcessor_forcesConfigurationClassInstantiation_tooEarly_soItMissesLaterBpps`：事件为 `config:constructed`、`bfpp:factoryMethodInvoked`、`bfpp:invoked`、`bpp:constructed`。
- `staticBeanFactoryPostProcessor_doesNotForceEarlyConfigurationInstantiation_soConfigurationBeanIsProcessedByBpps`：事件为 `bfpp:factoryMethodInvoked`、`bfpp:invoked`、`bpp:constructed`、`config:constructed`、`bpp:markedConfig`。

这两个序列的差异就是 BFPP 阶段和 BPP 注册阶段之间的边界。

## 相邻主题

- [post-processors-overview.md](post-processors-overview.md)：三类后处理器的阶段边界。
- [bdrpp-definition-registration.md](bdrpp-definition-registration.md)：需要新增定义时为什么用 BDRPP。
- [beanpost-processors.md](beanpost-processors.md)：实例创建和代理替换属于 BPP 窗口。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
