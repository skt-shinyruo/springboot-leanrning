# 第 100 章：04. Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootDataJpaLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 99 章：03. flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？](099-03-flush-and-visibility.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 101 章：05. Fetching 与 N+1：为什么查一次会变成查很多次？](101-05-fetching-and-n-plus-one.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaLabTest`

## 机制主线

这是 JPA 最“神奇”但也最容易被误用的机制：脏检查。

## 现象

- 你拿到一个 managed entity
- 修改它的字段
- 没有再调用 `save()`
- flush/commit 之后，数据库值变了

1. `Book saved = repository.save(...)`
2. `saved.changeAuthor("B")`（只改字段）
3. `entityManager.flush()`（触发同步）
4. `entityManager.clear()`（清掉上下文，避免“一级缓存假象”）
5. 重新 `findById` 并断言 author 已经变成 B

## 你应该得到的结论

1. dirty checking 的前提是：实体必须是 managed（受 persistence context 管理）
2. flush/commit 是“把变化写进 DB”的时机
3. 清理 context（`clear()`）能避免你误把“一级缓存”当成“数据库状态”

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`：

## 常见坑与边界

### 坑点 1：认为“改字段就立刻发 UPDATE”，忽略了 flush/commit 才是写入时机

- Symptom：你修改字段后立刻去看 SQL/DB，发现没变化，于是误判“脏检查没生效”
- Root Cause：dirty checking 会把变化记录在 persistence context，真正写入发生在 flush/commit
- Verification：`BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`
- Fix：学习阶段用 `entityManager.flush()` 主动触发同步，并在 `clear()` 后重新查询验证（避免一级缓存假象）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`

上一章：[flush 与可见性](099-03-flush-and-visibility.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[fetching 与 N+1](101-05-fetching-and-n-plus-one.md)

<!-- BOOKIFY:END -->
