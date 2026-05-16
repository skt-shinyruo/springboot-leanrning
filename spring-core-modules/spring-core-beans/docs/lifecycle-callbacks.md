# 生命周期回调：从构造到销毁，哪个窗口能看到什么

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 singleton bean 生命周期回调的稳定顺序，以及每个窗口里对象状态能看到什么。
    - 核心结论：constructor、property injection、Aware、BPP before init、`@PostConstruct`、`afterPropertiesSet`、custom init、BPP after init、destroy callbacks 有明确的先后。
    - 还要区分 raw object 和最终 exposed proxy：前者承载初始化回调，后者才是调用方最后拿到的对象。

    观察对象：生命周期各回调窗口里对象状态和暴露边界。
    主线位置：单个 bean 从实例化到销毁的全过程。
    对照入口：`SpringCoreBeansLifecycleCallbackOrderLabTest`、`SpringCoreBeansLifecycleRawVsProxyLabTest`。
<!-- CHAPTER-CARD:END -->

生命周期回调不是一串“差不多按顺序执行”的动作，而是几个清晰窗口。每个窗口里，bean 的状态不同，能做的事也不同。

## 标准顺序

对于一个普通 singleton，常见顺序是：

1. constructor
2. property injection
3. Aware 回调
4. BeanPostProcessor before init
5. `@PostConstruct`
6. `afterPropertiesSet`
7. custom init method
8. BeanPostProcessor after init
9. destroy 回调链

`SpringCoreBeansLifecycleCallbackOrderLabTest` 把这个顺序固定得很清楚。它不是“某些实现碰巧这样跑”，而是容器在初始化窗口上的固定组织方式。

## 每个窗口里能看到什么

### constructor

对象刚被创建，字段通常还是默认值。适合做纯构造参数赋值，不适合依赖外部注入后的状态。

### property injection

依赖字段、setter、注入点开始被填充，但对象还没有完成初始化回调。此时可以看到容器注入的数据，但不应假设 bean 已可对外安全使用。

### Aware

`BeanNameAware`、`BeanFactoryAware`、`ApplicationContextAware` 之类的回调发生在初始化前，用来把容器上下文信息交给对象。它们能看到注入后的状态，但还没进入完整初始化收口。

### BPP before init

这是后处理器对原始对象进行预处理的窗口。此时对象还没执行 `@PostConstruct` 和 `afterPropertiesSet`。

### `@PostConstruct` / `afterPropertiesSet` / custom init

这些回调表示对象的初始化逻辑。`@PostConstruct` 通常先于 `afterPropertiesSet`，再到自定义 init method。它们都发生在 bean 被最终暴露之前。

### BPP after init

这是最关键的边界之一。这里的后处理器可以返回一个新对象，替换原始 bean。AOP 代理、包装器、监控代理都常在这里出现。

`SpringCoreBeansLifecycleRawVsProxyLabTest` 明确证明：`@PostConstruct` 看到的是 raw bean，而 after-init BPP 可以把最终暴露对象换成 JDK proxy。

### destroy callbacks

销毁阶段会逆向清理。`@PreDestroy`、`DisposableBean#destroy()`、自定义 destroy method 都属于这个窗口。singleton 在容器关闭时会正常走这条链路；prototype 默认不会由容器统一销毁。

## raw object 和 exposed proxy 不是同一个窗口

很多排障问题都卡在这里：你在初始化回调里看到的对象，未必就是调用方最终拿到的对象。

`SpringCoreBeansLifecycleRawVsProxyLabTest` 用 identity hash 证明了这件事。`@PostConstruct` 记录的是 raw bean 的身份，而 `BeanPostProcessor#postProcessAfterInitialization` 之后，`getBean()` 返回的可能已经是 proxy。

因此：

- 初始化回调看的是 raw object
- 对外暴露看的是最终 exposed object
- proxy 替换发生在初始化完成之后、最终交付之前

## 什么时候该查生命周期回调

当你遇到这些问题时，优先看生命周期窗口：

- 属性明明注入了，却在初始化阶段读不到
- `@PostConstruct` 里调用的方法没有走代理
- 关闭上下文时没有触发清理
- 初始化后拿到的对象类型和构造时不一致

把对象放回正确的窗口里看，很多“为什么没生效”的问题会直接变成“它发生在另一个阶段”。
