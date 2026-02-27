# AOP Weaving（织入：LTW/CTW）：代理之外的另一条路

织入解决的是“代理做不到或不适合做”的那部分 AOP 需求：切点落在构造器、字段、final 方法等代理天然受限的位置；或希望以字节码层面的方式改变行为边界。本模块先把“代理 vs 织入”的边界跑清楚，再分别讨论 LTW（load-time weaving）与 CTW（compile-time weaving），最后用 join point 维度把落点与风险控制住。

---

## 10 分钟入口：先确认织入是否生效

- `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`

运行后应能回答：织入在何处介入；哪些 join point 能命中、哪些不能；与代理方案相比，行为边界与可观测性有什么变化。

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

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- Branch Matrix（LTW/CTW）：建议直接运行模块测试（让 Surefire 自动区分 execution）：
  `mvn -q -pl :spring-core-aop-weaving test`
  或分别运行：
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-core-aop-weaving -Dtest=*ExerciseSolutionTest test`
- 并发/性能（LTW 并发织入边界）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
