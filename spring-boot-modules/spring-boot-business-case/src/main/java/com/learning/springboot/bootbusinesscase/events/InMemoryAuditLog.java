package com.learning.springboot.bootbusinesscase.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

@Component
public class InMemoryAuditLog {

    private final CopyOnWriteArrayList<String> entries = new CopyOnWriteArrayList<>();

    public void add(String entry) {
        entries.add(entry);
    }

    public void clear() {
        entries.clear();
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
