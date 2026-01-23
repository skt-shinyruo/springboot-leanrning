# 第 87 章：01：401 vs 403：Basic Auth 与授权规则
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：401 vs 403：Basic Auth 与授权规则
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
    - 原理：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
    - 源码入口：`org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter`
    - 推荐 Lab：`BootSecurityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 86 章：00 - Deep Dive Guide（springboot-security）](../part-00-guide/086-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 88 章：02：CSRF：为什么 GET 没事但 POST 会 403？](088-02-csrf.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：401 vs 403：Basic Auth 与授权规则**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityLabTest`
    - Test file：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

## 机制主线


## 你应该观察到什么

- 访问需要登录的资源（例如 `/api/secure/ping`）：
  - 未登录 → **401**（`unauthorized`）
- 访问需要更高权限的资源（例如 `/api/admin/ping`）：
  - 已登录但权限不足 → **403**（`forbidden`）

## 机制解释（Why）

可以把 Security 的判断分成两步：

1) **你是谁**（Authentication）：有没有成功登录？当前 `Authentication` 是不是匿名？
2) **你能做什么**（Authorization）：你是否具备访问该资源所需的 role/authority？

在本模块里：

- Basic Auth 用户在 `SecurityConfig#userDetailsService` 中定义（`user/password`、`admin/password`）
- `/api/admin/**` 需要 `ROLE_ADMIN`（见 `SecurityConfig#apiChain`）

- 先看响应体 `message/status/path`（本模块统一返回 JSON 错误结构），再去看 `SecurityConfig` 的规则。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootSecurityLabTest`
- 建议命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

本章的目标是把“为什么有时是 401、有时是 403”讲清楚，并用可运行测试把结论固化下来。

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## Debug 建议


## 常见坑与边界

### 坑点 1：把 401/403 当成同一种失败，导致排障走错方向

- Symptom：接口访问失败时只盯着“账号密码/权限配置”某一处反复试错
- Root Cause：401 与 403 分别对应不同分流：
  - 401：Authentication 没建立（匿名/认证失败）
  - 403：Authentication 已建立，但 Authorization 不通过（权限不足/CSRF 等）
- Verification：
  - 401：`BootSecurityLabTest#secureEndpointReturns401WhenAnonymous`
  - 403：`BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`
- Fix：先根据响应码分流（401→认证；403→鉴权/CSRF），再回到 `SecurityConfig` 对齐规则

### 坑点 2：`hasRole("ADMIN")` 不是 `authorities("ADMIN")`（ROLE_ 前缀边界）

- Symptom：你以为“我已经给了 ADMIN 权限”，但访问 `/api/admin/**` 仍然 403
- Root Cause：
  - `hasRole("ADMIN")` 的语义是：需要 `ROLE_ADMIN`
  - 仅有 `ADMIN` authority 并不等价于 `ROLE_ADMIN`
- Verification：`BootSecurityLabTest#adminEndpointReturns403WhenAuthorityAdminButMissingRolePrefix_asPitfall`
- Breakpoints：
  - `SecurityConfig#apiChain`（`hasRole("ADMIN")` 规则定义）
  - `JsonAccessDeniedHandler#handle`（403 塑形）
- Fix：在需要 role 语义时给 `ROLE_ADMIN`（或改用 `hasAuthority("ADMIN")` 并统一你的权限命名）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityLabTest`
- Test file：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/086-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-security/02-csrf.md](088-02-csrf.md)

<!-- BOOKIFY:END -->
