# Spring Validation：目录

> 建议先把“约束模型 → 触发 → 违规结果”的主线跑通，再进入方法校验与代理边界；很多问题需要与 AOP/Web MVC 一起看。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/156-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/157-00-deep-dive-guide.md)

## 顺读主线

- [约束心智模型](part-01-validation-core/158-01-constraint-mental-model.md)
- [Programmatic Validator](part-01-validation-core/159-02-programmatic-validator.md)
- [方法校验与代理](part-01-validation-core/160-03-method-validation-proxy.md)
- [Groups](part-01-validation-core/161-04-groups.md)
- [自定义约束](part-01-validation-core/162-05-custom-constraint.md)
- [调试](part-01-validation-core/163-06-debugging.md)

## 关联模块（按需串联）

- 代理边界与自调用：`spring-core-aop`
- Web 入参/错误映射：`springboot-web-mvc`

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[157-02-breakpoint-map.md](part-00-guide/157-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[157-04-branch-decision-matrix.md](part-00-guide/157-04-branch-decision-matrix.md)
- 排障 playbook：[164-90-common-pitfalls.md](appendix/164-90-common-pitfalls.md)
- 自检清单：[165-99-self-check.md](appendix/165-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-core-validation -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - Validator 并发使用边界）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationValidatorConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/164-90-common-pitfalls.md)
- [自检](appendix/165-99-self-check.md)
