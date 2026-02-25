# 01. 主线时间线：Spring Boot Actuator
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Boot Actuator
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。
    - 原理：引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。
    - 源码入口：`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration` / `org.springframework.boot.actuate.endpoint.annotation.Endpoint` / `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties`
    - 推荐 Lab：`BootActuatorLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 166 章：Actuator/Observability 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 00 - Deep Dive Guide（springboot-actuator）](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：Actuator 如何把“应用内部状态”以 endpoints 的形式暴露出来（健康检查、指标、信息等）。
    - 读完你应该能复述：**启用 actuator → 配置暴露范围 → 访问 endpoints → 观测与排障** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → 仅 1 章主线 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`BootActuatorLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Actuator —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。
- 回到主线：引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本章是「第 167 章：主线时间线：Spring Boot Actuator」的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
建议先跑 `BootActuatorLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Actuator」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- Actuator 是“可观测性入口”：当你要解释系统行为、排查线上问题、做健康探针时，最先想到它。
- 它通常与安全（认证授权）、Web（暴露路径）一起出现，需要边界意识。

## 主线时间线（建议顺读）

1. Actuator 的基本使用与关键配置点
   - 阅读：[01-actuator-basics.md](../part-01-actuator/01-actuator-basics.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/01-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/02-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Actuator」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Actuator」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.actuate.endpoint.annotation.Endpoint`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Actuator」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootActuatorLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
