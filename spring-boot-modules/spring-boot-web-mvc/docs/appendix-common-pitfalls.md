# 01. 常见坑清单（Web MVC）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    Web MVC 的坑往往不是“写不出 Controller”，而是“把现象映射回主线时走错了分支”：401/403 发生在 FilterChain 里，还是发生在 `DispatcherServlet` 内？400 是 JSON 解析失败、类型绑定失败，还是校验失败？406/415 又是 read 还是 write 的内容协商问题？

    本章把这些高频误判整理成一张“分流地图”，并给出可以直接运行的 Lab/Test 入口。建议先跑 `BootWebMvcLabTest` 把主线跑通，再回到本章逐条对照；需要下探时，从 `DispatcherServlet#doDispatch` 与 `RequestMappingHandlerAdapter#invokeHandlerMethod` 两个入口切入最省时间。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. Observability（Interceptor 计时 vs Actuator 指标）](testing-observability-observability-and-metrics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99 自检：Spring Boot Web MVC](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章整理「90：常见坑清单（Web MVC）」相关的常见误判与排障入口。阅读时建议按“现象 → 分支 → 复现 → 修法”的顺序对照，而不是只背结论。
推荐先跑 `BootWebMvcLabTest`，用断言把分支固定下来，再回到本文逐条核对根因。

为了让“现象 → 边界”变成可重复证据，本章默认依赖两组入口：一组跑主线，一组专门把 400/406/415 这些错误分支跑全。跑完之后再回到断点地图/分支矩阵看调用栈，会更容易判断问题发生在 FilterChain 还是 `DispatcherServlet` 里。

- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`

断点地图见：[06-breakpoint-map.md](testing-observability-breakpoint-map.md)，关键分支矩阵见：[04-branch-decision-matrix.md](testing-observability-branch-decision-matrix.md)。当本章的坑都能被这些入口覆盖时，可以再用自检清单做一次回归：[02-self-check.md](appendix-self-check.md)。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`

## 机制主线

把“现象（状态码/响应体）”映射回 MVC 主线，是排障的最快路径。本章建议按下面顺序收敛：

1. **先判断发生在 Filter 还是 DispatcherServlet 内**
   - 典型现象：401/403 往往发生在 FilterChain（Security）里，而不是 controller。

2. **再判断是 mapping 失败还是 handler 执行失败**
   - mapping：404/405（路由/方法不支持）
   - handler 链路：参数解析/绑定/校验/消息体读写/业务异常

3. **把 400 拆成三类（不要混成“校验失败”）**
   - JSON 解析失败（body read）：`HttpMessageNotReadableException`
   - 类型不匹配（binder/param）：`MethodArgumentTypeMismatchException`
   - 约束失败（validation）：`MethodArgumentNotValidException` / `BindException`

4. **把 406/415 拆成 read vs write**
   - 415：read（Content-Type / consumes / converter 不匹配）
   - 406：write（Accept / produces / converter 不匹配）

5. **最后再看错误体“是谁翻译的”**
   - `@ControllerAdvice`（自定义 ApiError/ProblemDetail）
   - MVC 默认 resolver（405/406/415/…）

## 最小可运行实验（Lab）

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest` / `BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcRealWorldHttpLabTest` / `BootWebMvcSecurityLabTest` / `BootWebMvcObservabilityLabTest`
- Lab：`BootWebMvcAsyncSseLabTest`（含 DeferredResult）
- Lab：`BootWebMvcTraceLabTest`（Filter/Interceptor 顺序 + async lifecycle）
- 建议命令：`mvn -pl :spring-boot-web-mvc test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界


## 400/校验相关

- DTO 上写了约束注解，但 controller 入参没加 `@Valid`：校验不会触发。
- 400 不等于校验失败：建议先把“坏输入”分成三类（对应不同异常/断点）：
  - JSON 解析失败：`HttpMessageNotReadableException`（body 路径）
  - 绑定/类型不匹配：`MethodArgumentTypeMismatchException`（binder 路径）
  - 校验失败：`MethodArgumentNotValidException`（@RequestBody）或 `BindException`（@ModelAttribute）
  - 对照证据：`BootWebMvcLabTest` / `BootWebMvcBindingDeepDiveLabTest`
- binder 边界不止要“阻止绑定”，还要能“留下证据”：
  - `@InitBinder#setAllowedFields` 阻止危险字段（mass assignment）
  - `BindingResult#getSuppressedFields()` 输出“被阻止绑定字段”用于排障
  - 对照证据：`BootWebMvcBindingDeepDiveLabTest`

## 401/403（Security/CSRF）相关

- 引入 `spring-boot-starter-security` 后，很多 401/403 发生在 MVC 之前：优先从 FilterChainProxy 入手，不要先改 controller。
- **坑：试图用 `@ControllerAdvice/@ExceptionHandler` 统一处理 401/403**
  - 这类分支通常发生在 DispatcherServlet 之前，MVC 的异常解析链路根本没机会运行
  - 证据链建议：断言 `handler == null && resolvedException == null`（说明没进入 HandlerMethod/ExceptionResolvers）
  - 对照证据：`BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`
- 403 很多时候不是“没权限”，而是 **CSRF 缺失**（尤其是 POST/PUT/DELETE）。
  - 对照证据：`BootWebMvcSecurityLabTest`（401/403/CSRF）
  - 对照证据（边界）：`BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`（403 时 `handler/resolvedException` 为空）

推荐断点（401/403/CSRF）：

- `org.springframework.web.filter.DelegatingFilterProxy#doFilter`
- `org.springframework.security.web.FilterChainProxy#doFilterInternal`
- `org.springframework.security.web.access.ExceptionTranslationFilter#doFilter`
- `org.springframework.security.web.csrf.CsrfFilter#doFilterInternal`

## 406/415（内容协商）相关

- 415：`Content-Type` 与 `consumes`/converter 不匹配（read 失败）
- 406：`Accept` 与 `produces`/converter 不匹配（write 失败）
  - 对照证据：`BootWebMvcContractJacksonLabTest` / `BootWebMvcTestingDebuggingLabTest`
- 当需要确认“到底选了哪个 converter / 协商出的 content-type 是什么”：
  - 用 `ResponseBodyAdvice#beforeBodyWrite` 的 `selectedConverterType/selectedContentType` 写入响应头
  - 对照证据：`BootWebMvcMessageConverterTraceLabTest`

## ControllerAdvice 匹配/优先级相关

- advice “不生效” 常见原因不是 handler 写错，而是 selector 没命中：
  - `basePackages` 不包含 controller（包名变更/分包调整）
  - `annotations` selector：注解必须是 RUNTIME，且要标在 controller 类型上
  - `assignableTypes` selector：controller 必须满足可赋值关系（接口/父类）
  - 多个 selector 的组合是“并集（OR）”：不要把 basePackages 当成“再过滤一次”
  - 对照证据：`BootWebMvcAdviceMatchingLabTest`
- 当多个 advice 同时可处理同一异常时，`@Order` 决定最终生效（数值越小优先级越高）
  - 对照证据：`BootWebMvcAdviceOrderLabTest`

## 304/缓存（ETag）相关

- 304 是“条件请求命中”的正常分支：客户端带 `If-None-Match`，服务端用 ETag 判断没变化就不返回 body。
  - 对照证据：`BootWebMvcRealWorldHttpLabTest`
- 静态资源也可能命中 304：`Last-Modified` + `If-Modified-Since`。
  - 对照证据：`BootWebMvcRealWorldHttpLabTest`（静态资源条件请求）
- 通过 `ShallowEtagHeaderFilter` 也能触发 ETag/304（框架级计算 ETag）。
  - 对照证据：`BootWebMvcRealWorldHttpLabTest`（filter-etag）

## Async 相关（Callable/DeferredResult/SSE）

- 测试只断言 `asyncStarted` 不算闭环：必须 `asyncDispatch` 才能断言最终响应。
  - 对照证据：`BootWebMvcAsyncSseLabTest`
- async 下 Interceptor 回调“少一截”是正常现象：第一次 dispatch 会触发 `afterConcurrentHandlingStarted`，第二次 dispatch 才会走 `postHandle/afterCompletion`。
  - 对照证据：`BootWebMvcTraceLabTest`

## `@WebMvcTest` 相关

- `@WebMvcTest` 只加载 Web 层：如果 controller 依赖 service/repository，通常需要 `@MockBean` 或显式 `@Import`。
- `@WebMvcTest` 的“更快”来自加载范围更小：如果把太多东西 `@Import` 进来，它就不再快了。
- **坑：`@WebMvcTest` 下突然出现 401/403**
  - slice 测试默认也会走 filters；当 Security 在 classpath 且 filter chain 生效时，可能“还没进 controller 就被拦了”
  - 建议：显式导入想演示的 `SecurityFilterChain`（教学端点隔离），不要全局禁用 filters（否则会学到错误结论）
  - 对照证据：`BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`（401/403 时 `handler/resolvedException` 为 `null`）

## 404/路由相关

- 路由拼错：确认类级 `@RequestMapping` 与方法级 `@GetMapping/@PostMapping` 的组合。
- path variable 名称不匹配：`@PathVariable("id") Long id`

## 500/异常相关

- 先确认异常来自哪个阶段：resolver/binder/converter/controller，不要一上来就 try/catch。
- 建议先用 `resolvedException` 或日志把异常类型固定，再决定是加 `@ExceptionHandler` 还是修输入约束。

## 建议的排查顺序

1. 先用测试把“现象”固化（状态码 + 响应形状）
2. 再回到 controller / handler / advice 看“为什么”
3. 需要更接近真实链路时，再用 `@SpringBootTest(webEnvironment=RANDOM_PORT)`

## 对应 Lab（可运行）

- `BootWebMvcLabTest`
- `BootWebMvcSpringBootLabTest`
- `BootWebMvcTestingDebuggingLabTest`
- `BootWebMvcSecurityLabTest`
- `BootWebMvcObservabilityLabTest`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcRealWorldHttpLabTest`
- Lab：`BootWebMvcAsyncSseLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`
- Lab：`BootWebMvcAdviceOrderLabTest`
- Lab：`BootWebMvcAdviceMatchingLabTest`
- Lab：`BootWebMvcSecurityLabTest`
- Lab：`BootWebMvcObservabilityLabTest`

上一章：[02. Observability（Interceptor 计时 vs Actuator 指标）](testing-observability-observability-and-metrics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99 自检：Spring Boot Web MVC](appendix-self-check.md)
<!-- BOOKIFY:END -->
