# springboot-cache

## Purpose

学习 Spring Cache：缓存注解、key 生成、缓存失效与常见坑。

## Module Overview

- **Responsibility:** 用最小示例与测试覆盖缓存命中/失效/条件缓存。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Specifications

### Requirement: 缓存学习闭环
**Module:** springboot-cache
通过可断言实验理解缓存行为。

#### Scenario: 缓存命中与失效可验证
- 通过测试断言方法调用次数/缓存内容

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** springboot-cache
把“key/condition/unless/缓存失效/sync 防击穿/过期策略”写成可断言主线，并绑定默认 Lab 入口。

#### Scenario: Guide 主线可作为导航图
- Guide 已补齐：Cacheable 基础、CachePut/Evict、key/condition/unless、sync 防击穿、过期与手动 Ticker

#### Scenario: 章节坑点可回归
- 每章至少 1 个可断言坑点，并绑定默认 `*LabTest#method` 作为证据链

## Dependencies

- 与其他模块弱耦合

## Docs & 复现入口

- **Docs Index:** `docs/cache/springboot-cache/README.md`
- **Docs Guide:** `docs/cache/springboot-cache/part-00-guide/108-00-deep-dive-guide.md`
- **Breakpoint Map:** `docs/cache/springboot-cache/part-00-guide/108-02-breakpoint-map.md`
- **Branch Decision Matrix:** `docs/cache/springboot-cache/part-00-guide/108-04-branch-decision-matrix.md`
- **Playbook:** `docs/cache/springboot-cache/appendix/114-90-common-pitfalls.md`
- **Self-check:** `docs/cache/springboot-cache/appendix/115-99-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :springboot-cache -Dtest=BootCacheBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :springboot-cache -Dtest=BootCacheBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBranchMatrixLabTest.java`
- **Lab:** `spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java`
- **Lab (SpEL Key):** `spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheSpelKeyLabTest.java`
- **Exercise:** `spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseSolutionTest.java`
- **Lab（并发/性能：缓存击穿（stampede）可断言复现）：** `spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part02_perf_concurrency/BootCacheStampedeProtectionLabTest.java`
- **Book 专题页（方法论与样板索引）：** `docs/book/performance-and-concurrency.md`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；cache 示例集中在 `com.learning.springboot.bootcache.part01_cache`
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_cache`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs），并在 `part01_cache` 内提供 `ManualTicker`

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（缓存击穿（stampede）可断言复现）
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601071034_all_modules_docs_ag_contract](../../history/2026-01/202601071034_all_modules_docs_ag_contract/) - ✅ 已执行：全模块 docs 章节结构整理（A–G 结构 + 对应 Lab/Test 入口块）；后续不再推荐 A–G 作为写作规范/闸门
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用
- [202601062024_springboot_modules_teaching_rollout](../../history/2026-01/202601062024_springboot_modules_teaching_rollout/) - ✅ 已执行：docs/README 章节链接 SSOT 化 + guide/appendix 可跑入口块补齐 + 补齐 min-labs=2 + 自检闸门覆盖
