# springboot-business-case

## Purpose

用一个端到端业务案例串联：Web → 数据 → 事务 → 安全 → 可观测性。

## Module Overview

- **Responsibility:** 用可运行案例让学习者把多个知识点串成完整链路。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Specifications

### Requirement: 业务案例串联
**Module:** springboot-business-case
覆盖一个可运行业务流程与关键非功能点（日志、事务、安全）。

#### Scenario: 端到端流程可被测试验证
- 核心流程有集成测试兜底

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** springboot-business-case
把端到端链路（校验/事务/事件/AOP/异常塑形/回滚边界）做成可断言的“主线 + 分支 + 排障入口”。

#### Scenario: Guide 主线可作为导航图
- Guide 已补齐：请求 → 校验 → 事务 → 事件 → AOP → 异常塑形 → 回滚边界

#### Scenario: 章节坑点可回归
- 每章至少 1 个可断言坑点，并绑定默认 `*LabTest#method` 作为证据链

## Dependencies

- 依赖多个基础模块（学习路径依赖）

## Docs & 复现入口

- **Docs Index:** `spring-boot-modules/spring-boot-business-case/docs/README.md`
- **Docs Guide:** `spring-boot-modules/spring-boot-business-case/docs/part-00-guide/190-00-deep-dive-guide.md`
- **Breakpoint Map:** `spring-boot-modules/spring-boot-business-case/docs/part-00-guide/190-02-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-boot-modules/spring-boot-business-case/docs/part-00-guide/190-04-branch-decision-matrix.md`
- **Playbook:** `spring-boot-modules/spring-boot-business-case/docs/appendix/192-90-common-pitfalls.md`
- **Self-check:** `spring-boot-modules/spring-boot-business-case/docs/appendix/193-99-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBranchMatrixLabTest.java`
- **Lab:** `spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`
- **Lab (Service):** `spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseServiceLabTest.java`
- **Exercise:** `spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseSolutionTest.java`
- **Lab（并发/性能：并发下的业务边界证据链）：** `spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part02_perf_concurrency/BootBusinessCaseConcurrentOrderPlacementLabTest.java`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：为保留领域分层（`api/app/domain/events/tracing`），仅对 tests 与 docs 做 Part 对齐
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_business_case`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs）

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（并发下的业务边界证据链）
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 tests 分包（保留领域分层），并修复 README/docs 引用
