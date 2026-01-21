package com.learning.springboot.springcoreaopweaving.part02_ltw_fundamentals;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AspectjLtwLabTest.class
})
class AspectjWeavingBookMatrixLabTest {}
