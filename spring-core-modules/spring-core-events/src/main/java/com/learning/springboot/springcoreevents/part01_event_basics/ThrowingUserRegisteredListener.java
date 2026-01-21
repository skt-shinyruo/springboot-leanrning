package com.learning.springboot.springcoreevents.part01_event_basics;

import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class ThrowingUserRegisteredListener {

    @Order(Ordered.LOWEST_PRECEDENCE)
    @EventListener
    public void on(UserRegisteredEvent event) {
        if (!"Boom".equals(event.username())) {
            return;
        }

        System.out.println("EVENTS:throwingListener.thread=" + Thread.currentThread().getName());
        System.out.println("EVENTS:throwingListener.throwing=true");
        throw new IllegalStateException("listener boom");
    }
}
