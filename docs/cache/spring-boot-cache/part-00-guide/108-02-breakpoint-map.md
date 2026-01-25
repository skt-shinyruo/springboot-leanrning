# 第 108 章：02：断点地图（Cache Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Cache Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 108 章：00 - Deep Dive Guide（springboot-cache）](108-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 108 章：04：关键分支矩阵（Branch Decision Matrix）](108-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Cache Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- 本章目标：把缓存排障的关键问题变成可观察：**key 是什么**、**命中了哪个 cache**、**condition/unless 是否阻止缓存**、**sync 是否生效**。
- 推荐方法：用 invocationCount/trace 作为证据链（先证明有没有走到目标方法）。

## 运行入口（建议先跑）

- Book Matrix：`BootCacheBookMatrixLabTest`
- Branch Matrix：`BootCacheBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`

## 框架侧断点（优先）

- `org.springframework.cache.interceptor.CacheInterceptor#invoke`
- `org.springframework.cache.interceptor.CacheAspectSupport#execute`
- `org.springframework.cache.interceptor.CacheAspectSupport#generateKey`
- `org.springframework.cache.interceptor.CacheAspectSupport#findCachedValue`

## SpEL 与条件断点

- `org.springframework.cache.interceptor.CacheOperationExpressionEvaluator#key`
- `org.springframework.cache.interceptor.CacheOperationExpressionEvaluator#condition`
- `org.springframework.cache.interceptor.CacheOperationExpressionEvaluator#unless`

## Watchpoints（建议）

- `cacheNames` / `cache`（命中哪个 cache）
- `key`（最终 key，尤其是 SpEL）
- `condition` / `unless` 的求值结果（true/false）
- 目标方法是否真正执行（用 `invocationCount` 或断点证明）

## 常见分支定位（与矩阵表配合）

- “我以为命中了但没命中”：先看 key 是否相同（以及是否被拼接/序列化导致变化）。
- “condition/unless 不生效”：断点到 evaluator，观察表达式求值时机与变量内容。
- “并发下重复计算”：重点看 `sync=true` 是否生效（以及 cache provider 是否支持）。

## 排障入口（Playbook）

- 常见坑：[`../appendix/114-90-common-pitfalls.md`](../appendix/114-90-common-pitfalls.md)
- 自检：[`../appendix/115-99-self-check.md`](../appendix/115-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest` / `BootCacheBookMatrixLabTest` / `BootCacheBranchMatrixLabTest`

上一章：[过期语义](../part-01-cache/113-05-expiry-with-ticker.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[108-04-branch-decision-matrix.md](108-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
