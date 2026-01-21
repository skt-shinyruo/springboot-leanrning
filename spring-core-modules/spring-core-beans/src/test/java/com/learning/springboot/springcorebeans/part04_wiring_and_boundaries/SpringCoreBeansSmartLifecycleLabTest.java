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
            context.registerBean("a", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("A", 0, events));
            context.registerBean("b", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("B", 1, events));
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

    static class RecordingSmartLifecycle implements SmartLifecycle {

        private final String name;
        private final int phase;
        private final List<String> events;
        private boolean running;

        RecordingSmartLifecycle(String name, int phase, List<String> events) {
            this.name = name;
            this.phase = phase;
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
            return true;
        }
    }
}
