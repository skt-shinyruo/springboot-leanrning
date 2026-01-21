package com.learning.springboot.springcorebeans.part05_aot_and_real_world.serviceloader;

public class HiGreetingService implements DemoGreetingService {

    @Override
    public String hello() {
        return "hi";
    }
}

