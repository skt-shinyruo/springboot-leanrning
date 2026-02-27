# 01. 常见坑清单（建议反复对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：常见坑清单（建议反复对照）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
    - 原理：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager`
    - 推荐 Lab：`BootDataJpaDebugSqlLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[07. Debug/观察：怎么把 Hibernate 的 SQL“看清楚”？](../part-01-data-jpa/07-debug-sql.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-data-jpa）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)

- 本章主题：**01. 常见坑清单（建议反复对照）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应当能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`

## 机制主线

这页不展开完整机制主线；其定位更接近排障备忘录：把常见分支与可复现入口列出来，便于回到 tests 验证。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 这一模块的坑大多来自“以为自己在看数据库，其实在看 persistence context”。需要时用 `flush()+clear()` 把视角切回数据库。

## 坑 1：把 persistence context 当成数据库

- 现象：改了对象字段，立刻 `findById` 看到“变了”，误以为 DB 已更新
- 解决：`entityManager.flush()` + `entityManager.clear()` 再查，避免一级缓存假象
- 对照：见 [docs/04](../part-01-data-jpa/04-dirty-checking.md)

## 坑 2：不理解 flush 导致“JDBC 查不到/查到了但没提交”

- 现象：用 `JdbcTemplate` 直接查表，结果和想象不一致
- 解决：理解 flush vs commit 的差异（见 [docs/03](../part-01-data-jpa/03-flush-and-visibility.md)）

## 坑 3：懒加载 + 循环访问触发 N+1

- 现象：查列表很快，但访问关联属性时 SQL 爆炸
- 解决：先把问题复现清楚，再讨论 fetch 策略（见 [docs/05](../part-01-data-jpa/05-fetching-and-n-plus-one.md)）

## 坑 4：在事务外访问懒加载属性

- 现象：`LazyInitializationException`
- 原因：事务/Session 结束后再访问 lazy 属性无法触发查询

## 坑 5：测试里忘记 `@DataJpaTest` 的默认回滚

- 会看到：在测试里插入/更新了数据，断言也通过了；但换一个测试再查时，发现“数据没了”，于是误以为 JPA/flush/事务有问题。
- Root Cause：`@DataJpaTest` 默认在每个测试方法后回滚事务（这通常是好事：隔离、可重复）。
- Fix：
  - 需要跨测试共享数据：不要靠“上一个测试留下的数据”，改用 `@Sql`/测试数据工厂/在当前测试里准备数据。
  - 需要观测真实落库：在同一测试内显式 `flush()` + `clear()` 再查（避免一级缓存假象）。
- 对照：见 [docs/06](../part-01-data-jpa/06-datajpatest-slice.md)

## 坑 6：以为 `merge()` 会“把原对象重新托管”，结果改了半天没生效

- Symptom：在 `detach()/clear()` 之后继续改对象，觉得“脏检查会帮我 UPDATE”，但数据库里啥都没变；或者调用了 `merge()`，但后续仍然在 **原对象** 上继续改，结果再次不生效。
- Root Cause：JPA 的 `merge()` 语义是 **复制状态到一个新的 managed 实例**，并返回这个 managed 实例；传入的那个对象本身仍然是 detached，后续修改不会被脏检查追踪。
- Verification：`BootDataJpaMergeAndDetachLabTest#detached_changesWithoutMerge_shouldNotBePersisted`、`BootDataJpaMergeAndDetachLabTest#merge_shouldPersistDetachedChangesIntoManagedCopy`
- Breakpoints：`org.hibernate.internal.SessionImpl#merge`、`org.hibernate.event.internal.DefaultMergeEventListener#onMerge`
- Fix：后续操作一律使用 `merge()` 的返回值，或重新 `find()` 获取 managed；把“对象状态（managed/detached）→ 预期 SQL”用 Lab/Test 固化。

## 对应 Lab（可运行）

- `BootDataJpaLabTest`
- `BootDataJpaDebugSqlLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`

上一章：[part-01-data-jpa/07-debug-sql.md](../part-01-data-jpa/07-debug-sql.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
