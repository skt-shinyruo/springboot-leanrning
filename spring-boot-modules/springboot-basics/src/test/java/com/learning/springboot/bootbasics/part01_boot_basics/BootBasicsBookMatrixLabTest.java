package com.learning.springboot.bootbasics.part01_boot_basics;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootBasicsDefaultLabTest.class,
        BootBasicsDevLabTest.class,
        BootBasicsOverrideLabTest.class
})
class BootBasicsBookMatrixLabTest {}
