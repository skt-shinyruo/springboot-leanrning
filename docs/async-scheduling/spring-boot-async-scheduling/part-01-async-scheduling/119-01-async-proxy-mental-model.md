# 第 119 章：01：`@Async` 心智模型：代理与线程切换
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：`@Async` 心智模型：代理与线程切换
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。
    - 原理：方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。
    - 源码入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor` / `org.springframework.aop.interceptor.AsyncExecutionInterceptor` / `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor` / `org.springframework.core.task.TaskExecutor`
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 118 章：00 - Deep Dive Guide（springboot-async-scheduling）](../part-00-guide/118-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 120 章：02：Executor 与线程命名/并发边界](120-02-executor-and-threading.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：`@Async` 心智模型：代理与线程切换**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootAsyncSchedulingLabTest`
    - Test file：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java`

## 机制主线

本章只回答一个问题：**`@Async` 为什么能“切线程”？**

## 你应该观察到什么

- 没有 `@EnableAsync`：`@Async` 就像不存在，方法在当前线程执行
- 有 `@EnableAsync`：bean 会被代理，方法调用经代理转发到 executor 线程

## 机制解释（Why）

`@Async` 本质还是 **代理 + 拦截器**：

- 代理拦截方法调用
- 把“真正执行”提交给 `TaskExecutor`
- 对于返回 `Future/CompletableFuture` 的方法，把结果/异常封装起来返回

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

### 坑点 1：以为写了 `@Async` 就一定异步，忽略了 `@EnableAsync` 是前提

- Symptom：你以为方法已经“切线程”，但实际还是在调用线程里同步执行
- Root Cause：`@Async` 依赖 Spring 创建代理与拦截器；没有 `@EnableAsync` 就不会建立这套基础设施
- Verification：
  - 没有 EnableAsync：`BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync`
  - 有 EnableAsync：`BootAsyncSchedulingLabTest#asyncRunsOnExecutorThreadWhenEnableAsyncPresent`
- Fix：先锁住“代理是否存在 + 线程名是否变化”的证据链，再讨论业务层面的并发语义

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`
- Test file：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/118-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/02-executor-and-threading.md](120-02-executor-and-threading.md)

<!-- BOOKIFY:END -->
