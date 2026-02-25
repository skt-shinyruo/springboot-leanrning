# 06. `@Async` × `@Transactional`：事务边界与执行线程
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（事务边界就是线程边界）"

    工程里很常见的一种误解是：调用方在 `@Transactional` 里调 `@Async`，以为异步逻辑也“在同一个事务里”。这章专门把这件事说清楚：事务到底在哪个线程里生效。

    - 最简单的判断：在两个线程里分别看 `TransactionSynchronizationManager.isActualTransactionActive()`
    - 最小复现入口：`BootAsyncSchedulingTransactionBoundaryLabTest#transactionContextDoesNotPropagateAcrossAsyncThreadBoundaryByDefault`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. `@Scheduled` 基础与可测试性](05-scheduling-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[07. SecurityContext / RequestContext：默认丢失、传播与泄漏](07-security-and-request-context.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**06. `@Async` × `@Transactional`：事务边界与执行线程**
- 建议入口：优先运行 `BootAsyncSchedulingTransactionBoundaryLabTest#transactionContextDoesNotPropagateAcrossAsyncThreadBoundaryByDefault`（见文末“对应 Lab/Test”），先看清“不会自动传播”，再理解“同一方法同时标注时事务在哪里开启”。



## 先从“以为能回滚”的那种 bug 说起

很多事故的起点都很朴素：你在一个事务里做了一些校验，然后顺手调用 `@Async` 去做写库/发消息，心想失败就回滚、成功就提交。上线后你会发现：调用方事务回滚了，但异步那边已经“写出去了”。

这不是 Spring 在耍赖，而是事务的底层语义决定的：**事务上下文绑定在线程上**。线程一换，事务也就跟着断开了。

## 机制主线

### 1) 事务上下文属于线程：它不是“调用链共享变量”

多数工程里你对事务的直觉是“调用链共享一个事务”。但对 Spring 来说更精确的描述是：

- **事务上下文绑定在当前线程**
- Spring 通过 `TransactionSynchronizationManager` 维护这份线程内状态

因此，一旦你把执行切到另一个线程，原线程的事务上下文就不会“自动跟过去”。

最小证据链（调用方在事务里，但异步线程事务 inactive）：

- `BootAsyncSchedulingTransactionBoundaryLabTest#transactionContextDoesNotPropagateAcrossAsyncThreadBoundaryByDefault`

### 2) `@Async @Transactional` 同时标注时，事务在哪里开启？

当一个方法同时标注 `@Async` 与 `@Transactional`，你真正关心的不是“有没有事务”，而是：

> 事务是在调用方线程开启，还是在异步线程开启？

这个分支只需要一个可验证的判断：

- 如果事务在异步线程：你在异步线程里观察到 `TransactionSynchronizationManager.isActualTransactionActive()==true`
- 如果事务在调用方线程：调用方线程才应该观察到 active

最小证据链（事务发生在 async 线程而不是调用方线程）：

- `BootAsyncSchedulingTransactionBoundaryLabTest#asyncAndTransactionalOnSameMethod_runsTransactionInsideAsyncThread_notCallerThread`

**这条结论是很多线上事故的根因**：

- 你以为“在调用方事务里丢一个 @Async 做异步写库，失败就回滚”
  - 实际：异步逻辑在另一个线程执行，属于另一个事务（甚至可能没有事务）
- 你以为“这个方法标了 @Transactional，怎么异步线程里还会出现非事务写入”
  - 实际：要看 `@Transactional` 拦截是在异步线程里生效，还是根本没生效（例如 self-invocation 绕过代理）

## 怎么判断：别靠猜，直接在两条线程里看状态

- **调用方事务 active ≠ 异步线程事务 active**
- 想判断“异步逻辑是否在事务中”，最直接的方法就是在两条线程里分别观察 `TransactionSynchronizationManager`：
  - `TransactionSynchronizationManager.isActualTransactionActive()`
  - `TransactionSynchronizationManager.isSynchronizationActive()`

## 源码与断点

建议按“先证据后源码”的顺序：

1. 在 Lab 的断言处打断点，分别看调用方线程与异步线程的 `TransactionSynchronizationManager` 状态
2. 再在拦截器入口观察“拦截链在哪个线程继续执行”

推荐断点：

- `org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`（切线程的决定点）
- `org.springframework.transaction.interceptor.TransactionInterceptor#invoke`（事务拦截入口）
- `org.springframework.transaction.support.TransactionSynchronizationManager`（观察点：active/synchronization）

## 最小可运行实验（Lab）

- Lab：`BootAsyncSchedulingTransactionBoundaryLabTest`

## 常见坑与边界

### 坑点 1：以为“事务会自动跨线程传播”

你会在代码里写出一种“看起来很合理”的业务逻辑，但它依赖了一个不存在的前提：事务能跨线程传播。

证据入口：

- `BootAsyncSchedulingTransactionBoundaryLabTest#transactionContextDoesNotPropagateAcrossAsyncThreadBoundaryByDefault`

工程层面的修法通常是策略而不是技巧：

- 需要事务一致性：别把“必须与主事务一致”的动作放进 `@Async`
- 需要异步但可补偿：把 outbox / 补偿 / 重试语义显式设计出来，而不是依赖“同一事务”

## 小结与下一章

- 本章完成后：继续阅读上下文传播章节，把“线程边界”扩展到安全/请求/MDC 等真实上下文。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingTransactionBoundaryLabTest`

上一章：[part-01-async-scheduling/05-scheduling-basics.md](05-scheduling-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/07-security-and-request-context.md](07-security-and-request-context.md)

<!-- BOOKIFY:END -->
