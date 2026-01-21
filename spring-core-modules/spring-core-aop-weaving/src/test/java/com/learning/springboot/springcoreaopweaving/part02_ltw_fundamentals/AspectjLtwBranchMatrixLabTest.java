package com.learning.springboot.springcoreaopweaving.part02_ltw_fundamentals;

/**
 * 关键分支矩阵入口（LTW）：聚合 LTW（-javaagent）关键分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AspectjLtwLabTest.class
})
class AspectjLtwBranchMatrixLabTest {}

