package com.learning.springboot.springcoretx.appendix;

/**
 * 关键分支矩阵入口（Pitfalls）：聚合事务常见坑的可复现入口（自调用导致事务失效等），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreTxSelfInvocationPitfallLabTest.class
})
class SpringCoreTxPitfallsBranchMatrixLabTest {}

