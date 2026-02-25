# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把缓存的关键边界（key/condition/unless/sync/expiry）收敛成可回归矩阵表；每行都能用测试复现并用断点观察。
    - 原理：缓存本质是“用 key 命中 value”，而分支来自“key 怎么算/何时写/是否跳过”。
    - 源码入口：`CacheInterceptor` / `CacheAspectSupport` / `CacheOperationExpressionEvaluator`
    - 推荐 Lab：`BootCacheBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Cache Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. `@Cacheable` 最小闭环](../part-01-cache/01-cacheable-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

缓存类问题通常不是“有没有缓存”，而是“**命中谁、key 是什么、为什么没写进去/被踢掉**”。本页给出最小矩阵：

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| same key 命中 | 同 key 重复调用 | 第二次不再执行目标方法 | `BootCacheBranchMatrixLabTest` / `BootCacheLabTest` | `key` / invocationCount |
| different key 不命中 | 不同 key 调用 | 两次都执行目标方法并写入不同 entry | `BootCacheLabTest` | `key` / cache entries |
| SpEL key | `key = "#name + ':' + #lang"` | key 不同 → entry 独立 | `BootCacheSpelKeyLabTest` | `CacheOperationExpressionEvaluator#key` |
| condition/unless | condition=false 或 unless=true | 不写缓存（每次都执行） | `BootCacheLabTest` | evaluator 结果 |
| sync=true 防重复 | 并发同 key | 只计算一次 | `BootCacheLabTest` | `sync` / 并发证据 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`

## 调试路线（建议）

- 第 1 站：`CacheAspectSupport#generateKey`（确认 key）
- 第 2 站：`CacheAspectSupport#findCachedValue`（确认命中）
- 第 3 站：`CacheOperationExpressionEvaluator`（确认 condition/unless）

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：缓存本质是“用 key 命中 value”，而分支来自“key 怎么算/何时写/是否跳过”。
- 下一章：[第 109 章：01：@Cacheable 基础与命中语义](../part-01-cache/01-cacheable-basics.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootCacheBranchMatrixLabTest`
- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-cache/01-cacheable-basics.md](../part-01-cache/01-cacheable-basics.md)

<!-- BOOKIFY:END -->

