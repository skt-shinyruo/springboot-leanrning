package com.learning.springboot.springcoreprofiles.part01_profiles;

/**
 * 关键分支矩阵入口：聚合 Profile 激活与优先级关键分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreProfilesLabTest.class,
        SpringCoreProfilesProfilePrecedenceLabTest.class
})
class SpringCoreProfilesBranchMatrixLabTest {}

