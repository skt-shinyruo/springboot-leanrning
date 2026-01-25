# 第 128 章：深挖指南（Spring Core Events）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖指南（Spring Core Events）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 127 章：主线时间线：Spring Events](127-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 129 章：01. 事件心智模型：发布（publish）与订阅（listen）到底在解耦什么？](../part-01-event-basics/129-01-event-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**深挖指南（Spring Core Events）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsListenerFilteringLabTest`

## 机制主线

事件机制的“深挖主线”只有一句话：

> **publishEvent 只是入口；真正的行为由“监听器选择 + 调用时机 + 线程模型 + 异常策略 + 事务阶段”共同决定。**

建议用“同步 → 顺序/条件 → 异步 → 事务事件”四段式把主线跑通。

### 1) 时间线：一次 publishEvent 从发布到监听器执行

1. 发布方调用 `ApplicationEventPublisher#publishEvent`
2. 事件被交给 `ApplicationEventMulticaster` 分发
3. 解析并筛选监听器（按事件类型、参数类型、condition 等）
4. 逐个调用监听器
   - 默认是同步执行（在发布线程上）
   - 若监听器本身标了 `@Async` 且启用 async，则会切到 executor 线程
5. 异常传播策略（默认：监听器异常向发布方传播，直接影响发布方）
6. 若是事务事件（`@TransactionalEventListener`）：监听器执行点由事务阶段决定（afterCommit/afterRollback 等）

### 2) 关键参与者（你应该能点名并解释它们做什么）

- `ApplicationEventPublisher`：事件发布入口
- `ApplicationEventMulticaster`：事件分发器（决定“怎么调用监听器”）
- `@EventListener`：声明监听器（参数类型/condition/顺序）
- `@Order`：多监听器的顺序语义（注意：只对同一事件的 listener 排序）
- `@Async` + `@EnableAsync`：异步监听器的开关（没有 EnableAsync 就不会异步）
- `@TransactionalEventListener`：把事件与事务阶段绑定（最终一致性/副作用分离）

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **同步默认值：事件默认在发布线程同步执行**
   - 验证：`SpringCoreEventsLabTest#eventsAreSynchronousByDefault`
2. **异常传播：监听器异常默认向发布方传播（会让 publishEvent 失败）**
   - 验证：`SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault`
3. **顺序与过滤：同一事件可被多个监听器观察，顺序可由 @Order 固定；监听器也会按参数类型过滤**
   - 验证：`SpringCoreEventsLabTest#multipleListenersCanObserveTheSameEvent` / `SpringCoreEventsLabTest#orderedListenersFollowOrderAnnotation` / `SpringCoreEventsListenerFilteringLabTest#eventListener_shouldFilterByMethodParameterType`
4. **异步前提：没有 EnableAsync，@Async listener 仍在发布线程执行；启用后才切线程**
   - 验证：`SpringCoreEventsMechanicsLabTest#asyncAnnotationIsIgnored_withoutEnableAsync` / `SpringCoreEventsMechanicsLabTest#asyncListenerRunsOnDifferentThread_whenEnableAsyncIsOn`
5. **事务阶段：afterCommit 只在提交后触发；回滚时 afterRollback 才触发**
   - 验证：`SpringCoreEventsTransactionalEventLabTest#afterCommitListenerRunsOnlyAfterCommit` / `SpringCoreEventsTransactionalEventLabTest#afterCommitDoesNotRunOnRollback_butAfterRollbackDoes`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。
  
建议断点（从“事件不生效/顺序不对/线程不对”快速分流）：

- 事件分发入口：`org.springframework.context.event.SimpleApplicationEventMulticaster#multicastEvent`
- 注解监听器调用：`org.springframework.context.event.ApplicationListenerMethodAdapter#doInvoke`
- 事务事件触发：`org.springframework.transaction.event.TransactionalApplicationListenerMethodAdapter#onApplicationEvent`
- 异步 listener 分流：对照 `SpringCoreEventsMechanicsLabTest` 两个 async 用例，先锁住“是否启用 async”，再讨论线程池配置

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsListenerFilteringLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 验证入口（可跑）：
> - `SpringCoreEventsLabTest`
> - `SpringCoreEventsMechanicsLabTest`
> - `SpringCoreEventsListenerFilteringLabTest`

本模块建议按以下节奏阅读与验证：

配套验证入口：
- Labs/Exercises：见 `src/test/java/com/learning/springboot/springcoreevents/**`

## 常见坑与边界

- （本章坑点待补齐：建议先跑一次 E，再回看断言失败场景与边界条件。）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsListenerFilteringLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01-event-mental-model](../part-01-event-basics/129-01-event-mental-model.md)

<!-- BOOKIFY:END -->
