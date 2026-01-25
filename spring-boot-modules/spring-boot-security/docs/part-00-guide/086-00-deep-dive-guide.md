# 第 86 章：00 - Deep Dive Guide（springboot-security）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Deep Dive Guide（springboot-security）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
    - 原理：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
    - 源码入口：`org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter`
    - 推荐 Lab：`BootSecurityDevProfileLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 85 章：主线时间线：Spring Boot Security](085-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 87 章：01：401 vs 403：Basic Auth 与授权规则](../part-01-security/087-01-basic-auth-and-authorization.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00 - Deep Dive Guide（springboot-security）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`

## 机制主线

本模块用两条主线把 Spring Security 的“看起来很玄学”变成可分流、可断言的机制：

1. **HTTP 安全主线**：FilterChain → Authentication → Authorization（401/403 的分流）
2. **方法安全主线**：Method Security 依赖代理（self-invocation 是经典坑）

同时用一个对照组把 **CSRF（有状态）** 与 **JWT（无状态）** 的边界讲清楚：同样是 POST，为什么一个要 CSRF token，一个不需要。

### 1) 时间线：一次请求从进入到被放行/拒绝

以最常见的 API 请求为例：

1. **请求进入 Filter Chain**
   - 由 `FilterChainProxy` 选择匹配的 `SecurityFilterChain`。
2. **认证（Authentication）**
   - Basic：从 header 解析用户名/密码
   - JWT：从 `Authorization: Bearer ...` 解析 token 并构造 Authentication
3. **鉴权（Authorization）**
   - 判断当前身份是否具备访问资源所需的角色/权限
4. **业务处理**
   - 通过鉴权才会进入 Controller/Service
5. **异常/错误塑形**
   - 把 401/403/CSRF 等失败转换成稳定响应结构（便于前端与排障）

### 2) 关键参与者（你应该能“点名 + 说清职责”）

- `SecurityFilterChain` / `FilterChainProxy`：HTTP 安全的执行入口
- Authentication：
  - Basic：`httpBasic()`（测试里用它快速构造认证请求）
  - JWT：`Authorization: Bearer ...`（无状态认证）
- Authorization：角色/权限决定 403（不是 401）
- CSRF：
  - 有状态（session/cookie）场景默认开启，POST/PUT/DELETE 需要 token
  - 无状态（JWT）场景通常关闭（或按路径分流）
- Method Security：
  - `@PreAuthorize` 等注解依赖代理拦截
  - self-invocation 会绕过代理（经典误用）

### 3) 本模块的关键分支（默认可回归 + 可下断点）

> 记忆方式（强烈推荐）：先跑 Evidence（可断言），再用 Call Chain Sketch 把“现象 → 机制 → 断点锚点”串起来。

#### S1. 多 `SecurityFilterChain` 的匹配与顺序（securityMatcher + @Order）

- Evidence：
  - `BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
  - `BootSecurityMultiFilterChainOrderLabTest#traceIdFilterIsPresentInBothChains_asCrossCuttingConcern`
- Call Chain Sketch（最小可复述）：
  1) request → `FilterChainProxy#doFilterInternal`  
  2) 遍历 `SecurityFilterChain` → `DefaultSecurityFilterChain#matches`  
  3) 选择“第一个匹配”的 chain（顺序由 @Order + matcher 覆盖范围共同决定）  
  4) 进入该 chain 的 filters（后续所有认证/鉴权/CSRF 行为都由这条链决定）
- Breakpoints（断点锚点）：
  - repo：`SecurityConfig#jwtApiChain` / `SecurityConfig#apiChain`（规则定义入口）
  - spring：`org.springframework.security.web.FilterChainProxy#doFilterInternal`（分流入口）
  - spring：`org.springframework.security.web.DefaultSecurityFilterChain#matches`（匹配判定）

#### S2. 401 vs 403 分流（AuthenticationEntryPoint vs AccessDeniedHandler）

- Evidence：
  - 401：`BootSecurityLabTest#secureEndpointReturns401WhenAnonymous`
  - 403：`BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`
- Call Chain Sketch：
  1) filter chain 内抛出 `AuthenticationException` / `AccessDeniedException`  
  2) `ExceptionTranslationFilter#doFilter` 捕获并分流  
  3) 未认证 → `AuthenticationEntryPoint#commence`（401）  
  4) 已认证但拒绝 → `AccessDeniedHandler#handle`（403）
- Breakpoints：
  - repo：`JsonAuthenticationEntryPoint#commence`（401 统一错误响应）
  - repo：`JsonAccessDeniedHandler#handle`（403 统一错误响应，含 csrf_failed 分支）
  - spring：`org.springframework.security.web.access.ExceptionTranslationFilter#doFilter`（401/403 分流点）

#### S3. CSRF：Basic 链路默认开启，JWT 链路明确关闭（禁用只对“命中到的链”生效）

- Evidence：
  - Basic + 缺 token → 403：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated`
  - Basic + csrf() → 200：`BootSecurityLabTest#csrfTokenAllowsPostWhenAuthenticated`
  - JWT + POST 不需要 csrf → 200：`BootSecurityLabTest#jwtPostDoesNotRequireCsrf`
  - 命中哪条链的证据链：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
- Call Chain Sketch：
  1) request 命中 chain → 是否包含 `CsrfFilter`  
  2) CSRF 校验失败 → `AccessDeniedHandler`（本模块映射成 `csrf_failed`）  
  3) JWT chain 明确 `csrf.disable()`：因此同样的 POST 在 JWT 链路不会被 CSRF 拦截
- Breakpoints：
  - spring：`org.springframework.security.web.csrf.CsrfFilter#doFilterInternal`（CSRF 校验点）
  - repo：`JsonAccessDeniedHandler#handle`（csrf_failed 映射分支）
  - repo：`SecurityConfig#jwtApiChain`（JWT 链路禁用 CSRF）

#### S4. JWT：scope → `SCOPE_xxx` authority 映射（401/403 与 scope 缺失）

- Evidence：
  - 缺 bearer token → 401：`BootSecurityLabTest#jwtSecureEndpointReturns401WhenMissingBearerToken`
  - Bearer 前缀缺失 → 401：`BootSecurityLabTest#jwtSecureEndpointReturns401WhenBearerPrefixMissing_asPitfall`
  - scope 不足 → 403：`BootSecurityLabTest#jwtAdminEndpointReturns403WhenScopeMissing`
- Call Chain Sketch：
  1) `Authorization: Bearer <token>` → Resource Server 从 header 提取 token  
  2) token → decoder → Authentication  
  3) `scope` claim（空格分隔）→ authorities（`SCOPE_xxx`）  
  4) 授权规则基于 `hasAuthority("SCOPE_admin")` 做 403 分流
- Breakpoints：
  - repo：`JwtTokenService#issueToken`（scope claim 的形态：空格分隔）
  - repo：`JsonAuthenticationEntryPoint#commence`（401）
  - repo：`JsonAccessDeniedHandler#handle`（403）

#### S5. Method Security：代理边界（self-invocation 绕过）

- Evidence：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`
- Call Chain Sketch：
  1) 外部 bean 调用 → 走代理 → 触发 method security 拦截  
  2) 类内部 `this.xxx()` → 直接调用目标方法 → 绕过代理（因此注解“看起来写了但没生效”）
- Breakpoints：
  - repo：`SelfInvocationPitfallService#outerCallsAdminOnly`（自调用入口）
  - repo：`SelfInvocationPitfallService#adminOnly`（被绕过的注解方法）
  - repo：`AdminOnlyService#adminOnlyAction`（对照：正常经过代理的受保护方法）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。
  
建议断点（先分流再深入）：

- 当你看到 **401/403**：
  - 先在测试请求处确认是否带了认证信息（Basic/JWT）
  - 再考虑授权配置（角色/权限）与异常处理
- 当你看到 **CSRF 403**：
  - 在 `BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated` 的请求处下断点，对比加上 `csrf()` 前后的差异
- 当你怀疑 **method security 没生效**：
  - 先验证调用是否经过代理（self-invocation 通常不会）
  - 对照 `BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall` 的断言结果再排障
- 若要深挖 filter chain（可选）：
  - `org.springframework.security.web.FilterChainProxy#doFilterInternal`

### Debug Playbook（从“现象”到“断点”）

1) 现象：**返回 401 unauthorized**  
   - 分支判定：缺认证/认证失败/JWT header 格式不对  
   - 断点锚点：`JsonAuthenticationEntryPoint#commence`  
   - 可跑入口：`BootSecurityLabTest#secureEndpointReturns401WhenAnonymous` / `BootSecurityLabTest#jwtSecureEndpointReturns401WhenBearerPrefixMissing_asPitfall`

2) 现象：**返回 403 forbidden**  
   - 分支判定：权限不足 vs CSRF 拦截（看 message：`forbidden` vs `csrf_failed`）  
   - 断点锚点：`JsonAccessDeniedHandler#handle`  
   - 可跑入口：`BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`

3) 现象：**POST 返回 403 csrf_failed（明明已认证）**  
   - 分支判定：请求是否命中 Basic 链路（CSRF 默认开启）  
   - 断点锚点：`org.springframework.security.web.csrf.CsrfFilter#doFilterInternal`  
   - 可跑入口：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated`

4) 现象：**JWT API 行为像 Basic/CSRF（疑似命中错链）**  
   - 分支判定：matcher 覆盖范围 / @Order / 是否先命中更“宽”的 chain  
   - 断点锚点：`FilterChainProxy#doFilterInternal` / `DefaultSecurityFilterChain#matches`  
   - 可跑入口：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`

5) 现象：**`@PreAuthorize` 看起来没生效**  
   - 分支判定：是否 self-invocation（同类 this 调用绕过代理）  
   - 断点锚点：`SelfInvocationPitfallService#outerCallsAdminOnly` / `SelfInvocationPitfallService#adminOnly`  
   - 可跑入口：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`
- 建议命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 如何跑实验
- 运行本模块测试：`mvn -pl :spring-boot-security test`

## 对应 Lab（可运行）

- `BootSecurityLabTest`
- `BootSecurityDevProfileLabTest`
- `BootSecurityExerciseTest`

## 常见坑与边界


## 推荐学习目标
1. 能按“Filter Chain → Authentication → Authorization”讲清主线
2. 能解释 CSRF 的威胁模型与默认行为
3. 能解释方法级安全为何依赖代理，以及常见的自调用绕过陷阱
4. 能解释 JWT 无状态方案的边界：认证与授权分别在哪里发生

## 推荐阅读顺序
1. [01-basic-auth-and-authorization](../part-01-security/087-01-basic-auth-and-authorization.md)
2. [02-csrf](../part-01-security/088-02-csrf.md)
3. [04-filter-chain-and-order](../part-01-security/090-04-filter-chain-and-order.md)
4. [03-method-security-and-proxy](../part-01-security/089-03-method-security-and-proxy.md)
5. [05-jwt-stateless](../part-01-security/091-05-jwt-stateless.md)
6. [90-common-pitfalls](../appendix/092-90-common-pitfalls.md)
7. [99-self-check](../appendix/093-99-self-check.md)

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`
- Exercise：`BootSecurityExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-security/01-basic-auth-and-authorization.md](../part-01-security/087-01-basic-auth-and-authorization.md)

<!-- BOOKIFY:END -->
