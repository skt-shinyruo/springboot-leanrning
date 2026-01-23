package com.learning.springboot.bootlogging;

import com.learning.springboot.bootlogging.service.LoggingDemoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BootLoggingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootLoggingApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(LoggingDemoService demoService) {
        return args -> demoService.logOnce("startup");
    }
}
