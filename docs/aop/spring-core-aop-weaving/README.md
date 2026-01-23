# AOP Weaving（织入：LTW/CTW）：目录

> 织入是“代理之外的另一条路”：建议先搞清代理 vs 织入的边界，再决定 LTW/CTW，最后用 join point 维度把落点和风险控制住。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/043-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/044-00-deep-dive-guide.md)

## 顺读主线

- [代理 vs 织入](part-01-mental-model/045-01-proxy-vs-weaving.md)
- [LTW 基础](part-02-ltw/046-02-ltw-basics.md)
- [CTW 基础](part-03-ctw/047-03-ctw-basics.md)
- [Join Point 菜谱](part-04-join-points/048-04-join-point-cookbook.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[044-02-breakpoint-map.md](part-00-guide/044-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[044-04-branch-decision-matrix.md](part-00-guide/044-04-branch-decision-matrix.md)
- 排障 playbook：[049-90-common-pitfalls.md](appendix/049-90-common-pitfalls.md)
- 自检清单：[050-99-self-check.md](appendix/050-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- 可跑入口（Branch Matrix - LTW/CTW）：建议直接跑模块 `mvn -q -pl :spring-core-aop-weaving test`（让 Surefire 自动区分 execution）；或分别：
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
- 练习与答案（Exercises/Solutions 约定）：[exercises-and-solutions.md](../../book/exercises-and-solutions.md)
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-core-aop-weaving -Dtest=*ExerciseSolutionTest test`
- 并发/性能专题（可复现实验范式）：[performance-and-concurrency.md](../../book/performance-and-concurrency.md)
- 可跑入口（并发/性能 Lab - LTW 并发织入边界）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/049-90-common-pitfalls.md)
- [自检](appendix/050-99-self-check.md)
