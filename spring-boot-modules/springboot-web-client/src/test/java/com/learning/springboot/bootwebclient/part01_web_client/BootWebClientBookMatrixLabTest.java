package com.learning.springboot.bootwebclient.part01_web_client;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootWebClientRestClientLabTest.class,
        BootWebClientWebClientLabTest.class,
        BootWebClientWebClientFilterOrderLabTest.class
})
class BootWebClientBookMatrixLabTest {}
