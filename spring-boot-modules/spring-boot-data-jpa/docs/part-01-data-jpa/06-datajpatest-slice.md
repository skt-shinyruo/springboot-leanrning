# 06. `@DataJpaTest`：为什么它适合学 JPA（切片测试）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`@DataJpaTest`：为什么它适合学 JPA（切片测试）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
    - 原理：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager`
    - 推荐 Lab：`BootDataJpaLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. Fetching 与 N+1：为什么查一次会变成查很多次？](05-fetching-and-n-plus-one.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[07. Debug/观察：怎么把 Hibernate 的 SQL“看清楚”？](07-debug-sql.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「06. `@DataJpaTest`：为什么它适合学 JPA（切片测试）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootDataJpaLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 读完本章，应当能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaLabTest`

## 机制主线

- 更小、更快的 Spring Boot 测试上下文（只加载 JPA 相关）
- 默认事务包裹（测试结束自动回滚，减少污染）
- EntityManager / Repository 等关键对象开箱即用

- `TransactionSynchronizationManager.isActualTransactionActive()` 为 true

这意味着：

## 练习入口：回滚行为


## 学习建议


## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

`@DataJpaTest` 是学习 JPA 的绝佳工具，因为它提供了：

## 在本模块如何验证

看 `BootDataJpaLabTest#dataJpaTestRunsInsideATransaction`：

- 实体通常是 managed 的
- flush/dirty checking 的行为更容易复现与验证

看 `BootDataJpaExerciseTest#exercise_rollbackBehavior`：

- 目标：演示 `@DataJpaTest` 默认回滚
- 并通过 `@Commit` 或 `@Rollback(false)` 改变行为，再观察差异

- 学机制优先用 `@DataJpaTest`
- 需要跨层（Web + Service + JPA + Tx）再用 `@SpringBootTest` 或去 capstone 模块练

## 常见坑与边界

### 坑点 1：把 `@DataJpaTest` 当成“真实运行时”，忽略了默认事务与回滚语义

- Symptom：以为数据会落库/对外可见，但测试结束后一切“消失”；或在测试里做跨事务验证一直不稳定
- Root Cause：`@DataJpaTest` 默认用事务包裹并在测试结束回滚，这对学机制很友好，但不等价于生产运行时
- Verification：`BootDataJpaLabTest#dataJpaTestRunsInsideATransaction`
- Fix：学机制优先 `@DataJpaTest`；需要跨层/跨事务的真实边界验证，再切换到 `@SpringBootTest` 或在专门用例里明确事务策略

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`
- Exercise：`BootDataJpaExerciseTest`

上一章：[part-01-data-jpa/05-fetching-and-n-plus-one.md](05-fetching-and-n-plus-one.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-data-jpa/07-debug-sql.md](07-debug-sql.md)

<!-- BOOKIFY:END -->
