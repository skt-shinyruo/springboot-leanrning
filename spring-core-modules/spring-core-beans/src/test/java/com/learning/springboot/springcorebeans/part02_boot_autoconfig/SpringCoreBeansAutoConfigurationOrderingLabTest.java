package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class SpringCoreBeansAutoConfigurationOrderingLabTest {

    @Test
    void conditionalOnBean_canFailAcrossAutoConfigurations_whenOrderingIsNotDefined() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MarkerAutoConfiguration.class, DependentAutoConfigurationWithoutOrdering.class));

        runner.run(context -> {
            assertThat(context).hasSingleBean(Marker.class);
            assertThat(context).doesNotHaveBean(DependentFeature.class);
            System.out.println("OBSERVE: Marker exists at runtime, but DependentFeature was not registered due to condition evaluation timing");
            System.out.println("OBSERVE: Fix it by declaring @AutoConfiguration(after=MarkerAutoConfiguration.class) (see next test)");
        });
    }

    @Test
    void autoConfigurationAfter_canMakeCrossAutoConfigConditionsDeterministic_evenIfImportOrderIsReversed() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DependentAutoConfigurationAfterMarker.class, MarkerAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(DependentFeature.class);
                    assertThat(context.getBean(DependentFeature.class).origin()).isEqualTo("conditional-on-marker");
                    System.out.println("OBSERVE: @AutoConfiguration(after=...) makes the condition deterministic (decoupled from import list order)");
                });
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
