package com.learning.springboot.springcoreevents.part01_event_basics;

/**
 * 关键分支矩阵入口（Basics）：聚合事件分发基础关键分支（监听器过滤、事件机制），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreEventsLabTest.class,
        SpringCoreEventsMechanicsLabTest.class,
        SpringCoreEventsListenerFilteringLabTest.class
})
class SpringCoreEventsBasicsBranchMatrixLabTest {}

