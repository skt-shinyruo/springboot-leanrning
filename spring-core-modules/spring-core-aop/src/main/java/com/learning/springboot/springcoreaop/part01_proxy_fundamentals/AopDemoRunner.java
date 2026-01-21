package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.TracedBusinessService;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

@Component
public class AopDemoRunner implements ApplicationRunner {

    private final TracedBusinessService tracedBusinessService;
    private final SelfInvocationExampleService selfInvocationExampleService;
    private final InvocationLog invocationLog;

    public AopDemoRunner(
            TracedBusinessService tracedBusinessService,
            SelfInvocationExampleService selfInvocationExampleService,
            InvocationLog invocationLog
    ) {
        this.tracedBusinessService = tracedBusinessService;
        this.selfInvocationExampleService = selfInvocationExampleService;
        this.invocationLog = invocationLog;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("== spring-core-aop ==");

        System.out.println("AOP:tracedBusinessService.isAopProxy=" + AopUtils.isAopProxy(tracedBusinessService));
        System.out.println("AOP:tracedBusinessService.proxyType=" + proxyType(tracedBusinessService));
        System.out.println("AOP:tracedBusinessService.targetClass=" + AopProxyUtils.ultimateTargetClass(tracedBusinessService).getName());

        System.out.println("AOP:selfInvocationExampleService.isAopProxy=" + AopUtils.isAopProxy(selfInvocationExampleService));
        System.out.println("AOP:selfInvocationExampleService.proxyType=" + proxyType(selfInvocationExampleService));
        System.out.println("AOP:selfInvocationExampleService.targetClass=" + AopProxyUtils.ultimateTargetClass(selfInvocationExampleService).getName());

        invocationLog.reset();
        tracedBusinessService.process("hello");
        System.out.println("AOP:process.invocationCount=" + invocationLog.count());
        System.out.println("AOP:process.lastMethod=" + invocationLog.lastMethod());

        invocationLog.reset();
        String selfInvocationResult = selfInvocationExampleService.outer("Bob");
        System.out.println("AOP:selfInvocation.result=" + selfInvocationResult);
        System.out.println("AOP:selfInvocation.invocationCount=" + invocationLog.count());
        System.out.println("AOP:selfInvocation.lastMethod=" + invocationLog.lastMethod());
        System.out.println("AOP:selfInvocation.note=inner(...) is not traced when called via self-invocation");
    }

    private static String proxyType(Object bean) {
        if (AopUtils.isJdkDynamicProxy(bean)) {
            return "JDK";
        }
        if (AopUtils.isCglibProxy(bean)) {
            return "CGLIB";
        }
        if (AopUtils.isAopProxy(bean)) {
            return "AOP";
        }
        return "NONE";
    }
}
