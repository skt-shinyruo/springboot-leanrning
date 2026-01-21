package com.learning.springboot.bootbusinesscase.part01_business_case;

/**
 * 关键分支矩阵入口：聚合 Business Case 的关键业务分支（成功/失败、事务边界），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootBusinessCaseLabTest.class,
        BootBusinessCaseServiceLabTest.class
})
class BootBusinessCaseBranchMatrixLabTest {}

