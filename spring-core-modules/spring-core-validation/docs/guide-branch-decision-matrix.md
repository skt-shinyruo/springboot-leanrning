# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：分支发生在“是否走 Validator.validate”与“是否通过 proxy 触发 ExecutableValidator”。

    把 Validation 的关键分支（programmatic vs method validation、groups、自定义约束）整理成矩阵表。

    对照入口：`SpringCoreValidationBranchMatrixLabTest`。需要下探源码时，可以从 `SpringValidatorAdapter` / `MethodValidationInterceptor` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Validation）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 约束（Constraint）心智模型：校验对象与校验结果](validation-core-constraint-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreValidationBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：分支发生在“是否走 Validator.validate”与“是否通过 proxy 触发 ExecutableValidator”。需要下探源码时，可以从 `SpringValidatorAdapter` / `MethodValidationInterceptor` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| programmatic validate | 手动调用 Validator | 返回 violations 列表 | `SpringCoreValidationMechanicsLabTest` | violations size |
| method validation | 通过 proxy 调用方法 | 触发 ExecutableValidator | `SpringCoreValidationMechanicsLabTest` | 调用栈是否进入 interceptor |
| groups | 指定 groups | 只启用对应组约束 | `SpringCoreValidationLabTest` | groups/violations |
| custom constraint | 自定义 ConstraintValidator | 自定义逻辑生效 | `SpringCoreValidationLabTest` | validator 调用 |

## 运行命令

- `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

分支发生在“是否走 Validator.validate”与“是否通过 proxy 触发 ExecutableValidator”。

下一章见：[01：约束心智模型：annotation → ConstraintValidator](validation-core-constraint-mental-model.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreValidationBranchMatrixLabTest`
- Lab：`SpringCoreValidationMechanicsLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[validation-core-constraint-mental-model.md](validation-core-constraint-mental-model.md)

<!-- BOOKIFY:END -->
