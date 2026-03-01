# 05. Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕05：Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcTraceLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. DeferredResult（回调式异步）与 timeout/fallback（可控分支）](03-deferredresult-and-timeout.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. CORS 与预检（OPTIONS：浏览器为什么要先问一句）](../13-real-world-http/01-cors-preflight.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「05：Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcTraceLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcTraceLabTest`

## 机制主线（把 async 视为“两次 dispatch”）

把 async 看成 **两阶段**，会更容易理解“为什么回调少了”：

1. **第一次 dispatch（REQUEST）**
   - 进入 DispatcherServlet，选路与参数解析照常发生
   - handler 返回 `Callable` / `DeferredResult` / `SseEmitter` → WebAsyncManager 启动并发处理
   - 这时 Interceptor 会触发 `afterConcurrentHandlingStarted`

2. **第二次 dispatch（ASYNC）**
   - async 线程把结果写回 WebAsyncManager
   - 容器触发一次新的 dispatch（在测试里体现为 `asyncDispatch`）
   - DispatcherServlet 再跑一遍处理流程（但 handler 通常不会再执行）
   - Interceptor 这一次会完整触发 `preHandle/postHandle/afterCompletion`

## 源码与断点（建议从 Lab 反推）

建议断点（配合本章 Lab）：
- `org.springframework.web.servlet.DispatcherServlet#doDispatch`
- `org.springframework.web.servlet.HandlerExecutionChain#applyPreHandle`
- `org.springframework.web.servlet.HandlerExecutionChain#applyPostHandle`
- `org.springframework.web.servlet.HandlerExecutionChain#triggerAfterCompletion`
- `org.springframework.web.servlet.AsyncHandlerInterceptor#afterConcurrentHandlingStarted`

关键观察点（决定性分支）：

- `request.getDispatcherType()`：第一次通常是 REQUEST，二次 asyncDispatch 是 ASYNC
- 是否进入 `afterConcurrentHandlingStarted`：它是“第一次 async dispatch”最典型的证据
- handler 方法是否二次执行：通常不会（第二次 dispatch 主要是把 async 结果写回）

## 最小可运行实验（Lab）

本章使用“事件序列”把 lifecycle 变成可断言结果：

- Lab：`BootWebMvcTraceLabTest`
- 对应端点：
  - sync：`GET /api/advanced/trace/sync`
  - async：`GET /api/advanced/trace/async`

建议命令（方法级入口）：

```bash
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcTraceLabTest#asyncTraceRecordsAfterConcurrentHandlingStartedAndAsyncDispatchCallbacks test
```

在测试里会看到两类证据：

1. sync：完整回调链路（含 Filter finally 的 after）
2. async：第一次 dispatch 出现 `afterConcurrentHandlingStarted`，第二次 dispatch 出现 `postHandle/afterCompletion`

## 常见坑与边界

- **坑 1：只在 postHandle 做观测**
  - async 第一次 dispatch 不会进 postHandle，会“漏掉一半日志/埋点”。
  - 处理策略：观测逻辑要么放在 Filter（更外层），要么同时覆盖 async lifecycle（`afterConcurrentHandlingStarted` + 二次 dispatch）。

- **坑 2：把 async 当作“只是换了线程”**
  - 实际上它改变了 DispatcherServlet 的回调时机：看到的行为差异通常来自“两次 dispatch”的结构。

- **坑 3：测试只写一次 perform，不做 asyncDispatch**
  - 只能看到“asyncStarted”，看不到最终响应；这会让排障停留在“感觉”而不是“证据”。

## 小结与下一章

- 本章完成后：建议回到 Part 06 对照 Callable/DeferredResult/SSE 的实现与测试方式，形成“生命周期解释 → 可跑证据链”的闭环。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcTraceLabTest`

上一章：[03. DeferredResult（回调式异步）与 timeout/fallback（可控分支）](03-deferredresult-and-timeout.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. CORS 与预检（OPTIONS：浏览器为什么要先问一句）](../13-real-world-http/01-cors-preflight.md)
<!-- BOOKIFY:END -->
