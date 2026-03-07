package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

// 这个 Lab 用可回归事实证明：BeanNameAutoProxyCreator 能在“不写 @Aspect”的情况下按 beanName pattern 批量创建代理。
// 它在遗留项目/框架集成里很常见：你会看到一个 BPP + 一组 interceptor/advisor 名称，就把某批 bean 包起来了。

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SpringCoreAopBeanNameAutoProxyCreatorLabTest {

    @Test
    void beanNameAutoProxyCreator_proxies_only_matching_beans_by_name_pattern() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanNameAutoProxyCreatorConfig.class)) {
            NameBasedService orderService = context.getBean("orderService", NameBasedService.class);
            NameBasedService paymentService = context.getBean("paymentService", NameBasedService.class);
            BeanNameInvocationLog log = context.getBean(BeanNameInvocationLog.class);

            assertThat(AopUtils.isAopProxy(orderService)).isTrue();
            assertThat(AopUtils.isAopProxy(paymentService)).isFalse();

            log.clear();
            assertThat(orderService.handle("x")).isEqualTo("order:x");
            assertThat(paymentService.handle("y")).isEqualTo("payment:y");

            assertThat(log.entries()).containsExactly(
                    "interceptor-before:handle",
                    "target:order:x",
                    "interceptor-after:handle",
                    "target:payment:y");
        }
    }

    @Configuration
    static class BeanNameAutoProxyCreatorConfig {

        @Bean
        BeanNameInvocationLog beanNameInvocationLog() {
            return new BeanNameInvocationLog();
        }

        @Bean
        NameBasedService orderService(BeanNameInvocationLog log) {
            return new NameBasedServiceImpl("order", log);
        }

        @Bean
        NameBasedService paymentService(BeanNameInvocationLog log) {
            return new NameBasedServiceImpl("payment", log);
        }

        @Bean
        BeanNameTraceInterceptor beanNameTraceInterceptor(BeanNameInvocationLog log) {
            return new BeanNameTraceInterceptor(log);
        }

        @Bean
        static BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
            BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
            creator.setBeanNames("order*");
            creator.setInterceptorNames("beanNameTraceInterceptor");
            return creator;
        }
    }

    interface NameBasedService {
        String handle(String input);
    }

    static class NameBasedServiceImpl implements NameBasedService {
        private final String name;
        private final BeanNameInvocationLog log;

        NameBasedServiceImpl(String name, BeanNameInvocationLog log) {
            this.name = name;
            this.log = log;
        }

        @Override
        public String handle(String input) {
            log.add("target:" + name + ":" + input);
            return name + ":" + input;
        }
    }

    static class BeanNameTraceInterceptor implements MethodInterceptor {
        private final BeanNameInvocationLog log;

        BeanNameTraceInterceptor(BeanNameInvocationLog log) {
            this.log = log;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.add("interceptor-before:" + invocation.getMethod().getName());
            try {
                return invocation.proceed();
            } finally {
                log.add("interceptor-after:" + invocation.getMethod().getName());
            }
        }
    }

    static class BeanNameInvocationLog {
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
}
