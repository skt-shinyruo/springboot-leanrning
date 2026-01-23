package com.learning.springboot.bootbasics.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootbasics.BootBasicsApplication;
import com.learning.springboot.bootbasics.part01_boot_basics.AppProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

/**
 * 参考实现：对齐 BootBasicsExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class BootBasicsExerciseSolutionTest {

    @Test
    void solution_addNewPropertyField_viaAdditionalConfigurationProperties() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(ExtendedPropertiesConfig.class)
                .withPropertyValues(
                        "app.color=blue",
                        "app.greeting=hello");

        runner.run(context -> {
            assertThat(context).hasSingleBean(ExtendedAppProperties.class);
            assertThat(context.getBean(ExtendedAppProperties.class).color()).isEqualTo("blue");
            assertThat(context.getBean(ExtendedAppProperties.class).greeting()).isEqualTo("hello");
        });
    }

    @Test
    void solution_propertyPrecedence_firstPropertySourceWins() {
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addLast(new MapPropertySource("low", Map.of("app.greeting", "from-low")));
        env.getPropertySources().addFirst(new MapPropertySource("high", Map.of("app.greeting", "from-high")));

        assertThat(env.getProperty("app.greeting")).isEqualTo("from-high");
    }

    @Test
    void solution_conditionalBeanByProperty_presentOnlyWhenEnabled() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(ConditionalBeanConfig.class);

        runner.run(context -> assertThat(context).doesNotHaveBean(FeatureX.class));

        runner.withPropertyValues("app.feature-x.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(FeatureX.class));

        runner.withPropertyValues("app.feature-x.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(FeatureX.class));
    }

    @Test
    void solution_applicationContextRunner_readsConfigurationPropertiesFast() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(BootBasicsApplication.class)
                .withPropertyValues(
                        "app.name=demo",
                        "app.greeting=hi",
                        "app.feature-enabled=true");

        runner.run(context -> {
            assertThat(context).hasSingleBean(AppProperties.class);
            AppProperties props = context.getBean(AppProperties.class);
            assertThat(props.getName()).isEqualTo("demo");
            assertThat(props.getGreeting()).isEqualTo("hi");
            assertThat(props.isFeatureEnabled()).isTrue();
        });
    }

    @Test
    void solution_invalidPropertyType_failsFastAndHasUsefulMessage() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(BootBasicsApplication.class)
                .withPropertyValues("app.feature-enabled=not-a-boolean");

        runner.run(context -> {
            assertThat(context).hasFailed();
            Throwable failure = context.getStartupFailure();
            assertThat(failure).isNotNull();
            assertThat(failure)
                    .rootCause()
                    .hasMessageContaining("Invalid boolean value")
                    .hasMessageContaining("not-a-boolean");
            assertThat(failure.getMessage()).contains("Could not bind properties");
        });
    }

    @Configuration
    @EnableConfigurationProperties(ExtendedAppProperties.class)
    static class ExtendedPropertiesConfig {}

    @ConfigurationProperties(prefix = "app")
    record ExtendedAppProperties(String greeting, String color) {}

    static class FeatureX {
        private final String origin;

        FeatureX(String origin) {
            this.origin = origin;
        }

        String origin() {
            return origin;
        }
    }

    @Configuration
    static class ConditionalBeanConfig {

        @Bean
        @ConditionalOnProperty(prefix = "app.feature-x", name = "enabled", havingValue = "true")
        FeatureX featureX() {
            return new FeatureX("enabled");
        }
    }
}
