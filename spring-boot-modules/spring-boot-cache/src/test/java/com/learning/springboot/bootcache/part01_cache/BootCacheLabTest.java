package com.learning.springboot.bootcache.part01_cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.benmanes.caffeine.cache.Ticker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class BootCacheLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(CacheConfig.class)
            .withBean(GreetingCacheService.class);

    @Test
    void cacheableCachesResultForSameKey() {
        runner.run(context -> {
            GreetingCacheService service = context.getBean(GreetingCacheService.class);
            String v1 = service.cachedGreeting("alice");
            String v2 = service.cachedGreeting("alice");

            assertThat(v2).isEqualTo(v1);
            assertThat(service.invocationCount()).isEqualTo(1);
        });
    }

    @Test
    void cacheableUsesDifferentEntriesForDifferentKeys() {
        runner.run(context -> {
            GreetingCacheService service = context.getBean(GreetingCacheService.class);
            String v1 = service.cachedGreeting("alice");
            String v2 = service.cachedGreeting("bob");

            assertThat(v2).isNotEqualTo(v1);
            assertThat(service.invocationCount()).isEqualTo(2);
        });
    }

    @Test
    void cachePutUpdatesCacheValue() {
        runner.run(context -> {
            GreetingCacheService service = context.getBean(GreetingCacheService.class);
            String cached = service.cachedGreeting("alice");
            String updated = service.updateGreeting("alice");
            String cachedAfterUpdate = service.cachedGreeting("alice");

            assertThat(updated).isNotEqualTo(cached);
            assertThat(cachedAfterUpdate).isEqualTo(updated);
            assertThat(service.invocationCount()).isEqualTo(2);
        });
    }

    @Test
    void cacheEvictRemovesEntry() {
        runner.run(context -> {
            GreetingCacheService service = context.getBean(GreetingCacheService.class);
            String cached = service.cachedGreeting("alice");
            service.evictGreeting("alice");
            String afterEvict = service.cachedGreeting("alice");

            assertThat(afterEvict).isNotEqualTo(cached);
            assertThat(service.invocationCount()).isEqualTo(2);
        });
    }

    @Test
    void conditionPreventsCachingWhenFalse() {
        runner.run(context -> {
            GreetingCacheService service = context.getBean(GreetingCacheService.class);
            String v1 = service.conditionalGreeting("");
            String v2 = service.conditionalGreeting("");

            assertThat(v2).isNotEqualTo(v1);
            assertThat(service.invocationCount()).isEqualTo(2);
        });
    }

    @Test
    void unlessPreventsCachingBasedOnResult() {
        runner.run(context -> {
            GreetingCacheService service = context.getBean(GreetingCacheService.class);
            String v1 = service.unlessGreeting("skip");
            String v2 = service.unlessGreeting("skip");

            assertThat(v2).isEqualTo("skip");
            assertThat(v1).isEqualTo("skip");
            assertThat(service.invocationCount()).isEqualTo(2);
        });
    }

    @Test
    void cacheManagerProvidesNamedCaches() {
        runner.run(context -> {
            CacheManager cacheManager = context.getBean(CacheManager.class);
            assertThat(cacheManager.getCache("greetings")).isNotNull();
            assertThat(cacheManager.getCache("conditionalGreetings")).isNotNull();
            assertThat(cacheManager.getCache("unlessGreetings")).isNotNull();
        });
    }

    @Test
    void expiryCanBeTestedDeterministicallyWithManualTicker() {
        ApplicationContextRunner tickerRunner = new ApplicationContextRunner()
                .withUserConfiguration(CacheConfig.class)
                .withBean(GreetingCacheService.class)
                .withBean(Ticker.class, ManualTicker::new);

        tickerRunner.run(context -> {
            GreetingCacheService service = context.getBean(GreetingCacheService.class);
            ManualTicker ticker = (ManualTicker) context.getBean(Ticker.class);

            String v1 = service.cachedGreeting("alice");
            ticker.advance(Duration.ofSeconds(10));
            String v2 = service.cachedGreeting("alice");

            assertThat(v2).isNotEqualTo(v1);
            assertThat(service.invocationCount()).isEqualTo(2);
        });
    }

    @Test
    void syncTrueAvoidsDuplicateComputationsForSameKey() throws Exception {
        ApplicationContextRunner syncRunner = new ApplicationContextRunner()
                .withUserConfiguration(CacheConfig.class, SyncServiceConfig.class)
                .withBean(GreetingCacheService.class);

        syncRunner.run(context -> {
            SyncService service = context.getBean(SyncService.class);
            SyncProbe probe = context.getBean(SyncProbe.class);

            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                Future<String> f1 = pool.submit(() -> service.syncGreeting("alice"));
                assertThat(probe.started.await(1, TimeUnit.SECONDS)).isTrue();
                Future<String> f2 = pool.submit(() -> service.syncGreeting("alice"));

                probe.release.countDown();

                assertThat(f1.get(1, TimeUnit.SECONDS)).isEqualTo("sync:alice");
                assertThat(f2.get(1, TimeUnit.SECONDS)).isEqualTo("sync:alice");
                assertThat(probe.invocations.get()).isEqualTo(1);
            } finally {
                pool.shutdownNow();
            }
        });
    }

    @Test
    void cacheStatsAreAvailableOnNativeCaffeineCache() {
        runner.run(context -> {
            CacheManager cacheManager = context.getBean(CacheManager.class);
            Object nativeCache = cacheManager.getCache("greetings").getNativeCache();
            assertThat(nativeCache).isNotNull();
        });
    }

    @Configuration
    static class SyncServiceConfig {

        @Bean
        SyncProbe syncProbe() {
            return new SyncProbe();
        }

        @Bean
        SyncService syncService() {
            return new SyncService(syncProbe());
        }
    }

    static class SyncProbe {
        private final AtomicInteger invocations = new AtomicInteger();
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);
    }

    static class SyncService {
        private final SyncProbe probe;

        SyncService(SyncProbe probe) {
            this.probe = probe;
        }

        @Cacheable(cacheNames = "syncGreetings", key = "#name", sync = true)
        String syncGreeting(String name) throws InterruptedException {
            probe.invocations.incrementAndGet();
            probe.started.countDown();
            probe.release.await(1, TimeUnit.SECONDS);
            return "sync:" + name;
        }
    }
}
