# 性能与并发（可复现实验范式）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：性能与并发（可复现实验范式）
    - 怎么使用：先读“可复现范式”，再选一个样板实验（线程池饱和 / 异步事件 / SpEL 并发求值）跑通并打断点，最后回到对应模块的断点地图深挖。
    - 原理：本页不主讲某个模块机制；它收敛“如何写不 flaky 的并发/性能实验”的方法论与入口索引。
    - 源码入口：见“样板索引（本仓库可跑入口）”中的每个 Lab/Test。
    - 推荐 Lab：`BootAsyncSchedulingExecutorSaturationLabTest` / `SpringCoreSpelConcurrencyLabTest`
<!-- CHAPTER-CARD:END -->


本页目标：把“并发/性能问题”从主观体验（偶发、难复现）变成可断言的证据链：**用稳定的并发编排 + 明确的可观测点**，复现关键边界，再用断点走读源码主线。

> 说明：本仓库的“性能/并发”更多是机制边界与并发语义验证，而不是微基准跑分。需要真实性能数据时，建议独立引入 JMH，而不是把耗时阈值塞进单测。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：性能与并发（可复现实验范式） —— 用 latch/屏障 + 明确观测点（拒绝/异常/线程边界/指标）把并发问题做成可回归实验，而不是靠“运气复现”。
- 回到主线：当你在任一模块读到“异步/并发/性能边界”时，先回到本页选择一个可跑样板，再带着断点回到模块的调用链与断点地图。
- 下一章：建议回到书的 [Labs 索引](labs-index.md) 或对应模块 README，按主题进入具体机制章节。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「性能与并发（可复现实验范式）」展开：先把“什么算证据”说清楚，再收敛一组可复用的编排套路，最后给出本仓库的样板入口，保证你能跑起来、能复现、能打断点。

阅读建议：
- 先看第 1 节，把“如何不写 flaky 的并发实验”建立成默认习惯；
- 再从第 2 节挑一个样板实验跑通（建议从线程池饱和开始），把断点装在关键入口上；
- 最后回到模块的调用链/断点地图，对照源码主线理解“为什么会这样”。
<!-- BOOKLIKE-V2:INTRO:END -->

## 1) 可复现范式（Deterministic Playbook）

### 1.1 只断言“可观测事实”，不要断言耗时阈值

优先选择这些“可观测事实”作为断言：

- **异常路径**：必然抛出 `RejectedExecutionException` / `TimeoutException` / 特定业务异常
- **线程边界**：线程名前缀变化（主线程 vs 线程池线程）、是否发生线程切换
- **有无发生**：计数器/队列长度/收集到的事件条目数量（不是耗时）
- **顺序约束**：`@Order`、同步/异步分发先后（用 latch/队列记录）
- **可观测信号**：日志捕获（ListAppender/OutputCapture）、metrics 增量（MeterRegistry）

避免这些不稳定证据：

- `sleep(200ms)` 后“应该完成”
- “应该在 50ms 内完成”
- “跑起来感觉快了很多”

### 1.2 用 latch/屏障编排并发，而不是靠 sleep 赌时序

常用组合：

- `CountDownLatch startGate`：让所有 worker 同一时刻起跑（减少抖动）
- `CountDownLatch doneGate`：等待 worker 结束（防止测试提前退出）
- `Semaphore`：限制并发度（模拟资源瓶颈）

核心原则：**测试线程控制时序**，而不是被调度器控制。

### 1.3 通过线程命名/自建线程池把“边界”显式化

建议做法：

- 每个实验自建 `ExecutorService`，并使用线程名前缀（例如 `perf-` / `events-async-`）
- 对异步链路，在可观测点输出线程名并断言（例如 listener 线程名）
- 测试结束显式 `shutdown`（避免线程泄露影响其它测试）

### 1.4 通过“失败路径”做确定性断言（比成功路径更稳定）

很多并发/性能边界的确定性来自失败分支：

- 线程池饱和 → 拒绝策略必然触发
- 同步事件 listener 抛异常 → publisher 必然感知
- 共享对象的并发访问 → 如果有状态泄露，通常能稳定暴露（但要控制输入规模）

### 1.5 避免全局共享状态：每个测试都是独立上下文

原则：

- 不要依赖先前测试“跑过一次”留下的缓存/线程池
- 如果一定要用缓存（例如 SpEL expression cache），要把缓存作为“实验对象”，而不是隐式依赖

### 1.6 当你确实需要“等待”，用显式超时 + 诊断信息

推荐：

- 自建 `Waiter`（轮询条件 + 超时消息），失败时输出关键信息（线程名/队列长度/已收集事件）

不推荐：

- `Thread.sleep` + “差不多应该好了”

---

## 2) 样板索引（本仓库可跑入口）

> 提示：你也可以从书的 [Labs 索引](labs-index.md) 反向按模块查找所有 `*LabTest`。

### 2.1 线程池饱和与拒绝策略（确定性复现）

- 模块：`springboot-async-scheduling`
- 推荐入口：
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExecutorSaturationLabTest test`
- 对应测试类：
  - `spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part02_perf_concurrency/BootAsyncSchedulingExecutorSaturationLabTest.java`

这个样板解决的问题：**如何不靠耗时阈值，稳定断言“线程池已饱和/拒绝策略已触发”。**

### 2.2 事件异步分发的线程边界（可断言：publisher vs listener）

- 模块：`spring-core-events`
- 推荐入口：
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsExerciseSolutionTest test`
- 对应测试类：
  - `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseSolutionTest.java`

这个样板解决的问题：**当你配置 async multicaster / @Async listener 时，如何稳定证明“确实发生了线程切换”。**

### 2.3 SpEL 并发求值（复用 parsed expression + per-thread context）

- 模块：`spring-core-spel`
- 推荐入口：
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelConcurrencyLabTest test`
- 对应测试类：
  - `spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part02_perf_concurrency/SpringCoreSpelConcurrencyLabTest.java`

这个样板解决的问题：**并发下复用解析结果（Expression）是可行的，但 EvaluationContext 要按线程隔离。**

### 2.4 全量模块入口（并发/性能 Labs）

> 说明：下表入口统一采用“可复现范式”（latch/屏障/可观测事实/失败路径），避免 `sleep + 耗时阈值` 的 flaky 断言。

| 模块 | 推荐入口（命令） | 对应测试类 |
| --- | --- | --- |
| `springboot-basics` | `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsEnvironmentConcurrencyLabTest test` | `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part02_perf_concurrency/BootBasicsEnvironmentConcurrencyLabTest.java` |
| `springboot-web-mvc` | `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcRequestScopeIsolationLabTest test` | `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_perf_concurrency/BootWebMvcRequestScopeIsolationLabTest.java` |
| `springboot-data-jpa` | `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaEntityManagerConcurrencyLabTest test` | `spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part02_perf_concurrency/BootDataJpaEntityManagerConcurrencyLabTest.java` |
| `springboot-actuator` | `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorMetricsConcurrencyLabTest test` | `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part02_perf_concurrency/BootActuatorMetricsConcurrencyLabTest.java` |
| `springboot-testing` | `mvn -q -pl :spring-boot-testing -Dtest=BootTestingTestContextCacheLabTest test` | `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part02_perf_concurrency/BootTestingTestContextCacheLabTest.java` |
| `springboot-business-case` | `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseConcurrentOrderPlacementLabTest test` | `spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part02_perf_concurrency/BootBusinessCaseConcurrentOrderPlacementLabTest.java` |
| `springboot-security` | `mvn -q -pl :spring-boot-security -Dtest=BootSecuritySecurityContextIsolationLabTest test` | `spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part02_perf_concurrency/BootSecuritySecurityContextIsolationLabTest.java` |
| `springboot-web-client` | `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientRestClientConcurrencyLabTest test` | `spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part02_perf_concurrency/BootWebClientRestClientConcurrencyLabTest.java` |
| `springboot-cache` | `mvn -q -pl :spring-boot-cache -Dtest=BootCacheStampedeProtectionLabTest test` | `spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part02_perf_concurrency/BootCacheStampedeProtectionLabTest.java` |
| `spring-core-aop` | `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyConcurrencyLabTest test` | `spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_perf_concurrency/SpringCoreAopProxyConcurrencyLabTest.java` |
| `spring-core-aop-weaving` | `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwConcurrencyLabTest test` | `spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_perf_concurrency/AspectjLtwConcurrencyLabTest.java` |
| `spring-core-tx` | `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxThreadLocalBoundaryLabTest test` | `spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part02_perf_concurrency/SpringCoreTxThreadLocalBoundaryLabTest.java` |
| `spring-core-validation` | `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationValidatorConcurrencyLabTest test` | `spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part02_perf_concurrency/SpringCoreValidationValidatorConcurrencyLabTest.java` |
| `spring-core-resources` | `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesPatternResolverConcurrencyLabTest test` | `spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part02_perf_concurrency/SpringCoreResourcesPatternResolverConcurrencyLabTest.java` |
| `spring-core-profiles` | `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesEnvironmentConcurrencyLabTest test` | `spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part02_perf_concurrency/SpringCoreProfilesEnvironmentConcurrencyLabTest.java` |
| `spring-core-beans` | `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansConcurrentGetBeanLabTest test` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_perf_concurrency/SpringCoreBeansConcurrentGetBeanLabTest.java` |
