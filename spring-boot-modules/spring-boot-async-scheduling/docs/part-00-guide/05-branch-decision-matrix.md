# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（If/Then 表）"

    这张表是给“排障时不想靠猜”的：把最常见的分支写成 If/Then，并给出最小复现入口与观察点。

    用法也很简单：先找到当前的触发条件（Trigger），跑一次 Repro，把 Watchpoints 看一眼，结论就基本收敛了。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Async & Scheduling Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. `@Async` 心智模型：代理与线程切换](../part-01-async-scheduling/01-async-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAsyncSchedulingBranchMatrixLabTest`（见文末“对应 Lab/Test”），从当前的触发条件入手，跑一次 Repro，再对照本表的 Watchpoints 收敛结论。


## 关键分支矩阵（最小集合）

这张矩阵刻意只收“最小集合”：不是把所有可能性都列出来，而是把最常见、最容易误判、且能被本模块 tests 稳定复现的分支先钉住。

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 未启用 async | 没有 `@EnableAsync` | 不会生成代理，不会切线程 | `BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync` | `AopUtils.isAopProxy==false` / 线程名不变 |
| 启用 async | 有 `@EnableAsync` | 走代理并切到线程池 | `BootAsyncSchedulingLabTest#asyncRunsOnExecutorThreadWhenEnableAsyncPresent` | 线程名前缀 `async-` |
| 自调用坑 | bean 内部自调用 `@Async` 方法 | 绕过代理，不切线程 | `BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall` | 调用栈不进入 `CglibAopProxy` |
| Future 异常传播 | `CompletableFuture` 返回 | 异常包进 future | `BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture` | `future.get()` rootCause |
| void 异常去向 | `@Async void` 抛异常 | 调用方拿不到异常，进入 UncaughtExceptionHandler | `BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler` | handler 被调用 |
| void 异常细节 | void async + 参数 | handler 能拿到 method + args | `BootAsyncSchedulingUncaughtExceptionHandlerLabTest#voidAsyncExceptions_areDeliveredToUncaughtExceptionHandlerWithMethodAndArgs` | methodName/args/exception |
| executor（单一） | 仅 1 个 `TaskExecutor` | 成为默认 executor | `BootAsyncSchedulingExecutorSelectionLabTest#whenSingleTaskExecutorBeanExists_itIsUsedAsDefaultAsyncExecutor` | 线程名前缀 `only-` |
| executor（多选） | 多个 executor | 默认选 `taskExecutor` | `BootAsyncSchedulingExecutorSelectionLabTest#whenMultipleExecutorsExist_namedTaskExecutorWinsAsDefault` | 线程名前缀 `default-` |
| executor（显式） | `@Async("specialExecutor")` | 按名称选择 executor | `BootAsyncSchedulingExecutorSelectionLabTest#asyncValueSelectsQualifiedExecutorByName` | 线程名前缀 `special-` |
| executor（覆盖） | 实现 `AsyncConfigurer` | 覆盖默认选择，但显式仍生效 | `BootAsyncSchedulingExecutorSelectionLabTest#asyncConfigurerOverridesDefaultExecutorSelection_butQualifiedExecutorStillWorks` | `configurer-` vs `special-` |
| 上下文（丢失） | `@Async` 切线程 | ThreadLocal/MDC 默认不传播 | `BootAsyncSchedulingContextPropagationLabTest#threadLocalContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary` | context==null（异步线程） |
| 上下文（修复） | 配置 `TaskDecorator` | 上下文可传播且 finally 恢复/清理 | `BootAsyncSchedulingContextPropagationLabTest#taskDecoratorCanPropagateThreadLocalContext_andRestoreToAvoidLeaks` | 第二次任务 context==null（无残留） |
| 上下文（泄漏） | buggy decorator（跳过 null/不恢复） | 线程复用导致串号 | `BootAsyncSchedulingContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousThreadLocalValueAcrossTasks` | 第二次仍读到上一次 context |
| 事务（不传播） | 调用方在事务中调用 `@Async` | 异步线程默认不在调用方事务里 | `BootAsyncSchedulingTransactionBoundaryLabTest#transactionContextDoesNotPropagateAcrossAsyncThreadBoundaryByDefault` | callerTxActive=true / asyncTxActive=false |
| 事务（异步事务） | `@Async @Transactional` 同时标注 | 事务发生在异步线程（不是调用方线程） | `BootAsyncSchedulingTransactionBoundaryLabTest#asyncAndTransactionalOnSameMethod_runsTransactionInsideAsyncThread_notCallerThread` | asyncTxActive=true（async 线程） |
| SecurityContext（丢失） | `@Async` 读当前用户 | 异步线程读到 null | `BootAsyncSchedulingSecurityContextPropagationLabTest#securityContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary` | authentication==null |
| SecurityContext（修复） | Delegating* executor | 可传播且清理避免泄漏 | `BootAsyncSchedulingSecurityContextPropagationLabTest#delegatingSecurityContextExecutorCanPropagate_andCleansUpToAvoidThreadReuseLeaks` | 第一次=alice / 第二次=null |
| RequestContext（丢失） | `@Async` 读请求属性 | 异步线程读到 null | `BootAsyncSchedulingRequestContextPropagationLabTest#requestContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary` | requestAttributes==null |
| RequestContext（泄漏） | buggy decorator（跳过 null/不清理） | 线程复用导致串号 | `BootAsyncSchedulingRequestContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousRequestAttributesAcrossTasks` | 第二次仍读到上一次 rid |
| Boot 自动装配（execution） | 配置 `spring.task.execution.*` | 默认 executor 线程名可断言 | `BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskExecutionPropertiesConfigureDefaultExecutor_andAsyncUsesIt` | 线程名前缀 `boot-async-` |
| Boot 自动装配（scheduling） | 配置 `spring.task.scheduling.*` | 默认 scheduler 线程名可断言 | `BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskSchedulingPropertiesConfigureTaskScheduler_andScheduledUsesIt` | 线程名前缀 `boot-sched-` |
| Proxy 类型 | `proxyTargetClass=false` 且有接口 | 使用 JDK proxy | `BootAsyncSchedulingProxyTypeLabTest#jdkProxyIsUsedWhenProxyTargetClassFalseAndInterfacePresent` | `AopUtils.isJdkDynamicProxy==true` |
| Proxy 类型 | `proxyTargetClass=true` | 使用 CGLIB proxy | `BootAsyncSchedulingProxyTypeLabTest#cglibProxyIsUsedWhenProxyTargetClassTrue` | `AopUtils.isCglibProxy==true` |
| final 方法 | CGLIB + `final @Async` | 不能被拦截，异步失效 | `BootAsyncSchedulingProxyTypeLabTest#cglibCannotInterceptFinalMethods_asyncIsBypassed` | 线程名不变 |
| Scheduling 开关 | 没 `@EnableScheduling` | 不触发定时任务 | `BootAsyncSchedulingSchedulingLabTest#schedulingRequiresEnableScheduling` | 探针未被触发 |
| Scheduling 触发 | 有 `@EnableScheduling` | 至少触发一次 | `BootAsyncSchedulingSchedulingLabTest#schedulingTriggersTaskWhenEnableSchedulingPresent` | latch/计数 |
| Scheduling 注册 | fixedRate/fixedDelay/cron | 注册为不同 task 类型 | `BootAsyncSchedulingSchedulingRegistrationLabTest#scheduledTasksAreRegisteredAsDifferentTaskTypes` | `ScheduledTaskHolder` |
| Scheduling 异常语义 | `@Scheduled` 抛异常 | 异常进入 ErrorHandler，任务仍继续 | `BootAsyncSchedulingSchedulingExceptionSemanticsLabTest#scheduledExceptionsAreHandledByErrorHandler_andTaskContinues` | ErrorHandler 收集异常 |
| 组合注解 | `@Scheduled + @Async` | 触发线程与执行线程分离 | `BootAsyncSchedulingScheduledAsyncCombinationLabTest#scheduledRunsOnSchedulerThread_butScheduledPlusAsyncRunsOnExecutorThread` | `sched-` vs `async-` |
| 线程池饱和 | pool=1 queue=0 | 第二个任务被拒绝 | `BootAsyncSchedulingExecutorSaturationLabTest#executorSaturationRejectsSecondTaskDeterministically` | `TaskRejectedException` 或未开始 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

下一章见：[第 119 章：01：@Async 的心智模型：代理、线程池与返回值](../part-01-async-scheduling/01-async-proxy-mental-model.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootAsyncSchedulingBranchMatrixLabTest`
- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/01-async-proxy-mental-model.md](../part-01-async-scheduling/01-async-proxy-mental-model.md)

<!-- BOOKIFY:END -->
