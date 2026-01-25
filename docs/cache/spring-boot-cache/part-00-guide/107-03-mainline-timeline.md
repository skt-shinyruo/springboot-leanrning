# 第 107 章：主线时间线：Spring Boot Cache
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Boot Cache
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 106 章：Cache 主线](../../../book/106-cache-mainline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 108 章：00 - Deep Dive Guide（springboot-cache）](108-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：缓存注解如何通过代理织入方法调用，以及 key/condition/unless/并发击穿等关键边界。
    - 读完你应该能复述：**方法调用 → 缓存拦截器 → key 计算 → 命中/回源 → 回写/失效** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01 顺读 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`BootCacheLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Cache —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Cache」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-cache -Dtest=BootCacheLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- Cache 依赖 AOP：很多行为差异来自代理边界、key 计算、条件表达式与并发语义。
- 缓存最终是“读写一致性”问题：你需要明确缓存层与数据层的边界。

## 主线时间线（建议顺读）

1. 先把 @Cacheable 的基本语义跑通（命中/回源/回写）
   - 阅读：[01. @Cacheable 基础](../part-01-cache/109-01-cacheable-basics.md)
2. 写路径：@CachePut / @CacheEvict 如何影响一致性
   - 阅读：[02. @CachePut/@CacheEvict](../part-01-cache/110-02-cacheput-and-evict.md)
3. key/condition/unless：表达式与行为差异的来源
   - 阅读：[03. key/condition/unless](../part-01-cache/111-03-key-condition-unless.md)
4. 并发击穿：sync 与 stampede 的真实语义
   - 阅读：[04. sync 与击穿](../part-01-cache/112-04-sync-stampede.md)
5. 过期与时间：如何验证“到底什么时候失效”
   - 阅读：[05. 过期语义](../part-01-cache/113-05-expiry-with-ticker.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/114-90-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/115-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Cache」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Cache」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootCacheLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[深挖导读](108-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->
