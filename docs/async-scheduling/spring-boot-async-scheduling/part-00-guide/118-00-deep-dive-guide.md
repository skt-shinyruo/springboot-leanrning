# 第 118 章：00 - Deep Dive Guide（springboot-async-scheduling）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Deep Dive Guide（springboot-async-scheduling）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。
    - 原理：方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。
    - 源码入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor` / `org.springframework.aop.interceptor.AsyncExecutionInterceptor` / `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor` / `org.springframework.core.task.TaskExecutor`
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 117 章：主线时间线：Spring Boot Async & Scheduling](117-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 119 章：01：`@Async` 心智模型：代理与线程切换](../part-01-async-scheduling/119-01-async-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00 - Deep Dive Guide（springboot-async-scheduling）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

## 机制主线

本模块把两个“看起来很简单、但坑很多”的能力拆开讲清楚并用测试锁住：

- `@Async`：本质是 **AOP 代理 + 线程切换**（没有代理就没有 async）
- `@Scheduled`：本质是 **调度器驱动的触发**（没有 EnableScheduling 就不会触发）

### 1) 时间线：`@Async` 从调用到真正在线程池执行

1. Spring 在启动时创建 `@Async` 相关基础设施（需要 `@EnableAsync`）
2. Bean 被包装为代理（可通过 `AopUtils.isAopProxy` 验证）
3. 你调用方法时，调用先进入代理，再被投递到 Executor
4. 异常分流：
   - 返回 `Future/CompletableFuture`：异常回到 future 上（调用方可 get/handle）
   - 返回 `void`：异常走 `AsyncUncaughtExceptionHandler`（容易“悄悄吞掉”）

### 2) 时间线：`@Scheduled` 从启动到触发

1. Spring 在启动时注册 scheduling 基础设施（需要 `@EnableScheduling`）
2. 扫描 `@Scheduled` 方法并注册为任务
3. 调度线程按 fixedDelay/fixedRate/cron 触发任务执行

### 3) 关键参与者

- `@EnableAsync` / `@Async`：决定“是否代理 + 是否异步”
- `TaskExecutor` / `Executor`：决定线程池行为（线程名是最稳定的观测点之一）
- `AsyncUncaughtExceptionHandler`：决定 void async 方法异常如何被观察到
- `@EnableScheduling` / `@Scheduled`：决定“是否注册调度任务 + 触发语义”

### 4) 本模块的关键分支（2–5 条，默认可回归）

1. **没有 `@EnableAsync`：`@Async` 注解不生效（同步执行）**
   - 验证：`BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync`
2. **有 `@EnableAsync`：线程切换到 executor（线程名可作为证据）**
   - 验证：`BootAsyncSchedulingLabTest#asyncRunsOnExecutorThreadWhenEnableAsyncPresent`
3. **异常传播分流：Future 可带回异常；void 走 UncaughtExceptionHandler**
   - 验证：`BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture` / `BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler`
4. **自调用绕过代理（坑点）：self-invocation 让 `@Async` 失效**
   - 验证：`BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall`
5. **调度开关：没有 `@EnableScheduling` 不触发；有则触发**
   - 验证：`BootAsyncSchedulingLabTest#schedulingRequiresEnableScheduling` / `BootAsyncSchedulingLabTest#schedulingTriggersTaskWhenEnableSchedulingPresent`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。
  
建议断点（优先用“证据”定位分支）：

- `@Async` 是否真的走代理：
  - `BootAsyncSchedulingLabTest#asyncRunsOnExecutorThreadWhenEnableAsyncPresent` 的断言处（先锁住线程名与代理存在性）
- 排查 self-invocation：
  - `BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall` 的调用点（观察 outer 调用是否绕过代理）
- `@Scheduled` 是否真的注册任务：
  - 优先通过 `await`/latch 的测试断言判断，再下探到 scheduling 基础设施（避免靠日志猜）

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`
- 建议命令：`mvn -pl :spring-boot-async-scheduling test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 如何跑实验
- 运行本模块测试：`mvn -pl :spring-boot-async-scheduling test`

## 对应 Lab（可运行）

- `BootAsyncSchedulingLabTest`
- `BootAsyncSchedulingSchedulingLabTest`
- `BootAsyncSchedulingExerciseTest`

## 常见坑与边界

## 推荐学习目标
1. 能解释 `@Async` 为什么依赖代理（以及它和 AOP 的共性）
2. 能把“线程在哪里切换”的证据写进测试或日志
3. 能解释自调用为何会绕过 `@Async`
4. 能理解 `@Scheduled` 的基本触发语义与边界

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`
- Exercise：`BootAsyncSchedulingExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/01-async-proxy-mental-model.md](../part-01-async-scheduling/119-01-async-proxy-mental-model.md)

<!-- BOOKIFY:END -->
