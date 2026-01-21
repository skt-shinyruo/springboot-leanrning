package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.TracedBusinessService;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreAopLabTest {

    @Autowired
    private TracedBusinessService tracedBusinessService;

    @Autowired
    private SelfInvocationExampleService selfInvocationExampleService;

    @Autowired
    private InvocationLog invocationLog;

    @Test
    void adviceIsAppliedToTracedMethod() {
        invocationLog.reset();

        tracedBusinessService.process("hello");

        assertThat(invocationLog.count()).isEqualTo(1);
        assertThat(invocationLog.lastMethod()).contains("process");
    }

    @Test
    void selfInvocationDoesNotTriggerAdviceForInnerMethod() {
        invocationLog.reset();

        selfInvocationExampleService.outer("Bob");

        assertThat(invocationLog.count()).isEqualTo(1);
        assertThat(invocationLog.lastMethod()).contains("outer");
    }

    @Test
    void callingInnerMethodDirectlyDoesTriggerAdvice() {
        invocationLog.reset();

        selfInvocationExampleService.inner("Bob");

        assertThat(invocationLog.count()).isEqualTo(1);
        assertThat(invocationLog.lastMethod()).contains("inner");
    }

    @Test
    void tracedBusinessServiceIsAnAopProxy() {
        assertThat(AopUtils.isAopProxy(tracedBusinessService)).isTrue();
    }

    @Test
    void tracedBusinessServiceUsesCglibProxyBecauseNoInterfaceIsPresent() {
        assertThat(AopUtils.isCglibProxy(tracedBusinessService)).isTrue();
    }

    @Test
    void aspectRecordsShortSignatureLikeToShortString() {
        invocationLog.reset();

        tracedBusinessService.process("x");

        assertThat(invocationLog.lastMethod()).contains("TracedBusinessService.process");
    }
}

