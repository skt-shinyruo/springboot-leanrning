# 第 131 章：03. condition 与 payload：监听器为什么能“按条件触发”甚至接收普通对象？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：condition 与 payload：监听器为什么能“按条件触发”甚至接收普通对象？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ApplicationEventPublisher` 发布事件，监听器用 `@EventListener` 订阅；需要事务时机用 `@TransactionalEventListener`。
    - 原理：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
    - 源码入口：`org.springframework.context.event.SimpleApplicationEventMulticaster` / `org.springframework.context.event.ApplicationListenerMethodAdapter` / `org.springframework.transaction.support.TransactionSynchronizationManager`
    - 推荐 Lab：`SpringCoreEventsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 130 章：02. 多监听器与顺序：为什么 `@Order` 值得你认真对待？](130-02-multiple-listeners-and-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 132 章：04. 同步与异常传播：为什么监听器抛异常会“炸到发布方”？](132-04-sync-and-exceptions.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. condition 与 payload：监听器为什么能“按条件触发”甚至接收普通对象？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

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

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreEventsLabTest`
- 建议命令：`mvn -pl :spring-core-events test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

验证入口：`SpringCoreEventsLabTest#conditionalEventListenerOnlyRunsWhenConditionMatches`

验证入口：`SpringCoreEventsLabTest#publishingPlainObjectsAlsoWorks_asPayloadEvents`

- condition 让你更容易写“机制实验”（同一个发布动作，用不同输入触发不同监听器）
- payload 让事件机制更轻量：不一定每个动作都要建一个 event class

## 常见坑与边界

### 坑点 1：把复杂业务规则塞进 condition（SpEL），导致可读性差且难排障

- Symptom：监听器“偶尔不触发”，你只能猜 condition 到底在什么时候、用什么上下文求值
- Root Cause：condition 属于轻量过滤机制，复杂规则会让行为与排障成本急剧上升
- Verification：`SpringCoreEventsLabTest#conditionalEventListenerOnlyRunsWhenConditionMatches`
- Fix：condition 保持简单（例如基于字段前缀/flag）；复杂规则放到监听器内部或上游业务逻辑，并用测试锁住触发分支

### 坑点 2：payload 事件类型不匹配，导致监听器根本收不到

- Symptom：你 publish 了一个对象，但监听器方法从未被调用
- Root Cause：payload 匹配依赖“监听器参数类型”与 publish 的对象类型
- Verification：`SpringCoreEventsLabTest#publishingPlainObjectsAlsoWorks_asPayloadEvents`
- Fix：先用最小 payload（如 String）验证类型匹配，再逐步升级为专用 event class（推荐 immutable record）

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：condition/payload 都是在回答同一个问题：**哪些监听器会被触发**——前者是“先过滤再执行”，后者是“参数类型匹配就能接”，用最小输入把分支跑成断言比背规则更可靠。
- 回到主线：publish → `ApplicationEventMulticaster` 分发 → listener 执行（同步/异步）→ 事务事件在 AFTER_COMMIT 等时机触发，异常与顺序决定可见性。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreEventsLabTest`

上一章：[02-multiple-listeners-and-order](130-02-multiple-listeners-and-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04-sync-and-exceptions](132-04-sync-and-exceptions.md)

<!-- BOOKIFY:END -->
