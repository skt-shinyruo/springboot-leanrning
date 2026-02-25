# 02. `@Transactional` 如何生效：它也是 AOP（也是代理）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`@Transactional` 如何生效：它也是 AOP（也是代理）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 事务边界（Transaction Boundary）：你到底在“保护”哪一段代码？](01-transaction-boundary.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 回滚规则：为什么 checked exception 默认不回滚？](03-rollback-rules.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. `@Transactional` 如何生效：它也是 AOP（也是代理）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreTxLabTest`

## 机制主线

`@Transactional` 很多人把它当“事务开关”，但它的实现本质上是 **AOP 拦截器**。

> Spring 在调用目标方法前开启事务，在方法正常返回时提交，在异常传播时回滚。

在本模块的最小实验里，你会看到类似的自证方式：

- `AopUtils.isAopProxy(accountService)` 为 true

这说明：你注入的 `accountService` 并不是纯粹的 `AccountService` 实例，而是一个代理对象。

## `@Transactional` 生效的 3 个前提（最常见）

当你觉得事务“不生效”时，不必急于改传播/回滚规则，先问一个更底层的问题：

> 这次调用有没有走到 `TransactionInterceptor` 这条 AOP 链？

把它拆成三个可验证的小前提（按这个顺序排查，通常最快）：

1) **容器里拿到的是代理对象（Bean 被增强）**
   - 直观验证：`AopUtils.isAopProxy(accountService)` 为 true
   - 本模块断言：`SpringCoreTxLabTest#transactionalBeansAreProxied`

2) **调用入口走代理（不是同类内部 self-invocation）**
   - 反例：在同一个类里 `this.xxx()` / 直接方法调用
   - 结论：入口没走代理，拦截器自然不会触发（Tx/AOP 的同一类坑）

3) **事务属性与事务管理器能被正确解析**
   - 实践建议：优先把 `@Transactional` 标在 `public` 方法上（代理模式下最常见、最少坑；非 public 方法请谨慎验证）
   - 容器里要有可用的 `PlatformTransactionManager`，并且选的是你期望的数据源/事务体系（多数据源时最容易“看起来有事务，其实管错了库”）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreTxLabTest`
- 建议命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证“事务是靠代理实现的”

看测试：`SpringCoreTxLabTest#transactionalBeansAreProxied`

## 常见坑与边界

1. 目标对象必须是 Spring 容器管理的 bean（`@Service` / `@Component` 等）
2. 调用入口必须“走代理”  
   - 同类内部自调用会绕过代理（AOP/Tx 的同一类坑）
3. 目标方法必须能被代理拦截  
   - `final` 方法、`private` 方法等可能导致拦截失效（见 AOP 模块的 [04. final-and-proxy-limits](../../../spring-core-aop/docs/part-01-proxy-fundamentals/04-final-and-proxy-limits.md)）

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：`@Transactional` 的“生效”不是注解本身，而是你有没有真的走到 `TransactionInterceptor`——先确认代理与入口，再谈传播/回滚。
- 回到主线：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest`

上一章：[01-transaction-boundary](01-transaction-boundary.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03-rollback-rules](03-rollback-rules.md)

<!-- BOOKIFY:END -->
