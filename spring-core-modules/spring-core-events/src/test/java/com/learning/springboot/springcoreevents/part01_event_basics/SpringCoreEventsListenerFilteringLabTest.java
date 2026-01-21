package com.learning.springboot.springcoreevents.part01_event_basics;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

class SpringCoreEventsListenerFilteringLabTest {

    @Test
    void eventListener_shouldFilterByMethodParameterType() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ListenerFilteringConfig.class)) {
            ListenerProbe probe = context.getBean(ListenerProbe.class);

            context.publishEvent("hello");
            context.publishEvent(42);

            assertThat(probe.stringPayloads).containsExactly("hello");
            assertThat(probe.integerPayloads).containsExactly(42);
        }
    }

    @Configuration
    static class ListenerFilteringConfig {

        @Bean
        ListenerProbe probe() {
            return new ListenerProbe();
        }

        @Bean
        ListenerFilteringListeners listeners(ListenerProbe probe) {
            return new ListenerFilteringListeners(probe);
        }
    }

    static class ListenerProbe {
        private final List<String> stringPayloads = new ArrayList<>();
        private final List<Integer> integerPayloads = new ArrayList<>();
    }

    static class ListenerFilteringListeners {

        private final ListenerProbe probe;

        ListenerFilteringListeners(ListenerProbe probe) {
            this.probe = probe;
        }

        @EventListener
        public void onStringPayload(String payload) {
            probe.stringPayloads.add(payload);
        }

        @EventListener
        public void onIntegerPayload(Integer payload) {
            probe.integerPayloads.add(payload);
        }
    }
}

