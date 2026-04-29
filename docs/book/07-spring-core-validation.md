# 07 Spring Core Validation：约束模型、触发机制与方法校验边界

## 本章要回答的问题

- 能解释 Bean Validation 的三段式模型：约束（constraint）→ 触发（trigger）→ 违规（violation）。
- 能区分 Web 入参校验与方法校验，并能解释方法校验为何依赖代理边界。
- 能把 groups、自定义约束、调试入口等关键分支跑成可回归的断言。

## 主线框架

- **约束模型**：
  - 约束注解 + `ConstraintValidator` 实现；
  - 违规结果（`ConstraintViolation`）包含路径、消息、无效值等证据。
- **触发方式**：
  - Web 入参：通常由 Web MVC 绑定/校验阶段触发（与错误响应形状耦合）。
  - 方法校验：通常由方法拦截触发（依赖代理，常与 AOP/事务/安全类似）。
- **Groups**：
  - 以“同一对象，不同校验集合”的方式组织约束；需要用测试验证选择逻辑。
- **调试边界**：
  - 校验失败的根因可能在：约束定义、触发点、代理是否生效、以及错误映射。

本章与其他章节的关系：

- Web 入参/错误映射：回到 [06 Web MVC](06-spring-boot-web-mvc.md)
- 代理边界：回到 [04 AOP](04-spring-core-aop.md)
- 安全的“方法安全”也依赖类似代理边界：见 [14 Security](14-spring-boot-security.md)

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
  - 测试类：[`SpringCoreValidationBookMatrixLabTest.java`](../../spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-core-validation/README.md`](../../spring-core-modules/spring-core-validation/README.md)
- 导航型文档（用于快速定位“方法校验与代理”）：
  - 方法校验与代理：[`validation-core-method-validation-proxy.md`](../../spring-core-modules/spring-core-validation/docs/validation-core-method-validation-proxy.md)
  - 常见坑：[`appendix-common-pitfalls.md`](../../spring-core-modules/spring-core-validation/docs/appendix-common-pitfalls.md)

## 常见误区

- 把 `@Valid` 与 `@Validated` 当成同一个东西。它们在 groups、触发点与组合语义上有差异。
- 以为方法校验“只要注解写了就会执行”。若调用未经过代理，方法校验可能不会触发（与 AOP/事务同类问题）。
- 把校验失败当成“异常处理问题”。需要先确认：约束是否生效、触发点是否命中、违规结果是否符合预期。

## 验证练习

- 练习 1（把三段式模型跑成事实）：
  - 运行 `SpringCoreValidationBookMatrixLabTest`；
  - 选择一个校验失败场景，记录：
  - 触发点（Web 入参 or 方法拦截）；
  - `ConstraintViolation` 的路径与消息（证据）。
- 练习 2（groups 边界）：
  - 从模块文档选择一个 groups 相关入口；
  - 写出“哪一组会被触发”的规则，并用对应 Lab 验证。

## 小结

- Validation 的核心产出是：能用“约束→触发→违规”描述问题，并能在代理边界处证明为何生效/失效。
- 下一章进入 Testing，把“如何选测试切片、如何固定边界”作为工程化补齐。

## 延伸阅读

- 上一章（Web 入参校验落点）：[`06-spring-boot-web-mvc.md`](06-spring-boot-web-mvc.md)
- 代理边界（方法校验前置）：[`04-spring-core-aop.md`](04-spring-core-aop.md)
- 下一章（测试切片与 mocking）：[`08-spring-boot-testing.md`](08-spring-boot-testing.md)

---

[← 上一章](06-spring-boot-web-mvc.md) | [目录](README.md) | [下一章 →](08-spring-boot-testing.md)
