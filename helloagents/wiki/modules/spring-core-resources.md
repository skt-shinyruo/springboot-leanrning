# spring-core-resources

## Purpose

学习 Spring Resource 抽象：classpath/file/url 资源定位与读取。

## Module Overview

- **Responsibility:** 用最小示例与测试实验覆盖 Resource 加载、路径语义与常见坑。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-09

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
  - 对应测试类：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBookMatrixLabTest.java`

## Specifications

### Source Layout
- docs：`docs/resources/spring-core-resources/README.md`（目录页）
- docs：`docs/resources/spring-core-resources/part-00-guide/`（深挖指南）
- docs：`docs/resources/spring-core-resources/part-01-resource-abstraction/`（Resource 抽象与定位规则）
- docs：`docs/resources/spring-core-resources/appendix/`（常见坑/自测题）
- src(main)：`spring-core-modules/spring-core-resources/src/main/java/com/learning/springboot/springcoreresources/SpringCoreResourcesApplication.java`（入口，包名保持不变）
- src(main)：`spring-core-modules/spring-core-resources/src/main/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/**`
- src(test)：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part00_guide/**`
- src(test)：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/**`

### Docs Index
- 入口：`docs/resources/spring-core-resources/README.md`
- 断点地图：`docs/resources/spring-core-resources/part-00-guide/140-02-breakpoint-map.md`
- 关键分支矩阵：`docs/resources/spring-core-resources/part-00-guide/140-04-branch-decision-matrix.md`
- 排障 playbook：`docs/resources/spring-core-resources/appendix/147-90-common-pitfalls.md`
- 自检清单：`docs/resources/spring-core-resources/appendix/148-99-self-check.md`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`

### Requirement: Resource 学习闭环
**Module:** spring-core-resources
通过实验让用户理解不同前缀与相对路径的实际含义。

#### Scenario: 多种 Resource 前缀可验证
- classpath/file/url 的行为差异可通过测试稳定断言

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** spring-core-resources
把“classpath* pattern/exists vs handle/编码与读取边界”写成可断言主线，并补齐章节坑点证据链。

## Change History

- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 关键分支排障入口）
- [202601071034_all_modules_docs_ag_contract](../../history/2026-01/202601071034_all_modules_docs_ag_contract/) - ✅ 已执行：全模块 docs 章节结构整理（A–G 结构 + 对应 Lab/Test 入口块）；后续不再推荐 A–G 作为写作规范/闸门
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601061556_spring_core_modules_teaching_rollout](../../history/2026-01/202601061556_spring_core_modules_teaching_rollout/) - ✅ 已执行：对齐 docs 目录页/Part 编号与章节末尾“对应 Lab/Test”入口块，清理正文 `docs/NN` 缩写引用，并通过断链检查与教学覆盖检查
- [202601041046_spring-core-part-structure-sync](../../history/2026-01/202601041046_spring-core-part-structure-sync/) - ✅ 已执行：对齐 docs Part 目录结构与 src/main+src/test 分包结构（语义化 Part 命名），并修复 README/跨模块引用路径

## Dependencies

- 基础容器概念（可选）
