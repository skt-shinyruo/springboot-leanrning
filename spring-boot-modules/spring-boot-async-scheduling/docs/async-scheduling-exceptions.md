# 03. 异常传播：Future vs void
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（失败到底谁能看见）"

    异步异常最容易把人坑到的点是：**它常常不是“丢了”，而是“看错了地方”。**

    - 返回 `Future/CompletableFuture`：失败会回到调用方的 future 上（`get/join` 时才暴露）
    - 返回 `void`：失败不会回到调用方，最终落在 `AsyncUncaughtExceptionHandler`
    - 进一步验证：`BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. Executor 与线程命名/并发边界](async-scheduling-executor-and-threading.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. self-invocation：为什么异步有时不生效](async-scheduling-self-invocation.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture`（见文末“对应实验/测试”），先明确“异常不是丢了”，再对照 void 的 handler 语义补齐边界。


## 先从一个线上味道很重的场景开始

把某个操作改成 `@Async`，希望它不阻塞调用方。上线后，偶尔有人反馈“没生效”，但调用链上没异常、监控也没报警。翻日志，发现后台线程里本质上早就炸了。

异步异常之所以容易被忽略，不是因为它“消失”了，而是因为它有两种完全不同的语义：

- 返回 `Future/CompletableFuture`：失败会回到调用方的 future 上（`get/join` 时才暴露）
- 返回 `void`：失败不会回到调用方，最终落在 `AsyncUncaughtExceptionHandler`

这不是细枝末节，而是在设计“失败可见性”时必须做的选择。

## Future：把失败留在调用链里

当 `@Async` 方法返回 `Future/CompletableFuture`，异常不会在调用点抛出，而是被塞进 future，等调用方 `get()` / `join()` 时再以包装异常的形式抛出来。

会在调用方看到的通常是：

- `Future#get()` → `ExecutionException`
- `CompletableFuture#join()` → `CompletionException`

真正的业务异常在 root cause 上。这一点如果没拎清，排障时很容易盯着“外面的异常类型”转圈。

证据入口：

- `BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture`

### ListenableFuture：回调式 Future（仍能见到，但更常用 CompletableFuture）

在一些历史代码里，`@Async` 也会返回 `ListenableFuture`：它仍然是 Future，只是多了一层 callback API，让调用方可以用“回调 + 超时”的方式完成断言，而不是在调用线程里阻塞 `get()`。

对新代码而言，`CompletableFuture` 往往更自然（语言层面的 API、组合能力更强）。但理解 `ListenableFuture` 的语义仍然有价值：它常常出现在老项目或旧的组件接口里，排障时仍会遇到。

对照入口（可选）：

- `BootAsyncSchedulingLabTest#asyncListenableFuture_canUseCallbackInsteadOfBlockingGet`

## void：把失败交给 handler（但别指望“默认就够用”）

当返回值是 `void` 时，调用方没有容器接住异常，Spring 会把异常交给 `AsyncUncaughtExceptionHandler`。

这类写法常见于 fire-and-forget：比如发通知、刷缓存、异步打点。但它的隐患也很现实：如果 handler 没处理好，就只剩“某个线程里有一行 stacktrace”（甚至还不一定看得到）。

证据入口（语义 + 细节）：

- 基础语义：`BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler`
- handler 能拿到 method + args：`BootAsyncSchedulingUncaughtExceptionHandlerLabTest#voidAsyncExceptions_areDeliveredToUncaughtExceptionHandlerWithMethodAndArgs`

## 断点入口（可选）

如果想看“异常是怎么被分流”的：

- `org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`：包装与转交发生的位置
- `org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler#handleUncaughtException`：void 异步异常最终落点

## 进一步验证（可选）

本章相关的最小集合是：

- `BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture`
- `BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler`
- `BootAsyncSchedulingUncaughtExceptionHandlerLabTest#voidAsyncExceptions_areDeliveredToUncaughtExceptionHandlerWithMethodAndArgs`

## 小结

在异步这件事上，“异常能不能被看到”不是框架默认提供的保证，而是在 API 设计时做的选择：要不要把失败留在调用链里，要不要让调用方背上等待与处理的责任。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootAsyncSchedulingLabTest`

上一章：[async-scheduling-executor-and-threading.md](async-scheduling-executor-and-threading.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[async-scheduling-self-invocation.md](async-scheduling-self-invocation.md)

<!-- BOOKIFY:END -->
