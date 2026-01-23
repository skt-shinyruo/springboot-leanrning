# 第 105 章：99 - Self Check（springboot-data-jpa）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-data-jpa）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
    - 原理：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager`
    - 推荐 Lab：`BootDataJpaDebugSqlLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 104 章：90. 常见坑清单（建议反复对照）](104-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 106 章：Cache 主线](../../../book/106-cache-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/096-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/096-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-data-jpa）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest` / `BootDataJpaMergeAndDetachLabTest`

## 机制主线


## 自测题
1. `EntityManager` 的一级缓存如何影响“你以为查到了最新数据”？
2. `flush` 发生的时机有哪些？为什么某些查询会触发 flush？
3. N+1 的根因是什么？有哪些“看起来优化了但实际上没用”的改法？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest` / `BootDataJpaMergeAndDetachLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 对应 Exercise（可运行）

- `BootDataJpaExerciseTest`

## 常见坑与边界

### 坑点 1：只盯着 Repository API，不建立“证据链”，导致理解停留在玄学层

- Symptom：面对 flush/dirty checking/N+1/getReferenceById 时只能靠猜；遇到慢 SQL 也不知道从哪里排查
- Root Cause：JPA 的关键机制很多都发生在 persistence context 与事务边界里，不写断言就很难稳定复现
- Verification（建议作为排障兜底入口）：
  - dirty checking：`BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`
  - flush 可见性：`BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction`
  - getReferenceById（lazy proxy）：`BootDataJpaLabTest#getReferenceByIdReturnsALazyProxy_andInitializesOnPropertyAccess`
  - N+1：`BootDataJpaLabTest#nPlusOneHappensWhenAccessingLazyCollections`
- Fix：像 spring-core-beans 一样，把每个关键分支都做成默认 Lab（可运行 + 可断言 + 可回归），再谈“最佳实践/优化方案”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest` / `BootDataJpaMergeAndDetachLabTest`
- Exercise：`BootDataJpaExerciseTest`

上一章：[appendix/90-common-pitfalls.md](104-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
