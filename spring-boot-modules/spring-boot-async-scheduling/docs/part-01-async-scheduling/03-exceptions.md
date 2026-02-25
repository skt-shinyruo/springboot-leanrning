# 03. 异常传播：Future vs void
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（失败到底谁能看见）"

    异步异常最容易把人坑到的点是：**它常常不是“丢了”，而是“你看错了地方”。**

    - 返回 `Future/CompletableFuture`：失败会回到调用方的 future 上（`get/join` 时才暴露）
    - 返回 `void`：失败不会回到调用方，最终落在 `AsyncUncaughtExceptionHandler`
    - 进一步验证：`BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. Executor 与线程命名/并发边界](02-executor-and-threading.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. self-invocation：为什么异步有时不生效](04-self-invocation.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. 异常传播：Future vs void**
- 建议入口：优先运行 `BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture`（见文末“对应 Lab/Test”），先明确“异常不是丢了”，再对照 void 的 handler 语义补齐边界。



## 先从一个线上味道很重的场景开始

你把某个操作改成 `@Async`，希望它不阻塞调用方。上线后，偶尔有人反馈“没生效”，但调用链上没异常、监控也没报警。你翻日志，发现后台线程里其实早就炸了。

异步异常之所以容易被忽略，不是因为它“消失”了，而是因为它有两种完全不同的语义：

- 返回 `Future/CompletableFuture`：失败会回到调用方的 future 上（`get/join` 时才暴露）
- 返回 `void`：失败不会回到调用方，最终落在 `AsyncUncaughtExceptionHandler`

这不是细枝末节，而是你在设计“失败可见性”时必须做的选择。

## Future：把失败留在调用链里

当 `@Async` 方法返回 `Future/CompletableFuture`，异常不会在调用点抛出，而是被塞进 future，等调用方 `get()` / `join()` 时再以包装异常的形式抛出来。

你会在调用方看到的通常是：

- `Future#get()` → `ExecutionException`
- `CompletableFuture#join()` → `CompletionException`

真正的业务异常在 root cause 上。这一点如果没拎清，排障时很容易盯着“外面的异常类型”转圈。

证据入口：

- `BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture`

## void：把失败交给 handler（但别指望“默认就够用”）

当返回值是 `void` 时，调用方没有容器接住异常，Spring 会把异常交给 `AsyncUncaughtExceptionHandler`。

这类写法常见于 fire-and-forget：比如发通知、刷缓存、异步打点。但它的隐患也很现实：如果 handler 没处理好，你就只剩“某个线程里有一行 stacktrace”（甚至还不一定看得到）。

证据入口（语义 + 细节）：

- 基础语义：`BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler`
- handler 能拿到 method + args：`BootAsyncSchedulingUncaughtExceptionHandlerLabTest#voidAsyncExceptions_areDeliveredToUncaughtExceptionHandlerWithMethodAndArgs`

## 断点入口（可选）

如果你想看“异常是怎么被分流”的：

- `org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`：包装与转交发生的位置
- `org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler#handleUncaughtException`：void 异步异常最终落点

## 进一步验证（可选）

本章相关的最小集合是：

- `BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture`
- `BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler`
- `BootAsyncSchedulingUncaughtExceptionHandlerLabTest#voidAsyncExceptions_areDeliveredToUncaughtExceptionHandlerWithMethodAndArgs`

## 小结

在异步这件事上，“异常能不能被看到”不是框架帮你做的默认保证，而是你在 API 设计时做的选择：要不要把失败留在调用链里，要不要让调用方背上等待与处理的责任。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`

上一章：[part-01-async-scheduling/02-executor-and-threading.md](02-executor-and-threading.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/04-self-invocation.md](04-self-invocation.md)

<!-- BOOKIFY:END -->
