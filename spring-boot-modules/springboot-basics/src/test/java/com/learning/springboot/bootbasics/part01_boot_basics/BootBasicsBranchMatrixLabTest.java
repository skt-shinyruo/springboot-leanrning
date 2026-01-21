package com.learning.springboot.bootbasics.part01_boot_basics;

/**
 * 关键分支矩阵入口：聚合 Boot Basics 的关键配置分支（profiles / test override），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootBasicsDevLabTest.class,
        BootBasicsOverrideLabTest.class
})
class BootBasicsBranchMatrixLabTest {}

