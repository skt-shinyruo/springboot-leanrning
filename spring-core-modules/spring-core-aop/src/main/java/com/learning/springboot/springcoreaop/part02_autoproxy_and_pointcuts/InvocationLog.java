package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

@Component
public class InvocationLog {

    private final AtomicInteger invocationCount = new AtomicInteger();
    private final AtomicReference<String> lastMethod = new AtomicReference<>();

    public void record(String methodSignature) {
        lastMethod.set(methodSignature);
        invocationCount.incrementAndGet();
    }

    public int count() {
        return invocationCount.get();
    }

    public String lastMethod() {
        return lastMethod.get();
    }

    public void reset() {
        invocationCount.set(0);
        lastMethod.set(null);
    }
}

