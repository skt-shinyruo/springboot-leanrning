# 04. 同步与异常传播：为什么监听器抛异常会“炸到发布方”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕同步与异常传播：为什么监听器抛异常会“炸到发布方”？展开，主线可以概括为：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。

    先运行 `SpringCoreEventsMechanicsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。

    需要下探源码时，可以从 `org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. condition 与 payload：监听器为什么能“按条件触发”甚至接收普通对象？](event-basics-condition-and-payload.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 异步监听器：`@Async` 生效需要什么？线程会怎么变？](async-and-transactional-async-listener.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreEventsMechanicsLabTest`

## 机制主线

事件默认是同步的，所以异常传播也很直观：

> 监听器在发布方的调用栈里执行，因此监听器抛异常会直接传播回发布方。

- 监听器直接 `throw new IllegalStateException("listener boom")`
- `context.publishEvent(...)` 会抛出同样的异常

## 应当得到的结论

1. **同步事件不是“吞异常”的机制**

- 如果希望“监听器失败不影响主流程”，需要显式设计（比如异步、隔离、重试等）
- 学习仓库里先理解默认行为，再谈工程化处理

2. **事件不是“保证交付”的消息系统**

进程内同步事件更接近“回调链”：

- 快
- 简单
- 但耦合在同一个线程与同一段调用链上

## 验证路径：如何避免“学歪”

当希望用事件做解耦时，先问自己：

- 这个动作如果失败，是否应该让主流程失败？
  - 应该：同步事件 + 异常传播是合理的
  - 不应该：考虑异步/隔离（见 [05. async-listener](async-and-transactional-async-listener.md) 与 [06. async-multicaster](async-and-transactional-async-multicaster.md)）

## 最小可运行实验（Lab）

- Lab：`SpringCoreEventsMechanicsLabTest`
- 运行命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 在本模块如何验证

看 `SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault`

## 常见坑与边界

### 坑点 1：以为“监听器失败不会影响主流程”，结果异常直接炸回发布方

某个监听器抛异常后，发布事件的主流程也失败，导致误判“业务逻辑本身坏了”

同步事件的监听器在发布方调用栈里执行，异常默认向发布方传播

`SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault`

需要“监听器失败不影响主流程”时，选择异步/隔离策略（并明确异常处理与补偿），不要把同步事件当消息队列

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreEventsMechanicsLabTest`

上一章：[03-condition-and-payload](event-basics-condition-and-payload.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05-async-listener](async-and-transactional-async-listener.md)

<!-- BOOKIFY:END -->
