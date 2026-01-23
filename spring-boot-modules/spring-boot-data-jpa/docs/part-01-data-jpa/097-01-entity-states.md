# 第 97 章：01. Entity 状态机：transient / managed / detached / removed
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Entity 状态机：transient / managed / detached / removed
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `JpaRepository` 声明 CRUD/查询；在事务内修改 managed entity 依赖脏检查落库；用 fetch join/EntityGraph 控制 fetching，避免 N+1。
    - 原理：Repository 代理 → `EntityManager`/Persistence Context（一级缓存、实体状态）→ flush/dirty checking → 事务提交/回滚 → fetching 策略决定性能与边界。
    - 源码入口：`org.springframework.data.jpa.repository.support.SimpleJpaRepository` / `org.springframework.data.jpa.repository.support.JpaRepositoryFactory` / `jakarta.persistence.EntityManager` / `org.springframework.orm.jpa.JpaTransactionManager`
    - 推荐 Lab：`BootDataJpaLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 96 章：00 - Deep Dive Guide（springboot-data-jpa）](../part-00-guide/096-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 98 章：02. Persistence Context：JPA 的“一级缓存”与事务绑定](098-02-persistence-context.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01. Entity 状态机：transient / managed / detached / removed**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootDataJpaLabTest`

## 机制主线

学 JPA 最容易“学成玄学”的原因是：你只看到了 `repository.save()`，没看清楚背后有一套明确的状态机。

这一章只讲一个问题：

> **一个 Entity 在生命周期里会经历哪些状态？这些状态有什么可观察的后果？**

## 四种经典状态（务必记住）

1. **transient（瞬时/新建）**
   - 你 `new Book(...)` 出来的对象
   - 没有持久化身份（通常 `id == null`）

2. **managed（受管/持久化上下文内）**
   - Entity 被当前 persistence context 管理
   - 你对它的字段修改会被“脏检查”捕获（见 [docs/04](100-04-dirty-checking.md)）

3. **detached（游离/脱管）**
   - Entity 曾经是 managed，但被 detach/clear 后不再受当前 context 管理
   - 修改它不会自动同步到数据库（除非你 merge 回去）

4. **removed（已标记删除）**
   - 删除操作在 flush/commit 时真正反映到数据库

- `entityIsManagedAfterSaveInSamePersistenceContext`
  - `repository.save(...)` 后，`entityManager.contains(saved)` 为 true（managed）
- `entityManagerClearDetachesEntities`
  - `entityManager.clear()` 后，`contains(saved)` 变成 false（detached）

## 你应该得到的结论

- JPA 不是“直接对数据库写”，而是“先写进 persistence context，再在 flush/commit 时同步”
- 所以你后面学 flush / dirty checking / N+1 时，逻辑都会回到这套状态机上

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootDataJpaLabTest`
- 建议命令：`mvn -pl :spring-boot-data-jpa test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `BootDataJpaLabTest`：

## 常见坑与边界

### 坑点 1：修改 detached entity 以为能落库，结果“改了但没生效”

- Symptom：你拿着一个对象改字段，flush/commit 后数据库没变化，于是怀疑“JPA 不可靠”
- Root Cause：dirty checking 的前提是 entity 必须处于 managed 状态；detach/clear 后对象不再受 persistence context 管理
- Verification：
  - clear 后 contains=false（detached）：`BootDataJpaLabTest#entityManagerClearDetachesEntities`
  - 只有 managed + flush 才会落库：`BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`
- Fix：把“对象现在是不是 managed”作为排障第一问；需要重新纳管时用 merge/重新查询再修改

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/096-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-data-jpa/02-persistence-context.md](098-02-persistence-context.md)

<!-- BOOKIFY:END -->
