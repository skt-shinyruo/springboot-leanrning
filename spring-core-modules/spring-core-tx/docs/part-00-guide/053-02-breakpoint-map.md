# 第 53 章：02：断点地图（Spring Tx Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Spring Tx Debugger Pack）
    - 怎么使用：先跑 `SpringCoreTxBranchMatrixLabTest` 固化“rollback/propagation/自调用坑”的断言，再用断点把 `@Transactional` 的代理入口、事务创建/提交/回滚分支串起来。
    - 原理：`@Transactional` → AOP 拦截（TransactionInterceptor）→ 事务管理器创建事务（getTransaction）→ 业务执行 → commit/rollback（取决于异常与规则）。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor` / `org.springframework.transaction.support.AbstractPlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 53 章：00 - Deep Dive Guide（spring-core-tx）](053-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 53 章：04：关键分支矩阵（Branch Decision Matrix）](053-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

- Matrix：`SpringCoreTxBranchMatrixLabTest` / `SpringCoreTxPitfallsBranchMatrixLabTest`
- Lab：`SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxPropagationMatrixLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](053-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](053-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

