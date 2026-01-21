package com.learning.springboot.bootsecurity.part01_security;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootSecurityLabTest.class,
        BootSecurityMultiFilterChainOrderLabTest.class,
        BootSecurityDevProfileLabTest.class
})
class BootSecurityBookMatrixLabTest {}
