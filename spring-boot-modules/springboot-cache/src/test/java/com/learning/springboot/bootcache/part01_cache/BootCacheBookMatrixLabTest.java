package com.learning.springboot.bootcache.part01_cache;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootCacheLabTest.class,
        BootCacheSpelKeyLabTest.class
})
class BootCacheBookMatrixLabTest {}
