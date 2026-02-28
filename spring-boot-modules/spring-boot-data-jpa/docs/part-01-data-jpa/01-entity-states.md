# 01. Entity 状态机：transient / managed / detached / removed
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Entity 状态机：transient / managed / detached / removed展开，主线可以概括为：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。

    阅读时可以先跑 `BootDataJpaLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。

    需要下探源码时，可以从 `org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-data-jpa）](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. Persistence Context：JPA 的“一级缓存”与事务绑定](02-persistence-context.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaLabTest`

## 机制主线

学 JPA 最容易“学成玄学”的原因是：只看到了 `repository.save()`，没看清楚背后有一套明确的状态机。

这一章只讲一个问题：

> **一个 Entity 在生命周期里会经历哪些状态？这些状态有什么可观察的后果？**

## 四种经典状态（务必记住）

1. **transient（瞬时/新建）**
   - `new Book(...)` 出来的对象
   - 没有持久化身份（通常 `id == null`）

2. **managed（受管/持久化上下文内）**
   - Entity 被当前 persistence context 管理
   - 对它的字段修改会被“脏检查”捕获（见 [docs/04](04-dirty-checking.md)）

3. **detached（游离/脱管）**
   - Entity 曾经是 managed，但被 detach/clear 后不再受当前 context 管理
   - 修改它不会自动同步到数据库（除非 merge 回去）

4. **removed（已标记删除）**
   - 删除操作在 flush/commit 时真正反映到数据库

- `entityIsManagedAfterSaveInSamePersistenceContext`
  - `repository.save(...)` 后，`entityManager.contains(saved)` 为 true（managed）
- `entityManagerClearDetachesEntities`
  - `entityManager.clear()` 后，`contains(saved)` 变成 false（detached）

## 应当得到的结论

- JPA 不是“直接对数据库写”，而是“先写进 persistence context，再在 flush/commit 时同步”
- 所以后面学 flush / dirty checking / N+1 时，逻辑都会回到这套状态机上

## 最小可运行实验（Lab）

- Lab：`BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）


## 在本模块如何验证

看 `BootDataJpaLabTest`：

## 常见坑与边界

### 坑点 1：修改 detached entity 以为能落库，结果“改了但没生效”

拿着一个对象改字段，flush/commit 后数据库没变化，于是怀疑“JPA 不可靠”

dirty checking 的前提是 entity 必须处于 managed 状态；detach/clear 后对象不再受 persistence context 管理

- clear 后 contains=false（detached）：`BootDataJpaLabTest#entityManagerClearDetachesEntities`
- 只有 managed + flush 才会落库：`BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`

把“对象现在是不是 managed”作为排障第一问；需要重新纳管时用 merge/重新查询再修改

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-data-jpa/02-persistence-context.md](02-persistence-context.md)

<!-- BOOKIFY:END -->
