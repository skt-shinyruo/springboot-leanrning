# 01. 常见坑清单（建议反复对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：常见坑清单（建议反复对照）
    - 怎么使用：先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. Debug / 观察：如何判断“当前是否真的有事务”？](../part-02-template-and-debugging/02-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 自测题（Spring Core Tx）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障骨架（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，可以按下面 6 步收敛问题（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
   - Branch Matrix - 事务主分支：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
   - Branch Matrix - 常见坑聚合：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)

- 本章主题：**01. 常见坑清单（建议反复对照）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 本章结束后，应能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 速读路径：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest` / `SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`

## 机制主线

这页不展开完整机制主线；其定位更接近排障备忘录：把常见分支与可复现入口列出来，便于回到 tests 验证。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章主要作为补充说明/索引页使用：推荐直接从模块的 Matrix/Lab 入口进入，再回到这里对照。
- Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest` / `SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`
- 建议命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 事务排障的第一步永远是同一句话：**以为有事务，但事务真的存在吗？**（见上一章 Debug/观察，优先用断言而不是靠日志猜）

## 坑 1：同类自调用导致 `@Transactional` 不生效

- 现象：给 `inner()` 加了 `@Transactional`，但从 `outer()` 调 `inner()` 时事务没生效
- 原因：和 AOP 一样，自调用绕过代理
- Verification：
  - 自调用绕过代理：`SpringCoreTxSelfInvocationPitfallLabTest#selfInvocationBypassesTransactional_onInnerMethod`
  - 拆分 bean 后拦截器恢复：`SpringCoreTxSelfInvocationPitfallLabTest#splittingBeanRestoresTransactional_interceptorIsApplied`
  - （练习）`SpringCoreTxExerciseTest#exercise_selfInvocation`

## 坑 2：异常被 catch 住，结果没有回滚

- 现象：以为“抛过异常”就会回滚，但实际提交了
- 原因：事务是否回滚取决于异常是否逃逸出事务边界，或是否显式标记 rollback-only
- 建议：学习阶段优先用“查表行数”做验证，不要只看异常

## 坑 3：checked exception 默认不回滚

- 对照：见 [03. rollback-rules](../part-01-transaction-basics/03-rollback-rules.md)
- 解决：显式写 `rollbackFor`

## 坑 4：`REQUIRES_NEW` 不是“神奇回滚开关”

- 它只是把事务边界拆成两段：内层提交/回滚不直接决定外层
- 对照：见 [04. propagation](../part-01-transaction-basics/04-propagation.md)

## 坑 5：事务=代理，因此也会受到代理限制

- `final` 方法拦截不到（CGLIB 情况）
- private 方法通常也不会被拦截
- 对照：AOP 模块的 [04. final-and-proxy-limits](../../../spring-core-aop/docs/part-01-proxy-fundamentals/04-final-and-proxy-limits.md)

## 坑 6：`MANDATORY`/`NEVER` 是“边界约束”，不是默认选择

- 现象：一调用就抛 `IllegalTransactionStateException`，以为“事务坏了”
- 原因：这是传播行为的设计语义：用来把边界写死
- 对照：
  - `SpringCoreTxPropagationMatrixLabTest#mandatoryThrowsWhenNoExistingTransaction`
  - `SpringCoreTxPropagationMatrixLabTest#neverThrowsWhenTransactionExists`

## 坑 7：把 `NESTED` 当成 `REQUIRES_NEW`，结果语义误判

- 误判：以为 `NESTED` 会开新事务（其实更接近 savepoint）
- 正确理解：外层事务存在时，`NESTED` 在同一个物理事务里创建 savepoint
- 对照：`SpringCoreTxPropagationMatrixLabTest#nestedRollsBackOnlyInnerWhenOuterCatchesException`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest` / `SpringCoreTxPropagationMatrixLabTest` / `SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxSelfInvocationPitfallLabTest`
- Exercise：`SpringCoreTxExerciseTest`

上一章：[06-debugging](../part-02-template-and-debugging/02-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99-self-check](02-self-check.md)

<!-- BOOKIFY:END -->
