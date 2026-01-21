package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreAopLabTest.class,
        SpringCoreAopProxyMechanicsLabTest.class,
        SpringCoreAopExposeProxyLabTest.class
})
class SpringCoreAopBookMatrixLabTest {}
