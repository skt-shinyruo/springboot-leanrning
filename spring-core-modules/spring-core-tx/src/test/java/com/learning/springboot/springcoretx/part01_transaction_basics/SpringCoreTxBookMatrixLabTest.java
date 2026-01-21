package com.learning.springboot.springcoretx.part01_transaction_basics;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreTxLabTest.class,
        SpringCoreTxPropagationMatrixLabTest.class,
        SpringCoreTxRollbackRulesLabTest.class
})
class SpringCoreTxBookMatrixLabTest {}
