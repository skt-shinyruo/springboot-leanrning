# 第 81 章：01：Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebMvcSecurityLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 80 章：01：WebMvc 测试与排障（resolvedException / handler / 断点清单）](../part-07-testing-debugging/080-01-webmvc-testing-and-troubleshooting.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 82 章：90：常见坑清单（Web MVC）](../appendix/082-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）**
- 目标：把“加了 Spring Security 之后怎么就 401/403 了”变成可解释、可复现、可排障的调用链问题。

!!! summary "本章要点"

    - Spring Security 的主要入口是 **Servlet Filter 链**（FilterChainProxy），它发生在 **DispatcherServlet 之前**。
    - 401/403 往往不是 controller 的问题：
      - **401**：未认证（Authentication 不存在/失败）
      - **403**：已认证但不允许（权限不足）或 **CSRF 缺失**（常见于 POST/PUT/DELETE）
    - 工程落地建议：把“教学安全端点”与“既有教学主线端点”隔离，避免影响现有 Labs（本模块采用只保护 `/api/advanced/secure/**` 的策略）。

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

因此：当你看到 401/403，第一反应应该是“我有没有走到 DispatcherServlet”，而不是“controller 写错了”。

### C.1 如何证明“没进入 DispatcherServlet”（证据链优先）

只看 status code 很容易误判（尤其是 403：权限不足 vs CSRF）。更稳妥的方式是直接拿证据：

在 MockMvc 测试里抓 `MvcResult`：

- `MvcResult#getHandler()`
- `MvcResult#getResolvedException()`

常见判定（经验 → 证据链）：

1. **401/403 且 `handler == null`**
   - 大概率发生在 **Security FilterChain**（DispatcherServlet 之前）
   - 此时你再去加 `@ControllerAdvice` 往往是“改不到点上”
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

- **引入 security 依赖后，slice 测试（@WebMvcTest）默认也会受到安全过滤器影响**：要么显式导入你的 `SecurityFilterChain`（本模块示例），要么在特定测试里关闭 filters（不推荐默认关闭）。
- **CSRF 的“误伤”**：如果你没有刻意控制 CSRF，原本正常的 POST 会突然 403。真实工程里通常对纯 API 关闭 CSRF，但教学场景可以保留一小段端点用于演示分支。

## 小结与下一章

- 下一章进入 Observability：用 Interceptor 与 Actuator 指标把“请求耗时/请求量”变成可观察事实。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcSecurityLabTest` / `BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`

上一章：[01](../part-07-testing-debugging/080-01-webmvc-testing-and-troubleshooting.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[066-02-breakpoint-map.md](../part-00-guide/066-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
