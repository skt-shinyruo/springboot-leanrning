# 第 108 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 108 章：02：断点地图（Cache Debugger Pack）](108-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 109 章：01：@Cacheable 基础与命中语义](../part-01-cache/109-01-cacheable-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

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

- 常见坑：[`../appendix/114-90-common-pitfalls.md`](../appendix/114-90-common-pitfalls.md)
- 自检：[`../appendix/115-99-self-check.md`](../appendix/115-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootCacheLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest` / `BootCacheBranchMatrixLabTest` / `BootCacheSpelKeyLabTest`

上一章：[108-02-breakpoint-map.md](108-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/114-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
