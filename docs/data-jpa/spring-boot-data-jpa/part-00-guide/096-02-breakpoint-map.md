# 第 96 章：02：断点地图（Data JPA Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Data JPA Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootDataJpaMergeAndDetachLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 96 章：00 - Deep Dive Guide（springboot-data-jpa）](096-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 96 章：04：关键分支矩阵（Branch Decision Matrix）](096-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Data JPA Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- 本章目标：把 JPA 的“看不见的状态机”变成可观察：**实体状态（managed/detached）**、**flush 时机**、**SQL 证据链**。
- 推荐方法：把断点当作“证据采集器”，先证明分支发生点，再写结论（避免“我觉得 Hibernate 会…”）。

## 运行入口（建议先跑）

- Book Matrix：`BootDataJpaBookMatrixLabTest`
- Branch Matrix：`BootDataJpaBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

## JPA API 断点（优先）

- `jakarta.persistence.EntityManager#persist`
- `jakarta.persistence.EntityManager#merge`
- `jakarta.persistence.EntityManager#flush`
- `jakarta.persistence.EntityManager#clear`

## Spring Data 断点（从 Repository 入口下去）

- `org.springframework.data.jpa.repository.support.SimpleJpaRepository#save`
- `org.springframework.data.jpa.repository.support.SimpleJpaRepository#findById`

## Watchpoints（最常用的 4 个）

- `entityManager.contains(entity)`：是否 managed（状态机证据）
- `TransactionSynchronizationManager.isActualTransactionActive()`：是否在事务内
- “同一个 id 的对象引用是否变化”：merge 通常返回 managed copy（detached != managed）
- SQL 证据链：开启 show-sql（见 `BootDataJpaDebugSqlLabTest`）

## 常见分支定位（与矩阵表配合）

- “改了对象但没落库”：先确认对象是否 managed（contains），再确认是否 flush 发生。
- “merge 后为什么对象引用变了”：断点到 `EntityManager#merge`，观察返回值与原对象引用差异。

## 排障入口（Playbook）

- 常见坑：[`../appendix/104-90-common-pitfalls.md`](../appendix/104-90-common-pitfalls.md)
- 自检：[`../appendix/105-99-self-check.md`](../appendix/105-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaMergeAndDetachLabTest` / `BootDataJpaBookMatrixLabTest` / `BootDataJpaBranchMatrixLabTest` / `BootDataJpaDebugSqlLabTest`

上一章：[SQL 调试](../part-01-data-jpa/103-07-debug-sql.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[096-04-branch-decision-matrix.md](096-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
