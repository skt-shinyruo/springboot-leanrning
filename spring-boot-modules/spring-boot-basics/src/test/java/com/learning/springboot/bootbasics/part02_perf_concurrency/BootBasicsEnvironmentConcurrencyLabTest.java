package com.learning.springboot.bootbasics.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootbasics.BootBasicsApplication;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.Environment;

class BootBasicsEnvironmentConcurrencyLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(BootBasicsApplication.class)
            .withPropertyValues(
                    "app.greeting=Hello",
                    "app.feature-enabled=true"
            );

    @Test
    void environmentPropertyResolution_isThreadSafeAndConsistent_underConcurrentReads() {
        runner.run(context -> {
            Environment environment = context.getEnvironment();

            int tasks = 20;
            ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("bootbasics-perf-"));
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneGate = new CountDownLatch(tasks);
            ConcurrentLinkedQueue<String> greetings = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            try {
                for (int i = 0; i < tasks; i++) {
                    pool.submit(() -> {
                        try {
                            startGate.await(1, TimeUnit.SECONDS);
                            greetings.add(environment.getProperty("app.greeting"));
                        } catch (Throwable ex) {
                            errors.add(ex);
                        } finally {
                            doneGate.countDown();
                        }
                    });
                }

                startGate.countDown();
                assertThat(doneGate.await(2, TimeUnit.SECONDS)).isTrue();

                assertThat(errors).isEmpty();
                assertThat(greetings).hasSize(tasks);
                assertThat(greetings).allMatch("Hello"::equals);
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

