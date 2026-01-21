package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class SpringCoreBeansConditionEvaluationReportLabTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(PropertyGatedAutoConfiguration.class));

    private final ApplicationContextRunner defaultEnabledRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MatchIfMissingAutoConfiguration.class));

    @Test
    void conditionEvaluationReport_recordsNoMatchWhenPropertyIsMissing() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(DemoFeature.class);

            ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
            ConditionEvaluationReport.ConditionAndOutcomes outcomes = report.getConditionAndOutcomesBySource()
                    .get(PropertyGatedAutoConfiguration.class.getName());

            assertThat(outcomes).isNotNull();
            assertThat(outcomes.isFullMatch()).isFalse();

            List<String> negativeMessages = outcomes.stream()
                    .filter(it -> !it.getOutcome().isMatch())
                    .map(it -> it.getOutcome().getMessage())
                    .toList();

            System.out.println("OBSERVE: ConditionEvaluationReport shows why auto-config did NOT match");
            System.out.println("OBSERVE: source=" + PropertyGatedAutoConfiguration.class.getName());
            System.out.println("OBSERVE: fullMatch=" + outcomes.isFullMatch());
            negativeMessages.stream()
                    .limit(3)
                    .forEach(message -> System.out.println("OBSERVE: - " + message));
        });
    }

    @Test
    void conditionEvaluationReport_recordsMatchWhenPropertyIsEnabled() {
        contextRunner
                .withPropertyValues("demo.feature.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(DemoFeature.class);
                    assertThat(context.getBean(DemoFeature.class).origin()).isEqualTo("enabled-by-property");

                    ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
                    ConditionEvaluationReport.ConditionAndOutcomes outcomes = report.getConditionAndOutcomesBySource()
                            .get(PropertyGatedAutoConfiguration.class.getName());

                    assertThat(outcomes).isNotNull();
                    assertThat(outcomes.isFullMatch()).isTrue();

                    System.out.println("OBSERVE: ConditionEvaluationReport shows this auto-config matched (fullMatch=true)");
                    System.out.println("OBSERVE: source=" + PropertyGatedAutoConfiguration.class.getName());
                });
    }

    @Test
    void conditionalOnProperty_matchesWhenPropertyIsMissing_ifMatchIfMissingIsTrue() {
        defaultEnabledRunner.run(context -> {
            assertThat(context).hasSingleBean(DefaultEnabledFeature.class);
            assertThat(context.getBean(DefaultEnabledFeature.class).origin()).isEqualTo("enabled-by-default");

            ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
            ConditionEvaluationReport.ConditionAndOutcomes outcomes = report.getConditionAndOutcomesBySource()
                    .get(MatchIfMissingAutoConfiguration.class.getName());

            assertThat(outcomes).isNotNull();
            assertThat(outcomes.isFullMatch()).isTrue();

            System.out.println("OBSERVE: matchIfMissing=true => missing property still matches (DefaultEnabledFeature is registered)");
        });
    }

    @Test
    void conditionalOnProperty_doesNotMatchWhenPropertyIsExplicitlyFalse_evenIfMatchIfMissingIsTrue() {
        defaultEnabledRunner
                .withPropertyValues("demo.default.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DefaultEnabledFeature.class);

                    ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
                    ConditionEvaluationReport.ConditionAndOutcomes outcomes = report.getConditionAndOutcomesBySource()
                            .get(MatchIfMissingAutoConfiguration.class.getName());

                    assertThat(outcomes).isNotNull();
                    assertThat(outcomes.isFullMatch()).isFalse();

                    System.out.println("OBSERVE: demo.default.enabled=false => condition does NOT match even if matchIfMissing=true");
                });
    }

    @Test
    void conditionalOnProperty_matchesWhenPropertyIsTrue_evenIfMatchIfMissingIsTrue() {
        defaultEnabledRunner
                .withPropertyValues("demo.default.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultEnabledFeature.class);
                    assertThat(context.getBean(DefaultEnabledFeature.class).origin()).isEqualTo("enabled-by-default");
                });
    }

    record DemoFeature(String origin) {
    }

    record DefaultEnabledFeature(String origin) {
    }

    @AutoConfiguration
    @ConditionalOnProperty(prefix = "demo.feature", name = "enabled", havingValue = "true")
    static class PropertyGatedAutoConfiguration {
        @Bean
        DemoFeature demoFeature() {
            return new DemoFeature("enabled-by-property");
        }
    }

    @AutoConfiguration
    @ConditionalOnProperty(prefix = "demo.default", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class MatchIfMissingAutoConfiguration {
        @Bean
        DefaultEnabledFeature defaultEnabledFeature() {
            return new DefaultEnabledFeature("enabled-by-default");
        }
    }
}
