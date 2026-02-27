# Spring Boot Cache：缓存语义、key 与并发边界

本模块以缓存命中/回源为起点，逐步把写路径（更新/失效）、key 与条件表达式、以及并发场景下的缓存击穿（stampede）压成可验证的分支。重点不在注解清单，而在缓存语义与边界：哪些情况下“看起来没命中”，哪些情况下“命中了但不该命中”，以及如何用测试把这些差异固定下来。

---

## 10 分钟入口：先跑通一次命中与回源

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`

运行后应能回答：一次读请求的“命中/回源/回写”分别发生在什么位置；当写路径触发时（`@CachePut/@CacheEvict`），缓存状态如何变化。

---

## 阅读路线（主线 → 排障 → 自证）

1. 建立主线坐标
   - [主线时间线](part-00-guide/01-mainline-timeline.md)
   - [深挖导读](part-00-guide/02-deep-dive-guide.md)
2. 顺读正文（按语义递进）
   - [@Cacheable 基础](part-01-cache/01-cacheable-basics.md)
   - [@CachePut/@CacheEvict](part-01-cache/02-cacheput-and-evict.md)
   - [key/condition/unless](part-01-cache/03-key-condition-unless.md)
   - [`sync=true` 与击穿](part-01-cache/04-sync-stampede.md)
   - [过期语义（用 Ticker 控制时间）](part-01-cache/05-expiry-with-ticker.md)
3. 遇到问题时回到排障入口
   - [断点地图](part-00-guide/04-breakpoint-map.md)
   - [关键分支矩阵](part-00-guide/05-branch-decision-matrix.md)
   - [常见坑](appendix/01-common-pitfalls.md) / [自检](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-cache -Dtest=*ExerciseSolutionTest test`
- 并发/性能（击穿可断言复现）：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheStampedeProtectionLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
