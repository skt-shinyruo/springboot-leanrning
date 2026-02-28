# 01. 主线时间线：Spring Boot Security
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕主线时间线：Spring Boot Security展开，主线可以概括为：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。

    阅读时可以先跑 `BootSecurityLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。

    需要下探源码时，可以从 `org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 84 章：Security 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 00 - Deep Dive Guide（springboot-security）](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：安全在 Spring 中如何以 FilterChain 形式接入请求链路，以及认证/授权/CSRF/JWT 的关键分支。
    - 读完应当能复述：**请求进入 → Security FilterChain → 认证 → 授权 → 继续到 MVC/返回** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01 顺读 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`BootSecurityLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Security —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
- 回到主线：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本章是「第 85 章：主线时间线：Spring Boot Security」的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
建议先跑 `BootSecurityLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Security」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- Security 在 Web 入口之前：先过 FilterChain，再进入 MVC（DispatcherServlet）。
- 很多“401/403/登录态不对”的问题，本质是过滤器顺序、匹配范围、鉴权表达式或 Session/JWT 的边界。

## 主线时间线（建议顺读）

1. 基础认证与授权：先把 401/403 的含义与分支跑通
   - 阅读：[01. 基础认证与授权](../part-01-security/01-basic-auth-and-authorization.md)
2. CSRF：什么时候需要、为什么会报 403、怎么验证
   - 阅读：[02. CSRF](../part-01-security/02-csrf.md)
3. 方法级安全：为什么它依赖代理（以及常见边界）
   - 阅读：[03. 方法安全与代理](../part-01-security/03-method-security-and-proxy.md)
4. FilterChain 与顺序：很多“看起来怪”的行为都在这里
   - 阅读：[04. FilterChain 与顺序](../part-01-security/04-filter-chain-and-order.md)
5. JWT 无状态：如何把认证态从 Session 迁移到 Token
   - 阅读：[05. JWT 无状态](../part-01-security/05-jwt-stateless.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/01-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/02-self-check.md)

## 证据链（如何验证真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Security」的生效时机/顺序/边界；断点/入口：`org.springframework.security.web.FilterChainProxy`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Security」的生效时机/顺序/边界；断点/入口：`org.springframework.security.web.SecurityFilterChain`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Security」的生效时机/顺序/边界；断点/入口：`org.springframework.security.web.access.intercept.AuthorizationFilter`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootSecurityLabTest`` 后，把上述观察点逐条对照，写出自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
