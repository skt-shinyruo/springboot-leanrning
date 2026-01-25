# 第 91 章：05：JWT/Stateless：Bearer token + scope（最小闭环）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：05：JWT/Stateless：Bearer token + scope（最小闭环）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootSecurityLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 90 章：04：FilterChain：多链路 + 顺序 + 自定义 Filter](090-04-filter-chain-and-order.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 92 章：90：常见坑清单（Security）](../appendix/092-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05：JWT/Stateless：Bearer token + scope（最小闭环）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityLabTest`
    - Test file：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

## 机制主线

对应代码：

- `spring-boot-modules/spring-boot-security/src/main/java/com/learning/springboot/bootsecurity/part01_security/SecurityConfig.java`
- `spring-boot-modules/spring-boot-security/src/main/java/com/learning/springboot/bootsecurity/part01_security/JwtTokenService.java`

## 你应该观察到什么

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

默认 `spring-boot:run` 只演示 Basic Auth；如果你想手动拿 token 体验 JWT 链路：

1) 启动 dev profile（启用 token 发行端点）：

2) 获取 token（scope=admin）：

```bash
curl 'http://localhost:8085/api/jwt/dev/token?subject=alice&scope=admin'
```

3) 访问 admin endpoint：

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8085/api/jwt/admin/ping
```

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootSecurityLabTest`
- 建议命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

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

- Symptom：你确认带了 token，但接口仍然返回 401（尤其是把 `Authorization: <token>` 直接塞进去时）。
- Root Cause：默认的 Bearer Token 解析器只认 `Authorization: Bearer <token>`；前缀不对就解析不到 token，最终认证上下文为空。
- Verification：`BootSecurityLabTest#jwtSecureEndpointReturns401WhenBearerPrefixMissing_asPitfall`
- Breakpoints：`BearerTokenAuthenticationFilter#doFilterInternal`、`DefaultBearerTokenResolver#resolve`
- Fix：统一使用 `Authorization: Bearer <token>`；如必须兼容非标准格式，显式配置 `BearerTokenResolver`（并在文档/测试中固化约定）。

### 坑点 2：token 带了 scope 但授权仍然 403（scope/authority 前缀不一致）

- Symptom：JWT 认证通过（不再 401），但访问需要权限的接口返回 403。
- Root Cause：Spring Security 对 scope 的默认映射通常会带 `SCOPE_` 前缀；如果你在规则里写 `hasRole("ADMIN")`/`ROLE_`，或 scope 名称与规则不一致，就会被拒绝。
- Verification：`BootSecurityLabTest#jwtAdminEndpointReturns403WhenScopeMissing`
- Breakpoints：`JwtAuthenticationProvider#authenticate`、`JwtGrantedAuthoritiesConverter#convert`、`AuthorizationFilter#doFilter`
- Fix：对齐“token 里提供什么 → 代码里用什么做授权”的映射（例如统一使用 `hasAuthority("SCOPE_admin")` 或调整 converter）。

> 注意：`/api/jwt/dev/token` 仅用于学习（dev profile），不是生产做法。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityLabTest`
- Test file：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

上一章：[FilterChain 与顺序](090-04-filter-chain-and-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[086-02-breakpoint-map.md](../part-00-guide/086-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
