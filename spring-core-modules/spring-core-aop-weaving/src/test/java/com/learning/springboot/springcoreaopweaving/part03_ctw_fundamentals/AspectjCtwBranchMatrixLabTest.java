package com.learning.springboot.springcoreaopweaving.part03_ctw_fundamentals;

/**
 * 关键分支矩阵入口（CTW）：聚合 CTW（编译期织入）关键分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AspectjCtwLabTest.class
})
class AspectjCtwBranchMatrixLabTest {}

