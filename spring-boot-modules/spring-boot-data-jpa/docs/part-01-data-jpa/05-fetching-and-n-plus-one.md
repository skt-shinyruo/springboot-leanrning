# 05. Fetching 与 N+1：为什么查一次会变成查很多次？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Fetching 与 N+1：为什么查一次会变成查很多次？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
    - 原理：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager`
    - 推荐 Lab：`BootDataJpaDebugSqlLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？](04-dirty-checking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. `@DataJpaTest`：为什么它适合学 JPA（切片测试）](06-datajpatest-slice.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. Fetching 与 N+1：为什么查一次会变成查很多次？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`

## 机制主线

fetching 不是“性能优化技巧”，而是**关联加载策略**：你查一张表时，关联对象/集合是一次性抓回来，还是等你访问时再补一刀。

N+1 的本质也很朴素：

- 第一条查询拿到 N 个“父对象”（例如 Author 列表）
- 你在循环里访问 lazy 关联（例如 `author.getBooks()`）
- 于是每个父对象都再补一次 select（N 次）→ 形成 N+1

这类问题最怕“只看代码不看 SQL”。在这个模块里，建议至少形成两条证据链：

- 用 `BootDataJpaDebugSqlLabTest` 把 SQL 打开（让“我以为只有一条查询”变成可见事实）
- 用 `BootDataJpaLabTest` 把“访问方式 → SQL 数量/耗时”写成断言（避免只靠经验判断）

## 什么是 N+1（直觉版）

你以为你在做：

- 1 次查询：查出列表（N 条记录）

实际发生的是：

- 1 次查询：查出列表
- N 次查询：对列表里的每一条记录再查一次关联数据

因此叫 N+1。

## 为什么会发生？

典型触发条件：

- 关联关系是懒加载（lazy）
- 你在循环中访问了关联属性
- persistence context/事务仍然活着，因此触发了额外 SQL

## 在本模块的练习入口

如果你只想快速把 N+1 跑出来（强烈推荐先这么做）：

- N+1 发生：`BootDataJpaLabTest#nPlusOneHappensWhenAccessingLazyCollections`
- 一个常见修复方向（示例）：`BootDataJpaLabTest#entityGraphCanAvoidNPlusOne_whenFetchingCollections`

如果你想把它变成“自己能写出来的结论”（更推荐）：

- 动手题入口：`BootDataJpaExerciseTest#exercise_relationshipsAndFetching`（新增关系 + 复现 N+1 + 写断言）

## 应当得到的结论（比背解决方案更重要）

> N+1 不是“写错 SQL”，而是“加载策略 + 访问方式”共同决定的结果。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章主要作为补充说明/索引页使用：推荐直接从模块的 Matrix/Lab 入口进入，再回到这里对照。
- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

这一章以“学习路线图”的方式讲 N+1（本模块目前通过 Exercises 引导你亲手复现）。

看 `BootDataJpaExerciseTest#exercise_relationshipsAndFetching`：

- 目标：新增一个实体关系（例如 `Author -> Books`）
- 然后复现一个 N+1 场景（并在测试里证明它发生了）

学习时建议先把问题复现清楚，再讨论常见解决手段（fetch join / entity graph / batch size 等）。

## 常见坑与边界

### 坑点 1：循环里访问 lazy 关联，触发 N+1 但你毫无察觉

- Symptom：功能测试都通过，但线上接口突然变慢；profiling 发现 SQL 数量暴涨
- Root Cause：列表查询 + 循环访问 lazy 关联 → 触发额外 select（N+1）
- Verification（本模块默认 Lab 给出可回归证据链）：
  - N+1 发生：`BootDataJpaLabTest#nPlusOneHappensWhenAccessingLazyCollections`
  - 常见修复思路（示例）：EntityGraph 预取集合：`BootDataJpaLabTest#entityGraphCanAvoidNPlusOne_whenFetchingCollections`
- Fix：先用统计/日志把 N+1 变成事实，再选择 fetch join / entity graph / batch size 等手段，并用测试锁定“查询数不回退”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`
- Exercise：`BootDataJpaExerciseTest`

上一章：[part-01-data-jpa/04-dirty-checking.md](04-dirty-checking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-data-jpa/06-datajpatest-slice.md](06-datajpatest-slice.md)

<!-- BOOKIFY:END -->
