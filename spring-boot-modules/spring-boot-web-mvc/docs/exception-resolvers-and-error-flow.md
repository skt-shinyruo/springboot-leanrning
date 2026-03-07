# 04. ExceptionResolvers（异常从哪来、又被谁“翻译”成状态码）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：ExceptionResolvers（异常从哪来、又被谁“翻译”成状态码）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcTestingDebuggingLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 错误契约加固（解析失败 vs 校验失败 vs 类型不匹配）](exception-resolvers-error-contract-hardening.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. ProblemDetail vs 自定义错误体（ApiError：契约的两种路线）](exception-resolvers-problemdetail-vs-custom-error.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「04：ExceptionResolvers（异常从哪来、又被谁“翻译”成状态码）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcTestingDebuggingLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcTestingDebuggingLabTest`

## 机制主线（DispatcherServlet 的异常处理段落）

可以把异常处理理解成 DispatcherServlet 主链路的一个固定“尾部阶段”：

1. handler 执行前/中/后任何阶段抛出异常（包括参数解析、绑定、converter 读写、controller 业务）
2. `DispatcherServlet` 捕获异常并进入 `processHandlerException`
3. 逐个尝试 `HandlerExceptionResolver`（通常是一个 composite）
4. 第一个能处理的 resolver 决定：
   - 状态码（400/404/…）
   - body（ApiError/ProblemDetail/默认响应体）
   - content-type（`application/json` / `application/problem+json` 等）

## 常见分支：异常来自哪里？

把“异常类型”与“链路阶段”对应起来，是最快的定位方式：

- **选路阶段（HandlerMapping）**
  - 典型现象：404 / 405
  - 典型入口：`RequestMappingHandlerMapping#getHandlerInternal`

- **入参解析阶段（ArgumentResolver）**
  - 典型现象：400（缺参、类型不匹配）
  - 典型入口：`HandlerMethodArgumentResolverComposite#resolveArgument`

- **绑定与校验（Binder + Validation）**
  - 典型现象：400（BindException / MethodArgumentNotValidException）
  - 典型入口：`DataBinder#bind`、`DataBinder#validate`

- **消息体读写（HttpMessageConverter）**
  - read：415（Content-Type 不支持）/ 400（JSON 解析失败）
  - write：406（Accept 不支持）

- **安全链路（Security FilterChain）**
  - 典型现象：401/403（常发生在 DispatcherServlet 之前）
  - 排障建议：先证明“是否进入了 DispatcherServlet”（`handler/resolvedException` 证据链）再谈 resolver
  - 参考：Security 与 MVC 相对位置（含边界 Lab）：[01-filterchain-security/01-security-filterchain-and-mvc.md](filterchain-security-security-filterchain-and-mvc.md)

## 源码与断点（把“谁翻译的”看清）

建议断点（从外到内）：
- `org.springframework.web.servlet.DispatcherServlet#doDispatch`
- `org.springframework.web.servlet.DispatcherServlet#processHandlerException`
- `org.springframework.web.servlet.handler.HandlerExceptionResolverComposite#resolveException`
- `org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`
- `org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver#doResolveException`
- `org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver#doResolveException`

## 最小可运行实验（Lab）

建议按“先能定位，再谈优化契约”的顺序跑：

- 406/415 的 resolver 证据链：`BootWebMvcTestingDebuggingLabTest`
- binder/校验分支证据链：`BootWebMvcBindingDeepDiveLabTest`
- binder/校验/JSON parse 的 resolvedException 证据链：`BootWebMvcExceptionResolverChainLabTest`
- ProblemDetail 对照：`BootWebMvcProblemDetailLabTest`
- Security（401/403/CSRF）分支：`BootWebMvcSecurityLabTest` / `BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`

## 常见坑与边界

- **坑 1：把 400 全当成校验失败**
  - 400 可能来自：JSON 解析失败 / type mismatch / validation failed
  - 排障建议：先用 `resolvedException` 固定异常类型，再决定是补 `@ExceptionHandler` 还是修输入契约

- **坑 2：@WebMvcTest 忘了导入 ControllerAdvice**
  - slice 测试里，若没有 `@Import(GlobalExceptionHandler/AdvancedApiExceptionHandler)`，看到的错误体可能是默认行为而不是契约

- **坑 3：把 401/403 当成 MVC 的异常处理**
  - 很多安全分支发生在 FilterChain 中：优先从 FilterChainProxy/ExceptionTranslationFilter 入手

## 小结与下一章

- 本章完成后：进入 Part 04，把 406/415 与 Jackson 严格模式结合起来，建立“契约可控”的工程化闭环。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Lab：`BootWebMvcExceptionResolverChainLabTest`
- Lab：`BootWebMvcProblemDetailLabTest`
- Lab：`BootWebMvcAdviceMatchingLabTest`
- Lab：`BootWebMvcAdviceOrderLabTest`
- Lab：`BootWebMvcSecurityLabTest`

上一章：[03. 错误契约加固（解析失败 vs 校验失败 vs 类型不匹配）](exception-resolvers-error-contract-hardening.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. ProblemDetail vs 自定义错误体（ApiError：契约的两种路线）](exception-resolvers-problemdetail-vs-custom-error.md)
<!-- BOOKIFY:END -->
