package com.learning.springboot.springcorebeans.part00_guide;

/**
 * 断点包入口：聚合高频排障分支与关键阶段的断点实验。
 * 用于与断点地图与分支矩阵形成可跑闭环。
 */
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansCircularDependencyBoundaryLabTest;
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansPostProcessorOrderingLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansBeanDefinitionOverridingLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansFactoryBeanEdgeCasesLabTest;
import com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansLazyLabTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansCircularDependencyBoundaryLabTest.class,
        SpringCoreBeansPostProcessorOrderingLabTest.class,
        SpringCoreBeansFactoryBeanEdgeCasesLabTest.class,
        SpringCoreBeansLazyLabTest.class,
        SpringCoreBeansBeanDefinitionOverridingLabTest.class
})
class SpringCoreBeansBreakpointPackLabTest {}
