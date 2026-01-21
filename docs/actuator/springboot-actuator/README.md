# Spring Boot Actuator：目录

> 建议按“主线时间线”顺读：先把 Actuator 的 endpoint 暴露与访问跑通，再回头用附录把常见坑一次性排掉。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/167-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/168-00-deep-dive-guide.md)

## 顺读主线

- [Actuator 基础](part-01-actuator/169-01-actuator-basics.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[168-02-breakpoint-map.md](part-00-guide/168-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[168-04-branch-decision-matrix.md](part-00-guide/168-04-branch-decision-matrix.md)
- 排障 playbook：[170-90-common-pitfalls.md](appendix/170-90-common-pitfalls.md)
- 自检清单：[171-99-self-check.md](appendix/171-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :springboot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :springboot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

## 排坑与自检

- [常见坑](appendix/170-90-common-pitfalls.md)
- [自检](appendix/171-99-self-check.md)
