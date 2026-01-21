package com.learning.springboot.bootsecurity.part01_security;

/**
 * 关键分支矩阵入口：聚合 Security 的关键分支（多 filter chain、profile 差异），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootSecurityLabTest.class,
        BootSecurityDevProfileLabTest.class,
        BootSecurityMultiFilterChainOrderLabTest.class
})
class BootSecurityBranchMatrixLabTest {}

