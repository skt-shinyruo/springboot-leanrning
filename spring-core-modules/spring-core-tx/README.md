# spring-core-tx

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”讲透 **Spring 事务管理**。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/](../../docs/tx/spring-core-tx/)。

## 你将学到什么

- `@Transactional` 的声明式事务（本质是 AOP 拦截器）
- 提交（commit）与回滚（rollback）行为
- 回滚规则：runtime vs checked exception
- 传播行为：`REQUIRES_NEW` 的独立事务边界
- 程序化事务：`TransactionTemplate`

## 前置知识

- 建议先完成 `spring-core-aop`（知道“事务也是代理”更容易理解）
- 了解 commit/rollback 的基本直觉

## 关键命令

### 运行

```bash
mvn -pl :spring-core-tx spring-boot:run
```

运行后观察控制台输出：

- Service 在事务内执行一段会抛异常的逻辑，然后检查表行数（回滚）
- Service 再执行一次成功事务，然后检查表行数（提交）

### 测试

```bash
mvn -pl :spring-core-tx test
```

## 推荐 docs 阅读顺序（从现象到机制）

1. [事务边界：你到底在“保护”哪一段代码？](../../docs/tx/spring-core-tx/part-01-transaction-basics/01-transaction-boundary.md)
2. [`@Transactional` 如何生效：它也是 AOP（也是代理）](../../docs/tx/spring-core-tx/part-01-transaction-basics/02-transactional-proxy.md)
3. [回滚规则：为什么 checked exception 默认不回滚？](../../docs/tx/spring-core-tx/part-01-transaction-basics/03-rollback-rules.md)
4. [传播行为：`REQUIRED` vs `REQUIRES_NEW`](../../docs/tx/spring-core-tx/part-01-transaction-basics/04-propagation.md)
5. [程序化事务：`TransactionTemplate` 的价值](../../docs/tx/spring-core-tx/part-02-template-and-debugging/05-transaction-template.md)
6. [Debug / 观察：如何判断“当前是否真的有事务”？](../../docs/tx/spring-core-tx/part-02-template-and-debugging/06-debugging.md)
7. [常见坑清单（建议反复对照）](../../docs/tx/spring-core-tx/appendix/90-common-pitfalls.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java` | commit/rollback、回滚规则、传播、模板事务 | ⭐⭐ | `docs/01` → `docs/05` |
| Lab | `src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxSelfInvocationPitfallLabTest.java` | 自调用绕过事务（最小复现）+ 拆分 Bean 修复对比 | ⭐⭐ | `docs/02`、`docs/90` |
| Exercise | `src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseTest.java` | `REQUIRES_NEW`、自调用陷阱、回滚规则改造等练习 | ⭐⭐–⭐⭐⭐ | `docs/02`、`docs/03`、`docs/90` |

## 概念 → 在本模块哪里能“看见”

| 你要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 你应该能解释清楚 |
| --- | --- | --- | --- |
| commit / rollback 的最小闭环 | [docs/01](../../docs/tx/spring-core-tx/part-01-transaction-basics/01-transaction-boundary.md) | `SpringCoreTxLabTest#commitsOnSuccess` / `#rollsBackOnRuntimeException` + `AccountService` | 为什么“抛异常”会导致不落库 |
| `@Transactional` 也是 AOP（代理） | [docs/02](../../docs/tx/spring-core-tx/part-01-transaction-basics/02-transactional-proxy.md) | `SpringCoreTxLabTest#transactionalBeansAreProxied` | 事务拦截器在调用链的哪里 |
| 自调用绕过事务（同类内部调用） | [docs/02](../../docs/tx/spring-core-tx/part-01-transaction-basics/02-transactional-proxy.md) | `SpringCoreTxSelfInvocationPitfallLabTest#selfInvocationBypassesTransactional_onInnerMethod` | 为什么 `this.inner()` 不走代理、如何做最小规避 |
| checked exception 回滚规则 | [docs/03](../../docs/tx/spring-core-tx/part-01-transaction-basics/03-rollback-rules.md) | `SpringCoreTxLabTest#checkedExceptionsDoNotRollbackByDefault` | 为什么默认不回滚、如何用 `rollbackFor` 改 |
| `REQUIRES_NEW` 的独立事务边界 | [docs/04](../../docs/tx/spring-core-tx/part-01-transaction-basics/04-propagation.md) | `SpringCoreTxLabTest#requiresNewCanCommitEvenIfOuterTransactionRollsBack` | 外层回滚时内层为何还能提交 |
| 程序化事务与 rollback-only | [docs/05](../../docs/tx/spring-core-tx/part-02-template-and-debugging/05-transaction-template.md) | `SpringCoreTxLabTest#transactionTemplateAllowsProgrammaticCommitOrRollback` | `setRollbackOnly()` 的真实效果 |

## 常见 Debug 路径

- 先问 Spring：“当前是否真的有事务？” → `TransactionSynchronizationManager.isActualTransactionActive()`
- 不要只看异常，最终以“数据是否落库”来判断 commit/rollback
- 观察传播行为时，用不同标记写入（例如 owner=outer/inner），最不容易误判

## 常见坑

- 自调用绕过代理：同类内部调用不会触发事务拦截
- 异常被 catch 住导致提交：回滚与否取决于异常是否逃逸出边界或是否标记 rollback-only
- checked exception 默认不回滚：需要显式 `rollbackFor`
- `REQUIRES_NEW` 拆边界：内层提交/回滚不直接决定外层

## 参考

- Spring Framework Reference：Transaction Management
