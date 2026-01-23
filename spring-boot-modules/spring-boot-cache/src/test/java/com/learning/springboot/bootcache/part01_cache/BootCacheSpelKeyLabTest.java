package com.learning.springboot.bootcache.part01_cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.annotation.Cacheable;

class BootCacheSpelKeyLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(CacheConfig.class)
            .withBean(KeyedGreetingService.class);

    @Test
    void spelKeyCreatesIndependentCacheEntries() {
        runner.run(context -> {
            KeyedGreetingService service = context.getBean(KeyedGreetingService.class);

            String en1 = service.cachedGreeting("alice", "en");
            String en2 = service.cachedGreeting("alice", "en");
            String zh1 = service.cachedGreeting("alice", "zh");

            assertThat(en2).isEqualTo(en1);
            assertThat(zh1).isNotEqualTo(en1);
            assertThat(service.invocationCount()).isEqualTo(2);
        });
    }

    static class KeyedGreetingService {

        private final AtomicInteger invocations = new AtomicInteger();

        @Cacheable(cacheNames = "greetings", key = "#name + ':' + #lang")
        String cachedGreeting(String name, String lang) {
            int n = invocations.incrementAndGet();
            return lang + ":" + name + ":" + n;
        }

        int invocationCount() {
            return invocations.get();
        }
    }
}

