# 01. Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcSecurityLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 关键分支矩阵（Web MVC Branch Decision Matrix）](../14-testing-observability/04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）](../02-dispatcherservlet/03-webmvc-request-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01：Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcSecurityLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcSecurityLabTest`

## 机制主线（请求从哪进、在哪拦）

请求进入顺序（从外到内）：

1. **Servlet 容器 Filter 链**
   - Spring Security：`DelegatingFilterProxy` → `FilterChainProxy`
2. **DispatcherServlet**
   - HandlerMapping/HandlerAdapter/ArgumentResolver/Binder/Converter
3. **Controller**
4. **异常解析与响应写回**

因此：当看到 401/403，第一反应应该是“我有没有走到 DispatcherServlet”，而不是“controller 写错了”。

### C.1 如何证明“没进入 DispatcherServlet”（证据链优先）

只看 status code 很容易误判（尤其是 403：权限不足 vs CSRF）。更稳妥的方式是直接拿证据：

在 MockMvc 测试里抓 `MvcResult`：

- `MvcResult#getHandler()`
- `MvcResult#getResolvedException()`

常见判定（经验 → 证据链）：

1. **401/403 且 `handler == null`**
   - 大概率发生在 **Security FilterChain**（DispatcherServlet 之前）
   - 此时再去加 `@ControllerAdvice` 往往是“改不到点上”
2. **400/5xx 且 `handler != null`（且 `resolvedException != null`）**
   - 说明已经进入 **DispatcherServlet / HandlerMethod**，问题更可能在 MVC 的 binder/converter/exception resolver 段落

对应可运行证据链：

- Lab：`BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`
  - 401/403：断言 `handler/resolvedException` 为 `null`
  - 400（binder/validation/converter）：断言 `resolvedException` 是具体异常类型（例如 `BindException`/`MethodArgumentNotValidException`/`HttpMessageNotReadableException`）

## 源码与断点

推荐断点（按常见问题）：

- 是否进入了 Security FilterChain：
  - `org.springframework.web.filter.DelegatingFilterProxy#doFilter`
  - `org.springframework.security.web.FilterChainProxy#doFilterInternal`
- 401（未认证）：
  - `org.springframework.security.web.authentication.www.BasicAuthenticationFilter#doFilterInternal`
  - `org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler`
- 403（权限不足）：
  - `org.springframework.security.authorization.AuthorizationManager`（实现/调用点）
  - `org.springframework.security.web.access.ExceptionTranslationFilter`
- 403（CSRF）：
  - `org.springframework.security.web.csrf.CsrfFilter#doFilterInternal`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcSecurityLabTest`
  - （边界证据链增强）`BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`
  - 401：未认证访问 `/api/advanced/secure/ping`
  - 403：普通用户访问 `/api/advanced/secure/admin/ping`
  - 403（CSRF）：认证后 POST `/api/advanced/secure/update` 不带 token

对应源码：
- `SecurityConfig`：`spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/SecurityConfig.java`
- `SecureDemoController`：`spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/SecureDemoController.java`

## 常见坑与边界

- **引入 security 依赖后，slice 测试（@WebMvcTest）默认也会受到安全过滤器影响**：要么显式导入 `SecurityFilterChain`（本模块示例），要么在特定测试里关闭 filters（不推荐默认关闭）。
- **CSRF 的“误伤”**：如果没有刻意控制 CSRF，原本正常的 POST 会突然 403。真实工程里通常对纯 API 关闭 CSRF，但教学场景可以保留一小段端点用于演示分支。

## 小结与下一章

- 下一章进入 Observability：用 Interceptor 与 Actuator 指标把“请求耗时/请求量”变成可观察事实。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcSecurityLabTest`

上一章：[04. 关键分支矩阵（Web MVC Branch Decision Matrix）](../14-testing-observability/04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）](../02-dispatcherservlet/03-webmvc-request-call-chain.md)
<!-- BOOKIFY:END -->
