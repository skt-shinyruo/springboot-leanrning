# 01. 事务边界（Transaction Boundary）：究竟在“保护”哪一段代码？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕事务边界（Transaction Boundary）：究竟在“保护”哪一段代码？展开，主线可以概括为：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。

    先运行 `SpringCoreTxLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。

    需要下探源码时，可以从 `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Tx）](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. `@Transactional` 如何生效：它也是 AOP（也是代理）](02-transactional-proxy.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01. 事务边界（Transaction Boundary）：究竟在“保护”哪一段代码？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreTxLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreTxLabTest`

## 机制主线

事务边界描述的是一段“要么全部成功、要么全部失败”的原子单元。Spring 的声明式事务把这个边界落在**方法调用**上：一次方法调用进入代理，事务拦截器在调用前决定是否开启事务，在调用后决定提交或回滚。

从调用链的角度，可以把主线压缩成三步：

1. **进入边界**：外部调用进入 `@Transactional` 方法对应的 proxy，`TransactionInterceptor` 介入。
2. **绑定资源**：`PlatformTransactionManager` 获取/创建事务，并把连接等资源绑定到当前线程（ThreadLocal 语义）。
3. **退出边界**：方法正常返回则提交；抛出异常则按规则回滚（规则与传播在后续章节展开）。

这也是事务排障的“第一原则”：先把边界与入口说清楚，再讨论传播与回滚。

## 在本模块的最小闭环

本章的最小闭环围绕 `AccountService` 的两条路径展开：

- `createTwoAccounts()`：正常路径，最终提交
- `createTwoAccountsThenFail()`：异常路径，默认回滚（RuntimeException 逃逸出边界）

对应验证入口（可跑）：

- `SpringCoreTxLabTest#commitsOnSuccess`
- `SpringCoreTxLabTest#rollsBackOnRuntimeException`

建议用“数据事实”而不是日志来判断边界是否成立：以行数变化为断言，用例会更稳定。

## 需要记住的 3 件事

1. **边界依赖调用入口**：声明式事务依赖代理；绕过容器或同类自调用会绕过拦截器（后续章节与常见坑会展开）。
2. **边界不是语法糖**：事务的创建/提交/回滚是显式动作，发生在方法前后；异常是否回滚取决于“是否跨出边界”与回滚规则。
3. **边界只保护参与的资源**：数据库事务能保证同一事务资源内的一致性，但不会自动补偿外部系统副作用（消息、HTTP 调用、文件写入等）。

## 最小可运行实验（Lab）

- Lab：`SpringCoreTxLabTest`
- 建议命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

- `createTwoAccounts()`：成功 → commit → 表里有两行
- `createTwoAccountsThenFail()`：抛运行时异常 → rollback → 表里没有行

- `SpringCoreTxLabTest#commitsOnSuccess`
- `SpringCoreTxLabTest#rollsBackOnRuntimeException`

补充说明：回滚与否取决于异常是否“逃逸出边界”。

- 抛出运行时异常并向外传播：默认回滚
- 异常在边界内被 catch 并吞掉：默认会提交（除非显式标记 rollback-only，见 [01. 常见坑清单](../appendix/01-common-pitfalls.md)）

## 常见坑与边界

事务学习最重要的是先把概念落地：**事务边界** 是业务侧划定的“原子单元”。

> 事务边界 = “从哪里开始”到“哪里结束”这一段逻辑，要么全部成功提交，要么全部失败回滚。

1. **事务边界通常是“方法级别”的**
   - 声明式事务（`@Transactional`）默认就是围绕方法的 begin/commit/rollback

3. **事务边界不是“语法糖”，它依赖代理机制**
   - `@Transactional` 的生效条件见 [02. transactional-proxy](02-transactional-proxy.md)

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest`

上一章：[00-deep-dive-guide](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02-transactional-proxy](02-transactional-proxy.md)

<!-- BOOKIFY:END -->
