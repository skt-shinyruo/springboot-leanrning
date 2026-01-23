# 第 96 章：02：断点地图（Data JPA Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Data JPA Debugger Pack）
    - 怎么使用：先跑 `BootDataJpaBranchMatrixLabTest` 固化“实体状态/flush/merge-detach”的断言，再用断点把每次 SQL 的产生点与 persistence context 的状态变化对齐。
    - 原理：Repository 代理 → `EntityManager`（Persistence Context）→ flush/dirty checking → 事务提交/回滚 → fetching 决定 SQL 数量与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `jakarta.persistence.EntityManager` / `org.hibernate.Session`
    - 推荐 Lab：`BootDataJpaBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 96 章：00 - Deep Dive Guide（springboot-data-jpa）](096-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 96 章：04：关键分支矩阵（Branch Decision Matrix）](096-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

- Matrix：`BootDataJpaBranchMatrixLabTest`
- Lab：`BootDataJpaMergeAndDetachLabTest` / `BootDataJpaDebugSqlLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](096-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](096-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

