# 深挖导读：Spring Boot Async & Scheduling
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（深挖导读）"

    这章讲的不是“某个注解怎么写”，而是：这份模块为什么这么写、可以怎么用它、以及遇到分支时该去哪里验证。

    - 想顺着主线阅读：从 `@Async` 心智模型开始，按主线向下走
    - 想快速确认某个结论：从 `BootAsyncSchedulingBookMatrixLabTest` 入手即可
    - 想排障（没生效/线程不对/异常不见了）：用 `BootAsyncSchedulingBranchMatrixLabTest` + 断点地图
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Boot Async & Scheduling](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. `@Async` 心智模型：代理与线程切换](async-scheduling-async-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootAsyncSchedulingBookMatrixLabTest`（主线）或 `BootAsyncSchedulingBranchMatrixLabTest`（排障）（见文末“对应实验/测试”），先获得可复现现象，再按正文把边界收敛成结论。


## 这份模块的写法：把“边界”写成可回归的事实

在真实项目里，异步与调度的问题通常不是“不清楚注解的写法”，而是三件更具体的事：

1. **有没有走到代理**：没走到代理，`@Async/@Transactional` 这类 AOP 能力就像不存在。
2. **到底用了哪个执行器**：executor/scheduler 选错了，线程名、并发度、拒绝策略、异常处理都会跟着错。
3. **异常的可见性**：是希望调用方必须知道失败，还是允许它异步失败但必须被观测到？

所以这模块的取向是：尽量少用“只凭感觉判断”，尽量多给可验证的入口（对应的 `*LabTest#method`）。不是为了增加测试数量，而是为了让排障过程不至于只能只凭日志猜测。

## 两条主线，不要混在一起

### `@Async`：调用期拦截，真正执行发生在另一个线程

`@Async` 这件事核心链路可以收敛为一句话：**方法调用先落到代理上，代理把“真正执行”提交到 executor。**
如果代理不存在、调用绕开代理、或者 executor 选的不是预期中的那个，看到的现象就会开始变形。

可以把它粗略拆成四步理解：

1. 基础设施是否建立（`@EnableAsync`）
2. 调用是否经过代理（self-invocation 是典型坑）
3. 提交到哪个 executor（默认选择 / `@Async("beanName")` / `AsyncConfigurer`）
4. 异常如何回到调用方（Future/CompletableFuture）或落到 handler（void）

### `@Scheduled`：启动期注册，按时间触发

`@Scheduled` 更接近“系统级开关”：它不是在调用期拦截方法，而是启动时把方法注册成任务，然后由调度器线程在合适的时间点触发执行。

因此它的排障路径也不一样：

1. scheduling 开关是否打开（`@EnableScheduling`）
2. 任务是否注册成功（注册断言通常比“等它触发”更确定）
3. 触发与执行在哪个线程上（scheduler 线程 vs `@Scheduled + @Async` 的执行线程）
4. 异常语义是什么（抛异常后是否继续调度、异常由谁处理）

## 从哪里开始（按阅读目标选择入口）

- 想顺着主线阅读：从 [async-scheduling-async-proxy-mental-model.md](async-scheduling-async-proxy-mental-model.md) 开始，按主线向下走。
- 需要立即快速确认“这些结论是不是真的”：跑 `BootAsyncSchedulingBookMatrixLabTest`。
- 正在排障（不生效/线程不对/异常不见了）：跑 `BootAsyncSchedulingBranchMatrixLabTest`，然后去看 [断点地图](guide-breakpoint-map.md) 与 [关键分支矩阵](guide-branch-decision-matrix.md)。

## 源码与断点：先记住三个入口即可

如果愿意跟一遍源码，不需要急于把整个包都翻完。异步与调度这条链路，最常用的入口本质上就这三个：

- `org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor`：什么时候、如何把 bean 包成代理
- `org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`：什么时候决定提交到 executor、异常如何分流
- `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#processScheduled`：什么时候注册定时任务

更细的断点清单在 [断点地图](guide-breakpoint-map.md)，其定位更接近“排障用的备忘录”。

## 进一步验证（可选）

如果想把“读懂”变成可回归的事实，下面这些入口足够覆盖主线：

- `BootAsyncSchedulingBookMatrixLabTest`：主线最小集合（`@Async` + executor + `@Scheduled` on/off）
- `BootAsyncSchedulingLabTest`：`@Async` 代理、线程切换、异常、self-invocation
- `BootAsyncSchedulingSchedulingLabTest`：`@Scheduled` 开关与最小触发验证（不 flaky）

## 小结与下一章

下一章见：[01：`@Async` 心智模型：代理与线程切换](async-scheduling-async-proxy-mental-model.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`
- Exercise：`BootAsyncSchedulingExerciseTest`

上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[async-scheduling-async-proxy-mental-model.md](async-scheduling-async-proxy-mental-model.md)

<!-- BOOKIFY:END -->
