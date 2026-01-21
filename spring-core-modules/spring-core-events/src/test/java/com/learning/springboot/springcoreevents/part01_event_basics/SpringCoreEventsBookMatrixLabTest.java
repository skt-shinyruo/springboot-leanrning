package com.learning.springboot.springcoreevents.part01_event_basics;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreEventsLabTest.class,
        SpringCoreEventsMechanicsLabTest.class,
        SpringCoreEventsListenerFilteringLabTest.class
})
class SpringCoreEventsBookMatrixLabTest {}
