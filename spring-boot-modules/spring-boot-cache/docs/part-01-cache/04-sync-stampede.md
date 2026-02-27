# 04. `sync=true`：防缓存击穿（stampede）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（并发收敛）"

    `sync=true` 解决的是很具体的问题：并发请求同一个 key 时，让底层计算只发生一次；其他线程等待同一个结果。它能避免 stampede，但也会把并发变串行——这章把“收敛发生了”写成断言，同时把代价说清楚。

    - 证据入口：`BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. key / condition / unless：缓存边界](03-key-condition-unless.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 过期与可测性：用 Ticker 控制时间](05-expiry-with-ticker.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. `sync=true`：防缓存击穿（stampede）**
- 建议入口：优先运行 `BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`（见文末“对应 Lab/Test”），先把“同 key 只算一次”钉住，再评估等待成本是否可接受。



## 这类问题一定见过：并发一上来，下游就被打爆

缓存的命中路径很快，但未命中路径往往很重（回源 DB/远程调用）。当某个热点 key 在同一时间被并发请求，如果每个线程都去做一次“未命中计算”，会得到：

- 下游被瞬时放大打爆
- 服务线程也被拖慢

`sync=true` 的目的就是把“同 key 的并发未命中”收敛成一次。

## 机制主线（对同一个 key 收敛）

可以把 `sync=true` 的语义理解成：

- 同一个 key：只有一个线程负责计算并写入
- 其他线程：等待第一个线程的结果

注意它的边界：

- **只对同一个 key 生效**，不是全局锁
- 它会引入等待成本（吞吐与延迟要权衡）

## 怎么验证（把“并发收敛”写成断言）

- `BootCacheLabTest#syncTrueAvoidsDuplicateComputationsForSameKey`

这个测试的关键不是耗时，而是：

- 两个线程拿到同一个结果
- 底层方法只执行了一次（invocations=1）

## 常见坑与边界

### 坑点 1：把 `sync=true` 当成万能并发方案

`sync=true` 不是“让缓存更快”，它是“用等待换重复计算”。如果的计算很轻、或者 key 分布非常散，开启它可能只会增加等待与资源占用。

## 小结与下一章

- 下一章进入过期与可测性：缓存的另一个大坑是“到底什么时候失效”，以及如何写出不 flaky 的 TTL 测试。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`

上一章：[part-01-cache/03-key-condition-unless.md](03-key-condition-unless.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-cache/05-expiry-with-ticker.md](05-expiry-with-ticker.md)

<!-- BOOKIFY:END -->
