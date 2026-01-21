package com.learning.springboot.springcoretx.part01_transaction_basics;

/**
 * 关键分支矩阵入口：聚合事务关键分支（rollback rules / propagation），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreTxLabTest.class,
        SpringCoreTxRollbackRulesLabTest.class,
        SpringCoreTxPropagationMatrixLabTest.class
})
class SpringCoreTxBranchMatrixLabTest {}

