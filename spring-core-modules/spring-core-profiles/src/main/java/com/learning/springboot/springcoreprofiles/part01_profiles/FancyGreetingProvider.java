package com.learning.springboot.springcoreprofiles.part01_profiles;

public class FancyGreetingProvider implements GreetingProvider {

    @Override
    public String greeting() {
        return "fancy greeting";
    }
}

