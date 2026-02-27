# 07. SecurityContext / RequestContext：默认丢失、传播与泄漏
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（上下文丢失的真相）"

    如果在异步线程里拿不到“当前用户”或“当前请求”，先不必急于怀疑 Spring Security 或 MVC：它们大概率只是 ThreadLocal 的受害者。

    - 默认行为：线程一换，上下文就断（拿到 `null` 是正常的）
    - 更危险的情况：线程池复用 + 没清理 → 偶发串号（上一次任务的上下文残留）
    - 进一步验证：`BootAsyncSchedulingSecurityContextPropagationLabTest#delegatingSecurityContextExecutorCanPropagate_andCleansUpToAvoidThreadReuseLeaks`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[06. `@Async` × `@Transactional`：事务边界与执行线程](06-async-and-transactions.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08. Spring Boot `spring.task.*`：默认线程池/调度器与属性映射](08-boot-spring-task-autoconfig.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**07. SecurityContext / RequestContext：默认丢失、传播与泄漏**
- 建议入口：优先运行 `BootAsyncSchedulingSecurityContextPropagationLabTest#delegatingSecurityContextExecutorCanPropagate_andCleansUpToAvoidThreadReuseLeaks`（见文末“对应 Lab/Test”），再对照 RequestContext/ThreadLocal 的 decorator 用例把“清理”变成肌肉记忆。



## 先把危险说在前面：这不是“日志问题”

在很多团队里，“异步线程拿不到 MDC/traceId”最初只是一个排障不方便的问题；但当开始在异步线程里读取 `SecurityContext`、读取租户信息、读取 `RequestAttributes`，它就不再只是日志断链——它可能变成权限串号、租户串号、请求串号。

这一章把“上下文传播”从抽象 ThreadLocal 推进到两个真实对象：

- `SecurityContext`（当前用户/权限）
- `RequestContext`（请求属性：requestId/header/locale 等）

## 机制主线

### 1) 为什么默认会丢失：它们都是 ThreadLocal

这三类“上下文”在机制上高度同构：

- `ThreadLocal`：自定义的上下文
- MDC：日志上下文（底层也是 ThreadLocal）
- `SecurityContextHolder`：安全上下文（默认也是 ThreadLocal）
- `RequestContextHolder`：请求上下文（ThreadLocal 绑定 RequestAttributes）

因此结论也同构：

- **线程切换 = 默认不传播**
- **线程池复用 = 必须恢复/清理，否则必然有泄漏风险**

最小证据链（默认不传播）：

- ThreadLocal：`BootAsyncSchedulingContextPropagationLabTest#threadLocalContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary`
- SecurityContext：`BootAsyncSchedulingSecurityContextPropagationLabTest#securityContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary`
- RequestContext：`BootAsyncSchedulingRequestContextPropagationLabTest#requestContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary`

### 2) 如何传播：两条路线

#### 路线 A：通用路线（TaskDecorator）

适用于自己的 ThreadLocal、MDC、RequestContext 等：

- 提交时捕获
- 执行时设置
- finally 恢复/清理（关键）

正确写法（可传播且不泄漏）的最小证据链：

- ThreadLocal：`BootAsyncSchedulingContextPropagationLabTest#taskDecoratorCanPropagateThreadLocalContext_andRestoreToAvoidLeaks`
- RequestContext：`BootAsyncSchedulingRequestContextPropagationLabTest#taskDecoratorCanPropagateRequestContext_andRestoreToAvoidLeaks`

错误写法（捕获为 null 就跳过 / 不在 finally 清理）导致泄漏的最小证据链：

- ThreadLocal：`BootAsyncSchedulingContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousThreadLocalValueAcrossTasks`
- RequestContext：`BootAsyncSchedulingRequestContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousRequestAttributesAcrossTasks`

#### 路线 B：领域路线（Spring Security 的 Delegating*）

如果传播的是 SecurityContext，优先考虑 Spring Security 提供的 delegating wrapper：

- 它的核心价值不是“能传播”（当然也能手写 TaskDecorator）
- 而是把“捕获/设置/finally 清理”的正确细节做成可复用、可审计的基础设施

最小证据链（可传播 + 无泄漏）：

- `BootAsyncSchedulingSecurityContextPropagationLabTest#delegatingSecurityContextExecutorCanPropagate_andCleansUpToAvoidThreadReuseLeaks`

## 一眼能看到的现象

- 默认：
  - 调用方线程能读到上下文
  - 异步线程读到 null
- 修复后：
  - 异步线程读到与调用方一致的上下文
  - 当调用方上下文清空后，第二次异步任务不会读到上一次残留（无串号）

## 源码与断点

### TaskDecorator（通用上下文）

- `org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor#execute`：任务提交点（decorate 的发生位置）
- `TaskDecorator#decorate`：捕获 captured/previous，以及 finally 清理

### Spring Security delegating wrapper

- `org.springframework.security.concurrent.DelegatingSecurityContextRunnable#run`
- `org.springframework.security.concurrent.DelegatingSecurityContextCallable#call`

### RequestContext

- `org.springframework.web.context.request.RequestContextHolder#setRequestAttributes`
- `org.springframework.web.context.request.RequestContextHolder#resetRequestAttributes`

## 最小可运行实验（Lab）

- `BootAsyncSchedulingContextPropagationLabTest`
- `BootAsyncSchedulingSecurityContextPropagationLabTest`
- `BootAsyncSchedulingRequestContextPropagationLabTest`

## 常见坑与边界

### 坑点 1：以为“上下文丢失”只是个日志问题（实际上可能是权限/租户串号）

异步线程拿不到当前用户/租户只是第一阶段；更糟糕的是“偶发拿到错误用户/错误租户”（串号）。根因是 ThreadLocal 的线程语义 + 线程池的复用语义叠起来，放大了“未清理”的后果。

证据入口：

- SecurityContext 默认丢失：`BootAsyncSchedulingSecurityContextPropagationLabTest#securityContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary`
- RequestContext 泄漏反例：`BootAsyncSchedulingRequestContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousRequestAttributesAcrossTasks`

工程上的修法通常有两条底线：

- 明确哪些上下文允许跨线程传播（能不传播就别传播，传播面越小越安全）
- 统一收敛到 TaskDecorator / Delegating*，并在 finally 做恢复/清理（把“清理”当成必须项，而不是可选项）

## 小结与下一章

- 下一章进入 Spring Boot `spring.task.*`：把“默认 executor/scheduler 是谁、属性映射到哪里”写成断言，避免靠猜。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingContextPropagationLabTest`
- Lab：`BootAsyncSchedulingSecurityContextPropagationLabTest`
- Lab：`BootAsyncSchedulingRequestContextPropagationLabTest`

上一章：[part-01-async-scheduling/06-async-and-transactions.md](06-async-and-transactions.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/08-boot-spring-task-autoconfig.md](08-boot-spring-task-autoconfig.md)

<!-- BOOKIFY:END -->
