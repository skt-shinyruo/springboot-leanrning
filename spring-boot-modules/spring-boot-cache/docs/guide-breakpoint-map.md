# 04. 断点地图（Cache）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Cache）展开，主线可以概括为：`@Cacheable/@CachePut/@CacheEvict` → AOP 拦截（CacheInterceptor）→ key 计算/SpEL → CacheManager 命中 cache → get/put/evict。

    先跑 `BootCacheBranchMatrixLabTest` 固化“命中/不命中/条件/同步”的断言，再沿 `CacheInterceptor` 断点观察 key、condition/unless 与缓存写入时机。

    需要下探源码时，可以从 `org.springframework.cache.interceptor.CacheInterceptor` / `org.springframework.cache.interceptor.CacheAspectSupport` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Cache](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- 本章收束点：把缓存排障的关键问题变成可观察：**key 是什么**、**命中了哪个 cache**、**condition/unless 是否阻止缓存**、**sync 是否生效**。
- 证据链方法：用 invocationCount/trace 作为证据链（先证明有没有走到目标方法）。

## 运行入口（先运行）

- Book Matrix：`BootCacheBookMatrixLabTest`
- Branch Matrix：`BootCacheBranchMatrixLabTest`

运行命令：

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`

## 框架侧断点（优先）

- `org.springframework.cache.interceptor.CacheInterceptor#invoke`
- `org.springframework.cache.interceptor.CacheAspectSupport#execute`
- `org.springframework.cache.interceptor.CacheAspectSupport#generateKey`
- `org.springframework.cache.interceptor.CacheAspectSupport#findCachedValue`

## SpEL 与条件断点

- `org.springframework.cache.interceptor.CacheOperationExpressionEvaluator#key`
- `org.springframework.cache.interceptor.CacheOperationExpressionEvaluator#condition`
- `org.springframework.cache.interceptor.CacheOperationExpressionEvaluator#unless`

## 观察点

- `cacheNames` / `cache`（命中哪个 cache）
- `key`（最终 key，尤其是 SpEL）
- `condition` / `unless` 的求值结果（true/false）
- 目标方法是否真正执行（用 `invocationCount` 或断点证明）

## 常见分支定位（与矩阵表配合）

- “预期命中但没有命中”：先看 key 是否相同（以及是否被拼接/序列化导致变化）。
- “condition/unless 不生效”：断点到 evaluator，观察表达式求值时机与变量内容。
- “并发下重复计算”：重点看 `sync=true` 是否生效（以及 cache provider 是否支持）。

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

`@Cacheable/@CachePut/@CacheEvict` → AOP 拦截（CacheInterceptor）→ key 计算/SpEL → CacheManager 命中 cache → get/put/evict。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootCacheBranchMatrixLabTest`
- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

