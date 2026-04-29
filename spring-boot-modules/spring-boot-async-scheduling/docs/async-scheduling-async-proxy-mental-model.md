# 01. `@Async` 心智模型：代理与线程切换
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（先把误会拆开）"

    `@Async` 最容易让人误会的一点是：它不是“这个方法天生异步”，而是“这次调用被代理拦下来，转手丢进线程池”。

    - 最稳的观察点：线程名（有没有切出去，一眼就知道）
    - 最常见的坑：忘了 `@EnableAsync`、或调用路径绕开代理（self-invocation 下一章会专门讲）
    - 进一步验证：`BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Async & Scheduling](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. Executor 与线程命名/并发边界](async-scheduling-executor-and-threading.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync`（见文末“对应实验/测试”），先跑一次“反例”，再跑一次“正例”，会更快建立边界感。


## 先说清楚：`@Async` 到底“异步”在哪

很多人第一次写 `@Async`，是为了把某段耗时逻辑卸载出去：不要卡住请求线程、不要让定时任务线程被拖慢、也别让调用方背着一个“明明可以并行”的等待。

然后会遇到第一个挫败：同样的注解，有时能切线程，有时像没写。表面上像不可解释，但机制边界本质上很硬：

- **没有基础设施**（没开 `@EnableAsync`）→ 注解基本等价于注释
- **没有经过代理** → 拦截器不会触发，也就不会提交到线程池
- **提交到了线程池** → 方法体才真的在另一个线程里执行

把这条链路记成一句话即可：

> 调用线程 → 代理 → 提交到 executor → 工作线程执行方法体

## 最直接的判断：线程名变了吗

当怀疑“它到底有没有异步”时，先不需要急于看日志级别、也别先怪线程池配置。

先问一个更具体的问题：

> 这次执行方法体时，线程名变了吗？

线程名是最稳定的观测点之一：它不需要理解 Spring 内部有多少层，只要能看到 `main` 变成了 `async-...`，就知道“切线程”确实发生了。

对应的最小证据入口：

- 没启用 async：`BootAsyncSchedulingLabTest#asyncAnnotationDoesNothingWithoutEnableAsync`
- 启用 async：`BootAsyncSchedulingLabTest#asyncRunsOnExecutorThreadWhenEnableAsyncPresent`
- 把线程名当成断言：`BootAsyncSchedulingLabTest#executorThreadNamePrefixIsAStableObservationPoint`

## 为什么必须有 `@EnableAsync`

`@Async` 不是一个“运行期扫描注解、临时决定异步”的隐式机制。它依赖的是启动期建立的那套基础设施：

- `AsyncAnnotationBeanPostProcessor` 在启动时识别 `@Async`
- 它把目标 bean 包成代理，并在代理里挂上异步拦截器
- 调用方法时，拦截器才有机会把调用提交给 executor

没有 `@EnableAsync`，这套基础设施不启动，代理也就不会出现。

## 小结

这一章到这里本质上就结束了：`@Async` 的核心不在注解本身，而在“代理 + 提交 + 线程池”。

下一章会把视线从“有没有切线程”往前挪一步：**到底提交到了哪个 executor，以及为什么线程名是第一把尺子**。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootAsyncSchedulingLabTest`
- 测试文件：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[async-scheduling-executor-and-threading.md](async-scheduling-executor-and-threading.md)

<!-- BOOKIFY:END -->
