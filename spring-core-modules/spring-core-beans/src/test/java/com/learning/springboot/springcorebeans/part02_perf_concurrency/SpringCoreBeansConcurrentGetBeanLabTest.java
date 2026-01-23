package com.learning.springboot.springcorebeans.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 并发/性能实验：context refresh 之后，并发 getBean() 是安全的；候选选择（@Primary）保持一致。
 */
class SpringCoreBeansConcurrentGetBeanLabTest {

    @Test
    void concurrentGetBean_resolvesSamePrimaryCandidate_consistently() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrimaryCandidateConfig.class)) {
            int tasks = 40;
            ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("beans-perf-"));
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneGate = new CountDownLatch(tasks);
            ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            try {
                for (int i = 0; i < tasks; i++) {
                    pool.submit(() -> {
                        try {
                            startGate.await(1, TimeUnit.SECONDS);
                            results.add(context.getBean(Worker.class).id());
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
                assertThat(results).hasSize(tasks);
                assertThat(Set.copyOf(results)).containsExactly("primary");
            } finally {
                pool.shutdownNow();
            }
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

    interface Worker {
        String id();
    }

    @Configuration
    static class PrimaryCandidateConfig {
        @Bean
        @Primary
        Worker primaryWorker() {
            return () -> "primary";
        }

        @Bean
        Worker secondaryWorker() {
            return () -> "secondary";
        }
    }
}

