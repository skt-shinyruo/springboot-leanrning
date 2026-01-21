package com.learning.springboot.bootbasics.part01_boot_basics;

import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupPrinter implements ApplicationRunner {

    private final AppProperties properties;
    private final GreetingProvider greetingProvider;
    private final Environment environment;

    public StartupPrinter(AppProperties properties, GreetingProvider greetingProvider, Environment environment) {
        this.properties = properties;
        this.greetingProvider = greetingProvider;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("== springboot-basics ==");
        System.out.println("activeProfiles=" + Arrays.toString(environment.getActiveProfiles()));
        System.out.println("app.name=" + properties.getName());
        System.out.println("app.greeting=" + properties.getGreeting());
        System.out.println("app.featureEnabled=" + properties.isFeatureEnabled());
        System.out.println("greetingProvider=" + greetingProvider.getClass().getSimpleName());
        System.out.println("greeting=" + greetingProvider.greeting());
    }
}
