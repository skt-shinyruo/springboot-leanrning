# 第 118 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把异步/调度最常见的“为什么没生效”写成矩阵表，并为每个分支给出复现入口与观察点。
    - 原理：分支多数发生在：是否启用、是否走代理、异常如何传播、线程池/调度器选择。
    - 源码入口：`AsyncExecutionInterceptor` / `ScheduledAnnotationBeanPostProcessor`
    - 推荐 Lab：`BootAsyncSchedulingBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 118 章：02：断点地图（Async & Scheduling Debugger Pack）](118-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 119 章：01：@Async 的心智模型：代理、线程池与返回值](../part-01-async-scheduling/119-01-async-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 未启用 async | 没有 `@EnableAsync` | 不会生成代理，不会切线程 | `BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync` | `AopUtils.isAopProxy==false` / 线程名不变 |
| 启用 async | 有 `@EnableAsync` | 走代理并切到线程池 | `BootAsyncSchedulingLabTest#asyncRunsOnExecutorThreadWhenEnableAsyncPresent` | 线程名前缀 `async-` |
| 自调用坑 | bean 内部自调用 `@Async` 方法 | 绕过代理，不切线程 | `BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall` | 调用栈不进入 `CglibAopProxy` |
| Future 异常传播 | `CompletableFuture` 返回 | 异常包进 future | `BootAsyncSchedulingLabTest#asyncExceptionsPropagateThroughFuture` | `future.get()` rootCause |
| Scheduling 开关 | 没 `@EnableScheduling` | 不触发定时任务 | `BootAsyncSchedulingLabTest#schedulingRequiresEnableScheduling` | 探针未被触发 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/124-90-common-pitfalls.md`](../appendix/124-90-common-pitfalls.md)
- 自检：[`../appendix/125-99-self-check.md`](../appendix/125-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootAsyncSchedulingBranchMatrixLabTest`
- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](118-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/01-async-proxy-mental-model.md](../part-01-async-scheduling/119-01-async-proxy-mental-model.md)

<!-- BOOKIFY:END -->

