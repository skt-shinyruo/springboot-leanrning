# 06. Debug / 观察：如何排查“校验为什么没生效？”
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Debug / 观察：如何排查“校验为什么没生效？”
    - 怎么使用：先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 自定义约束：如何写一个最小可用的 `@Constraint`？](05-custom-constraint.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑清单（建议反复对照）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「06. Debug / 观察：如何排查“校验为什么没生效？”」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreValidationLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 本章结束后，应能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 速读路径：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

## 机制主线

校验没生效，常见原因通常不在约束本身，而在“校验入口”。

## 1) 程序化校验不生效？

先问自己：

- 我是不是调用了 `validator.validate(...)`？
- 我是不是在校验同一个对象实例？

本模块的 `ProgrammaticValidationService` 是最透明、最好排查的入口。

## 2) 方法参数校验不生效？

排查顺序建议是：

1. 这个对象是不是 Spring 管理的 bean？
2. 它是不是代理？（`AopUtils.isAopProxy(bean)`）
3. 我是不是“走代理调用”？（有没有自调用绕过代理）
4. 类上是否有 `@Validated`（触发 method validation 的关键标记）

对照测试：

## 3) 如何把 violations 看清楚？

学习阶段可以在断言里至少检查两项：

- `propertyPath`（定位字段）
- `message`（理解默认错误提示）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

- `SpringCoreValidationLabTest#methodValidatedServiceIsAnAopProxy`
- `SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy`

验证入口：`SpringCoreValidationMechanicsLabTest#constraintViolationIncludesMessageAndPropertyPath`

## 常见坑与边界

### 坑点 1：排障只看日志，不看 violations 证据链（路径/消息/group），导致越查越乱

- Symptom：校验失败时只能看到异常/日志，但不知道是哪条约束、哪个 group 生效
- Root Cause：violations 是结构化证据；groups 决定“哪些约束参与本次校验”
- Verification：
  - group 分流：`SpringCoreValidationMechanicsLabTest#groupsControlWhichConstraintsApply`
  - violation 证据字段：`SpringCoreValidationMechanicsLabTest#constraintViolationIncludesMessageAndPropertyPath`
- Fix：先用测试最小化复现（固定 group/输入），再基于 violations 的 propertyPath/message 定位规则与触发点

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[05-custom-constraint](05-custom-constraint.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->
