package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.TracingAspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

class SpringCoreAopProxyMechanicsLabTest {

    @Test
    void jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JdkProxyConfig.class)) {
            Greeter greeter = context.getBean(Greeter.class);

            assertThat(AopUtils.isAopProxy(greeter)).isTrue();
            assertThat(AopUtils.isJdkDynamicProxy(greeter)).isTrue();

            assertThatThrownBy(() -> context.getBean(PlainGreeter.class))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }

    @Test
    void cglibProxyIsUsedForClassBasedBeans_whenProxyTargetClassIsTrue() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CglibProxyConfig.class)) {
            PlainGreeter greeter = context.getBean(PlainGreeter.class);

            assertThat(AopUtils.isAopProxy(greeter)).isTrue();
            assertThat(AopUtils.isCglibProxy(greeter)).isTrue();
        }
    }

    @Test
    void finalMethodsAreNotInterceptedByCglibProxies() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FinalMethodConfig.class)) {
            InvocationLog log = context.getBean(InvocationLog.class);
            FinalMethodExampleService service = context.getBean(FinalMethodExampleService.class);

            log.reset();
            service.nonFinal("a");
            assertThat(log.count()).isEqualTo(1);

            service.finalMethod("b");
            assertThat(log.count()).isEqualTo(1);
        }
    }

    @Test
    void adviceOrderingCanBeControlledWithOrderAnnotation() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AdviceOrderConfig.class)) {
            CallOrderLog callOrderLog = context.getBean(CallOrderLog.class);
            OrderedTracedService service = context.getBean(OrderedTracedService.class);

            callOrderLog.clear();
            service.run();

            assertThat(callOrderLog.entries()).containsExactly("first", "second");
        }
    }

    interface Greeter {
        String greet(String name);
    }

    static class PlainGreeter implements Greeter {
        @Traced
        @Override
        public String greet(String name) {
            return "hi:" + name;
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = false)
    static class JdkProxyConfig {
        @Bean
        PlainGreeter plainGreeter() {
            return new PlainGreeter();
        }

        @Bean
        TracingAspect tracingAspect(InvocationLog invocationLog) {
            return new TracingAspect(invocationLog);
        }

        @Bean
        InvocationLog invocationLog() {
            return new InvocationLog();
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class CglibProxyConfig {
        @Bean
        PlainGreeter plainGreeter() {
            return new PlainGreeter();
        }

        @Bean
        TracingAspect tracingAspect(InvocationLog invocationLog) {
            return new TracingAspect(invocationLog);
        }

        @Bean
        InvocationLog invocationLog() {
            return new InvocationLog();
        }
    }

    static class FinalMethodExampleService {
        private final InvocationLog invocationLog;

        FinalMethodExampleService(InvocationLog invocationLog) {
            this.invocationLog = invocationLog;
        }

        @Traced
        public String nonFinal(String input) {
            return "nonFinal:" + input;
        }

        @Traced
        public final String finalMethod(String input) {
            return "final:" + input;
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class FinalMethodConfig {
        @Bean
        FinalMethodExampleService finalMethodExampleService(InvocationLog invocationLog) {
            return new FinalMethodExampleService(invocationLog);
        }

        @Bean
        TracingAspect tracingAspect(InvocationLog invocationLog) {
            return new TracingAspect(invocationLog);
        }

        @Bean
        InvocationLog invocationLog() {
            return new InvocationLog();
        }
    }

    @Component
    static class CallOrderLog {
        private final List<String> entries = new CopyOnWriteArrayList<>();

        void add(String entry) {
            entries.add(entry);
        }

        List<String> entries() {
            return List.copyOf(entries);
        }

        void clear() {
            entries.clear();
        }
    }

    static class OrderedTracedService {
        @Traced
        public void run() {
        }
    }

    @org.aspectj.lang.annotation.Aspect
    @Component
    @Order(1)
    static class FirstAspect {
        private final CallOrderLog callOrderLog;

        FirstAspect(CallOrderLog callOrderLog) {
            this.callOrderLog = callOrderLog;
        }

        @org.aspectj.lang.annotation.Around("@annotation(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced)")
        public Object around(org.aspectj.lang.ProceedingJoinPoint joinPoint) throws Throwable {
            callOrderLog.add("first");
            return joinPoint.proceed();
        }
    }

    @org.aspectj.lang.annotation.Aspect
    @Component
    @Order(2)
    static class SecondAspect {
        private final CallOrderLog callOrderLog;

        SecondAspect(CallOrderLog callOrderLog) {
            this.callOrderLog = callOrderLog;
        }

        @org.aspectj.lang.annotation.Around("@annotation(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced)")
        public Object around(org.aspectj.lang.ProceedingJoinPoint joinPoint) throws Throwable {
            callOrderLog.add("second");
            return joinPoint.proceed();
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class AdviceOrderConfig {
        @Bean
        OrderedTracedService orderedTracedService() {
            return new OrderedTracedService();
        }

        @Bean
        CallOrderLog callOrderLog() {
            return new CallOrderLog();
        }

        @Bean
        FirstAspect firstAspect(CallOrderLog callOrderLog) {
            return new FirstAspect(callOrderLog);
        }

        @Bean
        SecondAspect secondAspect(CallOrderLog callOrderLog) {
            return new SecondAspect(callOrderLog);
        }
    }
}

