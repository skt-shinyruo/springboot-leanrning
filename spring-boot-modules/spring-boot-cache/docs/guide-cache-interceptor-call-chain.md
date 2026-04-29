# 03. Cache 调用链（@Cacheable → CacheInterceptor → CacheManager）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：Cache 调用链（@Cacheable → CacheInterceptor → CacheManager）展开，主线可以概括为：缓存是 AOP 拦截器：调用进入代理 → `CacheInterceptor` 解析 cache operation → 生成 key → 从 CacheManager 读写。

    先跑 `BootCacheLabTest`，把“同 key 命中缓存”固化成断言，再按本章从代理入口走到 `CacheInterceptor` 的分支。

    需要下探源码时，可以从 `CacheInterceptor` / `CacheAspectSupport#execute` / `KeyGenerator` / `CacheManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Cache](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Cache）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootCacheLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：缓存是 AOP 拦截器：调用进入代理 → `CacheInterceptor` 解析 cache operation → 生成 key → 从 CacheManager 读写。需要下探源码时，可以从 `CacheInterceptor` / `CacheAspectSupport#execute` / `KeyGenerator` / `CacheManager` 这些入口切入。


## 最短调用链

1. 调用进入代理（AOP）
2. `CacheInterceptor#invoke`
3. `CacheAspectSupport#execute` 解析 cache operation
4. 生成 key（SpEL/KeyGenerator）
5. 通过 `CacheManager` 获取 cache 并读写

证据链入口：

- `BootCacheLabTest` / `BootCacheSpelKeyLabTest`

## 小结与下一章

缓存是 AOP 拦截器：调用进入代理 → `CacheInterceptor` 解析 cache operation → 生成 key → 从 CacheManager 读写。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootCacheLabTest`
- Lab：`BootCacheSpelKeyLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
