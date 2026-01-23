package com.learning.springboot.bootautoconfiguration.part00_guide;

/**
 * 关键分支矩阵入口（AutoConfiguration）：聚合条件装配/Backoff/顺序等最小分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootAutoConfigurationLabTest.class
})
class BootAutoConfigurationBranchMatrixLabTest {}
