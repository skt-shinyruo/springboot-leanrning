# 第 156 章：主线时间线：Spring Validation
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Validation
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 155 章：Validation 主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 157 章：深挖指南（Spring Core Validation）](157-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：Bean Validation（JSR-380）在 Spring 中如何落地，包括约束模型、方法级校验与代理边界。
    - 读完你应该能复述：**定义约束 → 触发校验（对象/方法）→ 生成约束违规 → 映射为错误输出** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01 顺读 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`SpringCoreValidationLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Validation —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
- 回到主线：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Validation」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 校验贯穿“入口层”（Web 参数/DTO）与“领域层”（方法级约束）：很多问题会同时触及 WebMVC 与 AOP 代理边界。
- 如果你遇到“方法校验不生效/自调用不生效”，通常要回到“代理主线”一起看（AOP 模块）。

## 主线时间线（建议顺读）

1. 先建立约束心智模型：Constraint/Validator/Violation 的关系
   - 阅读：[01. 约束心智模型](../part-01-validation-core/158-01-constraint-mental-model.md)
2. 显式校验：Programmatic Validator 如何工作（以及什么时候用它）
   - 阅读：[02. Programmatic Validator](../part-01-validation-core/159-02-programmatic-validator.md)
3. 方法级校验：为什么它需要代理、边界在哪里
   - 阅读：[03. 方法校验与代理](../part-01-validation-core/160-03-method-validation-proxy.md)
4. Groups：一套 DTO 多场景校验怎么组织
   - 阅读：[04. Groups](../part-01-validation-core/161-04-groups.md)
5. 自定义约束：从注解到 Validator 的闭环
   - 阅读：[05. 自定义约束](../part-01-validation-core/162-05-custom-constraint.md)
6. 学会调试：先能把“触发点/执行链/失败原因”看见
   - 阅读：[06. 调试](../part-01-validation-core/163-06-debugging.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/164-90-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/165-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Validation」的生效时机/顺序/边界；断点/入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Validation」的生效时机/顺序/边界；断点/入口：`org.springframework.validation.beanvalidation.MethodValidationPostProcessor`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Validation」的生效时机/顺序/边界；断点/入口：`org.springframework.validation.beanvalidation.SpringValidatorAdapter`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreValidationLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
