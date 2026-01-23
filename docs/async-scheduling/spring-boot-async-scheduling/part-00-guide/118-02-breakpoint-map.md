# 第 118 章：02：断点地图（Async & Scheduling Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Async & Scheduling Debugger Pack）
    - 怎么使用：先跑 `BootAsyncSchedulingBranchMatrixLabTest` 固化“是否走代理/线程切换/异常传播/定时触发”的断言，再用断点回答“为什么这次没异步/为什么没调度”。
    - 原理：`@Async/@Scheduled` 依赖代理与后置处理器；分支多发生在“有没有启用/是不是自调用/用的是哪个 Executor/TaskScheduler”。
    - 源码入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor` / `org.springframework.aop.interceptor.AsyncExecutionInterceptor` / `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor`
    - 推荐 Lab：`BootAsyncSchedulingBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 118 章：00 - Deep Dive Guide（springboot-async-scheduling）](118-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 118 章：04：关键分支矩阵（Branch Decision Matrix）](118-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章目标：把异步/调度最关键的分支（Enable、代理、自调用、异常）收敛为断点清单与 Watchpoints。
- 常见误区：看到 `@Async` 就以为会线程切换；看到 `@Scheduled` 就以为会触发（实际需要 enable + 代理链路）。

## 运行入口（建议先跑）

- Book Matrix：`BootAsyncSchedulingBookMatrixLabTest`
- Branch Matrix：`BootAsyncSchedulingBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`

## 入口断点（先证明“是不是代理”）

- `org.springframework.aop.support.AopUtils#isAopProxy`（在测试断言处观察）
- `org.springframework.aop.framework.CglibAopProxy#intercept`（确认调用是否经过代理）

## @Async 断点（线程切换的决定点）

- `org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`
- `org.springframework.aop.interceptor.AsyncExecutionAspectSupport#doSubmit`

## @Scheduled 断点（是否注册/是否触发）

- `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#processScheduled`
- `org.springframework.scheduling.support.ScheduledMethodRunnable#run`

## Watchpoints（建议）

- 当前线程名：`Thread.currentThread().getName()`
- 是否代理：`AopUtils.isAopProxy(bean)`
- `@EnableAsync/@EnableScheduling` 是否存在（从配置类/BeanDefinition 反推）
- 自调用场景：同一个 bean 内部调用是否绕过 proxy（观察调用栈是否进入 CglibAopProxy）

## 排障入口（Playbook）

- 常见坑：[`../appendix/124-90-common-pitfalls.md`](../appendix/124-90-common-pitfalls.md)
- 自检：[`../appendix/125-99-self-check.md`](../appendix/125-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootAsyncSchedulingBranchMatrixLabTest`
- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](118-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](118-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

