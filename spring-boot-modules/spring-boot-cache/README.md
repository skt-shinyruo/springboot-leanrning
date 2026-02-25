# spring-boot-cache

本模块用于学习缓存：Spring Cache 抽象（`@Cacheable/@CachePut/@CacheEvict`）、key/condition/unless、`sync=true`、以及基于 Caffeine 的可控过期实验。

本模块以 tests-first 为主，不启动 Web 服务（`spring.main.web-application-type=none`）。

## 关键命令

```bash
mvn -pl :spring-boot-cache test
```

## 推荐 docs 阅读顺序

（docs 目录页：[`docs/README.md`](docs/README.md)）

1. [`@Cacheable` 最小闭环](docs/part-01-cache/01-cacheable-basics.md)
2. [`@CachePut/@CacheEvict`：更新与失效](docs/part-01-cache/02-cacheput-and-evict.md)
3. [key/condition/unless：缓存边界](docs/part-01-cache/03-key-condition-unless.md)
4. [`sync=true`：防缓存击穿（stampede）](docs/part-01-cache/04-sync-stampede.md)
5. [过期与可测性：用 Ticker 控制时间](docs/part-01-cache/05-expiry-with-ticker.md)
6. [常见坑清单](docs/appendix/01-common-pitfalls.md)

## Labs / Exercises 索引

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java` | Cacheable/Put/Evict、key/condition/unless、sync、过期（可控） | ⭐⭐ | 逐个跑测试并对照 docs |
| Exercise | `src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseTest.java` | 扩展更多缓存一致性与边界场景 | ⭐⭐–⭐⭐⭐ | 从“多参数 key”开始 |
