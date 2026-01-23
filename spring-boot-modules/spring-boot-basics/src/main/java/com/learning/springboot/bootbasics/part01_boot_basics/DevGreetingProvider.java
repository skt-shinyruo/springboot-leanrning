package com.learning.springboot.bootbasics.part01_boot_basics;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevGreetingProvider implements GreetingProvider {

    private final AppProperties properties;

    public DevGreetingProvider(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public String greeting() {
        return properties.getGreeting() + " (from dev bean)";
    }
}
