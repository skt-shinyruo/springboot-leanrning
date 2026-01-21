package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

/**
 * 关键分支矩阵入口（Proxy）：聚合代理基础分支（自调用/ExposeProxy/机制对比），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreAopLabTest.class,
        SpringCoreAopProxyMechanicsLabTest.class,
        SpringCoreAopExposeProxyLabTest.class
})
class SpringCoreAopProxyBranchMatrixLabTest {}

