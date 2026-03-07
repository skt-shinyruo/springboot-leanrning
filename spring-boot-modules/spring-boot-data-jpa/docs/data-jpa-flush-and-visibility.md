# 03. flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？展开，主线可以概括为：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。

    阅读时可以先跑 `BootDataJpaLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。

    需要下探源码时，可以从 `org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. Persistence Context：JPA 的“一级缓存”与事务绑定](data-jpa-persistence-context.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？](data-jpa-dirty-checking.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaLabTest`

## 机制主线

新手最常见的误解是：

> “我调用了 `save()`，所以数据已经进数据库了。”

实际上，在很多情况下：

- `save()` 只是把实体交给 persistence context
- SQL 什么时候真正执行，要看 flush/commit 时机

## flush vs commit（一句话）

- **flush**：把 persistence context 的变更同步成 SQL 执行（但事务可能还没提交）
- **commit**：提交事务，让变更对其它事务可见

1. `repository.save(...)`
2. `entityManager.flush()`
3. 用 `JdbcTemplate` 直接查表行数

关键观察点：

- flush 之后，同一事务内用 JDBC 查能看到行数变化
- 这说明：SQL 已经发出并执行了（只是还没 commit）

## 应当得到的结论

- 学习 JPA 一定要区分“上下文里有什么”与“数据库里有什么”
- flush 是把两者对齐的手段之一（学习阶段特别好用）

## 最小可运行实验（Lab）

- Lab：`BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）


## 在本模块如何验证（强烈建议断点）

看 `BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction`：

## 常见坑与边界

### 坑点 1：把 flush 当成 commit，误以为“flush 后其它事务也能看到”

在一个事务里 flush 后能查到数据，于是以为数据已经“对外可见/已提交”

flush 只是把 SQL 发出去并执行在当前事务里；是否对其它事务可见取决于 commit

`BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction`

用“同事务 vs 跨事务”的视角分流：flush 用来对齐“上下文 vs DB”，commit 才决定“对外可见”

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`

上一章：[part-01-data-jpa/02-persistence-context.md](data-jpa-persistence-context.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-data-jpa/04-dirty-checking.md](data-jpa-dirty-checking.md)

<!-- BOOKIFY:END -->
