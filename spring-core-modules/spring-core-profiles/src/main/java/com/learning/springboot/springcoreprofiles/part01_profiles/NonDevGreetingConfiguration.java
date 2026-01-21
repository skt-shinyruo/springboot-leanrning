package com.learning.springboot.springcoreprofiles.part01_profiles;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!dev")
public class NonDevGreetingConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "mode", havingValue = "fancy")
    public GreetingProvider fancyGreetingProvider() {
        return new FancyGreetingProvider();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "mode", havingValue = "default", matchIfMissing = true)
    public GreetingProvider defaultGreetingProvider() {
        return new DefaultGreetingProvider();
    }
}

