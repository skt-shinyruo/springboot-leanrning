# 04. Groups：同一个对象，为什么“创建”和“更新”要用不同规则？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Groups：同一个对象，为什么“创建”和“更新”要用不同规则？展开，主线可以概括为：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。

    先运行 `SpringCoreValidationMechanicsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。

    需要下探源码时，可以从 `org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 方法参数校验：为什么它必须依赖 Spring 代理？](validation-core-method-validation-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 自定义约束：如何写一个最小可用的 `@Constraint`？](validation-core-custom-constraint.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「04. Groups：同一个对象，为什么“创建”和“更新”要用不同规则？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreValidationMechanicsLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreValidationMechanicsLabTest`

## 机制主线

> 同一个数据结构在不同场景下，约束规则不同。

典型场景：

- Create：字段必须填写
- Update：字段可以为空（只更新部分字段）

它演示了：

- 默认组（`Default`）校验不触发
- `Create` 组校验会触发 `@NotBlank(groups = Create.class)`

## 学习建议

- Groups 是很“工程化”的能力，但机制很清晰
- 学习阶段只需要掌握：不同 group 会选择不同的约束集合

## 最小可运行实验（Lab）

- Lab：`SpringCoreValidationMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 在本模块如何验证（不依赖 Spring）

看 `SpringCoreValidationMechanicsLabTest#groupsControlWhichConstraintsApply`

## 常见坑与边界

Validation Groups 用来解决一个常见问题：

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationMechanicsLabTest`

上一章：[03-method-validation-proxy](validation-core-method-validation-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05-custom-constraint](validation-core-custom-constraint.md)

<!-- BOOKIFY:END -->
