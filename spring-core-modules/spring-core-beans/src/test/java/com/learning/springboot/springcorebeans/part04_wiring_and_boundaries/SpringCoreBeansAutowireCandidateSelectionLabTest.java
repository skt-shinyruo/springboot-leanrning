package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.annotation.Priority;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

class SpringCoreBeansAutowireCandidateSelectionLabTest {

    @Test
    void orderAnnotation_affectsCollectionInjectionOrder() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(FirstOrderedWorker.class, SecondOrderedWorker.class, OrderedWorkersConsumer.class);
            context.refresh();

            OrderedWorkersConsumer consumer = context.getBean(OrderedWorkersConsumer.class);

            System.out.println("OBSERVE: @Order affects collection injection order (List/Stream ordering)");
            System.out.println("OBSERVE: Smaller order value wins (comes earlier)");

            assertThat(consumer.workerIds()).containsExactly("first", "second");
        }
    }

    @Test
    void orderAnnotation_doesNotResolveSingleInjectionAmbiguity() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(FirstOrderedWorker.class, SecondOrderedWorker.class, SingleWorkerConsumer.class);

        assertThatThrownBy(context::refresh)
                .as("单依赖注入出现多个候选时，@Order 不能帮你选出唯一候选，它只影响集合注入顺序")
                .hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class);

        context.close();

        System.out.println("OBSERVE: @Order does NOT pick a single autowire candidate");
        System.out.println("OBSERVE: For single injection, use @Primary / @Qualifier / @Priority (if applicable)");
    }

    @Test
    void priorityAnnotation_canBreakTieForSingleInjection_whenNoPrimaryOrQualifier() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(HighPriorityWorker.class, LowPriorityWorker.class, SingleWorkerConsumer.class);
            context.refresh();

            SingleWorkerConsumer consumer = context.getBean(SingleWorkerConsumer.class);

            System.out.println("OBSERVE: @Priority can break ties for single injection when no @Primary/@Qualifier is present");
            System.out.println("OBSERVE: Smaller priority value wins (higher priority)");

            assertThat(consumer.workerId()).isEqualTo("high-priority");
        }
    }

    @Test
    void primaryOverridesPriority_forSingleInjection() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(PrimaryWorker.class, VeryHighPriorityWorker.class, SingleWorkerConsumer.class);
            context.refresh();

            SingleWorkerConsumer consumer = context.getBean(SingleWorkerConsumer.class);

            System.out.println("OBSERVE: @Primary overrides @Priority for single injection candidate selection");
            System.out.println("OBSERVE: Even if another candidate has a 'higher' @Priority, @Primary still wins");

            assertThat(consumer.workerId()).isEqualTo("primary");
        }
    }

    @Test
    void byNameFallback_canResolveSingleInjectionAmbiguity_forAutowiredFieldInjection() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ByNameFallbackConfig.class)) {
            ByNameFallbackConsumer consumer = context.getBean(ByNameFallbackConsumer.class);

            System.out.println("OBSERVE: when multiple candidates exist, @Autowired may fall back to dependency-name matching beanName");
            System.out.println("OBSERVE: field name == beanName (secondaryWorker) narrows candidates to a single target");

            assertThat(consumer.workerId()).isEqualTo("secondary");
        }
    }

    @Test
    void primaryOverridesByNameFallback_forSingleInjection() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrimaryOverridesByNameFallbackConfig.class)) {
            ByNameFallbackConsumer consumer = context.getBean(ByNameFallbackConsumer.class);

            System.out.println("OBSERVE: @Primary wins before by-name fallback; field name matching does not override primary");

            assertThat(consumer.workerId()).isEqualTo("primary");
        }
    }

    @Test
    void qualifierOverridesPrimary_forSingleInjection() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(QualifierOverridesPrimaryConfig.class)) {
            QualifierOverridesPrimaryConsumer consumer = context.getBean(QualifierOverridesPrimaryConsumer.class);

            System.out.println("OBSERVE: @Qualifier is a stronger signal; it can select a non-primary candidate explicitly");

            assertThat(consumer.workerId()).isEqualTo("secondary");
        }
    }

    @Test
    void genericType_canNarrowCandidates_forSingleInjection() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(StringHandler.class, LongHandler.class, GenericHandlerConsumer.class);
            context.refresh();

            GenericHandlerConsumer consumer = context.getBean(GenericHandlerConsumer.class);

            System.out.println("OBSERVE: generic type info (Handler<String>) can narrow candidates when class metadata keeps generic signature");

            assertThat(consumer.handlerId()).isEqualTo("string");
        }
    }

    @Test
    void objectProvider_getIfUnique_returnsNull_whenMultipleCandidatesExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(FirstOrderedWorker.class, SecondOrderedWorker.class);
            context.refresh();

            ObjectProvider<Worker> provider = context.getBeanProvider(Worker.class);
            assertThat(provider.getIfUnique()).isNull();

            System.out.println("OBSERVE: ObjectProvider#getIfUnique() returns null when multiple candidates exist");
        }
    }

    @Test
    void objectProvider_orderedStream_respectsOrderAnnotation() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(SecondOrderedWorker.class, FirstOrderedWorker.class);
            context.refresh();

            ObjectProvider<Worker> provider = context.getBeanProvider(Worker.class);
            List<String> ids = provider.orderedStream().map(Worker::id).toList();

            System.out.println("OBSERVE: ObjectProvider#orderedStream() respects @Order/@Priority ordering");

            assertThat(ids).containsExactly("first", "second");
        }
    }

    interface Worker {
        String id();
    }

    @Order(0)
    static class FirstOrderedWorker implements Worker {
        @Override
        public String id() {
            return "first";
        }
    }

    @Order(1)
    static class SecondOrderedWorker implements Worker {
        @Override
        public String id() {
            return "second";
        }
    }

    static class OrderedWorkersConsumer {
        private final List<Worker> workers;

        OrderedWorkersConsumer(List<Worker> workers) {
            this.workers = workers;
        }

        List<String> workerIds() {
            return workers.stream().map(Worker::id).toList();
        }
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

    @Priority(1)
    static class HighPriorityWorker implements Worker {
        @Override
        public String id() {
            return "high-priority";
        }
    }

    @Priority(100)
    static class LowPriorityWorker implements Worker {
        @Override
        public String id() {
            return "low-priority";
        }
    }

    @Primary
    @Priority(100)
    static class PrimaryWorker implements Worker {
        @Override
        public String id() {
            return "primary";
        }
    }

    @Priority(0)
    static class VeryHighPriorityWorker implements Worker {
        @Override
        public String id() {
            return "very-high-priority";
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
        QualifierOverridesPrimaryConsumer qualifierOverridesPrimaryConsumer() {
            return new QualifierOverridesPrimaryConsumer();
        }
    }

    static class ByNameFallbackConsumer {
        @Autowired
        private Worker secondaryWorker;

        String workerId() {
            return secondaryWorker.id();
        }
    }

    static class QualifierOverridesPrimaryConsumer {
        @Autowired
        @Qualifier("secondaryWorker")
        private Worker worker;

        String workerId() {
            return worker.id();
        }
    }

    interface Handler<T> {
        String id();
    }

    static class StringHandler implements Handler<String> {
        @Override
        public String id() {
            return "string";
        }
    }

    static class LongHandler implements Handler<Long> {
        @Override
        public String id() {
            return "long";
        }
    }

    static class GenericHandlerConsumer {
        private final Handler<String> handler;

        GenericHandlerConsumer(Handler<String> handler) {
            this.handler = handler;
        }

        String handlerId() {
            return handler.id();
        }
    }
}
