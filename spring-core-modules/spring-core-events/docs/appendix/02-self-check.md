# 99 自检：Spring Events
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`SpringCoreEventsBookMatrixLabTest`
    - 分支入口：`SpringCoreEventsBasicsBranchMatrixLabTest` / `SpringCoreEventsAsyncTransactionalBranchMatrixLabTest`
    - 推荐先跑：`SpringCoreEventsLabTest` / `SpringCoreEventsTransactionalEventLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（建议反复对照）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
- Branch Matrix（基础事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
- Branch Matrix（异步/事务事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](../part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵](../part-00-guide/05-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](01-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. 发布事件与接收事件的最小闭环是什么？你如何用断言证明 listener 确实收到了事件？  
   - 证据入口：`SpringCoreEventsLabTest#listenerReceivesPublishedEvent`
2. 多个监听器能否观察同一个事件？你如何验证“不是只有一个 listener 生效”？  
   - 证据入口：`SpringCoreEventsLabTest#multipleListenersCanObserveTheSameEvent`
3. 多 listener 的顺序由什么决定？你如何用 `@Order` 把顺序固定成断言？  
   - 证据入口：`SpringCoreEventsLabTest#orderedListenersFollowOrderAnnotation`
4. `@EventListener(condition = ...)` 的 condition 何时求值？不满足时会怎样？  
   - 证据入口：`SpringCoreEventsLabTest#conditionalEventListenerOnlyRunsWhenConditionMatches`
5. payload event 是什么？为什么 `publishEvent("hello")` 也能被监听？  
   - 证据入口：`SpringCoreEventsLabTest#publishingPlainObjectsAlsoWorks_asPayloadEvents`
6. 事件默认是同步还是异步？你如何用线程名证明“默认同步”？  
   - 证据入口：`SpringCoreEventsLabTest#eventsAreSynchronousByDefault`
7. 同步事件里 listener 抛异常会怎样？它会不会打断发布方？  
   - 证据入口：`SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault`
8. `@Async` listener 的边界是什么？为什么没开 `@EnableAsync` 时它会“看起来没生效”？  
   - 证据入口：`SpringCoreEventsMechanicsLabTest#asyncListenerRunsOnDifferentThread_whenEnableAsyncIsOn` + `SpringCoreEventsMechanicsLabTest#asyncAnnotationIsIgnored_withoutEnableAsync`
9. `@TransactionalEventListener` 的 phase 边界是什么？你如何用对照用例证明 AFTER_COMMIT/AFTER_ROLLBACK 的分流？  
   - 证据入口：`SpringCoreEventsTransactionalEventLabTest#afterCommitListenerRunsOnlyAfterCommit` + `SpringCoreEventsTransactionalEventLabTest#afterCommitDoesNotRunOnRollback_butAfterRollbackDoes`
10. 你如何把事件分发改为“由 multicaster 异步派发”（而不是靠 `@Async`）？并证明 listener 运行在线程池线程上？  
    - 证据入口：`SpringCoreEventsAsyncMulticasterLabTest#asyncMulticasterDispatchesListenersOnExecutorThread`

## 退出条件（完成标准）

- 你能把事件系统拆成三条可验证分支：同步/异步（线程模型）→ 异常传播 → 事务阶段（afterCommit/afterRollback）。
- 你能在遇到“副作用时机不对/回滚仍触发”时，先用一条测试把事实锁定，再讨论架构（同步事件 vs 事务事件 vs 异步队列）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsLabTest` / `SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsListenerFilteringLabTest` / `SpringCoreEventsTransactionalEventLabTest` / `SpringCoreEventsAsyncMulticasterLabTest`

上一章：[90-common-pitfalls](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
