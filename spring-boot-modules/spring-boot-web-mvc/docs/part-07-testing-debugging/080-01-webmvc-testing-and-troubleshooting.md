# 第 80 章：01：WebMvc 测试与排障（resolvedException / handler / 断点清单）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：WebMvc 测试与排障（resolvedException / handler / 断点清单）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcTestingDebuggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 79 章：01：Servlet Async（Callable）与测试（asyncDispatch）](../part-06-async-sse/079-01-servlet-async-and-testing.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 81 章：01：Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）](../part-08-security-observability/081-01-security-filterchain-and-mvc.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：WebMvc 测试与排障（resolvedException / handler / 断点清单）**
- 目标：把“怎么调 Web MVC”写成可执行套路：先靠测试拿到现象，再用 `resolvedException`/`handler` 定位分支，最后用断点看调用链。

!!! summary "本章要点"

    - 看到 406/415，不要盯 controller：先看 `Accept`/`Content-Type`/`produces`/`consumes`，再看 converter 选择。
    - 用 MockMvc 的 `MvcResult#getResolvedException()` 能快速把“猜”变成“证据”：异常类型就是分支位置。
    - 看到 401/403，优先怀疑 Filter/Security，而不是 MVC handler：**很多安全分支发生在 DispatcherServlet 之前**。
      - 证据链建议：用 `MvcResult#getHandler()` / `MvcResult#getResolvedException()` 先证明“是否进入了 HandlerMethod”（见 `BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`）。
    - 当你需要“确认到底选了哪个 HttpMessageConverter”：可以用 `ResponseBodyAdvice#beforeBodyWrite` 把 `selectedConverterType/selectedContentType` 写进响应头，再用测试固化它（证据链优先）。


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

如果你不确定从哪开始，下这 3 个断点通常就能定位 80% 的 Web MVC 问题：

1. `DispatcherServlet#doDispatch`（总入口：证明“是否进入 MVC”）
2. `HandlerMethodArgumentResolverComposite#resolveArgument`（入参阶段：缺参/类型不匹配/校验入口）
3. `HandlerExceptionResolverComposite#resolveException`（异常翻译：谁把异常变成状态码/错误体）

搭配这 3 个观察字段（Watch List）：

- `request.getRequestURI()` / `request.getMethod()`（你到底在请求什么）
- `MvcResult#getHandler()`（命中了哪个 handler）
- `MvcResult#getResolvedException()`（分支的“铁证”）

## 最小可运行实验（Lab）

- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`（converter 选择证据：响应头）
- Lab：`BootWebMvcExceptionResolverChainLabTest`（400 分支定位：resolvedException）

## 常见坑与边界

- `@WebMvcTest` 不会加载完整上下文：当问题涉及 filter chain、真实端口、静态资源链路差异时，需要用 `@SpringBootTest(webEnvironment=RANDOM_PORT)` 再补一条端到端断言。
- 引入 `spring-boot-starter-security` 后，POST 变 403：常见原因是 CSRF。教学场景可以保留一个端点演示分支；真实 API 通常会对无状态接口关闭 CSRF。
- 当你需要“确认到底是 401 还是 403”：用 `status()` 固化现象后，再看响应头/异常入口（filter 链断点），避免盲改 controller。

## 小结与下一章

- 本章完成后建议回看 Part 03/04：用断点验证你对 resolver/converter 的理解是否正确。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`
- Lab：`BootWebMvcExceptionResolverChainLabTest`

上一章：[part-06-async-sse/03-deferredresult-and-timeout.md](../part-06-async-sse/03-deferredresult-and-timeout.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-08-security-observability/01-security-filterchain-and-mvc.md](../part-08-security-observability/081-01-security-filterchain-and-mvc.md)

<!-- BOOKIFY:END -->
