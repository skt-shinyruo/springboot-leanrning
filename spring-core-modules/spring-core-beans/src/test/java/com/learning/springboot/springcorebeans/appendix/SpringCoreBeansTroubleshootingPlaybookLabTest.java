package com.learning.springboot.springcorebeans.appendix;

/**
 * 排障 Playbook 入口：聚合高频故障与边界的复现实验。
 * 用于排障手册与断点包的回归入口。
 */
import com.learning.springboot.springcorebeans.appendix.SpringCoreBeansGenericTypeMatchingPitfallsLabTest;
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansCircularDependencyBoundaryLabTest;
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansEarlyReferenceLabTest;
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansRawInjectionDespiteWrappingLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansBeanDefinitionOverridingLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansContextHierarchyLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansFactoryBeanEdgeCasesLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansInjectionAmbiguityLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansLazyLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansValuePlaceholderResolutionLabTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansInjectionAmbiguityLabTest.class,
        SpringCoreBeansCircularDependencyBoundaryLabTest.class,
        SpringCoreBeansEarlyReferenceLabTest.class,
        SpringCoreBeansRawInjectionDespiteWrappingLabTest.class,
        SpringCoreBeansFactoryBeanEdgeCasesLabTest.class,
        SpringCoreBeansBeanDefinitionOverridingLabTest.class,
        SpringCoreBeansLazyLabTest.class,
        SpringCoreBeansValuePlaceholderResolutionLabTest.class,
        SpringCoreBeansContextHierarchyLabTest.class,
        SpringCoreBeansGenericTypeMatchingPitfallsLabTest.class
})
class SpringCoreBeansTroubleshootingPlaybookLabTest {}
