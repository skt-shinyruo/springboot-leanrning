# 01. WebMvc 测试与排障（resolvedException / handler / 断点清单）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：WebMvc 测试与排障（resolvedException / handler / 断点清单）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcTestingDebuggingLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 条件请求（Last-Modified / If-Modified-Since / ETag / ShallowEtagHeaderFilter）](real-world-http-conditional-requests-last-modified-etag-filter.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. Observability（Interceptor 计时 vs Actuator 指标）](testing-observability-observability-and-metrics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01：WebMvc 测试与排障（resolvedException / handler / 断点清单）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcTestingDebuggingLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcTestingDebuggingLabTest`

## 机制主线

- 本章用 `BootWebMvcTestingDebuggingLabTest` 固定两条排障证据链：
  - 415 → `HttpMediaTypeNotSupportedException`
  - 406 → `HttpMediaTypeNotAcceptableException`
- 并用 `BootWebMvcExceptionResolverChainLabTest` 固定“400 的三类根因”：
  - binder/validation → `BindException`
  - @RequestBody validation → `MethodArgumentNotValidException`
  - converter/read → `HttpMessageNotReadableException`

## 源码与断点

常用断点清单（按场景）：
- 400（解析失败/校验失败）：`RequestResponseBodyMethodProcessor#resolveArgument`、`DataBinder#validate`、`ExceptionHandlerExceptionResolver`
- 406/415：`AbstractMessageConverterMethodProcessor#writeWithMessageConverters`、`AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
- 404/选路问题：`RequestMappingHandlerMapping#getHandlerInternal`
- 401/403（Security/CSRF）：`DelegatingFilterProxy#doFilter`、`FilterChainProxy#doFilterInternal`、`CsrfFilter#doFilterInternal`、`ExceptionTranslationFilter`

### Debug 预设建议（最常用 3 个断点 + 3 个观察字段）

如果不确定从哪开始，下这 3 个断点通常就能定位 80% 的 Web MVC 问题：

1. `DispatcherServlet#doDispatch`（总入口：证明“是否进入 MVC”）
2. `HandlerMethodArgumentResolverComposite#resolveArgument`（入参阶段：缺参/类型不匹配/校验入口）
3. `HandlerExceptionResolverComposite#resolveException`（异常翻译：谁把异常变成状态码/错误体）

搭配这 3 个观察字段（Watch List）：

- `request.getRequestURI()` / `request.getMethod()`（到底在请求什么）
- `MvcResult#getHandler()`（命中了哪个 handler）
- `MvcResult#getResolvedException()`（分支的“铁证”）

## 最小可运行实验（Lab）

- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`（converter 选择证据：响应头）
- Lab：`BootWebMvcExceptionResolverChainLabTest`（400 分支定位：resolvedException）

## 常见坑与边界

- `@WebMvcTest` 不会加载完整上下文：当问题涉及 filter chain、真实端口、静态资源链路差异时，需要用 `@SpringBootTest(webEnvironment=RANDOM_PORT)` 再补一条端到端断言。
- 引入 `spring-boot-starter-security` 后，POST 变 403：常见原因是 CSRF。教学场景可以保留一个端点演示分支；真实 API 通常会对无状态接口关闭 CSRF。
- 当需要“确认到底是 401 还是 403”：用 `status()` 固化现象后，再看响应头/异常入口（filter 链断点），避免盲改 controller。

## 小结与下一章

- 本章完成后建议回看 Part 03/04：用断点验证对 resolver/converter 的理解是否正确。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`
- Lab：`BootWebMvcExceptionResolverChainLabTest`

上一章：[05. 条件请求（Last-Modified / If-Modified-Since / ETag / ShallowEtagHeaderFilter）](real-world-http-conditional-requests-last-modified-etag-filter.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. Observability（Interceptor 计时 vs Actuator 指标）](testing-observability-observability-and-metrics.md)
<!-- BOOKIFY:END -->
