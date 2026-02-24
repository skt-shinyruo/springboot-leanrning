# 第 115 章：99 - Self Check（springboot-cache）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘题）"

    这章不加新概念，只帮你复盘主线：读路径、写路径、边界分支、并发收敛与过期可测。每题都能落到 tests；答不上来就去跑对应入口，把结论变成事实。

    - 主线入口：`BootCacheBookMatrixLabTest`
    - 分支入口：`BootCacheBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 114 章：90：常见坑清单（Cache）](114-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（比做纸面题更快）

- Book Matrix：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`

## 自测题（每题都能落到 tests）

1. `@Cacheable` 命中后方法体会不会执行？你用哪个断言证明？  
   - 入口：`BootCacheLabTest#cacheableCachesResultForSameKey`
2. 不同 key 会不会共用 entry？你如何用断言证明它们分开？  
   - 入口：`BootCacheLabTest#cacheableUsesDifferentEntriesForDifferentKeys`
3. `@CachePut` 与 `@Cacheable` 的差异是什么？为什么 `@CachePut` 不会短路？  
   - 入口：`BootCacheLabTest#cachePutUpdatesCacheValue`
4. `@CacheEvict` 会带来什么现象？你如何证明“下一次会重新计算”？  
   - 入口：`BootCacheLabTest#cacheEvictRemovesEntry`
5. condition 与 unless 的差异是什么？分别在什么时候评估？  
   - 入口：`BootCacheLabTest#conditionPreventsCachingWhenFalse` / `BootCacheLabTest#unlessPreventsCachingBasedOnResult`
6. 你如何用 SpEL key 让 `alice/en` 与 `alice/zh` 进入不同 entry？  
   - 入口：`BootCacheSpelKeyLabTest#spelKeyCreatesIndependentCacheEntries`
7. `sync=true` 解决的是什么问题？它的边界是什么？  
   - 入口：`BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`
8. 为什么用 sleep 测 TTL 容易 flaky？你用什么方式把“时间推进”变成可控输入？  
   - 入口：`BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`
9. 如果线上出现“偶发慢/偶发打 DB”，你最推荐的排障顺序是什么？（三步即可）  
   - 对照：`114-90-common-pitfalls.md`

## 小结

- 这模块的核心心智模型就一句话：缓存是分支系统。你越早把分支写成断言，越少靠直觉和日志猜。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
- Exercise：`BootCacheExerciseTest`

上一章：[appendix/90-common-pitfalls.md](114-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
