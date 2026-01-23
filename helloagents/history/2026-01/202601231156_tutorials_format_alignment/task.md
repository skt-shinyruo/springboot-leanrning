# Task List: tutorials 风格对齐改造（全模块深挖标准化）

Directory: `helloagents/history/2026-01/202601231156_tutorials_format_alignment/`

---

## 1. Maven 结构与命名对齐（核心改造）

- [√] 1.1 产出模块命名映射表（`springboot-*` → `spring-boot-*`），并确认 13 个 Boot 模块的最终 artifactId（用于全仓库替换与回归闸门）
- [√] 1.2 调整 `spring-boot-modules/pom.xml`：modules 列表与目录名对齐映射表（保证聚合可构建）
- [√] 1.3 调整 `spring-core-modules/pom.xml`：确认 core 模块 modules 列表与目录一致（如无需重命名，则仅做 parent 分层准备）
- [√] 1.4 批量调整 Boot 子模块 `pom.xml`：parent 指向 `spring-boot-modules`，修正 `relativePath`，并更新 artifactId（与目录一致）
- [√] 1.5 批量调整 Core 子模块 `pom.xml`：parent 指向 `spring-core-modules`，修正 `relativePath`（artifactId 保持不变或按规则最小调整）
- [√] 1.6 执行一次全量回归闸门：`mvn -q test`（失败则停止进入下一阶段，先修复引用/构建问题）

## 2. 全仓库引用同步（docs / scripts / wiki）

- [√] 2.1 批量更新 `scripts/**` 中引用的 artifactId（`-pl :spring-boot-*` → `-pl :spring-boot-*`），并验证脚本可用
- [√] 2.2 批量更新 `docs/**` 中所有可运行命令与模块链接（重点：`mvn -q -pl :<artifactId>` 与模块路径）
- [ ] 2.3 批量更新各模块 `README.md`：命令、docs 路径、测试入口类名保持一致
- [ ] 2.4 更新 `helloagents/wiki/**`：模块页与学习路线中的 artifactId/路径引用与新命名一致
- [√] 2.5 回归闸门：`mvn -q test`（确保“改名 + 引用同步”不引入红测）

## 3. 全模块深挖标准化（A–E）

> 目标：所有模块都满足同一套“可导航 + 可验证 + 可断点 + 可排障 + 可并发复现”的最小闭环。

> 说明：
> 1) “深挖契约”以 `helloagents/project.md` 的 Chapter Contract 为内容标准，以 `docs/book/debugger-pack.md` 与 `docs/book/performance-and-concurrency.md` 为全局参考入口。  
> 2) Boot 模块在第 1 阶段会从 `springboot-*` 重命名为 `spring-boot-*`；本阶段涉及的 `mvn -q -pl :<artifactId>` 命令以改名后的 artifactId 为准。  
> 3) 下面任务把“每个模块必须具备的文件/入口”显式列出来：执行时以这些入口为验收标准；若文件已存在则做内容对齐与断链修复，若缺失则按同目录命名风格补齐。

### 3.0 全局基线与自动化检查（先做一次）

- [√] 3.0.1 固化“模块深挖最小闭环清单”：在 `docs/book/debugger-pack.md` 补充每模块必须具备的 Doc/Test/Perf 入口与验收口径
- [√] 3.0.2 新增 `scripts/audit-module-deep-dive.sh`：扫描 22 个模块，输出缺失项与入口清单（用于 3.1/3.2/3.3 的逐模块执行）
- [√] 3.0.3 生成一份可追踪基线：`helloagents/history/2026-01/202601231156_tutorials_format_alignment/module-deep-dive-audit.md`（记录每模块 docs/test/perf 的入口文件路径与命令）

### 3.1 Docs 深挖闭环（22 个模块逐一验收）

> 每个模块至少要能从 docs 入口做到：先读 deep-dive → 跑一个可验证入口（Book/Branch Matrix）→ 用断点定位关键分支 → 遇到问题能按 playbook 排障 → 用 self-check 自测。

- [ ] 3.1.1 `springboot-basics` docs 闭环验收与对齐
  - deep-dive：`docs/basics/springboot-basics/part-00-guide/004-00-deep-dive-guide.md`
  - call-chain：`docs/basics/springboot-basics/part-00-guide/004-01-springapplication-run-call-chain.md`
  - breakpoint-map：`docs/basics/springboot-basics/part-00-guide/004-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/basics/springboot-basics/part-00-guide/004-04-branch-decision-matrix.md`
  - pitfalls：`docs/basics/springboot-basics/appendix/007-90-common-pitfalls.md`
  - self-check：`docs/basics/springboot-basics/appendix/008-99-self-check.md`
- [ ] 3.1.2 `springboot-autoconfiguration` docs 闭环验收与对齐
  - deep-dive：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-00-deep-dive-guide.md`
  - call-chain：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-01-autoconfiguration-import-call-chain.md`
  - breakpoint-map：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-04-branch-decision-matrix.md`
  - pitfalls：`docs/autoconfig/springboot-autoconfiguration/appendix/197-90-common-pitfalls.md`
  - self-check：`docs/autoconfig/springboot-autoconfiguration/appendix/198-99-self-check.md`
- [ ] 3.1.3 `springboot-web-mvc` docs 闭环验收与对齐
  - deep-dive：`docs/web-mvc/springboot-web-mvc/part-00-guide/064-00-deep-dive-guide.md`
  - call-chain：`docs/web-mvc/springboot-web-mvc/part-00-guide/064-01-webmvc-request-call-chain.md`
  - breakpoint-map：`docs/web-mvc/springboot-web-mvc/part-00-guide/066-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/web-mvc/springboot-web-mvc/part-00-guide/064-04-branch-decision-matrix.md`
  - pitfalls：`docs/web-mvc/springboot-web-mvc/appendix/082-90-common-pitfalls.md`
  - self-check：`docs/web-mvc/springboot-web-mvc/appendix/083-99-self-check.md`
- [ ] 3.1.4 `springboot-data-jpa` docs 闭环验收与对齐
  - deep-dive：`docs/data-jpa/springboot-data-jpa/part-00-guide/096-00-deep-dive-guide.md`
  - call-chain：`docs/data-jpa/springboot-data-jpa/part-00-guide/096-01-repository-call-chain.md`
  - breakpoint-map：`docs/data-jpa/springboot-data-jpa/part-00-guide/096-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/data-jpa/springboot-data-jpa/part-00-guide/096-04-branch-decision-matrix.md`
  - pitfalls：`docs/data-jpa/springboot-data-jpa/appendix/104-90-common-pitfalls.md`
  - self-check：`docs/data-jpa/springboot-data-jpa/appendix/105-99-self-check.md`
- [ ] 3.1.5 `springboot-actuator` docs 闭环验收与对齐
  - deep-dive：`docs/actuator/springboot-actuator/part-00-guide/168-00-deep-dive-guide.md`
  - call-chain：`docs/actuator/springboot-actuator/part-00-guide/168-01-actuator-endpoint-call-chain.md`
  - breakpoint-map：`docs/actuator/springboot-actuator/part-00-guide/168-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/actuator/springboot-actuator/part-00-guide/168-04-branch-decision-matrix.md`
  - pitfalls：`docs/actuator/springboot-actuator/appendix/170-90-common-pitfalls.md`
  - self-check：`docs/actuator/springboot-actuator/appendix/171-99-self-check.md`
- [ ] 3.1.6 `springboot-testing` docs 闭环验收与对齐
  - deep-dive：`docs/testing/springboot-testing/part-00-guide/184-00-deep-dive-guide.md`
  - call-chain：`docs/testing/springboot-testing/part-00-guide/184-01-test-bootstrap-and-slicing-call-chain.md`
  - breakpoint-map：`docs/testing/springboot-testing/part-00-guide/184-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/testing/springboot-testing/part-00-guide/184-04-branch-decision-matrix.md`
  - pitfalls：`docs/testing/springboot-testing/appendix/186-90-common-pitfalls.md`
  - self-check：`docs/testing/springboot-testing/appendix/187-99-self-check.md`
- [ ] 3.1.7 `springboot-business-case` docs 闭环验收与对齐
  - deep-dive：`docs/business-case/springboot-business-case/part-00-guide/190-00-deep-dive-guide.md`
  - call-chain：`docs/business-case/springboot-business-case/part-00-guide/190-01-business-request-call-chain.md`
  - breakpoint-map：`docs/business-case/springboot-business-case/part-00-guide/190-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/business-case/springboot-business-case/part-00-guide/190-04-branch-decision-matrix.md`
  - pitfalls：`docs/business-case/springboot-business-case/appendix/192-90-common-pitfalls.md`
  - self-check：`docs/business-case/springboot-business-case/appendix/193-99-self-check.md`
- [ ] 3.1.8 `springboot-security` docs 闭环验收与对齐
  - deep-dive：`docs/security/springboot-security/part-00-guide/086-00-deep-dive-guide.md`
  - call-chain：`docs/security/springboot-security/part-00-guide/086-01-security-filterchain-call-chain.md`
  - breakpoint-map：`docs/security/springboot-security/part-00-guide/086-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/security/springboot-security/part-00-guide/086-04-branch-decision-matrix.md`
  - pitfalls：`docs/security/springboot-security/appendix/092-90-common-pitfalls.md`
  - self-check：`docs/security/springboot-security/appendix/093-99-self-check.md`
- [ ] 3.1.9 `springboot-observability` docs 闭环验收与对齐
  - deep-dive：`docs/observability/springboot-observability/part-00-guide/205-00-deep-dive-guide.md`
  - call-chain：`docs/observability/springboot-observability/part-00-guide/205-01-http-observation-call-chain.md`
  - breakpoint-map：`docs/observability/springboot-observability/part-00-guide/205-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/observability/springboot-observability/part-00-guide/205-04-branch-decision-matrix.md`
  - pitfalls：`docs/observability/springboot-observability/appendix/207-90-common-pitfalls.md`
  - self-check：`docs/observability/springboot-observability/appendix/208-99-self-check.md`
- [ ] 3.1.10 `springboot-logging` docs 闭环验收与对齐
  - deep-dive：`docs/logging/springboot-logging/part-00-guide/200-00-deep-dive-guide.md`
  - call-chain：`docs/logging/springboot-logging/part-00-guide/200-01-logging-call-chain.md`
  - breakpoint-map：`docs/logging/springboot-logging/part-00-guide/200-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/logging/springboot-logging/part-00-guide/200-04-branch-decision-matrix.md`
  - pitfalls：`docs/logging/springboot-logging/appendix/202-90-common-pitfalls.md`
  - self-check：`docs/logging/springboot-logging/appendix/203-99-self-check.md`
- [ ] 3.1.11 `springboot-web-client` docs 闭环验收与对齐
  - deep-dive：`docs/web-client/springboot-web-client/part-00-guide/174-00-deep-dive-guide.md`
  - call-chain：`docs/web-client/springboot-web-client/part-00-guide/174-01-webclient-call-chain.md`
  - breakpoint-map：`docs/web-client/springboot-web-client/part-00-guide/174-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/web-client/springboot-web-client/part-00-guide/174-04-branch-decision-matrix.md`
  - pitfalls：`docs/web-client/springboot-web-client/appendix/180-90-common-pitfalls.md`
  - self-check：`docs/web-client/springboot-web-client/appendix/181-99-self-check.md`
- [ ] 3.1.12 `springboot-async-scheduling` docs 闭环验收与对齐
  - deep-dive：`docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-00-deep-dive-guide.md`
  - call-chain：`docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-01-async-and-scheduling-call-chain.md`
  - breakpoint-map：`docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-04-branch-decision-matrix.md`
  - pitfalls：`docs/async-scheduling/springboot-async-scheduling/appendix/124-90-common-pitfalls.md`
  - self-check：`docs/async-scheduling/springboot-async-scheduling/appendix/125-99-self-check.md`
- [ ] 3.1.13 `springboot-cache` docs 闭环验收与对齐
  - deep-dive：`docs/cache/springboot-cache/part-00-guide/108-00-deep-dive-guide.md`
  - call-chain：`docs/cache/springboot-cache/part-00-guide/108-01-cache-interceptor-call-chain.md`
  - breakpoint-map：`docs/cache/springboot-cache/part-00-guide/108-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/cache/springboot-cache/part-00-guide/108-04-branch-decision-matrix.md`
  - pitfalls：`docs/cache/springboot-cache/appendix/114-90-common-pitfalls.md`
  - self-check：`docs/cache/springboot-cache/appendix/115-99-self-check.md`

- [ ] 3.1.14 `spring-core-beans` docs 闭环验收与对齐
  - deep-dive：`docs/beans/spring-core-beans/part-00-guide/011-00-deep-dive-guide.md`
  - call-chain：`docs/beans/spring-core-beans/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
  - breakpoint-map：`docs/beans/spring-core-beans/part-00-guide/013-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/beans/spring-core-beans/part-00-guide/011-04-branch-decision-matrix.md`
  - pitfalls：`docs/beans/spring-core-beans/appendix/025-90-common-pitfalls.md`
  - self-check：`docs/beans/spring-core-beans/appendix/026-99-self-check.md`
- [ ] 3.1.15 `spring-core-spel` docs 闭环验收与对齐
  - deep-dive：`docs/spel/spring-core-spel/part-00-guide/210-00-deep-dive-guide.md`
  - call-chain：`docs/spel/spring-core-spel/part-00-guide/210-01-spel-call-chain.md`
  - breakpoint-map：`docs/spel/spring-core-spel/part-00-guide/210-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/spel/spring-core-spel/part-00-guide/210-04-branch-decision-matrix.md`
  - pitfalls：`docs/spel/spring-core-spel/appendix/212-90-common-pitfalls.md`
  - self-check：`docs/spel/spring-core-spel/appendix/213-99-self-check.md`
- [ ] 3.1.16 `spring-core-aop` docs 闭环验收与对齐
  - deep-dive：`docs/aop/spring-core-aop/part-00-guide/029-00-deep-dive-guide.md`
  - call-chain：`docs/aop/spring-core-aop/part-00-guide/029-01-aop-invocation-call-chain.md`
  - breakpoint-map：`docs/aop/spring-core-aop/part-00-guide/029-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/aop/spring-core-aop/part-00-guide/029-04-branch-decision-matrix.md`
  - pitfalls：`docs/aop/spring-core-aop/appendix/040-90-common-pitfalls.md`
  - self-check：`docs/aop/spring-core-aop/appendix/041-99-self-check.md`
- [ ] 3.1.17 `spring-core-aop-weaving` docs 闭环验收与对齐
  - deep-dive：`docs/aop/spring-core-aop-weaving/part-00-guide/044-00-deep-dive-guide.md`
  - call-chain：`docs/aop/spring-core-aop-weaving/part-00-guide/044-01-aspectj-weaving-call-chain.md`
  - breakpoint-map：`docs/aop/spring-core-aop-weaving/part-00-guide/044-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/aop/spring-core-aop-weaving/part-00-guide/044-04-branch-decision-matrix.md`
  - pitfalls：`docs/aop/spring-core-aop-weaving/appendix/049-90-common-pitfalls.md`
  - self-check：`docs/aop/spring-core-aop-weaving/appendix/050-99-self-check.md`
- [ ] 3.1.18 `spring-core-events` docs 闭环验收与对齐
  - deep-dive：`docs/events/spring-core-events/part-00-guide/128-00-deep-dive-guide.md`
  - call-chain：`docs/events/spring-core-events/part-00-guide/128-01-events-call-chain.md`
  - breakpoint-map：`docs/events/spring-core-events/part-00-guide/128-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/events/spring-core-events/part-00-guide/128-04-branch-decision-matrix.md`
  - pitfalls：`docs/events/spring-core-events/appendix/136-90-common-pitfalls.md`
  - self-check：`docs/events/spring-core-events/appendix/137-99-self-check.md`
- [ ] 3.1.19 `spring-core-validation` docs 闭环验收与对齐
  - deep-dive：`docs/validation/spring-core-validation/part-00-guide/157-00-deep-dive-guide.md`
  - call-chain：`docs/validation/spring-core-validation/part-00-guide/157-01-validation-call-chain.md`
  - breakpoint-map：`docs/validation/spring-core-validation/part-00-guide/157-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/validation/spring-core-validation/part-00-guide/157-04-branch-decision-matrix.md`
  - pitfalls：`docs/validation/spring-core-validation/appendix/164-90-common-pitfalls.md`
  - self-check：`docs/validation/spring-core-validation/appendix/165-99-self-check.md`
- [ ] 3.1.20 `spring-core-resources` docs 闭环验收与对齐
  - deep-dive：`docs/resources/spring-core-resources/part-00-guide/140-00-deep-dive-guide.md`
  - call-chain：`docs/resources/spring-core-resources/part-00-guide/140-01-resource-loading-call-chain.md`
  - breakpoint-map：`docs/resources/spring-core-resources/part-00-guide/140-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/resources/spring-core-resources/part-00-guide/140-04-branch-decision-matrix.md`
  - pitfalls：`docs/resources/spring-core-resources/appendix/147-90-common-pitfalls.md`
  - self-check：`docs/resources/spring-core-resources/appendix/148-99-self-check.md`
- [ ] 3.1.21 `spring-core-tx` docs 闭环验收与对齐
  - deep-dive：`docs/tx/spring-core-tx/part-00-guide/053-00-deep-dive-guide.md`
  - call-chain：`docs/tx/spring-core-tx/part-00-guide/053-01-transaction-interceptor-call-chain.md`
  - breakpoint-map：`docs/tx/spring-core-tx/part-00-guide/053-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/tx/spring-core-tx/part-00-guide/053-04-branch-decision-matrix.md`
  - pitfalls：`docs/tx/spring-core-tx/appendix/060-90-common-pitfalls.md`
  - self-check：`docs/tx/spring-core-tx/appendix/061-99-self-check.md`
- [ ] 3.1.22 `spring-core-profiles` docs 闭环验收与对齐
  - deep-dive：`docs/profiles/spring-core-profiles/part-00-guide/151-00-deep-dive-guide.md`
  - call-chain：`docs/profiles/spring-core-profiles/part-00-guide/151-01-profile-activation-call-chain.md`
  - breakpoint-map：`docs/profiles/spring-core-profiles/part-00-guide/151-02-breakpoint-map.md`
  - branch-matrix-doc：`docs/profiles/spring-core-profiles/part-00-guide/151-04-branch-decision-matrix.md`
  - pitfalls：`docs/profiles/spring-core-profiles/appendix/153-90-common-pitfalls.md`
  - self-check：`docs/profiles/spring-core-profiles/appendix/154-99-self-check.md`

### 3.2 Tests 深挖闭环（22 个模块逐一验收）

> 目标：每个模块都至少暴露 4 类测试入口：Book Matrix（主线）、Branch Matrix（关键分支/边界）、Exercise（默认禁用）、ExerciseSolution（参与回归）；并尽量具备 1 个并发/性能可复现实验入口（无耗时阈值断言）。

- [ ] 3.2.1 `springboot-basics` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part02_perf_concurrency/BootBasicsEnvironmentConcurrencyLabTest.java`
- [ ] 3.2.2 `springboot-autoconfiguration` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseSolutionTest.java`
  - perf：缺失 → 见 3.3.1
- [ ] 3.2.3 `springboot-web-mvc` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcErrorBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_perf_concurrency/BootWebMvcRequestScopeIsolationLabTest.java`
- [ ] 3.2.4 `springboot-data-jpa` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part02_perf_concurrency/BootDataJpaEntityManagerConcurrencyLabTest.java`
- [ ] 3.2.5 `springboot-actuator` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part02_perf_concurrency/BootActuatorMetricsConcurrencyLabTest.java`
- [ ] 3.2.6 `springboot-testing` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part02_perf_concurrency/BootTestingTestContextCacheLabTest.java`
- [ ] 3.2.7 `springboot-business-case` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part02_perf_concurrency/BootBusinessCaseConcurrentOrderPlacementLabTest.java`
- [ ] 3.2.8 `springboot-security` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part00_guide/BootSecurityExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part00_guide/BootSecurityExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part02_perf_concurrency/BootSecuritySecurityContextIsolationLabTest.java`
- [ ] 3.2.9 `springboot-observability` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityExerciseSolutionTest.java`
  - perf：缺失 → 见 3.3.3
- [ ] 3.2.10 `springboot-logging` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingExerciseSolutionTest.java`
  - perf：缺失 → 见 3.3.2
- [ ] 3.2.11 `springboot-web-client` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part02_perf_concurrency/BootWebClientRestClientConcurrencyLabTest.java`
- [ ] 3.2.12 `springboot-async-scheduling` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part02_perf_concurrency/BootAsyncSchedulingExecutorSaturationLabTest.java`
- [ ] 3.2.13 `springboot-cache` tests 闭环验收与对齐
  - book：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBookMatrixLabTest.java`
  - branch：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBranchMatrixLabTest.java`
  - exercise：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseTest.java`
  - solution：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseSolutionTest.java`
  - perf：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part02_perf_concurrency/BootCacheStampedeProtectionLabTest.java`

- [ ] 3.2.14 `spring-core-beans` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBookMatrixLabTest.java`
  - branch：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
  - solution：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseSolutionTest.java`
  - perf：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_perf_concurrency/SpringCoreBeansConcurrentGetBeanLabTest.java`
- [ ] 3.2.15 `spring-core-spel` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelBookMatrixLabTest.java`
  - branch：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelExerciseTest.java`
  - solution：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelExerciseSolutionTest.java`
  - perf：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part01_perf_concurrency/SpringCoreSpelConcurrencyLabTest.java`
- [ ] 3.2.16 `spring-core-aop` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopBookMatrixLabTest.java`
  - branch（至少 1 组主入口）：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part00_guide/SpringCoreAopExerciseTest.java`
  - solution：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part00_guide/SpringCoreAopExerciseSolutionTest.java`
  - perf：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_perf_concurrency/SpringCoreAopProxyConcurrencyLabTest.java`
- [ ] 3.2.17 `spring-core-aop-weaving` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjWeavingBookMatrixLabTest.java`
  - branch（至少 1 组主入口）：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseTest.java`
  - solution：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseSolutionTest.java`
  - perf：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_perf_concurrency/AspectjLtwConcurrencyLabTest.java`
- [ ] 3.2.18 `spring-core-events` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBookMatrixLabTest.java`
  - branch（至少 1 组主入口）：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBasicsBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseTest.java`
  - solution：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseSolutionTest.java`
  - perf：缺失 → 见 3.3.4
- [ ] 3.2.19 `spring-core-validation` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBookMatrixLabTest.java`
  - branch：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseTest.java`
  - solution：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseSolutionTest.java`
  - perf：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part02_perf_concurrency/SpringCoreValidationValidatorConcurrencyLabTest.java`
- [ ] 3.2.20 `spring-core-resources` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBookMatrixLabTest.java`
  - branch：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseTest.java`
  - solution：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseSolutionTest.java`
  - perf：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part02_perf_concurrency/SpringCoreResourcesPatternResolverConcurrencyLabTest.java`
- [ ] 3.2.21 `spring-core-tx` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBookMatrixLabTest.java`
  - branch：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseTest.java`
  - solution：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseSolutionTest.java`
  - perf：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part02_perf_concurrency/SpringCoreTxThreadLocalBoundaryLabTest.java`
- [ ] 3.2.22 `spring-core-profiles` tests 闭环验收与对齐
  - book：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBookMatrixLabTest.java`
  - branch：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBranchMatrixLabTest.java`
  - exercise：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseTest.java`
  - solution：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseSolutionTest.java`
  - perf：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part02_perf_concurrency/SpringCoreProfilesEnvironmentConcurrencyLabTest.java`

### 3.3 并发/性能可复现实验补齐（当前已知缺口模块）

- [√] 3.3.1 `springboot-autoconfiguration` 新增并发/性能 Lab（禁止耗时阈值断言）
  - 新增：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part02_perf_concurrency/BootAutoConfigurationConcurrencyLabTest.java`
  - 同步：`docs/autoconfig/springboot-autoconfiguration/README.md` 增加“并发/性能入口”与可运行命令
- [√] 3.3.2 `springboot-logging` 新增并发/性能 Lab（建议以 MDC/线程隔离/异步日志边界为主题）
  - 新增：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part02_perf_concurrency/BootLoggingConcurrencyLabTest.java`
  - 同步：`docs/logging/springboot-logging/README.md` 增加“并发/性能入口”与可运行命令
- [√] 3.3.3 `springboot-observability` 新增并发/性能 Lab（建议以 Observation scope / meter registry 隔离为主题）
  - 新增：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part02_perf_concurrency/BootObservabilityConcurrencyLabTest.java`
  - 同步：`docs/observability/springboot-observability/README.md` 增加“并发/性能入口”与可运行命令
- [√] 3.3.4 `spring-core-events` 新增并发/性能 Lab（建议以 multicaster/异步事件/事务 after-commit 边界为主题）
  - 新增：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part03_perf_concurrency/SpringCoreEventsConcurrencyLabTest.java`
  - 同步：`docs/events/spring-core-events/README.md` 增加“并发/性能入口”与可运行命令
- [ ] 3.3.5 全量并发/性能用例稳定性审计：扫描所有 `*Concurrency*`/`*Perf*`/`*Saturation*` 测试，移除耗时阈值断言并统一为稳定信号（latch/可控时钟/失败路径计数等）

### 3.4 回归闸门

- [√] 3.4.1 执行一次全量回归闸门：`mvn -q test`

## 4. 质量与一致性验证

- [ ] 4.1 验证 docs-site 可构建（检查断链与导航一致性）
- [ ] 4.2 执行一次仓库级一致性检查：同一模块的 `artifactId`、目录名、文档命令、脚本参数一致

## 5. Security Check

- [ ] 5.1 执行安全检查（G9）：确认未引入明文密钥/外部生产依赖；示例配置最小化；并发实验避免不受控资源消耗

## 6. Knowledge Base Update（SSOT 同步）

- [ ] 6.1 同步更新知识库索引：`helloagents/wiki/overview.md`、`helloagents/wiki/learning-path.md`、`helloagents/wiki/modules/*.md`
- [ ] 6.2 更新 `helloagents/CHANGELOG.md` 记录本次结构改造与命名变更
