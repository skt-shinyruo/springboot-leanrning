# 第 116 章：Async/Scheduling 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Async/Scheduling 主线
    - 怎么使用：用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。
    - 原理：方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。
    - 源码入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor` / `org.springframework.aop.interceptor.AsyncExecutionInterceptor` / `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor` / `org.springframework.core.task.TaskExecutor`
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 115 章：99 - Self Check（spring-boot-cache）](../cache/spring-boot-cache/appendix/115-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 117 章：主线时间线：Spring Boot Async & Scheduling](../async-scheduling/spring-boot-async-scheduling/part-00-guide/117-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

当你把应用写到“能跑”之后，很快就会遇到并发与时间：慢任务要丢到后台、外部调用要异步、定时任务要按点触发。

也正因为它们“看起来很简单”，坑往往更隐蔽：

- `@Async` 贴上了却不生效（尤其是自调用场景）；
- 异步里抛异常，日志里却像“什么都没发生”；
- 定时任务开始堆积、并发、甚至把线程池打满。

本章希望你建立一条可以复述、可以调试的主线：

**方法调用 → 代理拦截 → 提交到 Executor/Scheduler → 执行/异常处理 →（可验证的证据）**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Async/Scheduling 主线 —— 用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。
- 回到主线：方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Async/Scheduling 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 你将学到什么（本章目标）

读完本章，你应该能做到：

1. 解释 `@Async` 为什么本质仍是代理，以及它的边界在哪里（尤其是 self-invocation）
2. 描述任务是如何被提交到线程池的：队列/线程数/拒绝策略带来的行为差异
3. 说明异常与返回值的语义：`void`/`Future`/`CompletableFuture` 的差异与“异常去哪了”
4. 对 `@Scheduled` 的触发与并发有清晰预期：fixedDelay/fixedRate/cron 的核心差异
5. 写出可稳定断言的测试（避免“异步测试偶尔红”）

---

## 主线（按时间线顺读）

把“异步/调度”当成一种“把执行从当前线程移走”的机制，主线如下：

1. 你声明意图：`@Async` / `@Scheduled` 把“并发/按时触发”变成显式边界
2. 容器创建代理：调用边界进入拦截器（如果没走到代理，这个功能就等于没开启）
3. 任务提交：把 Runnable/Callable 交给 `TaskExecutor`（异步）或 Scheduler（调度）
4. 任务执行：在线程池线程里跑；线程模型决定并发、堆积、吞吐与延迟
5. 结果与异常：返回值类型与异常处理策略决定“你能不能看见失败”

---

## 关键分支（读者检查点）

你在排障时，优先把问题归类到下面三个分支之一：

1. **没代理上**：为什么 `@Async` 不生效？（自调用/注入方式/代理类型）
2. **线程池策略**：为什么开始堆积或并发失控？（队列/核心线程/拒绝策略/调度触发语义）
3. **异常与测试**：为什么“看不见异常”或“测试不稳定”？（返回值语义、异常处理器、等待/同步方式）

---

## 读书式的“证据链”：你该如何验证

跑 Lab 时建议你至少验证两件事：

- **线程是否真的切走了**：观察线程名/线程 id（不要只看日志时间）
- **异常是否可见**：明确异常会走到哪里（日志、Future、回调、或被吞掉），并用断言把它固定住

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/118-02-breakpoint-map.md`](../async-scheduling/spring-boot-async-scheduling/part-00-guide/118-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/118-04-branch-decision-matrix.md`](../async-scheduling/spring-boot-async-scheduling/part-00-guide/118-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/async-scheduling/spring-boot-async-scheduling/appendix/124-90-common-pitfalls.md`](../async-scheduling/spring-boot-async-scheduling/appendix/124-90-common-pitfalls.md)
- 自检清单：[`docs/async-scheduling/spring-boot-async-scheduling/appendix/125-99-self-check.md`](../async-scheduling/spring-boot-async-scheduling/appendix/125-99-self-check.md)

- 模块目录页：[`docs/async-scheduling/spring-boot-async-scheduling/README.md`](../async-scheduling/spring-boot-async-scheduling/README.md)
- 模块主线时间线（含可跑入口）：[`docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/03-mainline-timeline.md`](../async-scheduling/spring-boot-async-scheduling/part-00-guide/117-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingLabTest test`（`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`（`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`（`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`

---

## 下一章怎么接

异步是“把执行切走”，另一种常见的解耦方式是“把调用关系切开”：发布事件，让多个监听器订阅与执行。

- 下一章：[第 126 章：Events 主线](126-events-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Async/Scheduling 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Async/Scheduling 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.interceptor.AsyncExecutionInterceptor`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Async/Scheduling 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootAsyncSchedulingLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
