package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SpringCoreBeansEarlyReferenceLabTest {

    @Test
    void getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EarlyReferenceConfiguration.class)) {
            Alpha alpha = context.getBean(Alpha.class);
            Beta beta = context.getBean(Beta.class);
            EarlyProxyingPostProcessor processor = context.getBean(EarlyProxyingPostProcessor.class);

            System.out.println("OBSERVE: circular dependency with setters succeeds via early singleton exposure");
            System.out.println("OBSERVE: getEarlyBeanReference can return an early proxy to avoid raw injection");

            assertThat(processor.earlyReferenceCreated()).isTrue();
            assertThat(Proxy.isProxyClass(alpha.getClass())).isTrue();
            assertThat(beta.alpha()).isSameAs(alpha);
        }
    }

    @Test
    void injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ConcreteTypeMismatchConfiguration.class);

        assertThatThrownBy(context::refresh)
                .isInstanceOf(UnsatisfiedDependencyException.class)
                .hasRootCauseInstanceOf(BeanNotOfRequiredTypeException.class);

        context.close();

        System.out.println("OBSERVE: JDK proxy only implements interfaces; it is NOT assignable to the concrete class");
        System.out.println("OBSERVE: injecting by concrete class can fail when the final exposed bean is a JDK proxy");
        System.out.println("OBSERVE: prefer injecting by interface for proxyable beans (or use class-based proxying when necessary)");
    }

    interface Alpha {
        String id();
    }

    static class AlphaImpl implements Alpha {
        private Beta beta;

        @Autowired
        void setBeta(Beta beta) {
            this.beta = beta;
        }

        @Override
        public String id() {
            return "alpha";
        }

        Beta beta() {
            return beta;
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
    }

    static class EarlyProxyingPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

        private final Map<String, Object> earlyProxies = new ConcurrentHashMap<>();
        private volatile boolean earlyReferenceCreated;

        boolean earlyReferenceCreated() {
            return earlyReferenceCreated;
        }

        @Override
        public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
            if (!beanName.equals("alpha") || !(bean instanceof Alpha alpha)) {
                return bean;
            }

            earlyReferenceCreated = true;
            return earlyProxies.computeIfAbsent(beanName, ignored -> Proxy.newProxyInstance(
                    Alpha.class.getClassLoader(),
                    new Class<?>[]{Alpha.class},
                    (proxy, method, args) -> method.invoke(alpha, args)
            ));
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            Object proxy = earlyProxies.get(beanName);
            return proxy != null ? proxy : bean;
        }
    }

    static class LateProxyingPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (!beanName.equals("alpha") || !(bean instanceof Alpha alpha)) {
                return bean;
            }

            return Proxy.newProxyInstance(
                    Alpha.class.getClassLoader(),
                    new Class<?>[]{Alpha.class},
                    (proxy, method, args) -> method.invoke(alpha, args)
            );
        }
    }

    @Configuration
    static class EarlyReferenceConfiguration {

        @Bean
        static EarlyProxyingPostProcessor earlyProxyingPostProcessor() {
            return new EarlyProxyingPostProcessor();
        }

        @Bean
        Alpha alpha() {
            return new AlphaImpl();
        }

        @Bean
        Beta beta() {
            return new Beta();
        }
    }

    @Configuration
    static class ConcreteTypeMismatchConfiguration {

        @Bean
        static LateProxyingPostProcessor lateProxyingPostProcessor() {
            return new LateProxyingPostProcessor();
        }

        @Bean
        ConcreteAlphaImpl alpha() {
            return new ConcreteAlphaImpl();
        }

        @Bean
        ConcreteBeta beta() {
            return new ConcreteBeta();
        }
    }

    static class ConcreteAlphaImpl implements Alpha {
        private ConcreteBeta beta;

        @Autowired
        void setBeta(ConcreteBeta beta) {
            this.beta = beta;
        }

        @Override
        public String id() {
            return "alpha";
        }

        ConcreteBeta beta() {
            return beta;
        }
    }

    static class ConcreteBeta {
        private ConcreteAlphaImpl alpha;

        @Autowired
        void setAlpha(ConcreteAlphaImpl alpha) {
            this.alpha = alpha;
        }

        ConcreteAlphaImpl alpha() {
            return alpha;
        }
    }
}
