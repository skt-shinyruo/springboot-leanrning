package com.learning.springboot.springcorebeans.part05_aot_and_real_world.methodinjection;

public class ReplaceableGreetingService {

    public String greet(String name) {
        return "original:" + name;
    }
}

