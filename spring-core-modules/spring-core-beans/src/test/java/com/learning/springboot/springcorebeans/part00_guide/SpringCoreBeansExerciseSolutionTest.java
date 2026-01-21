package com.learning.springboot.springcorebeans.part00_guide;

// 参考实现：对齐 SpringCoreBeansExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import com.learning.springboot.springcorebeans.part01_ioc_container.FormattingService;
import com.learning.springboot.springcorebeans.part01_ioc_container.LowerCaseTextFormatter;
import com.learning.springboot.springcorebeans.part01_ioc_container.PrototypeIdGenerator;
import com.learning.springboot.springcorebeans.part01_ioc_container.ProviderPrototypeConsumer;
import com.learning.springboot.springcorebeans.part01_ioc_container.TextFormatter;
import com.learning.springboot.springcorebeans.part01_ioc_container.UpperCaseTextFormatter;

class SpringCoreBeansExerciseSolutionTest {

    @Test
    void solution_identifyExceptionTypeForMissingBeanLookup() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.refresh();

            assertThatThrownBy(() -> context.getBean("doesNotExist"))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }

    @Test
    void solution_switchQualifierToLowerCaseFormatter() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LowerQualifierSolutionConfiguration.class)) {
            FormattingService service = context.getBean(FormattingService.class);
            assertThat(service.format("Hello")).isEqualTo("hello");
            assertThat(service.formatterImplementation()).containsIgnoringCase("Lower");
        }
    }

    @Test
    void solution_makeDirectPrototypeConsumerUseFreshPrototypeEachCall() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FreshPrototypePerCallSolutionConfiguration.class)) {
            FreshPrototypeConsumer consumer = context.getBean(FreshPrototypeConsumer.class);

            UUID first = consumer.currentId();
            UUID second = consumer.currentId();

            assertThat(first).isNotEqualTo(second);
        }
    }

    @Test
    void solution_resolveMultipleBeansViaPrimaryInsteadOfQualifier() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrimaryInsteadOfQualifierSolutionConfiguration.class)) {
            FormattingService service = context.getBean(FormattingService.class);

            assertThat(service.format("Hello")).isEqualTo("HELLO");
            assertThat(service.formatterImplementation()).containsIgnoringCase("Upper");
        }
    }

    @Test
    void solution_fixSingleInjectionAmbiguity_withPrimary() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrimaryWorkerSolutionConfiguration.class)) {
            AmbiguityConsumer consumer = context.getBean(AmbiguityConsumer.class);
            assertThat(consumer.workerId()).isEqualTo("primary");
        }
    }

    @Test
    void solution_changePrototypeScopeAndUpdateExpectations() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SingletonPrototypeIdGeneratorSolutionConfiguration.class)) {
            ProviderPrototypeConsumer consumer = context.getBean(ProviderPrototypeConsumer.class);
            UUID provider1 = consumer.newId();
            UUID provider2 = consumer.newId();
            assertThat(provider1).isEqualTo(provider2);
        }
    }

    interface Worker {
        String id();
    }

    record AmbiguityConsumer(Worker worker) {
        String workerId() {
            return worker.id();
        }
    }

    @Configuration
    static class LowerQualifierSolutionConfiguration {

        @Bean("upperFormatter")
        TextFormatter upperFormatter() {
            return new UpperCaseTextFormatter();
        }

        @Bean("lowerFormatter")
        TextFormatter lowerFormatter() {
            return new LowerCaseTextFormatter();
        }

        @Bean
        FormattingService formattingService(@Qualifier("lowerFormatter") TextFormatter formatter) {
            return new FormattingService(formatter);
        }
    }

    @Configuration
    static class FreshPrototypePerCallSolutionConfiguration {

        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        PrototypeIdGenerator prototypeIdGenerator() {
            return new PrototypeIdGenerator();
        }

        @Bean
        FreshPrototypeConsumer freshPrototypeConsumer(ObjectProvider<PrototypeIdGenerator> idGeneratorProvider) {
            return new FreshPrototypeConsumer(idGeneratorProvider);
        }
    }

    static class FreshPrototypeConsumer {
        private final ObjectProvider<PrototypeIdGenerator> idGeneratorProvider;

        FreshPrototypeConsumer(ObjectProvider<PrototypeIdGenerator> idGeneratorProvider) {
            this.idGeneratorProvider = idGeneratorProvider;
        }

        UUID currentId() {
            return idGeneratorProvider.getObject().getId();
        }
    }

    @Configuration
    static class PrimaryInsteadOfQualifierSolutionConfiguration {

        @Bean("upperFormatter")
        @Primary
        TextFormatter upperFormatter() {
            return new UpperCaseTextFormatter();
        }

        @Bean("lowerFormatter")
        TextFormatter lowerFormatter() {
            return new LowerCaseTextFormatter();
        }

        @Bean
        FormattingService formattingService(TextFormatter formatter) {
            return new FormattingService(formatter);
        }
    }

    @Configuration
    static class PrimaryWorkerSolutionConfiguration {

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
        AmbiguityConsumer ambiguityConsumer(Worker worker) {
            return new AmbiguityConsumer(worker);
        }
    }

    @Configuration
    static class SingletonPrototypeIdGeneratorSolutionConfiguration {

        @Bean
        PrototypeIdGenerator prototypeIdGenerator() {
            return new PrototypeIdGenerator();
        }

        @Bean
        ProviderPrototypeConsumer providerPrototypeConsumer(ObjectProvider<PrototypeIdGenerator> idGeneratorProvider) {
            return new ProviderPrototypeConsumer(idGeneratorProvider);
        }
    }
}
