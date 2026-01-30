package com.learning.springboot.springcorebeans.part03_container_internals;

// 本测试用于补齐循环依赖的“工程规避策略”对照：constructor 环通常 fail-fast，但可以用 @Lazy/ObjectProvider 打断依赖获取时机。

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringCoreBeansCircularDependencyBoundaryLabTest {

    @Test
    void constructorCycleFailsFast() {
        assertThatThrownBy(() -> new AnnotationConfigApplicationContext(FailingConstructorCycleConfig.class))
                .isInstanceOf(BeanCreationException.class)
                .hasRootCauseInstanceOf(BeanCurrentlyInCreationException.class);

        System.out.println("OBSERVE: constructor cycle fails fast because neither side can be instantiated first");
    }

    @Test
    void constructorCycleCanBeBrokenViaLazyInjectionPointProxy() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LazyConstructorCycleConfig.class)) {
            Alpha alpha = context.getBean(Alpha.class);
            LazyBeta beta = context.getBean(LazyBeta.class);

            System.out.println("OBSERVE: @Lazy on an injection point can break a constructor cycle");
            System.out.println("OBSERVE: the injected Alpha in beta is a lazy proxy, resolved on first use");

            assertThat(beta.alpha()).isNotNull();
            assertThat(Proxy.isProxyClass(beta.alpha().getClass())).isTrue();
            assertThat(beta.alpha().id()).isEqualTo("alpha");
            assertThat(alpha.id()).isEqualTo("alpha");
        }
    }

    @Test
    void constructorCycleCanBeBrokenViaObjectProvider() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ObjectProviderConstructorCycleConfig.class)) {
            ProviderAlpha alpha = context.getBean(ProviderAlpha.class);
            ProviderBeta beta = context.getBean(ProviderBeta.class);

            System.out.println("OBSERVE: ObjectProvider defers lookup; you can break a constructor cycle by delaying getObject()");

            assertThat(beta.alpha()).isSameAs(alpha);
            assertThat(beta.alpha().id()).isEqualTo("alpha");
        }
    }

    @Test
    void setterCycleMaySucceedViaEarlySingletonExposure_whenAllowCircularReferencesIsEnabled() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SetterCycleConfig.class)) {
            SetterAlpha alpha = context.getBean(SetterAlpha.class);
            SetterBeta beta = context.getBean(SetterBeta.class);

            System.out.println("OBSERVE: setter/field cycle may succeed because container can expose an early singleton reference");
            System.out.println("OBSERVE: 'succeeds' does NOT mean 'safe' — proxy/wrapping may introduce early-vs-final consistency issues");

            assertThat(alpha.beta()).isSameAs(beta);
            assertThat(beta.alpha()).isSameAs(alpha);
        }
    }

    @Test
    void setterCycleFailsFast_whenAllowCircularReferencesIsDisabled() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        ((DefaultListableBeanFactory) context.getBeanFactory()).setAllowCircularReferences(false);
        context.register(SetterCycleConfig.class);

        assertThatThrownBy(context::refresh)
                .as("禁用 allowCircularReferences 后，setter/field 循环依赖也应 fail-fast")
                .isInstanceOf(BeanCreationException.class)
                .hasRootCauseInstanceOf(BeanCurrentlyInCreationException.class);

        context.close();

        System.out.println("OBSERVE: allowCircularReferences=false => even setter/field cycles fail-fast");
        System.out.println("OBSERVE: this is often a safer default in large systems (it forces explicit refactoring)");
    }

    @Test
    void setterCycleWithAfterInitWrapping_prefersConsistency_whenAllowRawInjectionDespiteWrappingIsFalse() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
            beanFactory.setAllowCircularReferences(true);
            beanFactory.setAllowRawInjectionDespiteWrapping(false);
            context.register(WrappingSetterCycleConfig.class);
            context.refresh();

            WrappedAlpha alphaFromContext = context.getBean(WrappedAlpha.class);
            WrappingBeta beta = context.getBean(WrappingBeta.class);

            System.out.println("OBSERVE: allowRawInjectionDespiteWrapping=false protects consistency in circular refs");
            System.out.println("OBSERVE: the container prefers 'early == final': it injects a proxy early so dependents don't hold the raw instance");

            assertThat(Proxy.isProxyClass(alphaFromContext.getClass())).isTrue();
            assertThat(Proxy.isProxyClass(beta.alpha().getClass())).isTrue();
            assertThat(beta.alpha()).isSameAs(alphaFromContext);
        }
    }

    @Test
    void setterCycleWithAfterInitWrapping_succeedsButMayInjectRaw_whenAllowRawInjectionDespiteWrappingIsTrue() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
            beanFactory.setAllowCircularReferences(true);
            beanFactory.setAllowRawInjectionDespiteWrapping(true);
            context.register(WrappingSetterCycleConfig.class);
            context.refresh();

            WrappedAlpha alphaFromContext = context.getBean(WrappedAlpha.class);
            WrappingBeta beta = context.getBean(WrappingBeta.class);

            System.out.println("OBSERVE: allowRawInjectionDespiteWrapping=true can let a setter cycle 'succeed' even if final bean is wrapped");
            System.out.println("OBSERVE: beta may hold a raw reference while the container exposes a proxy as the final bean");

            assertThat(Proxy.isProxyClass(alphaFromContext.getClass())).isTrue();
            assertThat(Proxy.isProxyClass(beta.alpha().getClass())).isFalse();
            assertThat(beta.alpha()).isNotSameAs(alphaFromContext);
            assertThat(beta.alpha().id()).isEqualTo("alpha");
        }
    }

    interface Alpha {
        String id();
    }

    static class AlphaImpl implements Alpha {
        private final LazyBeta beta;

        AlphaImpl(LazyBeta beta) {
            this.beta = beta;
        }

        @Override
        public String id() {
            return "alpha";
        }

        LazyBeta beta() {
            return beta;
        }
    }

    static class LazyBeta {
        private final Alpha alpha;

        LazyBeta(Alpha alpha) {
            this.alpha = alpha;
        }

        Alpha alpha() {
            return alpha;
        }
    }

    @Configuration
    static class FailingConstructorCycleConfig {

        @Bean
        FailingA a(FailingB b) {
            return new FailingA(b);
        }

        @Bean
        FailingB b(FailingA a) {
            return new FailingB(a);
        }
    }

    static class FailingA {
        private final FailingB b;

        FailingA(FailingB b) {
            this.b = b;
        }

        FailingB b() {
            return b;
        }
    }

    static class FailingB {
        private final FailingA a;

        FailingB(FailingA a) {
            this.a = a;
        }

        FailingA a() {
            return a;
        }
    }

    @Configuration
    static class LazyConstructorCycleConfig {

        @Bean
        Alpha alpha(LazyBeta beta) {
            return new AlphaImpl(beta);
        }

        @Bean
        LazyBeta beta(@Lazy Alpha alpha) {
            return new LazyBeta(alpha);
        }
    }

    static class ProviderAlpha {
        private final ProviderBeta beta;

        ProviderAlpha(ProviderBeta beta) {
            this.beta = beta;
        }

        String id() {
            return "alpha";
        }

        ProviderBeta beta() {
            return beta;
        }
    }

    static class ProviderBeta {

        private final ObjectProvider<ProviderAlpha> alphaProvider;

        ProviderBeta(ObjectProvider<ProviderAlpha> alphaProvider) {
            this.alphaProvider = alphaProvider;
        }

        ProviderAlpha alpha() {
            return alphaProvider.getObject();
        }
    }

    @Configuration
    static class ObjectProviderConstructorCycleConfig {

        @Bean
        ProviderAlpha alpha(ProviderBeta beta) {
            return new ProviderAlpha(beta);
        }

        @Bean
        ProviderBeta beta(ObjectProvider<ProviderAlpha> alphaProvider) {
            return new ProviderBeta(alphaProvider);
        }
    }

    @Configuration
    static class SetterCycleConfig {

        @Bean
        SetterAlpha alpha() {
            return new SetterAlpha();
        }

        @Bean
        SetterBeta beta() {
            return new SetterBeta();
        }
    }

    static class SetterAlpha {
        private SetterBeta beta;

        @Autowired
        void setBeta(SetterBeta beta) {
            this.beta = beta;
        }

        SetterBeta beta() {
            return beta;
        }
    }

    static class SetterBeta {
        private SetterAlpha alpha;

        @Autowired
        void setAlpha(SetterAlpha alpha) {
            this.alpha = alpha;
        }

        SetterAlpha alpha() {
            return alpha;
        }
    }

    interface WrappedAlpha {
        String id();
    }

    static class WrappedAlphaImpl implements WrappedAlpha {
        private WrappingBeta beta;

        @Autowired
        void setBeta(WrappingBeta beta) {
            this.beta = beta;
        }

        @Override
        public String id() {
            return "alpha";
        }

        WrappingBeta beta() {
            return beta;
        }
    }

    static class WrappingBeta {
        private WrappedAlpha alpha;

        @Autowired
        void setAlpha(WrappedAlpha alpha) {
            this.alpha = alpha;
        }

        WrappedAlpha alpha() {
            return alpha;
        }
    }

    static class AfterInitJdkWrappingBpp implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (!(bean instanceof WrappedAlpha target)) {
                return bean;
            }
            if (Proxy.isProxyClass(bean.getClass())) {
                return bean;
            }
            return Proxy.newProxyInstance(
                    WrappedAlpha.class.getClassLoader(),
                    new Class<?>[]{WrappedAlpha.class},
                    (proxy, method, args) -> method.invoke(target, args)
            );
        }
    }

    @Configuration
    static class WrappingSetterCycleConfig {

        @Bean
        WrappedAlpha alpha() {
            return new WrappedAlphaImpl();
        }

        @Bean
        WrappingBeta beta() {
            return new WrappingBeta();
        }

        @Bean
        BeanPostProcessor afterInitJdkWrappingBpp() {
            return new AfterInitJdkWrappingBpp();
        }
    }
}
