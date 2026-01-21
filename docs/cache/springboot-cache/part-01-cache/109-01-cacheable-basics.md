# 第 109 章：01：`@Cacheable` 最小闭环
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：`@Cacheable` 最小闭环
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Cacheable/@CachePut/@CacheEvict` 声明缓存意图；根据 key/condition/unless 设计缓存命中与一致性策略。
    - 原理：方法调用 → AOP 代理 → CacheInterceptor → key 计算（KeyGenerator/SpEL）→ 命中短路/不命中回源 → 回写/失效。
    - 源码入口：`org.springframework.cache.interceptor.CacheInterceptor` / `org.springframework.cache.interceptor.CacheAspectSupport` / `org.springframework.cache.interceptor.KeyGenerator` / `org.springframework.cache.CacheManager`
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 108 章：00 - Deep Dive Guide（springboot-cache）](../part-00-guide/108-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 110 章：02：`@CachePut/@CacheEvict`：更新与失效](110-02-cacheput-and-evict.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：`@Cacheable` 最小闭环**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootCacheLabTest`
    - Test file：`spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java`

## 机制主线


## 你应该观察到什么

- 同一个 key（例如 name=alice）只计算一次，后续从 cache 直接返回
- 不同 key 会命中不同 entry

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

### 坑点 1：把 `@Cacheable` 方法当成“每次都会执行”，忽略了命中短路

- Symptom：你在方法里写了日志/计数/副作用，以为每次调用都会发生，但线上只发生一次或发生次数异常
- Root Cause：`@Cacheable` 命中后会短路方法执行（直接返回缓存值）
- Verification：`BootCacheLabTest#cacheableCachesResultForSameKey`（invocationCount 作为证据）
- Fix：缓存方法尽量保持纯函数/无副作用；副作用需要单独设计，不要依赖“方法一定会被调用”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`
- Test file：`spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/108-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-cache/02-cacheput-and-evict.md](110-02-cacheput-and-evict.md)

<!-- BOOKIFY:END -->
