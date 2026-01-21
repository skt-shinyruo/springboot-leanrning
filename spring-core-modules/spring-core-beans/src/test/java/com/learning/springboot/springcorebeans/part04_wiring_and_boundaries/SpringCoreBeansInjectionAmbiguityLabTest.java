package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

class SpringCoreBeansInjectionAmbiguityLabTest {

    @Test
    void singleInjectionFailsFast_whenMultipleCandidatesExist_andNoPrimaryOrQualifierIsPresent() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AmbiguousInjectionConfiguration.class);

        assertThatThrownBy(context::refresh)
                .as("单依赖注入遇到多个同类型候选时，容器应 fail-fast（避免静默注错）")
                .isInstanceOf(UnsatisfiedDependencyException.class)
                .hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class);

        context.close();

        System.out.println("OBSERVE: single injection ambiguity => NoUniqueBeanDefinitionException");
        System.out.println("OBSERVE: resolve ambiguity via @Primary (default winner) or @Qualifier (explicit selection)");
    }

    @Test
    void primary_canResolveSingleInjectionAmbiguity_byChoosingTheDefaultWinner() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrimaryWinsConfiguration.class)) {
            Consumer consumer = context.getBean(Consumer.class);
            assertThat(consumer.workerId()).isEqualTo("primary");
        }
    }

    @Test
    void qualifier_canResolveSingleInjectionAmbiguity_byExplicitlySelectingTheTargetBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(QualifierSelectsConfiguration.class)) {
            Consumer consumer = context.getBean(Consumer.class);
            assertThat(consumer.workerId()).isEqualTo("secondary");
        }
    }

    interface Worker {
        String id();
    }

    record Consumer(Worker worker) {
        String workerId() {
            return worker.id();
        }
    }

    @Configuration
    static class AmbiguousInjectionConfiguration {
        @Bean
        Worker primaryWorker() {
            return () -> "primary";
        }

        @Bean
        Worker secondaryWorker() {
            return () -> "secondary";
        }

        @Bean
        Consumer consumer(Worker worker) {
            return new Consumer(worker);
        }
    }

    @Configuration
    static class PrimaryWinsConfiguration {
        @Bean
        @Primary
        Worker primaryWorker() {
            return () -> "primary";
        }

        @Bean
        Worker secondaryWorker() {
            return () -> "secondary";
        }

        @Bean
        Consumer consumer(Worker worker) {
            return new Consumer(worker);
        }
    }

    @Configuration
    static class QualifierSelectsConfiguration {
        @Bean
        Worker primaryWorker() {
            return () -> "primary";
        }

        @Bean
        Worker secondaryWorker() {
            return () -> "secondary";
        }

        @Bean
        Consumer consumer(@Qualifier("secondaryWorker") Worker worker) {
            return new Consumer(worker);
        }
    }
}

