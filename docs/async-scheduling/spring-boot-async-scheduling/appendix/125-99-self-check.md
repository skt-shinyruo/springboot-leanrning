# 第 125 章：99 - Self Check（springboot-async-scheduling）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-async-scheduling）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。
    - 原理：方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。
    - 源码入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor` / `org.springframework.aop.interceptor.AsyncExecutionInterceptor` / `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor` / `org.springframework.core.task.TaskExecutor`
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 124 章：90：常见坑清单（Async & Scheduling）](124-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 126 章：Events 主线](../../../book/126-events-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/118-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/118-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-async-scheduling）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

## 机制主线

这一章用“最小实验 + 断言”复盘两条主线：

1. `@Async`：代理是否生效、线程是否切换、异常是否可观察
2. `@Scheduled`：开关是否打开、任务是否注册、测试是否可确定性验证

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`
- 建议命令：`mvn -pl :spring-boot-async-scheduling test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 自测题
1. `@Async` 默认使用哪个 Executor？如何自定义？如何验证确实生效？
2. 为什么 `@Async` 在自调用场景下不生效？如何规避？
3. `@Scheduled` 的固定频率与固定延迟有什么差异？

## 对应 Exercise（可运行）

- `BootAsyncSchedulingExerciseTest`

## 常见坑与边界

### 坑点 1：self-invocation 绕过代理，导致 `@Async`/`@Transactional` 类注解“看起来写了但不生效”

- Symptom：你在同一个 bean 内部调用 `@Async` 方法，发现没有切线程
- Root Cause：自调用不经过代理，拦截器不会触发（这是 AOP 的共同边界）
- Verification：`BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall`
- Fix：让调用跨越 bean 边界（或通过代理获取自身），并用线程名断言把行为锁住

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`
- Exercise：`BootAsyncSchedulingExerciseTest`

上一章：[appendix/90-common-pitfalls.md](124-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
