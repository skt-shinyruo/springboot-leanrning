# 第 108 章：00 - Deep Dive Guide（springboot-cache）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Deep Dive Guide（springboot-cache）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Cacheable/@CachePut/@CacheEvict` 声明缓存意图；根据 key/condition/unless 设计缓存命中与一致性策略。
    - 原理：方法调用 → AOP 代理 → CacheInterceptor → key 计算（KeyGenerator/SpEL）→ 命中短路/不命中回源 → 回写/失效。
    - 源码入口：`org.springframework.cache.interceptor.CacheInterceptor` / `org.springframework.cache.interceptor.CacheAspectSupport` / `org.springframework.cache.interceptor.KeyGenerator` / `org.springframework.cache.CacheManager`
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 107 章：主线时间线：Spring Boot Cache](107-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 109 章：01：`@Cacheable` 最小闭环](../part-01-cache/109-01-cacheable-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00 - Deep Dive Guide（springboot-cache）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`

## 机制主线

本模块的目标是把“缓存注解”从背诵题变成可断言机制：

- `@Cacheable`：**读缓存**（命中则短路，不命中才计算）
- `@CachePut`：**写缓存**（强制执行方法，再把返回值写入缓存）
- `@CacheEvict`：**删缓存**（删除某个 key 或整个 cache）

并且把三类最常见的“工程边界”锁进默认 Lab：

1. **key 规则**（同一个调用到底算不算同一个 entry）
2. **condition/unless 分支**（哪些请求/结果根本不应该缓存）
3. **stampede 防击穿**（`sync=true` 的语义与代价）

### 1) 时间线：一次 `@Cacheable` 调用发生了什么

1. 方法调用进入缓存拦截器（AOP 代理）
2. 计算 key（默认 key 或 SpEL key）
3. 查 cache：
   - 命中：直接返回缓存值（方法体不会执行）
   - 未命中：执行方法体 → 得到结果 → 写入 cache → 返回结果
4. `condition/unless` 分流：
   - `condition=false`：不缓存（即使命中也不会读取/写入）
   - `unless=true`：不缓存返回值（常见于“结果不合格/空值”等）

### 2) 关键参与者

- `@EnableCaching` / `CacheManager`：缓存基础设施与 cache 容器
- `Cache`：读写入口（命中/未命中）
- `@Cacheable/@CachePut/@CacheEvict`：声明式缓存语义
- `Ticker`（Caffeine）：让“过期”可测试（避免靠 sleep）

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **命中短路：同 key 只计算一次**
   - 验证：`BootCacheLabTest#cacheableCachesResultForSameKey`
2. **condition/unless：不缓存分支可断言（不是“感觉”）**
   - 验证：`BootCacheLabTest#conditionPreventsCachingWhenFalse` / `BootCacheLabTest#unlessPreventsCachingBasedOnResult`
3. **sync 防击穿：并发同 key 只允许一次计算**
   - 验证：`BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`
4. **过期可测试：用 manual ticker 做确定性过期验证**
   - 验证：`BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`
5. **SpEL key：复合 key 让缓存维度更精确**
   - 验证：`BootCacheSpelKeyLabTest#spelKeyCreatesIndependentCacheEntries`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。
  
建议断点（把“命中/未命中/不缓存”三种现象分清楚）：

- `BootCacheLabTest` 里每个断言前的调用点（先用 invocationCount 锁住“方法到底有没有执行”）
- 当你怀疑 key 不对：
  - 对照 `BootCacheSpelKeyLabTest`，先固定 key 计算规则，再讨论 cache 命中
- 当你怀疑“过期没生效”：
  - 不要用 sleep 试运气，优先用 ticker 类似 `ManualTicker` 固定时间推进

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
- 建议命令：`mvn -pl :spring-boot-cache test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 推荐学习目标
1. 能解释“缓存命中/未命中/更新/失效”的语义差异
2. 能把 key/condition/unless 的规则写成可断言的复现
3. 能解释 `sync` 解决的是什么问题，以及它的代价与边界

## 如何跑实验
- 运行本模块测试：`mvn -pl :spring-boot-cache test`

## 对应 Lab（可运行）

- `BootCacheLabTest`
- `BootCacheSpelKeyLabTest`
- `BootCacheExerciseTest`

## 常见坑与边界

- （本章坑点待补齐：建议先跑一次 E，再回看断言失败场景与边界条件。）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
- Exercise：`BootCacheExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-cache/01-cacheable-basics.md](../part-01-cache/109-01-cacheable-basics.md)

<!-- BOOKIFY:END -->
