# 05. `@Scheduled` 基础与可测试性
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（调度别靠“等它触发”）"

    `@Scheduled` 最容易把人带沟里的一点是：你以为它是“方法调用时拦截”，但它其实是**启动期注册任务，运行期按时间触发**。

    - 排障三步：开关（EnableScheduling）→ 注册（任务是否进了注册表）→ 触发（线程与异常语义）
    - 测试写法：优先断言“注册结果”，只在必要时做最小触发验证
    - 进一步验证：`BootAsyncSchedulingSchedulingRegistrationLabTest#scheduledTasksAreRegisteredAsDifferentTaskTypes`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. self-invocation：为什么异步有时不生效](04-self-invocation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. `@Async` × `@Transactional`：事务边界与执行线程](06-async-and-transactions.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. `@Scheduled` 基础与可测试性**
- 建议入口：优先运行 `BootAsyncSchedulingSchedulingRegistrationLabTest#scheduledTasksAreRegisteredAsDifferentTaskTypes`（见文末“对应 Lab/Test”），先把“注册语义”钉住，再做最小触发验证。



## 把 `@Scheduled` 当成“系统级开关”，排障会简单很多

`@Scheduled` 最烦人的地方往往不是 cron 表达式，而是它的生效过程和 `@Async` 完全不同：它不是“调用期拦截”，而是**启动期注册 + 运行期触发**。

所以我更喜欢把它拆成三个问题来问（排障也沿这三步走）：

1. scheduling 开关是否打开（`@EnableScheduling`）
2. 任务有没有被注册（注册断言）
3. 任务有没有按预期触发、在哪个线程执行、异常语义是什么（触发验证）

这三步对应三类不同的失败模式：没触发（开关/注册问题）、触发了但跑得不对（线程模型问题）、偶发不稳定（测试写法或异常语义没搞清）。

## 1) 开关：没有 `@EnableScheduling` 就谈不上调度

最小事实：

- 没开 scheduling：任务不会触发  
  - 证据入口：`BootAsyncSchedulingSchedulingLabTest#schedulingRequiresEnableScheduling`

## 2) 注册：比“等它触发”更确定

很多时候你关心的不是“它过了 3 秒有没有跑”，而是“它到底有没有被注册成一个任务”。这个问题最稳的回答方式，是直接断言注册结果：

- `ScheduledTaskHolder` 里持有已注册任务
- fixedRate / fixedDelay / cron 会被注册为不同 task 类型

证据入口：

- `BootAsyncSchedulingSchedulingRegistrationLabTest#scheduledTasksAreRegisteredAsDifferentTaskTypes`

## 3) 触发：怎么写不 flaky 的断言

当你必须证明“确实触发执行了”，本模块的取向是：

- 用 `CountDownLatch` 抓住第一次触发（它把结论锁在同步点上）
- 永远设置超时上限（避免测试挂死）
- 尽量避免长时间 `Thread.sleep`

证据入口：

- `BootAsyncSchedulingSchedulingLabTest#schedulingTriggersTaskWhenEnableSchedulingPresent`

## 定时任务抛异常会怎样

线上一个很常见的误会是：“定时任务抛异常会不会就此停掉？”

底层如果直接用 `ScheduledExecutorService` 的原生行为，确实可能因为异常导致后续执行被取消；但 Spring 通常会用异常包装与 `ErrorHandler` 兜住，避免异常把任务直接“炸没”。

证据入口（异常进入 ErrorHandler，同时任务仍继续触发）：

- `BootAsyncSchedulingSchedulingExceptionSemanticsLabTest#scheduledExceptionsAreHandledByErrorHandler_andTaskContinues`

## 组合注解：`@Scheduled + @Async`

调度线程池通常应该保持轻量：负责触发，不负责耗时执行。耗时逻辑如果直接跑在 scheduler 线程上，任务多了之后你会看到延迟、堆积、甚至互相拖死。

当同一个方法同时标注 `@Scheduled` 与 `@Async` 时：

- 触发发生在 scheduler 线程（常见前缀：`sched-`）
- 方法体执行切换到 async executor 线程（常见前缀：`async-`）

证据入口：

- `BootAsyncSchedulingScheduledAsyncCombinationLabTest#scheduledRunsOnSchedulerThread_butScheduledPlusAsyncRunsOnExecutorThread`

## 断点入口（可选）

- 注册入口：`org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#processScheduled`
- 执行入口：`org.springframework.scheduling.support.ScheduledMethodRunnable#run`

## 进一步验证（可选）

这一章相关的最小集合是：

- `BootAsyncSchedulingSchedulingLabTest`（开关与最小触发）
- `BootAsyncSchedulingSchedulingRegistrationLabTest`（注册断言）
- `BootAsyncSchedulingSchedulingExceptionSemanticsLabTest`（异常语义）
- `BootAsyncSchedulingScheduledAsyncCombinationLabTest`（组合注解的线程边界）

## 小结

对调度来说，“注册断言”是你最稳妥的朋友：它能把时间相关的不确定性压到最低。只有当你必须证明“确实触发执行”时，才让时间真正参与断言。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`

上一章：[part-01-async-scheduling/04-self-invocation.md](04-self-invocation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/06-async-and-transactions.md](06-async-and-transactions.md)

<!-- BOOKIFY:END -->
