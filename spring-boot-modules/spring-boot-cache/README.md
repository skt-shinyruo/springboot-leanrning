# spring-boot-cache

本模块用于学习缓存：Spring Cache 抽象（`@Cacheable/@CachePut/@CacheEvict`）、key/condition/unless、`sync=true`、以及基于 Caffeine 的可控过期实验。

本模块以 tests-first 为主，不启动 Web 服务（`spring.main.web-application-type=none`）。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix-self-check.md`](docs/appendix-self-check.md)

## 关键命令

```bash
mvn -pl :spring-boot-cache test
```

## docs 阅读顺序

（目录：见本 README 的「目录（唯一顺序来源）」）

1. [`@Cacheable` 最小闭环](docs/cache-cacheable-basics.md)
2. [`@CachePut/@CacheEvict`：更新与失效](docs/cache-cacheput-and-evict.md)
3. [key/condition/unless：缓存边界](docs/cache-key-condition-unless.md)
4. [`sync=true`：防缓存击穿（stampede）](docs/cache-sync-stampede.md)
5. [过期与可测性：用 Ticker 控制时间](docs/cache-expiry-with-ticker.md)
6. [常见坑清单](docs/appendix-common-pitfalls.md)

## 实验/练习索引

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。练习默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java` | Cacheable/Put/Evict、key/condition/unless、sync、过期（可控） | ⭐⭐ | 逐个跑测试并对照 docs |
| Exercise | `src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseTest.java` | 扩展更多缓存一致性与边界场景 | ⭐⭐–⭐⭐⭐ | 从“多参数 key”开始 |

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Cache：缓存语义、key 与并发边界

本模块以缓存命中/回源为起点，逐步把写路径（更新/失效）、key 与条件表达式、以及并发场景下的缓存击穿（stampede）压成可验证的分支。重点不在注解清单，而在缓存语义与边界：哪些情况下“表面上没命中”，哪些情况下“命中了但不该命中”，以及如何用测试把这些差异固定下来。

---

### 10 分钟入口：先跑通一次命中与回源
- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`

运行后应能回答：一次读请求的“命中/回源/回写”分别发生在什么位置；当写路径触发时（`@CachePut/@CacheEvict`），缓存状态如何变化。

---

### 阅读路线（主线 → 排障 → 自证）
1. 建立主线坐标
   - [主线时间线](docs/guide-mainline-timeline.md)
   - [深挖导读](docs/guide-deep-dive-guide.md)
2. 顺读正文（按语义递进）
   - [@Cacheable 基础](docs/cache-cacheable-basics.md)
   - [@CachePut/@CacheEvict](docs/cache-cacheput-and-evict.md)
   - [key/condition/unless](docs/cache-key-condition-unless.md)
   - [`sync=true` 与击穿](docs/cache-sync-stampede.md)
   - [过期语义（用 Ticker 控制时间）](docs/cache-expiry-with-ticker.md)
3. 遇到问题时回到排障入口
   - [断点地图](docs/guide-breakpoint-map.md)
   - [关键分支矩阵](docs/guide-branch-decision-matrix.md)
   - [常见坑](docs/appendix-common-pitfalls.md) / [自检](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-boot-cache -Dtest=*ExerciseSolutionTest test`
- 并发/性能（击穿可断言复现）：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheStampedeProtectionLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
