package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

/**
 * 关键分支矩阵入口（AutoProxy）：聚合自动代理与切点表达式关键分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreAopAutoProxyCreatorInternalsLabTest.class,
        SpringCoreAopPointcutExpressionsLabTest.class
})
class SpringCoreAopAutoProxyBranchMatrixLabTest {}

