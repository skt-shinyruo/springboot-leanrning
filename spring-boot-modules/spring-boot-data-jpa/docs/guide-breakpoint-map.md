# 04. 断点地图（Data JPA）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Data JPA）展开，主线可以概括为：Repository 代理 → `EntityManager`（Persistence Context）→ flush/dirty checking → 事务提交/回滚 → fetching 决定 SQL 数量与边界。

    先跑 `BootDataJpaBranchMatrixLabTest` 固化“实体状态/flush/merge-detach”的断言，再用断点把每次 SQL 的产生点与 persistence context 的状态变化对齐。

    需要下探源码时，可以从 `org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `jakarta.persistence.EntityManager` / `org.hibernate.Session` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Data JPA](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- 本章收束点：把 JPA 的“看不见的状态机”变成可观察：**实体状态（managed/detached）**、**flush 时机**、**SQL 证据链**。
- 证据链方法：把断点当作“证据采集器”，先证明分支发生点，再写结论（避免“凭印象判断 Hibernate 会…”）。

## 运行入口（先运行）

- Book Matrix：`BootDataJpaBookMatrixLabTest`
- Branch Matrix：`BootDataJpaBranchMatrixLabTest`

运行命令：

- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

## JPA API 断点（优先）

- `jakarta.persistence.EntityManager#persist`
- `jakarta.persistence.EntityManager#merge`
- `jakarta.persistence.EntityManager#flush`
- `jakarta.persistence.EntityManager#clear`

## Spring Data 断点（从 Repository 入口下去）

- `org.springframework.data.jpa.repository.support.SimpleJpaRepository#save`
- `org.springframework.data.jpa.repository.support.SimpleJpaRepository#findById`

## 观察点（最常用的 4 个）

- `entityManager.contains(entity)`：是否 managed（状态机证据）
- `TransactionSynchronizationManager.isActualTransactionActive()`：是否在事务内
- “同一个 id 的对象引用是否变化”：merge 通常返回 managed copy（detached != managed）
- SQL 证据链：开启 show-sql（见 `BootDataJpaDebugSqlLabTest`）

## 常见分支定位（与矩阵表配合）

- “改了对象但没落库”：先确认对象是否 managed（contains），再确认是否 flush 发生。
- “merge 后为什么对象引用变了”：断点到 `EntityManager#merge`，观察返回值与原对象引用差异。

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

Repository 代理 → `EntityManager`（Persistence Context）→ flush/dirty checking → 事务提交/回滚 → fetching 决定 SQL 数量与边界。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootDataJpaBranchMatrixLabTest`
- Lab：`BootDataJpaMergeAndDetachLabTest` / `BootDataJpaDebugSqlLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

