# 第 135 章：07. `@TransactionalEventListener`：为什么 after-commit 事件能“等事务提交后再执行”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`@TransactionalEventListener`：为什么 after-commit 事件能“等事务提交后再执行”？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsTransactionalEventLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 134 章：06. 异步广播：让事件“默认异步”而不是靠 `@Async`](134-06-async-multicaster.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 136 章：90. 常见坑清单（建议反复对照）](../appendix/136-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**07. `@TransactionalEventListener`：为什么 after-commit 事件能“等事务提交后再执行”？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreEventsTransactionalEventLabTest`
    - Test file：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsTransactionalEventLabTest.java`

## 机制主线

很多学习者在事件与事务结合时会遇到一个困惑：

> “我事务都回滚了，为什么监听器还执行了？”

你看到的不是“事务没回滚”，而是**副作用发生在事务之外**。

原因很朴素：`@EventListener` 默认就是同步回调——事件发布的那一刻，监听器就已经在同一条调用链里执行过了。事务随后回滚，只会撤销数据库提交，不会“自动撤销”你在监听器里做过的事情（发消息、写审计、调用外部 API）。

所以这类问题的关键从来不是“要不要用事件”，而是：

- 这段副作用应该发生在**提交之前**还是**提交之后**？
- 如果事务回滚，这段副作用应该**补偿**、**忽略**，还是**禁止发生**？

为了解决这个问题，Spring 提供了 `@TransactionalEventListener`：

> 把监听器的触发时机绑定到事务生命周期（例如 AFTER_COMMIT / AFTER_ROLLBACK / AFTER_COMPLETION）。

## 你需要记住的 2 种监听器

1) `@EventListener`（默认同步）

- 事件发布后立刻执行（在同一调用链里）
- 即使外层事务最终回滚，监听器也已经执行过了

2) `@TransactionalEventListener(phase = AFTER_COMMIT)`

- 事件先“挂起”，等事务提交后再触发
- 如果事务回滚，AFTER_COMMIT 监听器不会执行

## 在本仓库如何“看见”差异（两条入口）

这类问题最怕“凭感觉”：你以为 after-commit 会触发，但它只在事务真正 commit 时触发；你以为回滚会撤销 listener，但同步 listener 早就执行完了。

本仓库提供两条可以直接跑的入口（建议先跑其一，再带着断言回看源码/断点）：

- **纯机制入口（推荐先跑）**：`SpringCoreEventsTransactionalEventLabTest`
  - 对比：`@EventListener`（立刻执行） vs `@TransactionalEventListener(AFTER_COMMIT)`（提交后执行）
- **更接近真实业务入口**：`spring-boot-business-case` 的 `BootBusinessCaseLabTest`
  - 对比：事务回滚时 `sync` listener 仍执行，但 `afterCommit` 不会执行

推荐命令（任选其一）：

```bash
mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsTransactionalEventLabTest test
# 或
mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseLabTest test
```

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBusinessCaseLabTest` / `SpringCoreEventsTransactionalEventLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

- 代码入口：`spring-boot-modules/spring-boot-business-case/src/main/java/com/learning/springboot/bootbusinesscase/events/OrderEventListeners.java`
- 行为断言：`BootBusinessCaseLabTest` 里有两类断言
  - 成功提交：既有 `sync:` 也有 `afterCommit:` 日志
  - 回滚失败：只有 `sync:`，不会出现 `afterCommit:`

```bash
mvn -pl :spring-boot-business-case test
```

- `BootBusinessCaseLabTest#syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot`
- `BootBusinessCaseLabTest#afterCommitListenerRunsOnSuccess`

## 对应 Lab/Test（可运行）

- 入口测试：
  - `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsTransactionalEventLabTest.java`
    - `afterCommitListenerRunsOnlyAfterCommit`
    - `afterCommitDoesNotRunOnRollback_butAfterRollbackDoes`
- 推荐运行命令：
  - `mvn -pl :spring-core-events -Dtest=SpringCoreEventsTransactionalEventLabTest test`

## 常见坑与边界

### 坑点 1：你以为 “after-commit 一定会触发”，但事务回滚/没有事务时它根本不会跑

- Symptom：你发布了事件，但 `@TransactionalEventListener(phase = AFTER_COMMIT)` 的监听器完全没触发；或者本地调试“偶发触发/偶发不触发”。
- Root Cause：`AFTER_COMMIT` 监听器依赖事务同步回调：**只有事务真正提交**才会触发；如果事务回滚，或者发布事件时根本没有活跃事务，就不会进入 after-commit（除非显式启用 fallback 行为）。
- Verification：`SpringCoreEventsTransactionalEventLabTest#afterCommitListenerRunsOnlyAfterCommit`、`SpringCoreEventsTransactionalEventLabTest#afterCommitDoesNotRunOnRollback_butAfterRollbackDoes`
- Breakpoints：`TransactionalApplicationListenerMethodAdapter#onApplicationEvent`、`TransactionSynchronizationManager#isSynchronizationActive`
- Fix：确保事件发布发生在事务边界内，并根据语义选对 phase（需要回滚补偿就用 `AFTER_ROLLBACK` / `AFTER_COMPLETION`）；把“提交/回滚 → 哪些 listener 触发”的结论写进 Lab/Test，避免只靠经验判断。

- `@EventListener` 默认是“同步回调”，它不理解事务边界  
- 事务回滚只影响数据库提交，不会自动撤销你已经执行过的监听器逻辑

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：事务事件监听的关键不是“事件发布了没有”，而是“监听器在哪个事务阶段触发”——默认 `@EventListener` 会立刻执行，而 `@TransactionalEventListener` 可以把副作用推迟到 AFTER_COMMIT/AFTER_ROLLBACK 等阶段。
- 回到主线：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsTransactionalEventLabTest`
- Test file：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsTransactionalEventLabTest.java`

上一章：[06-async-multicaster](134-06-async-multicaster.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/136-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
