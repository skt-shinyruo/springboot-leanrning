# 03. Events 调用链（publish → multicaster → listener）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Events 调用链（publish → multicaster → listener）
    - 怎么使用：先跑 `SpringCoreEventsMechanicsLabTest`，把“同步/异步/事务事件时机”固化成断言，再按本文把 publish 到 listener 的调用链串起来。
    - 原理：`publishEvent` 并不神秘：它把事件交给 multicaster；multicaster 决定同步还是异步；事务事件由 TransactionSynchronization 绑定到事务边界。
    - 源码入口：`ApplicationEventPublisher#publishEvent` / `SimpleApplicationEventMulticaster#multicastEvent` / `TransactionalEventListener`
    - 推荐 Lab：`SpringCoreEventsMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Events）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Spring Events Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. Events 调用链（publish → multicaster → listener）**
- 建议入口：优先运行 `SpringCoreEventsMechanicsLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：`publishEvent` 并不神秘：它把事件交给 multicaster；multicaster 决定同步还是异步；事务事件由 TransactionSynchronization 绑定到事务边界。
- 源码入口：`ApplicationEventPublisher#publishEvent` / `SimpleApplicationEventMulticaster#multicastEvent` / `TransactionalEventListener`



## 最短调用链

1. `ApplicationEventPublisher#publishEvent`
2. `SimpleApplicationEventMulticaster#multicastEvent`
3. 找到匹配的 listeners（`@EventListener`/接口实现）
4. 同步调用或提交到 executor（异步）
5. （事务事件）绑定到 TransactionSynchronization，after-commit 才触发

证据链入口：

- `SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsLabTest`

## 小结与下一章

- 小结：`publishEvent` 并不神秘：它把事件交给 multicaster；multicaster 决定同步还是异步；事务事件由 TransactionSynchronization 绑定到事务边界。
- 下一章：[第 128 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsMechanicsLabTest`
- Lab：`SpringCoreEventsLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
