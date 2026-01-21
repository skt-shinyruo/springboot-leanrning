# 第 103 章：07. Debug/观察：怎么把 Hibernate 的 SQL“看清楚”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Debug/观察：怎么把 Hibernate 的 SQL“看清楚”？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
    - 原理：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager`
    - 推荐 Lab：`BootDataJpaDebugSqlLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 102 章：06. `@DataJpaTest`：为什么它适合学 JPA（切片测试）](102-06-datajpatest-slice.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 104 章：90. 常见坑清单（建议反复对照）](../appendix/104-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**07. Debug/观察：怎么把 Hibernate 的 SQL“看清楚”？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaDebugSqlLabTest`

## 机制主线

学习 JPA 时，“看见 SQL”可以极大降低抽象层带来的不确定性。

## 本模块的默认配置

`spring-boot-modules/springboot-data-jpa/src/main/resources/application.properties` 已经开启：

- `spring.jpa.show-sql=true`

这对学习足够了。

## 进一步的观察（可选）

如果你希望看到更详细的 SQL 与参数（学习用即可），可以考虑在本模块的 `application.properties` 里增加：

## 建议的学习姿势


## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootDataJpaDebugSqlLabTest`
- 建议命令：`mvn -pl :springboot-data-jpa test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

- `logging.level.org.hibernate.SQL=DEBUG`
- `logging.level.org.hibernate.orm.jdbc.bind=TRACE`

- 先用 tests 得到确定性结论（断言/可复现）
- 再用 SQL 日志解释“为什么会这样”（机制解释）

## 对应 Lab（可运行）

- `BootDataJpaDebugSqlLabTest`（跑起来观察控制台 SQL 输出）

## 常见坑与边界

### 坑点 1：只盯着 SQL 日志，容易把“没 flush/没 commit”误判成“没执行”

- Symptom：你在调试时“怎么没看到 UPDATE/INSERT？”——尤其是在 `@DataJpaTest` 或一个事务里做了修改后立刻查询/断点观察。
- Root Cause：JPA/Hibernate 的写入通常是 **flush 时机驱动**（提交事务、显式 flush、某些查询触发 flush）；事务没结束时，不一定会立即把 SQL 打出来。
- Verification：`BootDataJpaDebugSqlLabTest#showSqlHelpsExplainPersistenceBehavior_whenRunningTests`
- Breakpoints：`org.springframework.orm.jpa.JpaTransactionManager#doCommit`、`org.hibernate.internal.SessionImpl#flush`
- Fix：在“需要观察 SQL 的关键点”显式 `flush()`（并结合 `clear()`/重新查询），把“状态变化 → SQL 输出”锁定成可复现的最小闭环。

（注意：这些配置不适合生产环境，学习完建议删除/降级）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaDebugSqlLabTest`

上一章：[part-01-data-jpa/06-datajpatest-slice.md](102-06-datajpatest-slice.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/104-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
