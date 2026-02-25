# 01. 主线时间线：Spring Resources
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Resources
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 138 章：Resources 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 深挖指南（Spring Core Resources）](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：Resource 抽象如何统一 classpath/file/jar 等不同资源形态，以及你如何可靠地定位与读取资源。
    - 读完你应该能复述：**定位（路径/模式）→ 解析（Resource）→ 校验（exists/handles）→ 读取（编码/流）** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01 顺读 6 章 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`SpringCoreResourcesLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Resources —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
- 回到主线：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本章是「第 139 章：主线时间线：Spring Resources」的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
建议先跑 `SpringCoreResourcesLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Resources」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 资源读取贯穿启动期与运行期：配置、模板、静态资源、证书、SQL 脚本都可能走到这里。
- 资源问题常见于“本地可以、线上不行”：根因往往是 classpath/jar 与 filesystem 的差异。

## 主线时间线（建议顺读）

1. 先建立抽象：Resource/ResourceLoader 到底解决什么
   - 阅读：[01. Resource 抽象](../part-01-resource-abstraction/01-resource-abstraction.md)
2. 把 classpath 的定位方式讲清楚（相对路径、前缀、语义差异）
   - 阅读：[02. classpath 定位](../part-01-resource-abstraction/02-classpath-locations.md)
3. 通配与模式：classpath* 与 pattern 的边界
   - 阅读：[03. classpath* 与 pattern](../part-01-resource-abstraction/03-classpath-star-and-pattern.md)
4. exists 与 handles：为什么“exists=true 但读不到/不对”
   - 阅读：[04. exists 与 handles](../part-01-resource-abstraction/04-exists-and-handles.md)
5. 读取与编码：如何避免乱码与流泄露
   - 阅读：[05. 读取与编码](../part-01-resource-abstraction/05-reading-and-encoding.md)
6. jar vs filesystem：真正决定行为差异的地方
   - 阅读：[06. jar vs filesystem](../part-01-resource-abstraction/06-jar-vs-filesystem.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/01-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/02-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Resources」的生效时机/顺序/边界；断点/入口：`org.springframework.core.io.Resource`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Resources」的生效时机/顺序/边界；断点/入口：`org.springframework.core.io.ResourceLoader`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Resources」的生效时机/顺序/边界；断点/入口：`org.springframework.core.io.support.PathMatchingResourcePatternResolver`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreResourcesLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
