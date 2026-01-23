# 第 118 章：01：Async/Scheduling 调用链（@Async / @Scheduled 的生效时机）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Async/Scheduling 调用链（@Async / @Scheduled 的生效时机）
    - 怎么使用：先跑 `BootAsyncSchedulingLabTest`，把线程切换/调度触发固化为断言，再按本文从注解 → PostProcessor → 代理/注册表串起调用链。
    - 原理：`@Async` 是代理拦截（调用时切线程）；`@Scheduled` 是启动期注册任务（定时触发）。
    - 源码入口：`AsyncAnnotationBeanPostProcessor` / `AsyncExecutionInterceptor` / `ScheduledAnnotationBeanPostProcessor`
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 118 章：00. 深挖导读](118-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 118 章：02：断点地图](118-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 最短调用链

### 1) `@Async`（调用期）

1. 容器启动期：`AsyncAnnotationBeanPostProcessor` 扫描 `@Async` 并创建代理
2. 运行期调用进入代理
3. `AsyncExecutionInterceptor` 把调用提交到 `TaskExecutor`
4. 线程池线程执行目标方法

### 2) `@Scheduled`（启动期注册 + 运行期触发）

1. 启动期：`ScheduledAnnotationBeanPostProcessor` 收集 `@Scheduled` 方法
2. 把任务注册到 `TaskScheduler`
3. 运行期按触发器（fixedDelay/cron 等）回调执行

证据链入口：

- `BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`
- Lab：`BootAsyncSchedulingSchedulingLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](118-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](118-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
