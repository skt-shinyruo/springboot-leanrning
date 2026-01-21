package com.learning.springboot.springcoreevents.part02_async_and_transactional;

/**
 * 关键分支矩阵入口（Async/Tx）：聚合异步事件与事务事件关键分支，用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreEventsAsyncMulticasterLabTest.class,
        SpringCoreEventsTransactionalEventLabTest.class
})
class SpringCoreEventsAsyncTransactionalBranchMatrixLabTest {}

