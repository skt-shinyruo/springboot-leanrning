package com.learning.springboot.springcoreevents.part01_event_basics;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final ApplicationEventPublisher eventPublisher;

    public UserRegistrationService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void register(String username) {
        eventPublisher.publishEvent(new UserRegisteredEvent(username));
    }
}

