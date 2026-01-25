# 第 128 章：02：断点地图（Spring Events Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Spring Events Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsMechanicsLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 128 章：00 - Deep Dive Guide（spring-core-events）](128-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 128 章：04：关键分支矩阵（Branch Decision Matrix）](128-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Spring Events Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
- 回到主线：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- Events 排障的第一问题：**listener 有没有被匹配到**；第二问题：**在哪个线程执行**；第三问题：**异常会不会影响发布者**。
- 推荐证据链：测试断言（trace/线程名/事件次数）→ 断点（multicastEvent）→ Watchpoints（listeners 列表）。

## 运行入口（建议先跑）

- Book Matrix：`SpringCoreEventsBookMatrixLabTest`
- Branch Matrix（基础）：`SpringCoreEventsBasicsBranchMatrixLabTest`
- Branch Matrix（Async/Tx）：`SpringCoreEventsAsyncTransactionalBranchMatrixLabTest`

## 断点（发布与分发）

- `org.springframework.context.support.AbstractApplicationContext#publishEvent`
- `org.springframework.context.event.AbstractApplicationEventMulticaster#multicastEvent`
- `org.springframework.context.event.SimpleApplicationEventMulticaster#doInvokeListener`

## Watchpoints（建议）

- `event` 的类型（payload/condition 依赖）
- `listeners` 列表大小与顺序（order/filtering 的证据）
- `Thread.currentThread().getName()`（同步 vs 异步）
- 事务状态（TransactionalEventListener 分支）：`TransactionSynchronizationManager.isActualTransactionActive()`

## 排障入口（Playbook）

- 常见坑：[`../appendix/136-90-common-pitfalls.md`](../appendix/136-90-common-pitfalls.md)
- 自检：[`../appendix/137-99-self-check.md`](../appendix/137-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsBookMatrixLabTest` / `SpringCoreEventsBasicsBranchMatrixLabTest` / `SpringCoreEventsAsyncTransactionalBranchMatrixLabTest`

上一章：[事务事件](../part-02-async-and-transactional/135-07-transactional-event-listener.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[128-04-branch-decision-matrix.md](128-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
