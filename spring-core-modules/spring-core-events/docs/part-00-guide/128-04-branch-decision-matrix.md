# 第 128 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把事件系统的关键分支（listener 匹配/同步异步/事务事件）整理成矩阵表；每行都对应一个可跑入口与观察点。
    - 原理：分支来自：listener 是否匹配（condition/payload/type）、multicaster 是否有 executor、事务事件是否在事务内发布。
    - 源码入口：`AbstractApplicationEventMulticaster#multicastEvent`
    - 推荐 Lab：`SpringCoreEventsBasicsBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 128 章：02：断点地图（Spring Events Debugger Pack）](128-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 129 章：01：事件心智模型：publish/subscribe 的真实语义](../part-01-event-basics/129-01-event-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| listener filtering | condition/payload/type | 只有匹配的 listener 被调用 | `SpringCoreEventsListenerFilteringLabTest` | listeners 列表/调用次数 |
| 同步分发 | 默认 multicaster | 发布者线程执行 listener | `SpringCoreEventsMechanicsLabTest` | 线程名/调用顺序 |
| 异步分发 | multicaster 配置 executor | listener 在异步线程执行 | `SpringCoreEventsAsyncMulticasterLabTest` | 线程名/异常传播 |
| 事务事件 | 使用 `@TransactionalEventListener` | 在事务阶段触发（after commit 等） | `SpringCoreEventsTransactionalEventLabTest` | 事务状态/触发时机 |

## 推荐运行命令

- `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/136-90-common-pitfalls.md`](../appendix/136-90-common-pitfalls.md)
- 自检：[`../appendix/137-99-self-check.md`](../appendix/137-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreEventsBasicsBranchMatrixLabTest` / `SpringCoreEventsAsyncTransactionalBranchMatrixLabTest`
- Lab：`SpringCoreEventsListenerFilteringLabTest` / `SpringCoreEventsAsyncMulticasterLabTest` / `SpringCoreEventsTransactionalEventLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](128-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-event-basics/01-event-mental-model.md](../part-01-event-basics/129-01-event-mental-model.md)

<!-- BOOKIFY:END -->

