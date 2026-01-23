# 03：DeferredResult（回调式异步）与 timeout/fallback（可控分支）

## 导读

- 本章主题：**03：DeferredResult（回调式异步）与 timeout/fallback（可控分支）**
- 目标：在已有 Callable/SSE 的基础上补齐 DeferredResult：讲清它与 Callable 的模型差异、timeout 分支的工程落地方式，并用测试把 `asyncStarted → asyncDispatch` 的闭环固定下来。

!!! summary "本章要点"

    - `Callable`：更像“把 controller 的返回值延迟到另一个线程计算”，模型偏函数式（return a value）。
    - `DeferredResult`：更像“先返回一个承诺（promise），稍后由回调把结果塞进去”，模型偏事件式（setResult）。
    - timeout/fallback 的价值：把“超时后的行为”做成可控契约（而不是靠默认超时抛异常或让客户端猜）。
    - 教学实现里，为了让 MockMvc 回归稳定，本模块用“可控延迟回退”来模拟 timeout 分支（不依赖真实 Servlet 容器的 timeout 事件）。
    - 对测试来说：只要返回的是 async 类型（Callable/DeferredResult/SseEmitter），就要用 MockMvc 的 `asyncDispatch` 固定最终响应。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcAsyncSseLabTest`

## 机制主线（WebAsyncManager）

你可以把 DeferredResult 的流程理解成：

1. handler 返回 DeferredResult → 触发 asyncStarted
2. 业务侧（线程/回调）调用 `setResult`（或触发 timeout → fallback）
3. 容器触发二次 dispatch（测试中体现为 `asyncDispatch`）
4. DispatcherServlet 将结果写回响应

这也解释了为什么 Interceptor 在 async 场景会出现“两阶段回调”（可对照 Part 01 的 lifecycle 章节）。

## 源码与断点

建议断点（按主线）：
- `org.springframework.web.context.request.async.WebAsyncManager#startDeferredResultProcessing`
- `org.springframework.web.context.request.async.DeferredResult#setResultInternal`
- `org.springframework.web.servlet.DispatcherServlet#doDispatch`（二次 dispatch）

## 最小可运行实验（Lab）

- Lab：`BootWebMvcAsyncSseLabTest`
  - `deferredResultPingUsesAsyncDispatch`（正常完成）
  - `deferredResultTimeoutFallsBackToDefaultValue`（timeout/fallback 分支）

建议联动理解：
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

### 对应 Lab/Test

- Lab：`BootWebMvcAsyncSseLabTest`
- Lab：`BootWebMvcTraceLabTest`

上一章：[part-06-async-sse/02-sse-emitter.md](02-sse-emitter.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md](../part-07-testing-debugging/080-01-webmvc-testing-and-troubleshooting.md)

<!-- BOOKIFY:END -->
