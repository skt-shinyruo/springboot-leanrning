# springboot-basics

## Purpose

Spring Boot 基础：工程结构、配置、启动流程与最常用开发习惯。

## Module Overview

- **Responsibility:** 帮助学习者能跑通项目、理解配置与基础概念，为后续模块打底。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Specifications

### Requirement: 基础学习闭环
**Module:** springboot-basics
覆盖启动、配置、依赖管理与常用开发命令。

#### Scenario: 能跑通并理解最小应用
- 通过命令行与测试验证模块可运行

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** springboot-basics
把“启动/配置/Profiles/覆盖优先级”写成可排障主线，并绑定默认 Lab 的可断言入口。

#### Scenario: Guide 主线可作为导航图
- Guide 已补齐：启动与配置加载、profile 激活与差异、override/优先级与排障入口

#### Scenario: 章节坑点可回归
- 每章至少 1 个可断言坑点，并绑定默认 `*LabTest#method` 作为证据链

## Dependencies

- 与其他模块弱耦合

## Docs & 复现入口

- **Docs Index:** `docs/basics/springboot-basics/README.md`
- **Docs Guide:** `docs/basics/springboot-basics/part-00-guide/004-00-deep-dive-guide.md`
- **Breakpoint Map:** `docs/basics/springboot-basics/part-00-guide/004-02-breakpoint-map.md`
- **Branch Decision Matrix:** `docs/basics/springboot-basics/part-00-guide/004-04-branch-decision-matrix.md`
- **Playbook:** `docs/basics/springboot-basics/appendix/007-90-common-pitfalls.md`
- **Self-check:** `docs/basics/springboot-basics/appendix/008-99-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :springboot-basics -Dtest=BootBasicsBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :springboot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBranchMatrixLabTest.java`
- **Labs:**
  - `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java`
  - `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java`
  - `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`
- **Exercises:** `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseSolutionTest.java`
- **Lab（并发/性能：Environment 并发读取一致性）：** `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part02_perf_concurrency/BootBasicsEnvironmentConcurrencyLabTest.java`
- **Book 专题页（方法论与样板索引）：** `docs/book/performance-and-concurrency.md`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；示例代码集中在 `com.learning.springboot.bootbasics.part01_boot_basics`
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_boot_basics`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs）

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（Environment 并发读取一致性）
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601071034_all_modules_docs_ag_contract](../../history/2026-01/202601071034_all_modules_docs_ag_contract/) - ✅ 已执行：全模块 docs 章节结构整理（A–G 结构 + 对应 Lab/Test 入口块）；后续不再推荐 A–G 作为写作规范/闸门
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用
- [202601062024_springboot_modules_teaching_rollout](../../history/2026-01/202601062024_springboot_modules_teaching_rollout/) - ✅ 已执行：docs/README 章节链接 SSOT 化 + guide/appendix 可跑入口块补齐 + 自检闸门覆盖
