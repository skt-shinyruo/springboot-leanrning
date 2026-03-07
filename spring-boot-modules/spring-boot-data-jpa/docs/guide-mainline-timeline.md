# 01. 主线时间线：Spring Boot Data JPA
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕主线时间线：Spring Boot Data JPA展开，主线可以概括为：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。

    阅读时可以先跑 `BootDataJpaLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。

    需要下探源码时，可以从 `org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 94 章：Data JPA 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 00 - Deep Dive Guide（springboot-data-jpa）](guide-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：JPA 的实体状态与持久化上下文如何工作，以及 flush/dirty checking/fetching 等最影响真实项目的分支。
    - 读完应当能复述：**实体状态 → Persistence Context → SQL 何时发出（flush）→ 可见性与一致性** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01 顺读 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`BootDataJpaLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Data JPA —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
- 回到主线：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本章是「第 95 章：主线时间线：Spring Boot Data JPA」的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
建议先跑 `BootDataJpaLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Data JPA」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- Data JPA 几乎必然与事务一起出现：很多“查不到/查到旧数据”的问题，本质是事务隔离 + flush 时机 + 持久化上下文的叠加。
- 性能问题的高发点：N+1、无意的 flush、脏检查导致的额外 SQL。

## 主线时间线（建议顺读）

1. 先把实体状态讲清楚：Transient/Managed/Detached/Removed
   - 阅读：[01. 实体状态](data-jpa-entity-states.md)
2. 再把持久化上下文跑通：一级缓存与 identity
   - 阅读：[02. 持久化上下文](data-jpa-persistence-context.md)
3. flush 与可见性：什么时候会发 SQL，为什么“看起来没写但查到了”
   - 阅读：[03. flush 与可见性](data-jpa-flush-and-visibility.md)
4. 脏检查：为什么没 save 也会 update
   - 阅读：[04. 脏检查](data-jpa-dirty-checking.md)
5. 抓住性能分支：fetching 与 N+1
   - 阅读：[05. fetching 与 N+1](data-jpa-fetching-and-n-plus-one.md)
6. 用 slice 测试把行为固定下来：@DataJpaTest
   - 阅读：[06. @DataJpaTest](data-jpa-datajpatest-slice.md)
7. 学会看 SQL：把“到底执行了什么”变成可观察证据
   - 阅读：[07. SQL 调试](data-jpa-debug-sql.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](appendix-common-pitfalls.md)
- 自检：[99-self-check.md](appendix-self-check.md)

## 证据链（如何验证真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Data JPA」的生效时机/顺序/边界；断点/入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Data JPA」的生效时机/顺序/边界；断点/入口：`org.springframework.data.jpa.repository.support.JpaRepositoryFactory`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Data JPA」的生效时机/顺序/边界；断点/入口：`jakarta.persistence.EntityManager`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootDataJpaLabTest`` 后，把上述观察点逐条对照，写出自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
