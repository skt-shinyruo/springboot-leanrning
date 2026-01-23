package com.learning.springboot.boottesting.part01_testing;

import org.springframework.stereotype.Component;

@Component
public class NonWebSupport {

    public String marker() {
        return "non_web_support";
    }
}

