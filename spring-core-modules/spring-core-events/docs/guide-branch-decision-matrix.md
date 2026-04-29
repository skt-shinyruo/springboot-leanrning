# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：分支来自：listener 是否匹配（condition/payload/type）、multicaster 是否有 executor、事务事件是否在事务内发布。

    把事件系统的关键分支（listener 匹配/同步异步/事务事件）整理成矩阵表；每行都对应一个可跑入口与观察点。

    对照入口：`SpringCoreEventsBasicsBranchMatrixLabTest`。需要下探源码时，可以从 `AbstractApplicationEventMulticaster#multicastEvent` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Spring Events）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 事件心智模型：发布（publish）与订阅（listen）到底在解耦什么？](event-basics-event-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreEventsBasicsBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：分支来自：listener 是否匹配（condition/payload/type）、multicaster 是否有 executor、事务事件是否在事务内发布。需要下探源码时，可以从 `AbstractApplicationEventMulticaster#multicastEvent` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| listener filtering | condition/payload/type | 只有匹配的 listener 被调用 | `SpringCoreEventsListenerFilteringLabTest` | listeners 列表/调用次数 |
| 同步分发 | 默认 multicaster | 发布者线程执行 listener | `SpringCoreEventsMechanicsLabTest` | 线程名/调用顺序 |
| 异步分发 | multicaster 配置 executor | listener 在异步线程执行 | `SpringCoreEventsAsyncMulticasterLabTest` | 线程名/异常传播 |
| 事务事件 | 使用 `@TransactionalEventListener` | 在事务阶段触发（after commit 等） | `SpringCoreEventsTransactionalEventLabTest` | 事务状态/触发时机 |

## 运行命令

- `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

分支来自：listener 是否匹配（condition/payload/type）、multicaster 是否有 executor、事务事件是否在事务内发布。

下一章见：[01：事件心智模型：publish/subscribe 的真实语义](event-basics-event-mental-model.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreEventsBasicsBranchMatrixLabTest` / `SpringCoreEventsAsyncTransactionalBranchMatrixLabTest`
- Lab：`SpringCoreEventsListenerFilteringLabTest` / `SpringCoreEventsAsyncMulticasterLabTest` / `SpringCoreEventsTransactionalEventLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[event-basics-event-mental-model.md](event-basics-event-mental-model.md)

<!-- BOOKIFY:END -->

