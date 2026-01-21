package com.learning.springboot.springcorebeans.part01_ioc_container;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

@Component
public class LifecycleLogger {

    private boolean initialized;

    @PostConstruct
    void onInit() {
        initialized = true;
        System.out.println("LifecycleLogger: @PostConstruct called");
    }

    @PreDestroy
    void onDestroy() {
        System.out.println("LifecycleLogger: @PreDestroy called");
    }

    public boolean isInitialized() {
        return initialized;
    }
}
