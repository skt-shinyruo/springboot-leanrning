# SmartInitializingSingleton：所有非懒 singleton 结束后的一次统一回调

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 `SmartInitializingSingleton#afterSingletonsInstantiated()` 的触发时机和边界。
    - 核心结论：它发生在所有非懒 singleton 预实例化完成之后，不等于某个 bean 自己的 init 回调，也不等于应用完全启动完成。
    - 它更像是“容器里该创建的单例都先创建完了”这一时刻的统一观察点。

    观察对象：非懒 singleton 预实例化完成后的统一回调。
    主线位置：refresh 的 singleton 预实例化收尾阶段。
    对照入口：`SpringCoreBeansSmartInitializingSingletonLabTest`。
<!-- CHAPTER-CARD:END -->

`SmartInitializingSingleton` 提供的是一个容器级别的收尾点。它不是针对某个 bean 的初始化回调，而是在所有非懒 singleton 都已经创建完之后才触发。

## 它比单个 init 回调更晚

单个 bean 的 init callback 只负责自己的初始化闭环；`afterSingletonsInstantiated()` 则会等整个非懒 singleton 集合都先完成预实例化。

`SpringCoreBeansSmartInitializingSingletonLabTest` 里能看到这一点：

- eager bean 已经构造完成
- `afterSingletonsInstantiated()` 触发时，lazy bean 还不在 singleton cache 里
- lazy bean 只有在后续显式获取时才创建

这说明该回调关注的是“容器整体的单例收口”，不是“某个 bean 的私有生命周期”。

## 它也不是应用启动完成事件

`SmartInitializingSingleton` 发生在容器内部，不等于 Spring Boot 的 application started / ready 事件，也不等于整个应用所有外部依赖都已就绪。

它能保证的是：

- 非懒 singleton 已经被实例化
- 容器内部可见的单例图已经基本稳定

它不能自动保证：

- 外部系统已经可用
- Web 容器已经对外提供流量
- 其他上下文中的 bean 也已经完成

所以它适合做“容器内部的统一检查和收尾”，不适合当作应用级启动完成的同义词。

## 适合放什么逻辑

这类回调适合做：

- 检查某些单例是否都已注册并创建
- 做需要完整单例图的二次确认
- 统计、预热或观察容器状态

不适合做：

- 依赖某个 lazy bean 一定已经存在的逻辑
- 依赖外部资源完全可用的最终就绪逻辑
- 替代单个 bean 自己的 init method

## 看到它时，先问时间点

只要记住一句话就够了：它发生在“所有非懒 singleton 预实例化完成之后”，不是更早，也不是更晚到应用 ready 事件那一层。
