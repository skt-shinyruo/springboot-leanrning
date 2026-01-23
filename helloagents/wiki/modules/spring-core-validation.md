# spring-core-validation

## Purpose

学习 Bean Validation（Jakarta Validation）与 Spring 的集成：校验触发时机、异常表现与消息国际化。

## Module Overview

- **Responsibility:** 用最小示例与测试实验覆盖 `@Valid`、约束注解与校验器配置。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
  - 对应测试类：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBookMatrixLabTest.java`

## Specifications

### Source Layout
- docs：`docs/validation/spring-core-validation/README.md`（目录页）
- docs：`docs/validation/spring-core-validation/part-00-guide/`（深挖指南）
- docs：`docs/validation/spring-core-validation/part-01-validation-core/`（Validation 核心机制）
- docs：`docs/validation/spring-core-validation/appendix/`（常见坑/自测题）
- src(main)：`spring-core-modules/spring-core-validation/src/main/java/com/learning/springboot/springcorevalidation/SpringCoreValidationApplication.java`（入口，包名保持不变）
- src(main)：`spring-core-modules/spring-core-validation/src/main/java/com/learning/springboot/springcorevalidation/part01_validation_core/**`
- src(test)：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/**`
- src(test)：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/**`
- src(test)：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part02_perf_concurrency/**`（并发/性能 Labs）

### Docs Index
- 入口：`docs/validation/spring-core-validation/README.md`
- 断点地图：`docs/validation/spring-core-validation/part-00-guide/157-02-breakpoint-map.md`
- 关键分支矩阵：`docs/validation/spring-core-validation/part-00-guide/157-04-branch-decision-matrix.md`
- 排障 playbook：`docs/validation/spring-core-validation/appendix/164-90-common-pitfalls.md`
- 自检清单：`docs/validation/spring-core-validation/appendix/165-99-self-check.md`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`
- Solution（Exercises 对应答案回归）：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseSolutionTest.java`
- Lab（并发/性能：Validator 并发使用边界）：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part02_perf_concurrency/SpringCoreValidationValidatorConcurrencyLabTest.java`
- Book 专题页（方法论与样板索引）：`docs/book/performance-and-concurrency.md`

### Requirement: Validation 学习闭环
**Module:** spring-core-validation
通过测试实验覆盖常见约束、嵌套校验与消息输出。

#### Scenario: 校验失败表现可被稳定断言
- 校验异常类型与消息内容可验证

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** spring-core-validation
把“约束心智模型/编程式校验/方法校验代理/自定义约束/调试入口”写成可断言主线，并补齐章节坑点证据链。

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Exercises 对应 Solutions（默认参与回归）+ 新增并发/性能可复现实验（Validator 并发使用边界）+ 补齐 docs 目录页入口
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 调试入口统一）
- [202601071034_all_modules_docs_ag_contract](../../history/2026-01/202601071034_all_modules_docs_ag_contract/) - ✅ 已执行：全模块 docs 章节结构整理（A–G 结构 + 对应 Lab/Test 入口块）；后续不再推荐 A–G 作为写作规范/闸门
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601061556_spring_core_modules_teaching_rollout](../../history/2026-01/202601061556_spring_core_modules_teaching_rollout/) - ✅ 已执行：对齐 docs 目录页/Part 编号与章节末尾“对应 Lab/Test”入口块，清理正文 `docs/NN` 缩写引用，并通过断链检查与教学覆盖检查
- [202601041046_spring-core-part-structure-sync](../../history/2026-01/202601041046_spring-core-part-structure-sync/) - ✅ 已执行：对齐 docs Part 目录结构与 src/main+src/test 分包结构（语义化 Part 命名），并修复 README/跨模块引用路径

## Dependencies

- 基础容器概念（可选）
