package com.learning.springboot.bootautoconfiguration.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootautoconfiguration.autoconfig.GreetingAutoConfiguration;
import com.learning.springboot.bootautoconfiguration.autoconfig.GreetingDecoratorAutoConfiguration;
import com.learning.springboot.bootautoconfiguration.service.GreetingService;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class BootAutoConfigurationConcurrencyLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    GreetingAutoConfiguration.class,
                    GreetingDecoratorAutoConfiguration.class))
            .withPropertyValues("demo.greeting.decorate=true");

    @Test
    void retrievingPrimaryBeanAndCallingService_isConsistent_underConcurrency() {
        runner.run(context -> {
            int tasks = 24;
            ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("autoconfig-conc-"));
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneGate = new CountDownLatch(tasks);

            ConcurrentLinkedQueue<Integer> identities = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            try {
                for (int i = 0; i < tasks; i++) {
                    String name = "U" + i;
                    pool.submit(() -> {
                        try {
                            startGate.await(1, TimeUnit.SECONDS);

                            GreetingService service = context.getBean(GreetingService.class);
                            identities.add(System.identityHashCode(service));
                            results.add(service.greet(name));
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
                assertThat(results).hasSize(tasks);
                assertThat(results).allMatch(s -> s.startsWith("LOG("));

                assertThat(identities).hasSize(tasks);
                assertThat(new HashSet<>(identities)).hasSize(1);
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

