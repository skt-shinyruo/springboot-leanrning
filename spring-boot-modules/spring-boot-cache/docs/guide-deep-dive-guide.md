# 深挖导读：Spring Boot Cache
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（怎么读缓存）"

    这模块想把缓存从“注解背诵题”变成可回归的机制结论：命中短路、写入/失效、key 维度、condition/unless 分支、并发收敛、以及过期的可测试性。跑完一组 Lab，就能把线上常见的争论变成断言（到底走没走方法、到底缓存了没、到底谁在等谁）。

    - 主线入口：`BootCacheBookMatrixLabTest`
    - 关键证据：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Boot Cache](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. `@Cacheable` 最小闭环](cache-cacheable-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootCacheBookMatrixLabTest`（见文末“对应实验/测试”），用最小集合把“命中/写入/边界/并发/过期”跑成事实，再回到正文系统化串起来。


## 无需背 API，但要抓住“缓存本质上是一套分支系统”

缓存的难点往往不是“不清楚 `@Cacheable` 怎么写”，而是不知道自己现在处于哪条分支：

- 命中（方法不执行）还是未命中（方法执行 + 回写）？
- 这次到底用的 key 是什么？是不是把不同请求挤进了同一个 entry？
- `condition` 把请求挡在缓存逻辑之外了吗？`unless` 把结果挡在回写之外了吗？
- 并发时到底是“重复计算”还是“等待同一个结果”？等待成本能不能接受？
- 过期到底什么时候发生？如何在测试里确定性地证明它发生了？

这模块的写法就是：把每条分支都写成可断言的最小复现。

## 两条阅读路线

### 路线 A：顺读主线（5 章）

按 109 → 113 顺读即可：先读缓存命中，再读写路径，再读边界分支，最后进入并发与过期。

### 路线 B：正在排障（快速收敛）

1. 先跑 `BootCacheLabTest`（看 invocationCount，把“方法到底有没有执行”先钉住）
2. 再看 [断点地图](guide-breakpoint-map.md)（把“命中/未命中/不缓存/不回写”分清楚）
3. 再对照 [关键分支矩阵](guide-branch-decision-matrix.md)（把分支写成 If/Then）

## 机制主线（只记 5 句话）

1. **缓存发生在方法边界（AOP）**：代理拦截 → 算 key → 查 cache → 命中短路/未命中回源 →（可选）回写/失效。
2. **`@Cacheable` 是读路径**：命中后方法体不会执行。
3. **`@CachePut/@CacheEvict` 是写路径**：更新/失效是“显式表达的意图”，不是系统自动推导。
4. **key/condition/unless 决定缓存边界**：key 决定“缓存维度”；condition/unless 决定“哪些请求/哪些结果不缓存”。
5. **并发与过期要可测试**：并发用 latch 固定分支；过期用 `Ticker` 把时间推进变成可控输入（用可控时间替代 sleep）。

## 先运行的入口（少而全）

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`

## 小结与下一章

- 下一章从 `@Cacheable` 开始：先把“命中短路”讲清楚，否则后面所有坑都会像谜语。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootCacheLabTest` / `BootCacheSpelKeyLabTest`
- Exercise：`BootCacheExerciseTest`

上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[cache-cacheable-basics.md](cache-cacheable-basics.md)

<!-- BOOKIFY:END -->
