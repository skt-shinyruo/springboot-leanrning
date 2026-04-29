# 01. 常见坑清单（Cache）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（排障短文）"

    缓存的坑很少是“注解写错”，更多是语义误解：把缓存当数据库、把 key 当默认、把 `sync=true` 当万能锁、用 sleep 测 TTL。这里把最常见的坑写成可复现的入口，便于把争论变成断言。

    - 排障入口：`BootCacheBranchMatrixLabTest` + [断点地图](guide-breakpoint-map.md)
    - 核心证据：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 过期与可测性：用 Ticker 控制时间](cache-expiry-with-ticker.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[自检题](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试 收束答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 导读

优先运行 `BootCacheBranchMatrixLabTest`（见文末“对应实验/测试”），把“命中/写入/不缓存/并发/过期”这些分支先跑出来，再回到本章逐条对照误区。


## 缓存问题的排查路径

缓存排障更快收敛的三步是：

1. **先确认方法有没有执行**（命中短路是最常见的“少了一次调用”）
2. **再确认 key/分支**（到底算了什么 key？condition/unless 把挡在哪？）
3. **最后再谈策略**（put/evict、sync、TTL——都属于策略层）

## 把 `@Cacheable` 方法当成“每次都会执行”

证据入口：

- `BootCacheLabTest#cacheableCachesResultForSameKey`

为什么危险：

- 把日志/计数/副作用藏在缓存方法里，会被命中短路跳过

## 误解 `@CachePut`：以为它也会短路

证据入口：

- `BootCacheLabTest#cachePutUpdatesCacheValue`

一句话修正：

- `@CachePut` 的语义就是“强制执行 + 更新缓存”，它不负责短路

## condition vs unless：把“前置过滤”与“结果过滤”混在一起

证据入口：

- `BootCacheLabTest#conditionPreventsCachingWhenFalse`
- `BootCacheLabTest#unlessPreventsCachingBasedOnResult`

## key 没想清楚：不同维度挤进同一个 entry

证据入口（SpEL key）：

- `BootCacheSpelKeyLabTest#spelKeyCreatesIndependentCacheEntries`

## `sync=true` 的边界没想清楚

证据入口：

- `BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`

常见误会：

- `sync=true` 不是全局锁，只对同一个 key 收敛
- 它会引入等待成本（吞吐与延迟要权衡）

## 用 `Thread.sleep` 测 TTL（flaky 的经典来源）

证据入口：

- `BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`

## 小结与下一章

- 如果能把本页每个坑对应到一个可复现入口，并且能用一两句人话解释原因，缓存这条线就不会再像不可解释。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`

上一章：[cache-expiry-with-ticker.md](cache-expiry-with-ticker.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
