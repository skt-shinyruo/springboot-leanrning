# 10. Proxy 的对象语义：equals/hashCode/toString/Map key（以及如何自证）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕 Proxy 的对象语义：equals/hashCode/toString/Map key（以及如何自证）展开，主线可以概括为：Spring AOP 的 proxy 是一个“替身对象”；它在类型、身份与方法分发上都可能与 target 不同；在工程里最容易踩坑的是“把 proxy 当成 target 的等价物”。

    先运行 `SpringCoreAopProxyObjectSemanticsLabTest`，把“proxy ≠ target、getClass/类型判断应使用 AopUtils 工具”固化成断言；再回到正文理解：为什么日志/缓存 key/equals 等场景容易误判，以及如何用最短证据链自证。

    需要下探源码时，可以从 `org.springframework.aop.support.AopUtils#getTargetClass` / `org.springframework.aop.framework.AopProxyUtils#ultimateTargetClass` / `org.springframework.aop.framework.Advised#getTargetSource` 这些入口切入。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[09. TargetSource：Proxy 到底转发到谁（LazyInit/HotSwap…）](proxy-fundamentals-targetsource-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. AOP 的容器主线：AutoProxyCreator 作为 BPP（Advisor / Advice / Pointcut 三层模型）](autoproxy-and-pointcuts-autoproxy-creator-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

这章是“排坑章”，核心目标只有一个：

> **当你写出 `bean.getClass()` / `bean instanceof Impl` / `bean.toString()` / `Map<Bean, ...>` 时，能立刻意识到：你在处理 proxy 还是 target？**

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopProxyObjectSemanticsLabTest`

## 1) 三条最常用的结论（够用版）

1) **Proxy ≠ Target（通常不是同一个对象）**
   - 这不是 bug，是 AOP 的工作方式。
2) **类型判断不要用 `getClass()` 直觉**
   - 用 `AopUtils.getTargetClass(bean)` / `AopProxyUtils.ultimateTargetClass(bean)` 更稳。
3) **把 proxy 放进缓存/Map 作为 key 时要极谨慎**
   - 你缓存的是“代理对象引用”，还是“业务 identity”？这是两件事。

## 2) 为什么 equals/hashCode/toString 容易误判？

因为它们看起来像“普通对象方法”，但在 proxy 语境里它们有两个额外变量：

- JDK/CGLIB 代理类型差异（见第 2 章）
- 目标方法调用是否经过 proxy（call path）

因此，本仓库建议的排障策略是：

1) 先把 proxy/target 的事实查清楚（第 6、9 章）
2) 再讨论“对象语义/行为不一致”的根因

## 3) 最短证据链：如何在调试器里自证

在一个断点里，按下面顺序看：

- `bean.getClass()`（你拿到的对象到底是谁）
- `AopUtils.getTargetClass(bean)`（目标类型）
- `bean instanceof Advised`，若是：
  - `((Advised) bean).getTargetSource().getTarget()`（真实 target 实例）

## 4) 工程建议（不求完美，只求不踩大坑）

- 需要“目标类型”的地方：统一用 `AopUtils.getTargetClass` / `ultimateTargetClass`
- 需要“业务身份”的地方：不要把 proxy 当成 identity；用业务 id/主键/稳定键
- 需要“打印/日志”的地方：不要依赖 `toString()` 观察 AOP 是否生效；用断言/断点/`Advised#getAdvisors` 更靠谱

## 小结与下一章

- 把 proxy 当成 target 的等价物，是很多“看起来很玄学”的 bug 的根源。
- 下一章回到容器主线：AutoProxyCreator 如何决定代理、如何组装 advisors。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopProxyObjectSemanticsLabTest`

上一章：[09-targetsource](proxy-fundamentals-targetsource-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[07-autoproxy-creator-mainline](autoproxy-and-pointcuts-autoproxy-creator-mainline.md)

<!-- BOOKIFY:END -->

