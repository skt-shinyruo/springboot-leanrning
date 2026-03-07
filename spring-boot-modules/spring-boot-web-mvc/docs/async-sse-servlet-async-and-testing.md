# 01. Servlet Async（Callable）与测试（asyncDispatch）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：Servlet Async（Callable）与测试（asyncDispatch）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcAsyncSseLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 错误页（error/*.html）与内容协商（Accept：HTML vs JSON）](boot-error-error-pages-and-content-negotiation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. SSE（SseEmitter：text/event-stream 最小闭环）](async-sse-sse-emitter.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01：Servlet Async（Callable）与测试（asyncDispatch）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcAsyncSseLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcAsyncSseLabTest`

## 机制主线

- 本章用 `BootWebMvcAsyncSseLabTest` 固定 async 生命周期：先断言 asyncStarted，再 dispatch 拿最终响应。

## 源码与断点

建议断点：
- `org.springframework.web.context.request.async.WebAsyncManager#startCallableProcessing`
- `org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler#handleReturnValue`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcAsyncSseLabTest`

## 常见坑与边界

- 异步逻辑一旦引入线程切换，最容易出现“偶现红测”；所以示例必须是“可控、有限、可快速完成”的。

## 小结与下一章

- 下一章进入 SSE：如何用 `SseEmitter` 返回 `text/event-stream`，以及测试如何避免挂死。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcAsyncSseLabTest`

上一章：[03. 错误页（error/*.html）与内容协商（Accept：HTML vs JSON）](boot-error-error-pages-and-content-negotiation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. SSE（SseEmitter：text/event-stream 最小闭环）](async-sse-sse-emitter.md)
<!-- BOOKIFY:END -->
