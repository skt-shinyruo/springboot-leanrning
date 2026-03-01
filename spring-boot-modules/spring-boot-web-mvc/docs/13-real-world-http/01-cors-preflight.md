# 01. CORS 与预检（OPTIONS：浏览器为什么要先问一句）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：CORS 与预检（OPTIONS：浏览器为什么要先问一句）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcRealWorldHttpLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）](../12-async-sse/05-interceptor-async-lifecycle.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. Multipart 上传（multipart/form-data：与 JSON 完全不同的边界）](02-multipart-upload.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01：CORS 与预检（OPTIONS：浏览器为什么要先问一句）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcRealWorldHttpLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcRealWorldHttpLabTest`

## 机制主线

- 本章用 `BootWebMvcRealWorldHttpLabTest` 固定预检请求与响应头断言。

## 源码与断点

建议断点：
- `org.springframework.web.cors.DefaultCorsProcessor#processRequest`
- `org.springframework.web.servlet.handler.AbstractHandlerMapping#getHandler`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcRealWorldHttpLabTest`

## 常见坑与边界

- 只在后端放开 GET，但忘了允许前端带的自定义 header（例如 `X-Request-Id`），预检会失败。

## 小结与下一章

- 下一章进入 multipart 上传：为什么 `@RequestBody` 解析不了 `multipart/form-data`。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcRealWorldHttpLabTest`

上一章：[05. Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）](../12-async-sse/05-interceptor-async-lifecycle.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. Multipart 上传（multipart/form-data：与 JSON 完全不同的边界）](02-multipart-upload.md)
<!-- BOOKIFY:END -->
