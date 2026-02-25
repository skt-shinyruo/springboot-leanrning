# 02. Persistence Context：JPA 的“一级缓存”与事务绑定
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Persistence Context：JPA 的“一级缓存”与事务绑定
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
    - 原理：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager`
    - 推荐 Lab：`BootDataJpaLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. Entity 状态机：transient / managed / detached / removed](01-entity-states.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？](03-flush-and-visibility.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「02. Persistence Context：JPA 的“一级缓存”与事务绑定」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootDataJpaLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaLabTest`

## 机制主线

Persistence Context（持久化上下文）是 JPA/Hibernate 的核心：

- 它管理实体的 **生命周期与状态**
- 它保证同一个事务/上下文内的 **对象标识一致性**
- 它承载 **脏检查**、**延迟加载** 等机制的基础

## 关键心智模型（建议背下来）

> 在同一个事务（更准确：同一个 persistence context）里：
>
> - 实体对象不是“普通 POJO”，而是被管理的状态机
> - 修改对象字段 ≈ 修改待同步的持久化状态（最终在 flush/commit 写入 DB）

学习阶段推荐做两件事：

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `BootDataJpaLabTest` 里的这些实验：

- `entityIsManagedAfterSaveInSamePersistenceContext`
- `entityManagerClearDetachesEntities`
- `dataJpaTestRunsInsideATransaction`

尤其是 `dataJpaTestRunsInsideATransaction`：

- `@DataJpaTest` 默认会在一个事务里运行测试
- 这意味着 persistence context 通常也跟着事务生命周期走

## Debug/观察建议

1. 经常用 `entityManager.contains(entity)` 问自己：它现在是 managed 还是 detached？
2. 经常用 `entityManager.flush()` 强制把“上下文里的变化”同步到数据库，验证你对机制的理解

## 常见坑与边界

### 坑点 1：把“一致性视图”误当成“数据库事实”，导致结论被一级缓存误导

- Symptom：你明明改了字段但 SQL 看不到（或你以为查到的就是 DB 最新值），结论混乱
- Root Cause：persistence context 是“事务内一致性视图”（一级缓存 + 状态管理），不等价于数据库真实状态
- Verification：
  - managed 状态可被 contains 观察：`BootDataJpaLabTest#entityIsManagedAfterSaveInSamePersistenceContext`
  - clear 会让你从“上下文视图”回到“数据库事实”：`BootDataJpaLabTest#entityManagerClearDetachesEntities`
- Fix：学习阶段强烈建议在关键断言前后配合 `flush()` 与 `clear()`，避免被一级缓存制造的“假象”带偏

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`

上一章：[part-01-data-jpa/01-entity-states.md](01-entity-states.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-data-jpa/03-flush-and-visibility.md](03-flush-and-visibility.md)

<!-- BOOKIFY:END -->
