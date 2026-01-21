package com.learning.springboot.springcoreaopweaving.support;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class InvocationLog {

    private static final InvocationLog INSTANCE = new InvocationLog();

    private final CopyOnWriteArrayList<JoinPointEvent> events = new CopyOnWriteArrayList<>();

    private InvocationLog() {
    }

    public static InvocationLog getInstance() {
        return INSTANCE;
    }

    public void reset() {
        events.clear();
    }

    public void record(JoinPointEvent event) {
        events.add(event);
    }

    public List<JoinPointEvent> events() {
        return List.copyOf(events);
    }

    public int count() {
        return events.size();
    }
}
