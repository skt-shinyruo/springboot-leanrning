# 03. 方法参数校验：为什么它必须依赖 Spring 代理？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕方法参数校验：为什么它必须依赖 Spring 代理？展开，主线可以概括为：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。

    先运行 `SpringCoreValidationLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。

    需要下探源码时，可以从 `org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 程序化校验：为什么直接用 `Validator` 仍然很重要？](02-programmatic-validator.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. Groups：同一个对象，为什么“创建”和“更新”要用不同规则？](04-groups.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

## 机制主线

很多人第一次接触方法参数校验时会困惑：

> “我只是给方法参数加了 `@Valid`，为什么还要代理？”

## 本模块的最小闭环

`MethodValidatedUserService`：

- 类上有 `@Validated`
- 方法参数是 `@Valid CreateUserCommand`

对应测试：

## 关键结论：没有 Spring 代理，就没有 method validation 拦截器

- 直接 `new MethodValidatedUserService()`
- 调用 `register(invalid)`
- 不会抛异常（因为没有代理，没有拦截器）

## 应当得到的结论

方法校验（以及 AOP/Tx）都共享同一个底层规律：

> 只有“走代理的调用”才会被拦截增强。

因此它也会受到同类自调用等问题影响（见 AOP 模块的自调用章节）。

## 最小可运行实验（Lab）

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

原因是：方法校验不是编译器能力，它需要在运行时拦截方法调用。

- `SpringCoreValidationLabTest#methodValidationThrowsForInvalidInput`
- `SpringCoreValidationLabTest#methodValidatedServiceIsAnAopProxy`

看 `SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy`：

## 常见坑与边界

### 坑点 1：以为 method validation 是语言特性，忽略它依赖 Spring 代理

在 service 方法上写了约束，结果在某些调用路径上完全不生效

method validation 与 `@Transactional` 一样依赖 AOP 代理；绕开代理（new/自调用）就不会触发

- 代理存在性：`SpringCoreValidationLabTest#methodValidatedServiceIsAnAopProxy`
- 无代理不触发（坑点）：`SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy`

让调用跨 bean 边界（走代理），并用测试锁定“无效入参必抛 ConstraintViolationException”

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[02-programmatic-validator](02-programmatic-validator.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04-groups](04-groups.md)

<!-- BOOKIFY:END -->
