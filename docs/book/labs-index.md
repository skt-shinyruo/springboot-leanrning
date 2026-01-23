# Labs 索引（可跑入口）

> 本页由 `scripts/generate-book-labs-index.py` 生成。新增/移动 `*LabTest.java` 后请重新生成。

## 运行方式速记

- 全仓库：`mvn -q test`
- 单模块：`mvn -q -pl :<artifactId> test`
- 单类：`mvn -q -pl :<artifactId> -Dtest=<SomeLabTest> test`

## 按模块

### springboot-basics

- 数量：6
- 模块 docs：[`docs/basics/springboot-basics/README.md`](../basics/springboot-basics/README.md)

- [`BootBasicsBookMatrixLabTest`](../../spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- [`BootBasicsBranchMatrixLabTest`](../../spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
- [`BootBasicsDefaultLabTest`](../../spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java)
  - 运行：`mvn -q -pl :springboot-basics -Dtest=BootBasicsDefaultLabTest test`
- [`BootBasicsDevLabTest`](../../spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java)
  - 运行：`mvn -q -pl :springboot-basics -Dtest=BootBasicsDevLabTest test`
- [`BootBasicsOverrideLabTest`](../../spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java)
  - 运行：`mvn -q -pl :springboot-basics -Dtest=BootBasicsOverrideLabTest test`
- [`BootBasicsEnvironmentConcurrencyLabTest`](../../spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part02_perf_concurrency/BootBasicsEnvironmentConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :springboot-basics -Dtest=BootBasicsEnvironmentConcurrencyLabTest test`

### spring-core-beans

- 数量：72
- 模块 docs：[`docs/beans/spring-core-beans/README.md`](../beans/spring-core-beans/README.md)

- [`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest test`
- [`SpringCoreBeansLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test`
- [`SpringCoreBeansBeanFactoryVsApplicationContextLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanFactoryVsApplicationContextLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanFactoryVsApplicationContextLabTest test`
- [`SpringCoreBeansBeanGraphDebugLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanGraphDebugLabTest test`
- [`SpringCoreBeansBookMatrixLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- [`SpringCoreBeansComponentScanLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansComponentScanLabTest test`
- [`SpringCoreBeansContainerLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`
- [`SpringCoreBeansImportLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansImportLabTest test`
- [`SpringCoreBeansIocBranchMatrixLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- [`SpringCoreBeansAutoConfigurationBackoffTimingLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationBackoffTimingLabTest test`
- [`SpringCoreBeansAutoConfigurationImportOrderingLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationImportOrderingLabTest test`
- [`SpringCoreBeansAutoConfigurationLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationLabTest test`
- [`SpringCoreBeansAutoConfigurationOrderingLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationOrderingLabTest test`
- [`SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationOverrideMatrixLabTest test`
- [`SpringCoreBeansBeanDefinitionOriginLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionOriginLabTest test`
- [`SpringCoreBeansConditionEvaluationReportLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansConditionEvaluationReportLabTest test`
- [`SpringCoreBeansExceptionNavigationLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansExceptionNavigationLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansExceptionNavigationLabTest test`
- [`SpringCoreBeansProfileRegistrationLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansProfileRegistrationLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansProfileRegistrationLabTest test`
- [`SpringCoreBeansConcurrentGetBeanLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_perf_concurrency/SpringCoreBeansConcurrentGetBeanLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansConcurrentGetBeanLabTest test`
- [`SpringCoreBeansAwareInfrastructureLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAwareInfrastructureLabTest test`
- [`SpringCoreBeansBeanCreationTraceLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest test`
- [`SpringCoreBeansBootstrapInternalsLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBootstrapInternalsLabTest test`
- [`SpringCoreBeansCircularDependencyBoundaryLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansCircularDependencyBoundaryLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansCircularDependencyBoundaryLabTest test`
- [`SpringCoreBeansEarlyReferenceLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansEarlyReferenceLabTest test`
- [`SpringCoreBeansInternalsBranchMatrixLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInternalsBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`
- [`SpringCoreBeansLifecycleCallbackOrderLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleCallbackOrderLabTest test`
- [`SpringCoreBeansPostProcessorOrderingLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansPostProcessorOrderingLabTest test`
- [`SpringCoreBeansPreInstantiationLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansPreInstantiationLabTest test`
- [`SpringCoreBeansPrototypeDestroySemanticsLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansPrototypeDestroySemanticsLabTest test`
- [`SpringCoreBeansRawInjectionDespiteWrappingLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRawInjectionDespiteWrappingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansRawInjectionDespiteWrappingLabTest test`
- [`SpringCoreBeansRegistryPostProcessorLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansRegistryPostProcessorLabTest test`
- [`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansStaticBeanFactoryPostProcessorLabTest test`
- [`SpringCoreBeansAutowireCandidateSelectionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutowireCandidateSelectionLabTest test`
- [`SpringCoreBeansBeanDefinitionOverridingLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionOverridingLabTest test`
- [`SpringCoreBeansBeanFactoryApiLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanFactoryApiLabTest test`
- [`SpringCoreBeansBeanNameAliasLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanNameAliasLabTest test`
- [`SpringCoreBeansBeansSupportUtilitiesLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeansSupportUtilitiesLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeansSupportUtilitiesLabTest test`
- [`SpringCoreBeansContextHierarchyLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansContextHierarchyLabTest test`
- [`SpringCoreBeansCustomScopeLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansCustomScopeLabTest test`
- [`SpringCoreBeansDependsOnLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansDependsOnLabTest test`
- [`SpringCoreBeansEnvironmentPropertySourceLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansEnvironmentPropertySourceLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansEnvironmentPropertySourceLabTest test`
- [`SpringCoreBeansFactoryBeanDeepDiveLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanDeepDiveLabTest test`
- [`SpringCoreBeansFactoryBeanEdgeCasesLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanEdgeCasesLabTest test`
- [`SpringCoreBeansInjectionAmbiguityLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionAmbiguityLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInjectionAmbiguityLabTest test`
- [`SpringCoreBeansInjectionPhaseLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInjectionPhaseLabTest test`
- [`SpringCoreBeansJsr330InjectionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansJsr330InjectionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansJsr330InjectionLabTest test`
- [`SpringCoreBeansLazyLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansLazyLabTest test`
- [`SpringCoreBeansMergedBeanDefinitionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansMergedBeanDefinitionLabTest test`
- [`SpringCoreBeansOptionalInjectionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansOptionalInjectionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansOptionalInjectionLabTest test`
- [`SpringCoreBeansProgrammaticBeanPostProcessorLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansProgrammaticBeanPostProcessorLabTest test`
- [`SpringCoreBeansProgrammaticRegistrationLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansProgrammaticRegistrationLabTest test`
- [`SpringCoreBeansProxyingPhaseLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansProxyingPhaseLabTest test`
- [`SpringCoreBeansResolvableDependencyLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansResolvableDependencyLabTest test`
- [`SpringCoreBeansResourceInjectionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansResourceInjectionLabTest test`
- [`SpringCoreBeansSmartInitializingSingletonLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansSmartInitializingSingletonLabTest test`
- [`SpringCoreBeansSmartLifecycleLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansSmartLifecycleLabTest test`
- [`SpringCoreBeansTypeConversionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansTypeConversionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansTypeConversionLabTest test`
- [`SpringCoreBeansValuePlaceholderResolutionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansValuePlaceholderResolutionLabTest test`
- [`SpringCoreBeansAotFactoriesLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotFactoriesLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAotFactoriesLabTest test`
- [`SpringCoreBeansAotRuntimeHintsLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAotRuntimeHintsLabTest test`
- [`SpringCoreBeansAutowireCapableBeanFactoryLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutowireCapableBeanFactoryLabTest test`
- [`SpringCoreBeansBeanDefinitionValueResolutionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBeanDefinitionValueResolutionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionValueResolutionLabTest test`
- [`SpringCoreBeansBuiltInFactoryBeansLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBuiltInFactoryBeansLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBuiltInFactoryBeansLabTest test`
- [`SpringCoreBeansCustomQualifierLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansCustomQualifierLabTest test`
- [`SpringCoreBeansGroovyBeanDefinitionReaderLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansGroovyBeanDefinitionReaderLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansGroovyBeanDefinitionReaderLabTest test`
- [`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertiesBeanDefinitionReaderLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansPropertiesBeanDefinitionReaderLabTest test`
- [`SpringCoreBeansPropertyEditorLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertyEditorLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansPropertyEditorLabTest test`
- [`SpringCoreBeansReplacedMethodLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansReplacedMethodLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansReplacedMethodLabTest test`
- [`SpringCoreBeansServiceLoaderFactoryBeansLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansServiceLoaderFactoryBeansLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansServiceLoaderFactoryBeansLabTest test`
- [`SpringCoreBeansSpelValueLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansSpelValueLabTest test`
- [`SpringCoreBeansXmlBeanDefinitionReaderLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansXmlBeanDefinitionReaderLabTest test`
- [`SpringCoreBeansXmlNamespaceExtensionLabTest`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java)
  - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansXmlNamespaceExtensionLabTest test`

### spring-core-aop

- 数量：13
- 模块 docs：[`docs/aop/spring-core-aop/README.md`](../aop/spring-core-aop/README.md)

- [`SpringCoreAopBookMatrixLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- [`SpringCoreAopExposeProxyLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopExposeProxyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopExposeProxyLabTest test`
- [`SpringCoreAopLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopLabTest test`
- [`SpringCoreAopProxyBranchMatrixLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
- [`SpringCoreAopProxyMechanicsLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyMechanicsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyMechanicsLabTest test`
- [`SpringCoreAopAutoProxyBranchMatrixLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`
- [`SpringCoreAopAutoProxyCreatorInternalsLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyCreatorInternalsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyCreatorInternalsLabTest test`
- [`SpringCoreAopPointcutExpressionsLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopPointcutExpressionsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopPointcutExpressionsLabTest test`
- [`SpringCoreAopProxyConcurrencyLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_perf_concurrency/SpringCoreAopProxyConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyConcurrencyLabTest test`
- [`SpringCoreAopMultiProxyStackingLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopMultiProxyStackingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopMultiProxyStackingLabTest test`
- [`SpringCoreAopProceedNestingLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopProceedNestingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProceedNestingLabTest test`
- [`SpringCoreAopRealWorldStackingLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopRealWorldStackingLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopRealWorldStackingLabTest test`
- [`SpringCoreAopStackingBranchMatrixLabTest`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopStackingBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`

### spring-core-aop-weaving

- 数量：6
- 模块 docs：[`docs/aop/spring-core-aop-weaving/README.md`](../aop/spring-core-aop-weaving/README.md)

- [`AspectjLtwBranchMatrixLabTest`](../../spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
- [`AspectjLtwLabTest`](../../spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwLabTest test`
- [`AspectjWeavingBookMatrixLabTest`](../../spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjWeavingBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- [`AspectjLtwConcurrencyLabTest`](../../spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_perf_concurrency/AspectjLtwConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwConcurrencyLabTest test`
- [`AspectjCtwBranchMatrixLabTest`](../../spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
- [`AspectjCtwLabTest`](../../spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwLabTest.java)
  - 运行：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwLabTest test`

### spring-core-tx

- 数量：8
- 模块 docs：[`docs/tx/spring-core-tx/README.md`](../tx/spring-core-tx/README.md)

- [`SpringCoreTxPitfallsBranchMatrixLabTest`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxPitfallsBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`
- [`SpringCoreTxSelfInvocationPitfallLabTest`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxSelfInvocationPitfallLabTest.java)
  - 运行：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxSelfInvocationPitfallLabTest test`
- [`SpringCoreTxBookMatrixLabTest`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- [`SpringCoreTxBranchMatrixLabTest`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
- [`SpringCoreTxLabTest`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java)
  - 运行：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxLabTest test`
- [`SpringCoreTxPropagationMatrixLabTest`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxPropagationMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPropagationMatrixLabTest test`
- [`SpringCoreTxRollbackRulesLabTest`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxRollbackRulesLabTest.java)
  - 运行：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxRollbackRulesLabTest test`
- [`SpringCoreTxThreadLocalBoundaryLabTest`](../../spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part02_perf_concurrency/SpringCoreTxThreadLocalBoundaryLabTest.java)
  - 运行：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxThreadLocalBoundaryLabTest test`

### springboot-web-mvc

- 数量：23
- 模块 docs：[`docs/web-mvc/springboot-web-mvc/README.md`](../web-mvc/springboot-web-mvc/README.md)

- [`BootWebMvcBindingDeepDiveLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBindingDeepDiveLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcBindingDeepDiveLabTest test`
- [`BootWebMvcBookMatrixLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- [`BootWebMvcLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcLabTest test`
- [`BootWebMvcSpringBootLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcSpringBootLabTest test`
- [`BootWebMvcRequestScopeIsolationLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_perf_concurrency/BootWebMvcRequestScopeIsolationLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcRequestScopeIsolationLabTest test`
- [`BootWebMvcErrorViewLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcErrorViewLabTest test`
- [`BootWebMvcViewLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcViewLabTest test`
- [`BootWebMvcViewSpringBootLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcViewSpringBootLabTest test`
- [`BootWebMvcExceptionResolverChainLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcExceptionResolverChainLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcExceptionResolverChainLabTest test`
- [`BootWebMvcInternalsLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcInternalsLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcInternalsLabTest test`
- [`BootWebMvcMessageConverterTraceLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcMessageConverterTraceLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcMessageConverterTraceLabTest test`
- [`BootWebMvcTraceLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcTraceLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcTraceLabTest test`
- [`BootWebMvcContractJacksonLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part04_contract/BootWebMvcContractJacksonLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcContractJacksonLabTest test`
- [`BootWebMvcProblemDetailLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part04_contract/BootWebMvcProblemDetailLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcProblemDetailLabTest test`
- [`BootWebMvcRealWorldHttpLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part05_real_world/BootWebMvcRealWorldHttpLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcRealWorldHttpLabTest test`
- [`BootWebMvcAsyncSseLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part06_async_sse/BootWebMvcAsyncSseLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcAsyncSseLabTest test`
- [`BootWebMvcErrorBranchMatrixLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcErrorBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
- [`BootWebMvcTestingDebuggingLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcTestingDebuggingLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcTestingDebuggingLabTest test`
- [`BootWebMvcObservabilityLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcObservabilityLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcObservabilityLabTest test`
- [`BootWebMvcSecurityLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcSecurityLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcSecurityLabTest test`
- [`BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcSecurityVsMvcExceptionBoundaryLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcSecurityVsMvcExceptionBoundaryLabTest test`
- [`BootWebMvcAdviceOrderLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part09_advice_order/BootWebMvcAdviceOrderLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcAdviceOrderLabTest test`
- [`BootWebMvcAdviceMatchingLabTest`](../../spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part10_advice_matching/BootWebMvcAdviceMatchingLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcAdviceMatchingLabTest test`

### springboot-security

- 数量：6
- 模块 docs：[`docs/security/springboot-security/README.md`](../security/springboot-security/README.md)

- [`BootSecurityBookMatrixLabTest`](../../spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-security -Dtest=BootSecurityBookMatrixLabTest test`
- [`BootSecurityBranchMatrixLabTest`](../../spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-security -Dtest=BootSecurityBranchMatrixLabTest test`
- [`BootSecurityDevProfileLabTest`](../../spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityDevProfileLabTest.java)
  - 运行：`mvn -q -pl :springboot-security -Dtest=BootSecurityDevProfileLabTest test`
- [`BootSecurityLabTest`](../../spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java)
  - 运行：`mvn -q -pl :springboot-security -Dtest=BootSecurityLabTest test`
- [`BootSecurityMultiFilterChainOrderLabTest`](../../spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityMultiFilterChainOrderLabTest.java)
  - 运行：`mvn -q -pl :springboot-security -Dtest=BootSecurityMultiFilterChainOrderLabTest test`
- [`BootSecuritySecurityContextIsolationLabTest`](../../spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part02_perf_concurrency/BootSecuritySecurityContextIsolationLabTest.java)
  - 运行：`mvn -q -pl :springboot-security -Dtest=BootSecuritySecurityContextIsolationLabTest test`

### springboot-data-jpa

- 数量：6
- 模块 docs：[`docs/data-jpa/springboot-data-jpa/README.md`](../data-jpa/springboot-data-jpa/README.md)

- [`BootDataJpaBookMatrixLabTest`](../../spring-boot-modules/springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- [`BootDataJpaBranchMatrixLabTest`](../../spring-boot-modules/springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`
- [`BootDataJpaDebugSqlLabTest`](../../spring-boot-modules/springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaDebugSqlLabTest.java)
  - 运行：`mvn -q -pl :springboot-data-jpa -Dtest=BootDataJpaDebugSqlLabTest test`
- [`BootDataJpaLabTest`](../../spring-boot-modules/springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaLabTest.java)
  - 运行：`mvn -q -pl :springboot-data-jpa -Dtest=BootDataJpaLabTest test`
- [`BootDataJpaMergeAndDetachLabTest`](../../spring-boot-modules/springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaMergeAndDetachLabTest.java)
  - 运行：`mvn -q -pl :springboot-data-jpa -Dtest=BootDataJpaMergeAndDetachLabTest test`
- [`BootDataJpaEntityManagerConcurrencyLabTest`](../../spring-boot-modules/springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part02_perf_concurrency/BootDataJpaEntityManagerConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :springboot-data-jpa -Dtest=BootDataJpaEntityManagerConcurrencyLabTest test`

### springboot-cache

- 数量：5
- 模块 docs：[`docs/cache/springboot-cache/README.md`](../cache/springboot-cache/README.md)

- [`BootCacheBookMatrixLabTest`](../../spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-cache -Dtest=BootCacheBookMatrixLabTest test`
- [`BootCacheBranchMatrixLabTest`](../../spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-cache -Dtest=BootCacheBranchMatrixLabTest test`
- [`BootCacheLabTest`](../../spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java)
  - 运行：`mvn -q -pl :springboot-cache -Dtest=BootCacheLabTest test`
- [`BootCacheSpelKeyLabTest`](../../spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheSpelKeyLabTest.java)
  - 运行：`mvn -q -pl :springboot-cache -Dtest=BootCacheSpelKeyLabTest test`
- [`BootCacheStampedeProtectionLabTest`](../../spring-boot-modules/springboot-cache/src/test/java/com/learning/springboot/bootcache/part02_perf_concurrency/BootCacheStampedeProtectionLabTest.java)
  - 运行：`mvn -q -pl :springboot-cache -Dtest=BootCacheStampedeProtectionLabTest test`

### springboot-async-scheduling

- 数量：5
- 模块 docs：[`docs/async-scheduling/springboot-async-scheduling/README.md`](../async-scheduling/springboot-async-scheduling/README.md)

- [`BootAsyncSchedulingBookMatrixLabTest`](../../spring-boot-modules/springboot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- [`BootAsyncSchedulingBranchMatrixLabTest`](../../spring-boot-modules/springboot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`
- [`BootAsyncSchedulingLabTest`](../../spring-boot-modules/springboot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java)
  - 运行：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingLabTest test`
- [`BootAsyncSchedulingSchedulingLabTest`](../../spring-boot-modules/springboot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingSchedulingLabTest.java)
  - 运行：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingSchedulingLabTest test`
- [`BootAsyncSchedulingExecutorSaturationLabTest`](../../spring-boot-modules/springboot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part02_perf_concurrency/BootAsyncSchedulingExecutorSaturationLabTest.java)
  - 运行：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingExecutorSaturationLabTest test`

### spring-core-events

- 数量：8
- 模块 docs：[`docs/events/spring-core-events/README.md`](../events/spring-core-events/README.md)

- [`SpringCoreEventsBasicsBranchMatrixLabTest`](../../spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBasicsBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`
- [`SpringCoreEventsBookMatrixLabTest`](../../spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`
- [`SpringCoreEventsLabTest`](../../spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsLabTest test`
- [`SpringCoreEventsListenerFilteringLabTest`](../../spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsListenerFilteringLabTest.java)
  - 运行：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsListenerFilteringLabTest test`
- [`SpringCoreEventsMechanicsLabTest`](../../spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsMechanicsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsMechanicsLabTest test`
- [`SpringCoreEventsAsyncMulticasterLabTest`](../../spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsAsyncMulticasterLabTest.java)
  - 运行：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncMulticasterLabTest test`
- [`SpringCoreEventsAsyncTransactionalBranchMatrixLabTest`](../../spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsAsyncTransactionalBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`
- [`SpringCoreEventsTransactionalEventLabTest`](../../spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsTransactionalEventLabTest.java)
  - 运行：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsTransactionalEventLabTest test`

### spring-core-resources

- 数量：5
- 模块 docs：[`docs/resources/spring-core-resources/README.md`](../resources/spring-core-resources/README.md)

- [`SpringCoreResourcesBookMatrixLabTest`](../../spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- [`SpringCoreResourcesBranchMatrixLabTest`](../../spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
- [`SpringCoreResourcesLabTest`](../../spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesLabTest.java)
  - 运行：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesLabTest test`
- [`SpringCoreResourcesMechanicsLabTest`](../../spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesMechanicsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesMechanicsLabTest test`
- [`SpringCoreResourcesPatternResolverConcurrencyLabTest`](../../spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part02_perf_concurrency/SpringCoreResourcesPatternResolverConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesPatternResolverConcurrencyLabTest test`

### spring-core-profiles

- 数量：5
- 模块 docs：[`docs/profiles/spring-core-profiles/README.md`](../profiles/spring-core-profiles/README.md)

- [`SpringCoreProfilesBookMatrixLabTest`](../../spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- [`SpringCoreProfilesBranchMatrixLabTest`](../../spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`
- [`SpringCoreProfilesLabTest`](../../spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesLabTest.java)
  - 运行：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesLabTest test`
- [`SpringCoreProfilesProfilePrecedenceLabTest`](../../spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesProfilePrecedenceLabTest.java)
  - 运行：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesProfilePrecedenceLabTest test`
- [`SpringCoreProfilesEnvironmentConcurrencyLabTest`](../../spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part02_perf_concurrency/SpringCoreProfilesEnvironmentConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesEnvironmentConcurrencyLabTest test`

### spring-core-validation

- 数量：5
- 模块 docs：[`docs/validation/spring-core-validation/README.md`](../validation/spring-core-validation/README.md)

- [`SpringCoreValidationBookMatrixLabTest`](../../spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- [`SpringCoreValidationBranchMatrixLabTest`](../../spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`
- [`SpringCoreValidationLabTest`](../../spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationLabTest.java)
  - 运行：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationLabTest test`
- [`SpringCoreValidationMechanicsLabTest`](../../spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationMechanicsLabTest.java)
  - 运行：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationMechanicsLabTest test`
- [`SpringCoreValidationValidatorConcurrencyLabTest`](../../spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part02_perf_concurrency/SpringCoreValidationValidatorConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationValidatorConcurrencyLabTest test`

### springboot-actuator

- 数量：5
- 模块 docs：[`docs/actuator/springboot-actuator/README.md`](../actuator/springboot-actuator/README.md)

- [`BootActuatorBookMatrixLabTest`](../../spring-boot-modules/springboot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- [`BootActuatorBranchMatrixLabTest`](../../spring-boot-modules/springboot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`
- [`BootActuatorExposureOverrideLabTest`](../../spring-boot-modules/springboot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java)
  - 运行：`mvn -q -pl :springboot-actuator -Dtest=BootActuatorExposureOverrideLabTest test`
- [`BootActuatorLabTest`](../../spring-boot-modules/springboot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java)
  - 运行：`mvn -q -pl :springboot-actuator -Dtest=BootActuatorLabTest test`
- [`BootActuatorMetricsConcurrencyLabTest`](../../spring-boot-modules/springboot-actuator/src/test/java/com/learning/springboot/bootactuator/part02_perf_concurrency/BootActuatorMetricsConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :springboot-actuator -Dtest=BootActuatorMetricsConcurrencyLabTest test`

### springboot-web-client

- 数量：6
- 模块 docs：[`docs/web-client/springboot-web-client/README.md`](../web-client/springboot-web-client/README.md)

- [`BootWebClientBookMatrixLabTest`](../../spring-boot-modules/springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- [`BootWebClientBranchMatrixLabTest`](../../spring-boot-modules/springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
- [`BootWebClientRestClientLabTest`](../../spring-boot-modules/springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientRestClientLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-client -Dtest=BootWebClientRestClientLabTest test`
- [`BootWebClientWebClientFilterOrderLabTest`](../../spring-boot-modules/springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientFilterOrderLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-client -Dtest=BootWebClientWebClientFilterOrderLabTest test`
- [`BootWebClientWebClientLabTest`](../../spring-boot-modules/springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-client -Dtest=BootWebClientWebClientLabTest test`
- [`BootWebClientRestClientConcurrencyLabTest`](../../spring-boot-modules/springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part02_perf_concurrency/BootWebClientRestClientConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :springboot-web-client -Dtest=BootWebClientRestClientConcurrencyLabTest test`

### springboot-testing

- 数量：6
- 模块 docs：[`docs/testing/springboot-testing/README.md`](../testing/springboot-testing/README.md)

- [`BootTestingBookMatrixLabTest`](../../spring-boot-modules/springboot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-testing -Dtest=BootTestingBookMatrixLabTest test`
- [`BootTestingBranchMatrixLabTest`](../../spring-boot-modules/springboot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-testing -Dtest=BootTestingBranchMatrixLabTest test`
- [`BootTestingMockBeanLabTest`](../../spring-boot-modules/springboot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java)
  - 运行：`mvn -q -pl :springboot-testing -Dtest=BootTestingMockBeanLabTest test`
- [`GreetingControllerSpringBootLabTest`](../../spring-boot-modules/springboot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java)
  - 运行：`mvn -q -pl :springboot-testing -Dtest=GreetingControllerSpringBootLabTest test`
- [`GreetingControllerWebMvcLabTest`](../../spring-boot-modules/springboot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java)
  - 运行：`mvn -q -pl :springboot-testing -Dtest=GreetingControllerWebMvcLabTest test`
- [`BootTestingTestContextCacheLabTest`](../../spring-boot-modules/springboot-testing/src/test/java/com/learning/springboot/boottesting/part02_perf_concurrency/BootTestingTestContextCacheLabTest.java)
  - 运行：`mvn -q -pl :springboot-testing -Dtest=BootTestingTestContextCacheLabTest test`

### springboot-business-case

- 数量：5
- 模块 docs：[`docs/business-case/springboot-business-case/README.md`](../business-case/springboot-business-case/README.md)

- [`BootBusinessCaseBookMatrixLabTest`](../../spring-boot-modules/springboot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- [`BootBusinessCaseBranchMatrixLabTest`](../../spring-boot-modules/springboot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
- [`BootBusinessCaseLabTest`](../../spring-boot-modules/springboot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java)
  - 运行：`mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseLabTest test`
- [`BootBusinessCaseServiceLabTest`](../../spring-boot-modules/springboot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseServiceLabTest.java)
  - 运行：`mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseServiceLabTest test`
- [`BootBusinessCaseConcurrentOrderPlacementLabTest`](../../spring-boot-modules/springboot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part02_perf_concurrency/BootBusinessCaseConcurrentOrderPlacementLabTest.java)
  - 运行：`mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseConcurrentOrderPlacementLabTest test`

### spring-core-spel

- 数量：4
- 模块 docs：[`docs/spel/spring-core-spel/README.md`](../spel/spring-core-spel/README.md)

- [`SpringCoreSpelBookMatrixLabTest`](../../spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- [`SpringCoreSpelBranchMatrixLabTest`](../../spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`
- [`SpringCoreSpelLabTest`](../../spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelLabTest.java)
  - 运行：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelLabTest test`
- [`SpringCoreSpelConcurrencyLabTest`](../../spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part01_perf_concurrency/SpringCoreSpelConcurrencyLabTest.java)
  - 运行：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelConcurrencyLabTest test`

### springboot-autoconfiguration

- 数量：3
- 模块 docs：[`docs/autoconfig/springboot-autoconfiguration/README.md`](../autoconfig/springboot-autoconfiguration/README.md)

- [`BootAutoConfigurationBookMatrixLabTest`](../../spring-boot-modules/springboot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- [`BootAutoConfigurationBranchMatrixLabTest`](../../spring-boot-modules/springboot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`
- [`BootAutoConfigurationLabTest`](../../spring-boot-modules/springboot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationLabTest.java)
  - 运行：`mvn -q -pl :springboot-autoconfiguration -Dtest=BootAutoConfigurationLabTest test`

### springboot-logging

- 数量：3
- 模块 docs：[`docs/logging/springboot-logging/README.md`](../logging/springboot-logging/README.md)

- [`BootLoggingBookMatrixLabTest`](../../spring-boot-modules/springboot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- [`BootLoggingBranchMatrixLabTest`](../../spring-boot-modules/springboot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-logging -Dtest=BootLoggingBranchMatrixLabTest test`
- [`BootLoggingLabTest`](../../spring-boot-modules/springboot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingLabTest.java)
  - 运行：`mvn -q -pl :springboot-logging -Dtest=BootLoggingLabTest test`

### springboot-observability

- 数量：3
- 模块 docs：[`docs/observability/springboot-observability/README.md`](../observability/springboot-observability/README.md)

- [`BootObservabilityBookMatrixLabTest`](../../spring-boot-modules/springboot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBookMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- [`BootObservabilityBranchMatrixLabTest`](../../spring-boot-modules/springboot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBranchMatrixLabTest.java)
  - 运行：`mvn -q -pl :springboot-observability -Dtest=BootObservabilityBranchMatrixLabTest test`
- [`BootObservabilityLabTest`](../../spring-boot-modules/springboot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityLabTest.java)
  - 运行：`mvn -q -pl :springboot-observability -Dtest=BootObservabilityLabTest test`
