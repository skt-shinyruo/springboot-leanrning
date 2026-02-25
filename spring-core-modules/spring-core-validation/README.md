# spring-core-validation

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”学习 **Bean Validation（Jakarta Validation）**，以及它在 Spring 应用中的常见用法。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/](docs/README.md)。

## 你将学到什么

- 在命令对象上声明约束（`@NotBlank`、`@Email`、`@Min` 等）
- 程序化校验：直接使用 `jakarta.validation.Validator`
- 方法参数校验：`@Validated` + `@Valid`（本质依赖 Spring 代理）
- groups：按场景启用不同约束
- 自定义约束：`@Constraint` + `ConstraintValidator`

## 前置知识

- 了解基本的校验注解（不要求深入）
- 想理解“method validation 为什么依赖代理”：建议先了解 `spring-core-aop` 的代理心智模型
- 如果你在 Web 场景学习校验：建议配合 `springboot-web-mvc`

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

1. [约束心智模型：你在校验什么？校验结果是什么？](docs/part-01-validation-core/01-constraint-mental-model.md)
2. [程序化校验：为什么直接用 `Validator` 仍然很重要？](docs/part-01-validation-core/02-programmatic-validator.md)
3. [方法参数校验：为什么它必须依赖 Spring 代理？](docs/part-01-validation-core/03-method-validation-proxy.md)
4. [Groups：按场景启用不同规则](docs/part-01-validation-core/04-groups.md)
5. [自定义约束：写一个最小可用的 `@Constraint`](docs/part-01-validation-core/05-custom-constraint.md)
6. [Debug / 观察：如何排查“校验为什么没生效？”](docs/part-01-validation-core/06-debugging.md)
7. [常见坑清单（建议反复对照）](docs/appendix/01-common-pitfalls.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationLabTest.java` | 程序化校验 + Spring 集成（验证异常类型/violation） | ⭐⭐ | `docs/01`、`docs/02` |
| Lab | `src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationMechanicsLabTest.java` | 无代理不会触发 method validation、groups、自定义约束 | ⭐⭐ | `docs/03` → `docs/05` |
| Exercise | `src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseTest.java` | 扩展约束/groups/自定义注解等练习 | ⭐⭐–⭐⭐⭐ | `docs/06`、`docs/90` |

## 概念 → 在本模块哪里能“看见”

| 你要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 你应该能解释清楚 |
| --- | --- | --- | --- |
| violations 的结构化信息 | [docs/01](docs/part-01-validation-core/01-constraint-mental-model.md) | `SpringCoreValidationLabTest#programmaticValidationFindsViolations` | `propertyPath/message` 分别代表什么 |
| 程序化校验 | [docs/02](docs/part-01-validation-core/02-programmatic-validator.md) | `ProgrammaticValidationService` + `SpringCoreValidationLabTest#programmaticValidationReturnsNoViolationsForValidInput` | 不依赖 MVC 也能做校验与断言 |
| 方法参数校验依赖代理 | [docs/03](docs/part-01-validation-core/03-method-validation-proxy.md) | `SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy` | 没有代理就没有拦截器 |
| groups 的选择逻辑 | [docs/04](docs/part-01-validation-core/04-groups.md) | `SpringCoreValidationMechanicsLabTest#groupsControlWhichConstraintsApply` | 同一个对象，不同 group 触发不同约束 |
| 自定义约束 | [docs/05](docs/part-01-validation-core/05-custom-constraint.md) | `SpringCoreValidationMechanicsLabTest#customConstraintsCanBeDefinedWithConstraintValidator` | `ConstraintValidator` 如何被调用 |

## 常见 Debug 路径

- 程序化校验优先看：`propertyPath` 与 `message`（先定位、再解释）
- 方法参数校验优先排查：是不是 Spring bean、是不是代理、有没有自调用绕过代理、是否有 `@Validated`
- 想把机制看清楚：先用最小示例（service + test），再考虑 MVC 入口

## 常见坑

- 忘了 `@Validated` 导致方法参数校验不生效
- 直接 `new` service 没有代理，因此不会触发 method validation
- group 没指定导致你误以为约束“失效”

## 参考

- Jakarta Bean Validation（Jakarta Validation）规范
- Spring Framework Reference：Validation, Data Binding, and Type Conversion
