# 04. 断点地图（Async & Scheduling Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（断点地图）"

    我把异步/调度最常用的断点按“从外到内”的顺序整理在这里：先确认有没有走代理、再看提交到了哪个 executor/scheduler、最后才下探异常与上下文传播。

    本页定位更接近备忘录：可以不按顺序读，但真排障时往往能省不少时间。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-async-scheduling）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAsyncSchedulingBranchMatrixLabTest`（见文末“对应 Lab/Test”），在断言处下断点，再按本页清单逐步把分支与观察点对齐。


## 这页怎么用

异步/调度的排障，最怕的就是一上来就钻源码细节：可能在内部类里绕了半小时，最后才发现根因是“根本没启用”或“调用绕开了代理”。

所以这页的顺序是刻意的：先用断点回答“有没有发生”，再用断点回答“为什么是这样”。

## 从哪个入口开始（让断点命中更稳定）

- Book Matrix：`BootAsyncSchedulingBookMatrixLabTest`
- Branch Matrix：`BootAsyncSchedulingBranchMatrixLabTest`

## 入口断点（先证明“是不是代理”）

- `org.springframework.aop.support.AopUtils#isAopProxy`（在测试断言处观察）
- `org.springframework.aop.framework.CglibAopProxy#intercept`（确认调用是否经过代理）

## @Async 断点（线程切换的决定点）

- `org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`
- `org.springframework.aop.interceptor.AsyncExecutionAspectSupport#doSubmit`
- `org.springframework.aop.interceptor.AsyncExecutionAspectSupport#determineAsyncExecutor`（默认 executor/按名称选择的分支）

## TaskDecorator 断点（ThreadLocal/MDC 上下文传播与泄漏）

当遇到“traceId/MDC 在异步线程里丢失”或“串号（上一次任务残留）”时，优先从装饰器与线程池提交点下断点：

- `TaskDecorator#decorate`（或 lambda 实现处）：观察 captured/previous/finally 清理是否正确执行
- `org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor#execute`：确认任务是否被 decorate 后再提交（同时也能确认用的是不是 `ThreadPoolTaskExecutor`）
- `java.util.concurrent.ThreadPoolExecutor#execute`：观察线程池是否复用线程、任务是否堆积/排队

## `@Transactional` 断点（`@Async` × 事务边界）

当怀疑“我以为在事务里执行，但实际不在”时，建议把观察点拆成两条线程：

- 调用方线程：事务是否 active？
- 异步线程：事务是否 active？

推荐断点：

- `org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`：切线程与提交点
- `org.springframework.transaction.interceptor.TransactionInterceptor#invoke`：事务拦截入口（判断事务是否真正开启）
- `org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction`：事务创建/加入点（更底层）

稳定 Watchpoints：

- `TransactionSynchronizationManager.isActualTransactionActive()`：事务是否 active
- `TransactionSynchronizationManager.isSynchronizationActive()`：同步是否 active（是否绑定了同步）

## SecurityContext 断点（Spring Security delegating wrapper）

当遇到“异步线程拿不到当前用户”或“偶发串号”的问题时，优先确认：

- 是否使用了 delegating wrapper（例如 `DelegatingSecurityContextAsyncTaskExecutor`）
- wrapper 是否在 finally 做了清理

推荐断点：

- `org.springframework.security.concurrent.DelegatingSecurityContextRunnable#run`
- `org.springframework.security.concurrent.DelegatingSecurityContextCallable#call`
- `org.springframework.security.core.context.SecurityContextHolder#getContext`

## Spring Boot `spring.task.*` 自动装配断点（默认 executor/scheduler 从哪来）

当遇到“我以为用的是 Boot 默认线程池，但行为不对”时，建议先回答两件事：

1. Boot 是否真的装配了默认 `TaskExecutor/TaskScheduler`？
2. `@Async/@Scheduled` 最终选择的是哪个 bean？

推荐断点：

- Boot 自动装配（属性 → bean）：
  - `org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration`
  - `org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration`
- 选择规则（bean → 实际使用）：
  - `org.springframework.aop.interceptor.AsyncExecutionAspectSupport#determineAsyncExecutor`
  - `org.springframework.scheduling.config.TaskSchedulerRouter#determineDefaultScheduler`（或等价选择分支）

## @Scheduled 断点（是否注册/是否触发）

- `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#processScheduled`
- `org.springframework.scheduling.support.ScheduledMethodRunnable#run`

## @Scheduled 异常与 ErrorHandler（异常去哪了、会不会“炸没任务”）

- `org.springframework.scheduling.support.DelegatingErrorHandlingRunnable#run`（异常包装点）
- `org.springframework.util.ErrorHandler#handleError`（异常最终处理点：可用自定义 handler 观测）

## Watchpoints（建议）

- 当前线程名：`Thread.currentThread().getName()`
- 是否代理：`AopUtils.isAopProxy(bean)`
- `@EnableAsync/@EnableScheduling` 是否存在（从配置类/BeanDefinition 反推）
- 自调用场景：同一个 bean 内部调用是否绕过 proxy（观察调用栈是否进入 CglibAopProxy）
- ThreadLocal/MDC 上下文：调用方线程与工作线程里读取到的值是否一致；是否存在“第二次任务仍读到上一次值”的残留
- 事务上下文：调用方线程与工作线程里 `TransactionSynchronizationManager` 状态是否一致
- SecurityContext/RequestContext：异步线程读取到的 authentication/requestAttributes 是否符合预期；是否存在残留

## 排障入口（Playbook）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

下一章见：[第 118 章：04：关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootAsyncSchedulingBranchMatrixLabTest`
- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
