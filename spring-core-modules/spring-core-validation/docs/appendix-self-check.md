# 99 自检：Spring Validation
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（复盘出口）"

    - 主线入口：`SpringCoreValidationBookMatrixLabTest`
    - 分支入口：`SpringCoreValidationBranchMatrixLabTest`
    - 入口：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（排查时对照）](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题

1. Spring 容器里默认是否有 `Validator`？如何把“Validator 可用”固定成断言？
   - 证据入口：`SpringCoreValidationLabTest#validatorIsAvailableFromTheSpringContext`
2. programmatic validation 的最小闭环是什么？如何把“无效输入→violations 集合”写成断言？
   - 证据入口：`SpringCoreValidationLabTest#programmaticValidationFindsViolations`
3. programmatic validation 在有效输入下应该返回什么？如何避免“表面上没报错但本质上没校验”的错觉？
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

### 对应实验/测试

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[90-common-pitfalls](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)

<!-- BOOKIFY:END -->
