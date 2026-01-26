package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansSmartLifecycleLabTest {

    @Test
    void smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("a", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("A", 0, true, events));
            context.registerBean("b", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("B", 1, true, events));
            context.refresh();
        }

        System.out.println("OBSERVE: SmartLifecycle start order is phase ascending; stop order is reverse");
        assertThat(events).containsExactly(
                "start:A",
                "start:B",
                "stop:B",
                "stop:A"
        );
    }

    @Test
    void smartLifecycleDoesNotAutoStart_whenIsAutoStartupIsFalse() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("manual", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("MANUAL", 0, false, events));
            context.refresh();
        }

        System.out.println("OBSERVE: isAutoStartup=false => container will NOT automatically start the lifecycle on refresh");
        assertThat(events).isEmpty();
    }

    @Test
    void containerStopsSmartLifecycle_viaStopCallbackMethod_notStopMethod() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("callback", CallbackOnlySmartLifecycle.class, () -> new CallbackOnlySmartLifecycle(events));
            context.refresh();
        }

        System.out.println("OBSERVE: DefaultLifecycleProcessor calls SmartLifecycle.stop(Runnable) to support async stop");
        assertThat(events).containsExactly("start", "stop(callback)");
    }

    static class RecordingSmartLifecycle implements SmartLifecycle {

        private final String name;
        private final int phase;
        private final boolean autoStartup;
        private final List<String> events;
        private boolean running;

        RecordingSmartLifecycle(String name, int phase, boolean autoStartup, List<String> events) {
            this.name = name;
            this.phase = phase;
            this.autoStartup = autoStartup;
            this.events = events;
        }

        @Override
        public void start() {
            running = true;
            events.add("start:" + name);
        }

        @Override
        public void stop() {
            running = false;
            events.add("stop:" + name);
        }

        @Override
        public void stop(Runnable callback) {
            stop();
            callback.run();
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public int getPhase() {
            return phase;
        }

        @Override
        public boolean isAutoStartup() {
            return autoStartup;
        }
    }

    static class CallbackOnlySmartLifecycle implements SmartLifecycle {

        private final List<String> events;
        private boolean running;

        CallbackOnlySmartLifecycle(List<String> events) {
            this.events = events;
        }

        @Override
        public void start() {
            running = true;
            events.add("start");
        }

        @Override
        public void stop() {
            throw new AssertionError("Should not call stop() for SmartLifecycle; expected stop(Runnable)");
        }

        @Override
        public void stop(Runnable callback) {
            running = false;
            events.add("stop(callback)");
            callback.run();
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public int getPhase() {
            return 0;
        }

        @Override
        public boolean isAutoStartup() {
            return true;
        }
    }
}
