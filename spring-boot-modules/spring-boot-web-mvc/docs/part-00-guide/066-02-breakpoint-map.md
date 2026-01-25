# 第 66 章：02：断点地图（Part 01 Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Part 01 Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 65 章：01：知识地图（Web MVC Deep Dive Map）](065-01-knowledge-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 67 章：01：DispatcherServlet 主链路（把选路/参数解析/返回值/异常串起来）](../part-03-web-mvc-internals/067-01-dispatcherservlet-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章目标：把 Web MVC Part 01 的关键机制，收敛成一份“可复制粘贴到 IDE 断点列表”的断点地图。
- 使用方式：先跑一次“最小入口”，再按本页的断点清单逐段观察（不要一上来就全局搜日志）。

!!! summary "本章要点"

    - Web MVC 排障的第一原则：**先确认分支发生在哪一段（Filter / mapping / argument / binder / converter / resolver），再决定去哪下断点**。
    - 400/404/405/406/415 这类“看起来像 controller 的问题”，大量其实发生在 controller 之前/之后。
    - 最省时间的证据链：**测试断言 → resolvedException（异常类型）→ 断点（分支发生点）→ 观察字段（关键数据结构）**。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest`

## 机制主线（按请求阶段分段）

> 本页以“阶段”为主索引；每个阶段给出：入口断点、观察点、决定性分支（Decisive Branch）。

### C1. Filter（Servlet 最外层）

- 入口断点：
  - `jakarta.servlet.FilterChain#doFilter`
  - `org.springframework.web.filter.OncePerRequestFilter#doFilter`
- 观察点（Watch List）：
  - `request.getDispatcherType()`（REQUEST / ERROR / ASYNC）
  - `request.getRequestURI()` / `request.getMethod()`
- 决定性分支：
  - 是否进入 DispatcherServlet（如果没进，优先怀疑 FilterChain/Security）

### C2. DispatcherServlet：主分发入口

- 入口断点：
  - `org.springframework.web.servlet.DispatcherServlet#doDispatch`
- 观察点（Watch List）：
  - `mappedHandler`（是否为 null）
  - `handler` / `handlerAdapter`
- 决定性分支：
  - `mappedHandler == null`：典型对应 404（无 handler）

### C3. HandlerMapping：选路（404/405 很多在这里开端）

- 入口断点：
  - `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#getHandlerInternal`
- 观察点（Watch List）：
  - `bestMatch`/`bestMatchHandler`（具体字段名随版本可能不同，以 IDE 提示为准）
  - `request.getMethod()` 与 mapping 的 methods/consumes/produces
- 决定性分支：
  - “路径匹配成功但方法不支持”→ 405（通常后续由 `DefaultHandlerExceptionResolver` 翻译）

### C4. Argument Resolver：入参从哪来

- 入口断点：
  - `org.springframework.web.method.support.HandlerMethodArgumentResolverComposite#resolveArgument`
- 观察点（Watch List）：
  - `parameter`（当前正在解析的参数）
  - `resolver`（命中哪个 resolver）
- 决定性分支：
  - 缺参/缺 header/类型不匹配 → 400（通常是 resolver 抛异常）

### C5. 两条绑定通道：@RequestBody vs @ModelAttribute（最重要的分岔点）

#### body 通道：`@RequestBody` / HttpMessageConverter（读）

- 入口断点：
  - `org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor#resolveArgument`
  - `org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
- 观察点（Watch List）：
  - `contentType` / `messageConverters`
- 决定性分支：
  - 找不到可读 converter → 415
  - JSON 解析失败 → `HttpMessageNotReadableException`（常见 400）

#### binder 通道：`@ModelAttribute` / WebDataBinder（绑 + 校验）

- 入口断点：
  - `org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor#resolveArgument`
  - `org.springframework.web.bind.WebDataBinder#bind`
  - `org.springframework.validation.DataBinder#validate`
- 观察点（Watch List）：
  - `binder.getBindingResult()`（errors / suppressedFields）
  - `bindingResult.getAllErrors()`
- 决定性分支：
  - `@InitBinder` allowed/disallowed fields 是否触发 suppressedFields

### C6. Validation：@Valid / 方法级校验

- 入口断点（对象校验）：
  - `org.springframework.validation.beanvalidation.SpringValidatorAdapter#validate`
- 入口断点（方法级校验，视版本而定）：
  - 先从异常类型倒推（见 E 的 resolvedException 证据链），再在 IDE 里跳到对应 handler/method validator
- 决定性分支：
  - 没写 `@Valid`：不会触发对象校验
  - 有 `BindingResult`：可能不会抛异常（由 controller 自己决定如何塑形）

### C7. Interceptor：更靠近 handler 的增强点

- 入口断点：
  - `org.springframework.web.servlet.HandlerExecutionChain#applyPreHandle`
  - `org.springframework.web.servlet.HandlerExecutionChain#applyPostHandle`
  - `org.springframework.web.servlet.HandlerExecutionChain#triggerAfterCompletion`
  - `org.springframework.web.servlet.AsyncHandlerInterceptor#afterConcurrentHandlingStarted`
- 观察点（Watch List）：
  - 当前 dispatch 类型（REQUEST/ASYNC）
  - 事件序列（建议用本模块的 trace Lab 作为可断言证据）
- 决定性分支：
  - async 第一次 dispatch：不会触发 postHandle/afterCompletion（而是 afterConcurrentHandlingStarted）

### C8. 写回响应：ReturnValueHandler / HttpMessageConverter（写）

- 入口断点：
  - `org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor#writeWithMessageConverters`
- 观察点（Watch List）：
  - `selectedMediaType`（406/不匹配）
  - `converterType`
- 决定性分支：
  - 找不到可写 converter / Accept 不支持 → 406

### C9. ExceptionResolvers：异常如何变成状态码/错误体

- 入口断点：
  - `org.springframework.web.servlet.DispatcherServlet#processHandlerException`
  - `org.springframework.web.servlet.handler.HandlerExceptionResolverComposite#resolveException`
  - `org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`
  - `org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver#doResolveException`
  - `org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver#doResolveException`
- 观察点（Watch List）：
  - `ex`（异常类型，最关键）
  - `handler`（是否为 null；404 时可能没有 handler）
- 决定性分支：
  - `@ControllerAdvice` 是否适用（matching + order）

## 源码与断点（建议从 Lab 反推）

本页不是“所有断点的百科”，而是 Part 01 的最小集合；更完整的调用链解释见：

- DispatcherServlet 主链路：`part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md`
- resolver/binder：`part-03-web-mvc-internals/02-argument-resolver-and-binder.md`
- converter：`part-03-web-mvc-internals/03-message-converters-and-return-values.md`
- resolvers：`part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md`

## 最小可运行实验（Lab）

建议先跑这些入口再下断点：

- 基础主线（最快）：`BootWebMvcLabTest#pingEndpointReturnsPong`
- 400 根因分类（resolvedException）：`BootWebMvcExceptionResolverChainLabTest`（3 个方法对照）
- binder 深挖（InitBinder/suppressedFields）：`BootWebMvcBindingDeepDiveLabTest`
- Filter/Interceptor 顺序与 async：`BootWebMvcTraceLabTest`

## 常见坑与边界

- 一上来就怀疑 controller：建议先证明“是否进入了 DispatcherServlet（doDispatch）”。
- 只看状态码不看异常：建议用 `MvcResult#getResolvedException()` 固定异常类型。
- async 忽略二次 dispatch：建议用 trace Lab 的事件序列辅助定位。

## 小结与下一章

- 本页作为 Part 01 的“断点索引页”，建议与 Part 01 各章的 Debug 建议配合使用。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest`
- Lab：`BootWebMvcExceptionResolverChainLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Lab：`BootWebMvcTraceLabTest`

上一章：[part-00-guide/01-knowledge-map.md](065-01-knowledge-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-mvc/01-validation-and-error-shaping.md](../part-01-web-mvc/071-01-validation-and-error-shaping.md)

<!-- BOOKIFY:END -->
