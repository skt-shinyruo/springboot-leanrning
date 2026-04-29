# 07. Debug/观察：怎么把 Hibernate 的 SQL“看清楚”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕Debug/观察：怎么把 Hibernate 的 SQL“看清楚”？展开，主线可以概括为：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。

    阅读时可以先跑 `BootDataJpaDebugSqlLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。

    需要下探源码时，可以从 `org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[06. `@DataJpaTest`：为什么它适合学 JPA（切片测试）](data-jpa-datajpatest-slice.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 常见坑清单（排查时对照）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaDebugSqlLabTest`

## 机制主线

学习 JPA 时，“看见 SQL”可以极大降低抽象层带来的不确定性。

## 本模块的默认配置

`spring-boot-modules/spring-boot-data-jpa/src/main/resources/application.properties` 已经开启：

- `spring.jpa.show-sql=true`

这对学习足够了。

## 进一步的观察（可选）

如果希望看到更详细的 SQL 与参数（学习用即可），可以考虑在本模块的 `application.properties` 里增加：

## 学习方式


## 最小可运行实验（Lab）

- Lab：`BootDataJpaDebugSqlLabTest`
- 运行命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）


- `logging.level.org.hibernate.SQL=DEBUG`
- `logging.level.org.hibernate.orm.jdbc.bind=TRACE`

- 先用 tests 得到确定性结论（断言/可复现）
- 再用 SQL 日志解释“为什么会这样”（机制解释）

## 对应 Lab（可运行）

- `BootDataJpaDebugSqlLabTest`（跑起来观察控制台 SQL 输出）

## 常见坑与边界

### 坑点 1：只盯着 SQL 日志，容易把“没 flush/没 commit”误判成“没执行”

在调试时“怎么没看到 UPDATE/INSERT？”——尤其是在 `@DataJpaTest` 或一个事务里做了修改后立刻查询/断点观察。

JPA/Hibernate 的写入通常是 **flush 时机驱动**（提交事务、显式 flush、某些查询触发 flush）；事务没结束时，不一定会立即把 SQL 打出来。

`BootDataJpaDebugSqlLabTest#showSqlHelpsExplainPersistenceBehavior_whenRunningTests`

`org.springframework.orm.jpa.JpaTransactionManager#doCommit`、`org.hibernate.internal.SessionImpl#flush`

在“需要观察 SQL 的关键点”显式 `flush()`（并结合 `clear()`/重新查询），把“状态变化 → SQL 输出”锁定成可复现的最小闭环。

（注意：这些配置不适合生产环境，学习完删除/降级）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootDataJpaDebugSqlLabTest`

上一章：[data-jpa-datajpatest-slice.md](data-jpa-datajpatest-slice.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
