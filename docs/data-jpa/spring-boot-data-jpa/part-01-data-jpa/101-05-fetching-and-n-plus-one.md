# 第 101 章：05. Fetching 与 N+1：为什么查一次会变成查很多次？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Fetching 与 N+1：为什么查一次会变成查很多次？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootDataJpaDebugSqlLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 100 章：04. Dirty Checking（脏检查）：为什么改字段不用 save 也能落库？](100-04-dirty-checking.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 102 章：06. `@DataJpaTest`：为什么它适合学 JPA（切片测试）](102-06-datajpatest-slice.md)
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


## 你应该得到的结论（比背解决方案更重要）

> N+1 不是“写错 SQL”，而是“加载策略 + 访问方式”共同决定的结果。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
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
- Fix：先用统计/日志把 N+1 变成事实，再选择 fetch join / entity graph / batch size 等手段，并用测试锁住“查询数不回退”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest`
- Exercise：`BootDataJpaExerciseTest`

上一章：[脏检查](100-04-dirty-checking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[@DataJpaTest](102-06-datajpatest-slice.md)

<!-- BOOKIFY:END -->
