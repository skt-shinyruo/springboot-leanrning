# 第 53 章：02：断点地图（Spring Tx Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Spring Tx Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxRollbackRulesLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 53 章：00 - Deep Dive Guide（spring-core-tx）](053-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 53 章：04：关键分支矩阵（Branch Decision Matrix）](053-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Spring Tx Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
- 回到主线：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- Tx 排障核心：先确认“是否进入事务代理”，再确认“事务是否创建/加入”，最后确认“异常与规则导致 commit 还是 rollback”。
- 推荐证据链：测试断言（行数/状态）→ 断点（TransactionInterceptor/transaction manager）→ Watchpoints（rollbackOnly/propagation）。

## 运行入口（建议先跑）

- Book Matrix：`SpringCoreTxBookMatrixLabTest`
- Branch Matrix：`SpringCoreTxBranchMatrixLabTest`
- Pitfalls Matrix（常见坑）：`SpringCoreTxPitfallsBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`

## 入口断点（代理与事务边界）

- `org.springframework.transaction.interceptor.TransactionInterceptor#invoke`
- `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`

## 事务管理器断点（决定性分支）

- `org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction`
- `org.springframework.transaction.support.AbstractPlatformTransactionManager#commit`
- `org.springframework.transaction.support.AbstractPlatformTransactionManager#rollback`

## Watchpoints（建议）

- `TransactionSynchronizationManager.isActualTransactionActive()`：是否真的有事务
- `status.isNewTransaction()`：是否新开事务（传播行为的证据）
- `status.isRollbackOnly()`：是否被标记回滚
- `ex` 的类型（Runtime vs checked）与 rollback 规则（`rollbackFor/noRollbackFor`）

## 常见分支定位（与矩阵表配合）

- “没回滚”：先看异常是否被吞掉；再看异常类型是否触发默认 rollback；最后看是否被 `noRollbackFor` 覆盖。
- “传播行为不符合预期”：断点到 `getTransaction`，观察是否创建新事务、是否挂起旧事务。
- “自调用导致事务不生效”：看调用栈是否进入 proxy（没有进入就不是事务问题，是代理边界问题）。

## 排障入口（Playbook）

- 常见坑：[`../appendix/060-90-common-pitfalls.md`](../appendix/060-90-common-pitfalls.md)
- 自检：[`../appendix/061-99-self-check.md`](../appendix/061-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxBookMatrixLabTest` / `SpringCoreTxBranchMatrixLabTest` / `SpringCoreTxPitfallsBranchMatrixLabTest`

上一章：[事务调试](../part-02-template-and-debugging/059-06-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[053-01-transaction-interceptor-call-chain.md](053-01-transaction-interceptor-call-chain.md)

<!-- BOOKIFY:END -->
