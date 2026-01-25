# （Redirect）Validation 主线（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）Validation 主线（旧入口）
    - 怎么使用：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](155-validation-mainline.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
