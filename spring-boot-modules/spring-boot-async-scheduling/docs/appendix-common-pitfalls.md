# 01. 常见坑清单（Async & Scheduling）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（排障短文）"

    这章不想做“把坑列成清单”的那种目录页，我更希望它像在项目里记的排障随笔：每个坑都有复现入口、有根因、有修复方向。

    如果已经跑过一次 `BootAsyncSchedulingBranchMatrixLabTest`，这章读起来会更接近“对照答案”：会发现很多坑其实都在重复问同一件事——有没有走代理、用了哪个执行器、异常落在哪、上下文有没有清理。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[08. Spring Boot `spring.task.*`：默认线程池/调度器与属性映射](async-scheduling-boot-spring-task-autoconfig.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-async-scheduling）](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAsyncSchedulingBranchMatrixLabTest`（见文末“对应 Lab/Test”），先把关键分支跑出来，再回到本文逐条对照根因与修法。


## 我通常怎么排这种问题

异步与调度的坑看起来很散：一会儿像是配置问题，一会儿像是线程池问题，一会儿又像是异常被吞。但真正在项目里排起来，我一般会先把它“笨”一点做确定：

1. 先找一个**可重复的入口**把现象复现出来（优先 tests，别先靠日志猜）
2. 确认几件最关键的事实：有没有代理、线程名有没有变、异常落在哪里
3. 再去看原因：分支矩阵先收敛，断点地图再下探
4. 修完以后，把结论写成断言（否则过两周还会复发）

对应入口：

- Book Matrix（主线最小集合）：`BootAsyncSchedulingBookMatrixLabTest`
- Branch Matrix（关键分支最小集合）：`BootAsyncSchedulingBranchMatrixLabTest`
- 断点地图：[`04-breakpoint-map.md`](guide-breakpoint-map.md)
- 分支矩阵：[`05-branch-decision-matrix.md`](guide-branch-decision-matrix.md)

## `@Async` 不生效

### 坑点 1：写了 `@Async`，但忘了 `@EnableAsync`

会看到线程名始终不变：代码看起来“异步”，实际上仍在调用线程里同步执行。原因也很直白——没启用 async，就不会建立 `@Async` 的拦截基础设施。

可以用 `BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync` 把现象复现出来：同样的调用，线程名始终不变，bean 也不是代理。修复时只需要显式启用 `@EnableAsync`，再用线程名前缀与断言把“确实切线程”写死。

### 坑点 2：self-invocation 绕过代理

这个坑的“迷惑性”在于：外部调用能异步，内部调用却不异步。根因是 self-invocation 绕开了 proxy，拦截器自然不会触发。

这个坑可以用 `BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall` 复现，对照用例则是 `BootAsyncSchedulingLabTest#callingAsyncThroughAnotherBeanGoesThroughProxy`：同样的 `@Async` 方法，是否跨过 bean 边界决定了调用有没有走到代理。修复也应当对齐这一点——让调用跨越 bean 边界（把异步方法抽到另一个 bean，或确保调用走 proxy）。

### 坑点 3：CGLIB 无法拦截 `final` 方法

会看到 bean 明明是代理，但某个 `final @Async` 方法就是不切线程。原因是 CGLIB 需要覆写方法才能织入拦截器，`final` 方法没法覆写。

如果想把这个分支跑成事实，可以直接看 `BootAsyncSchedulingProxyTypeLabTest#cglibCannotInterceptFinalMethods_asyncIsBypassed`：bean 明明是代理，但 `final @Async` 方法无法被覆写，拦截器也就无从织入。修复原则很简单：不要在需要 AOP 能力的方法上使用 `final`（或改为接口 + JDK proxy 的方式）。

### 坑点 4：多线程池共存时，默认选错 executor

以为 `@Async` 会跑在定义的线程池，但线程名前缀对不上。这类问题多数不是“线程池坏了”，而是“默认选择规则把绕过去了”。

可以直接对照下面这些最常见分支（它们覆盖了“单 executor / 多 executor / 显式选择 / Boot 默认”几类场景）：

- 单 executor：`BootAsyncSchedulingExecutorSelectionLabTest#whenSingleTaskExecutorBeanExists_itIsUsedAsDefaultAsyncExecutor`
- 多 executor + `taskExecutor`：`BootAsyncSchedulingExecutorSelectionLabTest#whenMultipleExecutorsExist_namedTaskExecutorWinsAsDefault`
- `@Async("...")` 显式选择：`BootAsyncSchedulingExecutorSelectionLabTest#asyncValueSelectsQualifiedExecutorByName`
- Boot 默认 executor（`spring.task.execution.*`）：`BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskExecutionPropertiesConfigureDefaultExecutor_andAsyncUsesIt`

修复的核心是把“默认选择规则”变成可解释的事实：让“默认 executor”具备稳定名称（`taskExecutor`）与 threadNamePrefix；对关键链路直接用 `@Async("beanName")` 写清边界，避免和默认行为较劲。

## 边界误解（事务 / 上下文）

### 坑点 5：以为“调用方事务会跨 `@Async` 自动传播”

在 `@Transactional` 方法里调用 `@Async`，以为异步逻辑仍处在调用方事务中（能共享一致性/回滚语义）。真实情况通常是：线程一换，事务就断开了；即便 `@Async @Transactional` 同时标注，事务也是在异步线程那边开启。

可以用下面两条对照把边界跑出来：

- 调用方 txActive=true / 异步线程 txActive=false：`BootAsyncSchedulingTransactionBoundaryLabTest#transactionContextDoesNotPropagateAcrossAsyncThreadBoundaryByDefault`
- `@Async @Transactional`：事务在异步线程生效：`BootAsyncSchedulingTransactionBoundaryLabTest#asyncAndTransactionalOnSameMethod_runsTransactionInsideAsyncThread_notCallerThread`

修复思路是承认线程边界：别把“必须与主事务一致”的动作直接放到 `@Async`（它默认就是跨线程边界）；如果确实需要异步一致性，把补偿/重试/outbox 显式设计出来，而不是依赖“同一事务会自动传播”。

### 坑点 6：ThreadLocal / MDC / SecurityContext / RequestContext 在 `@Async` 之后丢失或串号（上下文丢失/泄漏）

“上下文丢失”通常是最先看到的现象：异步线程里拿不到 traceId/userId/tenantId（MDC/ThreadLocal），拿不到当前用户（SecurityContext），拿不到请求属性（RequestContext）。但更危险的是另一类：偶发串号——异步线程读到了上一次任务的残留值。

根因其实就两句话：

- ThreadLocal 本来就只属于线程，切线程当然不带过去
- 线程池会复用线程，不清理就一定有残留风险

下面这些对照用例分别覆盖默认不传播、正确传播与错误写法导致的泄漏：

- ThreadLocal 默认不传播：`BootAsyncSchedulingContextPropagationLabTest#threadLocalContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary`
- ThreadLocal 正确传播 + 清理：`BootAsyncSchedulingContextPropagationLabTest#taskDecoratorCanPropagateThreadLocalContext_andRestoreToAvoidLeaks`
- ThreadLocal 错误写法泄漏：`BootAsyncSchedulingContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousThreadLocalValueAcrossTasks`
- SecurityContext 默认不传播：`BootAsyncSchedulingSecurityContextPropagationLabTest#securityContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary`
- SecurityContext delegating 修复 + 不泄漏：`BootAsyncSchedulingSecurityContextPropagationLabTest#delegatingSecurityContextExecutorCanPropagate_andCleansUpToAvoidThreadReuseLeaks`
- RequestContext 默认不传播：`BootAsyncSchedulingRequestContextPropagationLabTest#requestContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary`
- RequestContext 错误写法泄漏：`BootAsyncSchedulingRequestContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousRequestAttributesAcrossTasks`

修复时可以把上下文传播当作一段严谨的“捕获 → 设置 → finally 清理/恢复”流程来实现：对 `ThreadPoolTaskExecutor` 用 `TaskDecorator`；关键细节是**即使 captured 为 null，也要在工作线程里清理**（否则线程复用会残留上一任务的上下文）。对 SecurityContext 更推荐直接使用 Spring Security 的 Delegating* executor，因为它已经把这套流程做成了可靠的基础设施。

## 异常看不到

### 坑点 7：void 异步异常“只有日志”，调用方完全无感

void 异步抛异常时，调用方没有任何感知（不会在调用点拿到异常）。异常最终会落到 `AsyncUncaughtExceptionHandler` 上；如果 handler 没处理好，就只剩“某个线程里偶尔有一条 stacktrace”。

最小对照入口如下：

- 基础语义：`BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler`
- handler 能拿到 method/args：`BootAsyncSchedulingUncaughtExceptionHandlerLabTest#voidAsyncExceptions_areDeliveredToUncaughtExceptionHandlerWithMethodAndArgs`

修复时应当先做“失败可见性”的选择：必须反馈失败的异步逻辑优先返回 `CompletableFuture`；`void` 也不是不能用，但要把 handler 当成正式告警入口，让 method + args + exception 都能被观测到。

## 调度测试 flaky

### 坑点 8：用 `Thread.sleep` 写调度测试，导致 flaky

这是最常见的一类 flaky：本地能跑，CI 偶发失败；或者为了“等它触发”把 sleep 写得很长，导致测试越来越慢。

最小对照入口如下：

- 没开 scheduling 不触发：`BootAsyncSchedulingSchedulingLabTest#schedulingRequiresEnableScheduling`
- 开启后至少触发一次：`BootAsyncSchedulingSchedulingLabTest#schedulingTriggersTaskWhenEnableSchedulingPresent`

修复的目标是让断言稳定：用 `CountDownLatch` 固定“至少触发一次”，并且永远设置超时上限；避免长时间 `Thread.sleep` 把测试变慢又变脆。

### 坑点 9：为了验证 scheduling 行为，用“等它触发”代替“断言注册结果”

想验证 fixedRate/fixedDelay/cron 的差异，但测试只能靠时间窗口猜。很多时候真正关心的是“注册语义”，而不是“触发次数”。

这个问题可以用 `BootAsyncSchedulingSchedulingRegistrationLabTest#scheduledTasksAreRegisteredAsDifferentTaskTypes` 复现。修复方向也更像“改测试策略”而不是“改业务逻辑”：优先断言 `ScheduledTaskHolder` 的注册结果，触发类测试只保留最小必要集合。

### 坑点 10：定时任务抛异常后语义不清，误以为“会/不会继续跑”

这个问题很容易在“线上偶发”时变成吵架：有人说“异常会让任务停掉”，有人说“不会”。实际上语义取决于异常是否被包装并交给 `ErrorHandler` 处理。

这个分支可以用 `BootAsyncSchedulingSchedulingExceptionSemanticsLabTest#scheduledExceptionsAreHandledByErrorHandler_andTaskContinues` 固化下来。修复时建议明确提供 `TaskScheduler` 并设置 `ErrorHandler`（至少能观测异常），再用测试把语义钉住。

### 坑点 11：scheduler 线程被耗时逻辑拖死，导致触发延迟/堆积

当定时任务变慢后，会看到“所有任务都延迟”，甚至开始堆积。根因是 scheduler 线程池的职责是触发，如果它还承担耗时逻辑，就会把整个调度系统拖垮。

可以用 `BootAsyncSchedulingScheduledAsyncCombinationLabTest#scheduledRunsOnSchedulerThread_butScheduledPlusAsyncRunsOnExecutorThread` 把这个分支跑成对照。修复方向是把耗时逻辑卸载到 async executor（`@Scheduled + @Async`，或者触发后手动提交到 executor），让 scheduler 线程只负责触发与调度。

## 对应 Lab（可运行）

- Book Matrix（主线）：`BootAsyncSchedulingBookMatrixLabTest`
- Branch Matrix（关键分支）：`BootAsyncSchedulingBranchMatrixLabTest`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

上一章：[part-01-async-scheduling/08-boot-spring-task-autoconfig.md](async-scheduling-boot-spring-task-autoconfig.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
