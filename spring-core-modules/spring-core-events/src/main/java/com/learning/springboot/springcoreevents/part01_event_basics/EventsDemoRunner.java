package com.learning.springboot.springcoreevents.part01_event_basics;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class EventsDemoRunner implements ApplicationRunner {

    private final UserRegistrationService userRegistrationService;
    private final InMemoryAuditLog auditLog;

    public EventsDemoRunner(UserRegistrationService userRegistrationService, InMemoryAuditLog auditLog) {
        this.userRegistrationService = userRegistrationService;
        this.auditLog = auditLog;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("== spring-core-events ==");

        System.out.println("EVENTS:publisher.thread=" + Thread.currentThread().getName());

        auditLog.clear();
        userRegistrationService.register("Alice");
        System.out.println("EVENTS:auditLog.entries=" + auditLog.entries());

        auditLog.clear();
        try {
            userRegistrationService.register("Boom");
            System.out.println("EVENTS:exception.propagated=false");
        } catch (RuntimeException ex) {
            System.out.println("EVENTS:exception.propagated=true");
            System.out.println("EVENTS:exception.type=" + ex.getClass().getName());
            System.out.println("EVENTS:exception.message=" + ex.getMessage());
            System.out.println("EVENTS:auditLog.entriesAfterException=" + auditLog.entries());
        }
    }
}
