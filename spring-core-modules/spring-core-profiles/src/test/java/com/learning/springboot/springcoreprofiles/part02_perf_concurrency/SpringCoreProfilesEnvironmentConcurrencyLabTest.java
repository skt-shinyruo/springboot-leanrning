package com.learning.springboot.springcoreprofiles.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreprofiles.part01_profiles.DevGreetingConfiguration;
import com.learning.springboot.springcoreprofiles.part01_profiles.GreetingProvider;
import com.learning.springboot.springcoreprofiles.part01_profiles.NonDevGreetingConfiguration;
import java.util.Arrays;
import java.util.Set;
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

/**
 * 并发/性能实验：Environment/profile/property 读取是线程安全的（并发读一致、不抛异常）。
 */
class SpringCoreProfilesEnvironmentConcurrencyLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(DevGreetingConfiguration.class, NonDevGreetingConfiguration.class)
            .withPropertyValues("app.mode=fancy");

    @Test
    void environmentReads_areConsistent_underConcurrentAccess() {
        runner.run(context -> {
            Environment environment = context.getEnvironment();
            GreetingProvider provider = context.getBean(GreetingProvider.class);

            int tasks = 20;
            ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("profiles-perf-"));
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneGate = new CountDownLatch(tasks);
            ConcurrentLinkedQueue<String> fingerprints = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            try {
                for (int i = 0; i < tasks; i++) {
                    pool.submit(() -> {
                        try {
                            startGate.await(1, TimeUnit.SECONDS);
                            String mode = environment.getProperty("app.mode");
                            String activeProfiles = Arrays.toString(environment.getActiveProfiles());
                            String greeting = provider.greeting();
                            fingerprints.add("mode=" + mode + ";profiles=" + activeProfiles + ";greeting=" + greeting);
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

