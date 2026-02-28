# 01. 主线时间线：Spring Boot Web Client
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕主线时间线：Spring Boot Web Client展开，主线可以概括为：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。

    阅读时可以先跑 `BootWebClientWebClientLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。

    需要下探源码时，可以从 `org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 172 章：Web Client 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 00 - Deep Dive Guide（springboot-web-client）](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：RestClient/WebClient 的调用主线、错误处理与超时重试，以及如何用测试把外部依赖固定下来。
    - 读完应当能复述：**构建请求 → 发出调用 → 处理状态码/异常 → 超时/重试 → 测试验证** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01 顺读 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`BootWebClientWebClientLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Web Client —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。
- 回到主线：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本章是「第 173 章：主线时间线：Spring Boot Web Client」的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
建议先跑 `BootWebClientWebClientLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Web Client」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- Web Client 是“对外出口”：它把外部 HTTP 依赖纳入系统边界，错误处理与超时策略决定系统韧性。
- 真实项目里，调用链排障需要“可观察证据”：日志、指标、以及 mock server 测试。

## 主线时间线（建议顺读）

1. RestClient 基础：同步调用的主线与最小闭环
   - 阅读：[01. RestClient](../part-01-web-client/01-restclient-basics.md)
2. WebClient 基础：响应式调用的主线与背压意识
   - 阅读：[02. WebClient](../part-01-web-client/02-webclient-basics.md)
3. 错误处理：状态码与异常的统一策略
   - 阅读：[03. 错误处理](../part-01-web-client/03-error-handling.md)
4. 超时与重试：避免“卡死/雪崩”的关键边界
   - 阅读：[04. 超时与重试](../part-01-web-client/04-timeout-and-retry.md)
5. 测试：用 MockWebServer 把外部依赖变成可重复实验
   - 阅读：[05. MockWebServer 测试](../part-01-web-client/05-testing-with-mockwebserver.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/01-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/02-self-check.md)

## 证据链（如何验证真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Web Client」的生效时机/顺序/边界；断点/入口：`org.springframework.web.reactive.function.client.WebClient`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Web Client」的生效时机/顺序/边界；断点/入口：`org.springframework.web.reactive.function.client.ExchangeFilterFunction`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Web Client」的生效时机/顺序/边界；断点/入口：`org.springframework.web.reactive.function.client.ExchangeFunction`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootWebClientWebClientLabTest`` 后，把上述观察点逐条对照，写出自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
