# Module Deep-Dive Audit（docs/tests/perf 入口基线）

- 生成来源：`helloagents/history/2026-01/202601231156_tutorials_format_alignment/task.md`（解析 3.1/3.2 章节）
- 生成方式：`scripts/audit-module-deep-dive.sh --format md --out <file>`

## springboot-basics

- Maven artifactId：`spring-boot-basics`
- Code module root：`spring-boot-modules/spring-boot-basics`

### Docs 入口
- ✅ deep-dive：`docs/basics/springboot-basics/part-00-guide/004-00-deep-dive-guide.md`
- ✅ call-chain：`docs/basics/springboot-basics/part-00-guide/004-01-springapplication-run-call-chain.md`
- ✅ breakpoint-map：`docs/basics/springboot-basics/part-00-guide/004-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/basics/springboot-basics/part-00-guide/004-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/basics/springboot-basics/appendix/007-90-common-pitfalls.md`
- ✅ self-check：`docs/basics/springboot-basics/appendix/008-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part02_perf_concurrency/BootBasicsEnvironmentConcurrencyLabTest.java`
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsEnvironmentConcurrencyLabTest test`

## springboot-autoconfiguration

- Maven artifactId：`spring-boot-autoconfiguration`
- Code module root：`spring-boot-modules/spring-boot-autoconfiguration`

### Docs 入口
- ✅ deep-dive：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-00-deep-dive-guide.md`
- ✅ call-chain：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-01-autoconfiguration-import-call-chain.md`
- ✅ breakpoint-map：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/autoconfig/springboot-autoconfiguration/appendix/197-90-common-pitfalls.md`
- ✅ self-check：`docs/autoconfig/springboot-autoconfiguration/appendix/198-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseTest.java`
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationExerciseSolutionTest test`
- ❌ perf：`(missing)`

### 缺失项
- tests:perf -> `(missing)`

## springboot-web-mvc

- Maven artifactId：`spring-boot-web-mvc`
- Code module root：`spring-boot-modules/spring-boot-web-mvc`

### Docs 入口
- ✅ deep-dive：`docs/web-mvc/springboot-web-mvc/part-00-guide/064-00-deep-dive-guide.md`
- ✅ call-chain：`docs/web-mvc/springboot-web-mvc/part-00-guide/064-01-webmvc-request-call-chain.md`
- ✅ breakpoint-map：`docs/web-mvc/springboot-web-mvc/part-00-guide/066-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/web-mvc/springboot-web-mvc/part-00-guide/064-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/web-mvc/springboot-web-mvc/appendix/082-90-common-pitfalls.md`
- ✅ self-check：`docs/web-mvc/springboot-web-mvc/appendix/083-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcErrorBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_perf_concurrency/BootWebMvcRequestScopeIsolationLabTest.java`
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcRequestScopeIsolationLabTest test`

## springboot-data-jpa

- Maven artifactId：`spring-boot-data-jpa`
- Code module root：`spring-boot-modules/spring-boot-data-jpa`

### Docs 入口
- ✅ deep-dive：`docs/data-jpa/springboot-data-jpa/part-00-guide/096-00-deep-dive-guide.md`
- ✅ call-chain：`docs/data-jpa/springboot-data-jpa/part-00-guide/096-01-repository-call-chain.md`
- ✅ breakpoint-map：`docs/data-jpa/springboot-data-jpa/part-00-guide/096-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/data-jpa/springboot-data-jpa/part-00-guide/096-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/data-jpa/springboot-data-jpa/appendix/104-90-common-pitfalls.md`
- ✅ self-check：`docs/data-jpa/springboot-data-jpa/appendix/105-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java`
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part02_perf_concurrency/BootDataJpaEntityManagerConcurrencyLabTest.java`
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaEntityManagerConcurrencyLabTest test`

## springboot-actuator

- Maven artifactId：`spring-boot-actuator`
- Code module root：`spring-boot-modules/spring-boot-actuator`

### Docs 入口
- ✅ deep-dive：`docs/actuator/springboot-actuator/part-00-guide/168-00-deep-dive-guide.md`
- ✅ call-chain：`docs/actuator/springboot-actuator/part-00-guide/168-01-actuator-endpoint-call-chain.md`
- ✅ breakpoint-map：`docs/actuator/springboot-actuator/part-00-guide/168-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/actuator/springboot-actuator/part-00-guide/168-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/actuator/springboot-actuator/appendix/170-90-common-pitfalls.md`
- ✅ self-check：`docs/actuator/springboot-actuator/appendix/171-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseTest.java`
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part02_perf_concurrency/BootActuatorMetricsConcurrencyLabTest.java`
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorMetricsConcurrencyLabTest test`

## springboot-testing

- Maven artifactId：`spring-boot-testing`
- Code module root：`spring-boot-modules/spring-boot-testing`

### Docs 入口
- ✅ deep-dive：`docs/testing/springboot-testing/part-00-guide/184-00-deep-dive-guide.md`
- ✅ call-chain：`docs/testing/springboot-testing/part-00-guide/184-01-test-bootstrap-and-slicing-call-chain.md`
- ✅ breakpoint-map：`docs/testing/springboot-testing/part-00-guide/184-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/testing/springboot-testing/part-00-guide/184-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/testing/springboot-testing/appendix/186-90-common-pitfalls.md`
- ✅ self-check：`docs/testing/springboot-testing/appendix/187-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java`
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part02_perf_concurrency/BootTestingTestContextCacheLabTest.java`
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingTestContextCacheLabTest test`

## springboot-business-case

- Maven artifactId：`spring-boot-business-case`
- Code module root：`spring-boot-modules/spring-boot-business-case`

### Docs 入口
- ✅ deep-dive：`docs/business-case/springboot-business-case/part-00-guide/190-00-deep-dive-guide.md`
- ✅ call-chain：`docs/business-case/springboot-business-case/part-00-guide/190-01-business-request-call-chain.md`
- ✅ breakpoint-map：`docs/business-case/springboot-business-case/part-00-guide/190-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/business-case/springboot-business-case/part-00-guide/190-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/business-case/springboot-business-case/appendix/192-90-common-pitfalls.md`
- ✅ self-check：`docs/business-case/springboot-business-case/appendix/193-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseTest.java`
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part02_perf_concurrency/BootBusinessCaseConcurrentOrderPlacementLabTest.java`
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseConcurrentOrderPlacementLabTest test`

## springboot-security

- Maven artifactId：`spring-boot-security`
- Code module root：`spring-boot-modules/spring-boot-security`

### Docs 入口
- ✅ deep-dive：`docs/security/springboot-security/part-00-guide/086-00-deep-dive-guide.md`
- ✅ call-chain：`docs/security/springboot-security/part-00-guide/086-01-security-filterchain-call-chain.md`
- ✅ breakpoint-map：`docs/security/springboot-security/part-00-guide/086-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/security/springboot-security/part-00-guide/086-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/security/springboot-security/appendix/092-90-common-pitfalls.md`
- ✅ self-check：`docs/security/springboot-security/appendix/093-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part00_guide/BootSecurityExerciseTest.java`
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecurityExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part00_guide/BootSecurityExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecurityExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part02_perf_concurrency/BootSecuritySecurityContextIsolationLabTest.java`
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecuritySecurityContextIsolationLabTest test`

## springboot-observability

- Maven artifactId：`spring-boot-observability`
- Code module root：`spring-boot-modules/spring-boot-observability`

### Docs 入口
- ✅ deep-dive：`docs/observability/springboot-observability/part-00-guide/205-00-deep-dive-guide.md`
- ✅ call-chain：`docs/observability/springboot-observability/part-00-guide/205-01-http-observation-call-chain.md`
- ✅ breakpoint-map：`docs/observability/springboot-observability/part-00-guide/205-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/observability/springboot-observability/part-00-guide/205-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/observability/springboot-observability/appendix/207-90-common-pitfalls.md`
- ✅ self-check：`docs/observability/springboot-observability/appendix/208-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityExerciseTest.java`
  - `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityExerciseSolutionTest test`
- ❌ perf：`(missing)`

### 缺失项
- tests:perf -> `(missing)`

## springboot-logging

- Maven artifactId：`spring-boot-logging`
- Code module root：`spring-boot-modules/spring-boot-logging`

### Docs 入口
- ✅ deep-dive：`docs/logging/springboot-logging/part-00-guide/200-00-deep-dive-guide.md`
- ✅ call-chain：`docs/logging/springboot-logging/part-00-guide/200-01-logging-call-chain.md`
- ✅ breakpoint-map：`docs/logging/springboot-logging/part-00-guide/200-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/logging/springboot-logging/part-00-guide/200-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/logging/springboot-logging/appendix/202-90-common-pitfalls.md`
- ✅ self-check：`docs/logging/springboot-logging/appendix/203-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingExerciseTest.java`
  - `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingExerciseSolutionTest test`
- ❌ perf：`(missing)`

### 缺失项
- tests:perf -> `(missing)`

## springboot-web-client

- Maven artifactId：`spring-boot-web-client`
- Code module root：`spring-boot-modules/spring-boot-web-client`

### Docs 入口
- ✅ deep-dive：`docs/web-client/springboot-web-client/part-00-guide/174-00-deep-dive-guide.md`
- ✅ call-chain：`docs/web-client/springboot-web-client/part-00-guide/174-01-webclient-call-chain.md`
- ✅ breakpoint-map：`docs/web-client/springboot-web-client/part-00-guide/174-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/web-client/springboot-web-client/part-00-guide/174-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/web-client/springboot-web-client/appendix/180-90-common-pitfalls.md`
- ✅ self-check：`docs/web-client/springboot-web-client/appendix/181-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseTest.java`
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part02_perf_concurrency/BootWebClientRestClientConcurrencyLabTest.java`
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientRestClientConcurrencyLabTest test`

## springboot-async-scheduling

- Maven artifactId：`spring-boot-async-scheduling`
- Code module root：`spring-boot-modules/spring-boot-async-scheduling`

### Docs 入口
- ✅ deep-dive：`docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-00-deep-dive-guide.md`
- ✅ call-chain：`docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-01-async-and-scheduling-call-chain.md`
- ✅ breakpoint-map：`docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/async-scheduling/springboot-async-scheduling/part-00-guide/118-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/async-scheduling/springboot-async-scheduling/appendix/124-90-common-pitfalls.md`
- ✅ self-check：`docs/async-scheduling/springboot-async-scheduling/appendix/125-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseTest.java`
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part00_guide/BootAsyncSchedulingExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part02_perf_concurrency/BootAsyncSchedulingExecutorSaturationLabTest.java`
  - `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingExecutorSaturationLabTest test`

## springboot-cache

- Maven artifactId：`spring-boot-cache`
- Code module root：`spring-boot-modules/spring-boot-cache`

### Docs 入口
- ✅ deep-dive：`docs/cache/springboot-cache/part-00-guide/108-00-deep-dive-guide.md`
- ✅ call-chain：`docs/cache/springboot-cache/part-00-guide/108-01-cache-interceptor-call-chain.md`
- ✅ breakpoint-map：`docs/cache/springboot-cache/part-00-guide/108-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/cache/springboot-cache/part-00-guide/108-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/cache/springboot-cache/appendix/114-90-common-pitfalls.md`
- ✅ self-check：`docs/cache/springboot-cache/appendix/115-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBookMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
- ✅ branch：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`
- ✅ exercise：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseTest.java`
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheExerciseTest test`
- ✅ solution：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseSolutionTest.java`
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheExerciseSolutionTest test`
- ✅ perf：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part02_perf_concurrency/BootCacheStampedeProtectionLabTest.java`
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheStampedeProtectionLabTest test`

## spring-core-beans

- Maven artifactId：`spring-core-beans`
- Code module root：`spring-core-modules/spring-core-beans`

### Docs 入口
- ✅ deep-dive：`docs/beans/spring-core-beans/part-00-guide/011-00-deep-dive-guide.md`
- ✅ call-chain：`docs/beans/spring-core-beans/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
- ✅ breakpoint-map：`docs/beans/spring-core-beans/part-00-guide/013-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/beans/spring-core-beans/part-00-guide/011-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/beans/spring-core-beans/appendix/025-90-common-pitfalls.md`
- ✅ self-check：`docs/beans/spring-core-beans/appendix/026-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- ✅ branch：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansExerciseSolutionTest test`
- ✅ perf：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_perf_concurrency/SpringCoreBeansConcurrentGetBeanLabTest.java`
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansConcurrentGetBeanLabTest test`

## spring-core-spel

- Maven artifactId：`spring-core-spel`
- Code module root：`spring-core-modules/spring-core-spel`

### Docs 入口
- ✅ deep-dive：`docs/spel/spring-core-spel/part-00-guide/210-00-deep-dive-guide.md`
- ✅ call-chain：`docs/spel/spring-core-spel/part-00-guide/210-01-spel-call-chain.md`
- ✅ breakpoint-map：`docs/spel/spring-core-spel/part-00-guide/210-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/spel/spring-core-spel/part-00-guide/210-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/spel/spring-core-spel/appendix/212-90-common-pitfalls.md`
- ✅ self-check：`docs/spel/spring-core-spel/appendix/213-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- ✅ branch：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelExerciseTest.java`
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelExerciseSolutionTest test`
- ✅ perf：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part01_perf_concurrency/SpringCoreSpelConcurrencyLabTest.java`
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelConcurrencyLabTest test`

## spring-core-aop

- Maven artifactId：`spring-core-aop`
- Code module root：`spring-core-modules/spring-core-aop`

### Docs 入口
- ✅ deep-dive：`docs/aop/spring-core-aop/part-00-guide/029-00-deep-dive-guide.md`
- ✅ call-chain：`docs/aop/spring-core-aop/part-00-guide/029-01-aop-invocation-call-chain.md`
- ✅ breakpoint-map：`docs/aop/spring-core-aop/part-00-guide/029-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/aop/spring-core-aop/part-00-guide/029-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/aop/spring-core-aop/appendix/040-90-common-pitfalls.md`
- ✅ self-check：`docs/aop/spring-core-aop/appendix/041-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- ✅ branch（至少 1 组主入口）：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part00_guide/SpringCoreAopExerciseTest.java`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part00_guide/SpringCoreAopExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopExerciseSolutionTest test`
- ✅ perf：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_perf_concurrency/SpringCoreAopProxyConcurrencyLabTest.java`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyConcurrencyLabTest test`

## spring-core-aop-weaving

- Maven artifactId：`spring-core-aop-weaving`
- Code module root：`spring-core-modules/spring-core-aop-weaving`

### Docs 入口
- ✅ deep-dive：`docs/aop/spring-core-aop-weaving/part-00-guide/044-00-deep-dive-guide.md`
- ✅ call-chain：`docs/aop/spring-core-aop-weaving/part-00-guide/044-01-aspectj-weaving-call-chain.md`
- ✅ breakpoint-map：`docs/aop/spring-core-aop-weaving/part-00-guide/044-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/aop/spring-core-aop-weaving/part-00-guide/044-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/aop/spring-core-aop-weaving/appendix/049-90-common-pitfalls.md`
- ✅ self-check：`docs/aop/spring-core-aop-weaving/appendix/050-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjWeavingBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- ✅ branch（至少 1 组主入口）：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseTest.java`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=SpringCoreAopWeavingExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=SpringCoreAopWeavingExerciseSolutionTest test`
- ✅ perf：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_perf_concurrency/AspectjLtwConcurrencyLabTest.java`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwConcurrencyLabTest test`

## spring-core-events

- Maven artifactId：`spring-core-events`
- Code module root：`spring-core-modules/spring-core-events`

### Docs 入口
- ✅ deep-dive：`docs/events/spring-core-events/part-00-guide/128-00-deep-dive-guide.md`
- ✅ call-chain：`docs/events/spring-core-events/part-00-guide/128-01-events-call-chain.md`
- ✅ breakpoint-map：`docs/events/spring-core-events/part-00-guide/128-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/events/spring-core-events/part-00-guide/128-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/events/spring-core-events/appendix/136-90-common-pitfalls.md`
- ✅ self-check：`docs/events/spring-core-events/appendix/137-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
- ✅ branch（至少 1 组主入口）：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBasicsBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseTest.java`
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsExerciseSolutionTest test`
- ❌ perf：`(missing)`

### 缺失项
- tests:perf -> `(missing)`

## spring-core-validation

- Maven artifactId：`spring-core-validation`
- Code module root：`spring-core-modules/spring-core-validation`

### Docs 入口
- ✅ deep-dive：`docs/validation/spring-core-validation/part-00-guide/157-00-deep-dive-guide.md`
- ✅ call-chain：`docs/validation/spring-core-validation/part-00-guide/157-01-validation-call-chain.md`
- ✅ breakpoint-map：`docs/validation/spring-core-validation/part-00-guide/157-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/validation/spring-core-validation/part-00-guide/157-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/validation/spring-core-validation/appendix/164-90-common-pitfalls.md`
- ✅ self-check：`docs/validation/spring-core-validation/appendix/165-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- ✅ branch：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseTest.java`
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part00_guide/SpringCoreValidationExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationExerciseSolutionTest test`
- ✅ perf：`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part02_perf_concurrency/SpringCoreValidationValidatorConcurrencyLabTest.java`
  - `mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationValidatorConcurrencyLabTest test`

## spring-core-resources

- Maven artifactId：`spring-core-resources`
- Code module root：`spring-core-modules/spring-core-resources`

### Docs 入口
- ✅ deep-dive：`docs/resources/spring-core-resources/part-00-guide/140-00-deep-dive-guide.md`
- ✅ call-chain：`docs/resources/spring-core-resources/part-00-guide/140-01-resource-loading-call-chain.md`
- ✅ breakpoint-map：`docs/resources/spring-core-resources/part-00-guide/140-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/resources/spring-core-resources/part-00-guide/140-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/resources/spring-core-resources/appendix/147-90-common-pitfalls.md`
- ✅ self-check：`docs/resources/spring-core-resources/appendix/148-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- ✅ branch：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseTest.java`
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesExerciseSolutionTest test`
- ✅ perf：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part02_perf_concurrency/SpringCoreResourcesPatternResolverConcurrencyLabTest.java`
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesPatternResolverConcurrencyLabTest test`

## spring-core-tx

- Maven artifactId：`spring-core-tx`
- Code module root：`spring-core-modules/spring-core-tx`

### Docs 入口
- ✅ deep-dive：`docs/tx/spring-core-tx/part-00-guide/053-00-deep-dive-guide.md`
- ✅ call-chain：`docs/tx/spring-core-tx/part-00-guide/053-01-transaction-interceptor-call-chain.md`
- ✅ breakpoint-map：`docs/tx/spring-core-tx/part-00-guide/053-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/tx/spring-core-tx/part-00-guide/053-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/tx/spring-core-tx/appendix/060-90-common-pitfalls.md`
- ✅ self-check：`docs/tx/spring-core-tx/appendix/061-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- ✅ branch：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseTest.java`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxExerciseSolutionTest test`
- ✅ perf：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part02_perf_concurrency/SpringCoreTxThreadLocalBoundaryLabTest.java`
  - `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxThreadLocalBoundaryLabTest test`

## spring-core-profiles

- Maven artifactId：`spring-core-profiles`
- Code module root：`spring-core-modules/spring-core-profiles`

### Docs 入口
- ✅ deep-dive：`docs/profiles/spring-core-profiles/part-00-guide/151-00-deep-dive-guide.md`
- ✅ call-chain：`docs/profiles/spring-core-profiles/part-00-guide/151-01-profile-activation-call-chain.md`
- ✅ breakpoint-map：`docs/profiles/spring-core-profiles/part-00-guide/151-02-breakpoint-map.md`
- ✅ branch-matrix-doc：`docs/profiles/spring-core-profiles/part-00-guide/151-04-branch-decision-matrix.md`
- ✅ pitfalls：`docs/profiles/spring-core-profiles/appendix/153-90-common-pitfalls.md`
- ✅ self-check：`docs/profiles/spring-core-profiles/appendix/154-99-self-check.md`

### Tests 入口（可跑命令）
- ✅ book：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBookMatrixLabTest.java`
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- ✅ branch：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBranchMatrixLabTest.java`
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`
- ✅ exercise：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseTest.java`
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesExerciseTest test`
- ✅ solution：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseSolutionTest.java`
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesExerciseSolutionTest test`
- ✅ perf：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part02_perf_concurrency/SpringCoreProfilesEnvironmentConcurrencyLabTest.java`
  - `mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesEnvironmentConcurrencyLabTest test`

---

- 总模块数：22
- 缺失项总数：4
