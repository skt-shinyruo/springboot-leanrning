package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

import org.springframework.stereotype.Service;

@Service
public class TracedBusinessService {

    @Traced
    public String process(String input) {
        return "processed:" + input;
    }
}

