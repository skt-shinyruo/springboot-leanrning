# springboot-data-jpa

## Purpose

学习 Spring Data JPA：实体映射、Repository、事务与查询。

## Module Overview

- **Responsibility:** 用最小示例与测试验证 JPA 行为、映射与查询方式。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Specifications

### Requirement: JPA 学习闭环
**Module:** springboot-data-jpa
覆盖实体映射、Repository CRUD 与事务边界。

#### Scenario: CRUD 行为可被测试验证
- 通过测试验证保存/查询/删除

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** springboot-data-jpa
把“实体状态/持久化上下文/flush/脏检查/N+1/代理与 EntityGraph”落到可断言的默认 Lab 证据链。

#### Scenario: Guide 主线可作为导航图
- Guide 已补齐：实体状态 → Persistence Context → flush/可见性 → dirty checking → fetching/N+1 → slice 测试

#### Scenario: 关键分支可被稳定断言
- N+1 与 `EntityGraph` 规避可通过默认 Lab 的统计/计数断言复现
- `getReferenceById` 懒代理的“获取不等于加载”边界可通过默认 Lab 固化为断言

## Dependencies

- 与事务模块有学习路径关联（可选）

## Docs & 复现入口

- **Docs Index:** `spring-boot-modules/spring-boot-data-jpa/docs/README.md`
- **Docs Guide:** `spring-boot-modules/spring-boot-data-jpa/docs/part-00-guide/096-00-deep-dive-guide.md`
- **Breakpoint Map:** `spring-boot-modules/spring-boot-data-jpa/docs/part-00-guide/096-02-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-boot-modules/spring-boot-data-jpa/docs/part-00-guide/096-04-branch-decision-matrix.md`
- **Playbook:** `spring-boot-modules/spring-boot-data-jpa/docs/appendix/104-90-common-pitfalls.md`
- **Self-check:** `spring-boot-modules/spring-boot-data-jpa/docs/appendix/105-99-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBranchMatrixLabTest.java`
- **Lab:** `spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaLabTest.java`
- **Lab (Merge/Detach):** `spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaMergeAndDetachLabTest.java`
- **Lab (Debug SQL):** `spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaDebugSqlLabTest.java`
- **Exercise:** `spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseSolutionTest.java`
- **Lab（并发/性能：EntityManager/事务边界隔离）：** `spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part02_perf_concurrency/BootDataJpaEntityManagerConcurrencyLabTest.java`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；JPA 示例集中在 `com.learning.springboot.bootdatajpa.part01_data_jpa`
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_data_jpa`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs）

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（EntityManager/事务边界隔离）
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601092110_depth_align_v2_batch01_sec_jpa_events_client](../../history/2026-01/202601092110_depth_align_v2_batch01_sec_jpa_events_client/) - ✅ 已执行：batch01 深挖对齐 v2（新增 merge/detach 默认 Lab + debug-sql/appendix 补齐坑点入口 + 自测入口补齐）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用
