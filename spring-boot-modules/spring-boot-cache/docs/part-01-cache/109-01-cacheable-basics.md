# 第 109 章：01：`@Cacheable` 最小闭环
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（命中就短路）"

    这章只把一件事讲清楚：`@Cacheable` 命中后会 **直接返回缓存值**，方法体不会执行。很多线上“少了一次调用/少了一次日志”的争论，都从这里开始。

    - 最小证据入口：`BootCacheLabTest#cacheableCachesResultForSameKey`
    - 观察点：`invocationCount()`（方法到底有没有执行）
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 108 章：00 - Deep Dive Guide（springboot-cache）](../part-00-guide/108-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 110 章：02：`@CachePut/@CacheEvict`：更新与失效](110-02-cacheput-and-evict.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先从最常见的误会开始：你以为它每次都会执行

很多人第一次用缓存，会在方法里写日志/计数/副作用，以为“每次调用都会发生”。然后你会在某一天发现：日志怎么只打印了一次？

原因很简单：**命中后短路**。

## 机制主线（读路径）

把 `@Cacheable` 想成“读缓存”的声明：

1. 调用进入代理（`@EnableCaching` 建立的基础设施）
2. 计算 key（这章先用最简单的 key：`#name`）
3. 查 cache
   - 命中：直接返回缓存值（方法体不执行）
   - 未命中：执行方法体 → 得到结果 → 写入 cache → 返回结果

这章的所有结论都用一个稳定的证据来证明：`invocationCount()`。

## 怎么验证（最短证据链）

- 同一个 key 只计算一次：`BootCacheLabTest#cacheableCachesResultForSameKey`
- 不同 key 命中不同 entry：`BootCacheLabTest#cacheableUsesDifferentEntriesForDifferentKeys`

推荐命令：

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheLabTest test`

## 源码与断点（够用版）

- 命中/未命中分支发生点：`org.springframework.cache.interceptor.CacheAspectSupport#execute`
- 如果你怀疑 key 不对：先去下一章（key/SpEL），把维度写成断言再回来

## 常见坑与边界

### 坑点 1：把缓存方法当成“每次都会执行”的业务入口

证据入口：

- 命中短路：`BootCacheLabTest#cacheableCachesResultForSameKey`

修法（工程语义）：

- 缓存方法尽量是纯函数/无副作用
- 需要副作用的逻辑，单独设计（别把它藏在会被短路的方法里）

## 小结与下一章

- 下一章进入写路径：`@CachePut/@CacheEvict`。读缓存解决“省计算”，写缓存解决“一致性”。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`
- Test file：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/108-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-cache/02-cacheput-and-evict.md](110-02-cacheput-and-evict.md)

<!-- BOOKIFY:END -->
