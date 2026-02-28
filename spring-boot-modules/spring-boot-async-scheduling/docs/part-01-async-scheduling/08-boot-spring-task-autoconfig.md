# 08. Spring Boot `spring.task.*`：默认线程池/调度器与属性映射
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（别和“默认”较劲）"

    这一章解决的其实是一个很具体的痛点：改了 `spring.task.*`，但线程名没变；以为自己在用 Boot 的默认线程池，但实际跑的是另一个 executor/scheduler。

    - 更稳妥的做法：把线程名前缀写成断言（别靠猜）
    - 进一步验证：`BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskExecutionPropertiesConfigureDefaultExecutor_andAsyncUsesIt`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[07. SecurityContext / RequestContext：默认丢失、传播与泄漏](07-security-and-request-context.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑清单（Async & Scheduling）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskExecutionPropertiesConfigureDefaultExecutor_andAsyncUsesIt`（见文末“对应 Lab/Test”），用线程名前缀把“配置到底影响了谁”写成断言。


## “我明明改了配置，为什么没生效？”

这句抱怨在排异步/调度问题时特别常见。改了：

- `spring.task.execution.thread-name-prefix`
- `spring.task.scheduling.thread-name-prefix`

结果线程名没变，于是开始怀疑是不是配置文件没加载、是不是 profile 没激活、是不是 Boot 有 bug……但很多时候真实原因更直接：**根本没用到 Boot 给那个默认 executor/scheduler**。

这一章的目标很简单：不用背 bean 名，也不用背自动装配类的细节，只需能回答三件事就够了：

- 这次 `@Async` 实际用的是哪个 executor？
- 这次 `@Scheduled` 实际用的是哪个 scheduler？
- `spring.task.*` 的属性，最终映射到了哪里？

### 1) `spring.task.execution.*`：默认 TaskExecutor 的来源与线程名观测点

Spring Boot 会根据 `spring.task.execution.*` 的配置创建默认执行器（TaskExecutor/AsyncTaskExecutor）。在机制上最需要抓住的不是 bean 名，而是：

- **线程名是最稳定的观测点**
- 只要能把线程名前缀写成断言，就能反向证明“用的是哪个 executor”

最小证据链（属性映射 → @Async 线程名前缀）：

- `BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskExecutionPropertiesConfigureDefaultExecutor_andAsyncUsesIt`

当遇到“我明明改了 thread-name-prefix，但线程名没变”时，常见原因只有两类：

1. 没启用 `@EnableAsync`（基础设施未建立，`@Async` 等价于不存在）
2. 提供了自己的 executor（按名称/按类型被选中），覆盖了 Boot 的默认

对应排查与证据链：对照 executor 选择矩阵章节与 Lab：

- `BootAsyncSchedulingExecutorSelectionLabTest`

### 2) `spring.task.scheduling.*`：默认 TaskScheduler 的来源与调度线程观测点

同样地，调度器的关键不是“记住 bean 叫啥”，而是：

- 让调度线程名具备稳定前缀
- 用最小触发断言固化“确实用的是 Boot 的 scheduler”

最小证据链（属性映射 → @Scheduled 线程名前缀）：

- `BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskSchedulingPropertiesConfigureTaskScheduler_andScheduledUsesIt`

## 应当观察到的现象

- 配置了 `spring.task.execution.thread-name-prefix`：
  - `@Async` 的执行线程名应当以该前缀开头（在没有提供其它 executor 覆盖的前提下）
- 配置了 `spring.task.scheduling.thread-name-prefix`：
  - `@Scheduled` 的触发线程名应当以该前缀开头（在没有提供其它 scheduler 覆盖的前提下）

## 源码与断点

- Boot 自动装配入口（理解“属性→bean”映射）：
  - `org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration`
  - `org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration`
- Async 选择 executor 的关键分支：
  - `org.springframework.aop.interceptor.AsyncExecutionAspectSupport#determineAsyncExecutor`
- Scheduling 选择 scheduler 的关键分支：
  - `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor`（注册点）
  - `org.springframework.scheduling.config.TaskSchedulerRouter`（按类型选择/兜底的分支）

## 最小可运行实验（Lab）

- Lab：`BootAsyncSchedulingSpringTaskAutoConfigurationLabTest`

## 常见坑与边界

### 坑点 1：以为改了 `spring.task.*` 就一定影响 `@Async/@Scheduled`

最常见的现象就是：改了 `thread-name-prefix`，但线程名没有变化。

根因通常只有两类：

- 没启用对应基础设施（没开 `@EnableAsync/@EnableScheduling`，那当然不会生效）
- 被更高优先级覆盖了（自己提供了 executor/scheduler，或者系统里有多个 executor 被按规则选中了）

证据入口：

- Boot 属性映射 → `@Async` 线程名：`BootAsyncSchedulingSpringTaskAutoConfigurationLabTest#springTaskExecutionPropertiesConfigureDefaultExecutor_andAsyncUsesIt`
- executor 选择优先级：`BootAsyncSchedulingExecutorSelectionLabTest#whenMultipleExecutorsExist_namedTaskExecutorWinsAsDefault`

修法也很朴素：先用线程名前缀把“实际使用的是谁”写成断言，再去找覆盖来源（别从配置文件开始猜）。

## 小结与下一章

- 下一章进入 Appendix：常见坑与自检题，把这三条“边界机制”（事务/上下文/自动装配）加入排障闭环。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingSpringTaskAutoConfigurationLabTest`

上一章：[part-01-async-scheduling/07-security-and-request-context.md](07-security-and-request-context.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->
