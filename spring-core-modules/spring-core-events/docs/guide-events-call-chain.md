# 03. Events 调用链（publish → multicaster → listener）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：Events 调用链（publish → multicaster → listener）展开，主线可以概括为：`publishEvent` 并不神秘：它把事件交给 multicaster；multicaster 决定同步还是异步；事务事件由 TransactionSynchronization 绑定到事务边界。

    先跑 `SpringCoreEventsMechanicsLabTest`，把“同步/异步/事务事件时机”固化成断言，再按本章把 publish 到 listener 的调用链串起来。

    需要下探源码时，可以从 `ApplicationEventPublisher#publishEvent` / `SimpleApplicationEventMulticaster#multicastEvent` / `TransactionalEventListener` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Events）](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Spring Events）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreEventsMechanicsLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：`publishEvent` 并不神秘：它把事件交给 multicaster；multicaster 决定同步还是异步；事务事件由 TransactionSynchronization 绑定到事务边界。需要下探源码时，可以从 `ApplicationEventPublisher#publishEvent` / `SimpleApplicationEventMulticaster#multicastEvent` / `TransactionalEventListener` 这些入口切入。


## 最短调用链

1. `ApplicationEventPublisher#publishEvent`
2. `SimpleApplicationEventMulticaster#multicastEvent`
3. 找到匹配的 listeners（`@EventListener`/接口实现）
4. 同步调用或提交到 executor（异步）
5. （事务事件）绑定到 TransactionSynchronization，after-commit 才触发

证据链入口：

- `SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsLabTest`

## 小结与下一章

`publishEvent` 并不神秘：它把事件交给 multicaster；multicaster 决定同步还是异步；事务事件由 TransactionSynchronization 绑定到事务边界。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreEventsMechanicsLabTest`
- Lab：`SpringCoreEventsLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
