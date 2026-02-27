# Spring Boot Data JPA：Persistence Context、flush 与查询边界

本模块以 JPA 的核心事实为中心组织内容：实体状态如何变化、持久化上下文如何影响可见性、`flush` 与脏检查在哪些时机发生，以及 fetching 策略如何演变成 N+1。很多行为只有在事务边界内才有意义，因此本模块与事务模块（`spring-core-tx`）是天然的串联关系。

---

## 10 分钟入口：固定持久化上下文的“可见性事实”

- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`

运行后应能回答：在同一事务/同一持久化上下文中，查询结果为何会“看起来被缓存”；何时 `flush` 会触发写入与可见性变化；哪些现象属于 ORM 的正常语义而不是“数据库不一致”。

## 从这里开始（先建立坐标）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线（按事实递进）

- [实体状态](part-01-data-jpa/01-entity-states.md)
- [持久化上下文](part-01-data-jpa/02-persistence-context.md)
- [flush 与可见性](part-01-data-jpa/03-flush-and-visibility.md)
- [脏检查](part-01-data-jpa/04-dirty-checking.md)
- [fetching 与 N+1](part-01-data-jpa/05-fetching-and-n-plus-one.md)
- [@DataJpaTest](part-01-data-jpa/06-datajpatest-slice.md)
- [SQL 调试](part-01-data-jpa/07-debug-sql.md)

## 关联模块（按需串联）

- 事务边界与一致性：`spring-core-tx`

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-data-jpa -Dtest=*ExerciseSolutionTest test`
- 并发/性能（EntityManager/事务边界隔离）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaEntityManagerConcurrencyLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
