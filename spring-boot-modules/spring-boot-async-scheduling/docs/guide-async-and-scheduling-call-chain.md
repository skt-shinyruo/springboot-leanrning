# 03. Async/Scheduling 调用链（@Async / @Scheduled 的生效时机）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（最短调用链）"

    本页的职责是：把 `@Async` / `@Scheduled` 的“最短调用链”写出来，便于在看 tests 或打断点时不迷路。

    可以把它当成一张速记卡：不需要背，排障时翻一眼就够。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Async & Scheduling](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Async & Scheduling）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`（见文末“对应实验/测试”），对照断言与调用栈，定位“代理建立/任务注册”发生点。


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

## 小结与下一章

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootAsyncSchedulingLabTest`
- Lab：`BootAsyncSchedulingSchedulingLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
