# AOP Weaving（织入：LTW/CTW）：目录

## 导读

本页是「AOP Weaving（织入：LTW/CTW）：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 织入是“代理之外的另一条路”：建议先搞清代理 vs 织入的边界，再决定 LTW/CTW，最后用 join point 维度把落点和风险控制住。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [代理 vs 织入](part-01-mental-model/01-proxy-vs-weaving.md)
- [LTW 基础](part-02-ltw/01-ltw-basics.md)
- [CTW 基础](part-03-ctw/01-ctw-basics.md)
- [Join Point 菜谱](part-04-join-points/01-join-point-cookbook.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- 可跑入口（Branch Matrix - LTW/CTW）：建议直接跑模块 `mvn -q -pl :spring-core-aop-weaving test`（让 Surefire 自动区分 execution）；或分别：
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-core-aop-weaving -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - LTW 并发织入边界）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
