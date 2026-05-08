# Post-Processor 总览：定义阶段与实例阶段
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这一页只回答：BFPP / BDRPP / BPP 的职责边界是什么，分别属于定义阶段还是实例阶段？
    - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansStaticBeanFactoryPostProcessorLabTest test`
    - 相邻主题只做跳转，不在本页重复展开。

    观察对象：BFPP、BDRPP、BPP 的阶段边界、可修改对象和相邻详细页路由。
    主线位置：容器与注册。
    对照入口：`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest`。
<!-- CHAPTER-CARD:END -->

## 先按阶段分，不按名字分

看到 Post-Processor 先问它介入的是哪个阶段。`refresh()` 前半段还在整理 `BeanDefinition` 和 `BeanFactory`，这时运行的是 BDRPP 与 BFPP；`registerBeanPostProcessors` 之后，普通 bean 创建链路才会把 BPP 应用到实例上。

因此排查时不要只看类名里有没有 “post processor”。同样是后处理器，BDRPP 可以新增定义，BFPP 可以修改已有定义，BPP 只能围绕实例创建前后做处理。阶段错了，常见结果不是“晚一点生效”，而是目标对象已经被提前创建或最终暴露对象被换成另一种类型。

## 三类处理器的能力边界

| 类型 | 所在阶段 | 主要 callback | 可修改对象 | 常见失败 |
| --- | --- | --- | --- | --- |
| `BeanDefinitionRegistryPostProcessor` | 普通 BFPP 之前的 registry 窗口 | `postProcessBeanDefinitionRegistry`，随后也会走 `postProcessBeanFactory` | `BeanDefinitionRegistry` 中的定义集合 | 在阶段内调用 `getBean()`，让业务 bean 早于 BPP 注册被创建 |
| `BeanFactoryPostProcessor` | 定义已加载后、普通 singleton 创建前、BPP 注册前 | `BeanFactoryPostProcessor#postProcessBeanFactory` | 已存在的 `BeanDefinition`、`BeanFactory` 配置 | 非 static `@Bean` BFPP 迫使配置类过早实例化；或在 BFPP 中创建普通 bean |
| `BeanPostProcessor` | 单个 bean 的实例化、属性填充、初始化和暴露过程 | `postProcessBeforeInitialization` / `postProcessAfterInitialization`，扩展接口还有属性填充回调 | 当前正在创建的 bean 实例，after-init 可替换最终对象 | 误以为还能注册定义；或 after-init 返回 JDK proxy 后按具体类查找失败 |

源码上先看两个委派入口：`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 负责 BDRPP/BFPP，`PostProcessorRegistrationDelegate#registerBeanPostProcessors` 负责把 `BeanPostProcessor` 挂到后续创建链路。

## 为什么 BDRPP 要单独看

BDRPP 不是“更早一点的 BFPP”这么简单。它多拿到一个 `BeanDefinitionRegistry`，可以在常规 BFPP 执行前新增定义。`SpringCoreBeansRegistryPostProcessorLabTest` 里的 `Registrar` 注册 `registeredBean`，随后 `Modifier` 作为普通 BFPP 还能拿到这个定义并把 `origin` 改成 `modified-by-bfpp`。

这个能力解释了很多基础设施行为，例如配置类解析、扫描和导入最终都要变成定义进入 registry。详情放在 [bdrpp-definition-registration.md](bdrpp-definition-registration.md)。

## 为什么 BPP 不应该改 BeanDefinition

BPP 的入口发生在 bean 已经进入创建过程之后。它看到的是原始实例、属性填充窗口、初始化前后回调，以及最终暴露对象。它不是定义注册器，也不是定义批量改写器。

把定义阶段问题塞进 BPP 会带来两个错位：一是目标定义可能早已被合并、缓存或用于创建；二是 BPP 的返回值影响的是当前实例，尤其 `postProcessAfterInitialization` 可以直接替换暴露对象。`SpringCoreBeansLifecycleRawVsProxyLabTest` 和 `SpringCoreBeansProxyingPhaseLabTest` 都展示了最终从容器拿到的对象可以是代理，而不是原始对象。

实例创建窗口的细节看 [beanpost-processors.md](beanpost-processors.md)；代理类型和自调用边界看 [proxying-phase.md](proxying-phase.md)。

## 读源码时先看委派器

建议按这条线读：

1. `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：看 Spring 怎样先处理 `BeanDefinitionRegistryPostProcessor`，再处理常规 `BeanFactoryPostProcessor`。
2. `BeanDefinitionRegistryPostProcessor`：看 registry 回调为什么能新增、删除或调整定义集合。
3. `BeanFactoryPostProcessor`：看定义加载完成后、普通 bean 创建前还能改什么。
4. `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：看 BPP 怎样被分组排序并注册到 bean 创建链路。
5. `BeanPostProcessor`：回到 `AbstractAutowireCapableBeanFactory#initializeBean`，看 before-init 和 after-init 怎样作用到实例。

如果问题是“为什么先后顺序不是我以为的顺序”，继续看 [post-processor-ordering.md](post-processor-ordering.md)。

## 用本模块怎么验证

最短可以分三组跑：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansStaticBeanFactoryPostProcessorLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRegistryPostProcessorLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPostProcessorOrderingLabTest test
```

观察重点：

- `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`：非 static `@Bean` BFPP 会先构造配置类并错过后续 BPP；static `@Bean` BFPP 不需要提前构造配置类。
- `SpringCoreBeansRegistryPostProcessorLabTest`：BDRPP 可以注册定义，普通 BFPP 可以继续修改这个定义；BDRPP/BFPP 阶段调用 `getBean()` 会让目标 bean 跳过后续 BPP。
- `SpringCoreBeansPostProcessorOrderingLabTest`：排序先分 `PriorityOrdered`、`Ordered`、无序三组，再在有序组内按较小 order 值先运行。

## 相邻主题

- [beanfactory-post-processors.md](beanfactory-post-processors.md)：BFPP 修改已有定义的窗口和禁区。
- [bdrpp-definition-registration.md](bdrpp-definition-registration.md)：BDRPP 为什么能在后处理器阶段新增定义。
- [beanpost-processors.md](beanpost-processors.md)：BPP 怎样介入实例创建和最终对象暴露。
- [post-processor-ordering.md](post-processor-ordering.md)：后处理器排序规则。
- [programmatic-bpp-registration.md](programmatic-bpp-registration.md)：手工注册 BPP 为什么绕过自动排序。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
