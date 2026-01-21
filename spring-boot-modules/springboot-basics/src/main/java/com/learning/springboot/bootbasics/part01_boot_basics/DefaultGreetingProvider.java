package com.learning.springboot.bootbasics.part01_boot_basics;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")
public class DefaultGreetingProvider implements GreetingProvider {

    private final AppProperties properties;

    public DefaultGreetingProvider(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public String greeting() {
        return properties.getGreeting() + " (from default bean)";
    }
}
