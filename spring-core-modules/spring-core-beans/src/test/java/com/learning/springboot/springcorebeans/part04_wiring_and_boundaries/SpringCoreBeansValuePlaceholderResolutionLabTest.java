package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

class SpringCoreBeansValuePlaceholderResolutionLabTest {

    @Test
    void defaultEmbeddedValueResolver_resolvesExistingProperty_butLeavesMissingPlaceholderUnresolved() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);

            ConfigurableEnvironment environment = context.getEnvironment();
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("lab", java.util.Map.of("demo.present", "hello")));

            context.registerBean(Target.class);
            context.refresh();

            Target target = context.getBean(Target.class);

            System.out.println("OBSERVE: A default embedded value resolver is installed during context refresh");
            System.out.println("OBSERVE: It delegates to Environment.resolvePlaceholders(..) => non-strict by default");
            System.out.println("OBSERVE: If a placeholder cannot be resolved, the raw '${...}' text may remain");

            assertThat(target.present()).isEqualTo("hello");
            assertThat(target.missing()).isEqualTo("${demo.missing}");
        }
    }

    @Test
    void propertySourcesPlaceholderConfigurer_canMakeMissingPlaceholderFailFast() {
        GenericApplicationContext context = new GenericApplicationContext();
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);

        ConfigurableEnvironment environment = context.getEnvironment();
        environment.getPropertySources()
                .addFirst(new MapPropertySource("lab", java.util.Map.of("demo.present", "hello")));

        context.registerBean(PropertySourcesPlaceholderConfigurer.class, () -> {
            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            configurer.setIgnoreUnresolvablePlaceholders(false);
            return configurer;
        });

        context.registerBean(Target.class);

        assertThatThrownBy(context::refresh)
                .as("当启用严格的 placeholder configurer 时，缺失的占位符应该 fail-fast")
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .getRootCause()
                .hasMessageContaining("Could not resolve placeholder 'demo.missing'");

        context.close();

        System.out.println("OBSERVE: PropertySourcesPlaceholderConfigurer can enforce strict placeholder resolution");
        System.out.println("OBSERVE: With strict mode, missing '${...}' fails during bean creation instead of silently passing through");
    }

    static class Target {

        @org.springframework.beans.factory.annotation.Value("${demo.present}")
        private String present;

        @org.springframework.beans.factory.annotation.Value("${demo.missing}")
        private String missing;

        String present() {
            return present;
        }

        String missing() {
            return missing;
        }
    }
}
