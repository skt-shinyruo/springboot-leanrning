# 第 126 章：Events 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Events 主线
    - 怎么使用：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 125 章：99 - Self Check（spring-boot-async-scheduling）](../async-scheduling/spring-boot-async-scheduling/appendix/125-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 127 章：主线时间线：Spring Events](../events/spring-core-events/part-00-guide/127-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**为什么发布一个事件就能触发多个监听器、异常怎么传播、事务提交后再发事件该怎么写**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Events 主线 —— 通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
- 回到主线：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Events 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. 发布：`ApplicationEventPublisher` 发出事件
2. 分发：`ApplicationEventMulticaster` 找到匹配的监听器并调用
3. 同步 vs 异步：默认同步；引入 executor 后变成异步
4. 事务事件：`@TransactionalEventListener` 在 AFTER_COMMIT 等时机触发
5. 常见坑：监听器顺序、异常传播、异步测试不稳定、事务回滚时事件是否触发

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/events/spring-core-events/part-00-guide/128-02-breakpoint-map.md`](../events/spring-core-events/part-00-guide/128-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/events/spring-core-events/part-00-guide/128-04-branch-decision-matrix.md`](../events/spring-core-events/part-00-guide/128-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/events/spring-core-events/appendix/136-90-common-pitfalls.md`](../events/spring-core-events/appendix/136-90-common-pitfalls.md)
- 自检清单：[`docs/events/spring-core-events/appendix/137-99-self-check.md`](../events/spring-core-events/appendix/137-99-self-check.md)

- 模块目录页：[`docs/events/spring-core-events/README.md`](../events/spring-core-events/README.md)
- 模块主线时间线（含可跑入口）：[`docs/events/spring-core-events/part-00-guide/03-mainline-timeline.md`](../events/spring-core-events/part-00-guide/127-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsLabTest test`（`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`（`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - 基础事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`（`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBasicsBranchMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - 异步/事务事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`（`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsAsyncTransactionalBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseTest.java`

---

## 下一章怎么接

把事件跑通后，很多问题会落到“资源加载/扫描”：例如 classpath、jar、pattern matching。我们进入 Resources 主线。

- 下一章：[第 138 章：Resources 主线](138-resources-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Events 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.context.event.SimpleApplicationEventMulticaster`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Events 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.context.event.ApplicationListenerMethodAdapter`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Events 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.support.TransactionSynchronizationManager`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreEventsLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
