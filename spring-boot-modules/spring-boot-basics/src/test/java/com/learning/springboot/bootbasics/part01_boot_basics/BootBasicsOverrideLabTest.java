package com.learning.springboot.bootbasics.part01_boot_basics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.greeting=Hello from test override",
        "app.feature-enabled=true"
})
class BootBasicsOverrideLabTest {

    @Autowired
    private AppProperties properties;

    @Autowired
    private GreetingProvider greetingProvider;

    @Test
    void testPropertiesOverrideApplicationProperties() {
        assertThat(properties.getGreeting()).isEqualTo("Hello from test override");
        assertThat(properties.isFeatureEnabled()).isTrue();
    }

    @Test
    void beansSeeOverriddenProperties() {
        assertThat(greetingProvider.greeting()).contains("Hello from test override");
    }
}

