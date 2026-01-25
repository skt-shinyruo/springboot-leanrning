# 第 112 章：04：`sync=true`：防缓存击穿（stampede）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：`sync=true`：防缓存击穿（stampede）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Cacheable/@CachePut/@CacheEvict` 声明缓存意图；根据 key/condition/unless 设计缓存命中与一致性策略。
    - 原理：方法调用 → AOP 代理 → CacheInterceptor → key 计算（KeyGenerator/SpEL）→ 命中短路/不命中回源 → 回写/失效。
    - 源码入口：`org.springframework.cache.interceptor.CacheInterceptor` / `org.springframework.cache.interceptor.CacheAspectSupport` / `org.springframework.cache.interceptor.KeyGenerator` / `org.springframework.cache.CacheManager`
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 111 章：03：key / condition / unless：缓存边界](111-03-key-condition-unless.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 113 章：05：过期与可测性：用 Ticker 控制时间](113-05-expiry-with-ticker.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04：`sync=true`：防缓存击穿（stampede）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootCacheLabTest`

## 机制主线

## 你应该观察到什么

- 并发请求同一个 key 时：
  - `sync=true` 能把“同 key 的并发计算”收敛成一次

## 机制解释（Why）

`sync=true` 的语义是：对同一个 key，只有一个线程负责计算并写入，其他线程等待结果。

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

### 坑点 1：把 `sync=true` 当成“万能并发方案”，忽略了等待与吞吐边界

- Symptom：并发同 key 时吞吐下降明显；线程大量等待导致延迟上升
- Root Cause：`sync=true` 会让同 key 的并发请求等待同一个计算结果（避免击穿，但把并发变串行）
- Verification：`BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`（invocations=1 证明“收敛”确实发生）
- Fix：只对“同 key 且计算昂贵”的场景启用 sync；同时评估等待成本与下游吞吐能力，必要时配合限流/降级

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`

上一章：[part-01-cache/03-key-condition-unless.md](111-03-key-condition-unless.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-cache/05-expiry-with-ticker.md](113-05-expiry-with-ticker.md)

<!-- BOOKIFY:END -->
