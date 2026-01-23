package com.learning.springboot.bootcache.part01_cache;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class GreetingCacheService {

    private final AtomicInteger invocations = new AtomicInteger();

    @Cacheable(cacheNames = "greetings", key = "#name")
    String cachedGreeting(String name) {
        int n = invocations.incrementAndGet();
        return "hello:" + name + ":v" + n;
    }

    @CachePut(cacheNames = "greetings", key = "#name")
    String updateGreeting(String name) {
        int n = invocations.incrementAndGet();
        return "hello:" + name + ":v" + n;
    }

    @CacheEvict(cacheNames = "greetings", key = "#name")
    void evictGreeting(String name) {
    }

    @Cacheable(cacheNames = "conditionalGreetings", key = "#name", condition = "#name != null && !#name.isBlank()")
    String conditionalGreeting(String name) {
        int n = invocations.incrementAndGet();
        return "conditional:" + name + ":v" + n;
    }

    @Cacheable(cacheNames = "unlessGreetings", key = "#name", unless = "#result.contains('skip')")
    String unlessGreeting(String name) {
        int n = invocations.incrementAndGet();
        if ("skip".equals(name)) {
            return "skip";
        }
        return "unless:" + name + ":v" + n;
    }

    int invocationCount() {
        return invocations.get();
    }
}

