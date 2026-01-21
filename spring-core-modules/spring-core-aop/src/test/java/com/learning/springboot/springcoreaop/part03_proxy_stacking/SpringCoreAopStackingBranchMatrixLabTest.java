package com.learning.springboot.springcoreaop.part03_proxy_stacking;

/**
 * 关键分支矩阵入口（Stacking）：聚合多层代理与 proceed 嵌套关键分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreAopMultiProxyStackingLabTest.class,
        SpringCoreAopProceedNestingLabTest.class,
        SpringCoreAopRealWorldStackingLabTest.class
})
class SpringCoreAopStackingBranchMatrixLabTest {}

