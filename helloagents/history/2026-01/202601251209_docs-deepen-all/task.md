# Task List: 全量文档加深（docs-deepen-all）

Directory: `helloagents/plan/202601251209_docs-deepen-all/`

---

## 1. 全局自动化与门禁

- [√] 1.1 新增章节契约检查脚本 `scripts/check-chapter-contract.py`（覆盖 `docs/**`），输出缺失项清单与建议修复路径
- [√] 1.2 修复/增强 `scripts/upsert-chapter-cards.py`：默认 Lab 发现逻辑兼容当前编号文件名（`*mainline-timeline.md`），确保推荐入口不退化为 N/A
- [√] 1.3 增强 `scripts/rewrite-chapters-booklike-v2.py`：在保持幂等前提下，补齐“关键分支/验证入口提示”的更强骨架（基于卡片字段）
- [√] 1.4 串联检查：更新 `scripts/audit-module-deep-dive.sh`，把（卡片/骨架/Bookify/断链/契约检查）输出到 `helloagents/plan/...` 报告文件

## 2. 全量批处理与一致性修复（以模块为单位增量）

- [√] 2.1 执行 `python3 scripts/upsert-chapter-cards.py --dry-run`，评估缺失/异常后再全量写入
- [√] 2.2 执行 `python3 scripts/rewrite-chapters-booklike-v2.py --dry-run --report helloagents/plan/202601251209_docs-deepen-all/booklike-v2-report.md`，确保无大面积失败/误判
- [√] 2.3 执行 `python3 scripts/bookify-docs.py`（建议先 `--dry-run` 再落盘），并二次运行验证幂等
- [√] 2.4 执行 `python3 scripts/check-md-relative-links.py --root docs`，修复断链直到 missing=0

## 3. 模块入口基线（用于审计与分批验收）

> 说明：本章 3.1/3.2 的结构会被 `scripts/audit-module-deep-dive.sh` 解析，用于检查入口文件是否存在并生成推荐运行命令。

### 3.1 Docs Entrypoints

- [√] 3.1.1 `spring-core-beans` docs
  - 目录页：`docs/beans/spring-core-beans/README.md`
  - 主线时间线：`docs/beans/spring-core-beans/part-00-guide/010-03-mainline-timeline.md`
  - 断点地图：`docs/beans/spring-core-beans/part-00-guide/013-02-breakpoint-map.md`
- [√] 3.1.2 `spring-core-aop` docs
  - 目录页：`docs/aop/spring-core-aop/README.md`
  - 主线时间线：`docs/aop/spring-core-aop/part-00-guide/028-03-mainline-timeline.md`
  - 断点地图：`docs/aop/spring-core-aop/part-00-guide/029-02-breakpoint-map.md`
- [√] 3.1.3 `spring-core-aop-weaving` docs
  - 目录页：`docs/aop/spring-core-aop-weaving/README.md`
  - 主线时间线：`docs/aop/spring-core-aop-weaving/part-00-guide/043-03-mainline-timeline.md`
  - 断点地图：`docs/aop/spring-core-aop-weaving/part-00-guide/044-02-breakpoint-map.md`
- [√] 3.1.4 `spring-core-tx` docs
  - 目录页：`docs/tx/spring-core-tx/README.md`
  - 主线时间线：`docs/tx/spring-core-tx/part-00-guide/052-03-mainline-timeline.md`
  - 断点地图：`docs/tx/spring-core-tx/part-00-guide/053-02-breakpoint-map.md`
- [√] 3.1.5 `spring-core-events` docs
  - 目录页：`docs/events/spring-core-events/README.md`
  - 主线时间线：`docs/events/spring-core-events/part-00-guide/127-03-mainline-timeline.md`
  - 断点地图：`docs/events/spring-core-events/part-00-guide/128-02-breakpoint-map.md`
- [√] 3.1.6 `spring-core-resources` docs
  - 目录页：`docs/resources/spring-core-resources/README.md`
  - 主线时间线：`docs/resources/spring-core-resources/part-00-guide/139-03-mainline-timeline.md`
  - 断点地图：`docs/resources/spring-core-resources/part-00-guide/140-02-breakpoint-map.md`
- [√] 3.1.7 `spring-core-profiles` docs
  - 目录页：`docs/profiles/spring-core-profiles/README.md`
  - 主线时间线：`docs/profiles/spring-core-profiles/part-00-guide/150-03-mainline-timeline.md`
  - 断点地图：`docs/profiles/spring-core-profiles/part-00-guide/151-02-breakpoint-map.md`
- [√] 3.1.8 `spring-core-validation` docs
  - 目录页：`docs/validation/spring-core-validation/README.md`
  - 主线时间线：`docs/validation/spring-core-validation/part-00-guide/156-03-mainline-timeline.md`
  - 断点地图：`docs/validation/spring-core-validation/part-00-guide/157-02-breakpoint-map.md`
- [√] 3.1.9 `spring-boot-web-mvc` docs
  - 目录页：`docs/web-mvc/spring-boot-web-mvc/README.md`
  - 主线时间线：`docs/web-mvc/spring-boot-web-mvc/part-00-guide/063-03-mainline-timeline.md`
  - 断点地图：`docs/web-mvc/spring-boot-web-mvc/part-00-guide/066-02-breakpoint-map.md`
- [√] 3.1.10 `spring-boot-security` docs
  - 目录页：`docs/security/spring-boot-security/README.md`
  - 主线时间线：`docs/security/spring-boot-security/part-00-guide/085-03-mainline-timeline.md`
  - 断点地图：`docs/security/spring-boot-security/part-00-guide/086-02-breakpoint-map.md`
- [√] 3.1.11 `spring-boot-data-jpa` docs
  - 目录页：`docs/data-jpa/spring-boot-data-jpa/README.md`
  - 主线时间线：`docs/data-jpa/spring-boot-data-jpa/part-00-guide/095-03-mainline-timeline.md`
  - 断点地图：`docs/data-jpa/spring-boot-data-jpa/part-00-guide/096-02-breakpoint-map.md`
- [√] 3.1.12 `spring-boot-cache` docs
  - 目录页：`docs/cache/spring-boot-cache/README.md`
  - 主线时间线：`docs/cache/spring-boot-cache/part-00-guide/107-03-mainline-timeline.md`
  - 断点地图：`docs/cache/spring-boot-cache/part-00-guide/108-02-breakpoint-map.md`
- [√] 3.1.13 `spring-boot-async-scheduling` docs
  - 目录页：`docs/async-scheduling/spring-boot-async-scheduling/README.md`
  - 主线时间线：`docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/117-03-mainline-timeline.md`
  - 断点地图：`docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/118-02-breakpoint-map.md`
- [√] 3.1.14 `spring-boot-actuator` docs
  - 目录页：`docs/actuator/spring-boot-actuator/README.md`
  - 主线时间线：`docs/actuator/spring-boot-actuator/part-00-guide/167-03-mainline-timeline.md`
  - 断点地图：`docs/actuator/spring-boot-actuator/part-00-guide/168-02-breakpoint-map.md`
- [√] 3.1.15 `spring-boot-testing` docs
  - 目录页：`docs/testing/spring-boot-testing/README.md`
  - 主线时间线：`docs/testing/spring-boot-testing/part-00-guide/183-03-mainline-timeline.md`
  - 断点地图：`docs/testing/spring-boot-testing/part-00-guide/184-02-breakpoint-map.md`
- [√] 3.1.16 `spring-boot-business-case` docs
  - 目录页：`docs/business-case/spring-boot-business-case/README.md`
  - 主线时间线：`docs/business-case/spring-boot-business-case/part-00-guide/189-03-mainline-timeline.md`
  - 断点地图：`docs/business-case/spring-boot-business-case/part-00-guide/190-02-breakpoint-map.md`
- [√] 3.1.17 `spring-boot-autoconfiguration` docs
  - 目录页：`docs/autoconfig/spring-boot-autoconfiguration/README.md`
  - 主线时间线：`docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/194-03-mainline-timeline.md`
  - 断点地图：`docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/195-02-breakpoint-map.md`
- [√] 3.1.18 `spring-boot-logging` docs
  - 目录页：`docs/logging/spring-boot-logging/README.md`
  - 主线时间线：`docs/logging/spring-boot-logging/part-00-guide/199-03-mainline-timeline.md`
  - 断点地图：`docs/logging/spring-boot-logging/part-00-guide/200-02-breakpoint-map.md`
- [√] 3.1.19 `spring-boot-observability` docs
  - 目录页：`docs/observability/spring-boot-observability/README.md`
  - 主线时间线：`docs/observability/spring-boot-observability/part-00-guide/204-03-mainline-timeline.md`
  - 断点地图：`docs/observability/spring-boot-observability/part-00-guide/205-02-breakpoint-map.md`
- [√] 3.1.20 `spring-boot-web-client` docs
  - 目录页：`docs/web-client/spring-boot-web-client/README.md`
  - 主线时间线：`docs/web-client/spring-boot-web-client/part-00-guide/173-03-mainline-timeline.md`
  - 断点地图：`docs/web-client/spring-boot-web-client/part-00-guide/174-02-breakpoint-map.md`
- [√] 3.1.21 `spring-core-spel` docs
  - 目录页：`docs/spel/spring-core-spel/README.md`
  - 主线时间线：`docs/spel/spring-core-spel/part-00-guide/209-03-mainline-timeline.md`
  - 断点地图：`docs/spel/spring-core-spel/part-00-guide/210-02-breakpoint-map.md`
- [√] 3.1.22 `spring-boot-basics` docs
  - 目录页：`docs/basics/spring-boot-basics/README.md`
  - 主线时间线：`docs/basics/spring-boot-basics/part-00-guide/003-03-mainline-timeline.md`
  - 断点地图：`docs/basics/spring-boot-basics/part-00-guide/004-02-breakpoint-map.md`

### 3.2 Test Entrypoints

- [√] 3.2.1 `spring-core-beans` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
- [√] 3.2.2 `spring-core-aop` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part00_guide/SpringCoreAopExerciseTest.java`
- [√] 3.2.3 `spring-core-aop-weaving` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwBranchMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseTest.java`
- [√] 3.2.4 `spring-core-tx` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxPitfallsBranchMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseTest.java`
- [√] 3.2.5 `spring-core-events` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBasicsBranchMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseTest.java`
- [√] 3.2.6 `spring-core-resources` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseTest.java`
- [√] 3.2.7 `spring-core-profiles` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseTest.java`
- [√] 3.2.8 `spring-core-validation` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseTest.java`
- [√] 3.2.9 `spring-boot-web-mvc` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBindingDeepDiveLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`
- [√] 3.2.10 `spring-boot-security` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part00_guide/BootSecurityExerciseTest.java`
- [√] 3.2.11 `spring-boot-data-jpa` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java`
- [√] 3.2.12 `spring-boot-cache` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseTest.java`
- [√] 3.2.13 `spring-boot-async-scheduling` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`
- [√] 3.2.14 `spring-boot-actuator` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseTest.java`
- [√] 3.2.15 `spring-boot-testing` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java`
- [√] 3.2.16 `spring-boot-business-case` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseTest.java`
- [√] 3.2.17 `spring-boot-autoconfiguration` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseTest.java`
- [√] 3.2.18 `spring-boot-logging` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingExerciseTest.java`
- [√] 3.2.19 `spring-boot-observability` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityExerciseTest.java`
- [√] 3.2.20 `spring-boot-web-client` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseTest.java`
- [√] 3.2.21 `spring-core-spel` tests
  - 最小可跑 Lab：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelExerciseTest.java`
- [√] 3.2.22 `spring-boot-basics` tests
  - 最小可跑 Lab：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java`
  - Exercise（默认禁用）：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`

### 3.3 Security Check

- [√] 3.3.1 执行安全检查：脚本仅在仓库内读写、无外部网络调用；文档与测试示例不包含敏感信息；Exercises 保持默认禁用策略
