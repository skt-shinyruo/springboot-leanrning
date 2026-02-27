# Spring Validation：约束模型、触发时机与代理边界

本模块以“约束模型 → 触发 → 违规结果（Violation）”为主线，把校验行为拆成可运行的事实：什么时候会触发校验、违规结果如何汇总、groups 如何影响匹配，以及方法校验在代理边界下为何会出现“看起来没生效”的反直觉现象。很多校验问题需要与 AOP（代理/自调用）和 Web MVC（入参绑定与错误映射）串联理解。

---

## 10 分钟入口：先把“触发与结果”跑成事实

- `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`

运行后应能回答：一次校验触发发生在什么位置；`ConstraintViolation` 的集合如何形成；方法校验在代理/自调用场景下为何会表现不同。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [约束心智模型](part-01-validation-core/01-constraint-mental-model.md)
- [Programmatic Validator](part-01-validation-core/02-programmatic-validator.md)
- [方法校验与代理](part-01-validation-core/03-method-validation-proxy.md)
- [Groups](part-01-validation-core/04-groups.md)
- [自定义约束](part-01-validation-core/05-custom-constraint.md)
- [调试](part-01-validation-core/06-debugging.md)

## 关联模块（按需串联）

- 代理边界与自调用：`spring-core-aop`
- Web 入参/错误映射：`springboot-web-mvc`

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-core-validation -Dtest=*ExerciseSolutionTest test`
- 并发/性能（Validator 并发使用边界）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationValidatorConcurrencyLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
