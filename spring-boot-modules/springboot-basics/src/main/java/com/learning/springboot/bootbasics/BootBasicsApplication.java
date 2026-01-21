package com.learning.springboot.bootbasics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BootBasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootBasicsApplication.class, args);
    }
}
