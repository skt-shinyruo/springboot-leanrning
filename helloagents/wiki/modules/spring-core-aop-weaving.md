# spring-core-aop-weaving

## Purpose

学习 AspectJ weaving（织入）：LTW（`-javaagent`）与 CTW（编译期织入），以及 proxy AOP 无法覆盖的 join point 与高级 pointcut（`call/get/set/constructor/withincode/cflow` 等）。

## Module Overview

- **Responsibility:** 用可验证的 Labs/Exercises 讲清 “Proxy vs Weaving” 的能力边界与排障路径。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
  - 对应测试类：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjWeavingBookMatrixLabTest.java`

## Specifications

### Source Layout

- docs：`docs/aop/spring-core-aop-weaving/README.md`（目录页）
- docs：`docs/aop/spring-core-aop-weaving/part-00-guide/`（跑通指南：LTW/CTW）
- docs：`docs/aop/spring-core-aop-weaving/part-01-mental-model/`（Proxy vs Weaving 心智模型）
- docs：`docs/aop/spring-core-aop-weaving/part-02-ltw/`（LTW：agent + aop.xml + include 范围）
- docs：`docs/aop/spring-core-aop-weaving/part-03-ctw/`（CTW：编译期织入与范围控制）
- docs：`docs/aop/spring-core-aop-weaving/part-04-join-points/`（Join Point/Pointcut Cookbook）
- docs：`docs/aop/spring-core-aop-weaving/appendix/`（常见坑/自测题）
- src(main)：`spring-core-modules/spring-core-aop-weaving/src/main/java/com/learning/springboot/springcoreaopweaving/SpringCoreAopWeavingApplication.java`（入口）
- src(main)：`spring-core-modules/spring-core-aop-weaving/src/main/java/com/learning/springboot/springcoreaopweaving/support/**`（可断言观察点：InvocationLog/JoinPointEvent）
- src(main)：`spring-core-modules/spring-core-aop-weaving/src/main/java/com/learning/springboot/springcoreaopweaving/ctwtargets/**`（CTW 目标对象）
- src(main)：`spring-core-modules/spring-core-aop-weaving/src/main/aspect/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/**`（CTW aspects，AspectJ 语法）
- src(test)：`spring-core-modules/spring-core-aop-weaving/src/test/resources/META-INF/aop.xml`（LTW 配置）
- src(test)：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/ltwtargets/**`（LTW 目标对象）
- src(test)：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/**`（LTW aspects + Labs）
- src(test)：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/**`（CTW Labs）
- src(test)：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_perf_concurrency/**`（并发/性能 Labs）
- src(test)：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/**`（Exercises，默认 `@Disabled`）

### Docs Index

- 入口：`docs/aop/spring-core-aop-weaving/README.md`
- 断点地图：`docs/aop/spring-core-aop-weaving/part-00-guide/044-02-breakpoint-map.md`
- 关键分支矩阵：`docs/aop/spring-core-aop-weaving/part-00-guide/044-04-branch-decision-matrix.md`
- 排障 playbook：`docs/aop/spring-core-aop-weaving/appendix/049-90-common-pitfalls.md`
- 自检清单：`docs/aop/spring-core-aop-weaving/appendix/050-99-self-check.md`
- Branch Matrix（关键分支入口，建议直接跑模块以分流 LTW/CTW）：`mvn -q -pl :spring-core-aop-weaving test`
  - LTW：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
  - CTW：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
- Solution（Exercises 对应答案回归）：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseSolutionTest.java`
- Lab（并发/性能：LTW 并发织入边界）：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_perf_concurrency/AspectjLtwConcurrencyLabTest.java`
- Book 专题页（方法论与样板索引）：`docs/book/performance-and-concurrency.md`

### Requirement: LTW/CTW 可验证闭环

**Module:** spring-core-aop-weaving  
通过两套 Labs 验证 weaving 行为：

- `*Ltw*Test`：带 `-javaagent:aspectjweaver.jar`（LTW）
- `*Ctw*Test`：不带 `-javaagent`（CTW）

并覆盖至少以下 join point / pointcut：

- `call` vs `execution`
- constructor call/execution
- field get/set
- `withincode`
- `cflow`

### Requirement: 排障分流（Proxy vs Weaving）

**Module:** spring-core-aop-weaving  
能够在真实问题中分流定位：

- Proxy 世界：是否没走 proxy（call path 问题）
- LTW：是否没带 agent / 没加载 aop.xml / include 范围错误
- CTW：是否构建未织入 / 织入范围错误 / 运行时使用未织入产物

## Dependencies

- 建议先完成 `spring-core-aop`（proxy AOP 主线）
- 构建与测试依赖 AspectJ（`aspectjrt`/`aspectjweaver`）与 Maven 插件（CTW）
- 编译目标为 Java 16（为兼容 CTW 使用的 ajc source level 上限）；运行仍要求 JDK 17+（父工程 enforcer）

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（LTW 并发织入边界）
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 排障入口统一）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601061556_spring_core_modules_teaching_rollout](../../history/2026-01/202601061556_spring_core_modules_teaching_rollout/) - ✅ 已执行：对齐 docs 目录页/Part 编号与章节末尾“对应 Lab/Test”入口块，清理正文 `docs/NN` 缩写引用，并通过断链检查与教学覆盖检查
- [202601061341_spring-core-aop-weaving](../../history/2026-01/202601061341_spring-core-aop-weaving/) - ✅ 已执行：创建 `spring-core-aop-weaving` 作为 weaving 深挖模块（LTW/CTW + join point cookbook + Labs/Exercises）
