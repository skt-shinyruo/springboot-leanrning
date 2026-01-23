# 第 127 章：主线时间线：Spring Events
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Events
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 126 章：Events 主线](../../../book/126-events-mainline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 128 章：深挖指南（Spring Core Events）](128-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：事件发布/监听在 Spring 中如何工作，以及同步/异步/事务事件的边界与落地方式。
    - 读完你应该能复述：**发布事件 → Multicaster 分发 → Listener 执行（同步/异步/事务）** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01（事件基础）→ Part 02（异步与事务）→ 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`SpringCoreEventsLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Events —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
- 回到主线：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Events」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 事件是“解耦工具”：它把调用关系从“直接调用”变成“发布/订阅”，但也会带来时序与可见性问题。
- 当事件变成异步或与事务绑定时，问题常常来自“什么时候发布、什么时候真正执行”。

## 主线时间线（建议顺读）

1. 先建立心智模型：事件到底是什么、与直接调用的差异是什么
   - 阅读：[01. 事件心智模型](../part-01-event-basics/129-01-event-mental-model.md)
2. 多个 Listener 的执行顺序与组合方式
   - 阅读：[02. 多监听器与顺序](../part-01-event-basics/130-02-multiple-listeners-and-order.md)
3. 条件与 payload：如何写出“只在特定条件触发”的事件监听
   - 阅读：[03. 条件与 payload](../part-01-event-basics/131-03-condition-and-payload.md)
4. 同步执行与异常：异常会不会中断后续 listener
   - 阅读：[04. 同步与异常](../part-01-event-basics/132-04-sync-and-exceptions.md)
5. 异步监听：从 @AsyncListener 到 multicaster 的线程模型
   - 阅读：[05. 异步监听](../part-02-async-and-transactional/133-05-async-listener.md)
   - 阅读：[06. 异步 multicaster](../part-02-async-and-transactional/134-06-async-multicaster.md)
6. 事务事件：什么时候触发、提交/回滚会怎么影响执行
   - 阅读：[07. 事务事件监听](../part-02-async-and-transactional/135-07-transactional-event-listener.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/136-90-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/137-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Events」的生效时机/顺序/边界；断点/入口：`org.springframework.context.event.SimpleApplicationEventMulticaster`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Events」的生效时机/顺序/边界；断点/入口：`org.springframework.context.event.ApplicationListenerMethodAdapter`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Events」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.support.TransactionSynchronizationManager`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreEventsLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
