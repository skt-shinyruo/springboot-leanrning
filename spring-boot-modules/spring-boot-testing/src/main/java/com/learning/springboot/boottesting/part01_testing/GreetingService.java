package com.learning.springboot.boottesting.part01_testing;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String greet(String name) {
        return "Hello, " + name;
    }
}
