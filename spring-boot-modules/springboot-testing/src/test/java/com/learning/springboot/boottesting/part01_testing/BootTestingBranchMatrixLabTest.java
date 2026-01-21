package com.learning.springboot.boottesting.part01_testing;

/**
 * 关键分支矩阵入口：聚合 Testing 的关键分支（slice / mock / webmvc vs springboot），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootTestingMockBeanLabTest.class,
        GreetingControllerWebMvcLabTest.class,
        GreetingControllerSpringBootLabTest.class
})
class BootTestingBranchMatrixLabTest {}

