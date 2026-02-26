# 99 自检：Spring Boot Async & Scheduling
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（当成习题册）"

    这页定位为练习册：把 Async/Scheduling 的关键分支按问题列出来。每题都对应一个可复现入口（tests/断点），用于复盘与回归。

    - 主线入口：`BootAsyncSchedulingBookMatrixLabTest`
    - 分支入口：`BootAsyncSchedulingBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（Async & Scheduling）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 99 - Self Check（springboot-async-scheduling）**
- 建议入口：优先运行 `BootAsyncSchedulingBookMatrixLabTest`（主线）或 `BootAsyncSchedulingBranchMatrixLabTest`（分支）（见文末“对应 Lab/Test”），再按题目回到对应复现入口。



## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/04-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/05-branch-decision-matrix.md)

## 自测题

把它当作“复盘题”更合适：不是让你背诵，而是让你能在真实项目里迅速判断分支、找到复现入口、写出可回归的结论。

1. 没有 `@EnableAsync` 时，`@Async` 会发生什么？你用哪个证据入口证明它“就像不存在”？（提示：看 proxy 与线程名）
2. 当系统里只有 1 个 `TaskExecutor` 时，默认 executor 如何选择？你用哪个 Lab 把它写成断言？
3. 当系统里有多个 executor 时，默认为什么会选 `taskExecutor`？你如何用线程名前缀证明“选中了哪个”？
4. 如何用 `@Async("beanName")` 显式选择线程池？它与 `AsyncConfigurer` 的关系是什么？
5. `@Async void` 抛异常时，为什么调用方看不到？异常最终去哪了？你如何证明 handler 能拿到 method + args？
6. self-invocation 为什么会绕过 `@Async`？你如何用“跨 bean 边界”修复并固化证据？
7. JDK proxy 与 CGLIB proxy 的差异是什么？为什么 CGLIB 拦截不了 `final @Async` 方法？
8. 没有 `@EnableScheduling` 时 `@Scheduled` 会怎样？开启后如何写出不 flaky 的“至少触发一次”断言？
9. fixedRate/fixedDelay/cron 如何在 Spring 内部注册为不同 task 类型？为什么注册断言比“等它触发”更确定？
10. `@Scheduled` 抛异常后任务会不会继续跑？异常由谁处理？你如何用 ErrorHandler 把语义固化？
11. `@Scheduled + @Async` 同时使用时，触发线程与执行线程各是谁？你如何用断言把两者区分出来？
12. `@Async` 切线程后，为什么 ThreadLocal/MDC 默认不传播？如何用 TaskDecorator 正确传播并避免泄漏？你如何证明“错误的 decorator 会串号”？
13. 调用方处在 `@Transactional` 中时调用 `@Async`，异步线程是否处于同一个事务？你如何用断言证明“不会自动传播”？
14. 当方法同时标注 `@Async` 与 `@Transactional` 时，事务发生在调用方线程还是异步线程？你如何证明这一点？
15. SecurityContext / RequestContext 为什么默认不跨线程？如何分别用 Delegating* 与 TaskDecorator 修复并证明“不会泄漏”？
16. `spring.task.execution.*` / `spring.task.scheduling.*` 的属性如何映射到默认 executor/scheduler？你如何用断言证明 `@Async/@Scheduled` 真正在用它们？

## 证据入口（推荐）

- Q1：`BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync`
- Q2：`BootAsyncSchedulingExecutorSelectionLabTest#whenSingleTaskExecutorBeanExists_itIsUsedAsDefaultAsyncExecutor`
- Q3：`BootAsyncSchedulingExecutorSelectionLabTest#whenMultipleExecutorsExist_namedTaskExecutorWinsAsDefault`
- Q4：`BootAsyncSchedulingExecutorSelectionLabTest#asyncValueSelectsQualifiedExecutorByName` / `BootAsyncSchedulingExecutorSelectionLabTest#asyncConfigurerOverridesDefaultExecutorSelection_butQualifiedExecutorStillWorks`
- Q5：`BootAsyncSchedulingLabTest#asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler` / `BootAsyncSchedulingUncaughtExceptionHandlerLabTest#voidAsyncExceptions_areDeliveredToUncaughtExceptionHandlerWithMethodAndArgs`
- Q6：`BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall` / `BootAsyncSchedulingLabTest#callingAsyncThroughAnotherBeanGoesThroughProxy`
- Q7：`BootAsyncSchedulingProxyTypeLabTest#jdkProxyIsUsedWhenProxyTargetClassFalseAndInterfacePresent` / `BootAsyncSchedulingProxyTypeLabTest#cglibCannotInterceptFinalMethods_asyncIsBypassed`
- Q8：`BootAsyncSchedulingSchedulingLabTest#schedulingRequiresEnableScheduling` / `BootAsyncSchedulingSchedulingLabTest#schedulingTriggersTaskWhenEnableSchedulingPresent`
- Q9：`BootAsyncSchedulingSchedulingRegistrationLabTest#scheduledTasksAreRegisteredAsDifferentTaskTypes`
- Q10：`BootAsyncSchedulingSchedulingExceptionSemanticsLabTest#scheduledExceptionsAreHandledByErrorHandler_andTaskContinues`
- Q11：`BootAsyncSchedulingScheduledAsyncCombinationLabTest#scheduledRunsOnSchedulerThread_butScheduledPlusAsyncRunsOnExecutorThread`
- Q12：`BootAsyncSchedulingContextPropagationLabTest#threadLocalContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary` / `BootAsyncSchedulingContextPropagationLabTest#taskDecoratorCanPropagateThreadLocalContext_andRestoreToAvoidLeaks` / `BootAsyncSchedulingContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousThreadLocalValueAcrossTasks`
- Q13：`BootAsyncSchedulingTransactionBoundaryLabTest#transactionContextDoesNotPropagateAcrossAsyncThreadBoundaryByDefault`
- Q14：`BootAsyncSchedulingTransactionBoundaryLabTest#asyncAndTransactionalOnSameMethod_runsTransactionInsideAsyncThread_notCallerThread`
- Q15：`BootAsyncSchedulingSecurityContextPropagationLabTest#securityContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary` / `BootAsyncSchedulingSecurityContextPropagationLabTest#delegatingSecurityContextExecutorCanPropagate_andCleansUpToAvoidThreadReuseLeaks` / `BootAsyncSchedulingRequestContextPropagationLabTest#requestContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary` / `BootAsyncSchedulingRequestContextPropagationLabTest#taskDecoratorCanPropagateRequestContext_andRestoreToAvoidLeaks`
- Q16：`BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskExecutionPropertiesConfigureDefaultExecutor_andAsyncUsesIt` / `BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskSchedulingPropertiesConfigureTaskScheduler_andScheduledUsesIt`

## 对应 Exercise（可运行）

- `BootAsyncSchedulingExerciseTest`

## 如果你卡住了

最常把人绊住的不是“没记住 API”，而是 AOP 的边界（self-invocation）。如果你发现某个结论怎么都对不上，先不必急于怀疑自己：跑一次 `BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall`，把“有没有走代理”这件事确认掉，很多问题会立刻变简单。

## 小结与下一章

- 小结：本章围绕关键主线与边界条件展开，建议结合可运行入口进行验证。
- 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingBookMatrixLabTest` / `BootAsyncSchedulingBranchMatrixLabTest`
- Exercise：`BootAsyncSchedulingExerciseTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
