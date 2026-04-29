# 99 自检：Spring Core Tx（事务）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（复盘出口）"

    - 主线入口：`SpringCoreTxBookMatrixLabTest`
    - 分支入口：`SpringCoreTxBranchMatrixLabTest`（主分支）/ `SpringCoreTxPitfallsBranchMatrixLabTest`（坑点聚合）
    - 入口：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（排查时对照）](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- Branch Matrix（事务主分支）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
- Branch Matrix（常见坑聚合）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题

1. 事务是否真的生效，第一步该验证什么？如何证明“走到了代理”？
   - 证据入口：`SpringCoreTxLabTest#transactionalBeansAreProxied`
2. 事务是否真的处于 active 状态？如何证明“在 @Transactional 方法内部是 active”？
   - 证据入口：`SpringCoreTxLabTest#transactionsAreActiveInsideTransactionalMethods`
3. 成功提交与 RuntimeException 回滚的最小对照用例是什么？如何用“行数变化”而不是日志判断？
   - 证据入口：`SpringCoreTxLabTest#commitsOnSuccess` + `SpringCoreTxLabTest#rollsBackOnRuntimeException`
4. CheckedException 默认为什么不回滚？如何把“默认提交 vs rollbackFor 覆盖”跑成事实？
   - 证据入口：`SpringCoreTxLabTest#checkedExceptionsDoNotRollbackByDefault` + `SpringCoreTxLabTest#rollbackForCheckedExceptionsCanBeConfigured`
5. 没有 `@Transactional` 时，为什么“每条语句像是自动提交”？异常为什么无法回滚已写入的数据？
   - 证据入口：`SpringCoreTxLabTest#withoutTransactional_eachStatementIsEffectivelyAutoCommitted`
6. `REQUIRES_NEW` 的核心语义是什么？如何证明“外层回滚但内层仍提交”？
   - 证据入口：`SpringCoreTxLabTest#requiresNewCanCommitEvenIfOuterTransactionRollsBack`
7. 为什么“内层抛异常但外层 catch 住”可能导致外层仍提交？如何把这个边界写成断言？
   - 证据入口：`SpringCoreTxLabTest#requiresNewRollbackDoesNotNecessarilyRollbackOuter_whenCaught`
8. `MANDATORY` 与 `NEVER` 的设计目的是什么？它们各自在什么条件下会直接抛异常？
   - 证据入口：`SpringCoreTxPropagationMatrixLabTest#mandatoryThrowsWhenNoExistingTransaction` + `SpringCoreTxPropagationMatrixLabTest#neverThrowsWhenTransactionExists`
9. self-invocation 为什么会绕过事务？如何用一条可回归用例证明“没走代理→不会回滚”，以及“拆分 bean→恢复拦截器”？
   - 证据入口：`SpringCoreTxSelfInvocationPitfallLabTest#selfInvocationBypassesTransactional_onInnerMethod` + `SpringCoreTxSelfInvocationPitfallLabTest#splittingBeanRestoresTransactional_interceptorIsApplied`
10. 事务上下文能否跨线程传播？如何用并发实验把“ThreadLocal 边界”固定成结论？
    - 证据入口：`SpringCoreTxThreadLocalBoundaryLabTest#transactionContext_doesNotCrossThreadBoundary`
11. 什么时候应该用 `TransactionTemplate`？如何验证“手动标记 rollbackOnly 会回滚”？
    - 证据入口：`SpringCoreTxLabTest#transactionTemplateAllowsProgrammaticCommitOrRollback`

## 退出条件（完成标准）

- 能用“代理是否参与 + 数据是否落库”两条证据链判断事务问题（而不是只看异常/日志）。
- 能把 propagation/rollback/self-invocation 这些高频坑写成最小对照用例，作为长期回归入口。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest` / `SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`

上一章：[90-common-pitfalls](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)

<!-- BOOKIFY:END -->
