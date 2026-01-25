# Spring Profiles：目录

> Profile 决定“哪些 Bean 会进容器”：遇到环境不一致、条件不生效、Bean 缺失等问题，建议优先从这里核对。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/150-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/151-00-deep-dive-guide.md)

## 顺读主线

- [Profile 激活与 Bean 选择](part-01-profiles/152-01-profile-activation-and-bean-selection.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[151-02-breakpoint-map.md](part-00-guide/151-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[151-04-branch-decision-matrix.md](part-00-guide/151-04-branch-decision-matrix.md)
- 排障 playbook：[153-90-common-pitfalls.md](appendix/153-90-common-pitfalls.md)
- 自检清单：[154-99-self-check.md](appendix/154-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-core-profiles -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - Environment 并发读取边界）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesEnvironmentConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/153-90-common-pitfalls.md)
- [自检](appendix/154-99-self-check.md)
