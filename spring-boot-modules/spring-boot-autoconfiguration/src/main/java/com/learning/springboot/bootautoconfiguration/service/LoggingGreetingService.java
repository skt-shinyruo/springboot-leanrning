package com.learning.springboot.bootautoconfiguration.service;

public class LoggingGreetingService implements GreetingService {

    private final GreetingService delegate;

    public LoggingGreetingService(GreetingService delegate) {
        this.delegate = delegate;
    }

    @Override
    public String greet(String name) {
        String result = delegate.greet(name);
        return "LOG(" + result + ")";
    }
}
