# 第 57 章：04. 传播行为（Propagation）：`REQUIRED` vs `REQUIRES_NEW` 到底差在哪？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：传播行为（Propagation）：`REQUIRED` vs `REQUIRES_NEW` 到底差在哪？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 56 章：03. 回滚规则：为什么 checked exception 默认不回滚？](056-03-rollback-rules.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 58 章：05. 程序化事务：为什么 `TransactionTemplate` 在学习阶段很有价值？](../part-02-template-and-debugging/058-05-transaction-template.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. 传播行为（Propagation）：`REQUIRED` vs `REQUIRES_NEW` 到底差在哪？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest`

## 机制主线

传播行为解决的问题是：**当一个事务方法调用另一个事务方法时，事务如何“衔接”？**

## 两个最常用的传播行为

### `REQUIRED`（默认）

- 如果当前已有事务：加入当前事务
- 如果当前没有事务：新开一个事务

直觉：**同生共死**（大多数业务场景默认就是它）

### `REQUIRES_NEW`

- 无论当前是否有事务：都新开一个事务
- 外层事务会被挂起，内层事务独立提交/回滚

直觉：**我先把内层这段“单独记账”**

- `requiresNewCanCommitEvenIfOuterTransactionRollsBack`
  - 外层最后抛异常回滚，但 `REQUIRES_NEW` 内层仍然提交
- `requiresNewRollbackDoesNotNecessarilyRollbackOuter_whenCaught`
  - 内层在新事务里失败回滚，但外层 catch 住异常后仍可提交

关键观察点：

## 三个常见的“进阶传播行为”（用来固定边界，不是日常默认）

> 这些传播行为的价值往往不在“更强大”，而在“**把边界写死，避免误用**”。

### `MANDATORY`：必须存在外层事务，否则直接失败

- 语义：调用方必须已经在事务中，否则抛 `IllegalTransactionStateException`
- 适用：你希望强制某段逻辑只能在事务内执行（例如必须和上游同生共死）
- 对照用例：`SpringCoreTxPropagationMatrixLabTest#mandatoryThrowsWhenNoExistingTransaction`

### `NEVER`：必须不存在事务，否则直接失败

- 语义：如果当前已有事务，直接抛 `IllegalTransactionStateException`
- 适用：你希望强制某段逻辑只能在“非事务”环境执行（例如明确不允许在事务里做某些外部交互）
- 对照用例：`SpringCoreTxPropagationMatrixLabTest#neverThrowsWhenTransactionExists`

### `NESTED`：在同一个物理事务里创建 savepoint（内层回滚不必然影响外层）

- 语义：外层事务存在时，内层会创建 savepoint；内层失败可以回滚到 savepoint
- 常见误区：把 `NESTED` 当作 `REQUIRES_NEW`（它们不是一回事）
- 约束：需要底层事务管理器支持 savepoint（典型：JDBC `DataSourceTransactionManager`）
- 对照用例：`SpringCoreTxPropagationMatrixLabTest#nestedRollsBackOnlyInnerWhenOuterCatchesException`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest`
- 建议命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `SpringCoreTxLabTest` 的两个用例：

## 常见坑与边界

- `REQUIRES_NEW` 不是“魔法保命符”，它只是把事务边界拆开了
- 外层是否回滚，依然取决于外层的异常传播/rollback-only 标记

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest`

上一章：[03-rollback-rules](056-03-rollback-rules.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05-transaction-template](../part-02-template-and-debugging/058-05-transaction-template.md)

<!-- BOOKIFY:END -->
