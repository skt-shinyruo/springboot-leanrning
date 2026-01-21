# 第 130 章：02. 多监听器与顺序：为什么 `@Order` 值得你认真对待？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：多监听器与顺序：为什么 `@Order` 值得你认真对待？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 129 章：01. 事件心智模型：发布（publish）与订阅（listen）到底在解耦什么？](129-01-event-mental-model.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 131 章：03. condition 与 payload：监听器为什么能“按条件触发”甚至接收普通对象？](131-03-condition-and-payload.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 多监听器与顺序：为什么 `@Order` 值得你认真对待？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreEventsLabTest`

## 机制主线

一个事件通常不止一个监听器想要处理：

- 写审计日志
- 发欢迎通知
- 统计指标
- 触发后续异步任务

本章关注两个问题：

1) 多个监听器会不会都收到同一个事件？  
2) 多个监听器的执行顺序能不能依赖？

## 1) 多监听器：同一个事件会被“广播”

本模块用 `@Import` 注入了一个额外监听器：

- `ExtraUserRegisteredListener` 也会接收 `UserRegisteredEvent`
- 因此 audit log 里会出现两条记录

## 2) 顺序：默认不要依赖“自然顺序”

如果你没有显式指定顺序：

- 监听器的执行顺序可能与你的想象不一致
- 甚至在不同 JVM / 不同构建方式下表现不同

当你确实需要顺序（学习阶段很常见，因为你要做确定性断言），就用 `@Order`：

## 你应该得到的结论

- 多监听器是事件机制的常态：它让你可以在不改发布方的情况下持续扩展能力
- 顺序默认不保证：需要确定性时就显式标注 `@Order`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreEventsLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

验证入口：`SpringCoreEventsLabTest#multipleListenersCanObserveTheSameEvent`

验证入口：`SpringCoreEventsLabTest#orderedListenersFollowOrderAnnotation`

## 常见坑与边界

### 坑点 1：依赖“自然顺序”，导致监听器执行顺序在不同环境下不稳定

- Symptom：本地顺序正常，换了 JVM/构建方式后顺序变化，引发副作用顺序问题（日志/审计/补偿）
- Root Cause：不显式声明顺序时，监听器顺序不应被依赖；需要确定性就用 `@Order`
- Verification：`SpringCoreEventsLabTest#orderedListenersFollowOrderAnnotation`
- Fix：当顺序是业务语义的一部分时就显式 `@Order`；否则把监听器设计成顺序无关（幂等/无共享可变状态）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsLabTest`

上一章：[01-event-mental-model](129-01-event-mental-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03-condition-and-payload](131-03-condition-and-payload.md)

<!-- BOOKIFY:END -->
