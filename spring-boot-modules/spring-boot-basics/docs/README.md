# Spring Boot Basics：目录

## 导读

本页是「Spring Boot Basics：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 这一模块负责把“配置如何影响应用行为”的主线跑通：先解决配置从哪来、怎么覆盖；再解决如何绑定为类型安全对象。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [配置源与 Profiles](part-01-boot-basics/01-property-sources-and-profiles.md)
- [配置绑定（@ConfigurationProperties）](part-01-boot-basics/02-configuration-properties-binding.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-boot-basics -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - Environment 并发读取一致性）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsEnvironmentConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)

## 下一步推荐

- 想把“配置 → Bean 装配”接上主线：继续读 `spring-core-beans`（IoC 容器）。
