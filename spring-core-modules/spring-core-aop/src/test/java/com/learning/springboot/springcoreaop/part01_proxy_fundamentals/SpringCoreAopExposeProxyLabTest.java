package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

// 本测试用于验证 exposeProxy 的效果：outer 内部通过 AopContext.currentProxy() 调用 inner，使 inner 也命中 advice（对照自调用绕过代理的默认行为）。

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreaop.SpringCoreAopApplication;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootTest(classes = SpringCoreAopApplication.class)
@Import(SpringCoreAopExposeProxyLabTest.ExposeProxyConfig.class)
class SpringCoreAopExposeProxyLabTest {

    @Autowired
    private ExposeProxyExampleService exposeProxyExampleService;

    @Autowired
    private InvocationLog invocationLog;

    @Test
    void exposeProxyAllowsSelfInvocationToTriggerAdvice() {
        invocationLog.reset();

        String result = exposeProxyExampleService.outer("Bob");

        assertThat(result).isEqualTo("outer->inner->Bob");
        assertThat(invocationLog.count()).isEqualTo(2);
        assertThat(invocationLog.lastMethod()).contains("outer");
    }

    @Configuration
    @EnableAspectJAutoProxy(exposeProxy = true)
    static class ExposeProxyConfig {
    }
}
