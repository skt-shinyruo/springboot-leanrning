package com.learning.springboot.bootautoconfiguration.service;

public class DefaultGreetingService implements GreetingService {

    private final String message;

    public DefaultGreetingService(String message) {
        this.message = message;
    }

    @Override
    public String greet(String name) {
        return message + "，" + name;
    }
}
