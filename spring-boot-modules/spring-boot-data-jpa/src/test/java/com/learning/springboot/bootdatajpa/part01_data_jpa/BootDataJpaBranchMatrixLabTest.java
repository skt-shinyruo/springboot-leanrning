package com.learning.springboot.bootdatajpa.part01_data_jpa;

/**
 * 关键分支矩阵入口：聚合 Data JPA 的关键状态分支（merge/detach、SQL 证据链等），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootDataJpaLabTest.class,
        BootDataJpaMergeAndDetachLabTest.class,
        BootDataJpaDebugSqlLabTest.class
})
class BootDataJpaBranchMatrixLabTest {}

