# Proxying phase：代理什么时候出现，谁会先拿到它

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释代理可能在创建链路的哪些阶段出现：预实例化短路、早期引用和初始化后包装。
    - 核心结论：调用方拿到的未必是 raw object；代理可能在 before-instantiation、early reference 或 after-init 阶段出现。
    - 还要理解 self-invocation 为什么会绕过 proxy，以及 JDK proxy 和 CGLIB proxy 对类型可见性的影响。

    观察对象：代理插入创建链路的不同阶段。
    主线位置：实例化前、循环依赖中、初始化后暴露前后。
    对照入口：`SpringCoreBeansProxyingPhaseLabTest`、`SpringCoreBeansLifecycleRawVsProxyLabTest`。
<!-- CHAPTER-CARD:END -->

代理并不只在“初始化后”出现。容器链路里至少有三个常见位置会看到代理进入视野。

## 1. 预实例化短路阶段

在实例化前，`postProcessBeforeInstantiation` 一类机制可以直接返回一个代理或替代对象，从而跳过原始构造链。

这类代理的特点是：raw object 甚至可能根本不会被创建。它比普通 after-init 包装更早，属于“先给你一个可用对象，原始构造不一定发生”的路径。

## 2. early reference 阶段

循环依赖中，容器可能在 bean 尚未完全初始化时提前暴露一个 early reference。这个引用可以是 raw object，也可以是已经被提前包装的 proxy。

这就是为什么 `getEarlyBeanReference()` 这个扩展点很重要。它决定了早期暴露给别人的到底是原始对象还是可调用的代理。完整的循环依赖对象流属于 [early-reference-and-three-level-cache.md](early-reference-and-three-level-cache.md)，本文只关心代理在这条链路里的出现阶段。

## 3. after-init 包装阶段

最常见的代理插入点还是 `BeanPostProcessor#postProcessAfterInitialization`。此时 bean 已经走完初始化回调，后处理器可以返回一个新的 proxy 作为最终 exposed object。

`SpringCoreBeansProxyingPhaseLabTest` 和 `SpringCoreBeansLifecycleRawVsProxyLabTest` 合起来说明了这一点：

- `@PostConstruct` 看到的是 raw bean
- after-init BPP 可以把最终返回值换成 JDK proxy
- 调用方最终拿到的对象可能和构造时完全不同

## self-invocation 为什么会绕过 proxy

代理只能拦截“经过代理对象的调用”。如果同一个对象内部用 `this.inner()` 直接调用自己，调用不会先经过外层 proxy，自然也不会触发拦截逻辑。

`SpringCoreBeansProxyingPhaseLabTest` 直接证明了这一点：

- `outer()` 经过代理时会被记录
- `outer()` 内部调用 `this.inner()` 不会再次经过代理
- 直接调用 `service.inner()` 时，才会走代理拦截

所以“我明明有代理，为什么切面没生效”时，要先问是不是 self-invocation。

## proxy 类型会改变调用方能看到什么

JDK proxy 只实现接口；CGLIB proxy 是子类。这个差异会直接影响：

- 按 concrete class 取 bean 是否成功
- 反射和类型判断结果
- 注入点是否接受这个对象

`SpringCoreBeansProxyingPhaseLabTest` 里两种 proxy 都被观察到了。JDK proxy 让 concrete class lookup 失效，而 CGLIB proxy 仍然可以按父类类型拿到。

## 看到代理时，先问它是在哪个阶段来的

代理不是单一机制。你要先分清：

- 是预实例化短路出来的
- 是 early reference 提前暴露的
- 还是初始化后才包装出来的

阶段不同，raw object 是否存在、谁先拿到它、以及 self-invocation 是否失效，都会变。
