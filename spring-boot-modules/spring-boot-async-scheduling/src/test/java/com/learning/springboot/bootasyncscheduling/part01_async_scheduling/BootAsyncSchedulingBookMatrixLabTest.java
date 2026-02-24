package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootAsyncSchedulingLabTest.class,
        BootAsyncSchedulingExecutorSelectionLabTest.class,
        BootAsyncSchedulingSchedulingLabTest.class
})
class BootAsyncSchedulingBookMatrixLabTest {}
