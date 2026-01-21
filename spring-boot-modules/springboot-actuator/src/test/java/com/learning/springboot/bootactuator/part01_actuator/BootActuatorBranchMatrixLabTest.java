package com.learning.springboot.bootactuator.part01_actuator;

/**
 * 关键分支矩阵入口：聚合 Actuator 的关键配置分支（exposure override 等），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootActuatorLabTest.class,
        BootActuatorExposureOverrideLabTest.class
})
class BootActuatorBranchMatrixLabTest {}

