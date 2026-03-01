# Spring Boot Web MVC：请求主线、绑定与错误形状

本模块围绕一次 HTTP 请求在 Spring MVC 中的完整旅程展开：从 FilterChain 进入，到 `DispatcherServlet#doDispatch` 选路与执行，再到参数解析、绑定与消息转换，最后收敛为一致的异常与错误响应形状。重点不在“记住有哪些注解”，而在建立可复用的排障路径：看到 400/406/415 等错误时，能够在最短调用链与断点上确认分支与根因。

---

## 10 分钟入口：先把一条请求链路跑通

- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`

运行后应能回答三个事实问题：

1. 请求进入 MVC 的关键入口在哪里（从 FilterChain 到 `doDispatch`）；
2. 绑定与转换失败时会走哪类异常解析；
3. 最终错误响应形状由哪些组件共同决定。

---

## 阅读路线（按请求从进入到结束的顺序）

### 0) 先建立坐标：拿到“全图”与断点入口

1. [从注解到断点：用一条主线学会 Spring MVC](00-guide/00-from-annotations-to-breakpoints.md)
2. [主线时间线](00-guide/01-mainline-timeline.md)
3. [深挖导读](00-guide/02-deep-dive-guide.md)
4. [知识地图（先看全图）](00-guide/05-knowledge-map.md)
5. [断点图（排障优先）](14-testing-observability/06-breakpoint-map.md)
6. [关键分支矩阵（If/Then 收敛）](14-testing-observability/04-branch-decision-matrix.md)

### 1) C1：进入容器（FilterChain / Security）

- 关键对象：`FilterChain` / `jakarta.servlet.Filter`、`DelegatingFilterProxy`、`FilterChainProxy`、`SecurityFilterChain`
- 扩展点：`SecurityFilterChain` 配置（认证/鉴权/CSRF/异常处理）；自定义 Filter（addFilterBefore/After）
- 常见分支：401（未认证）/ 403（无权限或 CSRF）/ 302（登录跳转，取决于安全配置）
- 证据（断点/测试）：`BootWebMvcSecurityLabTest`；断点 `DelegatingFilterProxy#doFilter` / `FilterChainProxy#doFilterInternal`（对照[断点图](14-testing-observability/06-breakpoint-map.md)）
- [Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）](01-filterchain-security/01-security-filterchain-and-mvc.md)

### 2) C2：进入 MVC（DispatcherServlet#doDispatch）

- 关键对象：`DispatcherServlet`、`HandlerMapping`、`HandlerAdapter`、`HandlerExecutionChain`
- 扩展点：`WebMvcConfigurer`（Interceptor/Converter/ArgumentResolver/Async）；`@ControllerAdvice`（异常收敛）
- 常见分支：404/405（选路）；400（解析/绑定/校验）；406/415（协商与 converter）；async 二次 dispatch
- 证据（断点/测试）：`BootWebMvcInternalsLabTest`；断点 `DispatcherServlet#doDispatch` / `processDispatchResult`
- [请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）](02-dispatcherservlet/03-webmvc-request-call-chain.md)
- [DispatcherServlet 主链路（把选路/参数解析/返回值/异常串起来）](02-dispatcherservlet/01-dispatcherservlet-call-chain.md)

### 3) C3：选路（HandlerMapping：404/405 的起点）

- 关键对象：`RequestMappingHandlerMapping`、`RequestMappingInfo`、`HandlerMethod`
- 扩展点：`@RequestMapping` 条件（path/method/params/headers/consumes/produces）
- 常见分支：404（无 handler）/ 405（路径命中但方法不支持）
- 证据（断点/测试）：`BootWebMvcSpringBootLabTest`；断点 `RequestMappingHandlerMapping#getHandlerInternal` / `handleNoMatch`
- [HandlerMapping：路由、404/405 与 mapping 约束](03-handlermapping/01-handlermapping-routing.md)

### 4) C4：拦截器（Interceptor）与“入口顺序”

- 关键对象：`HandlerInterceptor` / `AsyncHandlerInterceptor`、`HandlerExecutionChain`、`OncePerRequestFilter`
- 扩展点：`WebMvcConfigurer#addInterceptors`；自定义 Filter（容器层）
- 常见分支：`preHandle` 短路；async 两次 dispatch（REQUEST/ASYNC）；ERROR 分发（错误页）
- 证据（断点/测试）：`BootWebMvcTraceLabTest`；断点 `HandlerExecutionChain#applyPreHandle` / `triggerAfterCompletion`
- [Interceptor 与 Filter：入口在哪里、顺序怎么理解](04-handleradapter-interceptor/04-interceptor-and-filter-ordering.md)

### 5) C5：参数解析（ArgumentResolver）

- 关键对象：`HandlerMethodArgumentResolver`、`HandlerMethodArgumentResolverComposite`、`RequestResponseBodyMethodProcessor`、`WebDataBinder`
- 扩展点：`WebMvcConfigurer#addArgumentResolvers`；自定义注解 + resolver；`@InitBinder`
- 常见分支：缺参/类型不匹配（400）；`@RequestBody` 走 converter；`@ModelAttribute` 走 binder
- 证据（断点/测试）：`BootWebMvcInternalsLabTest` / `BootWebMvcBindingDeepDiveLabTest`；断点 `HandlerMethodArgumentResolverComposite#resolveArgument`
- [ArgumentResolver 与 Binder（解析参数/绑定/校验触发点）](05-argument-resolver/02-argument-resolver-and-binder.md)

### 6) C6：绑定/转换/校验（Binder / Conversion / Validation）

- 关键对象：`WebDataBinder`、`ConversionService`、`Converter/Formatter`、`Validator`
- 扩展点：`WebMvcConfigurer#addFormatters`；`@InitBinder#setAllowedFields`；`@Valid/@Validated`
- 常见分支：`BindException` / `MethodArgumentNotValidException` / `HandlerMethodValidationException`
- 证据（断点/测试）：`BootWebMvcBindingDeepDiveLabTest`；断点 `WebDataBinder#bind` / `DataBinder#validate`
- [请求绑定（Binding）与 Converter/Formatter](06-binding-validation/03-binding-and-converters.md)
- [校验（Validation）与错误响应形状（Error Shape）](06-binding-validation/01-validation-and-error-shaping.md)

### 7) C7：消息转换与内容协商（HttpMessageConverter / 406 / 415 / Jackson）

- 关键对象：`HttpMessageConverter`、`ContentNegotiationManager`、`ObjectMapper`
- 扩展点：`extendMessageConverters`；`Jackson2ObjectMapperBuilderCustomizer`；`ResponseBodyAdvice`
- 常见分支：415（读不进）/ 406（写不出）/ 400（malformed JSON）
- 证据（断点/测试）：`BootWebMvcErrorBranchMatrixLabTest` / `BootWebMvcContractJacksonLabTest`；断点 `readWithMessageConverters` / `writeWithMessageConverters`
- [Content Negotiation（406/415：Accept/Content-Type/produces/consumes）](07-message-conversion/01-content-negotiation-406-415.md)
- [Jackson ObjectMapper 可控（严格模式、未知字段、时间）](07-message-conversion/02-jackson-objectmapper-controls.md)

### 8) C8：Controller（边界与职责）

- 关键对象：`@RestController/@Controller`、`HandlerMethod`、`BindingResult`
- 扩展点：方法签名选择（`ResponseEntity` / `BindingResult`）；`@Validated`；`@InitBinder`
- 常见分支：有无 `BindingResult` 会改变是否抛异常；很多错误（401/403/406/415）并不发生在 controller 内
- 证据（断点/测试）：`BootWebMvcLabTest`；断点 `RequestMappingHandlerAdapter#invokeHandlerMethod`
- [Controller：边界、异常与契约的位置](08-controller/01-controller-boundary.md)

### 9) C9：返回值与写回（ReturnValue / View / Converter(write)）

- 关键对象：`HandlerMethodReturnValueHandler`、`ViewResolver`、`ModelAndView`
- 扩展点：ViewResolver 配置；返回值类型选择（view vs body）；PRG（Post/Redirect/Get）
- 常见分支：渲染/重定向/序列化三条路；写回失败常表现为 406/500
- 证据（断点/测试）：`BootWebMvcErrorViewLabTest`；断点 `HandlerMethodReturnValueHandlerComposite#handleReturnValue` / `writeWithMessageConverters`
- [传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）](09-return-value-view/01-thymeleaf-and-view-resolver.md)
- [表单提交闭环（@ModelAttribute / BindingResult / 校验回显 / PRG）](09-return-value-view/02-form-binding-validation-prg.md)
- [HttpMessageConverter 与返回值处理（序列化发生在哪里）](09-return-value-view/03-message-converters-and-return-values.md)

### 10) C10：异常收敛（ExceptionResolvers / ControllerAdvice / Error Shape）

- 关键对象：`HandlerExceptionResolverComposite`、`ExceptionHandlerExceptionResolver`、`@ControllerAdvice/@ExceptionHandler`
- 扩展点：自定义 `@ControllerAdvice`；advice matching/ordering；`ProblemDetail` vs 自定义错误体
- 常见分支：400 的三类根因（converter/binder/validation）；advice 不生效/被更高优先级覆盖
- 证据（断点/测试）：`BootWebMvcExceptionResolverChainLabTest` / `BootWebMvcAdviceOrderLabTest` / `BootWebMvcAdviceMatchingLabTest`；断点 `DispatcherServlet#processHandlerException`
- [统一异常处理（ControllerAdvice）与“坏输入”](10-exception-resolvers/02-exception-handling.md)
- [错误契约加固（解析失败 vs 校验失败 vs 类型不匹配）](10-exception-resolvers/03-error-contract-hardening.md)
- [ExceptionResolvers（异常从哪来、又被谁“翻译”成状态码）](10-exception-resolvers/04-exception-resolvers-and-error-flow.md)
- [ProblemDetail vs 自定义错误体（ApiError：契约的两种路线）](10-exception-resolvers/04-problemdetail-vs-custom-error.md)
- [ControllerAdvice 的匹配与优先级（为什么 advice 生效/不生效）](10-exception-resolvers/05-controlleradvice-matching-and-ordering.md)

### 11) C11：Spring Boot /error（ERROR dispatch、错误页与 Accept）

- 关键对象：`BasicErrorController`、`ErrorAttributes`、ERROR dispatch（`DispatcherType.ERROR`）
- 扩展点：自定义 `error/*.html` 错误页；自定义 `ErrorAttributes`；`server.error.*` 配置
- 常见分支：404/500 回落 `/error`；Accept 决定 HTML vs JSON
- 证据（断点/测试）：`BootWebMvcSpringBootLabTest`；断点 `BasicErrorController#error`（结合 `request.getDispatcherType()==ERROR`）
- [错误页（error/*.html）与内容协商（Accept：HTML vs JSON）](11-boot-error/03-error-pages-and-content-negotiation.md)

### 12) C12：Async / SSE（第二次 dispatch：DispatcherType.ASYNC）

- 关键对象：`WebAsyncManager`、`Callable`、`DeferredResult`、`SseEmitter`
- 扩展点：`WebMvcConfigurer#configureAsyncSupport`；timeout/fallback；async interceptor
- 常见分支：REQUEST/ASYNC 二次 dispatch；timeout；客户端断开（SSE）
- 证据（断点/测试）：`BootWebMvcAsyncSseLabTest`；断点 `AsyncHandlerInterceptor#afterConcurrentHandlingStarted` / `WebAsyncManager`
- [Servlet Async（Callable）与测试（asyncDispatch）](12-async-sse/01-servlet-async-and-testing.md)
- [SSE（SseEmitter：text/event-stream 最小闭环）](12-async-sse/02-sse-emitter.md)
- [DeferredResult（回调式异步）与 timeout/fallback（可控分支）](12-async-sse/03-deferredresult-and-timeout.md)
- [Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）](12-async-sse/05-interceptor-async-lifecycle.md)

### 13) C13：真实 HTTP 场景（CORS / 上传下载 / 静态资源 / 缓存）

- 关键对象：CORS Processor、`MultipartResolver`、`ResourceHttpRequestHandler`、`ShallowEtagHeaderFilter`
- 扩展点：`addCorsMappings`；上传大小限制；静态资源 handler；缓存控制（ETag/Last-Modified）
- 常见分支：预检 OPTIONS；上传解析失败；静态资源 304/404
- 证据（断点/测试）：`BootWebMvcRealWorldHttpLabTest`
从 [01](13-real-world-http/01-cors-preflight.md) 开始按顺序阅读即可（01～05）。

### 14) C14：测试 / 排障 / 观测（证据链）

- 关键对象：`MockMvc`、`MvcResult`（`handler` / `resolvedException`）、`MeterRegistry`
- 扩展点：`@WebMvcTest` 切片 + `@Import` advice；TimingInterceptor；Actuator exposure
- 常见分支：用 `resolvedException` 分型 400/406/415；指标端点暴露与路径匹配
- 证据（断点/测试）：`BootWebMvcTestingDebuggingLabTest` / `BootWebMvcObservabilityLabTest`；工具页：[分支矩阵](14-testing-observability/04-branch-decision-matrix.md) / [断点图](14-testing-observability/06-breakpoint-map.md)
- [WebMvc 测试与排障（resolvedException / handler / 断点清单）](14-testing-observability/01-webmvc-testing-and-troubleshooting.md)
- [Observability（Interceptor 计时 vs Actuator 指标）](14-testing-observability/02-observability-and-metrics.md)
- [关键分支矩阵（Web MVC Branch Decision Matrix）](14-testing-observability/04-branch-decision-matrix.md)
- [断点地图（Part 01 Debugger Pack）](14-testing-observability/06-breakpoint-map.md)

---

## 排障入口（从症状回到最短分支）

- 断点地图（排障优先）：[06-breakpoint-map.md](14-testing-observability/06-breakpoint-map.md)
- 请求调用链速览（快速定位）：[03-webmvc-request-call-chain.md](02-dispatcherservlet/03-webmvc-request-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[04-branch-decision-matrix.md](14-testing-observability/04-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- Branch Matrix（错误分支矩阵 400/406/415）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-web-mvc -Dtest=*ExerciseSolutionTest test`
- 并发/性能（RequestScope 隔离 / 并发请求边界）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcRequestScopeIsolationLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
