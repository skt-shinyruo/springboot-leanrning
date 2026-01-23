package com.learning.springboot.bootlogging.part00_guide;

/**
 * 关键分支矩阵入口（Logging）：聚合日志级别/分类等最小分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootLoggingLabTest.class
})
class BootLoggingBranchMatrixLabTest {}
