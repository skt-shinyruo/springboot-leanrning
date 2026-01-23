# 第 99 章：03. flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
    - 原理：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager`
    - 推荐 Lab：`BootDataJpaLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 98 章：02. Persistence Context：JPA 的“一级缓存”与事务绑定](098-02-persistence-context.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 100 章：04. Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？](100-04-dirty-checking.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


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

## 你应该得到的结论

- 学习 JPA 一定要区分“上下文里有什么”与“数据库里有什么”
- flush 是你把两者对齐的手段之一（学习阶段特别好用）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证（强烈建议断点）

看 `BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction`：

## 常见坑与边界

### 坑点 1：把 flush 当成 commit，误以为“flush 后其它事务也能看到”

- Symptom：你在一个事务里 flush 后能查到数据，于是以为数据已经“对外可见/已提交”
- Root Cause：flush 只是把 SQL 发出去并执行在当前事务里；是否对其它事务可见取决于 commit
- Verification：`BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction`
- Fix：用“同事务 vs 跨事务”的视角分流：flush 用来对齐“上下文 vs DB”，commit 才决定“对外可见”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`

上一章：[part-01-data-jpa/02-persistence-context.md](098-02-persistence-context.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-data-jpa/04-dirty-checking.md](100-04-dirty-checking.md)

<!-- BOOKIFY:END -->
