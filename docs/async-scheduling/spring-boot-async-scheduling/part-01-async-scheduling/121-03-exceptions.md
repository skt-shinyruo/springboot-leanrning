# 第 121 章：03：异常传播：Future vs void
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：03：异常传播：Future vs void
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。
    - 原理：方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。
    - 源码入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor` / `org.springframework.aop.interceptor.AsyncExecutionInterceptor` / `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor` / `org.springframework.core.task.TaskExecutor`
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 120 章：02：Executor 与线程命名/并发边界](120-02-executor-and-threading.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 122 章：04：self-invocation：为什么异步有时不生效](122-04-self-invocation.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03：异常传播：Future vs void**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootAsyncSchedulingLabTest`

## 机制主线

本章回答：异步方法抛异常，调用方到底能不能看到？

## 你应该观察到什么

- 返回 Future：异常会进入 Future（调用方 get/join 时才能拿到）
- void：异常不会传回调用方，而是交给 `AsyncUncaughtExceptionHandler`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootAsyncSchedulingLabTest`
- 建议命令：`mvn -pl :spring-boot-async-scheduling test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 常见坑与边界

### 坑点 1：void 异步异常被“悄悄吞掉”，线上只有日志没有告警

- Symptom：异步方法内部抛异常，调用方没有任何感知；线上只在日志里偶然看到 stacktrace
- Root Cause：void async 的异常不会传回调用方，而是交给 `AsyncUncaughtExceptionHandler`
- Verification：`BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler`
- Fix：对需要反馈失败的异步操作优先用 `CompletableFuture`；对 void async 必须配置并验证 UncaughtExceptionHandler

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`

上一章：[part-01-async-scheduling/02-executor-and-threading.md](120-02-executor-and-threading.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/04-self-invocation.md](122-04-self-invocation.md)

<!-- BOOKIFY:END -->
