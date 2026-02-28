# 05. 自定义约束：如何写一个最小可用的 `@Constraint`？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕自定义约束：如何写一个最小可用的 `@Constraint`？展开，主线可以概括为：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。

    先运行 `SpringCoreValidationMechanicsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。

    需要下探源码时，可以从 `org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. Groups：同一个对象，为什么“创建”和“更新”要用不同规则？](04-groups.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. Debug / 观察：如何排查“校验为什么没生效？”](06-debugging.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreValidationMechanicsLabTest`

## 机制主线

当内置约束不够用时，可以自定义约束注解：

- 声明注解（`@interface`）
- 关联一个 `ConstraintValidator`
- 在 `isValid(...)` 里实现规则

本模块的示例约束是：

- `@StartsWith(prefix = "user:")`

会看到：

- `"user:bob"` 通过
- `"bob"` 失败

## 应当得到的结论

自定义约束并不神秘，本质是：

> 把规则封装成一个可复用的“注解 + 校验器”对。

## 最小可运行实验（Lab）

- Lab：`SpringCoreValidationMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 在本模块如何验证

看 `SpringCoreValidationMechanicsLabTest#customConstraintsCanBeDefinedWithConstraintValidator`

## 常见坑与边界

### 坑点 1：自定义约束能跑但不可用（message/propertyPath 不清晰），导致调用方无法定位问题

做了自定义约束，但 violations 给出的信息不清晰，调用方不知道哪个字段因什么失败

自定义约束需要把 message 与 propertyPath 等“诊断信息”设计为可消费的契约

- 自定义约束可用：`SpringCoreValidationMechanicsLabTest#customConstraintsCanBeDefinedWithConstraintValidator`
- violation 含 propertyPath/message：`SpringCoreValidationMechanicsLabTest#constraintViolationIncludesMessageAndPropertyPath`

把 message 设计成可读且可参数化（如 `{prefix}`），并用测试断言 violations 的 message 与 path

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationMechanicsLabTest`

上一章：[04-groups](04-groups.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06-debugging](06-debugging.md)

<!-- BOOKIFY:END -->
