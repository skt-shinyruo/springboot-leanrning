# 自定义 Scope 与 scoped proxy：调用方拿到的为什么不是目标对象

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 Scope SPI 和 scoped proxy 如何协作，让长生命周期 Bean 安全持有短生命周期目标。
    - 核心结论：调用方通常拿到的是代理，真正的目标对象要在当前 scope 上下文里解析；上下文缺失或线程切换时，行为会随 scope 实现而变。
    - 还要看清楚：目标销毁回调由 scope 负责触发，代理类型选择会影响注入点能否匹配。

    观察对象：自定义 scope 的上下文解析、代理转发和销毁时机。
    主线位置：Bean 需要按 scope 解析，而不是直接从 singleton 缓存返回时。
    对照入口：`SpringCoreBeansCustomScopeLabTest`。
<!-- CHAPTER-CARD:END -->

自定义 scope 解决的不是“怎么新建对象”，而是“对象应该存在哪里、什么时候取、什么时候清理”。`Scope` SPI 允许容器把目标对象的存取责任交给一个上下文实现。

## Scope SPI 的核心动作

一个 scope 需要回答四件事：

1. 当前 scope 名下是否已经有目标对象
2. 如果没有，怎样创建并缓存它
3. 如何为该对象登记销毁回调
4. scope 结束时怎样触发这些回调

`SpringCoreBeansCustomScopeLabTest` 中的 thread scope 和可切换 scope 都体现了这个模型：对象不是直接躺在 singleton 缓存里，而是按上下文键取出。

## scoped proxy 做了什么

scoped proxy 的作用不是“让对象变成 proxy 就完了”，而是把“当前 scope 下的真实目标对象查找”延后到每次方法调用时。

所以调用方拿到的通常是代理对象，而不是目标对象本身。代理内部再去当前 scope 上下文取目标。这样一来，singleton 可以持有一个稳定引用，同时每次调用都能命中当前上下文里的实例。

这正是 `SpringCoreBeansCustomScopeLabTest` 里 scoped proxy 的关键观察：

- 单例消费者持有的是代理
- 线程切换后，代理会路由到各自线程的目标对象
- 没有 proxy 时，singleton 会在注入点把目标“冻结”为一个固定引用

## 上下文缺失时会怎样

自定义 scope 不是魔法。若调用发生时当前上下文不存在，scope 实现可以：

- 抛出异常
- 返回默认策略对象
- 延迟创建上下文

哪一种都取决于 scope 实现本身，而不是 scoped proxy 自动补齐。

这也是为什么跨线程使用时要特别谨慎。当前线程可能根本没有对应上下文，或者上下文虽然存在，但不是预期的那一个。此时代理能做的只是把问题延后到目标解析那一刻。

## 目标销毁回调归谁

销毁回调的登记通常由容器完成，但真正触发它的是 scope。

`SpringCoreBeansCustomScopeLabTest` 里可切换 scope 的实验说明得很清楚：对象被创建时，容器会把销毁回调交给 scope；当 scope 被清理时，回调才真正执行。也就是说，scope 不是纯粹的查表器，它还承担生命周期收口。

## 代理类型会影响注入点

scoped proxy 既可以是 JDK 动态代理，也可以是类代理。选择哪一种，直接影响注入点是否能匹配：

- 只有接口可注入时，JDK proxy 够用
- 需要按 concrete class 注入时，JDK proxy 可能不兼容
- 类代理更接近目标类型，但也会改变类型识别和方法拦截行为

`SpringCoreBeansCustomScopeLabTest` 中对 `ScopedProxyMode.INTERFACES` 的观察就说明了这一点：容器会额外注册 `scopedTarget.<beanName>`，而外层 bean 名称对应的是代理入口，不是实际目标。

## 先判断 scope，再判断 proxy

遇到自定义 scope 相关问题时，先确认三件事：

- 当前上下文是否真的存在
- 调用方拿到的是代理还是目标对象
- 销毁回调是怎么被触发的

只盯着 bean name 很容易误判。真正决定行为的是 scope 上下文和 proxy 的分工。
