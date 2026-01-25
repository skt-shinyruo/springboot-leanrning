# 第 183 章：主线时间线：Spring Boot Testing
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Boot Testing
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
    - 原理：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
    - 源码入口：`org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`
    - 推荐 Lab：`GreetingControllerWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 182 章：Testing 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 184 章：00 - Deep Dive Guide（springboot-testing）](184-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：如何用 slice、mock 与集成测试把 Spring 行为“锁住”，让机制理解可以被验证。
    - 读完你应该能复述：**选择测试切片 → 准备依赖（mock/替身）→ 验证行为 → 定位失败** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → 仅 1 章主线 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`GreetingControllerWebMvcLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Testing —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
- 回到主线：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Testing」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 测试是“把主线变成可重复实验”的方式：它让你能验证“代理/事务/Web 绑定”等机制是否按预期发生。

## 主线时间线（建议顺读）

1. 选择 slice 与 mocking：把测试范围控制在你想验证的边界上
   - 阅读：[01-slice-and-mocking.md](../part-01-testing/185-01-slice-and-mocking.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/186-90-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/187-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Testing」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.test.context.SpringBootTest`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Testing」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Testing」的生效时机/顺序/边界；断点/入口：`org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``GreetingControllerWebMvcLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
