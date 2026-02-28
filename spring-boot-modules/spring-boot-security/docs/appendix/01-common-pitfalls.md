# 01. 常见坑清单（Security）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    Security 的坑很少是“注解写错了”，更多是“请求在 FilterChain 里走到了哪一步”。本章把高频误判（401/403/CSRF、method security 的代理边界、多条 FilterChain 的匹配顺序）整理成一份可对照的排障笔记。

    建议先跑 `BootSecurityDevProfileLabTest` / `BootSecurityLabTest` 把状态码与错误体的分流跑成事实，再回到本章逐条对照；需要下探源码时，从 `FilterChainProxy` 选择链、`AuthorizationFilter` 的授权决策，以及异常翻译链路切入最省时间。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. JWT/Stateless：Bearer token + scope（最小闭环）](../part-01-security/05-jwt-stateless.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-security）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先把 FilterChain 的分流跑成断言（再谈 401/403/CSRF）

安全行为的第一个事实是：请求是否进入了预期的 `SecurityFilterChain`。只有把这个事实跑出来，401/403/CSRF 这些分支才有讨论基础；否则很容易在“没走到那条链”的前提下讨论授权规则。

建议先用两组矩阵测试把主线与分支固定下来：

- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`

需要沿源码追时，再对照本模块的断点地图与关键分支矩阵去命中入口，它们把“链选择点/异常翻译点/CSRF 拦截点”都标出来了：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md) / [05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`

## 最小可运行实验（Lab）

- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`
- 建议命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界
下面几个坑围绕的是同一条主线：HTTP 请求进入 `FilterChainProxy`，选择链之后完成认证（Authentication）与授权（Authorization），最后由异常翻译链路把结果变成 401/403/CSRF，再决定是否继续进入 MVC。

## 401 vs 403：先问“有没有认证”，再谈“有没有权限”

401 的核心含义是“没有通过认证”，调用方要做的动作是“登录/携带凭证”；403 则是“已经认证但被拒绝”，需要回到权限/CSRF 等策略上找原因。把它们混为一谈，排障通常会在错误的层里来回折返。

本模块的示例会把错误响应的 `message` 固定成三类：`unauthorized`、`forbidden`、`csrf_failed`。当错误体能稳定落在这三类之一时，排障速度会明显提升，因为“分支结果”被写进了证据里，而不是留在脑补里。

对应的最小对照（建议先跑一遍再读本节）：

- 401（匿名访问 secure）：`BootSecurityLabTest#secureEndpointReturns401WhenAnonymous`
- 403（已认证但无权限）：`BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`
- 403（CSRF 缺失导致的拦截）：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated`

## CSRF 误区

Basic Auth 并不天然绕过 CSRF：只要是“写操作”，就可能需要 CSRF token（取决于链路配置）。因此当看到 403 时，先不要急着改授权规则，可以先确认是不是 CSRF 分支命中了（上面的测试就是最短证据）。

对于纯 API 场景，常见做法是禁用 CSRF，但前提是安全边界被明确表达出来（例如选择 stateless 的 JWT 链路）。本模块在 `/api/jwt/**` 上给了一个对照：`BootSecurityLabTest#jwtPostDoesNotRequireCsrf`。

## Method Security 没生效

方法级安全依赖代理与拦截器链，因此最常见的根因仍然是 self-invocation：调用发生在同一个类内部，绕过了代理，于是 `@PreAuthorize` 等拦截器根本没有机会介入。

排查时先问自己一个问题：调用是从另一个 bean 进来的吗？还是同类 `this.xxx()`？这个边界可以用 `BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall` 直接对照。

## JWT 授权不匹配

JWT 的授权失败往往不是“token 不对”，而是 claim 的形状与授权规则对不上：scope claim 叫 `scope` 还是 `scp`？规则写的是 `hasRole` 还是 `hasAuthority("SCOPE_xxx")`？这些差异都属于“语义不匹配”，而不是实现错误。

本模块用 `BootSecurityLabTest#jwtAdminEndpointReturns403WhenScopeMissing` 与 `BootSecurityLabTest#jwtAdminEndpointIsAccessibleWhenAdminScopePresent` 把这个分支写成了最小对照。

## 多个 FilterChain 规则冲突

### 坑点：更“宽”的 matcher 抢先匹配，导致直觉里的链路根本没进来

以为 `/jwt/**` 会走 JWT 的那条 `SecurityFilterChain`，结果却走了另一条（常见表现：401/403 与预期不一致，或者根本没有走到加的 Filter）。这类问题之所以“像玄学”，是因为链路在最开始就选错了分支，后面的所有判断都建立在错误前提上。

`FilterChainProxy` 会按顺序遍历 `SecurityFilterChain`，**第一个 matches 的链就会被选中**；如果某条链的 matcher 过宽（例如 `/**`）且顺序更靠前，它会“吃掉”后续更具体的链。

这件事可以直接用 `BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain` 复现：同一个 `FilterChainProxy`，对不同 path 会选择不同链。断点入口通常落在 `FilterChainProxy#doFilterInternal`、`FilterChainProxy#getFilters`、`DefaultSecurityFilterChain#matches`。

修复思路也应当服务于“让分支变得确定”：把 matcher 写得更具体（优先写清路径/方法），并显式控制链顺序（例如 `@Order`）。同时，把“到底选了哪条链”用可断言的 Lab/Test 固化下来，避免把行为留在口头约定里。

- matcher 覆盖范围是否互斥？
- `@Order` 是否符合预期？

## 对应 Lab（可运行）

- `BootSecurityLabTest`
- `BootSecurityDevProfileLabTest`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`

上一章：[part-01-security/05-jwt-stateless.md](../part-01-security/05-jwt-stateless.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
