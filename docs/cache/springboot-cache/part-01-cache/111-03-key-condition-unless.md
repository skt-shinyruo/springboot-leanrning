# 第 111 章：03：key / condition / unless：缓存边界
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：03：key / condition / unless：缓存边界
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Cacheable/@CachePut/@CacheEvict` 声明缓存意图；根据 key/condition/unless 设计缓存命中与一致性策略。
    - 原理：方法调用 → AOP 代理 → CacheInterceptor → key 计算（KeyGenerator/SpEL）→ 命中短路/不命中回源 → 回写/失效。
    - 源码入口：`org.springframework.cache.interceptor.CacheInterceptor` / `org.springframework.cache.interceptor.CacheAspectSupport` / `org.springframework.cache.interceptor.KeyGenerator` / `org.springframework.cache.CacheManager`
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 110 章：02：`@CachePut/@CacheEvict`：更新与失效](110-02-cacheput-and-evict.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 112 章：04：`sync=true`：防缓存击穿（stampede）](112-04-sync-stampede.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03：key / condition / unless：缓存边界**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootCacheLabTest`

## 机制主线


## 你应该观察到什么

- condition：在方法执行前判断，false 时不走缓存（每次都会计算）
- unless：在方法执行后判断，满足条件时不缓存返回值

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootCacheLabTest`
- 建议命令：`mvn -pl :springboot-cache test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 常见坑与边界

### 坑点 1：把 condition 与 unless 当成一回事，导致“不该缓存的结果被缓存/反之”

- Symptom：你以为设置了 condition/unless 就能避免缓存某些情况，但实际命中行为与预期相反
- Root Cause：
  - `condition` 在方法执行前评估（决定“要不要走缓存逻辑”）
  - `unless` 在方法执行后评估（决定“要不要缓存返回值”）
- Verification：
  - condition=false 每次都计算：`BootCacheLabTest#conditionPreventsCachingWhenFalse`
  - unless=true 不缓存返回值：`BootCacheLabTest#unlessPreventsCachingBasedOnResult`
- Fix：把“前置过滤”和“结果过滤”分开设计；用测试把分支锁住，别靠直觉

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`

上一章：[part-01-cache/02-cacheput-and-evict.md](110-02-cacheput-and-evict.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-cache/04-sync-stampede.md](112-04-sync-stampede.md)

<!-- BOOKIFY:END -->
