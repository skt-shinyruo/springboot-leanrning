# 第 132 章：04. 同步与异常传播：为什么监听器抛异常会“炸到发布方”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：同步与异常传播：为什么监听器抛异常会“炸到发布方”？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 131 章：03. condition 与 payload：监听器为什么能“按条件触发”甚至接收普通对象？](131-03-condition-and-payload.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 133 章：05. 异步监听器：`@Async` 生效需要什么？线程会怎么变？](../part-02-async-and-transactional/133-05-async-listener.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. 同步与异常传播：为什么监听器抛异常会“炸到发布方”？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreEventsMechanicsLabTest`

## 机制主线

事件默认是同步的，所以异常传播也很直观：

> 监听器在发布方的调用栈里执行，因此监听器抛异常会直接传播回发布方。

- 监听器直接 `throw new IllegalStateException("listener boom")`
- `context.publishEvent(...)` 会抛出同样的异常

## 你应该得到的结论

1) **同步事件不是“吞异常”的机制**

- 如果你希望“监听器失败不影响主流程”，你需要显式设计（比如异步、隔离、重试等）
- 学习仓库里建议先理解默认行为，再谈工程化处理

2) **事件不是“保证交付”的消息系统**

进程内同步事件更像“回调链”：

- 快
- 简单
- 但耦合在同一个线程与同一段调用链上

## 学习建议：如何避免“学歪”

当你想用事件做解耦时，先问自己：

- 这个动作如果失败，是否应该让主流程失败？
  - 应该：同步事件 + 异常传播是合理的
  - 不应该：考虑异步/隔离（见 [05. async-listener](../part-02-async-and-transactional/133-05-async-listener.md) 与 [06. async-multicaster](../part-02-async-and-transactional/134-06-async-multicaster.md)）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreEventsMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault`

## 常见坑与边界

### 坑点 1：以为“监听器失败不会影响主流程”，结果异常直接炸回发布方

- Symptom：某个监听器抛异常后，发布事件的主流程也失败，导致你误判“业务逻辑本身坏了”
- Root Cause：同步事件的监听器在发布方调用栈里执行，异常默认向发布方传播
- Verification：`SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault`
- Fix：需要“监听器失败不影响主流程”时，选择异步/隔离策略（并明确异常处理与补偿），不要把同步事件当消息队列

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsMechanicsLabTest`

上一章：[03-condition-and-payload](131-03-condition-and-payload.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05-async-listener](../part-02-async-and-transactional/133-05-async-listener.md)

<!-- BOOKIFY:END -->
