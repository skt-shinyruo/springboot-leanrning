# 第 155 章：Validation 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Validation 主线
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 154 章：自测题（Spring Core Profiles）](../profiles/spring-core-profiles/appendix/154-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 156 章：主线时间线：Spring Validation](../validation/spring-core-validation/part-00-guide/156-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**校验错误从哪里来、为什么同样是“参数不合法”会走不同分支、为什么方法级校验常常“看起来没生效”**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Validation 主线 —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Validation 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. 约束声明：`@NotNull/@Size/...`（Bean Validation）
2. 校验执行：
   - Web 场景：参数绑定后校验（BindingResult/异常分支）
   - 方法级：`@Validated` 触发代理，在方法调用边界做校验
3. 产出结果：ConstraintViolation / FieldError / ProblemDetail 等
4. 常见坑：忘了 `@Validated`、代理类型导致不生效、自调用绕过、groups 与默认组误解

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/validation/spring-core-validation/part-00-guide/157-02-breakpoint-map.md`](../validation/spring-core-validation/part-00-guide/157-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/validation/spring-core-validation/part-00-guide/157-04-branch-decision-matrix.md`](../validation/spring-core-validation/part-00-guide/157-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/validation/spring-core-validation/appendix/164-90-common-pitfalls.md`](../validation/spring-core-validation/appendix/164-90-common-pitfalls.md)
- 自检清单：[`docs/validation/spring-core-validation/appendix/165-99-self-check.md`](../validation/spring-core-validation/appendix/165-99-self-check.md)

- 模块目录页：[`docs/validation/spring-core-validation/README.md`](../validation/spring-core-validation/README.md)
- 模块主线时间线（含可跑入口）：[`docs/validation/spring-core-validation/part-00-guide/03-mainline-timeline.md`](../validation/spring-core-validation/part-00-guide/156-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationLabTest test`（`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`（`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`（`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseTest.java`

---

## 下一章怎么接

当你把“功能正确”跑通后，下一层是“能被观察与运维”：Actuator/metrics/health 让你把系统变成可观测系统。

- 下一章：[第 166 章：Actuator/Observability 主线](166-actuator-observability-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Validation 主线」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Validation 主线」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Validation 主线」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
