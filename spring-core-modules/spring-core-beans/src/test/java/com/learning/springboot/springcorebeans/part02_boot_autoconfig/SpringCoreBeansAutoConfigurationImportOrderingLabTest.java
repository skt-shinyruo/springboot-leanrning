package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.annotation.Configurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class SpringCoreBeansAutoConfigurationImportOrderingLabTest {

    @Test
    void autoConfigurations_areSortedByAfterBeforeMetadata_beforeBeingApplied_soConditionsBecomeDeterministic() {
        AutoConfigurations autoConfigurations = AutoConfigurations.of(
                DependentAutoConfiguration.class,
                MarkerAutoConfiguration.class,
                FirstAutoConfiguration.class
        );

        Class<?>[] sorted = Configurations.getClasses(autoConfigurations);
        assertThat(sorted).containsExactly(
                FirstAutoConfiguration.class,
                MarkerAutoConfiguration.class,
                DependentAutoConfiguration.class
        );

        System.out.println("OBSERVE: AutoConfigurations.of(...) sorts by @AutoConfiguration(after/before), not by the input list order");
        System.out.println("OBSERVE: sorted=" + java.util.Arrays.toString(sorted));

        new ApplicationContextRunner()
                .withConfiguration(autoConfigurations)
                .run(context -> {
                    assertThat(context).hasSingleBean(Marker.class);
                    assertThat(context).hasSingleBean(DependentFeature.class);

                    DependentFeature feature = context.getBean(DependentFeature.class);
                    assertThat(feature.marker().origin()).isEqualTo("from-marker");
                });
    }

    record Marker(String origin) {
    }

    record DependentFeature(String origin, Marker marker) {
    }

    @AutoConfiguration(before = MarkerAutoConfiguration.class)
    static class FirstAutoConfiguration {
        @Bean
        String firstMarker() {
            return "first";
        }
    }

    @AutoConfiguration
    static class MarkerAutoConfiguration {
        @Bean
        Marker marker() {
            return new Marker("from-marker");
        }
    }

    @AutoConfiguration(after = MarkerAutoConfiguration.class)
    static class DependentAutoConfiguration {

        @Bean
        @ConditionalOnBean(Marker.class)
        DependentFeature dependentFeature(Marker marker) {
            return new DependentFeature("conditional-on-marker", marker);
        }
    }
}

