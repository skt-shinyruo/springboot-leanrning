package com.learning.springboot.springcoreevents.part02_async_and_transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.learning.springboot.springcoreevents.part01_event_basics.UserRegisteredEvent;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class SpringCoreEventsAsyncMulticasterLabTest {

    @Test
    void asyncMulticasterDispatchesListenersOnExecutorThread() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncMulticasterConfig.class)) {
            ThreadProbe probe = context.getBean(ThreadProbe.class);

            context.publishEvent(new UserRegisteredEvent("Alice"));

            assertThat(probe.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.threadName()).startsWith("events-multicaster-");
        }
    }

    record ThreadProbe(CountDownLatch latch, AtomicReference<String> threadNameRef) {

        boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        String threadName() {
            return threadNameRef.get();
        }
    }

    @Configuration
    static class AsyncMulticasterConfig {

        @Bean
        ThreadProbe threadProbe() {
            return new ThreadProbe(new CountDownLatch(1), new AtomicReference<>());
        }

        @Bean
        TaskExecutor applicationEventMulticasterTaskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
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
        UserRegisteredThreadCaptureListener userRegisteredThreadCaptureListener(ThreadProbe probe) {
            return new UserRegisteredThreadCaptureListener(probe);
        }
    }

    static class UserRegisteredThreadCaptureListener {
        private final ThreadProbe probe;

        UserRegisteredThreadCaptureListener(ThreadProbe probe) {
            this.probe = probe;
        }

        @EventListener
        public void on(UserRegisteredEvent event) {
            probe.threadNameRef().set(Thread.currentThread().getName());
            probe.latch().countDown();
        }
    }
}
