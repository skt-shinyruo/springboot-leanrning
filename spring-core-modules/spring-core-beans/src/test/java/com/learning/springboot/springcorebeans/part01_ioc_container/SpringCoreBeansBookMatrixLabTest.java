package com.learning.springboot.springcorebeans.part01_ioc_container;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansContainerLabTest.class,
        SpringCoreBeansBeanFactoryVsApplicationContextLabTest.class,
        SpringCoreBeansImportLabTest.class,
        SpringCoreBeansComponentScanLabTest.class
})
class SpringCoreBeansBookMatrixLabTest {}
