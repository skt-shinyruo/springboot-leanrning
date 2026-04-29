# 04. 断点地图（Spring Tx）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Spring Tx）展开，主线可以概括为：`@Transactional` → AOP 拦截（TransactionInterceptor）→ 事务管理器创建事务（getTransaction）→ 业务执行 → commit/rollback（取决于异常与规则）。

    先跑 `SpringCoreTxBranchMatrixLabTest` 固化“rollback/propagation/自调用坑”的断言，再用断点把 `@Transactional` 的代理入口、事务创建/提交/回滚分支串起来。

    需要下探源码时，可以从 `org.springframework.transaction.interceptor.TransactionInterceptor` / `org.springframework.transaction.support.AbstractPlatformTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Tx）](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- Tx 排障核心：先确认“是否进入事务代理”，再确认“事务是否创建/加入”，最后确认“异常与规则导致 commit 还是 rollback”。
- 证据链：测试断言（行数/状态）→ 断点（TransactionInterceptor/transaction manager）→ 观察点（rollbackOnly/propagation）。

## 运行入口（先运行）

- Book Matrix：`SpringCoreTxBookMatrixLabTest`
- Branch Matrix：`SpringCoreTxBranchMatrixLabTest`
- Pitfalls Matrix（常见坑）：`SpringCoreTxPitfallsBranchMatrixLabTest`

运行命令：

- `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`

## 入口断点（代理与事务边界）

- `org.springframework.transaction.interceptor.TransactionInterceptor#invoke`
- `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`

## 事务管理器断点（决定性分支）

- `org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction`
- `org.springframework.transaction.support.AbstractPlatformTransactionManager#commit`
- `org.springframework.transaction.support.AbstractPlatformTransactionManager#rollback`

## 观察点

- `TransactionSynchronizationManager.isActualTransactionActive()`：是否真的有事务
- `status.isNewTransaction()`：是否新开事务（传播行为的证据）
- `status.isRollbackOnly()`：是否被标记回滚
- `ex` 的类型（Runtime vs checked）与 rollback 规则（`rollbackFor/noRollbackFor`）

## 常见分支定位（与矩阵表配合）

- “没回滚”：先看异常是否被吞掉；再看异常类型是否触发默认 rollback；最后看是否被 `noRollbackFor` 覆盖。
- “传播行为不符合预期”：断点到 `getTransaction`，观察是否创建新事务、是否挂起旧事务。
- “自调用导致事务不生效”：看调用栈是否进入 proxy（没有进入就不是事务问题，是代理边界问题）。

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

`@Transactional` → AOP 拦截（TransactionInterceptor）→ 事务管理器创建事务（getTransaction）→ 业务执行 → commit/rollback（取决于异常与规则）。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreTxBranchMatrixLabTest` / `SpringCoreTxPitfallsBranchMatrixLabTest`
- Lab：`SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxPropagationMatrixLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

