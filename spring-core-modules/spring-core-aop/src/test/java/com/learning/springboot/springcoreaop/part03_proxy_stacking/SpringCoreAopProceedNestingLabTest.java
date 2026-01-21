package com.learning.springboot.springcoreaop.part03_proxy_stacking;

import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.TracingAspect;

// 这个 Lab 用可断言的顺序日志，把 `MethodInvocation#proceed` 的嵌套执行关系做成可验证结论。

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

class SpringCoreAopProceedNestingLabTest {

    @Test
    void aroundAdvice_isNestedViaProceed_inOrder() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProceedNestingConfig.class)) {
            InvocationSequenceLog log = context.getBean(InvocationSequenceLog.class);
            NestedCallService service = context.getBean(NestedCallService.class);

            assertThat(AopUtils.isAopProxy(service)).isTrue();

            log.clear();
            service.run();

            assertThat(log.entries()).containsExactly(
                    "outer-before",
                    "inner-before",
                    "target",
                    "inner-after",
                    "outer-after");
        }
    }

    static class InvocationSequenceLog {
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

    static class NestedCallService {
        private final InvocationSequenceLog log;

        NestedCallService(InvocationSequenceLog log) {
            this.log = log;
        }

        @Traced
        public void run() {
            log.add("target");
        }
    }

    @org.aspectj.lang.annotation.Aspect
    @Order(1)
    static class OuterAspect {
        private final InvocationSequenceLog log;

        OuterAspect(InvocationSequenceLog log) {
            this.log = log;
        }

        @org.aspectj.lang.annotation.Around("@annotation(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced)")
        public Object around(org.aspectj.lang.ProceedingJoinPoint joinPoint) throws Throwable {
            log.add("outer-before");
            try {
                return joinPoint.proceed();
            } finally {
                log.add("outer-after");
            }
        }
    }

    @org.aspectj.lang.annotation.Aspect
    @Order(2)
    static class InnerAspect {
        private final InvocationSequenceLog log;

        InnerAspect(InvocationSequenceLog log) {
            this.log = log;
        }

        @org.aspectj.lang.annotation.Around("@annotation(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced)")
        public Object around(org.aspectj.lang.ProceedingJoinPoint joinPoint) throws Throwable {
            log.add("inner-before");
            try {
                return joinPoint.proceed();
            } finally {
                log.add("inner-after");
            }
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class ProceedNestingConfig {

        @Bean
        InvocationSequenceLog invocationSequenceLog() {
            return new InvocationSequenceLog();
        }

        @Bean
        NestedCallService nestedCallService(InvocationSequenceLog invocationSequenceLog) {
            return new NestedCallService(invocationSequenceLog);
        }

        @Bean
        OuterAspect outerAspect(InvocationSequenceLog invocationSequenceLog) {
            return new OuterAspect(invocationSequenceLog);
        }

        @Bean
        InnerAspect innerAspect(InvocationSequenceLog invocationSequenceLog) {
            return new InnerAspect(invocationSequenceLog);
        }
    }
}
