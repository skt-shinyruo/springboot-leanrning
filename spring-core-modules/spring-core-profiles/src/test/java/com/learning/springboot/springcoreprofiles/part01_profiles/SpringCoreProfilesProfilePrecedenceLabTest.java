package com.learning.springboot.springcoreprofiles.part01_profiles;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class SpringCoreProfilesProfilePrecedenceLabTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(DevGreetingConfiguration.class, NonDevGreetingConfiguration.class);

    @Test
    void defaultProfilesContainDefault_whenNoActiveProfilesConfigured() {
        contextRunner.run(context -> {
            assertThat(context.getEnvironment().getActiveProfiles()).isEmpty();
            assertThat(context.getEnvironment().getDefaultProfiles()).contains("default");
        });
    }

    @Test
    void springProfilesActiveOverridesSpringProfilesDefault() {
        contextRunner
                .withPropertyValues("spring.profiles.default=dev", "spring.profiles.active=prod")
                .run(context -> {
                    assertThat(context.getEnvironment().getActiveProfiles()).contains("prod");
                    assertThat(context.getEnvironment().getActiveProfiles()).doesNotContain("dev");

                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DefaultGreetingProvider.class);
                });
    }

    @Test
    void multipleActiveProfilesStillActivateDev_andDisableNegationProfile() {
        contextRunner
                .withPropertyValues("spring.profiles.active=dev,prod", "app.mode=fancy")
                .run(context -> {
                    assertThat(context.getEnvironment().getActiveProfiles()).contains("dev", "prod");
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DevGreetingProvider.class);
                });
    }
}

