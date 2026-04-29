# 01. 401 vs 403：Basic Auth 与授权规则
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：401 vs 403：Basic Auth 与授权规则展开，主线可以概括为：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。

    阅读时可以先跑 `BootSecurityLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。

    需要下探源码时，可以从 `org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Security](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. CSRF：为什么 GET 没事但 POST 会 403？](security-csrf.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityLabTest`
    - 测试文件：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

## 应当观察到的现象

- 访问需要登录的资源（例如 `/api/secure/ping`）：
  - 未登录 → **401**（`unauthorized`）
- 访问需要更高权限的资源（例如 `/api/admin/ping`）：
  - 已登录但权限不足 → **403**（`forbidden`）

## 机制解释（Why）

可以把 Security 的判断分成两步：

1. **是谁**（Authentication）：有没有成功登录？当前 `Authentication` 是不是匿名？
2. **能做什么**（Authorization）：是否具备访问该资源所需的 role/authority？

在本模块里：

- Basic Auth 用户在 `SecurityConfig#userDetailsService` 中定义（`user/password`、`admin/password`）
- `/api/admin/**` 需要 `ROLE_ADMIN`（见 `SecurityConfig#apiChain`）

- 先看响应体 `message/status/path`（本模块统一返回 JSON 错误结构），再去看 `SecurityConfig` 的规则。

## 最小可运行实验（Lab）

- Lab：`BootSecurityLabTest`
- 运行命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）


本章的目标是把“为什么有时是 401、有时是 403”讲清楚，并用可运行测试把结论固化下来。

## 常见坑与边界

### 坑点 1：把 401/403 当成同一种失败，导致排障走错方向

接口访问失败时只盯着“账号密码/权限配置”某一处反复试错

401 与 403 分别对应不同分流：

- 401：Authentication 没建立（匿名/认证失败）
- 403：Authentication 已建立，但 Authorization 不通过（权限不足/CSRF 等）

- 401：`BootSecurityLabTest#secureEndpointReturns401WhenAnonymous`
- 403：`BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`

先根据响应码分流（401→认证；403→鉴权/CSRF），再回到 `SecurityConfig` 对齐规则

### 坑点 2：`hasRole("ADMIN")` 不是 `authorities("ADMIN")`（ROLE_ 前缀边界）

以为“已经授予 ADMIN 权限”，但访问 `/api/admin/**` 仍然 403

- `hasRole("ADMIN")` 的语义是：需要 `ROLE_ADMIN`
- 仅有 `ADMIN` authority 并不等价于 `ROLE_ADMIN`

`BootSecurityLabTest#adminEndpointReturns403WhenAuthorityAdminButMissingRolePrefix_asPitfall`

- `SecurityConfig#apiChain`（`hasRole("ADMIN")` 规则定义）
- `JsonAccessDeniedHandler#handle`（403 塑形）

在需要 role 语义时给 `ROLE_ADMIN`（或改用 `hasAuthority("ADMIN")` 并统一权限命名）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootSecurityLabTest`
- 测试文件：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[security-csrf.md](security-csrf.md)

<!-- BOOKIFY:END -->
