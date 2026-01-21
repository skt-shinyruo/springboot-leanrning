# Spring Boot Cache：目录

> 建议从 @Cacheable 起步，把命中/回源/回写跑通，再进入写路径、key/条件表达式与并发击穿。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/107-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/108-00-deep-dive-guide.md)

## 顺读主线

- [@Cacheable 基础](part-01-cache/109-01-cacheable-basics.md)
- [@CachePut/@CacheEvict](part-01-cache/110-02-cacheput-and-evict.md)
- [key/condition/unless](part-01-cache/111-03-key-condition-unless.md)
- [sync 与击穿](part-01-cache/112-04-sync-stampede.md)
- [过期语义](part-01-cache/113-05-expiry-with-ticker.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[108-02-breakpoint-map.md](part-00-guide/108-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[108-04-branch-decision-matrix.md](part-00-guide/108-04-branch-decision-matrix.md)
- 排障 playbook：[114-90-common-pitfalls.md](appendix/114-90-common-pitfalls.md)
- 自检清单：[115-99-self-check.md](appendix/115-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :springboot-cache -Dtest=BootCacheBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :springboot-cache -Dtest=BootCacheBranchMatrixLabTest test`

## 排坑与自检

- [常见坑](appendix/114-90-common-pitfalls.md)
- [自检](appendix/115-99-self-check.md)
