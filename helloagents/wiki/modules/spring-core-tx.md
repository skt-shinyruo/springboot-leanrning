# spring-core-tx

## Purpose

学习 Spring 事务：传播行为、回滚规则与代理边界。

## Module Overview

- **Responsibility:** 用最小业务场景与测试实验理解事务传播/回滚，并能定位常见坑。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-14

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
  - 对应测试类：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBookMatrixLabTest.java`

## Start Here（路线图 / 第一个可运行入口）

- 路线图：`helloagents/wiki/learning-path.md`
- Docs Index：`docs/tx/spring-core-tx/README.md`
- 第一个可运行入口（3 分钟开跑）：
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxLabTest#transactionsAreActiveInsideTransactionalMethods test`
  - 对应测试类：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`

## Specifications

### Source Layout
- docs：`docs/tx/spring-core-tx/README.md`（目录页）
- docs：`docs/tx/spring-core-tx/part-00-guide/`（深挖指南）
- docs：`docs/tx/spring-core-tx/part-01-transaction-basics/`（边界/代理/回滚/传播）
- docs：`docs/tx/spring-core-tx/part-02-template-and-debugging/`（TransactionTemplate/调试）
- docs：`docs/tx/spring-core-tx/appendix/`（常见坑/自测题）
- src(main)：`spring-core-modules/spring-core-tx/src/main/java/com/learning/springboot/springcoretx/SpringCoreTxApplication.java`（入口，包名保持不变）
- src(main)：`spring-core-modules/spring-core-tx/src/main/java/com/learning/springboot/springcoretx/part01_transaction_basics/**`
- src(main)：`spring-core-modules/spring-core-tx/src/main/java/com/learning/springboot/springcoretx/part02_template_and_debugging/**`
- src(test)：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/**`
- src(test)：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/**`
- src(test)：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/**`

### Docs Index
- 入口：`docs/tx/spring-core-tx/README.md`
- 断点地图：`docs/tx/spring-core-tx/part-00-guide/053-02-breakpoint-map.md`
- 关键分支矩阵：`docs/tx/spring-core-tx/part-00-guide/053-04-branch-decision-matrix.md`
- 排障 playbook：`docs/tx/spring-core-tx/appendix/060-90-common-pitfalls.md`
- 自检清单：`docs/tx/spring-core-tx/appendix/061-99-self-check.md`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`

### Requirement: 事务学习闭环
**Module:** spring-core-tx
通过 Labs/Exercises 覆盖传播、只读、回滚规则与自调用陷阱。

#### Scenario: 不同传播行为差异可被断言
- REQUIRED/REQUIRES_NEW 等差异在测试中可稳定验证
- 传播行为进阶差异（MANDATORY/NEVER/NESTED）可通过矩阵 Lab 固化为断言
- 自调用绕过 `@Transactional` 的陷阱可最小复现并对比修复（Lab）
- `spring-boot:run` 可观察事务活跃状态与回滚/提交差异（结构化前缀 `TX:`）
- 对应可复现闭环入口：
  - `docs/tx/spring-core-tx/part-01-transaction-basics/04-propagation.md`
  - `spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxPropagationMatrixLabTest.java`
  - `docs/tx/spring-core-tx/part-01-transaction-basics/03-rollback-rules.md`
  - `spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxRollbackRulesLabTest.java`

## Change History

- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 调试入口统一）
- [202601071034_all_modules_docs_ag_contract](../../history/2026-01/202601071034_all_modules_docs_ag_contract/) - ✅ 已执行：全模块 docs 章节结构整理（A–G 结构 + 对应 Lab/Test 入口块）；后续不再推荐 A–G 作为写作规范/闸门
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601061556_spring_core_modules_teaching_rollout](../../history/2026-01/202601061556_spring_core_modules_teaching_rollout/) - ✅ 已执行：对齐 docs 目录页/Part 编号与章节末尾“对应 Lab/Test”入口块，清理正文 `docs/NN` 缩写引用，并通过断链检查与教学覆盖检查
- [202601021322_complete_spring_core_fundamentals_remaining](../../history/2026-01/202601021322_complete_spring_core_fundamentals_remaining/) - ✅ 已执行：新增 Tx 自调用陷阱 Lab，并补齐 `TxDemoRunner` 结构化输出与进度清单入口
- [202601041046_spring-core-part-structure-sync](../../history/2026-01/202601041046_spring-core-part-structure-sync/) - ✅ 已执行：对齐 docs Part 目录结构与 src/main+src/test 分包结构（语义化 Part 命名），并修复 README/跨模块引用路径

## Dependencies

- 依赖 `spring-core-aop`/`spring-core-beans` 的代理与容器基础（学习路径依赖）
