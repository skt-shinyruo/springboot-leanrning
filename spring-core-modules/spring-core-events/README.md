# spring-core-events

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”学习 **Spring Application Events（应用事件）**。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/](docs/README.md)。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

## 本模块的学习产出

- 使用 `ApplicationEventPublisher` 发布事件
- 使用 `@EventListener` 处理事件（多监听器、`@Order`、condition）
- payload 事件：发布普通对象（例如 String）也可以被监听
- 默认同步行为与异常传播
- 异步监听器：`@Async`（对比启用/不启用 `@EnableAsync`）
- （练习）异步 multicaster：让事件“默认异步”分发
- （串线）`@TransactionalEventListener`：after-commit 与事务边界

## 前置知识

- 建议先完成 `spring-core-beans`（理解 Bean/容器即可）
- 如果要理解 after-commit：建议同时完成 `spring-core-tx`
- 了解多线程的最小概念（异步 = 可能换线程）

## 关键命令

### 运行

```bash
mvn -pl :spring-core-events spring-boot:run
```

运行后观察控制台输出：

- 发布方发出 `UserRegisteredEvent`
- 监听方收到事件并写入内存审计日志
- runner 在发布后打印审计日志条目

### 测试

```bash
mvn -pl :spring-core-events test
```

## 推荐 docs 阅读顺序（从现象到机制）

1. [事件心智模型：发布与订阅在解耦什么？](docs/part-01-event-basics/01-event-mental-model.md)
2. [多监听器与顺序：为什么 `@Order` 值得认真对待？](docs/part-01-event-basics/02-multiple-listeners-and-order.md)
3. [condition 与 payload：按条件触发与接收普通对象](docs/part-01-event-basics/03-condition-and-payload.md)
4. [同步与异常传播：为什么监听器抛异常会炸到发布方？](docs/part-01-event-basics/04-sync-and-exceptions.md)
5. [异步监听器：`@Async` 生效需要什么？](docs/part-02-async-and-transactional/01-async-listener.md)
6. [异步广播：让事件“默认异步”（multicaster）](docs/part-02-async-and-transactional/02-async-multicaster.md)
7. [`@TransactionalEventListener`：after-commit 监听器与事务边界](docs/part-02-async-and-transactional/03-transactional-event-listener.md)
8. [常见坑清单（建议反复对照）](docs/appendix/01-common-pitfalls.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java` | 多监听器、`@Order`、condition、payload、默认同步 | ⭐⭐ | `docs/01` → `docs/03` |
| Lab | `src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsMechanicsLabTest.java` | 异常传播、`@Async`（启用/不启用）的线程差异 | ⭐⭐ | `docs/04`、`docs/05` |
| Lab（Perf/Concurrency） | `src/test/java/com/learning/springboot/springcoreevents/part03_perf_concurrency/SpringCoreEventsConcurrencyLabTest.java` | 并发发布事件 + 异步 multicaster 分发稳定性（避免“并发丢事件/串线”误判） | ⭐⭐ | `docs/06` + 并发专题 |
| Exercise | `src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseTest.java` | 多 listener/顺序/异步 multicaster 等练习 | ⭐⭐–⭐⭐⭐ | `docs/06`、`docs/90` |

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| 发布事件与最小闭环 | [docs/129](docs/part-01-event-basics/01-event-mental-model.md) | `SpringCoreEventsLabTest#listenerReceivesPublishedEvent` + `UserRegistrationService`/`UserRegisteredListener` | 发布方与监听方如何解耦、如何验证触发 |
| 多监听器广播 | [docs/130](docs/part-01-event-basics/02-multiple-listeners-and-order.md) | `SpringCoreEventsLabTest#multipleListenersCanObserveTheSameEvent` | 为什么多个监听器都能收到同一事件 |
| 监听器顺序（`@Order`） | [docs/130](docs/part-01-event-basics/02-multiple-listeners-and-order.md) | `SpringCoreEventsLabTest#orderedListenersFollowOrderAnnotation` | 为什么默认不该依赖顺序、如何做确定性断言 |
| 条件触发（SpEL） | [docs/131](docs/part-01-event-basics/03-condition-and-payload.md) | `SpringCoreEventsLabTest#conditionalEventListenerOnlyRunsWhenConditionMatches` | condition 如何过滤事件 |
| payload 事件 | [docs/131](docs/part-01-event-basics/03-condition-and-payload.md) | `SpringCoreEventsLabTest#publishingPlainObjectsAlsoWorks_asPayloadEvents` | 为什么 publish String 也能被监听 |
| 默认同步线程 | [docs/129](docs/part-01-event-basics/01-event-mental-model.md) | `SpringCoreEventsLabTest#eventsAreSynchronousByDefault` | 默认事件分发发生在发布方线程 |
| 异常传播 | [docs/132](docs/part-01-event-basics/04-sync-and-exceptions.md) | `SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault` | 为什么 listener 抛异常会炸到 publisher |
| `@Async` 监听器 | [docs/133](docs/part-02-async-and-transactional/01-async-listener.md) | `SpringCoreEventsMechanicsLabTest#asyncListenerRunsOnDifferentThread_whenEnableAsyncIsOn` | `@EnableAsync` 对 `@Async` 的影响 |
| after-commit 监听器（事务集成） | [docs/135](docs/part-02-async-and-transactional/03-transactional-event-listener.md) | `BootBusinessCaseLabTest#syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot` + `OrderEventListeners` | 为什么回滚时 sync 会执行但 after-commit 不会 |

## 常见 Debug 路径

- 不要只靠控制台输出判断机制：优先写“可断言的观察点”（例如内存 log）
- 验证异步最稳定的方式是“断言线程名 + CountDownLatch 等待”，而不是靠日志时序
- 顺序相关断言务必显式 `@Order`，否则很容易学到错误结论

## 常见坑

- 事件默认同步：慢监听器会拖慢发布方
- 异常默认会传播回发布方：同步事件不是“吞异常机制”
- 没有 `@EnableAsync` 时 `@Async` 会被忽略
- 不要依赖默认监听器顺序：需要确定性就用 `@Order`

## 参考

- Spring Framework Reference：Application Events and Listeners
