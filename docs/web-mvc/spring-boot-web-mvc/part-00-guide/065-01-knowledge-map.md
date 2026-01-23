# 第 65 章：01：知识地图（Web MVC Deep Dive Map）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：知识地图（Web MVC Deep Dive Map）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 64 章：00 - Deep Dive Guide（springboot-web-mvc）](064-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 66 章：02：断点地图（Part 01 Debugger Pack）](066-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：知识地图（Web MVC Deep Dive Map）**
- 目标：把本模块的“知识点”收敛成一张可导航的地图：**主轴（调用链）+ 关键分支（状态码/异常）+ 证据链（可跑 Lab/Test）**。

!!! summary "本章要点"

    - 你要记住的不只是“有哪些注解”，而是**一次请求在 MVC 里经历的阶段**，以及每个阶段“为什么会分叉”（例如 404/406/415/400）。
    - 学习路径建议：**先跑 Lab 看到现象 → 再看 `resolvedException`/handler 证据 → 再打断点看调用链**。
    - 推荐搭配：断点地图（Part 01 Debugger Pack）`part-00-guide/02-breakpoint-map.md`（把断点从“散点”收敛成“按阶段可复用清单”）。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`

## 机制主线（从请求到响应）

> 下面按“主轴顺序”列出关键阶段 + 对应章节入口（建议按顺序阅读）。

1. **进入 Servlet 容器（最外层）**
   - Filter（包含 Spring Security FilterChain）
   - 关键问题：为什么 401/403 往往发生在 controller 之前？

2. **DispatcherServlet 主分发**
   - 选路：`HandlerMapping`（找到 handler / 没找到就是 404）
   - 执行：`HandlerAdapter`（决定如何调用 handler）

3. **入参解析（Argument Resolution）**
   - `HandlerMethodArgumentResolver`：`@RequestBody`、`@RequestParam`、`@PathVariable`、自定义 resolver 等
   - 常见分支：参数缺失/类型转换失败 → 400（type mismatch / missing parameter）

4. **绑定与校验（Binding + Validation）**
   - `DataBinder` + `ConversionService`：把 String 变成目标类型
   - Bean Validation（`@Valid`）：把约束失败变成可控响应
   - 常见分支：`BindException` / `MethodArgumentNotValidException`
   - 工程边界：`@InitBinder#setAllowedFields` 防 mass assignment；并可用 `BindingResult#getSuppressedFields()` 把“被阻止绑定字段”变成证据

5. **业务方法执行（Controller）**
   - 这里不是 MVC 的全部，反而常常不是问题发生的位置

6. **返回值处理（Return Value Handling）**
   - `HandlerMethodReturnValueHandler`：决定怎么写回响应
   - 典型分支：返回值是否需要序列化？是否走 view 渲染？

7. **序列化与内容协商（HttpMessageConverter + Content Negotiation）**
   - `Accept`/`Content-Type`/`produces`/`consumes` → converter 选择
   - 可观测证据：`ResponseBodyAdvice#beforeBodyWrite` 的 `selectedConverterType/selectedContentType`（写回阶段）
   - 关键分支：
     - 415：请求体格式不被支持（读不到）
     - 406：响应格式不被接受（写不出）

8. **异常处理（Exception Resolvers）**
   - `@ControllerAdvice` / `@ExceptionHandler`：把异常塑形成统一响应（`ApiError` / `ProblemDetail`）
   - 匹配规则：`basePackages` / `annotations` / `assignableTypes`（先决定“是否适用”）
   - 优先级：当多个 advice 都能处理同一异常时，`@Order` 决定最终生效（在“适用集合”之内排序）
   - 关键分支：异常来自哪个阶段（resolver/binder/converter/controller）？
   - 进一步：ExceptionResolvers 的“翻译链”（`ExceptionHandlerExceptionResolver` / `ResponseStatusExceptionResolver` / `DefaultHandlerExceptionResolver`）

9. **缓存与条件请求（ETag / 304）**
   - `ETag` + `If-None-Match` → 304（响应体可省略）
   - 对照：静态资源 `Last-Modified` + `If-Modified-Since` → 304；框架级 `ShallowEtagHeaderFilter` → 304

10. **观测与排障（Observability + Debugging）**
    - Interceptor 计时 vs 指标（`http.server.requests`）
    - `resolvedException`/handler/断点清单：把排障变成流程

## 源码与断点（建议从测试反推）

常用断点入口（按主轴顺序）：
- `org.springframework.web.filter.DelegatingFilterProxy#doFilter`（有 Security 时）
- `org.springframework.security.web.FilterChainProxy#doFilterInternal`（有 Security 时）
- `org.springframework.web.servlet.DispatcherServlet#doDispatch`
- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#getHandlerInternal`
- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#handleInternal`
- `org.springframework.web.method.support.HandlerMethodArgumentResolverComposite#resolveArgument`
- `org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor#resolveArgument`（@ModelAttribute）
- `org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor#resolveArgument`（@RequestBody）
- `org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`（读）
- `org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor#writeWithMessageConverters`（写）
- `org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`

## 最小可运行实验（Lab）

建议按“先主线，再分叉”的顺序跑：

### 主线（REST/页面/端到端）
- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Lab：`BootWebMvcViewLabTest` / `BootWebMvcErrorViewLabTest`

### 深挖（机制/契约/真实 HTTP/Async）
- Lab：`BootWebMvcInternalsLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`（converter 选择证据：selectedConverterType/selectedContentType）
- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcRealWorldHttpLabTest`
- Lab：`BootWebMvcAsyncSseLabTest`

### 排障/分支证据链
- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Lab：`BootWebMvcAdviceOrderLabTest`
- Lab：`BootWebMvcAdviceMatchingLabTest`

### Security / Observability（本次扩展）
- Lab：`BootWebMvcSecurityLabTest`
- Lab：`BootWebMvcObservabilityLabTest`

## 常见坑与边界

- 建议配合阅读：`appendix/90-common-pitfalls.md`（把“坑”变成可复现 + 可定位）

## 小结与下一章

- 本章完成后：先跑一次主线 Lab（E），再进入 Part 01 从“校验与错误塑形”开始建立第一个闭环。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Lab：`BootWebMvcInternalsLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`
- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcRealWorldHttpLabTest`
- Lab：`BootWebMvcAsyncSseLabTest`
- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Lab：`BootWebMvcAdviceOrderLabTest`
- Lab：`BootWebMvcAdviceMatchingLabTest`
- Lab：`BootWebMvcSecurityLabTest`
- Lab：`BootWebMvcObservabilityLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](064-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-mvc/01-validation-and-error-shaping.md](../part-01-web-mvc/071-01-validation-and-error-shaping.md)

<!-- BOOKIFY:END -->
