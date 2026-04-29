# 03. Repository 调用链（RepositoryProxy → SimpleJpaRepository → EntityManager）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：Repository 调用链（RepositoryProxy → SimpleJpaRepository → EntityManager）展开，主线可以概括为：Spring Data JPA 通过代理把接口方法路由到 `SimpleJpaRepository`；真实落库行为取决于事务边界与 persistence context（flush 时机）。

    先跑 `BootDataJpaLabTest`，把“保存/查询/flush 行为差异”固化成断言，再按本章串起 Repository 到 Hibernate 的调用链。

    需要下探源码时，可以从 `RepositoryFactorySupport` / `SimpleJpaRepository` / `EntityManager` /（Hibernate）`Session` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Data JPA](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Data JPA）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootDataJpaLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：Spring Data JPA 通过代理把接口方法路由到 `SimpleJpaRepository`；真实落库行为取决于事务边界与 persistence context（flush 时机）。需要下探源码时，可以从 `RepositoryFactorySupport` / `SimpleJpaRepository` / `EntityManager` /（Hibernate）`Session` 这些入口切入。


## 最短调用链

1. 调用 Repository 接口方法（本质上是 proxy）
2. 路由到 `SimpleJpaRepository`（save/find 等）
3. 调用 `EntityManager`（persist/merge/find）
4. 在事务边界内：persistence context 缓存与 flush 决定 SQL 何时真正发出

证据链入口：

- `BootDataJpaLabTest` / `BootDataJpaDebugSqlLabTest` / `BootDataJpaMergeAndDetachLabTest`

## 小结与下一章

Spring Data JPA 通过代理把接口方法路由到 `SimpleJpaRepository`；真实落库行为取决于事务边界与 persistence context（flush 时机）。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootDataJpaLabTest`
- Lab：`BootDataJpaDebugSqlLabTest`
- Lab：`BootDataJpaMergeAndDetachLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
