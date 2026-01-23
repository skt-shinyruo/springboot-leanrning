# 第 128 章：01：Events 调用链（publish → multicaster → listener）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Events 调用链（publish → multicaster → listener）
    - 怎么使用：先跑 `SpringCoreEventsMechanicsLabTest`，把“同步/异步/事务事件时机”固化成断言，再按本文把 publish 到 listener 的调用链串起来。
    - 原理：`publishEvent` 并不神秘：它把事件交给 multicaster；multicaster 决定同步还是异步；事务事件由 TransactionSynchronization 绑定到事务边界。
    - 源码入口：`ApplicationEventPublisher#publishEvent` / `SimpleApplicationEventMulticaster#multicastEvent` / `TransactionalEventListener`
    - 推荐 Lab：`SpringCoreEventsMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 128 章：00. 深挖导读](128-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 128 章：02：断点地图](128-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 最短调用链

1. `ApplicationEventPublisher#publishEvent`
2. `SimpleApplicationEventMulticaster#multicastEvent`
3. 找到匹配的 listeners（`@EventListener`/接口实现）
4. 同步调用或提交到 executor（异步）
5. （事务事件）绑定到 TransactionSynchronization，after-commit 才触发

证据链入口：

- `SpringCoreEventsMechanicsLabTest` / `SpringCoreEventsLabTest`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsMechanicsLabTest`
- Lab：`SpringCoreEventsLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](128-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](128-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
