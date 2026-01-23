package com.learning.springboot.bootlogging.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class BootLoggingConcurrencyLabTest {

    @Test
    void mdcIsThreadLocal_andDoesNotLeakAcrossThreads_underConcurrentUsage() throws Exception {
        int tasks = 32;
        ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("bootlogging-mdc-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);

        ConcurrentLinkedQueue<String> unexpectedBeforeSet = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> observedTraceIds = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                String traceId = "trace-" + i;
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);

                        String before = MDC.get("traceId");
                        if (before != null) {
                            unexpectedBeforeSet.add(before);
                        }

                        MDC.put("traceId", traceId);
                        observedTraceIds.add(MDC.get("traceId"));
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        MDC.clear();
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            assertThat(doneGate.await(2, TimeUnit.SECONDS)).isTrue();

            assertThat(errors).isEmpty();
            assertThat(unexpectedBeforeSet).isEmpty();
            assertThat(observedTraceIds).hasSize(tasks);
            assertThat(observedTraceIds).doesNotHaveDuplicates();
            assertThat(MDC.get("traceId")).isNull();
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

