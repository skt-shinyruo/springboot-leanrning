# 01. 主线时间线：Spring Boot Cache
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（缓存主线怎么排）"

    缓存这件事在项目里从来不是“加个注解就完事”：它本质是一套分支系统（命中/未命中、写入/失效、是否缓存、并发收敛、何时过期）。这条时间线的目的，是把分支按“你最常踩坑的顺序”排好，顺读一遍就能覆盖 80% 的实际问题。

    - 推荐先跑：`BootCacheBookMatrixLabTest`
    - 核心证据链：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 106 章：Cache 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 00 - Deep Dive Guide（springboot-cache）](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01. 主线时间线：Spring Boot Cache**
- 建议入口：优先运行 `BootCacheBookMatrixLabTest`（见文末“对应 Lab/Test”），把主线现象先跑通，再按本章顺序补齐每条分支的机制与代价。



## 这条主线解决什么问题

你在项目里做缓存，大概率会遇到这些问题：

- “我以为方法会执行，怎么只执行了一次？”
- “我更新了数据，为什么缓存还在？”
- “为什么这个请求不走缓存？”
- “并发一上来就打爆下游，`sync=true` 到底救不救得了？”
- “缓存什么时候过期？怎么写出不靠 `sleep` 的稳定测试？”

这模块的章节顺序就是按上面的排障顺序来的。

## 主线顺读（5 章）

1. `@Cacheable`：先把“命中短路”讲清楚  
   - [第 109 章：01：`@Cacheable` 最小闭环](../part-01-cache/01-cacheable-basics.md)
2. `@CachePut/@CacheEvict`：再把写路径（更新/失效）讲清楚  
   - [第 110 章：02：`@CachePut/@CacheEvict`：更新与失效](../part-01-cache/02-cacheput-and-evict.md)
3. key / condition / unless：缓存边界在哪里  
   - [第 111 章：03：key / condition / unless：缓存边界](../part-01-cache/03-key-condition-unless.md)
4. `sync=true`：并发击穿（stampede）怎么收敛  
   - [第 112 章：04：`sync=true`：防缓存击穿（stampede）](../part-01-cache/04-sync-stampede.md)
5. 过期与可测性：用 Ticker 把“时间推进”变成可控输入  
   - [第 113 章：05：过期与可测性：用 Ticker 控制时间](../part-01-cache/05-expiry-with-ticker.md)

## 推荐先跑的入口（把主线变成事实）

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`

## 小结与下一章

- 下一章是 Deep Dive Guide：告诉你这模块“怎么读”更省时间、哪些证据入口最值得先跑。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootCacheBookMatrixLabTest`
- Lab：`BootCacheLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md)

<!-- BOOKIFY:END -->
