# 04. FilterChain：多链路 + 顺序 + 自定义 Filter
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：FilterChain：多链路 + 顺序 + 自定义 Filter展开，主线可以概括为：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。

    阅读时可以先跑 `BootSecurityLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。

    需要下探源码时，可以从 `org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. Method Security 与代理：self-invocation 陷阱](03-method-security-and-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. JWT/Stateless：Bearer token + scope（最小闭环）](05-jwt-stateless.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityLabTest`
    - Test file：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

## 机制主线

本章关注两个问题：

1) 为什么一个应用里可以有多个 `SecurityFilterChain`？
2) 自定义 Filter 应该插在哪、如何用测试证明它真的执行了？

## 应当观察到的现象

- 即使请求被拒绝（401/403），响应依然带有 `X-Trace-Id`。
- `/api/jwt/**` 与 `/api/**` 走的是不同的 filter chain（本模块用路径分流 + `@Order` 固定优先级）。

## 机制解释（Why）

- Spring Security 支持多个 `SecurityFilterChain`：
  - 每个 chain 有自己的 matcher（例如 `securityMatcher("/api/jwt/**")`）
  - 通过 `@Order`（或内部排序）决定优先级：先匹配/先应用
- 自定义 Filter 的位置通常通过：
  - `addFilterBefore(filter, SomeFilter.class)`
  - `addFilterAfter(filter, SomeFilter.class)`
固定下来。

## 最小可运行实验（Lab）

- Lab：`BootSecurityLabTest`
- 建议命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）


## Debug 建议

- 用断点观察：请求进来时到底命中了哪个 chain（尤其是多个 matcher 的场景）。

## 常见坑与边界

### 坑点 1：多个 `SecurityFilterChain` 匹配与顺序错误，导致“命中错链路”

- 以为 `/api/jwt/**` 走的是 JWT chain，结果行为像 Basic chain（或反过来）
- 例如：JWT POST 本应不需要 CSRF，却被 CSRF 拦下；或某些 header/filter 只在部分响应出现

- 多个 chain 的优先级由 matcher 匹配与顺序共同决定；顺序错误时请求可能先命中一个更“宽”的 chain

从行为侧锁定“到底命中了哪条链路”

- TraceId filter 即使在 401 上也会写 header：`BootSecurityLabTest#traceIdHeaderIsAddedEvenOnUnauthorizedResponses`
- Basic 链路受 CSRF 影响：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated`
- JWT 链路不需要 CSRF：`BootSecurityLabTest#jwtPostDoesNotRequireCsrf`

为不同路径建立清晰 matcher，并用 `@Order` 固定优先级；用测试断言把链路行为锁定（避免“改配置后悄悄命中错链”）

### 坑点 2：只靠“响应码/行为”判断命中哪条链路，容易误判（推荐用 Filter 列表做证据链）

看到某个路径返回 401/403/CSRF，就主观判断“它一定命中了某条链”，结果排障方向全错

行为是“链路整体结果”，很容易被多个因素影响；而 `FilterChainProxy#getFilters(request)` 能直接给出“命中了哪条链的 filters”，证据更硬

`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`

- `org.springframework.security.web.FilterChainProxy#doFilterInternal`
- `org.springframework.security.web.DefaultSecurityFilterChain#matches`

先用默认 Lab 证明“请求命中了哪条链”，再去讨论 CSRF/认证/鉴权的细节（把排障从猜测变成证据链）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityLabTest`
- Test file：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

上一章：[part-01-security/03-method-security-and-proxy.md](03-method-security-and-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-security/05-jwt-stateless.md](05-jwt-stateless.md)

<!-- BOOKIFY:END -->
