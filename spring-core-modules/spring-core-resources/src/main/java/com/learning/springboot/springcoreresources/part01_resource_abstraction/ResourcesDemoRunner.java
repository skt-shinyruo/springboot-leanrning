package com.learning.springboot.springcoreresources.part01_resource_abstraction;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ResourcesDemoRunner implements ApplicationRunner {

    private final ResourceReadingService resourceReadingService;

    public ResourcesDemoRunner(ResourceReadingService resourceReadingService) {
        this.resourceReadingService = resourceReadingService;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("== spring-core-resources ==");

        String hello = resourceReadingService.readClasspathText("classpath:data/hello.txt").trim();
        System.out.println("classpath:data/hello.txt=" + hello);

        System.out.println("pattern.classpath*=data/*.txt=" + resourceReadingService
                .listResourceLocations("classpath*:data/*.txt"));
    }
}

