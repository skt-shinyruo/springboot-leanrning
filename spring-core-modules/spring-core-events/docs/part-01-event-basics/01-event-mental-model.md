# 01. 事件心智模型：发布（publish）与订阅（listen）到底在解耦什么？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕事件心智模型：发布（publish）与订阅（listen）到底在解耦什么？展开，主线可以概括为：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。

    先运行 `SpringCoreEventsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。

    需要下探源码时，可以从 `org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Events）](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 多监听器与顺序：为什么 `@Order` 值得认真对待？](02-multiple-listeners-and-order.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreEventsLabTest`

## 机制主线

Spring 的 Application Events 解决的是一个非常具体的问题：

> 让“发生了什么”（事件）与“需要做什么”（监听器）解耦。

不需要一开始就把它理解成“消息队列”。在本模块里，把它当成**进程内的回调机制**更合适。

## 本模块的最小闭环

- 发布方：`UserRegistrationService`
  - 在 `register(username)` 里发布 `UserRegisteredEvent`
- 监听方：`UserRegisteredListener`
  - `@EventListener` 接收事件，并写入 `InMemoryAuditLog`

## 需要记住的 3 件事

1) **事件默认是同步的**

- `publishEvent(...)` 会在当前线程里依次调用监听器
- 监听器执行完毕，发布方法才会返回

2) **事件类型匹配，决定谁会被调用**

- 监听方法的参数类型决定它能接收什么事件
- 可以发布任何对象（不仅仅是 `ApplicationEvent` 子类）

3) **事件对象建议做成“不可变”**

- 学习阶段特别建议把事件建模为不可变（例如 record）
- 否则多个监听器共享同一个事件对象时，很容易出现“互相污染”的副作用

本模块的 `UserRegisteredEvent` 就是 `record`，非常适合学习。

事件不是为了“炫技”，而是为了让核心流程更清晰：

- 核心流程：只负责发布“发生了什么”
- 扩展动作：由监听器决定“要做什么”

## 最小可运行实验（Lab）

- Lab：`SpringCoreEventsLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

对应测试：`SpringCoreEventsLabTest#listenerReceivesPublishedEvent`

验证入口：`SpringCoreEventsLabTest#eventsAreSynchronousByDefault`

验证入口：`SpringCoreEventsLabTest#publishingPlainObjectsAlsoWorks_asPayloadEvents`

## 常见坑与边界

### 坑点 1：把进程内事件当成“异步消息”，忽略了默认是同步回调链

以为发布事件不会影响主流程耗时/异常，结果发布方被监听器拖慢甚至被异常打断

Spring Application Events 默认同步执行，监听器在发布方调用栈里运行

`SpringCoreEventsLabTest#eventsAreSynchronousByDefault`

先把“同步默认值”当成事实；需要隔离耗时/失败就显式引入异步（@Async 或 async multicaster）并用测试锁定线程模型

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：事件的价值是解耦“发生了什么”和“接下来做什么”——发布方只负责 publish，监听方各自决定动作；但请先把默认同步回调链当成事实，再谈异步/事务阶段。
- 回到主线：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsLabTest`

上一章：[00-deep-dive-guide](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02-multiple-listeners-and-order](02-multiple-listeners-and-order.md)

<!-- BOOKIFY:END -->
