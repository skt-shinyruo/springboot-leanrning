package com.learning.springboot.bootwebmvc.part01_web_mvc;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootWebMvcLabTest.class,
        BootWebMvcSpringBootLabTest.class,
        BootWebMvcBindingDeepDiveLabTest.class
})
class BootWebMvcBookMatrixLabTest {}
