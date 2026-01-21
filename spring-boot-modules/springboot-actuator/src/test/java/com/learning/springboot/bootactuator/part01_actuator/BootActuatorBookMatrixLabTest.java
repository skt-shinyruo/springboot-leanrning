package com.learning.springboot.bootactuator.part01_actuator;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootActuatorLabTest.class,
        BootActuatorExposureOverrideLabTest.class
})
class BootActuatorBookMatrixLabTest {}
