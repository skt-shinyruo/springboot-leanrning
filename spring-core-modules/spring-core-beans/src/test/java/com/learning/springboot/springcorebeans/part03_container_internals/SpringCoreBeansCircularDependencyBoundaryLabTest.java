package com.learning.springboot.springcorebeans.part03_container_internals;

// 本测试用于补齐循环依赖的“工程规避策略”对照：constructor 环通常 fail-fast，但可以用 @Lazy/ObjectProvider 打断依赖获取时机。

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

class SpringCoreBeansCircularDependencyBoundaryLabTest {

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
}
