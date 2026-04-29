# spring-core-tx

本模块用“可运行的最小示例 + 可验证的测试实验（实验/练习）”讲透 **Spring 事务管理**。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 docs/。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](docs/appendix-self-check.md)

## 本模块完成后应能解释的内容

- `@Transactional` 的声明式事务（本质是 AOP 拦截器）
- 提交（commit）与回滚（rollback）行为
- 回滚规则：runtime vs checked exception
- 传播行为：`REQUIRES_NEW` 的独立事务边界
- 程序化事务：`TransactionTemplate`

## 前置知识

- 先完成 `spring-core-aop`（知道“事务也是代理”更容易理解）
- 了解 commit/rollback 的基本预期

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

## docs 阅读顺序（从现象到机制）

1. [事务边界：到底在“保护”哪一段代码？](docs/transaction-basics-transaction-boundary.md)
2. [`@Transactional` 如何生效：它也是 AOP（也是代理）](docs/transaction-basics-transactional-proxy.md)
3. [回滚规则：为什么 checked exception 默认不回滚？](docs/transaction-basics-rollback-rules.md)
4. [传播行为：`REQUIRED` vs `REQUIRES_NEW`](docs/transaction-basics-propagation.md)
5. [程序化事务：`TransactionTemplate` 的价值](docs/template-and-debugging-transaction-template.md)
6. [Debug / 观察：如何判断“当前是否真的有事务”？](docs/template-and-debugging-debugging.md)
7. [常见坑清单（排查时对照）](docs/appendix-common-pitfalls.md)

## 实验/练习索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。练习默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 延伸阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java` | commit/rollback、回滚规则、传播、模板事务 | ⭐⭐ | `docs/01` → `docs/05` |
| Lab | `src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxSelfInvocationPitfallLabTest.java` | 自调用绕过事务（最小复现）+ 拆分 Bean 修复对比 | ⭐⭐ | `docs/02`、`docs/90` |
| Exercise | `src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseTest.java` | `REQUIRES_NEW`、自调用陷阱、回滚规则改造等练习 | ⭐⭐–⭐⭐⭐ | `docs/02`、`docs/03`、`docs/90` |

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| commit / rollback 的最小闭环 | [docs/01](docs/transaction-basics-transaction-boundary.md) | `SpringCoreTxLabTest#commitsOnSuccess` / `#rollsBackOnRuntimeException` + `AccountService` | 为什么“抛异常”会导致不落库 |
| `@Transactional` 也是 AOP（代理） | [docs/02](docs/transaction-basics-transactional-proxy.md) | `SpringCoreTxLabTest#transactionalBeansAreProxied` | 事务拦截器在调用链的哪里 |
| 自调用绕过事务（同类内部调用） | [docs/02](docs/transaction-basics-transactional-proxy.md) | `SpringCoreTxSelfInvocationPitfallLabTest#selfInvocationBypassesTransactional_onInnerMethod` | 为什么 `this.inner()` 不走代理、如何做最小规避 |
| checked exception 回滚规则 | [docs/03](docs/transaction-basics-rollback-rules.md) | `SpringCoreTxLabTest#checkedExceptionsDoNotRollbackByDefault` | 为什么默认不回滚、如何用 `rollbackFor` 改 |
| `REQUIRES_NEW` 的独立事务边界 | [docs/04](docs/transaction-basics-propagation.md) | `SpringCoreTxLabTest#requiresNewCanCommitEvenIfOuterTransactionRollsBack` | 外层回滚时内层为何还能提交 |
| 程序化事务与 rollback-only | [docs/05](docs/template-and-debugging-transaction-template.md) | `SpringCoreTxLabTest#transactionTemplateAllowsProgrammaticCommitOrRollback` | `setRollbackOnly()` 的真实效果 |

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

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Core Tx（事务）：边界、传播与回滚

本模块把事务相关问题放回同一条可运行主线：事务边界如何建立、`@Transactional` 代理在何处介入、回滚规则如何判定、传播行为如何影响嵌套调用，以及 `TransactionTemplate` 如何作为显式边界工具用于调试与工程化收敛。

事务类问题在排障时最常见的误判是“以为走了事务，实际上没走代理”；因此本模块优先把“代理主线 + 边界事实”跑通，再进入回滚与传播等分支。

---

### 10 分钟入口：先确认事务边界与代理是否生效
- `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`

运行后应能回答：事务在何处开始/提交/回滚；拦截器链条在哪个入口触发；自调用等场景为何会绕过代理边界。

### 从这里开始（顺读路径）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)
3. [事务拦截器调用链（从 @Transactional 到 commit/rollback）](docs/guide-transaction-interceptor-call-chain.md)

### 顺读主线
- [事务边界](docs/transaction-basics-transaction-boundary.md)
- [@Transactional 代理](docs/transaction-basics-transactional-proxy.md)
- [回滚规则](docs/transaction-basics-rollback-rules.md)
- [传播行为](docs/transaction-basics-propagation.md)
- [TransactionTemplate](docs/template-and-debugging-transaction-template.md)
- [事务调试](docs/template-and-debugging-debugging.md)

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 事务拦截器调用链（源码主线锚点）：[03-transaction-interceptor-call-chain.md](docs/guide-transaction-interceptor-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- Branch Matrix（事务主分支）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
- Branch Matrix（常见坑聚合）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-core-tx -Dtest=*ExerciseSolutionTest test`
- 并发/性能（ThreadLocal 边界证据链）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxThreadLocalBoundaryLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
