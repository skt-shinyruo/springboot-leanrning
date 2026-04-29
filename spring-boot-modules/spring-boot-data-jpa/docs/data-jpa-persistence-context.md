# 02. Persistence Context：JPA 的“一级缓存”与事务绑定
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕Persistence Context：JPA 的“一级缓存”与事务绑定展开，主线可以概括为：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。

    阅读时可以先跑 `BootDataJpaLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。

    需要下探源码时，可以从 `org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. Entity 状态机：transient / managed / detached / removed](data-jpa-entity-states.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[03. flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？](data-jpa-flush-and-visibility.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「02. Persistence Context：JPA 的“一级缓存”与事务绑定」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `BootDataJpaLabTest`（或文末“对应实验/测试”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaLabTest`

## 机制主线

Persistence Context（持久化上下文）是 JPA/Hibernate 的核心：

- 它管理实体的 **生命周期与状态**
- 它保证同一个事务/上下文内的 **对象标识一致性**
- 它承载 **脏检查**、**延迟加载** 等机制的基础

## 关键心智模型（背下来）

> 在同一个事务（更准确：同一个 persistence context）里：
>
> - 实体对象不是“普通 POJO”，而是被管理的状态机
> - 修改对象字段 ≈ 修改待同步的持久化状态（最终在 flush/commit 写入 DB）

学习阶段可做两件事：

## 最小可运行实验（Lab）

- Lab：`BootDataJpaLabTest`
- 运行命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）


## 在本模块如何验证

看 `BootDataJpaLabTest` 里的这些实验：

- `entityIsManagedAfterSaveInSamePersistenceContext`
- `entityManagerClearDetachesEntities`
- `dataJpaTestRunsInsideATransaction`

尤其是 `dataJpaTestRunsInsideATransaction`：

- `@DataJpaTest` 默认会在一个事务里运行测试
- 这意味着 persistence context 通常也跟着事务生命周期走

## Debug/观察入口

1. 经常用 `entityManager.contains(entity)` 问自己：它现在是 managed 还是 detached？
2. 经常用 `entityManager.flush()` 强制把“上下文里的变化”同步到数据库，验证对机制的理解

## 常见坑与边界

### 坑点 1：把“一致性视图”误当成“数据库事实”，导致结论被一级缓存误导

明明改了字段但 SQL 看不到（或以为查到的就是 DB 最新值），结论混乱

persistence context 是“事务内一致性视图”（一级缓存 + 状态管理），不等价于数据库真实状态

- managed 状态可被 contains 观察：`BootDataJpaLabTest#entityIsManagedAfterSaveInSamePersistenceContext`
- clear 会让人从“上下文视图”回到“数据库事实”：`BootDataJpaLabTest#entityManagerClearDetachesEntities`

学习阶段可在关键断言前后配合 `flush()` 与 `clear()`，避免被一级缓存制造的“假象”带偏

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootDataJpaLabTest`

上一章：[data-jpa-entity-states.md](data-jpa-entity-states.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[data-jpa-flush-and-visibility.md](data-jpa-flush-and-visibility.md)

<!-- BOOKIFY:END -->
