package com.learning.springboot.springcoreresources.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreresources.part01_resource_abstraction.ResourceReadingService;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 并发/性能实验：并发资源 pattern 扫描（PathMatchingResourcePatternResolver）结果一致且不抛异常。
 */
@SpringBootTest
class SpringCoreResourcesPatternResolverConcurrencyLabTest {

    @Autowired
    private ResourceReadingService resourceReadingService;

    @Test
    void concurrentPatternScanning_isStableAndDeterministic() throws Exception {
        int tasks = 20;
        ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("resources-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);
        ConcurrentLinkedQueue<String> fingerprints = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);
                        List<String> locations = resourceReadingService.listResourceLocations("classpath*:data/*.txt");
                        fingerprints.add(locations.toString());
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            assertThat(doneGate.await(5, TimeUnit.SECONDS)).isTrue();

            assertThat(errors).isEmpty();
            assertThat(fingerprints).hasSize(tasks);
            assertThat(Set.copyOf(fingerprints)).hasSize(1);
        } finally {
            pool.shutdownNow();
        }
    }

    private static ThreadFactory namedThreadFactory(String prefix) {
        AtomicInteger seq = new AtomicInteger();
        return r -> {
            Thread t = new Thread(r);
            t.setName(prefix + seq.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
    }
}

