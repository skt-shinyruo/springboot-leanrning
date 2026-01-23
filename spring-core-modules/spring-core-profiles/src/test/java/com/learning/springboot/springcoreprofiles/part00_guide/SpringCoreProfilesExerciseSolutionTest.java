package com.learning.springboot.springcoreprofiles.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreprofiles.part01_profiles.DefaultGreetingProvider;
import com.learning.springboot.springcoreprofiles.part01_profiles.DevGreetingConfiguration;
import com.learning.springboot.springcoreprofiles.part01_profiles.FancyGreetingProvider;
import com.learning.springboot.springcoreprofiles.part01_profiles.GreetingProvider;
import com.learning.springboot.springcoreprofiles.part01_profiles.NonDevGreetingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 参考实现：对齐 SpringCoreProfilesExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 *
 * <p>本模块优先使用 {@link ApplicationContextRunner}，把“条件装配/Profiles/属性覆盖”收敛成可断言事实。
 */
class SpringCoreProfilesExerciseSolutionTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(
                    DevGreetingConfiguration.class,
                    NonDevGreetingConfiguration.class,
                    StagingGreetingConfiguration.class
            );

    @Test
    void solution_addNewProfile_staging_canSelectAStagingGreetingProvider() {
        contextRunner
                .withPropertyValues("spring.profiles.active=staging", "app.mode=unknown")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class).greeting()).contains("staging");
                });
    }

    @Test
    void solution_addNewConditional_canSwitchImplementationByProperty() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GreetingProvider.class);
            assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DefaultGreetingProvider.class);
        });

        contextRunner
                .withPropertyValues("app.mode=fancy")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(FancyGreetingProvider.class);
                });
    }

    @Test
    void solution_propertyPrecedence_testOverridesCanOverrideRunnerDefaults() {
        ApplicationContextRunner base = new ApplicationContextRunner()
                .withUserConfiguration(DevGreetingConfiguration.class, NonDevGreetingConfiguration.class)
                .withPropertyValues("app.mode=default");

        base.run(context -> assertThat(context.getEnvironment().getProperty("app.mode")).isEqualTo("default"));

        base.withPropertyValues("app.mode=fancy")
                .run(context -> {
                    assertThat(context.getEnvironment().getProperty("app.mode")).isEqualTo("fancy");
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(FancyGreetingProvider.class);
                });
    }

    @Test
    void solution_missingBeanStartupFailure_whenAllCandidatesAreDisabled_contextShouldFailFast() {
        new ApplicationContextRunner()
                .withUserConfiguration(DevGreetingConfiguration.class, NonDevGreetingConfiguration.class, MandatoryConsumerConfig.class)
                .withPropertyValues("spring.profiles.active=default", "app.mode=unknown")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(NoSuchBeanDefinitionException.class);
                });
    }

    @Configuration
    @Profile("staging")
    static class StagingGreetingConfiguration {
        @Bean
        GreetingProvider stagingGreetingProvider() {
            return () -> "staging greeting";
        }
    }

    @Configuration
    static class MandatoryConsumerConfig {
        @Bean
        Object mandatoryGreetingConsumer(GreetingProvider greetingProvider) {
            return new Object();
        }
    }
}

