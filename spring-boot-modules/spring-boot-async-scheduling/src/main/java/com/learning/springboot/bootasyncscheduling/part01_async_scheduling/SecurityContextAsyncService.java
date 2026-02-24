package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextAsyncService {

    @Async
    public CompletableFuture<AuthSnapshot> observeAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication == null ? null : authentication.getName();
        return CompletableFuture.completedFuture(new AuthSnapshot(Thread.currentThread().getName(), name));
    }

    public record AuthSnapshot(String threadName, String authenticationName) {}
}

