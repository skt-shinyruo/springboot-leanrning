package com.learning.springboot.bootcache.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootcache.BootCacheApplication;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;

/**
 * 并发/性能实验：缓存 stampede 防护（@Cacheable(sync=true)）—— 并发下底层方法只会被调用一次。
 *
 * <p>断言策略：只断言“调用次数/返回值”，不基于耗时阈值判断快慢。
 */
@SpringBootTest(classes = { BootCacheApplication.class, BootCacheStampedeProtectionLabTest.SyncConfig.class })
class BootCacheStampedeProtectionLabTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SyncService syncService;

    @Autowired
    private SyncProbe probe;

    @Test
    void cacheableSync_preventsDuplicateComputationUnderConcurrentRequests() throws Exception {
        cacheManager.getCache("syncGreetings").clear();
        probe.reset();

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<String> f1 = pool.submit(() -> syncService.syncGreeting("alice"));
            Future<String> f2 = pool.submit(() -> syncService.syncGreeting("alice"));

            assertThat(probe.started.await(1, TimeUnit.SECONDS)).isTrue();
            probe.release.countDown();

            assertThat(f1.get(1, TimeUnit.SECONDS)).isEqualTo("sync:alice");
            assertThat(f2.get(1, TimeUnit.SECONDS)).isEqualTo("sync:alice");
            assertThat(probe.invocations.get()).isEqualTo(1);
        } finally {
            pool.shutdownNow();
        }
    }

    @TestConfiguration
    static class SyncConfig {

        @Bean
        SyncProbe syncProbe() {
            return new SyncProbe();
        }

        @Bean
        SyncService syncService(SyncProbe probe) {
            return new SyncService(probe);
        }
    }

    static class SyncProbe {
        private final AtomicInteger invocations = new AtomicInteger();
        private CountDownLatch started = new CountDownLatch(1);
        private CountDownLatch release = new CountDownLatch(1);

        void reset() {
            invocations.set(0);
            started = new CountDownLatch(1);
            release = new CountDownLatch(1);
        }
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
            probe.release.await(2, TimeUnit.SECONDS);
            return "sync:" + name;
        }
    }
}

