package com.learning.springboot.springcorebeans.part03_container_internals;

/*
 * 本实验最小复现一个经典坑：循环依赖 + BeanPostProcessor 包裹（wrapping）时，dependent bean 可能持有 raw bean，
 * 而容器最终暴露的是 proxy，导致“同一个 bean 在系统里出现两份引用（raw vs proxy）”的不一致。
 *
 * 关键开关：DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping(boolean)
 * - false（更安全，默认）：检测到 raw 注入 + 最终被包裹时，会 fail-fast
 * - true：允许继续运行，但会留下“依赖绕过代理”的隐患
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

class SpringCoreBeansRawInjectionDespiteWrappingLabTest {

    @Test
    void whenAllowRawInjectionDespiteWrappingFalse_getBeanFailsFastToProtectConsistency() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            ((DefaultListableBeanFactory) context.getBeanFactory()).setAllowRawInjectionDespiteWrapping(false);
            context.register(RawInjectionConfiguration.class);
            context.refresh();

            assertThatThrownBy(() -> context.getBean(Alpha.class))
                    .as("默认应 fail-fast：避免 dependent bean 持有 raw，而容器暴露 proxy 的不一致状态")
                    .isInstanceOf(BeansException.class)
                    .isInstanceOf(BeanCurrentlyInCreationException.class);
        }
    }

    @Test
    void whenAllowRawInjectionDespiteWrappingTrue_dependentHoldsRawButContextExposesProxy() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            ((DefaultListableBeanFactory) context.getBeanFactory()).setAllowRawInjectionDespiteWrapping(true);
            context.register(RawInjectionConfiguration.class);
            context.refresh();

            Alpha alphaFromContext = context.getBean(Alpha.class);
            Beta beta = context.getBean(Beta.class);
            Alpha alphaFromBeta = beta.alpha();

            assertThat(Proxy.isProxyClass(alphaFromContext.getClass())).as("容器最终暴露的应是 proxy").isTrue();
            assertThat(Proxy.isProxyClass(alphaFromBeta.getClass())).as("dependent 持有的可能是 raw").isFalse();
            assertThat(alphaFromBeta).as("raw 与 proxy 不是同一份引用").isNotSameAs(alphaFromContext);

            CallLog callLog = context.getBean(CallLog.class);
            callLog.clear();

            beta.callAlpha();
            assertThat(callLog.entries()).as("通过 raw 引用调用，会绕过 proxy").isEmpty();

            alphaFromContext.ping();
            assertThat(callLog.entries()).containsExactly("ping");
        }
    }

    interface Alpha {
        void ping();
    }

    static class AlphaImpl implements Alpha {
        private Beta beta;

        @Autowired
        void setBeta(Beta beta) {
            this.beta = beta;
        }

        @Override
        public void ping() {
            // no-op
        }
    }

    static class Beta {
        private Alpha alpha;

        @Autowired
        void setAlpha(Alpha alpha) {
            this.alpha = alpha;
        }

        Alpha alpha() {
            return alpha;
        }

        void callAlpha() {
            alpha.ping();
        }
    }

    static class CallLog {
        private final List<String> entries = new ArrayList<>();

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

    static class WrappingAlphaPostProcessor implements BeanPostProcessor {
        private final ObjectProvider<CallLog> callLogProvider;

        WrappingAlphaPostProcessor(ObjectProvider<CallLog> callLogProvider) {
            this.callLogProvider = callLogProvider;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (!(bean instanceof AlphaImpl target)) {
                return bean;
            }

            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(target, args);
                    }
                    if ("ping".equals(method.getName())) {
                        callLogProvider.getObject().add("ping");
                    }
                    return method.invoke(target, args);
                }
            };

            return Proxy.newProxyInstance(
                    Alpha.class.getClassLoader(),
                    new Class<?>[]{Alpha.class},
                    handler
            );
        }
    }

    @Configuration
    static class RawInjectionConfiguration {

        @Bean
        CallLog callLog() {
            return new CallLog();
        }

        @Bean
        static BeanPostProcessor wrappingAlphaPostProcessor(ObjectProvider<CallLog> callLogProvider) {
            return new WrappingAlphaPostProcessor(callLogProvider);
        }

        @Bean
        @Lazy
        Alpha alpha() {
            return new AlphaImpl();
        }

        @Bean
        @Lazy
        Beta beta() {
            return new Beta();
        }
    }
}
