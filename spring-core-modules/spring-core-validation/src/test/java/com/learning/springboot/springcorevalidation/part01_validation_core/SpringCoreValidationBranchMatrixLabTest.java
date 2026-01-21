package com.learning.springboot.springcorevalidation.part01_validation_core;

/**
 * 关键分支矩阵入口：聚合 Validation 关键分支（programmatic/method validation、groups 等），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreValidationLabTest.class,
        SpringCoreValidationMechanicsLabTest.class
})
class SpringCoreValidationBranchMatrixLabTest {}

