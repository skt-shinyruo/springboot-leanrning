# 03. key / condition / unless：缓存边界
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（边界分支）"

    很多缓存 bug 不是“缓存不工作”，而是“缓存维度与边界不对”：key 把不同请求挤进同一个 entry；condition/unless 把以为会缓存的情况排除掉。这个章节把这些分支写成断言，避免只凭预期推断。

    - 证据入口：`BootCacheLabTest#conditionPreventsCachingWhenFalse` / `BootCacheLabTest#unlessPreventsCachingBasedOnResult`
    - SpEL key 入口：`BootCacheSpelKeyLabTest#spelKeyCreatesIndependentCacheEntries`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. `@CachePut/@CacheEvict`：更新与失效](cache-cacheput-and-evict.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. `sync=true`：防缓存击穿（stampede）](cache-sync-stampede.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootCacheSpelKeyLabTest#spelKeyCreatesIndependentCacheEntries`（见文末“对应实验/测试”），先把“key 决定维度”跑成事实，再补齐 condition/unless 的边界分支。


## 先建立判断：key 决定“维度”，condition/unless 决定“边界”

- key：决定在 cache 里“按什么维度存”
- condition：决定“要不要进入缓存逻辑”（在方法执行前）
- unless：决定“要不要把结果写回缓存”（在方法执行后）

如果把这三件事混在一起，缓存行为会非常像“不可解释”。

### 1) key：把调用映射成 cache entry

默认 key 规则很多人记不住也没关系；真正重要的是：**能不能用测试证明“哪些调用会共用 entry”**。

本模块提供了一个最直观的 SpEL key 证据链：把两参拼成一个 key，让 `alice/en` 与 `alice/zh` 进入不同 entry：

- `BootCacheSpelKeyLabTest#spelKeyCreatesIndependentCacheEntries`

### 2) condition：在方法执行前分流

如果 condition 为 false，这次调用根本不会走“读缓存/写缓存”的分支（所以每次都会计算）。

证据入口：

- `BootCacheLabTest#conditionPreventsCachingWhenFalse`

### 3) unless：在方法执行后分流

unless 的常见用法是“结果不合格就不缓存”（例如空值/默认值/错误码）。

证据入口：

- `BootCacheLabTest#unlessPreventsCachingBasedOnResult`

## 常见坑与边界

### 坑点 1：把 condition 与 unless 当成一回事

如果把它们当成同一种过滤器，就会得到两类反常见预期的现象：

- “预期不会走缓存，结果命中了”
- “预期会缓存，结果每次都在计算”

修法也很直接：把“前置过滤”和“结果过滤”分开设计，并用测试把分支锁定。

## 小结与下一章

- 下一章进入并发：`sync=true` 解决的是“并发同 key 重复计算”，但它的代价同样是真实存在的。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootCacheLabTest`

上一章：[cache-cacheput-and-evict.md](cache-cacheput-and-evict.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[cache-sync-stampede.md](cache-sync-stampede.md)

<!-- BOOKIFY:END -->
