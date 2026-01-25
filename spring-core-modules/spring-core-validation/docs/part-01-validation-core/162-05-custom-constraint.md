# 第 162 章：05. 自定义约束：如何写一个最小可用的 `@Constraint`？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：自定义约束：如何写一个最小可用的 `@Constraint`？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 161 章：04. Groups：同一个对象，为什么“创建”和“更新”要用不同规则？](161-04-groups.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 163 章：06. Debug / 观察：如何排查“校验为什么没生效？”](163-06-debugging.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 自定义约束：如何写一个最小可用的 `@Constraint`？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreValidationMechanicsLabTest`

## 机制主线

当内置约束不够用时，你可以自定义约束注解：

- 声明注解（`@interface`）
- 关联一个 `ConstraintValidator`
- 在 `isValid(...)` 里实现规则

本模块的示例约束是：

- `@StartsWith(prefix = "user:")`

你会看到：

- `"user:bob"` 通过
- `"bob"` 失败

## 你应该得到的结论

自定义约束并不神秘，本质是：

> 把规则封装成一个可复用的“注解 + 校验器”对。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreValidationMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `SpringCoreValidationMechanicsLabTest#customConstraintsCanBeDefinedWithConstraintValidator`

## 常见坑与边界

### 坑点 1：自定义约束能跑但不可用（message/propertyPath 不清晰），导致调用方无法定位问题

- Symptom：你做了自定义约束，但 violations 给出的信息不清晰，调用方不知道哪个字段因什么失败
- Root Cause：自定义约束需要把 message 与 propertyPath 等“诊断信息”设计为可消费的契约
- Verification：
  - 自定义约束可用：`SpringCoreValidationMechanicsLabTest#customConstraintsCanBeDefinedWithConstraintValidator`
  - violation 含 propertyPath/message：`SpringCoreValidationMechanicsLabTest#constraintViolationIncludesMessageAndPropertyPath`
- Fix：把 message 设计成可读且可参数化（如 `{prefix}`），并用测试断言 violations 的 message 与 path

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationMechanicsLabTest`

上一章：[04-groups](161-04-groups.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06-debugging](163-06-debugging.md)

<!-- BOOKIFY:END -->
