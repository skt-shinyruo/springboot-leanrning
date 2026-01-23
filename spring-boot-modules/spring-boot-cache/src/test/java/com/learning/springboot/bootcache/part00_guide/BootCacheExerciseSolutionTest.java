package com.learning.springboot.bootcache.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 参考实现：对齐 BootCacheExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class BootCacheExerciseSolutionTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(SolutionCacheConfig.class)
            .withBean(DefaultKeyService.class)
            .withBean(CustomKeyService.class)
            .withBean(EvictAllService.class)
            .withBean(NegativeCacheService.class)
            .withBean(ExpireAfterAccessService.class);

    @Test
    void solution_defaultKeyGeneration_multiArgsUseSimpleKey() {
        runner.run(context -> {
            DefaultKeyService service = context.getBean(DefaultKeyService.class);

            String v1 = service.greet("alice", "en");
            String v2 = service.greet("alice", "en");
            String v3 = service.greet("alice", "zh");

            assertThat(v2).isEqualTo(v1);
            assertThat(v3).isNotEqualTo(v1);
            assertThat(service.invocations()).isEqualTo(2);
        });
    }

    @Test
    void solution_customKey_makesAliceAndLowercaseHitSameCacheEntry() {
        runner.run(context -> {
            CustomKeyService service = context.getBean(CustomKeyService.class);

            String v1 = service.greet("Alice");
            String v2 = service.greet("alice");

            assertThat(v2).isEqualTo(v1);
            assertThat(service.invocations()).isEqualTo(1);
        });
    }

    @Test
    void solution_evictAllEntries_clearsEntireCache() {
        runner.run(context -> {
            EvictAllService service = context.getBean(EvictAllService.class);

            String a1 = service.greet("alice", "en");
            String b1 = service.greet("bob", "en");
            assertThat(service.invocations()).isEqualTo(2);

            service.evictAll();

            String a2 = service.greet("alice", "en");
            String b2 = service.greet("bob", "en");

            assertThat(a2).isNotEqualTo(a1);
            assertThat(b2).isNotEqualTo(b1);
            assertThat(service.invocations()).isEqualTo(4);
        });
    }

    @Test
    void solution_negativeCache_cachesEmptyResultDeterministically() {
        runner.run(context -> {
            NegativeCacheService service = context.getBean(NegativeCacheService.class);

            Optional<String> r1 = service.findGreeting("missing");
            Optional<String> r2 = service.findGreeting("missing");

            assertThat(r1).isEmpty();
            assertThat(r2).isEmpty();
            assertThat(service.invocations()).isEqualTo(1);
        });
    }

    @Test
    void solution_expireAfterAccess_canBeTestedWithManualTicker() {
        runner.run(context -> {
            ExpireAfterAccessService service = context.getBean(ExpireAfterAccessService.class);
            ManualTicker ticker = (ManualTicker) context.getBean(Ticker.class);

            String v1 = service.greet("alice");
            ticker.advance(Duration.ofSeconds(4));
            String v2 = service.greet("alice");
            ticker.advance(Duration.ofSeconds(4));
            String v3 = service.greet("alice");

            assertThat(v2).isEqualTo(v1);
            assertThat(v3).isEqualTo(v1);
            assertThat(service.invocations()).isEqualTo(1);

            ticker.advance(Duration.ofSeconds(6));
            String v4 = service.greet("alice");
            assertThat(v4).isNotEqualTo(v1);
            assertThat(service.invocations()).isEqualTo(2);
        });
    }

    @EnableCaching
    @Configuration
    static class SolutionCacheConfig {

        @Bean
        Ticker ticker() {
            return new ManualTicker();
        }

        @Bean
        CacheManager cacheManager(Ticker ticker) {
            CaffeineCache defaultKey = new CaffeineCache("defaultKey",
                    Caffeine.newBuilder()
                            .expireAfterWrite(Duration.ofSeconds(30))
                            .ticker(ticker)
                            .build());

            CaffeineCache lowerKey = new CaffeineCache("lowerKey",
                    Caffeine.newBuilder()
                            .expireAfterWrite(Duration.ofSeconds(30))
                            .ticker(ticker)
                            .build());

            CaffeineCache negative = new CaffeineCache("negative",
                    Caffeine.newBuilder()
                            .expireAfterWrite(Duration.ofSeconds(30))
                            .ticker(ticker)
                            .build());

            CaffeineCache accessExpire = new CaffeineCache("accessExpire",
                    Caffeine.newBuilder()
                            .expireAfterAccess(Duration.ofSeconds(5))
                            .ticker(ticker)
                            .build());

            SimpleCacheManager manager = new SimpleCacheManager();
            manager.setCaches(List.of(defaultKey, lowerKey, negative, accessExpire));
            return manager;
        }
    }

    static final class ManualTicker implements Ticker {
        private final AtomicLong nanos = new AtomicLong();

        @Override
        public long read() {
            return nanos.get();
        }

        void advance(Duration duration) {
            nanos.addAndGet(duration.toNanos());
        }
    }

    static class DefaultKeyService {
        private final AtomicInteger invocations = new AtomicInteger();

        @Cacheable(cacheNames = "defaultKey")
        String greet(String name, String lang) {
            return lang + ":" + name + ":" + invocations.incrementAndGet();
        }

        int invocations() {
            return invocations.get();
        }
    }

    static class CustomKeyService {
        private final AtomicInteger invocations = new AtomicInteger();

        @Cacheable(cacheNames = "lowerKey", key = "#name.toLowerCase()")
        String greet(String name) {
            return name + ":" + invocations.incrementAndGet();
        }

        int invocations() {
            return invocations.get();
        }
    }

    static class EvictAllService {
        private final AtomicInteger invocations = new AtomicInteger();

        @Cacheable(cacheNames = "defaultKey")
        String greet(String name, String lang) {
            return lang + ":" + name + ":" + invocations.incrementAndGet();
        }

        @CacheEvict(cacheNames = "defaultKey", allEntries = true)
        void evictAll() {
        }

        int invocations() {
            return invocations.get();
        }
    }

    static class NegativeCacheService {
        private final AtomicInteger invocations = new AtomicInteger();

        @Cacheable(cacheNames = "negative", key = "#name")
        Optional<String> findGreeting(String name) {
            invocations.incrementAndGet();
            if (name != null && name.startsWith("hit")) {
                return Optional.of("found:" + name);
            }
            return Optional.empty();
        }

        int invocations() {
            return invocations.get();
        }
    }

    static class ExpireAfterAccessService {
        private final AtomicInteger invocations = new AtomicInteger();

        @Cacheable(cacheNames = "accessExpire", key = "#name")
        String greet(String name) {
            return "v" + invocations.incrementAndGet();
        }

        int invocations() {
            return invocations.get();
        }
    }
}

