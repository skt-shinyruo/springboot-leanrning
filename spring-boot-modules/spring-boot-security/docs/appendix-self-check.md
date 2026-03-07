# 99 自检：Spring Boot Security
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`BootSecurityBookMatrixLabTest`
    - 分支入口：`BootSecurityBranchMatrixLabTest`
    - 推荐先跑：`BootSecurityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（Security）](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. 401 与 403 的分界线是什么？如何用两条用例把它固定为事实（而不是靠口述）？
   - 证据入口：`BootSecurityLabTest#secureEndpointReturns401WhenAnonymous` + `BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`
2. Basic Auth 成功时，认证信息在哪里体现？如何验证“当前用户是谁”？
   - 证据入口：`BootSecurityLabTest#secureEndpointIsAccessibleWithBasicAuth`
3. admin 端点对 admin 用户放行的条件是什么？如何证明“不是所有带用户名的人都能进”？
   - 证据入口：`BootSecurityLabTest#adminEndpointIsAccessibleForAdminUser` + `BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`
4. `roles` 与 `authorities` 的差异为什么会造成“看起来有 ADMIN 但仍 403”的坑？如何用一个用例稳定复现它？
   - 证据入口：`BootSecurityLabTest#adminEndpointReturns403WhenAuthorityAdminButMissingRolePrefix_asPitfall`
5. 为什么 POST 在已认证情况下仍可能被 CSRF 拦截？如何用一对用例证明“加 token 前后差异”？
   - 证据入口：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated` + `BootSecurityLabTest#csrfTokenAllowsPostWhenAuthenticated`
6. 为什么 JWT 场景下同样的 POST 不要求 CSRF？如何把它写成回归用例？
   - 证据入口：`BootSecurityLabTest#jwtPostDoesNotRequireCsrf`
7. Bearer token 的格式为什么重要？缺少 `Bearer ` 前缀会变成什么错误？
   - 证据入口：`BootSecurityLabTest#jwtSecureEndpointReturns401WhenBearerPrefixMissing_asPitfall`
8. scope/权限不足时为什么会 403？如何用一对用例把“read vs admin”的差异跑成事实？
   - 证据入口：`BootSecurityLabTest#jwtAdminEndpointReturns403WhenScopeMissing` + `BootSecurityLabTest#jwtAdminEndpointIsAccessibleWhenAdminScopePresent`
9. 多条 `SecurityFilterChain` 时，请求是如何被 matcher 分流的？如何证明 `/api/jwt/**` 不会走 Basic chain？
   - 证据入口：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
10. method security 的边界在哪里？为什么 self-invocation 会绕过它？如何用一个可回归用例锁定这个坑？
    - 证据入口：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`

## 退出条件（完成标准）

- 能把“安全问题”拆成可验证分支：路由/暴露（404）→ 认证（401）→ 授权（403）→ CSRF（403 的一种）。
- 能把多 filter chain 与 method security 的两条链路区分开，并能指回对应的测试证据。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`
- Exercise：`BootSecurityExerciseTest`

上一章：[appendix/90-common-pitfalls.md](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
