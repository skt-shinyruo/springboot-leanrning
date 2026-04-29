# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：缓存本质是“用 key 命中 value”，而分支来自“key 怎么算/何时写/是否跳过”。

    把缓存的关键边界（key/condition/unless/sync/expiry）收敛成可回归矩阵表；每行都能用测试复现并用断点观察。

    对照入口：`BootCacheBranchMatrixLabTest`。需要下探源码时，可以从 `CacheInterceptor` / `CacheAspectSupport` / `CacheOperationExpressionEvaluator` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Cache）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. `@Cacheable` 最小闭环](cache-cacheable-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

缓存类问题通常不是“有没有缓存”，而是“**命中谁、key 是什么、为什么没写进去/被踢掉**”。本页给出最小矩阵：

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| same key 命中 | 同 key 重复调用 | 第二次不再执行目标方法 | `BootCacheBranchMatrixLabTest` / `BootCacheLabTest` | `key` / invocationCount |
| different key 不命中 | 不同 key 调用 | 两次都执行目标方法并写入不同 entry | `BootCacheLabTest` | `key` / cache entries |
| SpEL key | `key = "#name + ':' + #lang"` | key 不同 → entry 独立 | `BootCacheSpelKeyLabTest` | `CacheOperationExpressionEvaluator#key` |
| condition/unless | condition=false 或 unless=true | 不写缓存（每次都执行） | `BootCacheLabTest` | evaluator 结果 |
| sync=true 防重复 | 并发同 key | 只计算一次 | `BootCacheLabTest` | `sync` / 并发证据 |

## 运行命令

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`

## 调试路线

- 第 1 站：`CacheAspectSupport#generateKey`（确认 key）
- 第 2 站：`CacheAspectSupport#findCachedValue`（确认命中）
- 第 3 站：`CacheOperationExpressionEvaluator`（确认 condition/unless）

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

缓存本质是“用 key 命中 value”，而分支来自“key 怎么算/何时写/是否跳过”。

下一章见：[01：@Cacheable 基础与命中语义](cache-cacheable-basics.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootCacheBranchMatrixLabTest`
- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[cache-cacheable-basics.md](cache-cacheable-basics.md)

<!-- BOOKIFY:END -->

