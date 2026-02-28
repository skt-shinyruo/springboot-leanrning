# 04. self-invocation：为什么异步有时不生效
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（AOP 的经典边界）"

    这一章讲的不是 `@Async` 的“特殊规则”，而是 Spring AOP 的一个老坑：**self-invocation 会绕开代理。**

    - 现象：同一个类里调用自己的 `@Async` 方法，不切线程
    - 根因：调用没经过 proxy，拦截器就不会触发
    - 进一步验证：`BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 异常传播：Future vs void](03-exceptions.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. `@Scheduled` 基础与可测试性](05-scheduling-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall`（见文末“对应 Lab/Test”），再跑一次“跨 bean 边界”的对照用例，差异会非常直观。


## 这个坑为什么这么“顽固”

可能遇到过这种情况：

- 外部调用 `someService.doAsync()`：线程名变了，确实异步
- 但在 `SomeService` 自己内部调用 `this.doAsync()`：线程名不变，像是没写 `@Async`

这不是哪里写错了，而是 self-invocation（自调用）天然会绕开 Spring 代理。

## self-invocation：发生了什么

把 `@Async` 当成“方法上的注解”很容易误会；把它当成“代理上的能力开关”就顺了：

- 代理负责拦截方法调用
- 异步拦截器负责把任务提交给 executor
- **调用没经过代理** → 拦截器不触发 → 自然不切线程

所谓 self-invocation，最常见的形态就是同一个 bean 内部直接调用自己的方法（`this.xxx()` 或者隐式的内部方法调用）。

## 两个最小事实（跑一次就会信）

- 自调用绕过：`BootAsyncSchedulingLabTest#selfInvocationBypassesAsyncAsAPitfall`
- 跨 bean 边界（走代理）：`BootAsyncSchedulingLabTest#callingAsyncThroughAnotherBeanGoesThroughProxy`

如果在 IDE 里跟一下调用栈，会很直观：前者不会进入 `CglibAopProxy` / JDK proxy 的 invocation handler，后者会。

## 怎么修才“稳”

修法很多，但最稳的一类都遵循一个原则：**让调用路径必然经过代理**。

常见的工程做法是拆分职责：

- `OuterService` 负责编排与事务/校验边界
- `InnerAsyncService` 只负责异步执行

这不是为了“代码好看”，而是为了把机制边界写死：不会再因为“某个同事顺手写了个内部调用”而让异步悄悄失效。

## 断点入口（可选）

- 代理层：`org.springframework.aop.framework.CglibAopProxy#intercept`（确认有没有进代理）
- 异步拦截：`org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`（确认有没有提交到 executor）

## 小结

self-invocation 不是 `@Async` 的专属坑，`@Transactional`、校验、权限等所有基于 AOP 的能力都共享这条边界。弄明白它，会少掉很多“看起来像玄学”的排障时间。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`

上一章：[part-01-async-scheduling/03-exceptions.md](03-exceptions.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/05-scheduling-basics.md](05-scheduling-basics.md)

<!-- BOOKIFY:END -->
