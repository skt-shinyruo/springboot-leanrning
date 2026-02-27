# Spring Profiles：激活条件与 Bean 选择

Profile 的核心语义不是“加载哪个配置文件”，而是决定哪些配置片段与哪些 Bean 会进入容器。环境不一致、条件不生效、Bean 缺失等问题，往往可以先回到 profile 的事实：到底激活了哪些 profile、这些 profile 如何影响条件注册与装配结果。

---

## 10 分钟入口：先把“激活事实”钉住

- `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`

运行后应能回答：active profiles 的最终值是什么；哪些 Bean 因 profile 条件进入或退出容器；同一配置在不同启动参数下为何会产生不同的 bean graph。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [Profile 激活与 Bean 选择](part-01-profiles/01-profile-activation-and-bean-selection.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-core-profiles -Dtest=*ExerciseSolutionTest test`
- 并发/性能（Environment 并发读取边界）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesEnvironmentConcurrencyLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
