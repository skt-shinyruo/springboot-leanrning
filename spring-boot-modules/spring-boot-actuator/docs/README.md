# Spring Boot Actuator：端点、暴露与观测信号

本模块围绕 Actuator 的三个高频问题展开：端点是否存在、端点是否暴露、端点是否可访问。它的目标不是记住端点清单，而是把“为什么看不到/为什么访问不到/为什么指标不变化”这类问题压成可验证的分支，并能在断点里快速定位到决策点。

---

## 10 分钟入口：先把端点跑通

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`

运行后应能确认三件事实：端点的注册结果、暴露配置的最终值、访问时的安全与路径边界。

---

## 阅读路线（主线 → 排障 → 自证）

1. 建立主线坐标（章节为何这样排列）
   - [主线时间线](part-00-guide/01-mainline-timeline.md)
   - [深挖导读](part-00-guide/02-deep-dive-guide.md)
2. 顺读正文（把 endpoint 暴露与访问跑通）
   - [Actuator 基础](part-01-actuator/01-actuator-basics.md)
3. 遇到问题时回到排障入口
   - [断点地图](part-00-guide/04-breakpoint-map.md)（优先：快速命中关键分支）
   - [关键分支矩阵](part-00-guide/05-branch-decision-matrix.md)（把现象收敛成 If/Then）
   - [常见坑](appendix/01-common-pitfalls.md) / [自检](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-actuator -Dtest=*ExerciseSolutionTest test`
- 并发/性能（并发请求驱动 metrics 增量）：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorMetricsConcurrencyLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
