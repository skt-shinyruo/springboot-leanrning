# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：事务分支由“异常类型 + rollback 规则 + propagation + 代理边界”共同决定。

    把事务最常见的分支（rollback rules / propagation / self-invocation pitfall）整理成矩阵表；每一行都能被测试复现并用断点验证。

    对照入口：`SpringCoreTxBranchMatrixLabTest`。需要下探源码时，可以从 `TransactionInterceptor` / `AbstractPlatformTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Spring Tx）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 事务边界（Transaction Boundary）：究竟在“保护”哪一段代码？](transaction-basics-transaction-boundary.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreTxBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：事务分支由“异常类型 + rollback 规则 + propagation + 代理边界”共同决定。需要下探源码时，可以从 `TransactionInterceptor` / `AbstractPlatformTransactionManager` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| 默认 rollback 规则 | 抛出 RuntimeException | 默认回滚 | `SpringCoreTxRollbackRulesLabTest` | commit/rollback 路径 |
| checked exception | 抛出 checked exception | 默认不回滚（提交） | `SpringCoreTxRollbackRulesLabTest` | rollbackOnly=false |
| propagation 行为 | REQUIRED/REQUIRES_NEW 等 | 是否新开/挂起事务符合预期 | `SpringCoreTxPropagationMatrixLabTest` | `isNewTransaction` |
| 自调用坑 | 同 bean 内部调用 `@Transactional` 方法 | 绕过代理导致事务不生效 | `SpringCoreTxPitfallsBranchMatrixLabTest` | 调用栈是否进入 proxy |

## 运行命令

- `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

事务分支由“异常类型 + rollback 规则 + propagation + 代理边界”共同决定。

下一章见：[01：事务边界：什么情况下“算在一个事务里”](transaction-basics-transaction-boundary.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreTxBranchMatrixLabTest` / `SpringCoreTxPitfallsBranchMatrixLabTest`
- Lab：`SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxPropagationMatrixLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[transaction-basics-transaction-boundary.md](transaction-basics-transaction-boundary.md)

<!-- BOOKIFY:END -->

