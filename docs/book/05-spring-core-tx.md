# 05 Spring Core Tx：事务边界、传播、回滚与常见坑

## 学习目标

- 能解释 `@Transactional` 的核心事实：它是通过代理拦截实现的边界控制，而不是“语法糖”。
- 能区分三类常见问题：事务没开 / 事务开了但回滚规则不符合预期 / 传播行为导致边界被拆分。
- 能读懂事务拦截器的调用链，并用断点验证 commit/rollback 的真实发生点。

## 概念框架

- **事务边界**：在什么入口开启/提交/回滚（常由拦截器在方法调用前后织入）。
- **回滚规则**：异常类型、`rollbackFor/noRollbackFor`、以及“异常是否被吞掉”共同决定结果。
- **传播行为（Propagation）**：嵌套调用时边界如何组合（加入/新开/挂起等）。
- **实现形态**：
  - `@Transactional` → `TransactionInterceptor` → `PlatformTransactionManager` → commit/rollback。
- **与 AOP 的关系**：
  - 事务是 AOP 的一个具体应用；排障先用代理心智模型再看注解参数（参见 [04 AOP](04-spring-core-aop.md)）。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
  - 测试类：[`SpringCoreTxBookMatrixLabTest.java`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-core-tx/README.md`](../../spring-core-modules/spring-core-tx/README.md)
- 导航型文档（用于快速定位 commit/rollback 发生点）：
  - 事务拦截器调用链：[`part-00-guide/03-transaction-interceptor-call-chain.md`](../../spring-core-modules/spring-core-tx/docs/guide-transaction-interceptor-call-chain.md)
  - 常见坑：[`appendix/01-common-pitfalls.md`](../../spring-core-modules/spring-core-tx/docs/appendix-common-pitfalls.md)

## 常见误区

- 以为 `@Transactional` 作用于 private 方法也有效。代理通常无法拦截 private 方法调用（边界进不去）。
- 以为“抛异常就一定回滚”。异常类型与回滚规则、以及异常是否被捕获/吞掉同样关键。
- 以为传播行为是“性能优化选项”。传播行为是边界语义的一部分，选错会直接改变一致性与可见性。
- 只在数据库层面观察结果，不回到事务拦截器链路验证边界。建议先证明“事务是否真的开启”。

## 练习

- 练习 1（把事务边界跑成事实）：
  - 运行 `SpringCoreTxBookMatrixLabTest`；
  - 选择一个“回滚不符合预期”的场景；
  - 用断点验证：事务在哪个方法入口开启、在哪个 catch/throw 分支走向 commit/rollback。
- 练习 2（传播行为复盘）：
  - 从模块文档的“传播行为”章节挑 2 个传播模式；
  - 为每个模式写一句“边界如何组合”的规则，并在对应 Lab 中验证。

## 小结

- 事务问题优先分型：是否经过代理 → 是否开启事务 → 是否满足回滚规则 → 传播行为是否拆分边界。
- 数据访问与测试策略会放大/隐藏事务现象：后续在 [09 Data JPA](09-spring-boot-data-jpa.md) 与 [08 Testing](08-spring-boot-testing.md) 会反复使用这些判断。

## 延伸阅读

- 数据访问（与事务强耦合）：[`09-spring-boot-data-jpa.md`](09-spring-boot-data-jpa.md)
- 测试策略（如何在不同 slice 中验证事务现象）：[`08-spring-boot-testing.md`](08-spring-boot-testing.md)
- 异步与事务边界（跨线程时语义变化）：[`11-spring-boot-async-scheduling.md`](11-spring-boot-async-scheduling.md)

---

[← 上一章](04-spring-core-aop.md) | [目录](README.md) | [下一章 →](06-spring-boot-web-mvc.md)

