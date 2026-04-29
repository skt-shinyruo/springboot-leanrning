# 09. TargetSource：Proxy 到底转发到谁（LazyInit/HotSwap…）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕TargetSource：Proxy 到底转发到谁（LazyInit/HotSwap…）展开，主线可以概括为：Spring AOP proxy 并不是“直接持有 target 对象引用”，而是持有一个 `TargetSource`；默认是单例 target，但也可以懒加载、可热切换或来自池化。

    先运行 `SpringCoreAopTargetSourceLabTest`，把“同一个 proxy 不变，但 target 可以切换/延迟创建”固化成断言；再回到正文把它落回调试入口：`((Advised) proxy).getTargetSource()`。

    需要下探源码时，可以从 `org.springframework.aop.framework.AdvisedSupport#getTargetSource` / `org.springframework.aop.target.HotSwappableTargetSource` / `org.springframework.aop.target.LazyInitTargetSource` 这些入口切入。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[08. Introduction / Mixin：@DeclareParents 给 Proxy“加接口能力”](proxy-fundamentals-introduction-mixin.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[10. Proxy 的对象语义：equals/hashCode/toString/Map key（以及如何自证）](proxy-fundamentals-proxy-object-semantics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

在调试器里看到：

- `((Advised) bean).getTargetSource().getTarget()`

很多人会误以为“target 永远是那个实现类对象”。
但 Spring AOP 里，“target 从哪来”本来就是可配置的，这也是 TargetSource 的存在价值。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopTargetSourceLabTest`

## 1) TargetSource 是什么（够用版）

一句话：

> **TargetSource = proxy 每次调用要转发到哪个 target 的策略。**

proxy 不一定直接持有 target，它只需要能在调用时拿到一个“当前 target”。

## 2) 三个最常见的 TargetSource 形态

### 2.1 默认形态：单例 target（最常见）

可以把它理解为：

- proxy → 固定 target

### 2.2 `LazyInitTargetSource`：延迟创建 target

目标：把“target 的创建”推迟到第一次方法调用时再发生（常用于启动加速/避免启动期副作用）。

常见误区：

- 以为它会“每次调用都重新创建 target”
  - 实际：它更像“第一次懒创建，然后复用”（具体以实现为准；本章 Lab 会给可观察结论）。

### 2.3 `HotSwappableTargetSource`：热切换 target

目标：在不重建 proxy 的情况下，把 target 切到另一个实现对象。

适用场景：

- 蓝绿切换/灰度、热更新模拟、运行期开关（更常见于框架/基础设施层）

风险提示：

- 切换本身通常是线程安全的，但 “旧 target 上的状态/资源” 如何回收属于需要负责的部分。

## 3) 调试入口：如何把 target 看清楚

优先用下面三件套：

- `AopUtils.isAopProxy(bean)`：先确认是不是 proxy
- `bean.getClass()` vs `AopUtils.getTargetClass(bean)`：再确认在看 proxy 还是 target 类型
- `((Advised) bean).getTargetSource()`：最后确认 target 的来源策略

## 小结与下一章

- proxy 通过 `TargetSource` 决定“调用转发到谁”；target 可能延迟创建、甚至可切换。
- 下一章进入 proxy 的对象语义：当把 proxy 当成普通对象（equals/hashCode/toString/Map key）时，会发生哪些坑。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreAopTargetSourceLabTest`

上一章：[08-introduction-mixin](proxy-fundamentals-introduction-mixin.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[10-proxy-semantics](proxy-fundamentals-proxy-object-semantics.md)

<!-- BOOKIFY:END -->

