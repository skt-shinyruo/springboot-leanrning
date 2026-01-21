package com.learning.springboot.boottesting.part01_testing;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        GreetingControllerWebMvcLabTest.class,
        GreetingControllerSpringBootLabTest.class,
        BootTestingMockBeanLabTest.class
})
class BootTestingBookMatrixLabTest {}
