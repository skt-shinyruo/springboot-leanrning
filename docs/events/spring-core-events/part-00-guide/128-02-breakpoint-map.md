# 第 128 章：02：断点地图（Spring Events Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Spring Events Debugger Pack）
    - 怎么使用：先跑 `SpringCoreEventsBasicsBranchMatrixLabTest`/`SpringCoreEventsAsyncTransactionalBranchMatrixLabTest` 固化“同步/异步/事务事件”的断言，再用断点观察 multicaster 如何选择 listener、在哪个线程执行、异常如何传播。
    - 原理：publishEvent → ApplicationEventMulticaster 选 listener → 逐个调用（同步/异步取决于 multicaster/executor）→ 事务事件依赖事务同步回调。
    - 源码入口：`org.springframework.context.event.AbstractApplicationEventMulticaster` / `org.springframework.context.event.SimpleApplicationEventMulticaster`
    - 推荐 Lab：`SpringCoreEventsBasicsBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 128 章：00 - Deep Dive Guide（spring-core-events）](128-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 128 章：04：关键分支矩阵（Branch Decision Matrix）](128-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

- Matrix：`SpringCoreEventsBasicsBranchMatrixLabTest` / `SpringCoreEventsAsyncTransactionalBranchMatrixLabTest`
- Lab：`SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsListenerFilteringLabTest` / `SpringCoreEventsAsyncMulticasterLabTest` / `SpringCoreEventsTransactionalEventLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](128-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](128-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

