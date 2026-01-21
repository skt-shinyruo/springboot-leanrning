package com.learning.springboot.bootbusinesscase.part01_business_case;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootBusinessCaseLabTest.class,
        BootBusinessCaseServiceLabTest.class
})
class BootBusinessCaseBookMatrixLabTest {}
