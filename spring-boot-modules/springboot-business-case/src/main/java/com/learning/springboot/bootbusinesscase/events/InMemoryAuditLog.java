package com.learning.springboot.bootbusinesscase.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class InMemoryAuditLog {

    private final List<String> entries = new ArrayList<>();

    public void add(String entry) {
        entries.add(entry);
    }

    public void clear() {
        entries.clear();
    }

    public List<String> entries() {
        return Collections.unmodifiableList(entries);
    }
}

