package com.learning.springboot.springcorebeans.part03_container_internals;

/**
 * 关键分支矩阵入口（Internals）：聚合容器内部关键分支（循环依赖、早期引用、后置处理器排序），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansCircularDependencyBoundaryLabTest.class,
        SpringCoreBeansEarlyReferenceLabTest.class,
        SpringCoreBeansPostProcessorOrderingLabTest.class
})
class SpringCoreBeansInternalsBranchMatrixLabTest {}

