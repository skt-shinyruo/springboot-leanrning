# 02. CSRF：为什么 GET 没事但 POST 会 403？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕02：CSRF：为什么 GET 没事但 POST 会 403？展开，主线可以概括为：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。

    阅读时可以先跑 `BootSecurityLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。

    需要下探源码时，可以从 `org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 401 vs 403：Basic Auth 与授权规则](01-basic-auth-and-authorization.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. Method Security 与代理：self-invocation 陷阱](03-method-security-and-proxy.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityLabTest`
    - Test file：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

## 应当观察到的现象

- 对 `/api/secure/change-email` 发起 POST：
  - 即使 Basic Auth 已登录，如果没有 CSRF token → **403**（本模块返回 `csrf_failed`）
  - 在测试中显式加上 `.with(csrf())` 后 → **200**

## 机制解释（Why）

CSRF 的核心点不是“有没有登录”，而是：

- 当请求会改变服务器状态（POST/PUT/DELETE 等），Spring Security 默认会要求一个“来自可信页面/会话”的 token。
- 这个 token 在浏览器场景通常由表单/页面自动携带；但在 API 场景/测试场景需要显式带上。

- **有认证 ≠ 允许所有写操作**

## 最小可运行实验（Lab）

- Lab：`BootSecurityLabTest`
- 建议命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）


本章通过一个最小 POST 接口复现 CSRF 现象，并解释：为什么“明明已经登录了”，POST 还是会 403。

## Debug 建议

- 优先在 tests 里复现：`MockMvc` + `csrf()` 比 curl 更可控。
- 想更进一步：把 missing/invalid CSRF 的 message 拆细（Exercise 有引导）。

## 常见坑与边界

### 坑点 1：为“修复 403”而全局关闭 CSRF，反而把安全边界打穿

在 API 测试/本地调试里遇到 POST 403，于是直接禁用 CSRF，问题“消失”但风险扩大

- CSRF 是针对“有状态（cookie/session）”的威胁模型；这类请求默认需要 token
- JWT 无状态 API 通常不需要 CSRF（或按路径分流），但这不等于所有链路都该关闭

- Basic 链路：缺 token 会 403：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated`
- 加 token 才通过：`BootSecurityLabTest#csrfTokenAllowsPostWhenAuthenticated`
- JWT 链路：POST 不需要 CSRF：`BootSecurityLabTest#jwtPostDoesNotRequireCsrf`

按链路分流（有状态链路保留 CSRF；无状态链路按需关闭），不要“一刀切”

### 坑点 2：我“禁用了 CSRF”，但 POST 还是 403（原因：请求命中了另一条 SecurityFilterChain）

在配置里写了 `csrf.disable()`，但 POST 仍然返回 `csrf_failed`

- CSRF 是否生效，不取决于“有没有写 disable”，而取决于**请求最终命中哪条 `SecurityFilterChain`**
- 一旦命中的是 Basic 链路（默认 CSRF 开启），就会走 `CsrfFilter`

三段证据链闭环

- Basic 链路缺 token → 403：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated`
- JWT 链路 POST 不需要 CSRF → 200：`BootSecurityLabTest#jwtPostDoesNotRequireCsrf`
- 用过滤器列表证明“命中哪条链”：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`

- `org.springframework.security.web.FilterChainProxy#doFilterInternal`（选择 chain）
- `org.springframework.security.web.DefaultSecurityFilterChain#matches`（匹配判定）
- `org.springframework.security.web.csrf.CsrfFilter#doFilterInternal`（CSRF 拦截点）

让 matcher 覆盖范围互斥、顺序明确（@Order），并用默认 Lab 把“命中链路”固定成回归断言

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityLabTest`
- Test file：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

上一章：[part-01-security/01-basic-auth-and-authorization.md](01-basic-auth-and-authorization.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-security/03-method-security-and-proxy.md](03-method-security-and-proxy.md)

<!-- BOOKIFY:END -->
