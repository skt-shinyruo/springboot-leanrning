# Module Deep-Dive Audit（docs/tests/perf 入口基线）

- 生成来源：`helloagents/plan/202601251209_docs-deepen-all/task.md`（解析 3.1/3.2 章节）
- 生成方式：`scripts/audit-module-deep-dive.sh --format md --out <file>`

## spring-core-beans

- Maven artifactId：`spring-core-beans`
- Code module root：`spring-core-modules/spring-core-beans`

### Docs 入口
- ✅ 目录页：`docs/beans/spring-core-beans/README.md`
- ✅ 主线时间线：`docs/beans/spring-core-beans/part-00-guide/02-mainline-timeline.md`
- ✅ 断点地图：`docs/beans/spring-core-beans/part-00-guide/07-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansExerciseTest test`

## spring-core-aop

- Maven artifactId：`spring-core-aop`
- Code module root：`spring-core-modules/spring-core-aop`

### Docs 入口
- ✅ 目录页：`docs/aop/spring-core-aop/README.md`
- ✅ 主线时间线：`docs/aop/spring-core-aop/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/aop/spring-core-aop/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part00_guide/SpringCoreAopExerciseTest.java`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopExerciseTest test`

## spring-core-aop-weaving

- Maven artifactId：`spring-core-aop-weaving`
- Code module root：`spring-core-modules/spring-core-aop-weaving`

### Docs 入口
- ✅ 目录页：`docs/aop/spring-core-aop-weaving/README.md`
- ✅ 主线时间线：`docs/aop/spring-core-aop-weaving/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/aop/spring-core-aop-weaving/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseTest.java`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=SpringCoreAopWeavingExerciseTest test`

## spring-core-tx

- Maven artifactId：`spring-core-tx`
- Code module root：`spring-core-modules/spring-core-tx`

### Docs 入口
- ✅ 目录页：`docs/tx/spring-core-tx/README.md`
- ✅ 主线时间线：`docs/tx/spring-core-tx/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/tx/spring-core-tx/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxPitfallsBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseTest.java`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxExerciseTest test`

## spring-core-events

- Maven artifactId：`spring-core-events`
- Code module root：`spring-core-modules/spring-core-events`

### Docs 入口
- ✅ 目录页：`docs/events/spring-core-events/README.md`
- ✅ 主线时间线：`docs/events/spring-core-events/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/events/spring-core-events/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBasicsBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseTest.java`
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsExerciseTest test`

## spring-core-resources

- Maven artifactId：`spring-core-resources`
- Code module root：`spring-core-modules/spring-core-resources`

### Docs 入口
- ✅ 目录页：`docs/resources/spring-core-resources/README.md`
- ✅ 主线时间线：`docs/resources/spring-core-resources/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/resources/spring-core-resources/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseTest.java`
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesExerciseTest test`

## spring-core-profiles

- Maven artifactId：`spring-core-profiles`
- Code module root：`spring-core-modules/spring-core-profiles`

### Docs 入口
- ✅ 目录页：`docs/profiles/spring-core-profiles/README.md`
- ✅ 主线时间线：`docs/profiles/spring-core-profiles/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/profiles/spring-core-profiles/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseTest.java`
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesExerciseTest test`

## spring-core-validation

- Maven artifactId：`spring-core-validation`
- Code module root：`spring-core-modules/spring-core-validation`

### Docs 入口
- ✅ 目录页：`docs/validation/spring-core-validation/README.md`
- ✅ 主线时间线：`docs/validation/spring-core-validation/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/validation/spring-core-validation/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseTest.java`
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationExerciseTest test`

## spring-boot-web-mvc

- Maven artifactId：`spring-boot-web-mvc`
- Code module root：`spring-boot-modules/spring-boot-web-mvc`

### Docs 入口
- ✅ 目录页：`docs/web-mvc/spring-boot-web-mvc/README.md`
- ✅ 主线时间线：`docs/web-mvc/spring-boot-web-mvc/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/web-mvc/spring-boot-web-mvc/part-00-guide/06-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBindingDeepDiveLabTest.java`
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBindingDeepDiveLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcExerciseTest test`

## spring-boot-security

- Maven artifactId：`spring-boot-security`
- Code module root：`spring-boot-modules/spring-boot-security`

### Docs 入口
- ✅ 目录页：`docs/security/spring-boot-security/README.md`
- ✅ 主线时间线：`docs/security/spring-boot-security/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/security/spring-boot-security/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part00_guide/BootSecurityExerciseTest.java`
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecurityExerciseTest test`

## spring-boot-data-jpa

- Maven artifactId：`spring-boot-data-jpa`
- Code module root：`spring-boot-modules/spring-boot-data-jpa`

### Docs 入口
- ✅ 目录页：`docs/data-jpa/spring-boot-data-jpa/README.md`
- ✅ 主线时间线：`docs/data-jpa/spring-boot-data-jpa/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/data-jpa/spring-boot-data-jpa/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java`
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaExerciseTest test`

## spring-boot-cache

- Maven artifactId：`spring-boot-cache`
- Code module root：`spring-boot-modules/spring-boot-cache`

### Docs 入口
- ✅ 目录页：`docs/cache/spring-boot-cache/README.md`
- ✅ 主线时间线：`docs/cache/spring-boot-cache/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/cache/spring-boot-cache/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseTest.java`
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheExerciseTest test`

## spring-boot-async-scheduling

- Maven artifactId：`spring-boot-async-scheduling`
- Code module root：`spring-boot-modules/spring-boot-async-scheduling`

### Docs 入口
- ✅ 目录页：`docs/async-scheduling/spring-boot-async-scheduling/README.md`
- ✅ 主线时间线：`docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExerciseTest test`

## spring-boot-actuator

- Maven artifactId：`spring-boot-actuator`
- Code module root：`spring-boot-modules/spring-boot-actuator`

### Docs 入口
- ✅ 目录页：`docs/actuator/spring-boot-actuator/README.md`
- ✅ 主线时间线：`docs/actuator/spring-boot-actuator/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/actuator/spring-boot-actuator/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseTest.java`
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorExerciseTest test`

## spring-boot-testing

- Maven artifactId：`spring-boot-testing`
- Code module root：`spring-boot-modules/spring-boot-testing`

### Docs 入口
- ✅ 目录页：`docs/testing/spring-boot-testing/README.md`
- ✅ 主线时间线：`docs/testing/spring-boot-testing/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/testing/spring-boot-testing/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java`
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingExerciseTest test`

## spring-boot-business-case

- Maven artifactId：`spring-boot-business-case`
- Code module root：`spring-boot-modules/spring-boot-business-case`

### Docs 入口
- ✅ 目录页：`docs/business-case/spring-boot-business-case/README.md`
- ✅ 主线时间线：`docs/business-case/spring-boot-business-case/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/business-case/spring-boot-business-case/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseTest.java`
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseExerciseTest test`

## spring-boot-autoconfiguration

- Maven artifactId：`spring-boot-autoconfiguration`
- Code module root：`spring-boot-modules/spring-boot-autoconfiguration`

### Docs 入口
- ✅ 目录页：`docs/autoconfig/spring-boot-autoconfiguration/README.md`
- ✅ 主线时间线：`docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseTest.java`
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationExerciseTest test`

## spring-boot-logging

- Maven artifactId：`spring-boot-logging`
- Code module root：`spring-boot-modules/spring-boot-logging`

### Docs 入口
- ✅ 目录页：`docs/logging/spring-boot-logging/README.md`
- ✅ 主线时间线：`docs/logging/spring-boot-logging/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/logging/spring-boot-logging/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingExerciseTest.java`
  - `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingExerciseTest test`

## spring-boot-observability

- Maven artifactId：`spring-boot-observability`
- Code module root：`spring-boot-modules/spring-boot-observability`

### Docs 入口
- ✅ 目录页：`docs/observability/spring-boot-observability/README.md`
- ✅ 主线时间线：`docs/observability/spring-boot-observability/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/observability/spring-boot-observability/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityExerciseTest.java`
  - `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityExerciseTest test`

## spring-boot-web-client

- Maven artifactId：`spring-boot-web-client`
- Code module root：`spring-boot-modules/spring-boot-web-client`

### Docs 入口
- ✅ 目录页：`docs/web-client/spring-boot-web-client/README.md`
- ✅ 主线时间线：`docs/web-client/spring-boot-web-client/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/web-client/spring-boot-web-client/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseTest.java`
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientExerciseTest test`

## spring-core-spel

- Maven artifactId：`spring-core-spel`
- Code module root：`spring-core-modules/spring-core-spel`

### Docs 入口
- ✅ 目录页：`docs/spel/spring-core-spel/README.md`
- ✅ 主线时间线：`docs/spel/spring-core-spel/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/spel/spring-core-spel/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelExerciseTest.java`
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelExerciseTest test`

## spring-boot-basics

- Maven artifactId：`spring-boot-basics`
- Code module root：`spring-boot-modules/spring-boot-basics`

### Docs 入口
- ✅ 目录页：`docs/basics/spring-boot-basics/README.md`
- ✅ 主线时间线：`docs/basics/spring-boot-basics/part-00-guide/01-mainline-timeline.md`
- ✅ 断点地图：`docs/basics/spring-boot-basics/part-00-guide/04-breakpoint-map.md`

### Tests 入口（可跑命令）
- ✅ 最小可跑 Lab：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- ✅ Exercise（默认禁用）：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsExerciseTest test`

---

- 总模块数：22
- 缺失项总数：0

## Docs 质量门禁（契约 / 断链）

### Chapter Contract（章节契约）

```text
[ISSUE] docs/actuator/spring-boot-actuator/appendix/01-common-pitfalls.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/actuator/spring-boot-actuator/appendix/02-self-check.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/actuator/spring-boot-actuator/part-00-guide/01-mainline-timeline.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/actuator/spring-boot-actuator/part-00-guide/01-mainline-timeline.md :: missing_bookify :: 缺少 BOOKIFY marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-docs.py
[ISSUE] docs/actuator/spring-boot-actuator/part-00-guide/02-deep-dive-guide.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/actuator/spring-boot-actuator/part-00-guide/03-actuator-endpoint-call-chain.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/actuator/spring-boot-actuator/part-00-guide/04-breakpoint-map.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/actuator/spring-boot-actuator/part-00-guide/05-branch-decision-matrix.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/actuator/spring-boot-actuator/part-01-actuator/01-actuator-basics.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/appendix/01-common-pitfalls.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/appendix/02-self-check.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-00-guide/01-mainline-timeline.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-00-guide/01-mainline-timeline.md :: missing_bookify :: 缺少 BOOKIFY marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-docs.py
[ISSUE] docs/aop/spring-core-aop/part-00-guide/02-deep-dive-guide.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-00-guide/03-aop-invocation-call-chain.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-00-guide/04-breakpoint-map.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-00-guide/05-branch-decision-matrix.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-01-proxy-fundamentals/02-jdk-vs-cglib.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-01-proxy-fundamentals/03-self-invocation.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-01-proxy-fundamentals/04-final-and-proxy-limits.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-01-proxy-fundamentals/05-expose-proxy.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-01-proxy-fundamentals/06-debugging.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-02-autoproxy-and-pointcuts/01-autoproxy-creator-mainline.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-02-autoproxy-and-pointcuts/02-pointcut-expression-system.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-03-proxy-stacking/01-multi-proxy-stacking.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop/part-03-proxy-stacking/02-real-world-stacking-playbook.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/appendix/01-common-pitfalls.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/appendix/02-self-check.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-00-guide/01-mainline-timeline.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-00-guide/01-mainline-timeline.md :: missing_bookify :: 缺少 BOOKIFY marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-docs.py
[ISSUE] docs/aop/spring-core-aop-weaving/part-00-guide/02-deep-dive-guide.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-00-guide/03-aspectj-weaving-call-chain.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-00-guide/04-breakpoint-map.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-00-guide/05-branch-decision-matrix.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-01-mental-model/01-proxy-vs-weaving.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-02-ltw/01-ltw-basics.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-03-ctw/01-ctw-basics.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/aop/spring-core-aop-weaving/part-04-join-points/01-join-point-cookbook.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/appendix/01-common-pitfalls.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/appendix/02-self-check.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/01-mainline-timeline.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/01-mainline-timeline.md :: missing_bookify :: 缺少 BOOKIFY marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-docs.py
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/02-deep-dive-guide.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/03-async-and-scheduling-call-chain.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/04-breakpoint-map.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-00-guide/05-branch-decision-matrix.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-01-async-scheduling/01-async-proxy-mental-model.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-01-async-scheduling/02-executor-and-threading.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-01-async-scheduling/03-exceptions.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-01-async-scheduling/04-self-invocation.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/async-scheduling/spring-boot-async-scheduling/part-01-async-scheduling/05-scheduling-basics.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/autoconfig/spring-boot-autoconfiguration/appendix/01-common-pitfalls.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/autoconfig/spring-boot-autoconfiguration/appendix/02-self-check.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/01-mainline-timeline.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/02-deep-dive-guide.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/03-autoconfiguration-import-call-chain.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/04-breakpoint-map.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/autoconfig/spring-boot-autoconfiguration/part-00-guide/05-branch-decision-matrix.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/autoconfig/spring-boot-autoconfiguration/part-01-autoconfig-basics/01-conditional-and-backoff.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/appendix/01-common-pitfalls.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/appendix/02-self-check.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/part-00-guide/01-mainline-timeline.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/part-00-guide/01-mainline-timeline.md :: missing_global_nav :: 缺少 GLOBAL-BOOK-NAV marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-global-chapters.py（或后续统一导航脚本）
[ISSUE] docs/basics/spring-boot-basics/part-00-guide/01-mainline-timeline.md :: missing_bookify :: 缺少 BOOKIFY marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-docs.py
[ISSUE] docs/basics/spring-boot-basics/part-00-guide/02-deep-dive-guide.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/part-00-guide/03-springapplication-run-call-chain.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/part-00-guide/04-breakpoint-map.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/part-00-guide/05-branch-decision-matrix.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/part-01-boot-basics/01-property-sources-and-profiles.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/basics/spring-boot-basics/part-01-boot-basics/02-configuration-properties-binding.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/beans/spring-core-beans/appendix/01-common-pitfalls.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/beans/spring-core-beans/appendix/11-self-check.md :: missing_card_fields :: 卡片缺字段：知识点, 怎么使用, 原理, 源码入口, 推荐 Lab :: 建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）
[ISSUE] docs/beans/spring-core-beans/appendix/02-glossary.md :: missing_chapter_card :: 缺少 CHAPTER-CARD marker（或 marker 不完整） :: 建议运行：python3 scripts/upsert-chapter-cards.py
[ISSUE] docs/beans/spring-core-beans/appendix/02-glossary.md :: missing_global_nav :: 缺少 GLOBAL-BOOK-NAV marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-global-chapters.py（或后续统一导航脚本）
[ISSUE] docs/beans/spring-core-beans/appendix/03-knowledge-map.md :: missing_chapter_card :: 缺少 CHAPTER-CARD marker（或 marker 不完整） :: 建议运行：python3 scripts/upsert-chapter-cards.py
[ISSUE] docs/beans/spring-core-beans/appendix/03-knowledge-map.md :: missing_global_nav :: 缺少 GLOBAL-BOOK-NAV marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-global-chapters.py（或后续统一导航脚本）
[ISSUE] docs/beans/spring-core-beans/appendix/04-interview-playbook.md :: missing_chapter_card :: 缺少 CHAPTER-CARD marker（或 marker 不完整） :: 建议运行：python3 scripts/upsert-chapter-cards.py
[ISSUE] docs/beans/spring-core-beans/appendix/04-interview-playbook.md :: missing_global_nav :: 缺少 GLOBAL-BOOK-NAV marker（或 marker 不完整） :: 建议运行：python3 scripts/bookify-global-chapters.py（或后续统一导航脚本）
[ISSUE] docs/beans/spring-core-beans/appendix/05-production-troubleshooting-checklist.md :: missing_chapter_card :: 缺少 CHAPTER-CARD marker（或 marker 不完整） :: 建议运行：python3 scripts/upsert-chapter-cards.py
[ERROR] 检测到章节契约缺口，请按提示修复后重试。
[CHECK] roots=[docs] files=391 checked=367 skipped=24 issues=528
```

### Relative Links（相对链接断链）

```text
[CHECK] root=docs files=391 missing=0
```
