# 第 157 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把 Validation 的关键分支（programmatic vs method validation、groups、自定义约束）整理成矩阵表。
    - 原理：分支发生在“是否走 Validator.validate”与“是否通过 proxy 触发 ExecutableValidator”。
    - 源码入口：`SpringValidatorAdapter` / `MethodValidationInterceptor`
    - 推荐 Lab：`SpringCoreValidationBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 157 章：02：断点地图（Validation Debugger Pack）](157-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 158 章：01：约束心智模型：annotation → ConstraintValidator](../part-01-validation-core/158-01-constraint-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreValidationBranchMatrixLabTest`
- Lab：`SpringCoreValidationMechanicsLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](157-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-validation-core/01-constraint-mental-model.md](../part-01-validation-core/158-01-constraint-mental-model.md)

<!-- BOOKIFY:END -->

