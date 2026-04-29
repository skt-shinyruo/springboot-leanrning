# 01. 主线时间线：Spring Boot Cache
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（缓存主线怎么排）"

    缓存这件事在项目里从来不是“加个注解就完事”：它本质是一套分支系统（命中/未命中、写入/失效、是否缓存、并发收敛、何时过期）。这条时间线的目的，是把分支按“最常踩坑的顺序”排好，顺读一遍就能覆盖 80% 的实际问题。

    - 入口：`BootCacheBookMatrixLabTest`
    - 核心证据链：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Cache 主线](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[深挖导读：Spring Boot Cache](guide-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootCacheBookMatrixLabTest`（见文末“对应实验/测试”），把主线现象先跑通，再按本章顺序补齐每条分支的机制与代价。


## 这条主线解决什么问题

在项目里做缓存，大概率会遇到这些问题：

- “预期方法会执行，为什么只执行了一次？”
- “数据已经更新，为什么缓存还在？”
- “为什么这个请求不走缓存？”
- “并发一上来就打爆下游，`sync=true` 到底救不救得了？”
- “缓存什么时候过期？怎么写出不靠 `sleep` 的稳定测试？”

这模块的章节顺序就是按上面的排障顺序来的。

## 主线顺读（5 章）

1. `@Cacheable`：先把“命中短路”讲清楚
   - [01：`@Cacheable` 最小闭环](cache-cacheable-basics.md)
2. `@CachePut/@CacheEvict`：再把写路径（更新/失效）讲清楚
   - [02：`@CachePut/@CacheEvict`：更新与失效](cache-cacheput-and-evict.md)
3. key / condition / unless：缓存边界在哪里
   - [03：key / condition / unless：缓存边界](cache-key-condition-unless.md)
4. `sync=true`：并发击穿（stampede）怎么收敛
   - [04：`sync=true`：防缓存击穿（stampede）](cache-sync-stampede.md)
5. 过期与可测性：用 Ticker 把“时间推进”变成可控输入
   - [05：过期与可测性：用 Ticker 控制时间](cache-expiry-with-ticker.md)

## 先运行的入口（把主线变成事实）

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`

## 小结与下一章

- 下一章是 深挖导读：告诉这模块“怎么读”更快收敛、哪些证据入口最值得先跑。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootCacheBookMatrixLabTest`
- Lab：`BootCacheLabTest`

上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md)

<!-- BOOKIFY:END -->
