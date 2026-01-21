package com.learning.springboot.springcoreprofiles.part01_profiles;

public class DefaultGreetingProvider implements GreetingProvider {

    @Override
    public String greeting() {
        return "default greeting";
    }
}

