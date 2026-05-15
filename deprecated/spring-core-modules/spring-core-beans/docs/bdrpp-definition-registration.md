# BDRPP：后处理器阶段新增定义
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这一页只回答：BDRPP 为什么能在普通 BFPP 之前新增或改写 BeanDefinition？
    - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRegistryPostProcessorLabTest test`
    - 相邻主题只做跳转，不在本页重复展开。

    观察对象：BeanDefinitionRegistryPostProcessor 的 registry 回调、重复发现窗口和与普通 BFPP 的顺序差异。
    主线位置：容器与注册。
    对照入口：`SpringCoreBeansRegistryPostProcessorLabTest`。
<!-- CHAPTER-CARD:END -->

## BDRPP 多了一个 registry 窗口

`BeanDefinitionRegistryPostProcessor` 继承自 `BeanFactoryPostProcessor`，但它多一个更早的入口：`BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`。这个入口发生在常规 BFPP 执行之前，目标不是某个 bean 实例，而是整个 `BeanDefinitionRegistry`。

所以 BDRPP 的典型职责是新增、删除或调整定义集合。配置类解析、扫描结果导入、自定义注册器等机制都需要这个窗口，因为它们要在普通 bean 创建前把“将来要创建什么”补进容器。

## 新定义为什么能被后续 BFPP 看见

在 `SpringCoreBeansRegistryPostProcessorLabTest` 里，`Registrar` 先注册名为 `registeredBean` 的定义，并把属性 `origin` 设成 `from-bdrpp`。同一个 `refresh()` 里，`Modifier` 作为普通 BFPP 随后执行：

```java
beanFactory.getBeanDefinition("registeredBean")
        .getPropertyValues()
        .add("origin", "modified-by-bfpp");
```

最后从容器拿到的 `RegisteredBean` 返回 `modified-by-bfpp`。这说明 BDRPP 的 registry 回调不是创建了一个绕开工厂的对象，而是把新的 `BeanDefinition` 放回统一定义集合；后续 BFPP 仍然能按普通定义修改它。

## 重复发现窗口是什么

`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 处理 BDRPP 时，不是只扫描一遍就结束。它会在 `PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors` 周围反复通过 `DefaultListableBeanFactory#getBeanNamesForType` 查找还没执行过的 `BeanDefinitionRegistryPostProcessor`。

这个重复发现窗口解决的是“BDRPP 注册了新的 BDRPP”这类情况。Spring 需要把新出现的 registry 处理器也纳入本轮定义收敛，否则后续 BFPP 和普通 bean 创建会看到不完整的定义集合。

这个循环只属于定义注册阶段。等流程进入普通 BFPP 或 BPP 注册后，再新增定义处理器就不应该期待它回到前面的 registry 窗口。

## 早期 getBean 会破坏什么

BDRPP 虽然有 `postProcessBeanFactory`，但这并不意味着可以在这个阶段创建普通业务 bean。`SpringCoreBeansRegistryPostProcessorLabTest` 里 `EarlyInstantiationBdrpp` 在后处理器阶段调用 `beanFactory.getBean("earlyTarget")`，结果是：

- `earlyTarget` 构造了，但 `processedByBpp()` 为 `false`。
- `lateTarget` 在 BPP 注册后正常创建，`processedByBpp()` 为 `true`。
- 事件里有 `earlyTarget:constructor`，没有 `bpp:earlyTarget`；有 `lateTarget:constructor` 和 `bpp:lateTarget`。

也就是说，早期 `getBean()` 把目标 bean 从实例创建主线里提前拉出来，绕过了后续注册的 `BeanPostProcessor`。BDRPP 应该收敛定义，不应该用创建实例来验证定义是否存在。

## 源码阅读顺序

建议按这条线读：

1. `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：先看整个 BFPP/BDRPP 委派入口。
2. `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`：看 registry 回调拿到的能力边界。
3. `PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors`：看 Spring 如何执行 registry 回调并记录已处理集合。
4. `DefaultListableBeanFactory#getBeanNamesForType`：看重复发现 BDRPP 的扫描入口。
5. `BeanFactoryPostProcessor#postProcessBeanFactory`：看 registry 阶段结束后，BDRPP 也会进入普通 factory 回调。

如果只是修改已有定义，不需要新增定义，回到 [beanfactory-post-processors.md](beanfactory-post-processors.md)。

## 用本模块怎么验证

运行：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRegistryPostProcessorLabTest test
```

观察三个用例：

- `beanDefinitionRegistryPostProcessor_canRegisterNewBeanDefinitions`：`Registrar` 注册 `registeredBean`，后续可以从容器取出。
- `bdrppRunsBeforeRegularBeanFactoryPostProcessor`：`registeredBean.origin()` 从 `from-bdrpp` 变成 `modified-by-bfpp`。
- `getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors`：`earlyTarget` 没被 BPP 标记，`lateTarget` 被标记。

## 相邻主题

- [post-processors-overview.md](post-processors-overview.md)：三类后处理器的阶段边界。
- [beanfactory-post-processors.md](beanfactory-post-processors.md)：普通 BFPP 修改已有定义的窗口。
- [beanpost-processors.md](beanpost-processors.md)：普通 bean 实例创建后的处理器窗口。
- [post-processor-ordering.md](post-processor-ordering.md)：BDRPP/BFPP/BPP 的排序规则。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
