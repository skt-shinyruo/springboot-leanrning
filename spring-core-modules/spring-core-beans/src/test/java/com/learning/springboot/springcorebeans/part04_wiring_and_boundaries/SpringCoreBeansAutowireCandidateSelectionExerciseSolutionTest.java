package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 参考实现：对齐 SpringCoreBeansAutowireCandidateSelectionExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest {

    @Test
    void solution_fixSingleInjectionAmbiguity_compareQualifierVsPrimary() {
        AnnotationConfigApplicationContext ambiguous = new AnnotationConfigApplicationContext();
        ambiguous.register(AmbiguousWorkersConfig.class, SingleWorkerConsumer.class);

        assertThatThrownBy(ambiguous::refresh)
                .as("同类型多个候选时，容器应 fail-fast（避免静默注错）")
                .hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class);
        ambiguous.close();

        try (AnnotationConfigApplicationContext primaryWins = new AnnotationConfigApplicationContext(PrimaryWinsConfig.class)) {
            SingleWorkerConsumer consumer = primaryWins.getBean(SingleWorkerConsumer.class);
            assertThat(consumer.workerId()).isEqualTo("primary");
        }

        try (AnnotationConfigApplicationContext qualifierWins = new AnnotationConfigApplicationContext(QualifierOverridesPrimaryConfig.class)) {
            QualifierWorkerConsumer consumer = qualifierWins.getBean(QualifierWorkerConsumer.class);
            assertThat(consumer.workerId()).isEqualTo("secondary");
        }
    }

    @Test
    void solution_understandByNameFallback_andWhyItIsRisky() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ByNameFallbackConfig.class)) {
            ByNameFallbackConsumer consumer = context.getBean(ByNameFallbackConsumer.class);
            assertThat(consumer.workerId()).isEqualTo("secondary");
        }

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrimaryOverridesByNameFallbackConfig.class)) {
            ByNameFallbackConsumer consumer = context.getBean(ByNameFallbackConsumer.class);
            assertThat(consumer.workerId()).isEqualTo("primary");
        }
    }

    @Test
    void solution_objectProviderGetIfUnique_semantics() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AmbiguousWorkersConfig.class)) {
            ObjectProvider<Worker> provider = context.getBeanProvider(Worker.class);
            assertThat(provider.getIfUnique()).isNull();
        }

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(UniqueWorkerConfig.class)) {
            ObjectProvider<Worker> provider = context.getBeanProvider(Worker.class);
            assertThat(Objects.requireNonNull(provider.getIfUnique()).id()).isEqualTo("only");
        }
    }

    interface Worker {
        String id();
    }

    static class SingleWorkerConsumer {
        private final Worker worker;

        SingleWorkerConsumer(Worker worker) {
            this.worker = worker;
        }

        String workerId() {
            return worker.id();
        }
    }

    static class QualifierWorkerConsumer {
        @Autowired
        @Qualifier("secondaryWorker")
        private Worker worker;

        String workerId() {
            return worker.id();
        }
    }

    static class ByNameFallbackConsumer {
        @Autowired
        private Worker secondaryWorker;

        String workerId() {
            return secondaryWorker.id();
        }
    }

    @Configuration
    static class AmbiguousWorkersConfig {
        @Bean
        Worker primaryWorker() {
            return () -> "primary";
        }

        @Bean
        Worker secondaryWorker() {
            return () -> "secondary";
        }
    }

    @Configuration
    static class PrimaryWinsConfig {
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
        SingleWorkerConsumer singleWorkerConsumer(Worker worker) {
            return new SingleWorkerConsumer(worker);
        }
    }

    @Configuration
    static class QualifierOverridesPrimaryConfig {
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
        QualifierWorkerConsumer qualifierWorkerConsumer() {
            return new QualifierWorkerConsumer();
        }
    }

    @Configuration
    static class ByNameFallbackConfig {
        @Bean
        Worker primaryWorker() {
            return () -> "primary";
        }

        @Bean
        Worker secondaryWorker() {
            return () -> "secondary";
        }

        @Bean
        ByNameFallbackConsumer byNameFallbackConsumer() {
            return new ByNameFallbackConsumer();
        }
    }

    @Configuration
    static class PrimaryOverridesByNameFallbackConfig {
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
        ByNameFallbackConsumer byNameFallbackConsumer() {
            return new ByNameFallbackConsumer();
        }
    }

    @Configuration
    static class UniqueWorkerConfig {
        @Bean
        Worker onlyWorker() {
            return () -> "only";
        }
    }
}

