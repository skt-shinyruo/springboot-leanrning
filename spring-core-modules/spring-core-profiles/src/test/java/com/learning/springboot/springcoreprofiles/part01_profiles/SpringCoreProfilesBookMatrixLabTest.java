package com.learning.springboot.springcoreprofiles.part01_profiles;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreProfilesLabTest.class,
        SpringCoreProfilesProfilePrecedenceLabTest.class
})
class SpringCoreProfilesBookMatrixLabTest {}
