# 04. Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？展开，主线可以概括为：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。

    阅读时可以先跑 `BootDataJpaLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。

    需要下探源码时，可以从 `org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？](03-flush-and-visibility.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. Fetching 与 N+1：为什么查一次会变成查很多次？](05-fetching-and-n-plus-one.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaLabTest`

## 机制主线

这是 JPA 最“神奇”但也最容易被误用的机制：脏检查。

## 现象

- 拿到一个 managed entity
- 修改它的字段
- 没有再调用 `save()`
- flush/commit 之后，数据库值变了

1. `Book saved = repository.save(...)`
2. `saved.changeAuthor("B")`（只改字段）
3. `entityManager.flush()`（触发同步）
4. `entityManager.clear()`（清掉上下文，避免“一级缓存假象”）
5. 重新 `findById` 并断言 author 已经变成 B

## 应当得到的结论

1. dirty checking 的前提是：实体必须是 managed（受 persistence context 管理）
2. flush/commit 是“把变化写进 DB”的时机
3. 清理 context（`clear()`）能避免误把“一级缓存”当成“数据库状态”

## 最小可运行实验（Lab）

- Lab：`BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）


## 在本模块如何验证

看 `BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`：

## 常见坑与边界

### 坑点 1：认为“改字段就立刻发 UPDATE”，忽略了 flush/commit 才是写入时机

修改字段后立刻去看 SQL/DB，发现没变化，于是误判“脏检查没生效”

dirty checking 会把变化记录在 persistence context，真正写入发生在 flush/commit

`BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`

学习阶段用 `entityManager.flush()` 主动触发同步，并在 `clear()` 后重新查询验证（避免一级缓存假象）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`

上一章：[part-01-data-jpa/03-flush-and-visibility.md](03-flush-and-visibility.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-data-jpa/05-fetching-and-n-plus-one.md](05-fetching-and-n-plus-one.md)

<!-- BOOKIFY:END -->
