package com.learning.springboot.springcoreresources.part01_resource_abstraction;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreResourcesLabTest.class,
        SpringCoreResourcesMechanicsLabTest.class
})
class SpringCoreResourcesBookMatrixLabTest {}
