package com.learning.springboot.springcoreevents.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreevents.part01_event_basics.InMemoryAuditLog;
import com.learning.springboot.springcoreevents.part01_event_basics.UserRegisteredEvent;
import com.learning.springboot.springcoreevents.part01_event_basics.UserRegistrationService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 参考实现：对齐 SpringCoreEventsExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class SpringCoreEventsExerciseSolutionTest {

    @Test
    void solution_multipleListeners() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MultipleListenersConfig.class)) {
            InMemoryAuditLog auditLog = context.getBean(InMemoryAuditLog.class);
            UserRegistrationService service = context.getBean(UserRegistrationService.class);

            auditLog.clear();
            service.register("Alice");

            assertThat(auditLog.entries())
                    .contains("userRegistered:Alice", "userRegisteredSecondary:Alice");
        }
    }

    @Test
    void solution_ordering() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(OrderingConfig.class)) {
            InMemoryAuditLog auditLog = context.getBean(InMemoryAuditLog.class);

            auditLog.clear();
            context.publishEvent(new UserRegisteredEvent("Alice"));

            assertThat(auditLog.entries())
                    .as("用 @Order 固定 listener 顺序，避免学到不保证的默认行为")
                    .containsExactly("ordered:first", "ordered:second");
        }
    }

    @Test
    void solution_asyncListenerRunsOnDifferentThread_whenEnableAsyncIsOn() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncListenerConfig.class)) {
            ThreadProbe probe = context.getBean(ThreadProbe.class);

            context.publishEvent(new UserRegisteredEvent("Alice"));

            assertThat(probe.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.threadName()).startsWith("events-async-");
            assertThat(probe.threadName()).isNotEqualTo(Thread.currentThread().getName());
        }
    }

    @Test
    void solution_asyncMulticasterDispatchesListenersOnExecutorThread() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncMulticasterConfig.class)) {
            ThreadProbe probe = context.getBean(ThreadProbe.class);

            context.publishEvent(new UserRegisteredEvent("Alice"));

            assertThat(probe.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.threadName()).startsWith("events-multicaster-");
        }
    }

    @Test
    void solution_conditionalListener_onlyFiresWhenConditionMatches() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConditionalListenerConfig.class)) {
            InMemoryAuditLog auditLog = context.getBean(InMemoryAuditLog.class);

            auditLog.clear();
            context.publishEvent(new UserRegisteredEvent("Alice"));
            context.publishEvent(new UserRegisteredEvent("Bob"));

            assertThat(auditLog.entries())
                    .containsExactly("conditional:Alice");
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
    static class MultipleListenersConfig {

        @Bean
        InMemoryAuditLog auditLog() {
            return new InMemoryAuditLog();
        }

        @Bean
        UserRegistrationService userRegistrationService(ApplicationEventPublisher publisher) {
            return new UserRegistrationService(publisher);
        }

        @Bean
        PrimaryAuditListener primaryAuditListener(InMemoryAuditLog auditLog) {
            return new PrimaryAuditListener(auditLog);
        }

        @Bean
        SecondaryAuditListener secondaryAuditListener(InMemoryAuditLog auditLog) {
            return new SecondaryAuditListener(auditLog);
        }
    }

    static class PrimaryAuditListener {
        private final InMemoryAuditLog auditLog;

        PrimaryAuditListener(InMemoryAuditLog auditLog) {
            this.auditLog = auditLog;
        }

        @EventListener
        public void on(UserRegisteredEvent event) {
            auditLog.add("userRegistered:" + event.username());
        }
    }

    static class SecondaryAuditListener {
        private final InMemoryAuditLog auditLog;

        SecondaryAuditListener(InMemoryAuditLog auditLog) {
            this.auditLog = auditLog;
        }

        @EventListener
        public void on(UserRegisteredEvent event) {
            auditLog.add("userRegisteredSecondary:" + event.username());
        }
    }

    @Configuration
    static class OrderingConfig {

        @Bean
        InMemoryAuditLog auditLog() {
            return new InMemoryAuditLog();
        }

        @Bean
        OrderedFirstListener orderedFirstListener(InMemoryAuditLog auditLog) {
            return new OrderedFirstListener(auditLog);
        }

        @Bean
        OrderedSecondListener orderedSecondListener(InMemoryAuditLog auditLog) {
            return new OrderedSecondListener(auditLog);
        }
    }

    static class OrderedFirstListener {
        private final InMemoryAuditLog auditLog;

        OrderedFirstListener(InMemoryAuditLog auditLog) {
            this.auditLog = auditLog;
        }

        @Order(1)
        @EventListener
        public void on(UserRegisteredEvent event) {
            auditLog.add("ordered:first");
        }
    }

    static class OrderedSecondListener {
        private final InMemoryAuditLog auditLog;

        OrderedSecondListener(InMemoryAuditLog auditLog) {
            this.auditLog = auditLog;
        }

        @Order(2)
        @EventListener
        public void on(UserRegisteredEvent event) {
            auditLog.add("ordered:second");
        }
    }

    @EnableAsync
    @Configuration
    static class AsyncListenerConfig {

        @Bean
        ThreadProbe threadProbe() {
            return new ThreadProbe(new CountDownLatch(1), new AtomicReference<>());
        }

        @Bean
        TaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("events-async-");
            executor.initialize();
            return executor;
        }

        @Bean
        AsyncAuditListener asyncAuditListener(ThreadProbe probe) {
            return new AsyncAuditListener(probe);
        }
    }

    static class AsyncAuditListener {
        private final ThreadProbe probe;

        AsyncAuditListener(ThreadProbe probe) {
            this.probe = probe;
        }

        @Async
        @EventListener
        public void on(UserRegisteredEvent event) {
            probe.threadNameRef().set(Thread.currentThread().getName());
            probe.latch().countDown();
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
            executor.setQueueCapacity(10);
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
        MulticasterThreadCaptureListener multicasterThreadCaptureListener(ThreadProbe probe) {
            return new MulticasterThreadCaptureListener(probe);
        }
    }

    static class MulticasterThreadCaptureListener {
        private final ThreadProbe probe;

        MulticasterThreadCaptureListener(ThreadProbe probe) {
            this.probe = probe;
        }

        @EventListener
        public void on(UserRegisteredEvent event) {
            probe.threadNameRef().set(Thread.currentThread().getName());
            probe.latch().countDown();
        }
    }

    @Configuration
    static class ConditionalListenerConfig {

        @Bean
        InMemoryAuditLog auditLog() {
            return new InMemoryAuditLog();
        }

        @Bean
        ConditionalAuditListener conditionalAuditListener(InMemoryAuditLog auditLog) {
            return new ConditionalAuditListener(auditLog);
        }
    }

    static class ConditionalAuditListener {
        private final InMemoryAuditLog auditLog;

        ConditionalAuditListener(InMemoryAuditLog auditLog) {
            this.auditLog = auditLog;
        }

        @EventListener(condition = "#event.username().startsWith('A')")
        public void on(UserRegisteredEvent event) {
            auditLog.add("conditional:" + event.username());
        }
    }
}
