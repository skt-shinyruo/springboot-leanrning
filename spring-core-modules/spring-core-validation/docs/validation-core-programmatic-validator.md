# 02. 程序化校验：为什么直接用 `Validator` 仍然很重要？
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕程序化校验：为什么直接用 `Validator` 仍然很重要？展开，主线可以概括为：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。

    先运行 `SpringCoreValidationLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。

    需要下探源码时，可以从 `org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 约束（Constraint）心智模型：校验对象与校验结果](validation-core-constraint-mental-model.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[03. 方法参数校验：为什么它必须依赖 Spring 代理？](validation-core-method-validation-proxy.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「02. 程序化校验：为什么直接用 `Validator` 仍然很重要？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreValidationLabTest`（或文末“对应实验/测试”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

## 机制主线

即使最终在 Web 层用 `@Valid`，学习阶段仍然需要掌握程序化校验：

## 本模块的最小示例

`ProgrammaticValidationService` 只是做了一件事：

- `validator.validate(command)`

对应测试：

学习阶段可以重点观察两项：

- `violation.getPropertyPath()`：究竟是哪一个字段违反了规则
- `violation.getMessage()`：默认消息是什么（后续会学到如何自定义）

## 最小可运行实验（Lab）

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
- 运行命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

- 它最直接、最可控
- 不依赖 Spring MVC / Controller
- 更适合做机制实验与精确断言

- `SpringCoreValidationLabTest#programmaticValidationFindsViolations`
- `SpringCoreValidationLabTest#programmaticValidationReturnsNoViolationsForValidInput`

## Debug / 观察入口

机制实验入口：`SpringCoreValidationMechanicsLabTest#constraintViolationIncludesMessageAndPropertyPath`

## 常见坑与边界

### 坑点 1：以为“声明了注解就会自动校验”，忽略 programmatic 需要显式调用

在对象字段上加了约束，但某条业务路径没有任何校验行为

programmatic validation 的触发点是 `Validator#validate(...)`（不调用就不会发生）

- 显式调用得到 violations：`SpringCoreValidationLabTest#programmaticValidationFindsViolations`
- 有效输入返回空 violations：`SpringCoreValidationLabTest#programmaticValidationReturnsNoViolationsForValidInput`

把“触发点”写进代码与测试：要么在边界层显式 validate，要么使用 method validation/框架集成触发

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[01-constraint-mental-model](validation-core-constraint-mental-model.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[03-method-validation-proxy](validation-core-method-validation-proxy.md)

<!-- BOOKIFY:END -->
