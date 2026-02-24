package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

/**
 * 关键分支矩阵入口：聚合 Async/Scheduling 的关键分支（线程池、异常传播、定时任务），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootAsyncSchedulingLabTest.class,
        BootAsyncSchedulingExecutorSelectionLabTest.class,
        BootAsyncSchedulingProxyTypeLabTest.class,
        BootAsyncSchedulingUncaughtExceptionHandlerLabTest.class,
        BootAsyncSchedulingContextPropagationLabTest.class,
        BootAsyncSchedulingTransactionBoundaryLabTest.class,
        BootAsyncSchedulingSecurityContextPropagationLabTest.class,
        BootAsyncSchedulingRequestContextPropagationLabTest.class,
        BootAsyncSchedulingSpringTaskAutoConfigurationLabTest.class,
        BootAsyncSchedulingSchedulingLabTest.class,
        BootAsyncSchedulingSchedulingRegistrationLabTest.class,
        BootAsyncSchedulingSchedulingExceptionSemanticsLabTest.class,
        BootAsyncSchedulingScheduledAsyncCombinationLabTest.class,
        com.learning.springboot.bootasyncscheduling.part02_perf_concurrency.BootAsyncSchedulingExecutorSaturationLabTest.class
})
class BootAsyncSchedulingBranchMatrixLabTest {}
