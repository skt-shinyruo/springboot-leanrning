package com.learning.springboot.springcoreprofiles.part01_profiles;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class SpringCoreProfilesLabTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(DevGreetingConfiguration.class, NonDevGreetingConfiguration.class);

    @Test
    void defaultsToDefaultProviderWhenNoProfileAndNoProperty() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GreetingProvider.class);
            assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DefaultGreetingProvider.class);
            assertThat(context.getBean(GreetingProvider.class).greeting()).contains("default");
        });
    }

    @Test
    void usesFancyProviderWhenPropertyEnabled() {
        contextRunner
                .withPropertyValues("app.mode=fancy")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(FancyGreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class).greeting()).contains("fancy");
                });
    }

    @Test
    void usesDevProviderWhenDevProfileActive() {
        contextRunner
                .withPropertyValues("spring.profiles.active=dev")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DevGreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class).greeting()).contains("dev");
                });
    }

    @Test
    void devProfileWinsOverNonDevConditionals() {
        contextRunner
                .withPropertyValues("spring.profiles.active=dev", "app.mode=fancy")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DevGreetingProvider.class);
                });
    }

    @Test
    void matchIfMissingMakesDefaultProviderAppearWhenPropertyMissing() {
        contextRunner
                .withPropertyValues("spring.profiles.active=default")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DefaultGreetingProvider.class);
                });
    }

    @Test
    void unknownModeDoesNotMatchAnyConditional() {
        contextRunner
                .withPropertyValues("app.mode=unknown")
                .run(context -> assertThat(context).doesNotHaveBean(GreetingProvider.class));
    }

    @Test
    void profileNegationActivatesNonDevConfigurationWhenDevIsNotActive() {
        contextRunner
                .withPropertyValues("spring.profiles.active=prod")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DefaultGreetingProvider.class);
                });
    }

    @Test
    void profileNegationDeactivatesNonDevConfigurationWhenDevIsActive() {
        contextRunner
                .withPropertyValues("spring.profiles.active=dev")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingProvider.class);
                    assertThat(context.getBean(GreetingProvider.class)).isInstanceOf(DevGreetingProvider.class);
                });
    }

    @Test
    void conditionalOnPropertyMatchesByPrefixAndName() {
        contextRunner
                .withPropertyValues("app.mode=fancy")
                .run(context -> assertThat(context.getEnvironment().getProperty("app.mode")).isEqualTo("fancy"));
    }

    @Test
    void configurationClassesArePartOfTheContextWhenActivated() {
        contextRunner
                .withPropertyValues("spring.profiles.active=dev")
                .run(context -> {
                    assertThat(context).hasSingleBean(DevGreetingConfiguration.class);
                    assertThat(context).doesNotHaveBean(NonDevGreetingConfiguration.class);
                });
    }
}

