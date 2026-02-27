# 02. 深挖指南（Spring Core Tx）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖指南（Spring Core Tx）
    - 怎么使用：先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Core Tx（事务）](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 事务边界（Transaction Boundary）：究竟在“保护”哪一段代码？](../part-01-transaction-basics/01-transaction-boundary.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章是「深挖指南（Spring Core Tx）」的深挖导读：说明如何阅读、如何验证、以及遇到分支时从哪里下断点更省时间。
建议先运行 `SpringCoreTxLabTest` 获得可复现现象，再带着断言/观察点回到正文对照机制。

!!! summary "本章要点"

    - 本章结束后，应能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 速读路径：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreTxLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`

## 机制主线

事务的“深挖主线”可以浓缩为三句话：

1. **事务边界一定发生在“方法调用链”上**：没有代理参与，就没有事务拦截器
2. **回滚与否不是“感觉”**：由异常类型 + rollback 规则 + 传播行为共同决定
3. **排障优先看证据链**：是否真的有事务、是否真的走代理、是否真的 commit/rollback

### 1) 时间线：一次 `@Transactional` 方法调用发生了什么

1. 调用进入 AOP 代理
2. `TransactionInterceptor` 决定是否开启事务（根据传播行为/是否已有事务）
3. 执行业务方法
4. 方法正常返回：提交事务（commit）
5. 方法抛异常：按 rollback 规则决定回滚（rollback）还是提交

### 2) 关键参与者

- `@Transactional`：声明事务语义（传播、隔离、只读、rollbackFor 等）
- `TransactionInterceptor`：事务拦截器（代理入口，决定开/提交/回滚）
- `PlatformTransactionManager`：事务管理器（真正执行 begin/commit/rollback）
- `TransactionSynchronizationManager`：判断“当前线程是否有事务”的观测点
- `TransactionTemplate`：编程式事务（把“边界 + 回滚”写成可断言代码）

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **默认回滚：RuntimeException 会回滚**
   - 验证：`SpringCoreTxLabTest#rollsBackOnRuntimeException`
2. **默认不回滚：checked exception 不回滚（除非配置 rollbackFor）**
   - 验证：`SpringCoreTxLabTest#checkedExceptionsDoNotRollbackByDefault` / `SpringCoreTxLabTest#rollbackForCheckedExceptionsCanBeConfigured`
3. **传播行为：REQUIRES_NEW 可以独立提交，即使外层回滚**
   - 验证：`SpringCoreTxLabTest#requiresNewCanCommitEvenIfOuterTransactionRollsBack`
4. **编程式事务：TransactionTemplate 可显式 rollbackOnly**
   - 验证：`SpringCoreTxLabTest#transactionTemplateAllowsProgrammaticCommitOrRollback`
5. **代理边界（坑点）：self-invocation 绕过事务拦截器**
   - 验证：`SpringCoreTxSelfInvocationPitfallLabTest#selfInvocationBypassesTransactional_onInnerMethod`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

建议断点（排障最省时间的 4 个点）：

- 代理入口：确认是否真的走到了事务拦截器
  - `org.springframework.transaction.interceptor.TransactionInterceptor#invoke`
- 事务活跃判断：在业务方法内确认当前线程是否有事务
  - `TransactionSynchronizationManager.isActualTransactionActive()`
- 回滚分流：异常抛出后到底走 commit 还是 rollback
  - `org.springframework.transaction.support.AbstractPlatformTransactionManager#processCommit`
  - `org.springframework.transaction.support.AbstractPlatformTransactionManager#processRollback`

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreTxLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`
- 建议命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

> 验证入口（可跑）：
> - `SpringCoreTxLabTest`
> - `SpringCoreTxSelfInvocationPitfallLabTest`

配套验证入口：
- Labs/Exercises：见 `src/test/java/com/learning/springboot/springcoretx/**`

## 常见坑与边界

建议阅读顺序：
1. 先把“事务边界”想清楚：哪里开、哪里关（Part 01）
2. 再把“代理机制”想清楚：为什么 self-invocation 会绕过事务（Part 01 + Appendix）
3. 最后进入回滚规则、传播与编程式事务（Part 01 + Part 02）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01-transaction-boundary](../part-01-transaction-basics/01-transaction-boundary.md)

<!-- BOOKIFY:END -->
