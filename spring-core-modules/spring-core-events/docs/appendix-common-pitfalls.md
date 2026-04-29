# 01. 常见坑清单（排查时对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕常见坑清单（排查时对照）展开，主线可以概括为：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。

    先运行 `SpringCoreEventsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。

    需要下探源码时，可以从 `org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. `@TransactionalEventListener`：为什么 after-commit 事件能“等事务提交后再执行”？](async-and-transactional-transactional-event-listener.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. 自测题（Spring Core Events）](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试 收束答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

### 排障骨架（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，可以按下面 6 步收敛问题（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
   - Branch Matrix - 基础事件：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
   - Branch Matrix - 异步/事务事件：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/观察点/关键日志收齐：[04-breakpoint-map.md](guide-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](guide-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](appendix-self-check.md)

这页不是教材，更接近“排障备忘录”。使用方式是：**先跑最小复现入口，再回来看坑位对照表**，而不是读完概念再猜配置。

!!! summary "这页主要帮助排除的误判"

    - “事件是异步的”：不是，默认同步；先用线程名把它断言出来。
    - “监听器没触发”：先排除过滤（参数类型/condition）与事务阶段（after-commit）差异。
    - “异常不会影响发布方”：默认会；想隔离就显式引入异步/捕获策略，并把行为写进测试。


!!! example "先运行的入口（把坑跑成断言）"

    - 基础分支：`SpringCoreEventsLabTest`
    - 异常/异步开关：`SpringCoreEventsMechanicsLabTest`
    - 过滤（类型/condition）：`SpringCoreEventsListenerFilteringLabTest`
    - 事务阶段：`SpringCoreEventsTransactionalEventLabTest`
    - 异步 multicaster：`SpringCoreEventsAsyncMulticasterLabTest`
## 最小可运行实验（Lab）

- Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest`
- 运行命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsTransactionalEventLabTest`

## 坑 1：误以为事件默认异步

- 会看到：发布方耗时变长、线程名不变；甚至被监听器异常打断，但以为“只是发了个事件”。
- 事实：事件默认同步（验证：`SpringCoreEventsLabTest#eventsAreSynchronousByDefault`）。
先把“默认同步”当作基线；需要隔离耗时/失败时，再选 `@Async` listener 或 async multicaster，并用线程名/时机写断言。

## 坑 2：监听器抛异常会炸到发布方

- 会看到：监听器一抛异常，发布方也跟着失败；后续 listener 可能没机会执行。
- 事实：同步事件在同一调用栈里执行，异常默认向上冒泡（验证：`SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault`）。
先明确异常处理策略（吞掉/转换/重试/隔离），再选择捕获策略或异步方案，并把行为写进测试。

## 坑 3：没有 `@Order` 却依赖执行顺序

- 会看到：多个监听器都能收到事件，但暗中依赖“先后顺序”，一换环境就开始漂移。
- 事实：顺序不是“想当然的注册顺序”。需要顺序就显式 `@Order`（验证：`SpringCoreEventsLabTest#orderedListenersFollowOrderAnnotation`）。
把顺序当成契约：要么明确 `@Order`，要么让监听器互不依赖（避免“靠顺序拼业务”）。

## 坑 4：以为写了 `@Async` 就一定异步

- 会看到：以为会换线程，但它仍在发布方线程里跑（线程名不变、耗时变长）。
- 事实：没有 `@EnableAsync` 时 `@Async` 会被忽略（验证：`SpringCoreEventsMechanicsLabTest#asyncAnnotationIsIgnored_withoutEnableAsync`）。
先把“是否真的异步”写成断言（线程名/时机），再讨论线程池、上下文传播与失败策略。

## 坑 5：事件对象可变导致监听器互相污染

事件对象会被同一次分发链路里的多个 listener 共享。如果事件是可变对象，那么：

- 会得到“看似偶发”的副作用：某个 listener 改了字段，另一个 listener 读到的是被修改后的状态。
- 会更难写断言：因为“谁先改、谁后读”会被顺序/异步放大成不稳定。

学习阶段最简单的修法是把“可变事件”当成代码味道：把事件建模为不可变对象（例如 `record`），让事件只承载事实而不承载过程状态；如果需要派生信息，在 listener 内部创建新对象（不要回写 event）。

## 坑 6：监听器“没触发”本质上是被过滤掉了（参数类型/条件不匹配）

`publishEvent(...)` 了，但某个 `@EventListener` 方法完全没进入；甚至怀疑 multicaster/线程/事务有问题。

Spring 的监听器分发有“筛选”阶段：最常见的是 **按监听器方法参数类型过滤**（以及 `@EventListener(condition = ...)` 进一步过滤）；类型/条件不匹配时，监听器就会被跳过。

`SpringCoreEventsListenerFilteringLabTest#eventListener_shouldFilterByMethodParameterType`

`SimpleApplicationEventMulticaster#multicastEvent`、`ApplicationListenerMethodAdapter#supportsEventType`

先把“到底有没有被分发/为什么被过滤”用可断言的最小 Lab 固化，再决定要不要换事件类型/改监听器签名/调整 condition。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest`

上一章：[07-transactional-event-listener](async-and-transactional-transactional-event-listener.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[99-self-check](appendix-self-check.md)

<!-- BOOKIFY:END -->
