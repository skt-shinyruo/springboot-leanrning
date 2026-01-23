# 第 108 章：01：Cache 调用链（@Cacheable → CacheInterceptor → CacheManager）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Cache 调用链（@Cacheable → CacheInterceptor → CacheManager）
    - 怎么使用：先跑 `BootCacheLabTest`，把“同 key 命中缓存”固化成断言，再按本文从代理入口走到 `CacheInterceptor` 的分支。
    - 原理：缓存是 AOP 拦截器：调用进入代理 → `CacheInterceptor` 解析 cache operation → 生成 key → 从 CacheManager 读写。
    - 源码入口：`CacheInterceptor` / `CacheAspectSupport#execute` / `KeyGenerator` / `CacheManager`
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 108 章：00. 深挖导读](108-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 108 章：02：断点地图](108-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 最短调用链

1. 调用进入代理（AOP）
2. `CacheInterceptor#invoke`
3. `CacheAspectSupport#execute` 解析 cache operation
4. 生成 key（SpEL/KeyGenerator）
5. 通过 `CacheManager` 获取 cache 并读写

证据链入口：

- `BootCacheLabTest` / `BootCacheSpelKeyLabTest`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`
- Lab：`BootCacheSpelKeyLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](108-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](108-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
