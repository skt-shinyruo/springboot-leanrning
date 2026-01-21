package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SpringCoreBeansOptionalInjectionLabTest {

    @Test
    void optionalDependencies_canBeExpressedByNullableOptionalAndObjectProvider() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(OptionalInjectionConfig.class)) {
            Consumer consumer = context.getBean(Consumer.class);

            assertThat(consumer.missingField()).isNull();
            assertThat(consumer.missingOptional()).isEmpty();
            assertThat(consumer.missingProviderGetIfAvailable()).isNull();
        }
    }

    @Test
    void optionalDependencies_areStillResolvedWhenBeanExists() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WithDependencyConfig.class)) {
            Consumer consumer = context.getBean(Consumer.class);
            MissingDependency dependency = context.getBean(MissingDependency.class);

            assertThat(consumer.missingField()).isSameAs(dependency);
            assertThat(consumer.missingOptional()).containsSame(dependency);
            assertThat(consumer.missingProviderGetIfAvailable()).isSameAs(dependency);
        }
    }

    @Configuration
    static class OptionalInjectionConfig {
        @Bean
        Consumer consumer(Optional<MissingDependency> optional, ObjectProvider<MissingDependency> provider) {
            return new Consumer(optional, provider);
        }
    }

    @Configuration
    static class WithDependencyConfig {
        @Bean
        MissingDependency missingDependency() {
            return new MissingDependency();
        }

        @Bean
        Consumer consumer(Optional<MissingDependency> optional, ObjectProvider<MissingDependency> provider) {
            return new Consumer(optional, provider);
        }
    }

    static class MissingDependency {
    }

    static class Consumer {
        private final Optional<MissingDependency> optional;
        private final ObjectProvider<MissingDependency> provider;

        @Autowired(required = false)
        private MissingDependency field;

        Consumer(Optional<MissingDependency> optional, ObjectProvider<MissingDependency> provider) {
            this.optional = optional;
            this.provider = provider;
        }

        MissingDependency missingField() {
            return field;
        }

        Optional<MissingDependency> missingOptional() {
            return optional;
        }

        MissingDependency missingProviderGetIfAvailable() {
            return provider.getIfAvailable();
        }
    }
}

