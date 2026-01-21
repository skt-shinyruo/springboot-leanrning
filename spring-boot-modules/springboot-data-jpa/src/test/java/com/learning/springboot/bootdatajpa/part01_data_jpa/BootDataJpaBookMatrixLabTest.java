package com.learning.springboot.bootdatajpa.part01_data_jpa;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootDataJpaLabTest.class,
        BootDataJpaMergeAndDetachLabTest.class,
        BootDataJpaDebugSqlLabTest.class
})
class BootDataJpaBookMatrixLabTest {}
