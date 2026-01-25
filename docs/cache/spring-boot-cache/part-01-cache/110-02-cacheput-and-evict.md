# 第 110 章：02：`@CachePut/@CacheEvict`：更新与失效
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：`@CachePut/@CacheEvict`：更新与失效
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootCacheLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 109 章：01：`@Cacheable` 最小闭环](109-01-cacheable-basics.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 111 章：03：key / condition / unless：缓存边界](111-03-key-condition-unless.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02：`@CachePut/@CacheEvict`：更新与失效**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootCacheLabTest`

## 机制主线


## 你应该观察到什么

- `@CachePut` 会强制执行方法，并把返回值写入 cache
- `@CacheEvict` 会移除指定 key 的 entry（下一次访问会重新计算）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootCacheLabTest`
- 建议命令：`mvn -pl :spring-boot-cache test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 常见坑与边界

### 坑点 1：误以为 `@CachePut` 会“像 Cacheable 一样命中短路”，导致写入逻辑被误解

- Symptom：你以为 CachePut 在命中时不会执行方法，结果发现方法每次都执行（甚至带来额外 DB 调用）
- Root Cause：`@CachePut` 的语义就是“强制执行方法并更新缓存”，它不是读缓存短路
- Verification：`BootCacheLabTest#cachePutUpdatesCacheValue`（invocationCount 与缓存值更新为证据）
- Fix：需要短路就用 `@Cacheable`；需要更新缓存且愿意执行方法就用 `@CachePut`；失效用 `@CacheEvict`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootCacheLabTest`

上一章：[@Cacheable 基础](109-01-cacheable-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[key/condition/unless](111-03-key-condition-unless.md)

<!-- BOOKIFY:END -->
