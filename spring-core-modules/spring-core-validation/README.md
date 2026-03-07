# spring-core-validation

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”学习 **Bean Validation（Jakarta Validation）**，以及它在 Spring 应用中的常见用法。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 docs/。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

## 本模块的学习产出

- 在命令对象上声明约束（`@NotBlank`、`@Email`、`@Min` 等）
- 程序化校验：直接使用 `jakarta.validation.Validator`
- 方法参数校验：`@Validated` + `@Valid`（本质依赖 Spring 代理）
- groups：按场景启用不同约束
- 自定义约束：`@Constraint` + `ConstraintValidator`

## 前置知识

- 了解基本的校验注解（不要求深入）
- 想理解“method validation 为什么依赖代理”：建议先了解 `spring-core-aop` 的代理心智模型
- 在 Web 场景学习校验时：建议配合 `springboot-web-mvc`

## 关键命令

### 运行

```bash
mvn -pl :spring-core-validation spring-boot:run
```

运行后观察控制台输出：

- 程序化校验得到的 violations
- 方法参数校验在输入非法时抛出的异常

### 测试

```bash
mvn -pl :spring-core-validation test
```

## 推荐 docs 阅读顺序（从现象到机制）

1. [约束心智模型：在校验什么？校验结果是什么？](docs/validation-core-constraint-mental-model.md)
2. [程序化校验：为什么直接用 `Validator` 仍然很重要？](docs/validation-core-programmatic-validator.md)
3. [方法参数校验：为什么它必须依赖 Spring 代理？](docs/validation-core-method-validation-proxy.md)
4. [Groups：按场景启用不同规则](docs/validation-core-groups.md)
5. [自定义约束：写一个最小可用的 `@Constraint`](docs/validation-core-custom-constraint.md)
6. [Debug / 观察：如何排查“校验为什么没生效？”](docs/validation-core-debugging.md)
7. [常见坑清单（建议反复对照）](docs/appendix-common-pitfalls.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationLabTest.java` | 程序化校验 + Spring 集成（验证异常类型/violation） | ⭐⭐ | `docs/01`、`docs/02` |
| Lab | `src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationMechanicsLabTest.java` | 无代理不会触发 method validation、groups、自定义约束 | ⭐⭐ | `docs/03` → `docs/05` |
| Exercise | `src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseTest.java` | 扩展约束/groups/自定义注解等练习 | ⭐⭐–⭐⭐⭐ | `docs/06`、`docs/90` |

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| violations 的结构化信息 | [docs/01](docs/validation-core-constraint-mental-model.md) | `SpringCoreValidationLabTest#programmaticValidationFindsViolations` | `propertyPath/message` 分别代表什么 |
| 程序化校验 | [docs/02](docs/validation-core-programmatic-validator.md) | `ProgrammaticValidationService` + `SpringCoreValidationLabTest#programmaticValidationReturnsNoViolationsForValidInput` | 不依赖 MVC 也能做校验与断言 |
| 方法参数校验依赖代理 | [docs/03](docs/validation-core-method-validation-proxy.md) | `SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy` | 没有代理就没有拦截器 |
| groups 的选择逻辑 | [docs/04](docs/validation-core-groups.md) | `SpringCoreValidationMechanicsLabTest#groupsControlWhichConstraintsApply` | 同一个对象，不同 group 触发不同约束 |
| 自定义约束 | [docs/05](docs/validation-core-custom-constraint.md) | `SpringCoreValidationMechanicsLabTest#customConstraintsCanBeDefinedWithConstraintValidator` | `ConstraintValidator` 如何被调用 |

## 常见 Debug 路径

- 程序化校验优先看：`propertyPath` 与 `message`（先定位、再解释）
- 方法参数校验优先排查：是不是 Spring bean、是不是代理、有没有自调用绕过代理、是否有 `@Validated`
- 想把机制看清楚：先用最小示例（service + test），再考虑 MVC 入口

## 常见坑

- 忘了 `@Validated` 导致方法参数校验不生效
- 直接 `new` service 没有代理，因此不会触发 method validation
- group 未指定导致误以为约束“失效”

## 参考

- Jakarta Bean Validation（Jakarta Validation）规范
- Spring Framework Reference：Validation, Data Binding, and Type Conversion

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Validation：约束模型、触发时机与代理边界

本模块以“约束模型 → 触发 → 违规结果（Violation）”为主线，把校验行为拆成可运行的事实：什么时候会触发校验、违规结果如何汇总、groups 如何影响匹配，以及方法校验在代理边界下为何会出现“看起来没生效”的反直觉现象。很多校验问题需要与 AOP（代理/自调用）和 Web MVC（入参绑定与错误映射）串联理解。

---

### 10 分钟入口：先把“触发与结果”跑成事实
- `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`

运行后应能回答：一次校验触发发生在什么位置；`ConstraintViolation` 的集合如何形成；方法校验在代理/自调用场景下为何会表现不同。

### 从这里开始（建议顺序）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线
- [约束心智模型](docs/validation-core-constraint-mental-model.md)
- [Programmatic Validator](docs/validation-core-programmatic-validator.md)
- [方法校验与代理](docs/validation-core-method-validation-proxy.md)
- [Groups](docs/validation-core-groups.md)
- [自定义约束](docs/validation-core-custom-constraint.md)
- [调试](docs/validation-core-debugging.md)

### 关联模块（按需串联）
- 代理边界与自调用：`spring-core-aop`
- Web 入参/错误映射：`springboot-web-mvc`

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-core-validation -Dtest=*ExerciseSolutionTest test`
- 并发/性能（Validator 并发使用边界）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationValidatorConcurrencyLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
