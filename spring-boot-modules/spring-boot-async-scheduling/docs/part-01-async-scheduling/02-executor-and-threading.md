# 02. Executor 与线程命名/并发边界
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（把 executor 这件事说透）"

    如果说上一章回答的是“`@Async` 为什么能切线程”，这一章回答的就是更现实的问题：**切到哪一个线程池？**

    - 反复用到的尺子：线程名（前缀能直接写成断言）
    - 最可能踩到的坑：项目里有多个 executor，但 `@Async` 选的不是直觉里的那个
    - 进一步验证：`BootAsyncSchedulingExecutorSelectionLabTest#whenMultipleExecutorsExist_namedTaskExecutorWinsAsDefault`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. `@Async` 心智模型：代理与线程切换](01-async-proxy-mental-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 异常传播：Future vs void](03-exceptions.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. Executor 与线程命名/并发边界**
- 建议入口：优先运行 `BootAsyncSchedulingExecutorSelectionLabTest#whenMultipleExecutorsExist_namedTaskExecutorWinsAsDefault`（见文末“对应 Lab/Test”），用线程名前缀把“到底选中了谁”写成断言。



## 这一章要解决的不是“怎么配线程池”，而是“怎么不再猜”

如果已经接受上一章的结论：`@Async` 会把执行提交到 executor，那么下一步自然是追问：

> 提交到哪个 executor？

在一个稍微复杂点的项目里，线程池往往不止一个：有 IO 池、CPU 池、批处理池，还有框架默认的那个。此时最容易出现的不是“配置写错”，而是“大家各写各的，最后没人能回答：到底是谁在跑”。

这一章我想把它收敛成三件事：

1. 默认 executor 的选择规则（以及如何让它变得可控）
2. 显式选择：`@Async("beanName")`
3. 切线程之后的副作用：ThreadLocal/MDC 等上下文的丢失与泄漏

## 默认 executor：直觉里的“默认”往往不默认

不写 `@Async("...")` 的时候，Spring 会帮助找一个“默认 executor”。问题是：当系统里 executor 多起来之后，人会开始凭印象说话——“我不是已经定义了线程池吗？”——但 Spring 选的不一定是预期的那个。

把选择规则记成三条就够用（它们都能在本模块里找到对应断言）：

- **只有一个 `TaskExecutor` bean**：它通常会被当作默认 executor
  - 证据入口：`BootAsyncSchedulingExecutorSelectionLabTest#whenSingleTaskExecutorBeanExists_itIsUsedAsDefaultAsyncExecutor`
- **有多个 executor**：名为 `taskExecutor` 的那个更容易胜出
  - 证据入口：`BootAsyncSchedulingExecutorSelectionLabTest#whenMultipleExecutorsExist_namedTaskExecutorWinsAsDefault`
- **实现 `AsyncConfigurer#getAsyncExecutor()`**：可以把“默认是谁”写死在配置里
  - 证据入口：`BootAsyncSchedulingExecutorSelectionLabTest#asyncConfigurerOverridesDefaultExecutorSelection_butQualifiedExecutorStillWorks`

## 显式选择：`@Async("beanName")`

`@Async("specialExecutor")` 的价值不在于“能跑”，而在于**减少含糊**：当真的在规划多个线程池（IO/CPU/低优先级）时，把边界写在代码上，比依赖默认行为更可靠。

证据入口：

- `BootAsyncSchedulingExecutorSelectionLabTest#asyncValueSelectsQualifiedExecutorByName`

## 线程名：别把它当“日志装饰”，它是尺子

线程名是排障时最划算的观测点之一。无需先懂 Spring 里那几层拦截器，只要线程名前缀是稳定的，就能把“它跑在哪”写成断言、写进报警、也写进团队约定里。

证据入口（把 threadNamePrefix 固化为断言）：

- `BootAsyncSchedulingLabTest#executorThreadNamePrefixIsAStableObservationPoint`

## 切线程之后：ThreadLocal / MDC 为什么会断

当执行真的切到线程池之后，很快会遇到一个现实问题：调用方线程里有上下文（traceId、tenantId、userId……），异步线程里却什么都没有。

这不是 Spring “没帮助带过去”，而是 ThreadLocal 的语义本来就只属于线程：线程一换，上下文自然断开。更麻烦的是线程池会复用线程——如果把上下文 set 进去却不清理，下一次任务就可能读到残留值（串号）。

证据入口（默认不传播 / 正确传播 / 错误写法导致泄漏）：

- 默认不传播：`BootAsyncSchedulingContextPropagationLabTest#threadLocalContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary`
- 正确传播 + finally 恢复：`BootAsyncSchedulingContextPropagationLabTest#taskDecoratorCanPropagateThreadLocalContext_andRestoreToAvoidLeaks`
- 错误写法泄漏：`BootAsyncSchedulingContextPropagationLabTest#buggyTaskDecoratorThatSkipsNullCanLeakPreviousThreadLocalValueAcrossTasks`

修复路线也就一条：对 `ThreadPoolTaskExecutor` 配 `TaskDecorator`，做“提交线程捕获 → 工作线程设置 → finally 清理/恢复”。注意一个细节：**捕获到 null 也要清理**，不然线程复用时更容易串号。

## 进一步验证（可选）

这一章相关的最小集合是：

- executor 选择矩阵：`BootAsyncSchedulingExecutorSelectionLabTest`
- 线程名前缀观测点：`BootAsyncSchedulingLabTest#executorThreadNamePrefixIsAStableObservationPoint`
- 上下文传播与泄漏：`BootAsyncSchedulingContextPropagationLabTest`

## 小结

executor 这件事，一旦在团队里变成“凭印象说话”，就会不断返工。最简单的解法不是多背几条规则，而是把“线程名/选择结果”写成可回归的事实：测试能断言、日志能定位、排障能收敛。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`

上一章：[part-01-async-scheduling/01-async-proxy-mental-model.md](01-async-proxy-mental-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-async-scheduling/03-exceptions.md](03-exceptions.md)

<!-- BOOKIFY:END -->
