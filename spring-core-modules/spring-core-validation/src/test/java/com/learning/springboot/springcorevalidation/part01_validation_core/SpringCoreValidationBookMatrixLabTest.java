package com.learning.springboot.springcorevalidation.part01_validation_core;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreValidationLabTest.class,
        SpringCoreValidationMechanicsLabTest.class
})
class SpringCoreValidationBookMatrixLabTest {}
