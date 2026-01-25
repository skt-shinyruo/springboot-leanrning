# springboot-actuator

## Purpose

学习 Spring Boot Actuator：健康检查、指标、日志与端点暴露策略。

## Module Overview

- **Responsibility:** 提供 Actuator 的可运行示例与验证用例，理解端点与安全边界。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Specifications

### Requirement: Actuator 学习闭环
**Module:** springboot-actuator
覆盖常用端点、配置项与可观测性基础。

#### Scenario: 端点暴露与访问控制
- 通过配置与测试验证端点是否可访问

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** springboot-actuator
把“端点注册/暴露策略/安全边界/排障入口”落到可断言的默认 Lab 证据链与断点入口。

#### Scenario: Guide 主线可作为导航图
- Guide 已补齐：端点注册 → 暴露策略 → 安全边界 → 排障入口
- 关键分支与断点在 Guide 中可一跳定位

#### Scenario: 章节坑点可回归
- 每章至少 1 个可断言坑点，并绑定默认 `*LabTest#method` 作为证据链

## Dependencies

- 与其他模块弱耦合

## Docs & 复现入口

- **Docs Index:** `spring-boot-modules/spring-boot-actuator/docs/README.md`
- **Docs Guide:** `spring-boot-modules/spring-boot-actuator/docs/part-00-guide/168-00-deep-dive-guide.md`
- **Breakpoint Map:** `spring-boot-modules/spring-boot-actuator/docs/part-00-guide/168-02-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-boot-modules/spring-boot-actuator/docs/part-00-guide/168-04-branch-decision-matrix.md`
- **Playbook:** `spring-boot-modules/spring-boot-actuator/docs/appendix/170-90-common-pitfalls.md`
- **Self-check:** `spring-boot-modules/spring-boot-actuator/docs/appendix/171-99-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBranchMatrixLabTest.java`
- **Labs:**
  - `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java`
  - `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`
- **Exercise:** `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseSolutionTest.java`
- **Lab（并发/性能：并发请求驱动 metrics 增量）：** `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part02_perf_concurrency/BootActuatorMetricsConcurrencyLabTest.java`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；Actuator 示例集中在 `com.learning.springboot.bootactuator.part01_actuator`
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_actuator`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs）

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（并发请求驱动 metrics 增量）
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用
