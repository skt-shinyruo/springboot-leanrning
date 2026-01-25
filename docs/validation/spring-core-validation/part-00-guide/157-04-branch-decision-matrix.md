# 第 157 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationMechanicsLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 157 章：02：断点地图（Validation Debugger Pack）](157-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 158 章：01：约束心智模型：annotation → ConstraintValidator](../part-01-validation-core/158-01-constraint-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
- 回到主线：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「04：关键分支矩阵（Branch Decision Matrix）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationMechanicsLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| programmatic validate | 手动调用 Validator | 返回 violations 列表 | `SpringCoreValidationMechanicsLabTest` | violations size |
| method validation | 通过 proxy 调用方法 | 触发 ExecutableValidator | `SpringCoreValidationMechanicsLabTest` | 调用栈是否进入 interceptor |
| groups | 指定 groups | 只启用对应组约束 | `SpringCoreValidationLabTest` | groups/violations |
| custom constraint | 自定义 ConstraintValidator | 自定义逻辑生效 | `SpringCoreValidationLabTest` | validator 调用 |

## 推荐运行命令

- `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/164-90-common-pitfalls.md`](../appendix/164-90-common-pitfalls.md)
- 自检：[`../appendix/165-99-self-check.md`](../appendix/165-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.validation.beanvalidation.MethodValidationPostProcessor`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.validation.beanvalidation.SpringValidatorAdapter`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreValidationMechanicsLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationMechanicsLabTest` / `SpringCoreValidationLabTest` / `SpringCoreValidationBranchMatrixLabTest`

上一章：[157-02-breakpoint-map.md](157-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/164-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
