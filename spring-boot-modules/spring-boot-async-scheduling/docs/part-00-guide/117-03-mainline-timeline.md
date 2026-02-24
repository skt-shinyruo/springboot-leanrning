# 第 117 章：主线时间线：Spring Boot Async & Scheduling
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（路线图）"

    这一章不讲新 API，它更像“路标”：告诉你这模块要解决哪些边界问题，以及为什么章节按这个顺序组织。

    - 你会带走：一条主线（代理 → 提交 → 执行）+ 两个开关（EnableAsync / EnableScheduling）
    - 常见误会：写了注解就会生效；看到了日志就等于“真的异步”
    - 进一步验证：`BootAsyncSchedulingBookMatrixLabTest`（主线） / `BootAsyncSchedulingBranchMatrixLabTest`（分支）
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 116 章：Async/Scheduling 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 118 章：00 - Deep Dive Guide（springboot-async-scheduling）](118-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话：这一章是路线图——把 `@Async/@Scheduled` 的主线与常见分支先摆出来，后面每一章都是在填这张图的细节。
- 下一章：进入“深挖导读”，把“怎么读、怎么验证、怎么排障”说得更具体一些。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKLIKE-V2:INTRO:START -->
如果你愿意按主线顺读，建议把注意力放在三个反复出现的问题上：有没有代理、用了哪个 executor/scheduler、异常最终落在哪。

这不是“背结论”，而是为了让你在真实项目里遇到不确定行为时，能用一套稳定的判断方法收敛问题。
<!-- BOOKLIKE-V2:INTRO:END -->

## 先把“这模块到底在讲什么”说清楚

在业务代码里，`@Async` 和 `@Scheduled` 都像是“加个注解就行”。但在排过几次线上问题之后，你会发现它们其实一直在问同一类问题：

- 这次调用到底有没有经过代理？（没经过就谈不上拦截、线程切换、事务等 AOP 能力）
- 这次执行最终落在哪个线程池/调度器上？（你以为的“默认”很可能不是默认）
- 异常到底去哪了？（你是要“调用方必须知道”，还是“只要能观测到即可”）

本模块的文档与测试基本都围绕这三件事展开：把不可见的边界变成可观察的事实。

## 为什么主线按这个顺序排

很多讲 `@Async` 的文章会从“怎么配置线程池”开始，但我更倾向于先把“它靠什么生效”讲透：如果你连代理链都没走到，线程池配置再漂亮也没意义。

下面这条顺序，刻意从“机制前提”一路走到“真实工程边界”。

1. 先把 `@Async` 的“生效点”讲明白：代理与线程切换  
   - 阅读：[01：`@Async` 心智模型：代理与线程切换](../part-01-async-scheduling/119-01-async-proxy-mental-model.md)  
   - 为什么先讲它：大多数“不生效”，根因都在这里。

2. 再谈 executor：你想让它跑在哪个线程池上  
   - 阅读：[02：Executor 与线程命名/并发边界](../part-01-async-scheduling/120-02-executor-and-threading.md)  
   - 为什么紧接着讲：线程名是最稳定的观测点；executor 选择规则不弄清楚，后面所有“我以为”都站不住。

3. 然后处理异常：异步失败，到底是谁要负责知道  
   - 阅读：[03：异常传播：Future vs void](../part-01-async-scheduling/121-03-exceptions.md)  
   - 为什么现在讲：你会很快发现“没看到异常”并不等于“没失败”。

4. 把最常见的坑单独拎出来：self-invocation  
   - 阅读：[04：self-invocation：为什么异步有时不生效](../part-01-async-scheduling/122-04-self-invocation.md)  
   - 为什么要单独一章：这不是 `@Async` 的“特殊规则”，而是 AOP 的通用边界；懂了它，`@Transactional`、校验、权限这些也顺带更清晰。

5. 接着进入 `@Scheduled`：它不是“调用期拦截”，而是“启动期注册 + 运行期触发”  
   - 阅读：[05：`@Scheduled` 基础与可测试性](../part-01-async-scheduling/123-05-scheduling-basics.md)  
   - 为什么放在后面：调度的坑更像“时间相关 + 线程相关”的组合题，先把 `@Async` 的线程模型打稳更省心。

6. 最后补上三章工程边界：事务、上下文、Boot 自动装配  
   - [06：`@Async` × `@Transactional`：事务边界与执行线程](../part-01-async-scheduling/126-06-async-and-transactions.md)  
   - [07：SecurityContext / RequestContext：默认丢失、传播与泄漏](../part-01-async-scheduling/127-07-security-and-request-context.md)  
   - [08：Spring Boot `spring.task.*`：默认线程池/调度器与属性映射](../part-01-async-scheduling/128-08-boot-spring-task-autoconfig.md)  
   - 为什么最后讲：这些问题通常不是“你没会用注解”，而是“线程边界 + 线程池复用 + 默认行为不透明”叠起来的结果。

## 如果你现在是在排障

- 想确认“到底有没有走代理 / 有没有切线程 / 异常去哪了”：先看 [关键分支矩阵](118-04-branch-decision-matrix.md)
- 想从源码里搞清楚“为什么这次没生效”：用 [断点地图](118-02-breakpoint-map.md) 省掉翻来覆去找入口的时间
- 想快速对照常见坑：看 [常见坑清单](../appendix/124-90-common-pitfalls.md)，每个坑都有最小复现入口

## 进一步验证（可选）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 主线最小集合：`BootAsyncSchedulingBookMatrixLabTest`
- 关键分支最小集合：`BootAsyncSchedulingBranchMatrixLabTest`
- 如果你想从断点看一遍完整链路：从 `AsyncAnnotationBeanPostProcessor`（代理建立）→ `AsyncExecutionInterceptor`（提交）→ `ScheduledAnnotationBeanPostProcessor`（调度注册）这三个入口切入就够了。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

下一章：深挖导读（把阅读路线与验证入口放在一起说清楚）。
