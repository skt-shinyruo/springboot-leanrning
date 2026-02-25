# 01. 常见坑清单（Security）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：90：常见坑清单（Security）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
    - 原理：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
    - 源码入口：`org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter`
    - 推荐 Lab：`BootSecurityDevProfileLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. JWT/Stateless：Bearer token + scope（最小闭环）](../part-01-security/05-jwt-stateless.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-security）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)

- 本章主题：**01. 常见坑清单（Security）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`

## 机制主线

这页不展开完整机制主线；其定位更接近排障备忘录：把常见分支与可复现入口列出来，便于回到 tests 验证。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`
- 建议命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界


## 401 vs 403 判断错

- 401：没认证（anonymous / 登录失败）
- 403：已认证但没权限（或 CSRF 拦截）

建议先看本模块统一错误响应的 `message`：
- `unauthorized`
- `forbidden`
- `csrf_failed`

## CSRF 误区

- Basic Auth 并不天然绕过 CSRF：你只要是“写操作”，就可能需要 CSRF token（取决于你的链路配置）。
- 对 API 场景常见做法是禁用 CSRF，但要明确安全边界（本模块用 `/api/jwt/**` 做了演示）。

## Method Security 没生效

- 最常见根因：self-invocation 没走代理。
- 排查时先问自己：调用是从另一个 bean 进来的吗？还是同类 `this.xxx()`？

## JWT 授权不匹配

- scope claim 长什么样？（`scope` vs `scp`）
- 你的规则写的是 `hasRole` 还是 `hasAuthority("SCOPE_xxx")`？

## 多个 FilterChain 规则冲突

### 坑点：更“宽”的 matcher 抢先匹配，导致你以为的链路根本没进来

- Symptom：你以为 `/jwt/**` 会走 JWT 的那条 `SecurityFilterChain`，结果却走了另一条（常见表现：401/403 与预期不一致，或者根本没有走到你加的 Filter）。
- Root Cause：`FilterChainProxy` 会按顺序遍历 `SecurityFilterChain`，**第一个 matches 的链就会被选中**；如果某条链的 matcher 过宽（例如 `/**`）且顺序更靠前，它会“吃掉”后续更具体的链。
- Verification：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
- Breakpoints：`FilterChainProxy#doFilterInternal`、`FilterChainProxy#getFilters`、`DefaultSecurityFilterChain#matches`
- Fix：让 matcher 更具体（优先写清路径/方法），并显式控制链顺序（例如 `@Order`）；同时把“到底选了哪条链”用可断言的 Lab/Test 固化下来。

- matcher 覆盖范围是否互斥？
- `@Order` 是否符合你的预期？

## 对应 Lab（可运行）

- `BootSecurityLabTest`
- `BootSecurityDevProfileLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest`

上一章：[part-01-security/05-jwt-stateless.md](../part-01-security/05-jwt-stateless.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
