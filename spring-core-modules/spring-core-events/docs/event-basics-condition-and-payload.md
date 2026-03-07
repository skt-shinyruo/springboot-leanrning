# 03. condition 与 payload：监听器为什么能“按条件触发”甚至接收普通对象？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕condition 与 payload：监听器为什么能“按条件触发”甚至接收普通对象？展开，主线可以概括为：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。

    先运行 `SpringCoreEventsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。

    需要下探源码时，可以从 `org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 多监听器与顺序：为什么 `@Order` 值得认真对待？](event-basics-multiple-listeners-and-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 同步与异常传播：为什么监听器抛异常会“炸到发布方”？](event-basics-sync-and-exceptions.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreEventsLabTest`

## 机制主线

Spring 事件有两个很实用的能力：

1) **按条件触发**：只在满足某些条件时才执行监听器
2) **payload 事件**：发布普通对象，也可以被 `@EventListener` 接住

## 1) 条件触发：`@EventListener(condition = "...")`

本模块里条件是：

- `#event.username().startsWith('A')`

因此：

- `Bob` 不触发
- `Alice` 触发

学习建议：

- 条件尽量保持简单可读（学习阶段尤其重要）
- 把“条件”当作一种轻量的过滤器，而不是把复杂业务规则塞进 SpEL

## 2) payload：发布 String 也能被监听器接到

核心规则很简单：

> 监听方法参数的类型，与 publish 的对象类型匹配即可。

本模块里：

- publish：`eventPublisher.publishEvent("hello")`
- listen：`public void on(String payload)`

## 最小可运行实验（Lab）

- Lab：`SpringCoreEventsLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

验证入口：`SpringCoreEventsLabTest#conditionalEventListenerOnlyRunsWhenConditionMatches`

验证入口：`SpringCoreEventsLabTest#publishingPlainObjectsAlsoWorks_asPayloadEvents`

- condition 更容易写“机制实验”（同一个发布动作，用不同输入触发不同监听器）
- payload 让事件机制更轻量：不一定每个动作都要建一个 event class

## 常见坑与边界

### 坑点 1：把复杂业务规则塞进 condition（SpEL），导致可读性差且难排障

监听器“偶尔不触发”，只能猜 condition 到底在什么时候、用什么上下文求值

condition 属于轻量过滤机制，复杂规则会让行为与排障成本急剧上升

`SpringCoreEventsLabTest#conditionalEventListenerOnlyRunsWhenConditionMatches`

condition 保持简单（例如基于字段前缀/flag）；复杂规则放到监听器内部或上游业务逻辑，并用测试锁定触发分支

### 坑点 2：payload 事件类型不匹配，导致监听器根本收不到

publish 了一个对象，但监听器方法从未被调用

payload 匹配依赖“监听器参数类型”与 publish 的对象类型

`SpringCoreEventsLabTest#publishingPlainObjectsAlsoWorks_asPayloadEvents`

先用最小 payload（如 String）验证类型匹配，再逐步升级为专用 event class（推荐 immutable record）

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：condition/payload 都是在回答同一个问题：**哪些监听器会被触发**——前者是“先过滤再执行”，后者是“参数类型匹配就能接”，用最小输入把分支跑成断言比背规则更可靠。
- 回到主线：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsLabTest`

上一章：[02-multiple-listeners-and-order](event-basics-multiple-listeners-and-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04-sync-and-exceptions](event-basics-sync-and-exceptions.md)

<!-- BOOKIFY:END -->
