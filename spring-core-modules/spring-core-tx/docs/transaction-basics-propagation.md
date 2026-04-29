# 04. 传播行为（Propagation）：`REQUIRED` vs `REQUIRES_NEW` 到底差在哪？
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕传播行为（Propagation）：`REQUIRED` vs `REQUIRES_NEW` 到底差在哪？展开，主线可以概括为：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。

    先运行 `SpringCoreTxLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。

    需要下探源码时，可以从 `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 回滚规则：为什么 checked exception 默认不回滚？](transaction-basics-rollback-rules.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 程序化事务：为什么 `TransactionTemplate` 在学习阶段很有价值？](template-and-debugging-transaction-template.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「04. 传播行为（Propagation）：`REQUIRED` vs `REQUIRES_NEW` 到底差在哪？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreTxLabTest`（或文末“对应实验/测试”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest`

## 机制主线

传播行为解决的问题是：**当一个事务方法调用另一个事务方法时，事务如何“衔接”？**

## 两个最常用的传播行为

### `REQUIRED`（默认）

- 如果当前已有事务：加入当前事务
- 如果当前没有事务：新开一个事务

预期：**同生共死**（大多数业务场景默认就是它）

### `REQUIRES_NEW`

- 无论当前是否有事务：都新开一个事务
- 外层事务会被挂起，内层事务独立提交/回滚

预期：**内层这段应“单独记账”**

- `requiresNewCanCommitEvenIfOuterTransactionRollsBack`
  - 外层最后抛异常回滚，但 `REQUIRES_NEW` 内层仍然提交
- `requiresNewRollbackDoesNotNecessarilyRollbackOuter_whenCaught`
  - 内层在新事务里失败回滚，但外层 catch 住异常后仍可提交

关键观察点：

- 外层事务是否被挂起。
- 内层提交/回滚是否独立于外层最终结果。

## 三个常见的“进阶传播行为”（用来固定边界，不是日常默认）

> 这些传播行为的价值往往不在“更强大”，而在“**把边界写死，避免误用**”。

### `MANDATORY`：必须存在外层事务，否则直接失败

- 语义：调用方必须已经在事务中，否则抛 `IllegalTransactionStateException`
- 适用：需要强制某段逻辑只能在事务内执行（例如必须和上游同生共死）
- 对照用例：`SpringCoreTxPropagationMatrixLabTest#mandatoryThrowsWhenNoExistingTransaction`

### `NEVER`：必须不存在事务，否则直接失败

- 语义：如果当前已有事务，直接抛 `IllegalTransactionStateException`
- 适用：需要强制某段逻辑只能在“非事务”环境执行（例如明确不允许在事务里做某些外部交互）
- 对照用例：`SpringCoreTxPropagationMatrixLabTest#neverThrowsWhenTransactionExists`

### `NESTED`：在同一个物理事务里创建 savepoint（内层回滚不必然影响外层）

- 语义：外层事务存在时，内层会创建 savepoint；内层失败可以回滚到 savepoint
- 常见误区：把 `NESTED` 当作 `REQUIRES_NEW`（它们不是一回事）
- 约束：需要底层事务管理器支持 savepoint（典型：JDBC `DataSourceTransactionManager`）
- 对照用例：`SpringCoreTxPropagationMatrixLabTest#nestedRollsBackOnlyInnerWhenOuterCatchesException`

## 最小可运行实验（Lab）

- Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest`
- 运行命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 在本模块如何验证

重点对照 `SpringCoreTxLabTest` 中两个 `REQUIRES_NEW` 场景：外层回滚不影响已提交的内层事务；内层回滚被捕获后也不必然导致外层回滚。

## 常见坑与边界

- `REQUIRES_NEW` 不是“隐式机制保命符”，它只是把事务边界拆开了
- 外层是否回滚，依然取决于外层的异常传播/rollback-only 标记

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest`

上一章：[03-rollback-rules](transaction-basics-rollback-rules.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05-transaction-template](template-and-debugging-transaction-template.md)

<!-- BOOKIFY:END -->
