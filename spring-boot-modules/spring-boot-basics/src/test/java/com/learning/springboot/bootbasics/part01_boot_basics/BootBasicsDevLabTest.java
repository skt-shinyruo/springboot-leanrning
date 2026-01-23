package com.learning.springboot.bootbasics.part01_boot_basics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class BootBasicsDevLabTest {

    @Autowired
    private AppProperties properties;

    @Autowired
    private GreetingProvider greetingProvider;

    @Autowired
    private Environment environment;

    @Test
    void loadsDevProfileConfigurationAndBean() {
        assertThat(properties.getName()).isEqualTo("springboot-basics");
        assertThat(properties.getGreeting()).isEqualTo("你好，DEV 配置");
        assertThat(properties.isFeatureEnabled()).isTrue();

        assertThat(greetingProvider).isInstanceOf(DevGreetingProvider.class);
        assertThat(greetingProvider.greeting()).contains("dev bean");
    }

    @Test
    void activeProfilesContainDev() {
        assertThat(environment.getActiveProfiles()).contains("dev");
    }

    @Test
    void devProfileOverridesFeatureFlag() {
        assertThat(environment.getProperty("app.feature-enabled")).isEqualTo("true");
        assertThat(properties.isFeatureEnabled()).isTrue();
    }

    @Test
    void greetingProviderUsesDevGreeting() {
        assertThat(greetingProvider.greeting()).contains("DEV");
    }
}
