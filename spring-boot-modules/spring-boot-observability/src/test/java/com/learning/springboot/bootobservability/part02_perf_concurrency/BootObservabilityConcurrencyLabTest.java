package com.learning.springboot.bootobservability.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootobservability.BootObservabilityApplication;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class BootObservabilityConcurrencyLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(BootObservabilityApplication.class);

    @Test
    void observationScope_isThreadLocal_andDoesNotLeak_underConcurrency() {
        runner.run(context -> {
            ObservationRegistry registry = context.getBean(ObservationRegistry.class);

            int tasks = 24;
            ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("bootobs-"));
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneGate = new CountDownLatch(tasks);
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            try {
                for (int i = 0; i < tasks; i++) {
                    String observationName = "obs-" + i;
                    pool.submit(() -> {
                        Observation observation = Observation.start(observationName, registry);
                        try {
                            startGate.await(1, TimeUnit.SECONDS);

                            try (Observation.Scope scope = observation.openScope()) {
                                Observation current = registry.getCurrentObservation();
                                if (current == null) {
                                    errors.add(new IllegalStateException("current observation is null"));
                                } else if (current != observation) {
                                    errors.add(new IllegalStateException("current observation != started observation"));
                                }
                            }
                        } catch (Throwable ex) {
                            errors.add(ex);
                        } finally {
                            observation.stop();
                            doneGate.countDown();
                        }
                    });
                }

                startGate.countDown();
                assertThat(doneGate.await(2, TimeUnit.SECONDS)).isTrue();

                assertThat(errors).isEmpty();
                assertThat(registry.getCurrentObservation()).isNull();
            } finally {
                pool.shutdownNow();
            }
        });
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

