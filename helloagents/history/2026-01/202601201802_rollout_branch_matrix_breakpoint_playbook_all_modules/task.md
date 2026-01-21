# 任务清单（Task）

> 方案类型：Standard Development（以 springboot-web-mvc 为模板推广到全模块）

## 0. 对齐目标：让所有模块“长得像 web-mvc / beans”

- [√] 定义统一交付物清单（每个模块都必须具备）：
  - Branch Matrix：关键分支最小可运行入口（JUnit Platform Suite）
  - Breakpoint Map：断点/Watchpoint 地图（可复制调试路径）
  - Branch Decision Matrix：关键分支矩阵（表格：条件→期望→复现→观察点）
  - Common Pitfalls Playbook：排障 playbook（症状→复现→证据→决策→修复）
  - Self-check：自检清单（必须提供“从 Book Matrix/Branch Matrix 进入”的路径）
  - 模块 README：模块入口页（从这里开始 + 进阶入口 + 可跑入口 + 排坑与自检）
  - Book 主线页：统一“进阶入口”引用以上三件套
  - Knowledge Base：模块页与 Book 主线一致（SSOT）
- [√] 明确 Branch Matrix 分层规则：
  - Book Matrix：主线最小集合（已全量存在）
  - Branch Matrix：关键分支最小集合（本轮新增）
  - Debug/Trace 类 Lab：更细粒度的证据链入口（可被 Branch Matrix 引用，但不强制全部纳入）
- [√] 明确 Suite 聚合可见性策略（必须写入实现约束中）：
  - 默认：Suite 与被选 `*LabTest` 同 package（兼容 package-private）
  - 若跨 package 需要聚合：拆分多个 Branch Matrix（按 part/package）优先；提升 `public` 仅作为最后手段
- [√] 固化文档模板（以 web-mvc / beans 为“模板真源”）：
  - Breakpoint Map 固定章节结构（运行方式/入口断点/关键分支断点/Watchpoints/快速自检）
  - Branch Decision Matrix 固定表头（分支/触发条件/期望行为/复现入口/观察点/排障提示）
  - Playbook 固定流程（Symptoms → Repro → Evidence → Decision → Fix → Verify）
  - README 固定块（建议新增：进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口）

## 1. Boot 模块：逐模块任务（每个模块都按同一骨架推进）

### 1.1 springboot-basics（对齐：profiles/override 分支 + 配置优先级）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`（必要时补充 1 个“默认分支”对照）
- [√] Breakpoint Map：新增 `docs/basics/springboot-basics/part-00-guide/004-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/basics/springboot-basics/part-00-guide/004-04-branch-decision-matrix.md`
- [√] Playbook：补齐 `docs/basics/springboot-basics/appendix/007-90-common-pitfalls.md` 的排障结构块，并在 `docs/basics/springboot-basics/appendix/008-99-self-check.md` 增加“从 Branch Matrix 进入”的自检入口
- [√] 模块 README：更新 `docs/basics/springboot-basics/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/002-boot-basics-mainline.md` + `helloagents/wiki/modules/springboot-basics.md`
- [√] 验证：`mvn -q -pl :springboot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

### 1.2 springboot-web-mvc（对齐：作为模板模块，补齐“统一模板”对齐项）

- [√] Branch Matrix：保留现有 `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcErrorBranchMatrixLabTest.java` 作为 Error/DispatcherType 关键分支入口
- [√] Branch Decision Matrix：新增 `docs/web-mvc/springboot-web-mvc/part-00-guide/064-04-branch-decision-matrix.md`（列出至少 3 个可复现关键分支，并引用现有/新增入口）
- [√] Breakpoint Map：对齐/增强 `docs/web-mvc/springboot-web-mvc/part-00-guide/066-02-breakpoint-map.md` 到统一模板（确保包含 Watchpoints 与“证据收集清单”）
- [√] Playbook：对齐 `docs/web-mvc/springboot-web-mvc/appendix/082-90-common-pitfalls.md` 与 `docs/web-mvc/springboot-web-mvc/appendix/083-99-self-check.md` 到统一结构
- [√] 模块 README：更新 `docs/web-mvc/springboot-web-mvc/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/062-webmvc-mainline.md` + `helloagents/wiki/modules/springboot-web-mvc.md`
- [√] 验证：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`

### 1.3 springboot-data-jpa（对齐：entity state / persistence context / merge-detach / debug-sql）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootDataJpaMergeAndDetachLabTest` / `BootDataJpaDebugSqlLabTest`
- [√] Breakpoint Map：新增 `docs/data-jpa/springboot-data-jpa/part-00-guide/096-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/data-jpa/springboot-data-jpa/part-00-guide/096-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/data-jpa/springboot-data-jpa/appendix/104-90-common-pitfalls.md` + `docs/data-jpa/springboot-data-jpa/appendix/105-99-self-check.md`
- [√] 模块 README：更新 `docs/data-jpa/springboot-data-jpa/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/094-data-jpa-mainline.md` + `helloagents/wiki/modules/springboot-data-jpa.md`
- [√] 验证：`mvn -q -pl :springboot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

### 1.4 springboot-cache（对齐：key/condition/unless + stampede + spel）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootCacheSpelKeyLabTest`（必要时补充 key/condition/unless 对照）
- [√] Breakpoint Map：新增 `docs/cache/springboot-cache/part-00-guide/108-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/cache/springboot-cache/part-00-guide/108-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/cache/springboot-cache/appendix/114-90-common-pitfalls.md` + `docs/cache/springboot-cache/appendix/115-99-self-check.md`
- [√] 模块 README：更新 `docs/cache/springboot-cache/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/106-cache-mainline.md` + `helloagents/wiki/modules/springboot-cache.md`
- [√] 验证：`mvn -q -pl :springboot-cache -Dtest=BootCacheBranchMatrixLabTest test`

### 1.5 springboot-async-scheduling（对齐：@Async / executor / 异常传播 / scheduling）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingSchedulingLabTest`
- [√] Breakpoint Map：新增 `docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/async-scheduling/springboot-async-scheduling/appendix/124-90-common-pitfalls.md` + `docs/async-scheduling/springboot-async-scheduling/appendix/125-99-self-check.md`
- [√] 模块 README：更新 `docs/async-scheduling/springboot-async-scheduling/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/116-async-scheduling-mainline.md` + `helloagents/wiki/modules/springboot-async-scheduling.md`
- [√] 验证：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`

### 1.6 springboot-web-client（对齐：RestClient vs WebClient / filter order / timeout-retry）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootWebClientRestClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`
- [√] Breakpoint Map：新增 `docs/web-client/springboot-web-client/part-00-guide/174-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/web-client/springboot-web-client/part-00-guide/174-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/web-client/springboot-web-client/appendix/180-90-common-pitfalls.md` + `docs/web-client/springboot-web-client/appendix/181-99-self-check.md`
- [√] 模块 README：更新 `docs/web-client/springboot-web-client/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/172-web-client-mainline.md` + `helloagents/wiki/modules/springboot-web-client.md`
- [√] 验证：`mvn -q -pl :springboot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`

### 1.7 springboot-actuator（对齐：exposure/安全边界/端点可见性）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootActuatorExposureOverrideLabTest`
- [√] Breakpoint Map：新增 `docs/actuator/springboot-actuator/part-00-guide/168-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/actuator/springboot-actuator/part-00-guide/168-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/actuator/springboot-actuator/appendix/170-90-common-pitfalls.md` + `docs/actuator/springboot-actuator/appendix/171-99-self-check.md`
- [√] 模块 README：更新 `docs/actuator/springboot-actuator/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/166-actuator-observability-mainline.md` + `helloagents/wiki/modules/springboot-actuator.md`
- [√] 验证：`mvn -q -pl :springboot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

### 1.8 springboot-security（对齐：multi filter chain / profile / jwt / mvc boundary）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootSecurityMultiFilterChainOrderLabTest` / `BootSecurityDevProfileLabTest`
- [√] Breakpoint Map：新增 `docs/security/springboot-security/part-00-guide/086-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/security/springboot-security/part-00-guide/086-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/security/springboot-security/appendix/092-90-common-pitfalls.md` + `docs/security/springboot-security/appendix/093-99-self-check.md`
- [√] 模块 README：更新 `docs/security/springboot-security/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/084-security-mainline.md` + `helloagents/wiki/modules/springboot-security.md`
- [√] 验证：`mvn -q -pl :springboot-security -Dtest=BootSecurityBranchMatrixLabTest test`

### 1.9 springboot-testing（对齐：slice/mock/springboot-vs-webmvc）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootTestingMockBeanLabTest` / `GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest`
- [√] Breakpoint Map：新增 `docs/testing/springboot-testing/part-00-guide/184-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/testing/springboot-testing/part-00-guide/184-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/testing/springboot-testing/appendix/186-90-common-pitfalls.md` + `docs/testing/springboot-testing/appendix/187-99-self-check.md`
- [√] 模块 README：更新 `docs/testing/springboot-testing/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/182-testing-mainline.md` + `helloagents/wiki/modules/springboot-testing.md`
- [√] 验证：`mvn -q -pl :springboot-testing -Dtest=BootTestingBranchMatrixLabTest test`

### 1.10 springboot-business-case（对齐：真实业务链路 + 事务边界 + 可观测性证据）

- [√] Branch Matrix：新增 `spring-boot-modules/springboot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBranchMatrixLabTest.java`
  - 选入分支最小集合：`BootBusinessCaseServiceLabTest`（必要时补充成功/失败路径对照）
- [√] Breakpoint Map：新增 `docs/business-case/springboot-business-case/part-00-guide/190-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/business-case/springboot-business-case/part-00-guide/190-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/business-case/springboot-business-case/appendix/192-90-common-pitfalls.md` + `docs/business-case/springboot-business-case/appendix/193-99-self-check.md`
- [√] 模块 README：更新 `docs/business-case/springboot-business-case/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/188-business-case.md` + `helloagents/wiki/modules/springboot-business-case.md`
- [√] 验证：`mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

## 2. Spring Core 模块：逐模块任务（多 part 模块优先拆分多个 Branch Matrix）

### 2.1 spring-core-beans（对齐：IoC 分支 + 内部机制分支 + 排障路径）

- [√] Branch Matrix（拆分优先）：新增至少 2 个入口（按 package/part）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInternalsBranchMatrixLabTest.java`
  - 选入分支最小集合（示例）：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- [√] Breakpoint Map：对齐/增强 `docs/beans/spring-core-beans/part-00-guide/013-02-breakpoint-map.md` 到统一模板
- [√] Branch Decision Matrix：新增 `docs/beans/spring-core-beans/part-00-guide/011-04-branch-decision-matrix.md`（引用上面的多个 Branch Matrix 入口）
- [√] Playbook：对齐 `docs/beans/spring-core-beans/appendix/025-90-common-pitfalls.md` + `docs/beans/spring-core-beans/appendix/026-99-self-check.md`
- [√] 模块 README：更新 `docs/beans/spring-core-beans/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/009-ioc-container-mainline.md` + `helloagents/wiki/modules/spring-core-beans.md`
- [√] 验证：分别 spot-check 两个入口（`-Dtest=...`），再跑全仓 `mvn -q test`

### 2.2 spring-core-aop（对齐：proxy fundamentals / autoproxy / stacking 分支）

- [√] Branch Matrix（拆分优先）：新增 3 个入口（按 part/package）：
  - `spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyBranchMatrixLabTest.java`
  - `spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyBranchMatrixLabTest.java`
  - `spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopStackingBranchMatrixLabTest.java`
  - 选入分支最小集合（示例）：`SpringCoreAopExposeProxyLabTest` / `SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopRealWorldStackingLabTest`
- [√] Breakpoint Map：新增 `docs/aop/spring-core-aop/part-00-guide/029-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/aop/spring-core-aop/part-00-guide/029-04-branch-decision-matrix.md`（引用多个 Branch Matrix 入口）
- [√] Playbook：对齐 `docs/aop/spring-core-aop/appendix/040-90-common-pitfalls.md` + `docs/aop/spring-core-aop/appendix/041-99-self-check.md`
- [√] 模块 README：更新 `docs/aop/spring-core-aop/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/027-aop-proxy-mainline.md` + `helloagents/wiki/modules/spring-core-aop.md`
- [√] 验证：逐入口 spot-check + 全仓 `mvn -q test`

### 2.3 spring-core-aop-weaving（对齐：LTW/CTW 环境分支 + 运行方式可复现）

- [√] Branch Matrix（按运行环境拆分）：新增 2 个入口，并用 assume/skip 保护“跑到错误 surefire execution 时不误失败”：
  - `spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwBranchMatrixLabTest.java`
  - `spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwBranchMatrixLabTest.java`
- [√] Breakpoint Map：新增 `docs/aop/spring-core-aop-weaving/part-00-guide/044-02-breakpoint-map.md`（必须写清：LTW 需要 `-javaagent`；CTW 要求无 `-javaagent`）
- [√] Branch Decision Matrix：新增 `docs/aop/spring-core-aop-weaving/part-00-guide/044-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/aop/spring-core-aop-weaving/appendix/049-90-common-pitfalls.md` + `docs/aop/spring-core-aop-weaving/appendix/050-99-self-check.md`
- [√] 模块 README：更新 `docs/aop/spring-core-aop-weaving/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（LTW/CTW + Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/042-aop-weaving-mainline.md` + `helloagents/wiki/modules/spring-core-aop-weaving.md`
- [√] 验证：分别执行 `-Dtest=AspectjLtwBranchMatrixLabTest` 与 `-Dtest=AspectjCtwBranchMatrixLabTest`，并保证全仓 `mvn -q test` 通过

### 2.4 spring-core-tx（对齐：rollback rules / propagation / self-invocation pitfall）

- [√] Branch Matrix（必要时拆分）：新增至少 1 个入口：
  - `spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBranchMatrixLabTest.java`
  - 选入分支最小集合：`SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxPropagationMatrixLabTest`
  - 若要覆盖 appendix pitfall：新增 `spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxPitfallsBranchMatrixLabTest.java`（同包聚合）
- [√] Breakpoint Map：新增 `docs/tx/spring-core-tx/part-00-guide/053-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/tx/spring-core-tx/part-00-guide/053-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/tx/spring-core-tx/appendix/060-90-common-pitfalls.md` + `docs/tx/spring-core-tx/appendix/061-99-self-check.md`
- [√] 模块 README：更新 `docs/tx/spring-core-tx/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/051-tx-mainline.md` + `helloagents/wiki/modules/spring-core-tx.md`
- [√] 验证：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`（如有 pitfall 入口，追加 spot-check）

### 2.5 spring-core-events（对齐：sync vs async multicaster / transactional events）

- [√] Branch Matrix（拆分优先）：新增 2 个入口：
  - `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBasicsBranchMatrixLabTest.java`
  - `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsAsyncTransactionalBranchMatrixLabTest.java`
- [√] Breakpoint Map：新增 `docs/events/spring-core-events/part-00-guide/128-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/events/spring-core-events/part-00-guide/128-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/events/spring-core-events/appendix/136-90-common-pitfalls.md` + `docs/events/spring-core-events/appendix/137-99-self-check.md`
- [√] 模块 README：更新 `docs/events/spring-core-events/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/126-events-mainline.md` + `helloagents/wiki/modules/spring-core-events.md`
- [√] 验证：逐入口 spot-check + 全仓 `mvn -q test`

### 2.6 spring-core-profiles（对齐：profile activation / precedence / conditional）

- [√] Branch Matrix：新增 `spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBranchMatrixLabTest.java`
  - 选入分支最小集合：`SpringCoreProfilesProfilePrecedenceLabTest`
- [√] Breakpoint Map：新增 `docs/profiles/spring-core-profiles/part-00-guide/151-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/profiles/spring-core-profiles/part-00-guide/151-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/profiles/spring-core-profiles/appendix/153-90-common-pitfalls.md` + `docs/profiles/spring-core-profiles/appendix/154-99-self-check.md`
- [√] 模块 README：更新 `docs/profiles/spring-core-profiles/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/149-profiles-mainline.md` + `helloagents/wiki/modules/spring-core-profiles.md`
- [√] 验证：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`

### 2.7 spring-core-resources（对齐：classpath patterns / jar-vs-fs / encoding）

- [√] Branch Matrix：新增 `spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBranchMatrixLabTest.java`
  - 选入分支最小集合：`SpringCoreResourcesMechanicsLabTest`
- [√] Breakpoint Map：新增 `docs/resources/spring-core-resources/part-00-guide/140-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/resources/spring-core-resources/part-00-guide/140-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/resources/spring-core-resources/appendix/147-90-common-pitfalls.md` + `docs/resources/spring-core-resources/appendix/148-99-self-check.md`
- [√] 模块 README：更新 `docs/resources/spring-core-resources/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/138-resources-mainline.md` + `helloagents/wiki/modules/spring-core-resources.md`
- [√] 验证：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`

### 2.8 spring-core-validation（对齐：programmatic vs method validation / groups / custom constraint）

- [√] Branch Matrix：新增 `spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBranchMatrixLabTest.java`
  - 选入分支最小集合：`SpringCoreValidationMechanicsLabTest`
- [√] Breakpoint Map：新增 `docs/validation/spring-core-validation/part-00-guide/157-02-breakpoint-map.md`
- [√] Branch Decision Matrix：新增 `docs/validation/spring-core-validation/part-00-guide/157-04-branch-decision-matrix.md`
- [√] Playbook：对齐 `docs/validation/spring-core-validation/appendix/164-90-common-pitfalls.md` + `docs/validation/spring-core-validation/appendix/165-99-self-check.md`
- [√] 模块 README：更新 `docs/validation/spring-core-validation/README.md`，补齐“进阶入口：断点地图 / 关键分支矩阵 / 排障 playbook / 自检入口”，并补充对应可跑命令（Book/Branch Matrix）
- [√] 入口联动：更新 `docs/book/155-validation-mainline.md` + `helloagents/wiki/modules/spring-core-validation.md`
- [√] 验证：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`

## 3. 全局入口与一致性（跨模块统一收敛）

- [√] Book 主线 18 章：统一新增“进阶入口：关键分支矩阵 / 断点地图 / 排障 Playbook”（以 `docs/book/062-webmvc-mainline.md` 为模板），逐文件落地：
  - `docs/book/002-boot-basics-mainline.md`
  - `docs/book/009-ioc-container-mainline.md`
  - `docs/book/027-aop-proxy-mainline.md`
  - `docs/book/042-aop-weaving-mainline.md`
  - `docs/book/051-tx-mainline.md`
  - `docs/book/062-webmvc-mainline.md`
  - `docs/book/084-security-mainline.md`
  - `docs/book/094-data-jpa-mainline.md`
  - `docs/book/106-cache-mainline.md`
  - `docs/book/116-async-scheduling-mainline.md`
  - `docs/book/126-events-mainline.md`
  - `docs/book/138-resources-mainline.md`
  - `docs/book/149-profiles-mainline.md`
  - `docs/book/155-validation-mainline.md`
  - `docs/book/166-actuator-observability-mainline.md`
  - `docs/book/172-web-client-mainline.md`
  - `docs/book/182-testing-mainline.md`
  - `docs/book/188-business-case.md`
- [√] Knowledge Base（SSOT）：逐模块同步新增入口（Branch Matrix / Breakpoint Map / Playbook），逐文件落地：
  - `helloagents/wiki/modules/springboot-basics.md`
  - `helloagents/wiki/modules/springboot-web-mvc.md`
  - `helloagents/wiki/modules/springboot-data-jpa.md`
  - `helloagents/wiki/modules/springboot-cache.md`
  - `helloagents/wiki/modules/springboot-async-scheduling.md`
  - `helloagents/wiki/modules/springboot-web-client.md`
  - `helloagents/wiki/modules/springboot-actuator.md`
  - `helloagents/wiki/modules/springboot-security.md`
  - `helloagents/wiki/modules/springboot-testing.md`
  - `helloagents/wiki/modules/springboot-business-case.md`
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/wiki/modules/spring-core-aop.md`
  - `helloagents/wiki/modules/spring-core-aop-weaving.md`
  - `helloagents/wiki/modules/spring-core-tx.md`
  - `helloagents/wiki/modules/spring-core-events.md`
  - `helloagents/wiki/modules/spring-core-profiles.md`
  - `helloagents/wiki/modules/spring-core-resources.md`
  - `helloagents/wiki/modules/spring-core-validation.md`

## 4. 质量验证（必须跑通）

- [√] 逐模块 spot-check：按上面每个模块的 `mvn -q -pl ... -Dtest=... test` 执行（至少覆盖所有新增 Branch Matrix 入口）。
- [√] 全仓回归：`mvn -q test`
- [√] 文档：`python3 scripts/generate-book-labs-index.py` + `bash scripts/check-docs.sh` + `bash scripts/docs-site-build.sh`
- [√] 安全与一致性自检：确认未引入新的敏感信息、未破坏模块边界、文档入口与测试入口一致（SSOT）

## 5. 收尾（方案包生命周期）

- [√] 更新 `task.md` 勾选状态（[ ] → [√]/[-]/[X]）。
- [√] 同步 `helloagents/CHANGELOG.md`（记录“全模块关键分支矩阵 + 断点地图 + 排障 playbook 推广”）。
- [√] 迁移方案包到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`。
