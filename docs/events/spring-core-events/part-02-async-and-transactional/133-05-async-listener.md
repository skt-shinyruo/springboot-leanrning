# 第 133 章：05. 异步监听器：`@Async` 生效需要什么？线程会怎么变？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：异步监听器：`@Async` 生效需要什么？线程会怎么变？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsMechanicsLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 132 章：04. 同步与异常传播：为什么监听器抛异常会“炸到发布方”？](../part-01-event-basics/132-04-sync-and-exceptions.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 134 章：06. 异步广播：让事件“默认异步”而不是靠 `@Async`](134-06-async-multicaster.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 异步监听器：`@Async` 生效需要什么？线程会怎么变？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreEventsMechanicsLabTest`

## 机制主线

异步监听器的目标是把“监听器逻辑”从发布方的线程里拆出去：

- 发布方更快返回
- 监听器在另一个线程执行（通常由线程池提供）

## 关键点：`@Async` 不是“写了就生效”

### 1) 开启 `@EnableAsync`：异步才会生效

看 `asyncListenerRunsOnDifferentThread_whenEnableAsyncIsOn`：

- 配置类加 `@EnableAsync`
- 提供 `ThreadPoolTaskExecutor`，并设置 `threadNamePrefix`
- 断言线程名以 `events-async-` 开头

### 2) 不开启 `@EnableAsync`：`@Async` 会被忽略

看 `asyncAnnotationIsIgnored_withoutEnableAsync`：

- 同样的 listener 方法加了 `@Async`
- 但由于没有 `@EnableAsync`，最终还是在当前线程执行

## 你应该得到的结论

- “我用了 `@Async`，为什么还是同步？”
  - 多半是没启用 async（或线程池没配置）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreEventsMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

验证入口：`SpringCoreEventsMechanicsLabTest`

- 异步不是事件的“默认能力”，而是另一个拦截器机制（`@Async`）叠加出来的
- 学习阶段最好用“线程名断言”来验证异步（比看日志稳定）

## 常见坑与边界

### 坑点 1：`@Async` 写了但没生效（线程没变）

- Symptom：你给 listener 方法加了 `@Async`，但断点/日志显示仍然在发布事件的线程里执行。
- Root Cause：`@Async` 依赖 Spring 的代理机制；如果没有开启 `@EnableAsync`（或 bean 没被代理、发生自调用），`@Async` 会被忽略。
- Verification：`SpringCoreEventsMechanicsLabTest#asyncAnnotationIsIgnored_withoutEnableAsync`、`SpringCoreEventsMechanicsLabTest#asyncListenerRunsOnDifferentThread_whenEnableAsyncIsOn`
- Breakpoints：`AsyncAnnotationBeanPostProcessor#postProcessAfterInitialization`、`AnnotationAsyncExecutionInterceptor#invoke`
- Fix：开启 `@EnableAsync`（并确保 listener 是容器管理的 bean 且不自调用）；用 Lab/Test 把“线程是否变化”的事实固定下来，避免只靠肉眼看日志。

## 常见误区


## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsMechanicsLabTest`

上一章：[同步与异常](../part-01-event-basics/132-04-sync-and-exceptions.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[异步 multicaster](134-06-async-multicaster.md)

<!-- BOOKIFY:END -->
