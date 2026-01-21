package com.learning.springboot.springcoreprofiles.part01_profiles;

import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ProfilesDemoRunner implements ApplicationRunner {

    private final GreetingProvider greetingProvider;
    private final Environment environment;

    public ProfilesDemoRunner(GreetingProvider greetingProvider, Environment environment) {
        this.greetingProvider = greetingProvider;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("== spring-core-profiles ==");
        System.out.println("activeProfiles=" + Arrays.toString(environment.getActiveProfiles()));
        System.out.println("app.mode=" + environment.getProperty("app.mode"));
        System.out.println("greetingProvider=" + greetingProvider.getClass().getSimpleName());
        System.out.println("greeting=" + greetingProvider.greeting());
    }
}

