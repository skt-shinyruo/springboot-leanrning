package com.learning.springboot.bootobservability.part00_guide;

/**
 * 关键分支矩阵入口（Observability）：聚合“请求 → 指标/观测信号”的最小分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootObservabilityLabTest.class
})
class BootObservabilityBranchMatrixLabTest {}
