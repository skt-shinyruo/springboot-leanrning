# SmartLifecycle：容器启动与停止阶段的编排规则

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 `SmartLifecycle` 在容器 start / stop 阶段的行为，以及 `autoStartup` 和 `phase` 如何决定顺序。
    - 核心结论：它关注的是生命周期编排，不是 bean 的构造或销毁回调；启动按 phase 递增，停止按 phase 递减。
    - 还要看清楚：停止时调用的是 `stop(Runnable)` 回调，这给异步停机留了窗口。

    观察对象：容器启动、停止和 phase 编排。
    主线位置：bean 已创建，但容器开始统一启动 / 停止生命周期组件时。
    对照入口：`SpringCoreBeansSmartLifecycleLabTest`。
<!-- CHAPTER-CARD:END -->

`SmartLifecycle` 的语义和普通 bean 初始化不同。它描述的是“这个组件什么时候随容器一起 start、什么时候在关闭时 stop”，而不是“这个对象什么时候被构造”。

## 启动顺序

容器启动时，`SmartLifecycle` 会按 phase 递增的顺序启动。phase 小的先 start，phase 大的后 start。

`SpringCoreBeansSmartLifecycleLabTest` 里的两个实例把这个顺序固定下来：

- `A` 的 phase 是 0，先启动
- `B` 的 phase 是 1，后启动

这说明 phase 不是标签，而是启动编排顺序的数值规则。

## 停止顺序

关闭时，顺序反过来。phase 大的先 stop，phase 小的后 stop。这样可以保证依赖链上的下游组件先收口，上游组件后收口。

这比“按注册顺序关闭”更接近真实资源依赖关系。

## `autoStartup` 的作用

`isAutoStartup()` 决定容器 refresh 后是否会自动启动这个生命周期组件。

如果返回 false，容器不会在 refresh 后自动调用 `start()`。这使得组件可以被注册到容器里，但保留手动启动的控制权。

`SpringCoreBeansSmartLifecycleLabTest` 里 `manual` 这个 bean 没有自动启动，说明 `SmartLifecycle` 不是“注册进去就必定运行”。

## 停止时为什么用 `stop(Runnable)`

容器关闭时不会只看 `stop()`。`DefaultLifecycleProcessor` 会优先走 `stop(Runnable)`，给异步清理留下回调窗口。

`SpringCoreBeansSmartLifecycleLabTest` 的 callback-only 实验固定了这个边界：容器在停机时调用的是 `stop(Runnable)`，而不是只走同步 `stop()`。

这意味着如果组件需要异步释放资源，应该把完成信号通过 callback 回传，而不是假设容器会阻塞等待你内部做完一切。

## 它和 bean 初始化 / 销毁不是一回事

`SmartLifecycle` 运行在容器的 start / stop 编排层，而不是普通 bean 的构造、`@PostConstruct` 或 `@PreDestroy` 层。

所以不要把它当成：

- 构造器替代品
- init method 替代品
- destroy method 替代品

它只是给“容器管理的可运行组件”增加了一个更高层的启动/停止协议。
