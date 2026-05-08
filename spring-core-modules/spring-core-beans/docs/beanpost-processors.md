# BeanPostProcessor：实例创建中的介入窗口
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这一页只回答：BPP 如何介入实例创建，什么时候会把 bean 换成 proxy？
    - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleRawVsProxyLabTest test`
    - 相邻主题只做跳转，不在本页重复展开。

    观察对象：BPP before/after initialization、InstantiationAwareBPP 和替换 exposedObject 的窗口。
    主线位置：容器与注册。
    对照入口：`SpringCoreBeansLifecycleRawVsProxyLabTest` / `SpringCoreBeansProxyingPhaseLabTest`。
<!-- CHAPTER-CARD:END -->

## BPP 看到的是实例创建过程

`BeanPostProcessor` 不是定义注册器。它不负责把 `BeanDefinition` 放进容器，也不应该承担 BDRPP/BFPP 的定义修改职责。它的窗口在普通 bean 创建过程中：实例已经被创建或正在被填充属性，容器即将或已经执行初始化回调。

从源码看，主线在 `AbstractAutowireCapableBeanFactory#initializeBean`。这个方法会调用 `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`，执行 init callback，再调用 `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`。扩展接口 `InstantiationAwareBeanPostProcessor` 还可以更早介入实例化后、属性填充前后的窗口。

## beforeInitialization 和 afterInitialization 的差异

before-init BPP 发生在 init callback 前，适合给原始对象补充初始化前的处理。after-init BPP 发生在 `@PostConstruct`、`InitializingBean#afterPropertiesSet` 或自定义 init method 之后，返回值会成为后续链路继续处理的对象。

`SpringCoreBeansLifecycleRawVsProxyLabTest` 验证了这个顺序：`RawWorkService#init` 的 `@PostConstruct` 记录的是原始对象 identity；`AfterInitJdkProxyingBpp#postProcessAfterInitialization` 随后返回 JDK proxy，并记录最终暴露对象 identity。两个 identity 不相等，容器里按 `WorkService` 拿到的是 proxy。

这说明 `@PostConstruct` 不是在代理对象上执行。初始化回调面对的是 raw bean，after-init BPP 才有机会替换最终暴露对象。

## InstantiationAwareBeanPostProcessor 介入属性填充

`InstantiationAwareBeanPostProcessor` 是 BPP 家族里更靠前的扩展点。它可以在实例构造后、属性填充前后参与，例如 `InstantiationAwareBeanPostProcessor#postProcessProperties` 会在真正 setter 注入前拿到 `PropertyValues`。

`SpringCoreBeansBeanCreationTraceLabTest` 的事件顺序是：

```text
service:constructed
iabpp:afterInstantiation(service)
iabpp:postProcessProperties(service,hasDependencyProperty=true,dependencyInjected=false)
service:setDependency
bpp:beforeInitialization(service)
service:afterPropertiesSet
bpp:afterInitialization(service):replacedByJdkProxy
```

`hasDependencyProperty=true` 说明属性值已经准备好；`dependencyInjected=false` 说明 setter 还没有执行。随后才出现 `service:setDependency`。这就是属性填充窗口和初始化窗口的分界。

## afterInitialization 可以替换最终暴露对象

after-init BPP 的返回值不是装饰性的日志结果，它会影响容器最终暴露的对象。`SpringCoreBeansProxyingPhaseLabTest` 展示了两种替换：

- `ProxyingPostProcessor` 返回 JDK proxy，最终对象只实现接口，按具体类 `SelfInvocationService` 查找会失败。
- `CglibProxyingPostProcessor` 返回 CGLIB 子类，最终对象仍然可以按具体类查找。

这个能力解释了为什么调用方拿到的对象可能不是构造器里创建的那个 raw object。它也解释了自调用绕过代理：raw object 内部的 `this.inner(...)` 没有经过容器暴露出去的 proxy。

## 源码阅读顺序

建议按这条线读：

1. `AbstractAutowireCapableBeanFactory#doCreateBean`：先定位实例化、属性填充、初始化的整体顺序。
2. `InstantiationAwareBeanPostProcessor#postProcessProperties`：看属性填充前扩展点怎样拿到 `PropertyValues`。
3. `AbstractAutowireCapableBeanFactory#initializeBean`：看 aware、before-init、init callback、after-init 怎样串联。
4. `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`：看 before-init BPP 的应用位置。
5. `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：看最终对象替换发生在哪里。

如果问题是“这些 BPP 何时注册进容器”，回到 [post-processors-overview.md](post-processors-overview.md) 和 [post-processor-ordering.md](post-processor-ordering.md)。

## 用本模块怎么验证

运行：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleRawVsProxyLabTest,SpringCoreBeansBeanCreationTraceLabTest,SpringCoreBeansProxyingPhaseLabTest test
```

观察重点：

- `SpringCoreBeansLifecycleRawVsProxyLabTest`：`@PostConstruct` 记录 raw object，最终暴露对象是 JDK proxy。
- `SpringCoreBeansBeanCreationTraceLabTest`：`postProcessProperties` 发生在 setter 注入前，after-init BPP 可以把 `service` 替换成 JDK proxy。
- `SpringCoreBeansProxyingPhaseLabTest`：JDK proxy 和 CGLIB proxy 对类型查找、自调用拦截的影响不同。

## 相邻主题

- [post-processors-overview.md](post-processors-overview.md)：BFPP/BDRPP/BPP 的阶段分界。
- [bean-creation-mainline.md](bean-creation-mainline.md)：单个 bean 创建主线。
- [proxying-phase.md](proxying-phase.md)：代理替换、自调用和类型边界。
- [programmatic-bpp-registration.md](programmatic-bpp-registration.md)：手工注册 BPP 的排序边界。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
