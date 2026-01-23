package com.learning.springboot.springcoreevents.part03_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreevents.part01_event_basics.UserRegisteredEvent;
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
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class SpringCoreEventsConcurrencyLabTest {

    private static final int EXPECTED_EVENTS = 30;

    @Test
    void asyncMulticaster_dispatchesAllEvents_underConcurrentPublishers() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncMulticasterConcurrencyConfig.class)) {
            EventsProbe probe = context.getBean(EventsProbe.class);

            ExecutorService publishers = Executors.newFixedThreadPool(8, namedThreadFactory("events-publisher-"));
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch publishedGate = new CountDownLatch(EXPECTED_EVENTS);
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            try {
                for (int i = 0; i < EXPECTED_EVENTS; i++) {
                    String name = "U" + i;
                    publishers.submit(() -> {
                        try {
                            startGate.await(1, TimeUnit.SECONDS);
                            context.publishEvent(new UserRegisteredEvent(name));
                        } catch (Throwable ex) {
                            errors.add(ex);
                        } finally {
                            publishedGate.countDown();
                        }
                    });
                }

                startGate.countDown();
                assertThat(publishedGate.await(2, TimeUnit.SECONDS)).isTrue();
                assertThat(probe.await(2, TimeUnit.SECONDS)).isTrue();

                assertThat(errors).isEmpty();
                assertThat(probe.count()).isEqualTo(EXPECTED_EVENTS);
                assertThat(probe.threadNames()).hasSize(EXPECTED_EVENTS);
                assertThat(probe.threadNames()).allMatch(t -> t.startsWith("events-multicaster-"));
            } finally {
                publishers.shutdownNow();
            }
        }
    }

    record EventsProbe(CountDownLatch latch, AtomicInteger counter, ConcurrentLinkedQueue<String> threadNames) {

        boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        int count() {
            return counter.get();
        }
    }

    @Configuration
    static class AsyncMulticasterConcurrencyConfig {

        @Bean
        EventsProbe eventsProbe() {
            return new EventsProbe(new CountDownLatch(EXPECTED_EVENTS), new AtomicInteger(), new ConcurrentLinkedQueue<>());
        }

        @Bean
        TaskExecutor applicationEventMulticasterTaskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(4);
            executor.setMaxPoolSize(4);
            executor.setThreadNamePrefix("events-multicaster-");
            executor.initialize();
            return executor;
        }

        @Bean(name = org.springframework.context.support.AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
        ApplicationEventMulticaster applicationEventMulticaster(TaskExecutor applicationEventMulticasterTaskExecutor) {
            SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
            multicaster.setTaskExecutor(applicationEventMulticasterTaskExecutor);
            return multicaster;
        }

        @Bean
        UserRegisteredListener userRegisteredListener(EventsProbe probe) {
            return new UserRegisteredListener(probe);
        }
    }

    static class UserRegisteredListener {
        private final EventsProbe probe;

        UserRegisteredListener(EventsProbe probe) {
            this.probe = probe;
        }

        @EventListener
        public void on(UserRegisteredEvent event) {
            probe.threadNames().add(Thread.currentThread().getName());
            probe.counter().incrementAndGet();
            probe.latch().countDown();
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

