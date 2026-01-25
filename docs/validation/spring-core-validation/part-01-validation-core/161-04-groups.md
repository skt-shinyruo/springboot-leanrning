# 第 161 章：04. Groups：同一个对象，为什么“创建”和“更新”要用不同规则？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Groups：同一个对象，为什么“创建”和“更新”要用不同规则？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 160 章：03. 方法参数校验：为什么它必须依赖 Spring 代理？](160-03-method-validation-proxy.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 162 章：05. 自定义约束：如何写一个最小可用的 `@Constraint`？](162-05-custom-constraint.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. Groups：同一个对象，为什么“创建”和“更新”要用不同规则？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

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

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreValidationMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证（不依赖 Spring）

看 `SpringCoreValidationMechanicsLabTest#groupsControlWhichConstraintsApply`

## 常见坑与边界

Validation Groups 用来解决一个常见问题：

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationMechanicsLabTest`

上一章：[03-method-validation-proxy](160-03-method-validation-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05-custom-constraint](162-05-custom-constraint.md)

<!-- BOOKIFY:END -->
