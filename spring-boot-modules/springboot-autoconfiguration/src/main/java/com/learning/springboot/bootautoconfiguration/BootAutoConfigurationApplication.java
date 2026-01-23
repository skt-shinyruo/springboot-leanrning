package com.learning.springboot.bootautoconfiguration;

import com.learning.springboot.bootautoconfiguration.service.GreetingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BootAutoConfigurationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootAutoConfigurationApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(GreetingService greetingService) {
        return args -> System.out.println("AUTOCONFIG:greeting=" + greetingService.greet("世界"));
    }
}
