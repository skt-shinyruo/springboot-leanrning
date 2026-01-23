# Spring Boot Basics：目录

> 这一模块负责把“配置如何影响应用行为”的主线跑通：先解决配置从哪来、怎么覆盖；再解决如何绑定为类型安全对象。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/003-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/004-00-deep-dive-guide.md)

## 顺读主线

- [配置源与 Profiles](part-01-boot-basics/005-01-property-sources-and-profiles.md)
- [配置绑定（@ConfigurationProperties）](part-01-boot-basics/006-02-configuration-properties-binding.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[004-02-breakpoint-map.md](part-00-guide/004-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[004-04-branch-decision-matrix.md](part-00-guide/004-04-branch-decision-matrix.md)
- 排障 playbook：[007-90-common-pitfalls.md](appendix/007-90-common-pitfalls.md)
- 自检清单：[008-99-self-check.md](appendix/008-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :springboot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :springboot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
- 练习与答案（Exercises/Solutions 约定）：[exercises-and-solutions.md](../../book/exercises-and-solutions.md)
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :springboot-basics -Dtest=*ExerciseSolutionTest test`
- 并发/性能专题（可复现实验范式）：[performance-and-concurrency.md](../../book/performance-and-concurrency.md)
- 可跑入口（并发/性能 Lab - Environment 并发读取一致性）：`mvn -q -pl :springboot-basics -Dtest=BootBasicsEnvironmentConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/007-90-common-pitfalls.md)
- [自检](appendix/008-99-self-check.md)

## 下一步推荐

- 想把“配置 → Bean 装配”接上主线：继续读 `spring-core-beans`（IoC 容器）。
