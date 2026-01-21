# 第 124 章：90：常见坑清单（Async & Scheduling）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：90：常见坑清单（Async & Scheduling）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `@Async` 把执行切到线程池（TaskExecutor），用 `@Scheduled` 让任务按 cron/fixedDelay/fixedRate 触发；明确线程池配置与异常可见性。
    - 原理：方法调用 → 代理拦截（Async/Scheduling）→ 提交到 Executor/Scheduler → 线程池执行 → 返回值/异常传播语义决定可观察性与稳定性。
    - 源码入口：`org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor` / `org.springframework.aop.interceptor.AsyncExecutionInterceptor` / `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor` / `org.springframework.core.task.TaskExecutor`
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 123 章：05：`@Scheduled` 基础与可测试性](../part-01-async-scheduling/123-05-scheduling-basics.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 125 章：99 - Self Check（springboot-async-scheduling）](125-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[118-02-breakpoint-map.md](../part-00-guide/118-02-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[118-04-branch-decision-matrix.md](../part-00-guide/118-04-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[125-99-self-check.md](125-99-self-check.md)

- 本章主题：**90：常见坑清单（Async & Scheduling）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

## 机制主线

- （本章主线内容暂以契约骨架兜底；建议结合源码与测试用例补齐主线解释。）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`
- 建议命令：`mvn -pl :springboot-async-scheduling test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界


## `@Async` 不生效

- 忘了 `@EnableAsync`
- self-invocation（同类 this 调用绕过代理）
- executor 没配置导致线程/并发行为不符合预期

## 异常看不到

- void 的异常不会传回调用方：需要 AsyncUncaughtExceptionHandler

## 调度测试 flaky

- 用 `Thread.sleep` 过长/过短都不稳定
- 建议 latch + await + 上限超时

## 对应 Lab（可运行）

- `BootAsyncSchedulingLabTest`
- `BootAsyncSchedulingSchedulingLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

上一章：[part-01-async-scheduling/05-scheduling-basics.md](../part-01-async-scheduling/123-05-scheduling-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](125-99-self-check.md)

<!-- BOOKIFY:END -->
