package com.learning.springboot.springcoreevents.part01_event_basics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class SpringCoreEventsMechanicsLabTest {

    @Test
    void listenerExceptionsPropagateToPublisher_byDefault() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ThrowingListenerConfig.class)) {
            assertThatThrownBy(() -> context.publishEvent(new UserRegisteredEvent("Boom")))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("listener boom");
        }
    }

    @Test
    void asyncListenerRunsOnDifferentThread_whenEnableAsyncIsOn() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncEnabledConfig.class)) {
            AsyncProbe probe = context.getBean(AsyncProbe.class);

            context.publishEvent(new UserRegisteredEvent("Alice"));

            assertThat(probe.latch().await(2, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.threadName()).startsWith("events-async-");
        }
    }

    @Test
    void asyncAnnotationIsIgnored_withoutEnableAsync() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncDisabledConfig.class)) {
            AsyncProbe probe = context.getBean(AsyncProbe.class);

            context.publishEvent(new UserRegisteredEvent("Bob"));

            assertThat(probe.latch().await(2, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.threadName()).isEqualTo(Thread.currentThread().getName());
        }
    }

    @Test
    void youCanPublishAnyObjectAndMatchListenerParameterType() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StringOnlyListenerConfig.class)) {
            StringOnlyProbe probe = context.getBean(StringOnlyProbe.class);

            context.publishEvent("hello");

            assertThat(probe.last().get()).isEqualTo("hello");
        }
    }

    @Configuration
    static class ThrowingListenerConfig {
        @Bean
        ThrowingListener throwingListener() {
            return new ThrowingListener();
        }
    }

    static class ThrowingListener {
        @EventListener
        public void on(UserRegisteredEvent event) {
            throw new IllegalStateException("listener boom");
        }
    }

    record AsyncProbe(CountDownLatch latch, AtomicReference<String> threadNameRef) {
        String threadName() {
            return threadNameRef.get();
        }
    }

    @Configuration
    @EnableAsync
    static class AsyncEnabledConfig {
        @Bean
        AsyncProbe asyncProbe() {
            return new AsyncProbe(new CountDownLatch(1), new AtomicReference<>());
        }

        @Bean
        ThreadPoolTaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setThreadNamePrefix("events-async-");
            executor.initialize();
            return executor;
        }

        @Bean
        AsyncListener asyncListener(AsyncProbe probe) {
            return new AsyncListener(probe);
        }
    }

    @Configuration
    static class AsyncDisabledConfig {
        @Bean
        AsyncProbe asyncProbe() {
            return new AsyncProbe(new CountDownLatch(1), new AtomicReference<>());
        }

        @Bean
        AsyncListener asyncListener(AsyncProbe probe) {
            return new AsyncListener(probe);
        }
    }

    static class AsyncListener {
        private final AsyncProbe probe;

        AsyncListener(AsyncProbe probe) {
            this.probe = probe;
        }

        @Async
        @EventListener
        public void on(UserRegisteredEvent event) {
            probe.threadNameRef().set(Thread.currentThread().getName());
            probe.latch().countDown();
        }
    }

    record StringOnlyProbe(AtomicReference<String> last) {
    }

    @Configuration
    static class StringOnlyListenerConfig {
        @Bean
        StringOnlyProbe stringOnlyProbe() {
            return new StringOnlyProbe(new AtomicReference<>());
        }

        @Bean
        StringOnlyListener stringOnlyListener(StringOnlyProbe probe) {
            return new StringOnlyListener(probe);
        }
    }

    static class StringOnlyListener {
        private final StringOnlyProbe probe;

        StringOnlyListener(StringOnlyProbe probe) {
            this.probe = probe;
        }

        @EventListener
        public void on(String payload) {
            probe.last().set(payload);
        }
    }
}
