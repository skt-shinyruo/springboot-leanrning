package com.learning.springboot.springcoreaop.part03_proxy_stacking;

import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.TracingAspect;

// 这个 Lab 用“模拟 Tx/Cache/Security 的 Advisors”展示：单 proxy 多 advisor 是主流形态；套娃 proxy 也能被识别与拆解。

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

class SpringCoreAopMultiProxyStackingLabTest {

    @Test
    void multiple_advisors_are_applied_within_a_single_proxy_by_default() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MultiAdvisorConfig.class)) {
            InvocationSequenceLog log = context.getBean(InvocationSequenceLog.class);
            StackingService service = context.getBean(StackingService.class);

            NamedAroundInterceptor security = context.getBean("securityInterceptor", NamedAroundInterceptor.class);
            NamedAroundInterceptor tx = context.getBean("txInterceptor", NamedAroundInterceptor.class);
            NamedAroundInterceptor cache = context.getBean("cacheInterceptor", NamedAroundInterceptor.class);

            assertThat(AopUtils.isAopProxy(service)).isTrue();

            Advised advised = (Advised) service;
            assertThat(advised.getAdvisors())
                    .anySatisfy(advisor -> assertThat(advisor.getAdvice()).isSameAs(security))
                    .anySatisfy(advisor -> assertThat(advisor.getAdvice()).isSameAs(tx))
                    .anySatisfy(advisor -> assertThat(advisor.getAdvice()).isSameAs(cache));

            log.clear();
            service.process("x");

            assertThat(log.entries()).containsExactly(
                    "security-before",
                    "tx-before",
                    "cache-before",
                    "target:x",
                    "cache-after",
                    "tx-after",
                    "security-after");
        }
    }

    @Test
    void nested_proxy_can_wrap_an_existing_proxy_and_is_detectable_via_target_introspection() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MultiAdvisorConfig.class)) {
            InvocationSequenceLog log = context.getBean(InvocationSequenceLog.class);
            StackingService innerProxy = context.getBean(StackingService.class);

            ProxyFactory proxyFactory = new ProxyFactory(innerProxy);
            proxyFactory.addAdvice(new NamedAroundInterceptor("outer", log));
            StackingService outerProxy = (StackingService) proxyFactory.getProxy();

            assertThat(AopUtils.isAopProxy(outerProxy)).isTrue();

            Object outerTarget = ((Advised) outerProxy).getTargetSource().getTarget();
            assertThat(AopUtils.isAopProxy(outerTarget)).isTrue();

            log.clear();
            outerProxy.process("y");

            assertThat(log.entries()).containsExactly(
                    "outer-before",
                    "security-before",
                    "tx-before",
                    "cache-before",
                    "target:y",
                    "cache-after",
                    "tx-after",
                    "security-after",
                    "outer-after");
        }
    }

    interface StackingService {
        String process(String input);
    }

    static class StackingServiceImpl implements StackingService {
        private final InvocationSequenceLog log;

        StackingServiceImpl(InvocationSequenceLog log) {
            this.log = log;
        }

        @Override
        public String process(String input) {
            log.add("target:" + input);
            return "ok:" + input;
        }
    }

    static class ProcessMethodPointcut extends StaticMethodMatcherPointcut {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return "process".equals(method.getName()) && StackingServiceImpl.class.isAssignableFrom(targetClass);
        }
    }

    static class NamedAroundInterceptor implements MethodInterceptor {
        private final String name;
        private final InvocationSequenceLog log;

        NamedAroundInterceptor(String name, InvocationSequenceLog log) {
            this.name = name;
            this.log = log;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.add(name + "-before");
            try {
                return invocation.proceed();
            } finally {
                log.add(name + "-after");
            }
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

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = false)
    static class MultiAdvisorConfig {

        @Bean
        InvocationSequenceLog invocationSequenceLog() {
            return new InvocationSequenceLog();
        }

        @Bean
        StackingService stackingService(InvocationSequenceLog invocationSequenceLog) {
            return new StackingServiceImpl(invocationSequenceLog);
        }

        @Bean
        ProcessMethodPointcut processMethodPointcut() {
            return new ProcessMethodPointcut();
        }

        @Bean
        NamedAroundInterceptor securityInterceptor(InvocationSequenceLog invocationSequenceLog) {
            return new NamedAroundInterceptor("security", invocationSequenceLog);
        }

        @Bean
        NamedAroundInterceptor txInterceptor(InvocationSequenceLog invocationSequenceLog) {
            return new NamedAroundInterceptor("tx", invocationSequenceLog);
        }

        @Bean
        NamedAroundInterceptor cacheInterceptor(InvocationSequenceLog invocationSequenceLog) {
            return new NamedAroundInterceptor("cache", invocationSequenceLog);
        }

        @Bean
        Advisor securityAdvisor(ProcessMethodPointcut processMethodPointcut, NamedAroundInterceptor securityInterceptor) {
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(processMethodPointcut, securityInterceptor);
            advisor.setOrder(1);
            return advisor;
        }

        @Bean
        Advisor txAdvisor(ProcessMethodPointcut processMethodPointcut, NamedAroundInterceptor txInterceptor) {
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(processMethodPointcut, txInterceptor);
            advisor.setOrder(2);
            return advisor;
        }

        @Bean
        Advisor cacheAdvisor(ProcessMethodPointcut processMethodPointcut, NamedAroundInterceptor cacheInterceptor) {
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(processMethodPointcut, cacheInterceptor);
            advisor.setOrder(3);
            return advisor;
        }
    }
}

