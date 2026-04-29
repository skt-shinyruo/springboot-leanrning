# 02. `@CachePut/@CacheEvict`：更新与失效
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（写路径）"

    如果 `@Cacheable` 是“读缓存”，那 `@CachePut/@CacheEvict` 就是“写缓存”：前者强制执行方法并更新缓存，后者删除缓存 entry（让下一次读取回源）。这章把它们的语义差异写成断言，避免线上只凭预期争论。

    - 最小证据入口：`BootCacheLabTest#cachePutUpdatesCacheValue` / `BootCacheLabTest#cacheEvictRemovesEntry`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. `@Cacheable` 最小闭环](cache-cacheable-basics.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[03. key / condition / unless：缓存边界](cache-key-condition-unless.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootCacheLabTest#cachePutUpdatesCacheValue`（见文末“对应实验/测试”），先把“put 不短路、evict 让下一次回源”的语义写成断言。


## 两句人话先说清楚

- `@CachePut`：**每次都会执行方法**，并把返回值写回 cache
- `@CacheEvict`：把 cache 里的某个 key（或整个 cache）清掉

它们解决的是一致性问题：当真实数据变了，需要怎么让缓存跟着变？

## 机制主线（写路径）

这章不展开拦截器内部细节，只需记住与 `@Cacheable` 的关键差异：

- `@Cacheable` 命中后短路（方法体不执行）
- `@CachePut` 不短路（方法体必执行）
- `@CacheEvict` 不关心返回值，它关心“删谁”

## 怎么验证（tests 就是事实）

- `@CachePut` 更新值：`BootCacheLabTest#cachePutUpdatesCacheValue`
- `@CacheEvict` 删除 entry：`BootCacheLabTest#cacheEvictRemovesEntry`

观察点仍然是最稳定的那个：

- `invocationCount()`（方法到底执行了几次）

## 常见坑与边界

### 坑点 1：把 `@CachePut` 当成 “Cacheable 也会短路”

现象：

- 以为“缓存命中就不会执行”，结果发现 `@CachePut` 每次都在跑（甚至多打了 DB）

证据入口：

- `BootCacheLabTest#cachePutUpdatesCacheValue`

修法（工程语义）：

- 需要短路：用 `@Cacheable`
- 需要更新：用 `@CachePut`
- 需要失效：用 `@CacheEvict`

## 小结与下一章

- 下一章进入“缓存边界”：key/condition/unless 决定哪些请求共用一个 entry，以及哪些请求/结果根本不缓存。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootCacheLabTest`

上一章：[cache-cacheable-basics.md](cache-cacheable-basics.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[cache-key-condition-unless.md](cache-key-condition-unless.md)

<!-- BOOKIFY:END -->
