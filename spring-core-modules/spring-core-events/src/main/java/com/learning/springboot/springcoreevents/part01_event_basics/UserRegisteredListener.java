package com.learning.springboot.springcoreevents.part01_event_basics;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {

    private final InMemoryAuditLog auditLog;

    public UserRegisteredListener(InMemoryAuditLog auditLog) {
        this.auditLog = auditLog;
    }

    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        String entry = "userRegistered:" + event.username();
        auditLog.add(entry);
        System.out.println("EVENTS:listener.thread=" + Thread.currentThread().getName());
        System.out.println("EVENTS:listener.entry=" + entry);
    }
}
