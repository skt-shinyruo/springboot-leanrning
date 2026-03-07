# 02. Debug / 观察：如何判断“当前是否真的有事务”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Debug / 观察：如何判断“当前是否真的有事务”？展开，主线可以概括为：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。

    先运行 `SpringCoreTxLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。

    需要下探源码时，可以从 `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 程序化事务：为什么 `TransactionTemplate` 在学习阶段很有价值？](template-and-debugging-transaction-template.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑清单（建议反复对照）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreTxLabTest`

## 机制主线

事务相关的学习痛点通常是：以为有事务，但其实没有；或者以为回滚了，但其实提交了。

这一章给出几个“可落地的观察手段”。

## 1) 直接问 Spring：当前是否有事务？

最简单的判断方式是：

- `TransactionSynchronizationManager.isActualTransactionActive()`

## 2) 看 SQL/数据，而不是只看异常

事务最终影响的是“数据是否落库”：

- 一次插入 + 抛异常，最后查表行数（是否回滚）
- 这是最直观、最不容易误判的方式

## 3) 观察传播行为：用“不同 owner 写入”做标签

本模块的做法值得复用：

- outer 写 `outer`，inner 写 `inner`
- 能直接从表里看到哪段提交了、哪段回滚了

## 4)（可选）打开事务日志

如果需要更细粒度观察，可以在模块的 `application.properties` 中设置日志级别（学习用即可）：

## 最小可运行实验（Lab）

- Lab：`SpringCoreTxLabTest`
- 建议命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

本模块在 `SpringCoreTxLabTest#transactionsAreActiveInsideTransactionalMethods` 已经验证了这一点。

- `logging.level.org.springframework.transaction=DEBUG`
- `logging.level.org.springframework.jdbc.datasource=DEBUG`

## 常见坑与边界

### 坑点 1：看到 `@Transactional` 就以为“肯定有事务”，忽略代理边界与 self-invocation

以为当前方法在事务里，但 `isActualTransactionActive()` 为 false，或异常后数据仍落库

`@Transactional` 依赖代理；自调用/绕开 Spring 管理的 bean 会让拦截器不生效

- 事务在方法内确实活跃：`SpringCoreTxLabTest#transactionsAreActiveInsideTransactionalMethods`
- self-invocation 绕过事务（坑点）：`SpringCoreTxSelfInvocationPitfallLabTest#selfInvocationBypassesTransactional_onInnerMethod`

排障先锁定两条证据链：是否走代理（AopProxy）+ 方法内事务是否活跃（TransactionSynchronizationManager），再讨论传播/回滚细节

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest`

上一章：[05-transaction-template](template-and-debugging-transaction-template.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
