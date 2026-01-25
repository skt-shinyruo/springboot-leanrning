# 第 157 章：02：断点地图（Validation Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Validation Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationMechanicsLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 157 章：00 - Deep Dive Guide（spring-core-validation）](157-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 157 章：04：关键分支矩阵（Branch Decision Matrix）](157-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Validation Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
- 回到主线：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「02：断点地图（Validation Debugger Pack）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationMechanicsLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 运行入口（建议先跑）

- Book Matrix：`SpringCoreValidationBookMatrixLabTest`
- Branch Matrix：`SpringCoreValidationBranchMatrixLabTest`

## 断点（对象校验）

- `org.springframework.validation.beanvalidation.SpringValidatorAdapter#validate`
- `jakarta.validation.Validator#validate`（实现类由 provider 决定）

## 断点（方法校验：代理边界）

- `org.springframework.validation.beanvalidation.MethodValidationPostProcessor#afterPropertiesSet`
- `org.springframework.validation.beanvalidation.MethodValidationInterceptor#invoke`

## Watchpoints（建议）

- violations 列表（size/每条 message/path）
- 当前线程是否进入 AOP proxy（方法校验依赖 proxy）
- groups（默认组 vs 自定义组）

## 排障入口（Playbook）

- 常见坑：[`../appendix/164-90-common-pitfalls.md`](../appendix/164-90-common-pitfalls.md)
- 自检：[`../appendix/165-99-self-check.md`](../appendix/165-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationMechanicsLabTest` / `SpringCoreValidationBookMatrixLabTest` / `SpringCoreValidationBranchMatrixLabTest`

上一章：[调试](../part-01-validation-core/163-06-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[157-04-branch-decision-matrix.md](157-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
