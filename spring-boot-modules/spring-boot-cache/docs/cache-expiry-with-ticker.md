# 05. 过期与可测性：用 Ticker 控制时间
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（用可控时间替代 sleep）"

    TTL/过期是缓存里最容易写出 flaky 测试的地方：用真实时间 + `Thread.sleep`，在 CI 上迟早会崩。这个章节的核心是把“时间推进”变成可控输入：用 Caffeine `Ticker`（如 `ManualTicker`）写出确定性断言。

    - 证据入口：`BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. `sync=true`：防缓存击穿（stampede）](cache-sync-stampede.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 常见坑清单（Cache）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`（见文末“对应实验/测试”），把“时间推进”变成可控输入，再写确定性断言。


## 想验证的本质上不是“等 5 秒”，而是“过期发生了”

在这模块里，缓存配置（Caffeine）有过期策略（`expireAfterWrite`）。真实项目里当然可以靠时间等它过期，但在测试里，靠 sleep 等是最不稳定的方式。

Ticker 的核心价值是：把“现在是什么时间”变成一个可注入的依赖。

## 机制主线（把时间变成输入）

如果能控制“时间推进”，就能写出确定性断言：

1. 第一次调用：未命中 → 执行方法 → 写入 cache
2. 推进时间（例如 +10s）
3. 第二次调用：因为 TTL 已过期 → 再次执行方法 → 回写

## 怎么验证（最短证据链）

- `BootCacheLabTest#expiryCanBeTestedDeterministicallyWithManualTicker`

观察点：

- 两次返回值不同
- invocationCount 从 1 变成 2（第二次确实回源了）

## 常见坑与边界

### 坑点 1：用真实时间 + sleep 测 TTL

这类测试要么慢，要么 flaky，最糟糕的是又慢又 flaky。

修法：

- 用 `Ticker`（例如 `ManualTicker`）让时间推进变成可控输入

## 小结与下一章

- 主线到这里结束，附录会把最常见的“缓存误解”整理成排障短文与自测题。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootCacheLabTest`

上一章：[cache-sync-stampede.md](cache-sync-stampede.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
