package com.learning.springboot.springcoreprofiles.part01_profiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevGreetingConfiguration {

    @Bean
    public GreetingProvider greetingProvider() {
        return new DevGreetingProvider();
    }
}

