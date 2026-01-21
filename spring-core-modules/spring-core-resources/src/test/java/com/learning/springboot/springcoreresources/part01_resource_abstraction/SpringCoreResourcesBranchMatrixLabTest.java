package com.learning.springboot.springcoreresources.part01_resource_abstraction;

/**
 * 关键分支矩阵入口：聚合 Resource 抽象关键分支（classpath 模式、Jar/FS、编码），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreResourcesLabTest.class,
        SpringCoreResourcesMechanicsLabTest.class
})
class SpringCoreResourcesBranchMatrixLabTest {}

