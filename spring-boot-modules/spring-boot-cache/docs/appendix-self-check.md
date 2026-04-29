# 99 自检：Spring Boot Cache
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（复盘题）"

    这章不加新概念，只帮助复盘主线：读路径、写路径、边界分支、并发收敛与过期可测。每题都能落到 tests；答不上来就去跑对应入口，把结论变成事实。

    - 主线入口：`BootCacheBookMatrixLabTest`
    - 分支入口：`BootCacheBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（Cache）](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootCacheBookMatrixLabTest`（见文末“对应实验/测试”），先跑一遍主线最小集合，再按题目回到对应证据入口逐一验证。


## 先跑入口（比做纸面题更快）

- Book Matrix：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)

## 自测题（每题都能落到 tests）

1. `@Cacheable` 命中后方法体会不会执行？用哪个断言证明？
   - 入口：`BootCacheLabTest#cacheableCachesResultForSameKey`
2. 不同 key 会不会共用 entry？如何用断言证明它们分开？
   - 入口：`BootCacheLabTest#cacheableUsesDifferentEntriesForDifferentKeys`
3. `@CachePut` 与 `@Cacheable` 的差异是什么？为什么 `@CachePut` 不会短路？
   - 入口：`BootCacheLabTest#cachePutUpdatesCacheValue`
4. `@CacheEvict` 会带来什么现象？如何证明“下一次会重新计算”？
   - 入口：`BootCacheLabTest#cacheEvictRemovesEntry`
5. condition 与 unless 的差异是什么？分别在什么时候评估？
   - 入口：`BootCacheLabTest#conditionPreventsCachingWhenFalse` / `BootCacheLabTest#unlessPreventsCachingBasedOnResult`
6. 如何用 SpEL key 让 `alice/en` 与 `alice/zh` 进入不同 entry？
   - 入口：`BootCacheSpelKeyLabTest#spelKeyCreatesIndependentCacheEntries`
7. `sync=true` 解决的是什么问题？它的边界是什么？
   - 入口：`BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`
8. 为什么用 sleep 测 TTL 容易 flaky？用什么方式把“时间推进”变成可控输入？
   - 入口：`BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`
9. 如果线上出现“偶发慢/偶发打 DB”，优先采用的排障顺序是什么？（三步即可）
   - 对照：`01-common-pitfalls.md`

## 小结

- 这模块的核心心智模型就一句话：缓存是分支系统。越早把分支写成断言，越少依赖预期和日志猜测。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
- Exercise：`BootCacheExerciseTest`

上一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)

<!-- BOOKIFY:END -->
