package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced;

import org.springframework.stereotype.Service;

@Service
public class SelfInvocationExampleService {

    @Traced
    public String outer(String name) {
        return "outer->" + inner(name);
    }

    @Traced
    public String inner(String name) {
        return "inner->" + name;
    }
}

