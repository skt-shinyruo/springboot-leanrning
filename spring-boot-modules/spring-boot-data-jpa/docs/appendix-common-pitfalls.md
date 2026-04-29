# 01. 常见坑清单（排查时对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"

    这一页的坑大多来自一个错觉：读者以为自己在看数据库，本质上在看 persistence context（一级缓存与实体状态）。一旦把“可见性”与“提交”混在一起，flush/commit、懒加载、N+1、merge/detach 这些行为都会显得像隐式机制。

    先运行 `BootDataJpaDebugSqlLabTest`（把 SQL 看清楚）与 `BootDataJpaLabTest`（把实体状态/可见性跑成断言），再回到本章逐条对照。需要下探源码时，入口通常从 `SimpleJpaRepository`、`EntityManager` 与 `JpaTransactionManager` 三条线展开。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[07. Debug/观察：怎么把 Hibernate 的 SQL“看清楚”？](data-jpa-debug-sql.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[自检题](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试 收束答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 先把“看见的是一级缓存还是数据库”跑成证据

Data JPA 的坑很多时候不是 SQL 本身，而是视角不对：同一行 `findById`，有时读到的是 persistence context，有时读到的才是数据库。排障时如果先把这一点做成可重复的断言，后面讨论 flush、事务可见性、fetching 策略会更收敛。

先运行两组矩阵测试：Book Matrix 把主线跑通，Branch Matrix 把 flush/merge/fetching 这些高频分支跑全。跑完后再回到断点地图与分支矩阵页逐条对照，读起来会更像“对照答案”，而不是堆概念。

- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

需要下探时，从本模块的断点地图与关键分支矩阵切入：它们把 `SimpleJpaRepository`、flush 与事务边界的落点标得很清楚：[guide-breakpoint-map.md](guide-breakpoint-map.md) / [guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`

## 最小可运行实验（Lab）

- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`
- 运行命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 这一模块的坑大多来自“以为自己在看数据库，本质上在看 persistence context”。需要时用 `flush()+clear()` 把视角切回数据库。

## 坑 1：把 persistence context 当成数据库

在事务里改了 managed entity 的字段，立刻 `findById` 再查发现“已经变了”，于是误以为数据库已经 UPDATE。实际上这往往只是一级缓存的可见性：同一个 persistence context 里拿到的仍然是同一个 managed 实例。

想把视角切回数据库，最直接的办法是 `flush()` + `clear()` 再查（把缓存假象清掉）。对应机制与对照实验见：[04. Dirty Checking：为什么改字段就会 UPDATE？](data-jpa-dirty-checking.md)。

## 坑 2：不理解 flush 导致“JDBC 查不到/查到了但没提交”

在同一事务中用 JPA 写入，再用 `JdbcTemplate` 直接查表，结果和想象不一致，这是典型的 flush/commit 概念混用：flush 是把 SQL 发出去，commit 才决定事务的最终命运。机制与可见性对照见：[03. flush 与可见性：为什么“发了 SQL”也不等于提交？](data-jpa-flush-and-visibility.md)。

## 坑 3：懒加载 + 循环访问触发 N+1

查列表很快，但访问关联属性时 SQL 爆炸，这通常不是“JPA 很慢”，而是 fetching 边界没有被显式表达。先把 N+1 复现清楚，再讨论 fetch join/EntityGraph/批量抓取这些策略（否则很容易在错误的地方优化）。对照见：[05. Fetching 与 N+1：把“慢在哪里”跑成事实](data-jpa-fetching-and-n-plus-one.md)。

## 坑 4：在事务外访问懒加载属性

- 现象：`LazyInitializationException`
- 原因：事务/Session 结束后再访问 lazy 属性无法触发查询

## 坑 5：测试里忘记 `@DataJpaTest` 的默认回滚

在测试里插入/更新了数据，断言也通过了；但换一个测试再查时发现“数据没了”，于是误以为 JPA/flush/事务有问题。这里最常见的真相是：`@DataJpaTest` 默认在每个测试方法后回滚事务（这通常是好事：隔离、可重复）。

需要跨测试共享数据时，不要依赖“上一个测试留下的数据”，而是用 `@Sql`/测试数据工厂/在当前测试里准备数据。需要观测真实落库时，则在同一测试内显式 `flush()` + `clear()` 再查（避免一级缓存假象）。

更多 slice 边界与回滚语义的对照见：[06. `@DataJpaTest`：slice 的边界与默认回滚](data-jpa-datajpatest-slice.md)。

## 坑 6：以为 `merge()` 会“把原对象重新托管”，结果改了半天没生效

在 `detach()/clear()` 之后继续改对象，预期“脏检查会自动 UPDATE”，但数据库没有变化；或者调用了 `merge()`，但后续仍然在 **原对象** 上继续改，结果再次不生效。

JPA 的 `merge()` 语义是 **复制状态到一个新的 managed 实例**，并返回这个 managed 实例；传入的那个对象本身仍然是 detached，后续修改不会被脏检查追踪。

这个误判可以用 `BootDataJpaMergeAndDetachLabTest#detached_changesWithoutMerge_shouldNotBePersisted` 与 `BootDataJpaMergeAndDetachLabTest#merge_shouldPersistDetachedChangesIntoManagedCopy` 直接对照；如果要看更底层的落点，入口通常在 `org.hibernate.internal.SessionImpl#merge` 与 `org.hibernate.event.internal.DefaultMergeEventListener#onMerge`。

后续操作一律使用 `merge()` 的返回值，或重新 `find()` 获取 managed；把“对象状态（managed/detached）→ 预期 SQL”用实验/测试 固化。

## 对应 Lab（可运行）

- `BootDataJpaLabTest`
- `BootDataJpaDebugSqlLabTest`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`

上一章：[data-jpa-debug-sql.md](data-jpa-debug-sql.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
