# 03. AOP 调用链（从代理入口到 Advice 链执行）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：AOP 调用链（从代理入口到 Advice 链执行）展开，主线可以概括为：Spring AOP 以代理实现：容器阶段由 AutoProxyCreator 作为 BPP 创建代理；运行阶段由 JDK/CGLIB 代理把调用转发到 `ReflectiveMethodInvocation#proceed`，逐个执行拦截器（Advice）。

    先运行 `SpringCoreAopProceedNestingLabTest`，把“proceed 嵌套顺序/拦截器链”固化成断言，再按本文把调用链串起来：代理如何生成（BPP 阶段）→ 调用如何进入代理 → 如何执行 `MethodInterceptor` 链。

    需要下探源码时，可以从 `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator` / `org.springframework.aop.framework.JdkDynamicAopProxy#invoke` / `org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南：把“代理是怎么来的、advice 链怎么跑”落到源码与断点](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Spring AOP Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01：AOP 调用链（从代理入口到 Advice 链执行）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreAopProceedNestingLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopProceedNestingLabTest`

## 1. 生成链：代理是怎么在容器里产生的？

Spring AOP 默认不是“编译期织入”，而是“运行期代理”。因此需要先回答一个最常见的排障问题：

> 这个 bean 什么时候变成了 proxy？

在 Spring 里，这个问题大多数时候都能回到同一个答案：

- **AutoProxyCreator 是一个 BeanPostProcessor（BPP）**
- 它在 bean 初始化后（after initialization）有机会把 bean 换成 proxy

主线（高层视角）：

1. 容器创建 bean（原始对象）
2. BPP 链执行
3. 轮到 AutoProxyCreator：如果命中 Advisor/Pointcut → `wrapIfNecessary` → 创建 proxy
4. 容器最终暴露的是 proxy（之后注入/获取到的都是 proxy）

可以优先从这些入口验证“是否走过生成链”：

- `AbstractAutoProxyCreator#postProcessAfterInitialization`
- `AbstractAutoProxyCreator#wrapIfNecessary`

## 2. 执行链：一次方法调用是怎么进入 Advice 链的？

当拿到的是代理对象时，调用会先进入代理层，再进入拦截器链。

### 2.1 JDK 代理 vs CGLIB 代理：入口不同，但核心相同

两条入口（常见）：

1. **JDK 动态代理**：`JdkDynamicAopProxy#invoke(...)`
2. **CGLIB 代理**：`CglibAopProxy.DynamicAdvisedInterceptor#intercept(...)`

它们最终都会落到同一件事：

- 构造一个 `MethodInvocation`（通常是 `ReflectiveMethodInvocation`）
- 从 `Advised` 上拿到 `interceptorsAndDynamicMethodMatchers`
- 进入 `proceed()` 执行链

### 2.2 Advice 链执行：`proceed()` 嵌套决定 before/after 顺序

核心逻辑（需要能复述出来）：

1. `proceed()` 每调用一次，就推进到链条下一个拦截器
2. “前置逻辑”发生在 `invocation.proceed()` 之前
3. “后置逻辑”发生在 `invocation.proceed()` 之后
4. 最底层会调用目标方法（reflection invoke）

因此调试时应当形成这个直觉：

- 多个 around advice 的执行顺序，本质是多次嵌套的 `proceed()`（像递归一样）

对应证据链建议优先用：

- `SpringCoreAopProceedNestingLabTest`（把顺序固化成断言，不靠日志）

## 3. 三个最常见的“为什么没走 AOP”的分叉点

> 这些分叉点不只是概念，它们都能落到“能验证的入口”。

1. **自调用绕过（self-invocation）**
   - 现象：`this.inner()` 没有被拦截
   - 根因：调用没有经过代理对象
2. **代理类型不匹配（JDK vs CGLIB）**
   - 现象：按实现类注入失败/pointcut 命中差异（this/target）
   - 根因：JDK 代理只代理接口；CGLIB 基于子类
3. **切点根本没命中（Advisor/Pointcut 未匹配）**
   - 现象：bean 有 proxy，但某个方法没有被拦截
   - 根因：表达式/注解匹配范围不对

这些分叉点对应的“推荐断点清单”见下一章的断点地图：

- [02：断点地图（AOP Debugger Pack）](04-breakpoint-map.md)

## 小结与下一章

- 本章把 AOP 的“生成链/执行链”串成了一条可复述叙事；下一章把这些入口收敛为 Debugger Pack。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopProceedNestingLabTest`
- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest`
- Lab：`SpringCoreAopProxyMechanicsLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
