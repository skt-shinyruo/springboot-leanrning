package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

// 这个 Lab 把 Spring AOP 放回到容器视角：AutoProxyCreator 作为 BPP 如何筛选 Advisor 并创建 proxy。

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

class SpringCoreAopAutoProxyCreatorInternalsLabTest {

    @Test
    void autoProxyCreator_isRegisteredAsBeanPostProcessor_whenEnableAspectJAutoProxyIsUsed() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AutoProxyCreatorConfig.class)) {
            List<?> beanPostProcessors = ((AbstractBeanFactory) context.getBeanFactory()).getBeanPostProcessors();

            AbstractAutoProxyCreator autoProxyCreator = beanPostProcessors.stream()
                    .filter(AbstractAutoProxyCreator.class::isInstance)
                    .map(AbstractAutoProxyCreator.class::cast)
                    .findFirst()
                    .orElse(null);

            assertThat(autoProxyCreator).isNotNull();
            assertThat(autoProxyCreator).isInstanceOf(SmartInstantiationAwareBeanPostProcessor.class);
        }
    }

    @Test
    void advisor_pointcut_and_advice_form_a_pipeline_that_results_in_a_proxy_and_an_interceptor_chain() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AutoProxyCreatorConfig.class)) {
            InvocationSequenceLog log = context.getBean(InvocationSequenceLog.class);
            WorkService workService = context.getBean(WorkService.class);

            assertThat(AopUtils.isAopProxy(workService)).isTrue();

            Advised advised = (Advised) workService;
            RecordingInterceptor recordingInterceptor = context.getBean(RecordingInterceptor.class);

            assertThat(advised.getAdvisors())
                    .anySatisfy(advisor -> assertThat(advisor.getAdvice()).isSameAs(recordingInterceptor));

            assertThat(advised.getAdvisors())
                    .anySatisfy(advisor -> {
                        assertThat(advisor).isInstanceOf(PointcutAdvisor.class);
                        Pointcut pointcut = ((PointcutAdvisor) advisor).getPointcut();
                        assertThat(pointcut).isInstanceOf(WorkMethodPointcut.class);
                    });

            log.clear();
            workService.work("a");

            assertThat(log.entries()).containsExactly("advice-before", "target:a", "advice-after");
        }
    }

    interface WorkService {
        String work(String input);
    }

    static class WorkServiceImpl implements WorkService {
        private final InvocationSequenceLog log;

        WorkServiceImpl(InvocationSequenceLog log) {
            this.log = log;
        }

        @Override
        public String work(String input) {
            log.add("target:" + input);
            return "ok:" + input;
        }
    }

    static class WorkMethodPointcut extends StaticMethodMatcherPointcut {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return "work".equals(method.getName()) && WorkServiceImpl.class.isAssignableFrom(targetClass);
        }
    }

    static class RecordingInterceptor implements MethodInterceptor {
        private final InvocationSequenceLog log;

        RecordingInterceptor(InvocationSequenceLog log) {
            this.log = log;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.add("advice-before");
            try {
                return invocation.proceed();
            } finally {
                log.add("advice-after");
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
    static class AutoProxyCreatorConfig {

        @Bean
        InvocationSequenceLog invocationSequenceLog() {
            return new InvocationSequenceLog();
        }

        @Bean
        WorkService workService(InvocationSequenceLog invocationSequenceLog) {
            return new WorkServiceImpl(invocationSequenceLog);
        }

        @Bean
        WorkMethodPointcut workMethodPointcut() {
            return new WorkMethodPointcut();
        }

        @Bean
        RecordingInterceptor recordingInterceptor(InvocationSequenceLog invocationSequenceLog) {
            return new RecordingInterceptor(invocationSequenceLog);
        }

        @Bean
        Advisor workAdvisor(WorkMethodPointcut workMethodPointcut, RecordingInterceptor recordingInterceptor) {
            return new DefaultPointcutAdvisor(workMethodPointcut, recordingInterceptor);
        }
    }
}
