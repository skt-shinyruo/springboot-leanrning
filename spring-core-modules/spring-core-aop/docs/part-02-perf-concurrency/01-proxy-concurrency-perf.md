# 01. 并发 / 性能：同一 proxy 并发调用边界（ThreadLocal 不串线）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：并发 / 性能：同一 proxy 并发调用边界（ThreadLocal 不串线）
    - 怎么使用：先跑本章 Lab，把“proxy 可并发调用 + advice 的 ThreadLocal 状态不跨线程串线”固化成断言；再回到正文理解为什么 proxy 是共享对象、invocation 是每次调用独立对象，以及什么状态是安全的、什么是危险的。
    - 原理：AOP proxy 通常是单例复用；每次方法调用会创建独立的 `MethodInvocation` 并执行拦截器链；如果 advice 需要携带“调用上下文”，应使用 ThreadLocal（并在 finally 清理）或显式上下文传递机制。
    - 源码入口：`org.springframework.aop.framework.DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopProxyConcurrencyLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）](../part-02-autoproxy-and-pointcuts/02-pointcut-expression-system.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 多切面/多代理叠加与顺序：AOP/Tx/Cache/Security 代理链如何叠、如何看](../part-03-proxy-stacking/01-multi-proxy-stacking.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「并发 / 性能：同一 proxy 并发调用边界（ThreadLocal 不串线）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreAopProxyConcurrencyLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - proxy 通常是单例复用：多个线程会同时调用同一个 proxy 对象。
    - invocation（一次调用的上下文）是每次调用独立的：不要把“每次调用状态”放在 aspect 的字段里。
    - ThreadLocal 是常见的“每线程上下文承载”方案：必须在 finally 清理，否则会在线程池里造成串线/泄露。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopProxyConcurrencyLabTest`

## 机制主线

### 1) 先澄清一个误解：proxy 不是“每次调用新建的”

在典型 Spring 应用中：

- bean（以及其 proxy）通常是单例（singleton）对象
- 因此在高并发下，多线程会对同一个 proxy 发起调用

这意味着：

- **proxy 本身必须能被并发安全地调用**
- 但这并不代表“advice 可以随便存状态”

### 2) 哪些对象是每次调用独立的？

一次方法调用进入 proxy 后，Spring AOP 会为这次调用准备“执行上下文”，典型形态是：

- `MethodInvocation`（通常是 `ReflectiveMethodInvocation`）
- 本次调用要执行的拦截器链（interceptors）

这些对象是“按调用构造/按方法缓存”的，关键结论是：

- **不要把 per-invocation 状态存到单例 aspect 的字段里**（会跨线程互相覆盖）

### 3) advice 需要携带上下文怎么办？

常见的工程需求是“携带 correlation id / trace id / 请求上下文”等。

在 proxy AOP 模型下，典型做法之一是 ThreadLocal：

- 在 advice 进入时 set
- 在 finally 里 clear/remove

本章 Lab 用 ThreadLocal 演示一个可断言结论：

> 同一个 proxy 并发调用时，每个线程都能看到自己设置的 correlation id（不会串线）。

### 4) 并发与性能：应当关心的边界

- **线程安全边界**：proxy 可共享；advice 要么无状态，要么状态是线程隔离/线程安全的。
- **线程池风险**：ThreadLocal 如果不清理，会在复用线程时泄露上一次请求的上下文。
- **链条长度成本**：拦截器链越长，单位调用的开销越大；建议先用最小切点建立基线，再逐步叠加增强验证成本。

## 在本模块如何验证（建议跑一次）

跑这一个测试方法即可：

- `SpringCoreAopProxyConcurrencyLabTest#proxyInvocation_isThreadIsolated_underConcurrentCalls`

建议命令（可选）：

- `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyConcurrencyLabTest test`

应当能得到一个明确结论：

- 多线程并发调用同一 proxy，最终断言全部通过（不串线）

## 推荐断点（可选）

如果想把“线程隔离”在断点里看见：

- advice 入口：`SpringCoreAopProxyConcurrencyLabTest.CorrelationIdAspect#around`
- 目标方法：`SpringCoreAopProxyConcurrencyLabTest.ConcurrencyTracedService#echoCorrelationId`
- 链条主线：`ReflectiveMethodInvocation#proceed`（观察每个线程的调用上下文是独立推进的）

## 常见坑与边界

1. **把调用上下文写到 aspect 字段里**
   - 现象：并发下随机失败、日志串线、偶发脏数据
   - 根因：aspect 通常是单例，字段会被多线程覆盖
   - 修复：用 ThreadLocal 或显式上下文传参；并保证 finally 清理
2. **ThreadLocal 忘记清理（线程池复用导致泄露）**
   - 现象：后续请求读到“上一请求”的上下文
   - 修复：使用 `try/finally`，在 finally 中 `remove()`
3. **误以为 exposeProxy 能解决跨线程上下文**
   - exposeProxy/currentProxy 依赖当前线程上下文；换线程（`@Async`）不会自动传播

## 小结与下一章

- 本章把“proxy 并发调用边界”固化成了可断言事实；下一章进入“多增强叠加与顺序”，会看到并发下更常见的复杂链路形态（Tx/Cache/Security）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopProxyConcurrencyLabTest`

上一章：[08-pointcut-expression-system](../part-02-autoproxy-and-pointcuts/02-pointcut-expression-system.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[09-multi-proxy-stacking](../part-03-proxy-stacking/01-multi-proxy-stacking.md)

<!-- BOOKIFY:END -->
