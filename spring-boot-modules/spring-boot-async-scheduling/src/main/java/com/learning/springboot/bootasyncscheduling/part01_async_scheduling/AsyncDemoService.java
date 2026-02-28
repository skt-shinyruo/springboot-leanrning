package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

@Service
class AsyncDemoService {

    @Async
    CompletableFuture<String> currentThreadName() {
        return CompletableFuture.completedFuture(Thread.currentThread().getName());
    }

    @Async
    ListenableFuture<String> currentThreadNameAsListenableFuture() {
        ListenableFutureTask<String> task = new ListenableFutureTask<>(() -> Thread.currentThread().getName());
        task.run();
        return task;
    }

    @Async
    void runAsync(Runnable task) {
        task.run();
    }

    @Async
    CompletableFuture<String> failsAsFuture() {
        throw new IllegalStateException("boom_future");
    }

    @Async
    void failsAsVoid() {
        throw new IllegalStateException("boom_void");
    }
}
