# 99 自检：Spring Validation
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`SpringCoreValidationBookMatrixLabTest`
    - 分支入口：`SpringCoreValidationBranchMatrixLabTest`
    - 推荐先跑：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（建议反复对照）](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. Spring 容器里默认是否有 `Validator`？如何把“Validator 可用”固定成断言？
   - 证据入口：`SpringCoreValidationLabTest#validatorIsAvailableFromTheSpringContext`
2. programmatic validation 的最小闭环是什么？如何把“无效输入→violations 集合”写成断言？
   - 证据入口：`SpringCoreValidationLabTest#programmaticValidationFindsViolations`
3. programmatic validation 在有效输入下应该返回什么？如何避免“看起来没报错但其实没校验”的错觉？
   - 证据入口：`SpringCoreValidationLabTest#programmaticValidationReturnsNoViolationsForValidInput`
4. method validation 的触发边界在哪里？为什么它会以 `ConstraintViolationException` 的形式失败？
   - 证据入口：`SpringCoreValidationLabTest#methodValidationThrowsForInvalidInput`
5. method validation 为什么需要代理？如何证明“没有 Spring 代理时不会触发校验”？
   - 证据入口：`SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy`
6. 如何证明“走的是代理”？
   - 证据入口：`SpringCoreValidationLabTest#methodValidatedServiceIsAnAopProxy`
7. groups 解决的核心问题是什么？如何用一条对照用例证明“同一对象在不同 group 下违规集合不同”？
   - 证据入口：`SpringCoreValidationMechanicsLabTest#groupsControlWhichConstraintsApply`
8. 自定义约束的最小闭环是什么？（注解 → Validator → violations）如何把它写成断言？
   - 证据入口：`SpringCoreValidationMechanicsLabTest#customConstraintsCanBeDefinedWithConstraintValidator`
9. violation 的 message/path 为什么重要？如何用断言把“错误解释性”固定下来？
   - 证据入口：`SpringCoreValidationMechanicsLabTest#constraintViolationIncludesMessageAndPropertyPath`
10. Validator 是否线程安全？如何用并发实验证明“并发校验一致、不抛异常”？
    - 证据入口：`SpringCoreValidationValidatorConcurrencyLabTest#validator_isThreadSafe_underConcurrentValidations`

## 退出条件（完成标准）

- 能把校验机制拆成两条链路并提供证据：programmatic（显式触发）vs method（代理触发）。
- 能把 groups/custom constraint 的行为写成断言（而不是只背注解名）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[90-common-pitfalls](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
