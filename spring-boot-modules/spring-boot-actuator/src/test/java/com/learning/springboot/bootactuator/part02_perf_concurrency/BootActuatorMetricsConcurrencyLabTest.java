package com.learning.springboot.bootactuator.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

/**
 * 并发/性能实验：并发请求驱动 metrics 变化（用可观测计数，而不是耗时阈值）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootActuatorMetricsConcurrencyLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void concurrentHealthRequests_incrementHttpServerRequestsCount() throws Exception {
        long before = countHealth200Requests();

        int tasks = 12;
        ExecutorService pool = Executors.newFixedThreadPool(6, namedThreadFactory("bootactuator-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);
                        ResponseEntity<String> resp = restTemplate.getForEntity("/actuator/health", String.class);
                        assertThat(resp.getStatusCode().value()).isEqualTo(200);
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

            long after = countHealth200Requests();
            assertThat(after - before)
                    .as("COUNT 基于可观测计数；即使已存在历史请求，也应至少增加本次并发请求数")
                    .isGreaterThanOrEqualTo(tasks);
        } finally {
            pool.shutdownNow();
        }
    }

    private long countHealth200Requests() {
        List<Timer> timers = meterRegistry.getMeters().stream()
                .filter(m -> "http.server.requests".equals(m.getId().getName()))
                .filter(m -> m instanceof Timer)
                .map(m -> (Timer) m)
                .filter(t -> hasTag(t.getId(), "uri", "/actuator/health"))
                .filter(t -> hasTag(t.getId(), "status", "200"))
                .toList();

        return timers.stream().mapToLong(Timer::count).sum();
    }

    private static boolean hasTag(Meter.Id id, String key, String value) {
        return id.getTags().stream().anyMatch(t -> t.getKey().equals(key) && t.getValue().equals(value));
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

