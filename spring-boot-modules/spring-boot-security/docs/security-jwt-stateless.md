# 05. JWT/Stateless：Bearer token + scope（最小闭环）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕05：JWT/Stateless：Bearer token + scope（最小闭环）展开，主线可以概括为：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。

    阅读时可以先跑 `BootSecurityLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。

    需要下探源码时，可以从 `org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. FilterChain：多链路 + 顺序 + 自定义 Filter](security-filter-chain-and-order.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 常见坑清单（Security）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityLabTest`
    - 测试文件：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

## 机制主线

对应代码：

- `spring-boot-modules/spring-boot-security/src/main/java/com/learning/springboot/bootsecurity/part01_security/SecurityConfig.java`
- `spring-boot-modules/spring-boot-security/src/main/java/com/learning/springboot/bootsecurity/part01_security/JwtTokenService.java`

## 应当观察到的现象

- `/api/jwt/secure/ping`：
  - 不带 token → 401
  - 带 `Authorization: Bearer <token>` → 200，且能看到 `subject`
- `/api/jwt/admin/ping`：
  - token 没有 `admin` scope → 403
  - token 有 `admin` scope → 200
- JWT 链路默认禁用 CSRF：
  - POST 在带 token 的情况下无需额外 CSRF token

## 机制解释（Why）

### 1) “Stateless”的关键点

- 不依赖 session 保存登录态
- 每次请求都携带凭证（Bearer token）

### 2) “scope → 权限”的映射

Spring Security 默认会把 JWT 的 `scope`（空格分隔）映射成 `SCOPE_xxx` 的 authority。

因此：

- token scope = `admin`
- 对应 authority = `SCOPE_admin`
- 鉴权规则可以写：`hasAuthority("SCOPE_admin")`

## 本地手动体验（可选）

默认 `spring-boot:run` 只演示 Basic Auth；如果想手动拿 token 体验 JWT 链路：

1. 启动 dev profile（启用 token 发行端点）：

2. 获取 token（scope=admin）：

```bash
curl 'http://localhost:8085/api/jwt/dev/token?subject=alice&scope=admin'
```

3. 访问 admin endpoint：

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8085/api/jwt/admin/ping
```

## 最小可运行实验（Lab）

- Lab：`BootSecurityLabTest`
- 运行命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）


本章的目标是：在不依赖外部 IdP 的情况下，用最小示例理解 JWT/Stateless 的工作方式，并通过 tests 固化结论。

## 实验入口

- `spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`
  - `jwtSecureEndpointReturns401WhenMissingBearerToken`
  - `jwtSecureEndpointIsAccessibleWithBearerToken`
  - `jwtAdminEndpointReturns403WhenScopeMissing`
  - `jwtAdminEndpointIsAccessibleWhenAdminScopePresent`
  - `jwtPostDoesNotRequireCsrf`

```bash
mvn -pl :spring-boot-security spring-boot:run -Dspring-boot.run.profiles=dev
```

## 常见坑与边界

### 坑点 1：Authorization 头里没有 `Bearer ` 前缀，结果永远是 401

确认带了 token，但接口仍然返回 401（尤其是把 `Authorization: <token>` 直接塞进去时）。

默认的 Bearer Token 解析器只认 `Authorization: Bearer <token>`；前缀不对就解析不到 token，最终认证上下文为空。

`BootSecurityLabTest#jwtSecureEndpointReturns401WhenBearerPrefixMissing_asPitfall`

`BearerTokenAuthenticationFilter#doFilterInternal`、`DefaultBearerTokenResolver#resolve`

统一使用 `Authorization: Bearer <token>`；如必须兼容非标准格式，显式配置 `BearerTokenResolver`（并在文档/测试中固化约定）。

### 坑点 2：token 带了 scope 但授权仍然 403（scope/authority 前缀不一致）

JWT 认证通过（不再 401），但访问需要权限的接口返回 403。

Spring Security 对 scope 的默认映射通常会带 `SCOPE_` 前缀；如果在规则里写 `hasRole("ADMIN")`/`ROLE_`，或 scope 名称与规则不一致，就会被拒绝。

`BootSecurityLabTest#jwtAdminEndpointReturns403WhenScopeMissing`

`JwtAuthenticationProvider#authenticate`、`JwtGrantedAuthoritiesConverter#convert`、`AuthorizationFilter#doFilter`

对齐“token 里提供什么 → 代码里用什么做授权”的映射（例如统一使用 `hasAuthority("SCOPE_admin")` 或调整 converter）。

> 注意：`/api/jwt/dev/token` 仅用于学习（dev profile），不是生产做法。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootSecurityLabTest`
- 测试文件：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

上一章：[security-filter-chain-and-order.md](security-filter-chain-and-order.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
