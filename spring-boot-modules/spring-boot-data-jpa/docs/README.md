# Spring Boot Data JPA：目录

## 导读

本页是「Spring Boot Data JPA：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议顺读 7 章把实体状态、持久化上下文、flush/脏检查与 N+1 的主线跑通；很多问题需要与事务（Tx）一起看。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

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
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-boot-data-jpa -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - EntityManager/事务边界隔离）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaEntityManagerConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
