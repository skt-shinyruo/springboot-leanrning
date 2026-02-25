# 03. Repository 调用链（RepositoryProxy → SimpleJpaRepository → EntityManager）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Repository 调用链（RepositoryProxy → SimpleJpaRepository → EntityManager）
    - 怎么使用：先跑 `BootDataJpaLabTest`，把“保存/查询/flush 行为差异”固化成断言，再按本文串起 Repository 到 Hibernate 的调用链。
    - 原理：Spring Data JPA 通过代理把接口方法路由到 `SimpleJpaRepository`；真实落库行为取决于事务边界与 persistence context（flush 时机）。
    - 源码入口：`RepositoryFactorySupport` / `SimpleJpaRepository` / `EntityManager` /（Hibernate）`Session`
    - 推荐 Lab：`BootDataJpaLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-data-jpa）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Data JPA Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. Repository 调用链（RepositoryProxy → SimpleJpaRepository → EntityManager）**
- 建议入口：优先运行 `BootDataJpaLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：Spring Data JPA 通过代理把接口方法路由到 `SimpleJpaRepository`；真实落库行为取决于事务边界与 persistence context（flush 时机）。
- 源码入口：`RepositoryFactorySupport` / `SimpleJpaRepository` / `EntityManager` /（Hibernate）`Session`



## 最短调用链

1. 调用 Repository 接口方法（其实是 proxy）
2. 路由到 `SimpleJpaRepository`（save/find 等）
3. 调用 `EntityManager`（persist/merge/find）
4. 在事务边界内：persistence context 缓存与 flush 决定 SQL 何时真正发出

证据链入口：

- `BootDataJpaLabTest` / `BootDataJpaDebugSqlLabTest` / `BootDataJpaMergeAndDetachLabTest`

## 小结与下一章

- 小结：Spring Data JPA 通过代理把接口方法路由到 `SimpleJpaRepository`；真实落库行为取决于事务边界与 persistence context（flush 时机）。
- 下一章：[第 96 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`
- Lab：`BootDataJpaDebugSqlLabTest`
- Lab：`BootDataJpaMergeAndDetachLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
