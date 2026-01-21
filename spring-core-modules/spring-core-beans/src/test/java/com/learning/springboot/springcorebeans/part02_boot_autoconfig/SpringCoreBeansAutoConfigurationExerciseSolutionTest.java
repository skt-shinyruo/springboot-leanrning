package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

// 参考实现：对齐 SpringCoreBeansAutoConfigurationExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

class SpringCoreBeansAutoConfigurationExerciseSolutionTest {

    @Test
    void solution_addPropertyGateToGreeting_matchIfMissingTriState() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DemoGreetingAutoConfiguration.class));

        runner.run(context -> assertThat(context).hasSingleBean(DemoGreeting.class));

        runner.withPropertyValues("demo.greeting.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(DemoGreeting.class));

        runner.withPropertyValues("demo.greeting.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(DemoGreeting.class));
    }

    @Test
    void solution_createAndResolveAmbiguity_withPrimary() {
        ApplicationContextRunner ambiguous = new ApplicationContextRunner()
                .withUserConfiguration(AmbiguousClientConfiguration.class);

        ambiguous.run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class);
        });

        ApplicationContextRunner resolved = new ApplicationContextRunner()
                .withUserConfiguration(PrimaryClientConfiguration.class);

        resolved.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(ClientConsumer.class);
            assertThat(context.getBean(ClientConsumer.class).clientId()).isEqualTo("primary");
        });
    }

    @Test
    void solution_addDebugSummaryHelper_keepsAssertionsStable() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DemoGreetingAutoConfiguration.class))
                .withPropertyValues("demo.greeting.enabled=false");

        runner.run(context -> {
            String summary = debugSummary(context, "demo.greeting.enabled", DemoGreeting.class);

            assertThat(context).doesNotHaveBean(DemoGreeting.class);
            assertThat(summary)
                    .contains("demo.greeting.enabled=false")
                    .contains("DemoGreeting=<missing>");
        });
    }

    @Test
    void solution_matchIfMissingTriState_andConditionEvaluationReport() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DefaultEnabledFeatureAutoConfiguration.class));

        runner.run(context -> {
            assertThat(context).hasSingleBean(DefaultEnabledFeature.class);
            assertThat(context.getBean(DefaultEnabledFeature.class).origin()).isEqualTo("enabled-by-default");

            assertThat(negativeMessages(context, DefaultEnabledFeatureAutoConfiguration.class)).isEmpty();
        });

        runner.withPropertyValues("exercise.default.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DefaultEnabledFeature.class);
                    assertThat(negativeMessages(context, DefaultEnabledFeatureAutoConfiguration.class)).isNotEmpty();
                });

        runner.withPropertyValues("exercise.default.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(DefaultEnabledFeature.class));
    }

    @Test
    void solution_makeConditionalOnBeanDeterministic_withAutoConfigurationAfter() {
        ApplicationContextRunner orderSensitive = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MarkerAutoConfiguration.class, DependentAutoConfigurationWithoutOrdering.class));

        orderSensitive.run(context -> {
            assertThat(context).hasSingleBean(Marker.class);
            assertThat(context).doesNotHaveBean(DependentFeature.class);

            assertThat(negativeMessages(context, DependentAutoConfigurationWithoutOrdering.class)).isNotEmpty();
        });

        ApplicationContextRunner deterministic = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DependentAutoConfigurationAfterMarker.class, MarkerAutoConfiguration.class));

        deterministic.run(context -> {
            assertThat(context).hasSingleBean(DependentFeature.class);
            assertThat(context.getBean(DependentFeature.class).origin()).isEqualTo("conditional-on-marker");
        });
    }

    private static String debugSummary(AssertableApplicationContext context, String propertyKey, Class<?> beanType) {
        String prop = context.getEnvironment().getProperty(propertyKey);
        String bean = context.containsBeanDefinition(beanType.getName()) || context.getBeansOfType(beanType).size() > 0
                ? beanType.getSimpleName()
                : "<missing>";
        return propertyKey + "=" + prop + ", " + beanType.getSimpleName() + "=" + bean;
    }

    private static List<String> negativeMessages(AssertableApplicationContext context, Class<?> source) {
        ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
        var bySource = report.getConditionAndOutcomesBySource();

        ConditionEvaluationReport.ConditionAndOutcomes outcomes = bySource.get(source.getName());
        if (outcomes != null) {
            if (outcomes.isFullMatch()) {
                return List.of();
            }
            return outcomes.stream()
                    .filter(it -> !it.getOutcome().isMatch())
                    .map(it -> it.getOutcome().getMessage())
                    .toList();
        }

        String simpleName = source.getSimpleName();
        return bySource.entrySet().stream()
                .filter(it -> it.getKey().contains(simpleName))
                .flatMap(it -> it.getValue().stream())
                .filter(it -> !it.getOutcome().isMatch())
                .map(it -> it.getOutcome().getMessage())
                .distinct()
                .toList();
    }

    record DemoGreeting(String message) {
    }

    @AutoConfiguration
    @ConditionalOnProperty(prefix = "demo.greeting", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class DemoGreetingAutoConfiguration {

        @Bean
        DemoGreeting demoGreeting() {
            return new DemoGreeting("hello");
        }
    }

    record DemoClient(String id) {
    }

    record ClientConsumer(DemoClient client) {
        String clientId() {
            return client.id();
        }
    }

    static class AmbiguousClientConfiguration {
        @Bean
        DemoClient clientA() {
            return new DemoClient("a");
        }

        @Bean
        DemoClient clientB() {
            return new DemoClient("b");
        }

        @Bean
        ClientConsumer consumer(DemoClient client) {
            return new ClientConsumer(client);
        }
    }

    static class PrimaryClientConfiguration {
        @Bean
        @Primary
        DemoClient primaryClient() {
            return new DemoClient("primary");
        }

        @Bean
        DemoClient secondaryClient() {
            return new DemoClient("secondary");
        }

        @Bean
        ClientConsumer consumer(DemoClient client) {
            return new ClientConsumer(client);
        }
    }

    record DefaultEnabledFeature(String origin) {
    }

    @AutoConfiguration
    @ConditionalOnProperty(prefix = "exercise.default", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class DefaultEnabledFeatureAutoConfiguration {

        @Bean
        DefaultEnabledFeature defaultEnabledFeature() {
            return new DefaultEnabledFeature("enabled-by-default");
        }
    }

    record Marker(String origin) {
    }

    record DependentFeature(String origin, Marker marker) {
    }

    @AutoConfiguration
    static class MarkerAutoConfiguration {
        @Bean
        Marker marker() {
            return new Marker("from-marker-auto-config");
        }
    }

    @AutoConfiguration
    static class DependentAutoConfigurationWithoutOrdering {

        @Bean
        @ConditionalOnBean(Marker.class)
        DependentFeature dependentFeature(Marker marker) {
            return new DependentFeature("conditional-on-marker", marker);
        }
    }

    @AutoConfiguration(after = MarkerAutoConfiguration.class)
    static class DependentAutoConfigurationAfterMarker {

        @Bean
        @ConditionalOnBean(Marker.class)
        DependentFeature dependentFeature(Marker marker) {
            return new DependentFeature("conditional-on-marker", marker);
        }
    }
}
