# 01. DispatcherServlet 主链路（把选路/参数解析/返回值/异常串起来）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：DispatcherServlet 主链路（把选路/参数解析/返回值/异常串起来）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcInternalsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）](dispatcherservlet-webmvc-request-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. HandlerMapping：路由、404/405 与 mapping 约束](handlermapping-routing.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 目标：建立“请求在 MVC 内部如何被分派”的可观察心智模型：从 `@RequestMapping` 找到 handler，到参数解析、返回值写回、异常映射。
- 基线版本：Spring Framework `6.2.15`（本仓库由 Spring Boot `3.5.9` 管理依赖版本）。本章提到的方法名以该版本为准。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcInternalsLabTest`

## 机制主线

（本章以 `BootWebMvcInternalsLabTest` 为证据链：用 `@ClientIp` + 自定义 `HandlerMethodArgumentResolver` 让“参数解析阶段”可被稳定断言。）

## 主线伪代码（应能“顺着念出来”）

无需背源码细节，但要能把主链路用伪代码复述出来（这样才知道断点该打在哪里）：

1. `DispatcherServlet#doDispatch`
   - `checkMultipart(request)` → multipart 分支（是否需要包装 request）
   - `getHandler(request)` → **HandlerMapping**（选路，得到 `HandlerExecutionChain`）
   - `getHandlerAdapter(handler)` → **HandlerAdapter**（决定“怎么调用 handler”）
   - `applyPreHandle(...)` → Interceptor 的 `preHandle` 链（可能短路）
   - `adapter.handle(...)` → 进入 handler 调用（ArgumentResolver/HandlerMethod）
   - `WebAsyncManager#isConcurrentHandlingStarted` → async 分支（是否进入“异步接管”）
   - `applyPostHandle(...)` → Interceptor 的 `postHandle`
   - `processDispatchResult(...)` → 写回响应（View/MessageConverter）或处理异常（ExceptionResolvers）
   - `triggerAfterCompletion(...)` → Interceptor 的 `afterCompletion`（收尾，保证执行）

2. handler 调用（以 `@RequestMapping` 方法为例）
   - `HandlerMethodArgumentResolverComposite`：解析每个方法参数
   - `WebDataBinder`：对 `@RequestParam/@ModelAttribute` 做绑定与校验
   - `HttpMessageConverter`：对 `@RequestBody/@ResponseBody` 做读写

3. 异常处理
   - 任一阶段抛异常 → 进入 `processHandlerException` → 交给 `HandlerExceptionResolver` 链翻译成状态码/响应体

## 关键对象：在调试时“手里拿着什么”

- `HandlerExecutionChain`：handler + interceptors（能解释“为什么 preHandle 没执行/执行了两次”）
- `HandlerMethod`： controller 方法的封装（参数、注解、返回值信息都在这里）
- `RequestMappingHandlerAdapter`：最常见的 adapter（负责把 `HandlerMethod` 变成“可调用”）

## 关键分支 1：`HandlerAdapter` 决定“到底在调试哪套调用模型”

在 `DispatcherServlet#getHandlerAdapter(handler)` 这一步，Spring MVC 会把“handler 的形态”映射到“如何调用它”的策略上。

最常见的 handler 形态有三类：

1. **`HandlerMethod`**（`@RequestMapping` 系列）：对应 `RequestMappingHandlerAdapter`
2. **`HttpRequestHandler`**（更底层的 handler 接口）：对应 `HttpRequestHandlerAdapter`
3. **旧式 `Controller`**（历史兼容）：对应 `SimpleControllerHandlerAdapter`

这一步的价值在于：当看到“请求为什么与 `RequestMappingHandlerAdapter` 没关系”，很可能是 handler 根本不是 `HandlerMethod`。

本模块的主线默认都落在 `RequestMappingHandlerAdapter` 上，对应证据链：

- `BootWebMvcInternalsLabTest`（自定义 `HandlerMethodArgumentResolver`）

## 关键分支 2：`processDispatchResult` 为什么既能写回响应，又能“翻译异常”

很多人第一次读调用链会把“写回响应”和“异常处理”拆成两段理解，但在 `DispatcherServlet` 里它们是同一个收尾阶段：

- handler 执行阶段可能得到 `ModelAndView`（视图渲染路径）
- 也可能直接写回响应体（`@ResponseBody` 路径，通常 `ModelAndView` 为 `null`）
- 也可能抛出异常（交给 `HandlerExceptionResolver` 链）

把它压缩成伪代码会更容易抓住分支：

```text
doDispatch():
  processedRequest = checkMultipart(request)
  mappedHandler = getHandler(processedRequest)     // HandlerExecutionChain
  ha = getHandlerAdapter(mappedHandler.handler)

  if (!mappedHandler.applyPreHandle()) return

  try:
    mv = ha.handle(processedRequest, response, handler)
  catch (ex):
    dispatchException = ex

  if (asyncManager.isConcurrentHandlingStarted()) return

  mappedHandler.applyPostHandle(...)
  processDispatchResult(..., mv, dispatchException)

processDispatchResult(..., mv, ex):
  if (ex != null):
    mv = processHandlerException(..., ex)          // ExceptionResolvers
  if (mv != null && !mv.wasCleared()):
    render(mv)                                     // ViewResolver/View
  triggerAfterCompletion(...)
```

### 一个非常关键的“定位提示”

当遇到“为什么最终返回的是某个 JSON 错误体 / 为什么是某个状态码”，需要问的不是“controller 返回了什么”，而是：

- **异常有没有被 resolver 处理？**
  - 有：应当去看 `processHandlerException` / `HandlerExceptionResolverComposite`
  - 没有：异常会继续抛出，最终可能走到容器错误页或 Spring Boot 的 error 机制（这时需要从 Web 服务器/错误页链路定位）

### 2.1 把链路补完整：FilterChain → DispatcherServlet → ExceptionResolvers → Spring Boot error

先说结论：**MVC 的 resolver 链不是“全局兜底”**。它能处理的是“进入了 DispatcherServlet 之后、且发生在它能 catch 的窗口内”的异常；一旦异常越过了 `DispatcherServlet`（或根本没进入它），就会进入 Servlet 容器/Boot 的 error 机制。

为了把这件事讲清楚，本章从容器视角把链路补成一条完整叙事：

#### 第 0 段：Servlet 容器先跑 FilterChain（DispatcherServlet 只是 chain 里的一个 Servlet）

真正的“第一现场”是：

- `Filter#doFilter` / `FilterChain#doFilter`（Servlet 容器层）
- 最终才会走到某个 Servlet 的 `service(...)`（在 Boot + MVC 场景里通常是 `DispatcherServlet`）

这意味着一个排障分叉点：

- 异常发生在 **FilterChain**：MVC 的 `HandlerExceptionResolver` 根本没机会处理（因为还没到 `DispatcherServlet`）
- 异常发生在 **DispatcherServlet** 内部：才可能进入 resolver 链

本模块用 `WebMvcTraceFilter` 把“Filter 在 MVC 之外”固化成证据：Filter 的 before/after 在 Interceptor 之前出现（见 `BootWebMvcTraceLabTest`）。

#### 第 1 段：进入 DispatcherServlet（doDispatch）后，异常先尝试被 resolver 收敛

当异常发生在 handler 调用（或渲染阶段），`DispatcherServlet` 会走到：

- `processDispatchResult(..., ex)` → `processHandlerException(..., ex)`
- `processHandlerException` 内部再把异常交给 `HandlerExceptionResolver` 链（`HandlerExceptionResolverComposite`）

需要记住 resolver 的“处理语义”：

- resolver **返回非空 `ModelAndView`**：表示“已处理”，后续会按 view/render 路径收尾
- resolver **返回 `null`**：表示“不处理，交给后续 resolver”，链条继续往下走
- 如果 resolver 链跑完仍然没人处理：`DispatcherServlet` 会把异常 **重新抛出**（这就是“resolver 未处理”的关键落点）

这里应当能把“异常有没有被 resolver 处理”落到一个可断言的事实：`MvcResult#getResolvedException()` 是否为 null（见 `BootWebMvcTestingDebuggingLabTest`）。

#### 第 2 段：resolver 没处理（异常越过 DispatcherServlet）→ 进入 Spring Boot error

当异常从 `DispatcherServlet` 继续抛出后，它会回到 Servlet 容器：

1. 容器将本次请求标记为错误（通常是 500），并把异常/状态码写入 request 的标准 error attributes
2. 容器触发一次 **ERROR dispatch**（`DispatcherType.ERROR`），并转发到“错误页入口”
3. 在 Spring Boot 默认配置下，这个入口就是 `/error`，由 `BasicErrorController` 处理

于是会看到一个“表面上像第二次进 MVC”的现象：**同一个请求会再次进入 DispatcherServlet**，但这次是 ERROR dispatch（不是 async 的 ASYNC dispatch）。

Boot error 侧的关键对象（看源码/排障时的抓手）是：

- `org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController`：`/error` 的默认 controller
- `org.springframework.boot.web.servlet.error.DefaultErrorAttributes`：提供错误信息（status/path/message 等），并从 request 的 error attributes 里取数据
- `templates/error/*`：HTML 错误页模板（404/4xx/5xx），由 Boot 的 error view resolver 选择

把“异常 → error”这条链路变成证据，可用两个“不同入口”的实验对照：

1. **未知路由（404）**：没有 handler，本质是“状态码错误”触发 error 机制
   - JSON（API）：`BootWebMvcSpringBootLabTest#unknownRouteFallsBackToSpringBootErrorEndpoint`
   - HTML（页面）：`BootWebMvcViewSpringBootLabTest#unknownRouteReturnsCustom404HtmlPage`
2. **handler 抛异常（500）**：如果的 `@ControllerAdvice`/resolver 兜不住，最终也会回落到 `/error`
   - 章节承接：错误页与 Accept 分支详见 Part 02：[03：错误页（error/*.html）与内容协商（Accept：HTML vs JSON）](boot-error-error-pages-and-content-negotiation.md)

对应证据链：

- `BootWebMvcExceptionResolverChainLabTest`（把异常解析链路固定成可回归断言）
- `BootWebMvcTestingDebuggingLabTest`（用 `resolvedException` 把“异常类型”固定）
- `BootWebMvcSpringBootLabTest`（未知 API 路由：回落到 Boot `/error` JSON）
- `BootWebMvcViewSpringBootLabTest`（未知页面路由：回落到 Boot error view（404 模板））

## 关键分支 3：async 为什么容易让人感觉“同一个请求走了两次”

`doDispatch` 里有一个非常重要的检查点：

- `WebAsyncManager#isConcurrentHandlingStarted()`

一旦 handler 启动了异步（例如 `DeferredResult`/`SseEmitter` 等），当前线程会提前返回，后续会发生一次 **async dispatch**（第二次进入 `DispatcherServlet`）。

因此会观察到：

- Interceptor 生命周期回调分两段出现（sync dispatch / async dispatch）
- 某些断点会命中两次，但“不是重复执行业务”，而是两次 dispatch 阶段不同

本模块对这条分支有专门的可复现证据链（配合断点阅读）：

- `BootWebMvcTraceLabTest`
- 章节：[05：Interceptor 的 async 生命周期（为什么会回调两次）](async-sse-interceptor-async-lifecycle.md)

### 3.1 async 的“两次 dispatch”时间线（REQUEST → ASYNC）

把 async 讲成“时间线”比讲成“概念”更可靠。可以用下面这条时间线在断点里对照：

```text
T0: DispatcherType=REQUEST（第一次进入 doDispatch）
  FilterChain: doFilter(...)
  Interceptor: preHandle[REQUEST]
  Handler: 返回 Callable/DeferredResult（启动异步）
  Interceptor: afterConcurrentHandlingStarted[REQUEST]   // 注意：此时不会走 postHandle/afterCompletion
  FilterChain: finally/after[REQUEST]

T1: DispatcherType=ASYNC（异步结果就绪，第二次 dispatch）
  （很多 Filter 默认不会再跑：OncePerRequestFilter 默认跳过 async dispatch）
  Interceptor: preHandle[ASYNC]
  （通常不会再次执行当前 controller 方法，而是处理“并发结果”并写回）
  Interceptor: postHandle[ASYNC]
  Interceptor: afterCompletion[ASYNC]
```

这条时间线解决两个“初学者必踩坑”：

1. **为什么 postHandle/afterCompletion 没走？**
   因为 async 在 REQUEST dispatch 里启动后，会用 `afterConcurrentHandlingStarted` 替代它们；真正的 afterCompletion 在 ASYNC dispatch 才发生。
2. **为什么 filter 没跑两次？**
   因为大量 Filter 继承自 `OncePerRequestFilter`，默认会跳过 async dispatch（需要时可以在 `shouldNotFilterAsyncDispatch()` 上做显式控制）。

### 3.2 可断言证据链：把“两次 dispatch”变成稳定输出

在本模块里无需靠“猜”来确认两次 dispatch：`BootWebMvcTraceLabTest` 直接断言了事件序列里同时存在：

- `filter:before[REQUEST]` / `filter:after[REQUEST]`（Filter 只在 REQUEST 出现）
- `interceptor:preHandle[REQUEST]` 与 `interceptor:preHandle[ASYNC]`（Interceptor 两次出现）
- `interceptor:afterConcurrentHandlingStarted[REQUEST]`（async 的分水岭）

这就是一个可复用的排障套路：当怀疑某个逻辑“执行了两次/没执行完”，先把 `DispatcherType`（REQUEST/ASYNC）固定，再去判断它属于哪一次 dispatch 的生命周期阶段。

### 3.3 ERROR vs ASYNC：两类“二次 dispatch”不要混为一谈

排障时经常会遇到一句话：

> “同一个请求怎么又进了一次 DispatcherServlet？”

这句话有两种完全不同的成因：

1. **ASYNC dispatch（DispatcherType=ASYNC）**：因为 handler 启动了异步处理（Callable/DeferredResult…），结果就绪后容器触发第二次 dispatch。
2. **ERROR dispatch（DispatcherType=ERROR）**：因为异常越过了 `DispatcherServlet`（或没有 handler 的 404），容器进入错误处理流程并转发到 `/error`。

如果把这两类二次 dispatch 混在一起，很容易出现误判：以为业务执行了两次，实际可能只是“异步收尾”或“错误页/错误响应的二次处理”。

#### 3.3.1 ERROR dispatch 时间线（DispatcherType=ERROR：回落到 `/error`）

下面这条时间线的重点是：**第二次进 MVC 时，handler 已经不是原来的 controller，而是 `/error` 对应的错误处理 handler**。

```text
T0: DispatcherType=REQUEST（第一次进入 doDispatch）
  FilterChain: doFilter(...)
  DispatcherServlet: doDispatch()
    - handler 执行抛异常，且 ExceptionResolvers 没有人处理（返回 null）
    - 或者根本没有 handler（404：getHandler 返回 null）
  DispatcherServlet: 把异常继续抛出 / 或交由容器错误页机制处理

T1: DispatcherType=ERROR（容器触发错误派发，进入 /error）
  （容器会把异常/状态码等写入 request 的 error attributes）
  DispatcherServlet: doDispatch()
    - HandlerMapping 命中 /error
    - 进入 BasicErrorController（Spring Boot 默认）
    - 根据 Accept 选择：HTML error view（templates/error/*）或 JSON 错误体
```

调试时无需背“谁转发谁”，只要把 **DispatcherType** 固定即可：

- 在 `DispatcherServlet#doDispatch` 打条件断点：`request.getDispatcherType().name().equals(\"ERROR\")`
- 再在 `/error` handler 上打断点（默认是 `BasicErrorController`）

对应本仓库的证据链（用不同入口对照 ERROR 机制的输出形态）：

- JSON（API 404）：`BootWebMvcSpringBootLabTest#unknownRouteFallsBackToSpringBootErrorEndpoint`
- HTML（页面 404）：`BootWebMvcViewSpringBootLabTest#unknownRouteReturnsCustom404HtmlPage`

#### 3.3.2 ASYNC vs ERROR 对照表（排障时 10 秒定性）

| 对比项 | ASYNC dispatch | ERROR dispatch |
|---|---|---|
| `DispatcherType` | `ASYNC` | `ERROR` |
| 触发条件 | handler 启动异步（Callable/DeferredResult…） | 异常越过 DispatcherServlet / 404 无 handler / 容器错误页机制触发 |
| “第二次进 MVC”在干什么 | 处理并发结果并写回响应 | 处理 `/error`（错误页/错误 JSON） |
| 会不会再次执行原 controller 方法 | 通常不会（走并发结果） | 不会（执行的是 `/error` handler） |
| 证据链（本仓库） | `BootWebMvcTraceLabTest`（REQUEST/ASYNC 事件序列） | `BootWebMvcSpringBootLabTest` / `BootWebMvcViewSpringBootLabTest`（/error 输出形态） |

### 3.4 分支决策表：现象 → 所在阶段 → 关键方法 → 可断言证据链

把本章主线压缩成“可复用排障套路”，可以直接用下表反推断点与证据链：

| 现象（看到的） | 所在阶段（大概率落点） | 关键方法（断点入口） | 可断言证据链（本仓库） |
|---|---|---|---|
| 异常发生在 FilterChain（比如安全/鉴权/自定义 Filter 抛错），MVC resolver 不生效 | FilterChain（还没到 DispatcherServlet） | `Filter#doFilter` / `OncePerRequestFilter#doFilterInternal` | `BootWebMvcTraceLabTest`（filter 事件在 interceptor 之前） |
| handler 抛异常，但最终返回的不是预期的 JSON/状态码 | `processHandlerException`（resolver 链） | `DispatcherServlet#processHandlerException`<br>`HandlerExceptionResolverComposite#resolveException` | `BootWebMvcExceptionResolverChainLabTest`（固定 resolver 链）<br>`BootWebMvcTestingDebuggingLabTest`（resolvedException 断言） |
| resolver 没处理（异常继续抛出）最终走到了 Boot `/error` | ERROR dispatch（`/error`） | `DispatcherServlet#doDispatch`（条件：ERROR）<br>`BasicErrorController` | `BootWebMvcSpringBootLabTest`（API 404 -> JSON）<br>`BootWebMvcViewSpringBootLabTest`（页面 404 -> error view） |
| async 表面上“走了两次”，Interceptor 回调两段出现 | ASYNC dispatch（REQUEST → ASYNC） | `WebAsyncManager#isConcurrentHandlingStarted`<br>`AsyncHandlerInterceptor#afterConcurrentHandlingStarted` | `BootWebMvcTraceLabTest`（REQUEST/ASYNC 事件序列） |

## 参数解析/绑定/校验：把“进方法”拆成 3 段（合并自原对应章节）

当在工程里遇到“参数进不来/类型转换不对/校验不生效”，先把问题拆成三段再定位：

1. **解析（resolver）**：这个参数从哪里来（header/path/query/body/session…）？
2. **绑定（binder）**：这个值怎么从 String 变成目标类型（converter/formatter）？
3. **校验（validation）**：这个对象是否符合约束（`@Valid` / `@Validated`）？

### resolver vs binder：怎么快速判断“该扩展哪里”

- **这个参数从哪里来？** → 先看 resolver（或现有注解是否选错）
- **这个值怎么变成目标类型？** → 先看 binder/ConversionService（Converter/Formatter）
- **这个对象是否符合约束？** → 先看 validation（校验通常发生在绑定完成之后）

### 常见内置 ArgumentResolver（大概率会遇到）

- `@RequestParam`：`RequestParamMethodArgumentResolver`
- `@PathVariable`：`PathVariableMethodArgumentResolver`
- `@RequestHeader`：`RequestHeaderMethodArgumentResolver`
- `@RequestBody`：`RequestResponseBodyMethodProcessor`（注意它依赖 HttpMessageConverter）
- `HttpServletRequest/HttpServletResponse`：`ServletRequestMethodArgumentResolver`

写自定义 resolver 时，核心原则是：**只解决“来源”问题，不要把“类型转换/校验”硬塞进 resolver**（否则会绕开框架的 binder/validation 机制）。

### 补充：suppressedFields（把“被禁止绑定字段”变成证据）

当用 `@InitBinder#setAllowedFields` 或 `setDisallowedFields` 做“绑定边界”时，除了断言“值没进来”，还把“被阻止绑定的字段名”变成可观察证据：

- Spring 6.2+：`BindingResult#getSuppressedFields()`
- 本模块提供证据链：`POST /api/advanced/binding/mass-assignment-debug` + `BootWebMvcBindingDeepDiveLabTest`

## 源码与断点

断点入口（按优先级）：

- `org.springframework.web.servlet.DispatcherServlet#doDispatch`
- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#getHandlerInternal`
- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#handleInternal`
- `org.springframework.web.method.support.HandlerMethodArgumentResolverComposite#resolveArgument`
- （binder）`org.springframework.web.bind.support.WebDataBinderFactory#createBinder`
- （binder）`org.springframework.validation.DataBinder#bind`
- （binder）`org.springframework.validation.DataBinder#validate`
- （返回值）`org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor#writeWithMessageConverters`
- （异常）`org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcInternalsLabTest`
- Lab：`BootWebMvcLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- 运行命令：`mvn -pl :spring-boot-web-mvc test`

## 常见坑与边界

- 看到行为不一致时，先确认观察的是哪一层：Filter（Servlet 容器） vs Interceptor（MVC handler 链） vs ArgumentResolver（方法参数解析）。
- “注解已添加但没有生效”的第一排查点：是不是落在了错误的扩展点（应该写 resolver 但写成 converter，或反过来）。

## 小结与下一章

- 下一章会把“返回值写回”（MessageConverter）与内容协商连起来，解释 406/415 在链路里发生在哪里。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebMvcInternalsLabTest` / `BootWebMvcLabTest` / `BootWebMvcBindingDeepDiveLabTest`

上一章：[03. 请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）](dispatcherservlet-webmvc-request-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. HandlerMapping：路由、404/405 与 mapping 约束](handlermapping-routing.md)
<!-- BOOKIFY:END -->
