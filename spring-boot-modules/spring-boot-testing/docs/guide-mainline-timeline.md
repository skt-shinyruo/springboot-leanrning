# 01. 主线时间线：Spring Boot Testing
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕主线时间线：Spring Boot Testing展开，主线可以概括为：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。

    阅读时可以先跑 `GreetingControllerWebMvcLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。

    需要下探源码时，可以从 `org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Testing 主线](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[深挖导读：Spring Boot Testing](guide-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

!!! summary
    - 这一模块关注：如何用 slice、mock 与集成测试把 Spring 行为“锁定”，让机制理解可以被验证。
    - 读完后应能复述：**选择测试切片 → 准备依赖（mock/替身）→ 验证行为 → 定位失败** 这一条主线。
    - 阅读顺序：先读《深挖导读》→ 本章 → 仅 1 章主线 → 附录排坑。

!!! example "先运行的 Lab（把时间线变成证据）"

    - Lab：`GreetingControllerWebMvcLabTest`

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Testing」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读路径：
- 先看章首的“章节入口/本章要点”，建立预期；
- 先运行本章 Lab 固化现象，再回到正文对照机制。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 测试是“把主线变成可重复实验”的方式：它让能验证“代理/事务/Web 绑定”等机制是否按预期发生。

## 主线时间线（顺读路径）

1. 选择 slice 与 mocking：把测试范围控制在想验证的边界上
   - 阅读：[01-slice-and-mocking.md](testing-slice-and-mocking.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](appendix-common-pitfalls.md)
- 自检：[99-self-check.md](appendix-self-check.md)

## 证据链（如何验证真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章入口后，聚焦「主线时间线：Spring Boot Testing」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.test.context.SpringBootTest`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章入口后，聚焦「主线时间线：Spring Boot Testing」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章入口后，聚焦「主线时间线：Spring Boot Testing」的生效时机/顺序/边界；断点/入口：`org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 动作：跑完 ``GreetingControllerWebMvcLabTest`` 后，把上述观察点逐条对照，写出 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Testing —— 先运行本章 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
- 回到主线：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
- 下一章：按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->
