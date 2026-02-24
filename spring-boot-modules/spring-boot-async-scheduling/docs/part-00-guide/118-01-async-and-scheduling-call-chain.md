# 第 118 章：01：Async/Scheduling 调用链（@Async / @Scheduled 的生效时机）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（最短调用链）"

    这页只做一件事：把 `@Async` / `@Scheduled` 的“最短调用链”写出来，方便你在看 tests 或打断点时不迷路。

    你可以把它当成一张速记卡：不需要背，排障时翻一眼就够。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 118 章：00. 深挖导读](118-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 118 章：02：断点地图](118-02-breakpoint-map.md)
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
