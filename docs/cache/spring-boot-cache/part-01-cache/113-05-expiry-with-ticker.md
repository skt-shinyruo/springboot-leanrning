# 第 113 章：05：过期与可测性：用 Ticker 控制时间
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：05：过期与可测性：用 Ticker 控制时间
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Cacheable/@CachePut/@CacheEvict` 声明缓存意图；根据 key/condition/unless 设计缓存命中与一致性策略。
    - 原理：方法调用 → AOP 代理 → CacheInterceptor → key 计算（KeyGenerator/SpEL）→ 命中短路/不命中回源 → 回写/失效。
    - 源码入口：`org.springframework.cache.interceptor.CacheInterceptor` / `org.springframework.cache.interceptor.CacheAspectSupport` / `org.springframework.cache.interceptor.KeyGenerator` / `org.springframework.cache.CacheManager`
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 112 章：04：`sync=true`：防缓存击穿（stampede）](112-04-sync-stampede.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 114 章：90：常见坑清单（Cache）](../appendix/114-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05：过期与可测性：用 Ticker 控制时间**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootCacheLabTest`

## 机制主线

## 你应该观察到什么

- 通过 `ManualTicker` 快进时间，不需要 `Thread.sleep` 也能断言 TTL 过期行为

## 机制解释（Why）

Ticker 的核心价值是：让“时间推进”变成可控输入，从而写出稳定断言。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootCacheLabTest`
- 建议命令：`mvn -pl :spring-boot-cache test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 常见坑与边界

### 坑点 1：用真实时间 + sleep 测 TTL，导致 flaky 与慢测试

- Symptom：缓存过期测试偶发失败（机器负载/调度抖动），或为避免失败把 sleep 写很长导致测试很慢
- Root Cause：真实时间不可控，sleep 不是确定性输入
- Verification：`BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`
- Fix：用 `Ticker`（如 `ManualTicker`）把时间推进变成可控输入，把“过期”变成可断言事实

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`

上一章：[part-01-cache/04-sync-stampede.md](112-04-sync-stampede.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/114-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
