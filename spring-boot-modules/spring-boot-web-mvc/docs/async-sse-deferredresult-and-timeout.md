# 03. DeferredResult（回调式异步）与 timeout/fallback（可控分支）

## 导读

- 目标：在已有 Callable/SSE 的基础上补齐 DeferredResult：讲清它与 Callable 的模型差异、timeout 分支的工程落地方式，并用测试把 `asyncStarted → asyncDispatch` 的闭环固定下来。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcAsyncSseLabTest`

## 机制主线（WebAsyncManager）

可以把 DeferredResult 的流程理解成：

1. handler 返回 DeferredResult → 触发 asyncStarted
2. 业务侧（线程/回调）调用 `setResult`（或触发 timeout → fallback）
3. 容器触发二次 dispatch（测试中体现为 `asyncDispatch`）
4. DispatcherServlet 将结果写回响应

这也解释了为什么 Interceptor 在 async 场景会出现“两阶段回调”（可对照 Part 01 的 lifecycle 章节）。

## 源码与断点

断点入口（按主线）：
- `org.springframework.web.context.request.async.WebAsyncManager#startDeferredResultProcessing`
- `org.springframework.web.context.request.async.DeferredResult#setResultInternal`
- `org.springframework.web.servlet.DispatcherServlet#doDispatch`（二次 dispatch）

## 最小可运行实验（Lab）

- Lab：`BootWebMvcAsyncSseLabTest`
  - `deferredResultPingUsesAsyncDispatch`（正常完成）
  - `deferredResultTimeoutFallsBackToDefaultValue`（timeout/fallback 分支）

联动理解：
- lifecycle 对照：`BootWebMvcTraceLabTest`（解释 async 两次 dispatch）

## 常见坑与边界

- **坑 1：测试不做 asyncDispatch**
  - 只断言 `asyncStarted` 不能保证最终响应正确；必须 `asyncDispatch` 才算闭环。

- **坑 2：timeout 行为不受控**
  - 真实工程里，timeout 必须是契约的一部分（返回什么形状/什么错误码/是否可重试）。
  - 本模块用 fallback 让 timeout 分支可测试、可解释。

- **坑 3：在 async 场景只用 postHandle 做清理**
  - 第一次 dispatch 可能不进 postHandle；资源清理/埋点需要覆盖 async lifecycle（可对照 Part 01 的 Interceptor 生命周期章节）。

## 小结与下一章

- 本章完成后：进入 Part 07（Testing & Debugging），把 async 与错误分支一起纳入“可回归排障流程”。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebMvcAsyncSseLabTest`
- Lab：`BootWebMvcTraceLabTest`

上一章：[02. SSE（SseEmitter：text/event-stream 最小闭环）](async-sse-sse-emitter.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）](async-sse-interceptor-async-lifecycle.md)
<!-- BOOKIFY:END -->
