# 第 160 章：03. 方法参数校验：为什么它必须依赖 Spring 代理？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：方法参数校验：为什么它必须依赖 Spring 代理？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 159 章：02. 程序化校验：为什么直接用 `Validator` 仍然很重要？](159-02-programmatic-validator.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 161 章：04. Groups：同一个对象，为什么“创建”和“更新”要用不同规则？](161-04-groups.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. 方法参数校验：为什么它必须依赖 Spring 代理？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

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

## 你应该得到的结论

方法校验（以及 AOP/Tx）都共享同一个底层规律：

> 只有“走代理的调用”才会被拦截增强。

因此它也会受到同类自调用等问题影响（见 AOP 模块的自调用章节）。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

原因是：方法校验不是编译器能力，它需要在运行时拦截方法调用。

- `SpringCoreValidationLabTest#methodValidationThrowsForInvalidInput`
- `SpringCoreValidationLabTest#methodValidatedServiceIsAnAopProxy`

看 `SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy`：

## 常见坑与边界

### 坑点 1：以为 method validation 是语言特性，忽略它依赖 Spring 代理

- Symptom：你在 service 方法上写了约束，结果在某些调用路径上完全不生效
- Root Cause：method validation 与 `@Transactional` 一样依赖 AOP 代理；绕开代理（new/自调用）就不会触发
- Verification：
  - 代理存在性：`SpringCoreValidationLabTest#methodValidatedServiceIsAnAopProxy`
  - 无代理不触发（坑点）：`SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy`
- Fix：让调用跨 bean 边界（走代理），并用测试锁住“无效入参必抛 ConstraintViolationException”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[02-programmatic-validator](159-02-programmatic-validator.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04-groups](161-04-groups.md)

<!-- BOOKIFY:END -->
