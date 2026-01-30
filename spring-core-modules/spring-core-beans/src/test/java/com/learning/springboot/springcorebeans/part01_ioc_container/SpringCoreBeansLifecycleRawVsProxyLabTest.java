package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.PostConstruct;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SpringCoreBeansLifecycleRawVsProxyLabTest {

    @Test
    void postConstructRunsOnRaw_butFinalExposedBeanMayBeProxy() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RawVsProxyConfig.class)) {
            LifecycleTrace trace = context.getBean(LifecycleTrace.class);
            WorkService service = context.getBean(WorkService.class);

            System.out.println("OBSERVE: @PostConstruct happens before after-init BPP, so it runs on the raw bean");
            System.out.println("OBSERVE: after-init BPP may wrap/replace the bean, so the final exposed bean can be a proxy");

            assertThat(trace.postConstructIdentityHash()).isNotZero();
            assertThat(trace.exposedIdentityHash()).isNotZero();
            assertThat(trace.postConstructIdentityHash()).isNotEqualTo(trace.exposedIdentityHash());

            assertThat(Proxy.isProxyClass(service.getClass())).isTrue();
            assertThat(System.identityHashCode(service)).isEqualTo(trace.exposedIdentityHash());
        }
    }

    interface WorkService {
        String hello(String name);
    }

    static class LifecycleTrace {
        private volatile int postConstructIdentityHash;
        private volatile int exposedIdentityHash;

        void recordPostConstruct(Object rawBean) {
            this.postConstructIdentityHash = System.identityHashCode(rawBean);
        }

        void recordExposed(Object exposedBean) {
            this.exposedIdentityHash = System.identityHashCode(exposedBean);
        }

        int postConstructIdentityHash() {
            return postConstructIdentityHash;
        }

        int exposedIdentityHash() {
            return exposedIdentityHash;
        }
    }

    static class RawWorkService implements WorkService {
        private final LifecycleTrace trace;

        RawWorkService(LifecycleTrace trace) {
            this.trace = trace;
        }

        @PostConstruct
        void init() {
            trace.recordPostConstruct(this);
        }

        @Override
        public String hello(String name) {
            return "hello:" + name;
        }
    }

    static class AfterInitJdkProxyingBpp implements BeanPostProcessor {
        private final LifecycleTrace trace;

        AfterInitJdkProxyingBpp(LifecycleTrace trace) {
            this.trace = trace;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (!(bean instanceof WorkService target)) {
                return bean;
            }

            Object proxy = Proxy.newProxyInstance(
                    WorkService.class.getClassLoader(),
                    new Class<?>[]{WorkService.class},
                    (ignored, method, args) -> method.invoke(target, args)
            );

            trace.recordExposed(proxy);
            return proxy;
        }
    }

    @Configuration
    static class RawVsProxyConfig {

        @Bean
        LifecycleTrace lifecycleTrace() {
            return new LifecycleTrace();
        }

        @Bean
        WorkService workService(LifecycleTrace trace) {
            return new RawWorkService(trace);
        }

        @Bean
        BeanPostProcessor afterInitJdkProxyingBpp(LifecycleTrace trace) {
            return new AfterInitJdkProxyingBpp(trace);
        }
    }
}

