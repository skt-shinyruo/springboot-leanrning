package com.learning.springboot.springcoreevents.part01_event_basics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

@SpringBootTest
@Import(SpringCoreEventsLabTest.ExtraListenersConfig.class)
class SpringCoreEventsLabTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private InMemoryAuditLog auditLog;

    @Autowired
    private OrderedListenerLog orderedListenerLog;

    @Autowired
    private ConditionalListenerLog conditionalListenerLog;

    @Autowired
    private PayloadListenerLog payloadListenerLog;

    @Autowired
    private ThreadCaptureLog threadCaptureLog;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    void listenerReceivesPublishedEvent() {
        auditLog.clear();

        userRegistrationService.register("Bob");

        assertThat(auditLog.entries()).contains("userRegistered:Bob");
    }

    @Test
    void multipleListenersCanObserveTheSameEvent() {
        auditLog.clear();

        userRegistrationService.register("Alice");

        assertThat(auditLog.entries()).contains("userRegistered:Alice");
        assertThat(auditLog.entries()).contains("extraListener:Alice");
    }

    @Test
    void orderedListenersFollowOrderAnnotation() {
        orderedListenerLog.clear();

        userRegistrationService.register("Order");

        assertThat(orderedListenerLog.entries()).containsExactly("ordered:first", "ordered:second");
    }

    @Test
    void conditionalEventListenerOnlyRunsWhenConditionMatches() {
        conditionalListenerLog.clear();

        userRegistrationService.register("Bob");
        userRegistrationService.register("Alice");

        assertThat(conditionalListenerLog.entries()).containsExactly("conditional:Alice");
    }

    @Test
    void publishingPlainObjectsAlsoWorks_asPayloadEvents() {
        payloadListenerLog.reset();

        eventPublisher.publishEvent("hello");

        assertThat(payloadListenerLog.lastPayload()).isEqualTo("hello");
    }

    @Test
    void eventsAreSynchronousByDefault() {
        threadCaptureLog.reset();

        userRegistrationService.register("Sync");

        assertThat(threadCaptureLog.lastThreadName()).isEqualTo(Thread.currentThread().getName());
    }

    @TestConfiguration
    static class ExtraListenersConfig {

        @Bean
        ExtraUserRegisteredListener extraUserRegisteredListener(InMemoryAuditLog auditLog) {
            return new ExtraUserRegisteredListener(auditLog);
        }

        @Bean
        OrderedListenerLog orderedListenerLog() {
            return new OrderedListenerLog();
        }

        @Bean
        OrderedFirstListener orderedFirstListener(OrderedListenerLog log) {
            return new OrderedFirstListener(log);
        }

        @Bean
        OrderedSecondListener orderedSecondListener(OrderedListenerLog log) {
            return new OrderedSecondListener(log);
        }

        @Bean
        ConditionalListenerLog conditionalListenerLog() {
            return new ConditionalListenerLog();
        }

        @Bean
        ConditionalUserRegisteredListener conditionalUserRegisteredListener(ConditionalListenerLog log) {
            return new ConditionalUserRegisteredListener(log);
        }

        @Bean
        PayloadListenerLog payloadListenerLog() {
            return new PayloadListenerLog();
        }

        @Bean
        StringPayloadListener stringPayloadListener(PayloadListenerLog log) {
            return new StringPayloadListener(log);
        }

        @Bean
        ThreadCaptureLog threadCaptureLog() {
            return new ThreadCaptureLog();
        }

        @Bean
        ThreadCaptureListener threadCaptureListener(ThreadCaptureLog log) {
            return new ThreadCaptureListener(log);
        }
    }

    static class ExtraUserRegisteredListener {
        private final InMemoryAuditLog auditLog;

        ExtraUserRegisteredListener(InMemoryAuditLog auditLog) {
            this.auditLog = auditLog;
        }

        @EventListener
        public void on(UserRegisteredEvent event) {
            auditLog.add("extraListener:" + event.username());
        }
    }

    static class OrderedListenerLog {
        private final java.util.List<String> entries = new java.util.concurrent.CopyOnWriteArrayList<>();

        void add(String entry) {
            entries.add(entry);
        }

        java.util.List<String> entries() {
            return java.util.List.copyOf(entries);
        }

        void clear() {
            entries.clear();
        }
    }

    static class OrderedFirstListener {
        private final OrderedListenerLog log;

        OrderedFirstListener(OrderedListenerLog log) {
            this.log = log;
        }

        @Order(1)
        @EventListener
        public void on(UserRegisteredEvent event) {
            log.add("ordered:first");
        }
    }

    static class OrderedSecondListener {
        private final OrderedListenerLog log;

        OrderedSecondListener(OrderedListenerLog log) {
            this.log = log;
        }

        @Order(2)
        @EventListener
        public void on(UserRegisteredEvent event) {
            log.add("ordered:second");
        }
    }

    static class ConditionalListenerLog {
        private final java.util.List<String> entries = new java.util.concurrent.CopyOnWriteArrayList<>();

        void add(String entry) {
            entries.add(entry);
        }

        java.util.List<String> entries() {
            return java.util.List.copyOf(entries);
        }

        void clear() {
            entries.clear();
        }
    }

    static class ConditionalUserRegisteredListener {
        private final ConditionalListenerLog log;

        ConditionalUserRegisteredListener(ConditionalListenerLog log) {
            this.log = log;
        }

        @EventListener(condition = "#event.username().startsWith('A')")
        public void on(UserRegisteredEvent event) {
            log.add("conditional:" + event.username());
        }
    }

    static class PayloadListenerLog {
        private final java.util.concurrent.atomic.AtomicReference<String> lastPayload = new java.util.concurrent.atomic.AtomicReference<>();

        void reset() {
            lastPayload.set(null);
        }

        void setLastPayload(String payload) {
            lastPayload.set(payload);
        }

        String lastPayload() {
            return lastPayload.get();
        }
    }

    static class StringPayloadListener {
        private final PayloadListenerLog log;

        StringPayloadListener(PayloadListenerLog log) {
            this.log = log;
        }

        @EventListener
        public void on(String payload) {
            log.setLastPayload(payload);
        }
    }

    static class ThreadCaptureLog {
        private final java.util.concurrent.atomic.AtomicReference<String> lastThreadName = new java.util.concurrent.atomic.AtomicReference<>();

        void reset() {
            lastThreadName.set(null);
        }

        void setLastThreadName(String name) {
            lastThreadName.set(name);
        }

        String lastThreadName() {
            return lastThreadName.get();
        }
    }

    static class ThreadCaptureListener {
        private final ThreadCaptureLog log;

        ThreadCaptureListener(ThreadCaptureLog log) {
            this.log = log;
        }

        @EventListener
        public void on(UserRegisteredEvent event) {
            log.setLastThreadName(Thread.currentThread().getName());
        }
    }
}

