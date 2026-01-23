package com.learning.springboot.springcorespel.part00_guide;

/**
 * 关键分支矩阵入口（SpEL）：聚合 parser/evaluation context 的关键分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreSpelLabTest.class
})
class SpringCoreSpelBranchMatrixLabTest {}
