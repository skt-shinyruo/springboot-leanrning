# 第 115 章：99 - Self Check（springboot-cache）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-cache）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Cacheable/@CachePut/@CacheEvict` 声明缓存意图；根据 key/condition/unless 设计缓存命中与一致性策略。
    - 原理：方法调用 → AOP 代理 → CacheInterceptor → key 计算（KeyGenerator/SpEL）→ 命中短路/不命中回源 → 回写/失效。
    - 源码入口：`org.springframework.cache.interceptor.CacheInterceptor` / `org.springframework.cache.interceptor.CacheAspectSupport` / `org.springframework.cache.interceptor.KeyGenerator` / `org.springframework.cache.CacheManager`
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 114 章：90：常见坑清单（Cache）](114-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 116 章：Async/Scheduling 主线](../../../book/116-async-scheduling-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/108-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/108-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-cache）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`

## 机制主线


## 自测题
1. `@Cacheable` 与 `@CachePut` 的差异是什么？为什么 `@CachePut` 不会短路？
2. `condition` 与 `unless` 的差异是什么？分别在什么时候评估？
3. `sync=true` 的实现思路是什么？为什么它只对同一个 key 生效？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
- 建议命令：`mvn -pl :spring-boot-cache test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 对应 Exercise（可运行）

- `BootCacheExerciseTest`

## 常见坑与边界

### 坑点 1：只看“缓存命中感觉变快”，但没有证据链，导致线上行为不可解释

- Symptom：线上出现“偶发慢/偶发打 DB”，但你无法解释是 key/condition/unless/sync/过期哪一条分支导致
- Root Cause：缓存机制本质是分支系统；没有测试证据链时，你只能靠猜
- Verification（建议把这些作为排障兜底入口）：
  - 命中短路证据：`BootCacheLabTest#cacheableCachesResultForSameKey`
  - 不缓存分支证据：`BootCacheLabTest#conditionPreventsCachingWhenFalse` / `BootCacheLabTest#unlessPreventsCachingBasedOnResult`
  - 防击穿证据：`BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`
  - 过期证据：`BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`
- Fix：把关键分支写成默认 Lab（像本模块一样），遇到问题先跑断言复现，再改配置/代码

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
- Exercise：`BootCacheExerciseTest`

上一章：[appendix/90-common-pitfalls.md](114-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
