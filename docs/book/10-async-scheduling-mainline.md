# （Redirect）Async/Scheduling 主线（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）Async/Scheduling 主线（旧入口）
    - 怎么使用：用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。
    - 原理：方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。
    - 源码入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor` / `org.springframework.aop.interceptor.AsyncExecutionInterceptor` / `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor` / `org.springframework.core.task.TaskExecutor`
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](116-async-scheduling-mainline.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
