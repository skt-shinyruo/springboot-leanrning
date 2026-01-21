package com.learning.springboot.springcoreprofiles.part01_profiles;

public class DevGreetingProvider implements GreetingProvider {

    @Override
    public String greeting() {
        return "dev greeting";
    }
}

