# 第 136 章：90. 常见坑清单（建议反复对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：常见坑清单（建议反复对照）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 135 章：07. `@TransactionalEventListener`：为什么 after-commit 事件能“等事务提交后再执行”？](../part-02-async-and-transactional/135-07-transactional-event-listener.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 137 章：自测题（Spring Core Events）](137-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
   - Branch Matrix - 基础事件：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
   - Branch Matrix - 异步/事务事件：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[128-02-breakpoint-map.md](../part-00-guide/128-02-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[128-04-branch-decision-matrix.md](../part-00-guide/128-04-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[137-99-self-check.md](137-99-self-check.md)

- 本章主题：**90. 常见坑清单（建议反复对照）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest`

## 机制主线

- （本章主线内容暂以契约骨架兜底；建议结合源码与测试用例补齐主线解释。）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界


## 坑 1：误以为事件默认异步

- 事实：事件默认同步（见 `SpringCoreEventsLabTest#eventsAreSynchronousByDefault`）
- 影响：监听器慢会直接拖慢发布方

## 坑 2：监听器抛异常会炸到发布方

- 事实：同步事件在同一调用栈里执行（见 `SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault`）
- 学习建议：先理解默认行为，再谈隔离/重试

## 坑 3：没有 `@Order` 却依赖执行顺序

- 事实：默认顺序不稳定
- 建议：需要确定性（尤其 tests）时显式 `@Order`

## 坑 4：以为写了 `@Async` 就一定异步

- 事实：没有 `@EnableAsync` 时 `@Async` 会被忽略（见 mechanics lab）

## 坑 5：事件对象可变导致监听器互相污染


## 坑 6：监听器“没触发”其实是被过滤掉了（参数类型/条件不匹配）

- Symptom：你 `publishEvent(...)` 了，但某个 `@EventListener` 方法完全没进入；你甚至怀疑 multicaster/线程/事务有问题。
- Root Cause：Spring 的监听器分发有“筛选”阶段：最常见的是 **按监听器方法参数类型过滤**（以及 `@EventListener(condition = ...)` 进一步过滤）；类型/条件不匹配时，监听器就会被跳过。
- Verification：`SpringCoreEventsListenerFilteringLabTest#eventListener_shouldFilterByMethodParameterType`
- Breakpoints：`SimpleApplicationEventMulticaster#multicastEvent`、`ApplicationListenerMethodAdapter#supportsEventType`
- Fix：先把“到底有没有被分发/为什么被过滤”用可断言的最小 Lab 固化，再决定要不要换事件类型/改监听器签名/调整 condition。

- 建议：学习阶段优先用不可变事件（record），减少副作用

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest`

上一章：[07-transactional-event-listener](../part-02-async-and-transactional/135-07-transactional-event-listener.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99-self-check](137-99-self-check.md)

<!-- BOOKIFY:END -->
