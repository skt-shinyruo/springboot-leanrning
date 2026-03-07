# 05. 知识地图（Web MVC Deep Dive Map）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（源码入口 + 分支地图）"
    - 基线版本：Spring Framework `6.2.15`（`org.springframework:spring-webmvc:6.2.15`）
    - 这一页要解决：把 404/405/400/406/415/500/ASYNC/ERROR 映射回“主线阶段 → 源码入口”，从而决定断点应该打在哪一步
    - 证据链：`BootWebMvcBookMatrixLabTest`（全景）/ `BootWebMvcInternalsLabTest`（主线骨架）/ `BootWebMvcErrorBranchMatrixLabTest`（400/406/415）
    - 源码入口（最常用）：`org.springframework.web.servlet.FrameworkServlet#processRequest` / `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.HandlerExceptionResolverComposite#resolveException` / `readWithMessageConverters` / `writeWithMessageConverters`

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-web-mvc）](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. 断点地图（Part 01 Debugger Pack）](testing-observability-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01：知识地图（Web MVC Deep Dive Map）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

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

- 建议配合阅读：[01-common-pitfalls.md](appendix-common-pitfalls.md)（把“坑”变成可复现 + 可定位）

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

上一章：[02. 00 - Deep Dive Guide（springboot-web-mvc）](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. 断点地图（Part 01 Debugger Pack）](testing-observability-breakpoint-map.md)
<!-- BOOKIFY:END -->
