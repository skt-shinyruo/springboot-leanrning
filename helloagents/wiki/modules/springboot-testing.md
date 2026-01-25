# springboot-testing

## Purpose

学习 Spring Boot 测试：测试分层、Test Slice、Mock 策略与可维护断言。

## Module Overview

- **Responsibility:** 提供多种测试策略的示例，让学习者能写出稳定、快速、可读的测试。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Specifications

### Requirement: 测试策略学习闭环
**Module:** springboot-testing
覆盖单元测试/集成测试/Test Slice 与常见误区。

#### Scenario: 能选择合适的测试切片并写出稳定断言
- 给出推荐路径与对比示例

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** springboot-testing
把“测试切片边界/Mock 替换语义/排障分流”写成可执行主线，并用默认 Lab 固化关键分支。

#### Scenario: Guide 主线可作为导航图
- Guide 已补齐：slice vs full、@MockBean 替换边界、排障分流（失败分层定位）

#### Scenario: 章节坑点可回归
- 每章至少 1 个可断言坑点，并绑定默认 `*LabTest#method` 作为证据链

## Dependencies

- 与其他模块弱耦合（为它们提供测试方法论）

## Docs & 复现入口

- **Docs Index:** `spring-boot-modules/spring-boot-testing/docs/README.md`
- **Docs Guide:** `spring-boot-modules/spring-boot-testing/docs/part-00-guide/184-00-deep-dive-guide.md`
- **Breakpoint Map:** `spring-boot-modules/spring-boot-testing/docs/part-00-guide/184-02-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-boot-modules/spring-boot-testing/docs/part-00-guide/184-04-branch-decision-matrix.md`
- **Playbook:** `spring-boot-modules/spring-boot-testing/docs/appendix/186-90-common-pitfalls.md`
- **Self-check:** `spring-boot-modules/spring-boot-testing/docs/appendix/187-99-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBranchMatrixLabTest.java`
- **Labs:**
  - `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`
  - `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java`
  - `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`
- **Exercise:** `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseSolutionTest.java`
- **Lab（并发/性能：TestContextCache 复用边界证据链）：** `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part02_perf_concurrency/BootTestingTestContextCacheLabTest.java`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；示例代码集中在 `com.learning.springboot.boottesting.part01_testing`
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_testing`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs）

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（TestContextCache 复用边界证据链）
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用
